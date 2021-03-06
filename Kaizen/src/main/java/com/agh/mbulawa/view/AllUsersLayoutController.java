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
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AllUsersLayoutController {

	@FXML
	private TableView<User> usersTable;
	@FXML
	private TableColumn<User, Integer> idColumn;
	@FXML
	private TableColumn<User, String> firstNameColumn;
	@FXML
	private TableColumn<User, String> lastNameColumn;
	@FXML
	private TableColumn<User, String> loginColumn;
	@FXML
	private Button editButton;
	@FXML
	private Button delButton;
	@FXML
	private PasswordField pass;
	@FXML
	private ProgressIndicator indicator;

	private Main main;
	private Stage dialogStage;

	private ObservableList<User> usersList = FXCollections.observableArrayList();

	public void setMain(Main main) {
		this.main = main;
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	@FXML
	private void initialize() {

		indicator.setVisible(false);

		usersTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		editButton.setDisable(true);
		delButton.setDisable(true);

		editButton.setVisible(false);
		delButton.setVisible(false);

		usersTable.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> enableButtons());

		new Thread(new Runnable() {

			@Override
			public void run() {

				createList();

				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
						firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
						lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
						loginColumn.setCellValueFactory(cellData -> cellData.getValue().loginProperty());

						idColumn.prefWidthProperty().bind(usersTable.widthProperty().divide(4));
						firstNameColumn.prefWidthProperty().bind(usersTable.widthProperty().divide(4));
						lastNameColumn.prefWidthProperty().bind(usersTable.widthProperty().divide(4));
						loginColumn.prefWidthProperty().bind(usersTable.widthProperty().divide(4));

						usersTable.setItems(usersList);
					}
				});
			}

		}).start();
	}

	public void createList() {
		UserDaoImpl userDaoImpl = new UserDaoImpl();
		userDaoImpl.createConnection();
		usersList.setAll(userDaoImpl.getUsersList());
		userDaoImpl.closeConnection();
	}

	public void enableButtons() {
		if (usersTable.getSelectionModel().getSelectedItems().size() == 1) {
			editButton.setDisable(false);
		} else {
			editButton.setDisable(true);
		}

		delButton.setDisable(false);
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
	private void handlaEdit() {
		User user = usersTable.getSelectionModel().getSelectedItem();
		main.setUserId(user.getId());
		main.showAddUserDialog(user, true, false);
		usersList.clear();
		createList();
	}

	@FXML
	private void handleRemove() {

		
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
						createList();
					}
				});

			}
		});

		thread.start();

	}

	@FXML
	private void handleManage() {
		if (pass.getText().equals("admin")) {
			editButton.setVisible(true);
			delButton.setVisible(true);
		}
	}

	@FXML
	private void handleExit() {
		dialogStage.close();
	}
}
