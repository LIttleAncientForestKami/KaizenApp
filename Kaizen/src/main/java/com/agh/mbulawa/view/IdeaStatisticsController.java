package com.agh.mbulawa.view;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.agh.mbulawa.Main;
import com.agh.mbulawa.dao.IdeaDaoImpl;
import com.agh.mbulawa.dao.UserDaoImpl;
import com.agh.mbulawa.model.Idea;
import com.agh.mbulawa.model.User;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuItem;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class IdeaStatisticsController {

	private Stage dialogStage;
	private Main main;

	@FXML
	private BarChart<String, Integer> ideaBar;
	@FXML
	private CategoryAxis xAxis;
	@FXML
	private NumberAxis yAxis;
	@FXML
	private ContextMenu menu;
	@FXML
	private MenuItem item;
	@FXML
	private DatePicker fromDatePicker;
	@FXML
	private DatePicker toDatePicker;

	private List<Integer> numberOfUsersIdeas = new ArrayList<>();
	private Series<String, Integer> series;
	private ObservableList<String> usersNames = FXCollections.observableArrayList();

	private Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();

	private final double SCREEN_HEIGHT = visualBounds.getHeight();
	private final double SCREEN_WIDTH = visualBounds.getWidth();

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public void setMain(Main main) {
		this.main = main;
	}

	@FXML
	private void initialize() {

		ideaBar.setMaxSize(5000.0, 5000.0);
		ideaBar.setPrefSize(SCREEN_WIDTH, SCREEN_HEIGHT);

		LocalDate fromValue = fromDatePicker.getValue();
		LocalDate toValue = toDatePicker.getValue();

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {

				initializeBarSeries(fromValue, toValue);
			}

		});

		thread.start();

		// To be sure that chart data is ready
		// from thread. We can add xAxis labels
		// after that.
		while (thread.isAlive()) {
		}
		;

		int count = usersNames.size() / 100;

		// System.out.println(count);

		if (count != 0) {
			ideaBar.setMaxWidth(1500 * count);

			ideaBar.setPrefSize(SCREEN_WIDTH + 1500.0 * count, SCREEN_HEIGHT);
		}
		/*
		 * xAxis.setAutoRanging(true); xAxis.invalidateRange(usersNames);
		 */
		xAxis.setCategories(usersNames);

	}

	private void initializeBarSeries(LocalDate fromValue, LocalDate toValue) {

		// Clear all list
		usersNames.clear();
		List<Idea> userIdeasList = new ArrayList<>();
		numberOfUsersIdeas.clear();

		UserDaoImpl userDaoImpl = new UserDaoImpl();
		userDaoImpl.createConnection();
		List<User> usersList = userDaoImpl.getUsersList();

		IdeaDaoImpl ideaDaoImpl = new IdeaDaoImpl();
		ideaDaoImpl.createConnection();

		for (User u : usersList) {
			if (u.getIsAdmin() > 0) {
				continue;
			}
			usersNames.add(u.getLogin());
			userIdeasList = ideaDaoImpl.getUserIdeasList(u.getId());
			List<Idea> ideaListTemp = new ArrayList<>();
			ideaListTemp.addAll(userIdeasList);

			System.out.println(ideaListTemp.size());
			for (int i = 0; i < ideaListTemp.size(); i++) {
				Idea idea = ideaListTemp.get(i);
				LocalDate date = LocalDate.parse(idea.getAddDate());

				if (!(fromValue == null || toValue == null)) {
					boolean isAfter = date.isAfter(fromValue);
					boolean isBefore = date.isBefore(toValue);
					boolean isEqual = date.isEqual(fromValue) || date.isEqual(toValue);
					boolean dateFits = (isAfter || isEqual) && (isBefore || isEqual);

					if (!dateFits) {
						userIdeasList.remove(idea);
					}
				}
			}
			System.out.println(userIdeasList.size());
			numberOfUsersIdeas.add(userIdeasList.size());
		}

		// Bubble sort of series.
		// Sort by number of user Ideas.
		// Sort names in the same time to keep in sync of series.
		for (int i = 0; i < usersNames.size(); i++) {
			for (int j = 0; j < usersNames.size() - i - 1; j++) {

				if (numberOfUsersIdeas.get(j) < numberOfUsersIdeas.get(j + 1)) {

					String tempName = usersNames.get(j);
					String tempNameNext = usersNames.get(j + 1);

					// clear to avoid duplicate in ObservableArrayList.
					usersNames.set(j + 1, "");

					// replace objects
					usersNames.set(j, tempNameNext);
					usersNames.set(j + 1, tempName);

					// no need to avoid duplicate with integer in yAxis.
					Integer numberOfIdeaTemp = numberOfUsersIdeas.get(j);
					numberOfUsersIdeas.set(j, numberOfUsersIdeas.get(j + 1));
					numberOfUsersIdeas.set(j + 1, numberOfIdeaTemp);

				}
			}
		}

		userDaoImpl.closeConnection();
		ideaDaoImpl.closeConnection();

		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				series = new XYChart.Series<>();
				// Add data to series
				for (int i = 0; i < usersNames.size(); i++) {
					series.getData()
							.add(new XYChart.Data<String, Integer>(usersNames.get(i), numberOfUsersIdeas.get(i)));
				}

				ideaBar.getData().clear();
				ideaBar.layout();
				ideaBar.getData().add(series);
				for (Node n : ideaBar.lookupAll(".default-color0.chart-bar")) {
					n.setStyle("-fx-bar-fill: rgb(142,128,230);");
				}
				ideaBar.setAnimated(false);
			}
		});
	}

	@FXML
	private void handleOnMauseCliked(MouseEvent event) {
		if (MouseButton.SECONDARY.equals(event.getButton())) {
			menu.show(dialogStage, event.getScreenX(), event.getScreenY());
		}
	}

	@FXML
	private void handleShowByDate() {

		initializeBarSeries(fromDatePicker.getValue(), toDatePicker.getValue());

	}

	// Save Chart as PNG image.
	@FXML
	private void handleSaveChartImage() {

		WritableImage image = ideaBar.snapshot(new SnapshotParameters(), null);

		FileChooser fileChooser = new FileChooser();

		FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");

		fileChooser.getExtensionFilters().add(extFilterPNG);

		File file = fileChooser.showSaveDialog(main.getPrimaryStage());

		try {
			if (file != null) {
				if (!file.getPath().endsWith(".png")) {
					file = new File(file.getPath() + ".png");
				}

				ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
