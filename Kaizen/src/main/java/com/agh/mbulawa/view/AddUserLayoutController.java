package com.agh.mbulawa.view;

import com.agh.mbulawa.Main;
import com.agh.mbulawa.dao.UserDaoImpl;
import com.agh.mbulawa.model.User;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddUserLayoutController {

	private Stage dialogStage;

	private Main main;

	@FXML
	private TextField firstNameField;
	@FXML
	private TextField lastNameField;
	@FXML
	private TextField loginField;
	@FXML
	private TextField facultyField;
	@FXML
	private TextField passField;

	private User user;
	private boolean isEdit;
	private boolean isAdmin;

	public void setMain(Main main) {
		this.main = main;

		this.firstNameField.setText(user.getFirstName());
		this.lastNameField.setText(user.getLastName());
		this.loginField.setText(user.getLogin());
		this.facultyField.setText(user.getFaculty());
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setEdit(boolean isEdit) {
		this.isEdit = isEdit;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public void initialize() {

	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	@FXML
	private void handleOk() {
		if (!(firstNameField.getText().isEmpty() && lastNameField.getText().isEmpty() && loginField.getText().isEmpty()
				&& passField.getText().isEmpty())) {

			User user = new User(firstNameField.getText(), lastNameField.getText(), loginField.getText(),
					facultyField.getText(), passField.getText());
			UserDaoImpl userDaoImpl = new UserDaoImpl();
//FIXME: wycięte
			if (isAdmin) {
				user.setIsAdmin(1);
			}

			if (isEdit) {
				user.setId(main.getUserId());
				userDaoImpl.updateUser(user);
				dialogStage.close();

			} else if (userDaoImpl.getUserIdByLogin(loginField.getText()) != 0) {
				Alert alert = new Alert(AlertType.WARNING);
				alert.initOwner(dialogStage);
				alert.setTitle("Błędny Login");
				alert.setHeaderText("Ten Login jest już zajęty!!");
				alert.setContentText("Prosze podać inny login");

				alert.showAndWait();
			} else {
				userDaoImpl.addUser(user);
				dialogStage.close();
			}
			userDaoImpl.closeConnection();

		} else {
			Alert alert = new Alert(AlertType.WARNING);
			alert.initOwner(dialogStage);
			alert.setTitle("PUSTE POLA!");
			alert.setHeaderText("Niektóre pola są puste!");
			alert.setContentText("Prosze uzupełnić wszystkie pola aby kontynuować!");

			alert.showAndWait();
		}
	}

	@FXML
	private void handleEnterPressed(KeyEvent event) {

		if (event.getCode().equals(KeyCode.ENTER)) {
			handleOk();
		} else if (event.getCode().equals(KeyCode.ESCAPE)) {
			dialogStage.close();
		}
	}

	@FXML
	private void handleCancel() {
		dialogStage.close();
	}

}
