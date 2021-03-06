package com.agh.mbulawa.view;

import com.agh.mbulawa.Main;
import com.agh.mbulawa.dao.IdeaDaoImpl;
import com.agh.mbulawa.model.Idea;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class NewAddIdeaController {

	@FXML
	private TextField nameField;
	@FXML
	private ComboBox<String> categoryCombokBox;
	@FXML
	private TextArea contentArea;

	@FXML
	private BorderPane firstBorder;
	@FXML
	private BorderPane secondBorder;
	@FXML
	private BorderPane thirdBorder;

	private Stage dialogStage;
	private Main main;
	private Idea idea;

	private boolean isOkCliked;
	private boolean isEdit;

	private ObservableList<String> categories = FXCollections.observableArrayList();

	public NewAddIdeaController() {

	}

	public NewAddIdeaController(Idea idea, boolean isEdit) {
		this.idea = idea;
		this.isEdit = isEdit;
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public void setMain(Main main) {
		this.main = main;

		if (this.idea.getCategory() != null)
			categoryCombokBox.setValue((this.idea.getCategory()));
		nameField.setText(this.idea.getName());
		contentArea.setText(this.idea.getContent());
	}

	public boolean isOkCliked() {
		return isOkCliked;
	}

	public void setOkCliked(boolean isOkCliked) {
		this.isOkCliked = isOkCliked;
	}

	public boolean isEdit() {
		return isEdit;
	}

	public void setEdit(boolean isEdit) {
		this.isEdit = isEdit;
	}

	public Idea getIdea() {
		return idea;
	}

	public void setIdea(Idea idea) {
		this.idea = idea;
	}

	public void initialize() {
		categories.addAll("Porządki", "Organizacja", "Instrukcje", "Dokumentacja", "Stanowisko");
		categoryCombokBox.getItems().addAll(categories);
		contentArea.setWrapText(true);

		secondBorder.setVisible(false);
		thirdBorder.setVisible(false);
	}

	@FXML
	private void handleFirstNext() {

		if (categoryCombokBox.getSelectionModel().getSelectedItem() != null) {
			firstBorder.setVisible(false);
			secondBorder.setVisible(true);
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(main.getPrimaryStage());
			alert.setTitle("EMPTY FIELDS!");
			alert.setHeaderText("No text entered or category not selected!");
			alert.setContentText("Please check all fields and enter the text in it!");

			alert.showAndWait();
		}

	}

	@FXML
	private void handleSecondNext() {

		if (nameField.getText() != null && !nameField.getText().isEmpty()) {
			secondBorder.setVisible(false);
			thirdBorder.setVisible(true);
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(main.getPrimaryStage());
			alert.setTitle("EMPTY FIELDS!");
			alert.setHeaderText("No text entered or category not selected!");
			alert.setContentText("Please check all fields and enter the text in it!");

			alert.showAndWait();
		}

	}

	@FXML
	private void handleOK() {
		IdeaDaoImpl ideaDaoImpl = new IdeaDaoImpl();
		ideaDaoImpl.createConnection();
		ideaDaoImpl.createTable();

		if (contentArea != null && !contentArea.getText().isEmpty()) {

			// System.out.println(categoryCombokBox.getSelectionModel().getSelectedItem());

			String category = categoryCombokBox.getSelectionModel().getSelectedItem();
			String name = nameField.getText();
			String content = contentArea.getText();

			this.idea.setCategory(category);
			this.idea.setName(name);
			this.idea.setContent(content);

			if (isEdit) {
				// System.out.println(name);
				// idea.setId(ideaDaoImpl.getIdeaIdByName(name));
				idea.setUserId(main.getUserId());
				ideaDaoImpl.updateIdea(idea);

			} else {
				ideaDaoImpl.addIdea(idea, main.getUserId());
			}
			setOkCliked(true);

			dialogStage.close();
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(main.getPrimaryStage());
			alert.setTitle("EMPTY FIELDS!");
			alert.setHeaderText("No text entered or category not selected!");
			alert.setContentText("Please check all fields and enter the text in it!");

			setOkCliked(false);

			alert.showAndWait();
		}
	}

	@FXML
	private void handleExit() {
		dialogStage.close();
	}
}
