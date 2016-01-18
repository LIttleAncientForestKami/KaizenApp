package com.agh.mbulawa.view;

import com.agh.mbulawa.Main;
import com.agh.mbulawa.dao.IdeaDaoImpl;
import com.agh.mbulawa.model.Idea;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

public class MainUserLayoutController {

	@FXML
	private TableView<Idea> ideasTable;
	@FXML
	private TableColumn<Idea, String> ideaCategoryColumn;
	@FXML
	private TableColumn<Idea, String> ideaNameColumn;
	@FXML
	private TextArea ideaArea;
	@FXML
	private Button edit;
	/*@FXML
	private Button remove;*/

	private Main main;

	public MainUserLayoutController() {

	}

	@FXML
	private void initialize() {
		ideaCategoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
		ideaNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

		edit.disableProperty().set(true);
		//remove.disableProperty().set(true);

		ideasTable.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> showIdea(newValue));

		ideaCategoryColumn.prefWidthProperty().bind(ideasTable.widthProperty().divide(2));
		
		ideaArea.setEditable(false);
		ideaArea.setWrapText(true);
	}

	public void setMain(Main main) {
		this.main = main;

		ideasTable.setItems(this.main.getIdeasTable());
	}

	public void showIdea(Idea idea) {
		if (idea != null)
			ideaArea.setText(idea.getContent());

		if (edit.isDisabled() /*&& remove.isDisabled()*/) {
			new Thread() {
				public void run() {

					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							edit.setDisable(false);
							//remove.setDisable(false);
						}
					});
				}
			}.start();
		}
	}

	public void clearArea() {
		ideaArea.setText("");
		edit.setDisable(false);
		//remove.setDisable(false);
	}

	@FXML
	public void handleAdd() {
		Idea idea = new Idea("", "", "");
		boolean isOkCliked = main.showAddIdeaDialog(idea, false);
		if (isOkCliked) {
			main.refreshTable();
			edit.setDisable(true);
			//remove.setDisable(true);
		}
	}

	@FXML
	private void handleEdit() {
		/*String category = ideasTable.getSelectionModel().getSelectedItem().getCategory();
		String name = ideasTable.getSelectionModel().getSelectedItem().getName();
		String content = ideasTable.getSelectionModel().getSelectedItem().getContent();
		Idea idea = new Idea(category, name, content);*/
		Idea idea = ideasTable.getSelectionModel().getSelectedItem();
		boolean isOkCliked = main.showAddIdeaDialog(idea, true);
		if (isOkCliked) {
			clearArea();
			main.refreshTable();
			edit.setDisable(true);
			//remove.setDisable(true);
		}

	}

	@FXML
	private void handleRemove() {
		IdeaDaoImpl ideaDaoImpl = new IdeaDaoImpl();
		String name = ideasTable.getSelectionModel().getSelectedItem().getName();
		ideaDaoImpl.createConnection();

		int id = ideaDaoImpl.getIdeaIdByName(name);
		ideaDaoImpl.removeIdea(id);
		ideaDaoImpl.closeConnection();
		clearArea();
		main.refreshTable();
		edit.setDisable(true);
		//remove.setDisable(true);

	}

	@FXML
	private void handleLogout() {
		main.setUserId(0);
		main.getIdeasTable().clear();
		main.showLoginLayut();
	}

}
