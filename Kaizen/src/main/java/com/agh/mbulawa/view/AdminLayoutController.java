package com.agh.mbulawa.view;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
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
import javafx.scene.control.Button;
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
	private TableColumn<User, Integer> userIdColumn;
	@FXML
	private TableColumn<User, String> firstNameColumn;
	@FXML
	private TableColumn<User, String> lastNameColumn;
	@FXML
	private TableColumn<User, String> loginColumn;
	@FXML
	private TableColumn<User, Integer> amountOfIdeasColumn;

	// Ideas table
	@FXML
	private TableView<Idea> ideasTable;
	@FXML
	private TableColumn<Idea, Integer> ideaIdColumn;
	@FXML
	private TableColumn<Idea, String> nameColumn;
	@FXML
	private TableColumn<Idea, String> categoryColumn;
	@FXML
	private TableColumn<Idea, Integer> authorColumn;
	// Buttons
	@FXML
	private Button editUserButton;
	@FXML
	private Button delUserButton;
	@FXML
	private Button editIdeaButton;
	@FXML
	private Button delIdeaButton;
	@FXML
	private Button showIdeasButton;

	@FXML
	private ProgressIndicator indicator;

	private Main main;
	private ObservableList<User> usersList = FXCollections.observableArrayList();
	private ObservableList<Idea> ideasList = FXCollections.observableArrayList();

	public void setMain(Main main) {
		this.main = main;
	}

	public void setDialogStage(Stage dialogStage) {
	}

	@FXML
	private void initialize() {

		indicator.setVisible(false);

		usersTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		ideasTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		editUserButton.setDisable(true);
		delUserButton.setDisable(true);
		showIdeasButton.setDisable(true);

		editIdeaButton.setDisable(true);
		delIdeaButton.setDisable(true);

		usersTable.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> enableUserButtons());
		ideasTable.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> enableIdeaButtons());

		new Thread(new Runnable() {

			@Override
			public void run() {

				createUsersList();
				createIdeasList();

				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						userIdColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
						firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
						lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
						loginColumn.setCellValueFactory(cellData -> cellData.getValue().loginProperty());
						amountOfIdeasColumn.setCellValueFactory(
								cellData -> cellData.getValue().amountOfIdeasProperty().asObject());

						userIdColumn.prefWidthProperty().bind(usersTable.widthProperty().divide(5));
						firstNameColumn.prefWidthProperty().bind(usersTable.widthProperty().divide(5));
						lastNameColumn.prefWidthProperty().bind(usersTable.widthProperty().divide(5));
						loginColumn.prefWidthProperty().bind(usersTable.widthProperty().divide(5));
						amountOfIdeasColumn.prefWidthProperty().bind(usersTable.widthProperty().divide(5));

						usersTable.setItems(usersList);

						ideaIdColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
						nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
						categoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
						authorColumn.setCellValueFactory(cellData -> cellData.getValue().userIdProperty().asObject());

						ideaIdColumn.prefWidthProperty().bind(ideasTable.widthProperty().divide(4));
						nameColumn.prefWidthProperty().bind(ideasTable.widthProperty().divide(4));
						categoryColumn.prefWidthProperty().bind(ideasTable.widthProperty().divide(4));
						authorColumn.prefWidthProperty().bind(ideasTable.widthProperty().divide(4));

						ideasTable.setItems(ideasList);
					}
				});
			}

		}).start();
	}

	public void createUsersList() {
		UserDaoImpl userDaoImpl = new UserDaoImpl();
		userDaoImpl.createConnection();

		IdeaDaoImpl ideaDaoImpl = new IdeaDaoImpl();
		ideaDaoImpl.createConnection();

		usersList.setAll(userDaoImpl.getUsersList());

		for (User user : usersList) {
			int amountOfIdeas = ideaDaoImpl.getUserIdeasList(user.getId()).size();
			user.setAmountOfIdeas(amountOfIdeas);
		}

		// Bad solution because of change size of usersList during iteration.
		// ConcurrentModificationException.

		/*
		 * for (User user : usersList) { if (user.getIsAdmin() == 1) {
		 * usersList.remove(user); } }
		 */

		for (int i = 0; i < usersList.size(); i++) {
			User user = usersList.get(i);
			if (user.getIsAdmin() == 1) {
				usersList.remove(user);
			}
		}

		userDaoImpl.closeConnection();
		ideaDaoImpl.closeConnection();
	}

	public void createIdeasList() {
		IdeaDaoImpl ideaDaoImpl = new IdeaDaoImpl();
		ideaDaoImpl.createConnection();
		ideasList.setAll(ideaDaoImpl.getAllIdeasList());
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

	public void enableIdeaButtons() {
		if (ideasTable.getSelectionModel().getSelectedItems().size() == 1) {
			editIdeaButton.setDisable(false);
		} else {
			editIdeaButton.setDisable(true);
		}

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
		User user = new User("", "", "", "");
		main.showAddUserDialog(user, false, false);

		usersList.clear();
		createUsersList();

		delUserButton.setDisable(true);
		editUserButton.setDisable(true);
		showIdeasButton.setDisable(true);
	}

	@FXML
	private void handleAddAdmin() {
		User user = new User("", "", "", "");
		user.setIsAdmin(1);
		main.showAddUserDialog(user, false, true);

		delUserButton.setDisable(true);
		editUserButton.setDisable(true);
		showIdeasButton.setDisable(true);
	}
	
	@FXML
	private void handlaEditUser() {
		User user = usersTable.getSelectionModel().getSelectedItem();
		main.setUserId(user.getId());
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
	private void handleEditIdea() {
		Idea idea = ideasTable.getSelectionModel().getSelectedItem();
		main.showAddIdeaDialog(idea, true);

		ideasList.clear();
		createIdeasList();
		
		delIdeaButton.setDisable(true);
		editIdeaButton.setDisable(true);
		
	}

	@FXML
	private void handleRemoveIdea() {
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

					}
				});

			}
		});

		thread.start();
	}

	@FXML private void handleShowUserIdeas () {
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
}
