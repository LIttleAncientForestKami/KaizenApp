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

public class AllIdeasLayoutController {

	@FXML
	private TableView<Idea> ideasTable;
	@FXML
	private TableColumn<Idea, String> ideaCategoryColumn;
	@FXML
	private TableColumn<Idea, String> ideaNameColumn;
	@FXML
	private TableColumn<Idea, String> ideaStatusColumn;
	@FXML
	private TextArea ideaArea;

	private Stage dialogStage;

	private ObservableList<Idea> ideasList = FXCollections.observableArrayList();

	public AllIdeasLayoutController() {

	}

	@FXML
	private void initialize() {
		ideaCategoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
		ideaNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		ideaStatusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

		ideasTable.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> showIdea(newValue));

		int divider = 3; // amount of column to divide equal
		
		ideaCategoryColumn.prefWidthProperty().bind(ideasTable.widthProperty().divide(divider));

		ideaArea.setEditable(false);
		ideaArea.setWrapText(true);

		new Thread(new Runnable() {

			@Override
			public void run() {

				IdeaDaoImpl ideaDaoImpl = new IdeaDaoImpl();
				ideaDaoImpl.createConnection();
				ideasList.addAll(ideaDaoImpl.getAllIdeasList());
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

	public void showIdea(Idea idea) {
		if (idea != null)
			ideaArea.setText(idea.getContent());

	}

	@FXML
	private void handleExit() {
		dialogStage.close();
	}
}
