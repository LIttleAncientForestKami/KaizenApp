package com.agh.mbulawa.view;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.agh.mbulawa.Main;
import com.agh.mbulawa.dao.IdeaDaoImpl;
import com.agh.mbulawa.dao.UserDaoImpl;
import com.agh.mbulawa.model.Idea;
import com.agh.mbulawa.model.User;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AdminLayoutController {

	// Users table
	@FXML
	private TableView<User> usersTable;
	@FXML
	private TableColumn<User, Integer> userRowNumberColumn;
	@FXML
	private TableColumn<User, String> firstNameColumn;
	@FXML
	private TableColumn<User, String> lastNameColumn;
	@FXML
	private TableColumn<User, String> loginColumn;
	@FXML
	private TableColumn<User, String> facultyColumn;
	@FXML
	private TableColumn<User, Integer> amountOfIdeasColumn;

	// Ideas table
	@FXML
	private TableView<Idea> ideasTable;
	@FXML
	private TableColumn<Idea, Integer> ideaRowNumberColumn;
	@FXML
	private TableColumn<Idea, String> nameColumn;
	@FXML
	private TableColumn<Idea, String> categoryColumn;
	@FXML
	private TableColumn<Idea, Integer> authorColumn;
	@FXML
	private TableColumn<Idea, String> addDateColumn;
	@FXML
	private TableColumn<Idea, String> editDateColumn;
	@FXML
	private TableColumn<Idea, String> statusColumn;

	// Buttons
	@FXML
	private Button changePasswordButton;
	@FXML
	private Button editUserButton;
	@FXML
	private Button delUserButton;
	@FXML
	private Button editIdeaButton;
	@FXML
	private Button delIdeaButton;
	@FXML
	private Button changeIdeaStatusButton;
	@FXML
	private Button showIdeasButton;

	@FXML
	private ChoiceBox<String> statusChoiceBox;

	@FXML
	private ProgressIndicator indicator;

	private Main main;
	private ObservableList<User> usersList = FXCollections.observableArrayList();
	private ObservableList<Idea> ideasList = FXCollections.observableArrayList();

	private ObservableList<String> statusList = FXCollections.observableArrayList();
	private int rights;
	private String login;

	public void setMain(Main main) {
		this.main = main;
	}

	public void setDialogStage(Stage dialogStage) {
	}

	public void setRights(int rights) {
		this.rights = rights;
		if (rights == 2)
			changePasswordButton.setVisible(true);
	}

	public void setLogin(String login) {
		this.login = login;

	}

	@FXML
	private void initialize() {

		indicator.setVisible(false);
		changePasswordButton.setVisible(false);

		statusList.add("Dodany");
		statusList.add("Zaakceptowany");
		statusList.add("W realizacji");
		statusList.add("Zawieszony");
		statusList.add("Zrealizowany");
		statusList.add("Odrzucony");

		statusChoiceBox.getItems().addAll(statusList);

		statusChoiceBox.getSelectionModel().selectFirst();

		usersTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		ideasTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		editUserButton.setDisable(true);
		delUserButton.setDisable(true);
		showIdeasButton.setDisable(true);

		editIdeaButton.setDisable(true);
		delIdeaButton.setDisable(true);
		changeIdeaStatusButton.setDisable(true);
		statusChoiceBox.setDisable(true);

		usersTable.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> enableUserButtons());
		ideasTable.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> enableIdeaElements());

		new Thread(new Runnable() {

			@Override
			public void run() {

				createUsersList();
				createIdeasList();

				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						userRowNumberColumn
								.setCellValueFactory(cellData -> cellData.getValue().rowNumberProperty().asObject());
						firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
						lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
						loginColumn.setCellValueFactory(cellData -> cellData.getValue().loginProperty());
						facultyColumn.setCellValueFactory(cellData -> cellData.getValue().facultyProperty());
						amountOfIdeasColumn.setCellValueFactory(
								cellData -> cellData.getValue().amountOfIdeasProperty().asObject());

						int userColumntDivider = 5;

						userRowNumberColumn.prefWidthProperty().bind(usersTable.widthProperty().divide(30));
						firstNameColumn.prefWidthProperty().bind((usersTable.widthProperty()
								.subtract(userRowNumberColumn.widthProperty().get()).divide(userColumntDivider)));
						lastNameColumn.prefWidthProperty().bind((usersTable.widthProperty()
								.subtract(userRowNumberColumn.widthProperty().get()).divide(userColumntDivider)));
						loginColumn.prefWidthProperty().bind((usersTable.widthProperty()
								.subtract(userRowNumberColumn.widthProperty().get()).divide(userColumntDivider)));
						facultyColumn.prefWidthProperty().bind((usersTable.widthProperty()
								.subtract(userRowNumberColumn.widthProperty().get()).divide(userColumntDivider)));
						amountOfIdeasColumn.prefWidthProperty().bind((usersTable.widthProperty()
								.subtract(userRowNumberColumn.widthProperty().get()).divide(userColumntDivider)));

						usersTable.setItems(usersList);

						ideaRowNumberColumn
								.setCellValueFactory(cellData -> cellData.getValue().rowNumberProperty().asObject());
						nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
						categoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
						authorColumn.setCellValueFactory(cellData -> cellData.getValue().userIdProperty().asObject());
						addDateColumn.setCellValueFactory(cellData -> cellData.getValue().addDateProperty());
						editDateColumn.setCellValueFactory(cellData -> cellData.getValue().editDateProperty());
						statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

						int ideasColumntDivider = 7;

						ideaRowNumberColumn.prefWidthProperty()
								.bind(ideasTable.widthProperty().divide(ideasColumntDivider));
						nameColumn.prefWidthProperty().bind(ideasTable.widthProperty().divide(ideasColumntDivider));
						categoryColumn.prefWidthProperty().bind(ideasTable.widthProperty().divide(ideasColumntDivider));
						authorColumn.prefWidthProperty().bind(ideasTable.widthProperty().divide(ideasColumntDivider));
						addDateColumn.prefWidthProperty().bind(ideasTable.widthProperty().divide(ideasColumntDivider));
						editDateColumn.prefWidthProperty().bind(ideasTable.widthProperty().divide(ideasColumntDivider));
						statusColumn.prefWidthProperty().bind(ideasTable.widthProperty().divide(ideasColumntDivider));

						ideasTable.setItems(ideasList);
					}
				});
			}

		}).start();
	}

	public void createUsersList() {
		UserDaoImpl userDaoImpl = new UserDaoImpl();
		IdeaDaoImpl ideaDaoImpl = new IdeaDaoImpl();
		userDaoImpl.createConnection();
		ideaDaoImpl.createConnection();

		usersList.setAll(userDaoImpl.getUsersList());

		List<User> tempListOfUsers = new ArrayList<>();
		tempListOfUsers = usersList;

		for (int i = 0; i < tempListOfUsers.size(); i++) {

			User user = tempListOfUsers.get(i);
			int isAdmin = user.getIsAdmin();

			System.out.println(user);

			if (isAdmin == 2) {
				usersList.remove(user);
			}
		}

		int rowNumber = 1;

		for (User user : usersList) {
			int amountOfIdeas = ideaDaoImpl.getUserIdeasList(user.getId()).size();
			user.setAmountOfIdeas(amountOfIdeas);

			if (user.getIsAdmin() != 2) {
				user.setRowNumber(rowNumber++);
			}

		}

		userDaoImpl.closeConnection();
		ideaDaoImpl.closeConnection();
	}

	public void createIdeasList() {
		IdeaDaoImpl ideaDaoImpl = new IdeaDaoImpl();
		ideaDaoImpl.createConnection();
		ideasList.setAll(ideaDaoImpl.getAllIdeasList());

		int rowNumber = 1;
		for (Idea idea : ideasList) {
			idea.setRowNumber(rowNumber++);
		}
		ideaDaoImpl.closeConnection();
	}

	public void enableUserButtons() {
		if (usersTable.getSelectionModel().getSelectedItems().size() == 1) {
			editUserButton.setDisable(false);
			showIdeasButton.setDisable(false);
		} else {
			editUserButton.setDisable(true);
			showIdeasButton.setDisable(true);
		}

		delUserButton.setDisable(false);
	}

	public void enableIdeaElements() {
		if (ideasTable.getSelectionModel().getSelectedItems().size() == 1) {
			editIdeaButton.setDisable(false);
		} else {
			editIdeaButton.setDisable(true);
		}

		changeIdeaStatusButton.setDisable(false);
		statusChoiceBox.setDisable(false);
		delIdeaButton.setDisable(false);
	}

	@FXML
	private void handleExport() {
		FileChooser fileChooser = new FileChooser();

		FileChooser.ExtensionFilter extFilterCSV = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
		FileChooser.ExtensionFilter extFilterTXT = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
		FileChooser.ExtensionFilter extFilterPDF = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");

		fileChooser.getExtensionFilters().add(extFilterCSV);
		fileChooser.getExtensionFilters().add(extFilterTXT);
		fileChooser.getExtensionFilters().add(extFilterPDF);

		File file = fileChooser.showSaveDialog(main.getPrimaryStage());
		;
		String selectedExtension = "";

		// If user cancel the fileChooser action
		// there is no selectedExtensionFilterProperty
		// so its need to be check
		if (file != null) {

			selectedExtension = fileChooser.selectedExtensionFilterProperty().get().getExtensions().get(0);

			if (selectedExtension.equals("*.csv")) {
				if (!file.getPath().endsWith(".csv")) {
					file = new File(file.getPath() + ".csv");
				}

			} else if (selectedExtension.equals("*.txt")) {
				if (!file.getPath().endsWith(".txt")) {
					file = new File(file.getPath() + ".txt");
				}

			} else if (selectedExtension.equals("*.pdf")) {
				if (!file.getPath().endsWith(".pdf")) {
					file = new File(file.getPath() + ".pdf");
				}
			}

			try {
				this.export(new FileOutputStream(file), selectedExtension);
			} catch (Exception e) {
				System.out.println("Nie udało się wyeksportować pliku!");
				e.printStackTrace();
			}
		}
	}

	public void export(OutputStream file, String extension) throws Exception {

		Writer writer = null;

		try {
			writer = new PrintWriter(new OutputStreamWriter(file, "UTF-8"));
			// writer = new PrintWriter(file, "UTF-8");
			if (extension.equals("*.csv")) {
				writer.write("\ufeff");
				for (User user : usersList) {
					String text = user.getId() + ";" + user.getFirstName() + ";" + user.getLastName() + ";"
							+ user.getLogin() + "\n";

					writer.write(text);
				}
			} else if (extension.equals("*.txt")) {
				for (User user : usersList) {

					String text = user.toString();

					writer.write(text);
				}

			} else if (extension.equals("*.pdf")) {

				Document document = new Document();

				PdfWriter.getInstance(document, file);

				document.open();

				BaseFont bf = BaseFont.createFont("c:/windows/fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
				Font font = new Font(bf, 12);

				for (User user : usersList) {

					document.add(new Paragraph(user.toString(), font));
					document.add(new Paragraph("\n"));
				}

				document.close();

				file.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			writer.flush();
			writer.close();
		}
	}

	@FXML
	public void handleAddUser() {
		User user = new User("", "", "", "", "");
		main.showAddUserDialog(user, false, false);

		usersList.clear();
		createUsersList();

		delUserButton.setDisable(true);
		editUserButton.setDisable(true);
		showIdeasButton.setDisable(true);
	}

	@FXML
	private void handleAddAdmin() {

		if (this.rights == 2) {
			User user = new User("", "", "", "", "");
			user.setIsAdmin(1);
			main.showAddUserDialog(user, false, true);
			usersList.clear();
			createUsersList();
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(main.getPrimaryStage());
			alert.setTitle("Brak uprawnień!");
			alert.setHeaderText("Nie masz praw aby to zrobić!");
			alert.setContentText("Tylko główny administarator może tego dokonać!");

			alert.showAndWait();
		}
		delUserButton.setDisable(true);
		editUserButton.setDisable(true);
		showIdeasButton.setDisable(true);
	}

	@FXML
	private void handlaEditUser() {
		User user = usersTable.getSelectionModel().getSelectedItem();
		main.setUserId(user.getId());
		if (rights != 0) {
			main.showAddUserDialog(user, true, true);
		} else
			main.showAddUserDialog(user, true, false);
		usersList.clear();
		createUsersList();

		delUserButton.setDisable(true);
		editUserButton.setDisable(true);
		showIdeasButton.setDisable(true);

	}

	@FXML
	private void handleRemoveUser() {

		indicator.setVisible(true);

		// User user = usersTable.getSelectionModel().getSelectedItem();
		ObservableList<User> selectedItems = usersTable.getSelectionModel().getSelectedItems();

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				UserDaoImpl userDaoImpl = new UserDaoImpl();
				userDaoImpl.createConnection();
				for (User user : selectedItems) {
					userDaoImpl.removeUser(user.getId());
				}
				userDaoImpl.closeConnection();

				IdeaDaoImpl ideaDaoImpl = new IdeaDaoImpl();
				ideaDaoImpl.createConnection();

				for (User user : selectedItems) {
					List<Idea> userIdeasList = ideaDaoImpl.getUserIdeasList(user.getId());
					for (Idea idea : userIdeasList) {
						ideaDaoImpl.removeIdea(idea.getId());
					}
				}
				ideaDaoImpl.closeConnection();

				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						indicator.setVisible(false);
						usersList.clear();
						createUsersList();
						ideasList.clear();
						createIdeasList();

						delUserButton.setDisable(true);
						editUserButton.setDisable(true);
						showIdeasButton.setDisable(true);
					}
				});

			}
		});

		thread.start();

	}

	@FXML
	private void handleAddIdea() {
		Idea idea = new Idea("", "", "");
		main.showAddIdeaDialog(idea, false);

		ideasList.clear();
		createIdeasList();

		delIdeaButton.setDisable(true);
		editIdeaButton.setDisable(true);
		changeIdeaStatusButton.setDisable(true);
		statusChoiceBox.setDisable(true);
	}
	
	@FXML
	private void handleEditIdea() {
		Idea idea = ideasTable.getSelectionModel().getSelectedItem();
		main.showAddIdeaDialog(idea, true);

		ideasList.clear();
		createIdeasList();

		delIdeaButton.setDisable(true);
		editIdeaButton.setDisable(true);
		changeIdeaStatusButton.setDisable(true);
		statusChoiceBox.setDisable(true);

	}

	@FXML
	private void handleRemoveIdea() {

		if (rights == 2) {
			indicator.setVisible(true);

			ObservableList<Idea> selectedItems = ideasTable.getSelectionModel().getSelectedItems();

			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {

					IdeaDaoImpl ideaDaoImpl = new IdeaDaoImpl();
					ideaDaoImpl.createConnection();

					for (Idea idea : selectedItems) {
						ideaDaoImpl.removeIdea(idea.getId());
					}

					ideaDaoImpl.closeConnection();

					Platform.runLater(new Runnable() {

						@Override
						public void run() {

							indicator.setVisible(false);
							ideasList.clear();
							createIdeasList();

							delIdeaButton.setDisable(true);
							editIdeaButton.setDisable(true);
							changeIdeaStatusButton.setDisable(true);
							statusChoiceBox.setDisable(true);

						}
					});

				}
			});

			thread.start();
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(main.getPrimaryStage());
			alert.setTitle("Brak uprawnień!");
			alert.setHeaderText("Nie masz praw aby to zrobić!");
			alert.setContentText("Tylko główny administarator może tego dokonać!");

			alert.showAndWait();
		}
	}

	@FXML
	private void handleChangeIdeaStatus() {
		indicator.setVisible(true);

		ObservableList<Idea> selectedItems = ideasTable.getSelectionModel().getSelectedItems();

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {

				IdeaDaoImpl ideaDaoImpl = new IdeaDaoImpl();
				ideaDaoImpl.createConnection();

				for (Idea idea : selectedItems) {
					ideaDaoImpl.changeIdeaStatus(idea.getId(),
							statusChoiceBox.getSelectionModel().getSelectedItem().toString());
				}

				ideaDaoImpl.closeConnection();

				Platform.runLater(new Runnable() {

					@Override
					public void run() {

						indicator.setVisible(false);
						ideasList.clear();
						createIdeasList();

						delIdeaButton.setDisable(true);
						editIdeaButton.setDisable(true);
						changeIdeaStatusButton.setDisable(true);
						statusChoiceBox.setDisable(true);

					}
				});

			}
		});

		thread.start();
	}

	@FXML
	private void handleShowUserIdeas() {
		main.showIdeasLayout(usersTable.getSelectionModel().getSelectedItem().getId());
	}

	@FXML
	private void handleStatistics() {
		main.showIdeaStatistics();
	}

	@FXML
	private void handleLogout() {
		main.showLoginLayut();
	}

	@FXML
	private void handleChangePassword() {
		main.showChangePassLayout(login);
	}

}
