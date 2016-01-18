package com.agh.mbulawa.view;

import com.agh.mbulawa.dao.IdeaDaoImpl;
import com.agh.mbulawa.model.Idea;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class IdeasLayoutController {

	@FXML
	private TableView<Idea> ideasTable;
	@FXML
	private TableColumn<Idea, String> ideaCategoryColumn;
	@FXML
	private TableColumn<Idea, String> ideaNameColumn;
	@FXML
	private TextArea ideaArea;

	private int userId = 0;

	private Stage dialogStage;

	private ObservableList<Idea> ideasList = FXCollections.observableArrayList();

	public IdeasLayoutController() {

	}

	@FXML
	private void initialize() {
		ideaCategoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
		ideaNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

		ideasTable.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> showIdea(newValue));

		ideaCategoryColumn.prefWidthProperty().bind(ideasTable.widthProperty().divide(2));

		ideaArea.setEditable(false);
		ideaArea.setWrapText(true);

		new Thread(new Runnable() {

			@Override
			public void run() {

				IdeaDaoImpl ideaDaoImpl = new IdeaDaoImpl();
				ideaDaoImpl.createConnection();
				ideasList.addAll(ideaDaoImpl.getUserIdeasList(userId));
				ideaDaoImpl.closeConnection();

				Platform.runLater(new Runnable() {

					@Override
					public void run() {

						ideasTable.setItems(ideasList);

					}
				});

			}
		}).start();
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void showIdea(Idea idea) {
		if (idea != null)
			ideaArea.setText(idea.getContent());

	}

	@FXML
	private void handleExit() {
		dialogStage.close();
	}
}
