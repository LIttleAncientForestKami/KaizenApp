package com.agh.mbulawa.view;

import com.agh.mbulawa.Main;
import com.agh.mbulawa.dao.UserDaoImpl;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginLayoutController {

	private Main main;

	@FXML
	private TextField loginField;
	@FXML
	private PasswordField passField;

	private String userPass;
	private boolean isValid;
	private int isAdmin;

	public LoginLayoutController() {

	}

	public void setMain(Main main) {
		this.main = main;
	}

	public String getUserPass() {
		return userPass;
	}

	public void setUserPass(String userPass) {
		this.userPass = userPass;
	}

	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	public int getIsAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(int isAdmin) {
		this.isAdmin = isAdmin;
	}

	@FXML
	private void handleLogin() {

		if (validation(loginField.getText(), passField.getText())) {

			if (isAdmin == 0) {
				main.showMainUserLayout();
			} else if (isAdmin == 1) {
				main.showAdminLayout();
			}
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(main.getPrimaryStage());
			alert.setTitle("Login Fail");
			alert.setHeaderText("Wrong Login and/or password!");
			alert.setContentText("Please check your pass and login!");

			alert.showAndWait();
		}
	}

	// Methods used in previous version of program.

	/*
	 * @FXML private void handleAdd() { User user = new User("", "", "", "");
	 * main.showAddUserDialog(user, false, false); }
	 */

	/*
	 * @FXML private void handleShowAll() { main.showAllUsersList(); }
	 */

	@FXML
	private void handleExit() {
		System.exit(0);
	}

	// Checking a login and password

	public boolean validation(String login, String password) {

		UserDaoImpl userDaoImpl = new UserDaoImpl();
		userDaoImpl.createConnection();
		String userPassword = userDaoImpl.getUserPassByLogin(login);
		int userId = userDaoImpl.getUserIdByLogin(login);
		if (userId != 0)
			this.setIsAdmin(userDaoImpl.isUserAdmin(userId));
		main.setUserId(userId);
		userDaoImpl.closeConnection();

		if (password.equals(userPassword))
			return true;
		return false;
	}
}
