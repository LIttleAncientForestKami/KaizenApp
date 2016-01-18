package com.agh.mbulawa.view;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.agh.mbulawa.Main;
import com.agh.mbulawa.dao.IdeaDaoImpl;
import com.agh.mbulawa.dao.UserDaoImpl;
import com.agh.mbulawa.model.Idea;
import com.agh.mbulawa.model.User;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
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

	private Series<String, Integer> series = new XYChart.Series<>();
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

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				UserDaoImpl userDaoImpl = new UserDaoImpl();
				userDaoImpl.createConnection();
				List<User> usersList = userDaoImpl.getUsersList();

				IdeaDaoImpl ideaDaoImpl = new IdeaDaoImpl();
				ideaDaoImpl.createConnection();
				List<Integer> numberOfUsersIdeas = new ArrayList<Integer>();
				for (User u : usersList) {
					if (u.getIsAdmin() == 1) {
						continue;
					}
					usersNames.add(u.getLogin());
					List<Idea> userIdeasList = ideaDaoImpl.getUserIdeasList(u.getId());
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

				// Add data to series
				for (int i = 0; i < usersNames.size(); i++) {
					series.getData()
							.add(new XYChart.Data<String, Integer>(usersNames.get(i), numberOfUsersIdeas.get(i)));
				}
				userDaoImpl.closeConnection();
				ideaDaoImpl.closeConnection();

				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						ideaBar.getData().add(series);
					}
				});
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

	@FXML
	private void handleOnMauseCliked(MouseEvent event) {
		if (MouseButton.SECONDARY.equals(event.getButton())) {
			menu.show(dialogStage, event.getScreenX(), event.getScreenY());
		}
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
