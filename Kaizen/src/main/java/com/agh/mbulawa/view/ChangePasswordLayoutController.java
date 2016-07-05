package com.agh.mbulawa.view;

import com.agh.mbulawa.Main;
import com.agh.mbulawa.dao.UserDaoImpl;
import com.agh.mbulawa.model.User;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class ChangePasswordLayoutController {

	@FXML
	private Button okButton;
	@FXML
	private Button cancelButton;

	@FXML
	private TextField newPassword;

	private Main main;
	private Stage dialogStage;
	private String login;

	public void setMain(Main main) {
		this.main = main;
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	@FXML
	private void initialize() {

	}

	@FXML
	private void handleOK() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				UserDaoImpl userDAO = new UserDaoImpl();
				userDAO.createConnection();
				int userIdByLogin = userDAO.getUserIdByLogin(login);
				User userById = userDAO.getUserById(userIdByLogin);
				userById.setPassword(newPassword.getText());
				userDAO.updateUser(userById);
				userDAO.closeConnection();
			}
		}).start();
		dialogStage.close();
	}

	@FXML
	private void handleKeyPressed(KeyEvent event) {

		if (event.getCode().equals(KeyCode.ENTER)) {
			handleOK();
		} else if (event.getCode().equals(KeyCode.ESCAPE)) {
			handleCancel();
		}
	}

	@FXML
	private void handleCancel() {
		dialogStage.close();
	}

}
