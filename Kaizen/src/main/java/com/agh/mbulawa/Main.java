package com.agh.mbulawa;

import java.io.IOException;
import java.util.List;

import com.agh.mbulawa.dao.IdeaDaoImpl;
import com.agh.mbulawa.dao.UserDaoImpl;
import com.agh.mbulawa.model.Idea;
import com.agh.mbulawa.model.User;
import com.agh.mbulawa.view.AddIdeaLayoutController;
import com.agh.mbulawa.view.AddUserLayoutController;
import com.agh.mbulawa.view.AdminLayoutController;
import com.agh.mbulawa.view.AllUsersLayoutController;
import com.agh.mbulawa.view.IdeaStatisticsController;
import com.agh.mbulawa.view.IdeasLayoutController;
import com.agh.mbulawa.view.LoginLayoutController;
import com.agh.mbulawa.view.MainUserLayoutController;
import com.agh.mbulawa.view.RootLayoutController;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Main extends Application {

	private int userId;

	private Stage primaryStage;
	private BorderPane rootLayout;
	private AnchorPane mainUserLayout;

	ObservableList<Idea> ideasTable = FXCollections.observableArrayList();

	// Getters and Setters
	public int getUserId() {
		return userId;
	}

	public ObservableList<Idea> getIdeasTable() {
		return ideasTable;
	}

	public void setIdeasTable(ObservableList<Idea> ideasTable) {
		this.ideasTable = ideasTable;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	// Generate for reference for alerts in other scenes
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	// Generated to change root context from other scenes
	public BorderPane getRootLayout() {
		return this.rootLayout;
	}

	public AnchorPane getMainUserLayout() {
		return mainUserLayout;
	}

	public void setMainUserLayout(AnchorPane mainUserLayout) {
		this.mainUserLayout = mainUserLayout;
	}

	@Override
	public void start(Stage primaryStage) {

		this.primaryStage = primaryStage;

		primaryStage.setTitle("Kaizen");

		initRootLayout();
		showLoginLayut();

		@SuppressWarnings("unused")
		Thread thread = new Thread() {

			@Override
			public void run() {
				IdeaDaoImpl ideaDaoImpl = new IdeaDaoImpl();
				ideaDaoImpl.createConnection();
				ideaDaoImpl.createTable();
				ideaDaoImpl.closeConnection();

				UserDaoImpl userDaoImpl = new UserDaoImpl();
				userDaoImpl.createConnection();
				userDaoImpl.createTable();

				User user = new User("Admin", "Admin", "Admin", "Admin");

				user.setIsAdmin(1);

				userDaoImpl.addUser(user);

				/*
				 * User user = new User("NoAdmin", "NoAdmin", "NoAdmin", "123");
				 * userDaoImpl.addUser(user);
				 */
				/*
				 * for (int i = 0; i < 10; i++) { User user2 = new User(("Imię"
				 * + i), ("Nazwisko" + i), ("Login" + i), "Hasło");
				 * System.out.println(user2.getIsAdmin());;
				 * userDaoImpl.addUser(user2); // userDaoImpl.addUser(new
				 * User("Kajetan", "Koszyk", // "Kokosz", "123")); }
				 */
				/*
				 * List<User> usersList = userDaoImpl.getUsersList(); for (User
				 * u : usersList) { System.out.println(u.toString()); }
				 */
				userDaoImpl.closeConnection();
			}
		};

		@SuppressWarnings("unused")
		Thread thread2 = new Thread() {

			@Override
			public void run() {

				IdeaDaoImpl ideaDaoImpl = new IdeaDaoImpl();
				ideaDaoImpl.createConnection();
				ideaDaoImpl.createTable();

				ideaDaoImpl.addIdea(new Idea("Porządki", "Porządek na biurku",
						"Posegregowanie wszystkich przedmiotów znajdujących się w przestrzeni biurka"), 2);
				ideaDaoImpl.addIdea(new Idea("Porządki2", "Porządek na ziemi",
						"Posegregowanie wszystkich przedmiotów znajdujących się na ziemi"), 1);
				ideaDaoImpl.addIdea(new Idea("Stanowisko", "Nowe stanowisko",
						"Zorganizowanie nowego stanowiska segregacji odpadów"), 1);

				List<Idea> ideasList = ideaDaoImpl.getAllIdeasList();
				for (Idea i : ideasList) {
					System.out.println(i.toString());
				}

				ideaDaoImpl.closeConnection();
			}
		};

		// thread.start();
		// thread2.start();

	}

	// Created methods for handle the ideas table in MainUserLayout.
	// Other handle methods are in specific layout controllers.
	public void initializeTable() {
		IdeaDaoImpl ideaDaoImpl = new IdeaDaoImpl();
		ideaDaoImpl.createConnection();
		ideaDaoImpl.createTable();
		List<Idea> ideasList = ideaDaoImpl.getUserIdeasList(userId);
		ideaDaoImpl.closeConnection();
		ideasTable.addAll(ideasList);
	}

	public void addToTable(Idea idea) {
		ideasTable.add(idea);
	}

	public void refreshTable() {
		ideasTable.clear();
		initializeTable();
	}

	// Methods responsible for initialize and showing the layouts.
	public void initRootLayout() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();

			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);

			RootLayoutController controller = loader.getController();
			controller.setMain(this);

			primaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showLoginLayut() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/LoginLayout.fxml"));
			AnchorPane loginLayout = (AnchorPane) loader.load();

			rootLayout.setCenter(loginLayout);
			LoginLayoutController controller = loader.getController();
			controller.setMain(this);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showMainUserLayout() {
		try {

			initializeTable();

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/MainUserLayout.fxml"));
			AnchorPane mainUserLayout = (AnchorPane) loader.load();
			rootLayout.setCenter(mainUserLayout);

			MainUserLayoutController controller = loader.getController();
			controller.setMain(this);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showAdminLayout() {

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/AdminLayout.fxml"));
			AnchorPane adminLayout = (AnchorPane) loader.load();
			rootLayout.setCenter(adminLayout);

			AdminLayoutController controller = loader.getController();
			controller.setMain(this);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showIdeasLayout(int userId) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/IdeasLayout.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Pomysły Pracowników");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);

			IdeasLayoutController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setUserId(userId);

			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showAddUserDialog(User user, boolean isEdit, boolean isAdmin) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/AddUserLayout.fxml"));
			AnchorPane page = (AnchorPane) loader.load();
			this.setMainUserLayout(mainUserLayout);

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Edit Person");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);

			AddUserLayoutController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setEdit(isEdit);
			controller.setUser(user);
			controller.setAdmin(isAdmin);
			controller.setMain(this);

			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean showAddIdeaDialog(Idea idea, boolean isEdit) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/AddIdeaLayout.fxml"));
			AnchorPane page = (AnchorPane) loader.load();
			this.setMainUserLayout(mainUserLayout);

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Add Idea");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);

			AddIdeaLayoutController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setIdea(idea);
			controller.setMain(this);
			controller.setEdit(isEdit);

			dialogStage.showAndWait();

			return controller.isOkCliked();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void showIdeaStatistics() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/IdeaStatistics.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Idea Statistics");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			// dialogStage.setResizable(false);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);

			IdeaStatisticsController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setMain(this);

			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void showAllUsersList() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/AllUsersLayout.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Wszyscy użytkownicy");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);

			Scene scene = new Scene(page);
			dialogStage.setScene(scene);

			AllUsersLayoutController controller = loader.getController();
			controller.setMain(this);
			controller.setDialogStage(dialogStage);

			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// The main method.
	public static void main(String[] args) {
		launch(args);

	}
}
