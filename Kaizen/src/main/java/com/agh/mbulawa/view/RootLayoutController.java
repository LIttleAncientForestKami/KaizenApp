package com.agh.mbulawa.view;

import com.agh.mbulawa.Main;

import javafx.fxml.FXML;

public class RootLayoutController {

	@SuppressWarnings("unused")
	private Main main;

	public void setMain(Main main) {
		this.main = main;
	}

	@FXML
	private void initialize() {
		
	}

	/*@SuppressWarnings("resource")
	private void copyFileUsingFileChannels(File source, File dest)

			throws IOException {

		FileChannel inputChannel = null;

		FileChannel outputChannel = null;

		try {

			inputChannel = new FileInputStream(source).getChannel();

			outputChannel = new FileOutputStream(dest).getChannel();

			outputChannel.transferFrom(inputChannel, 0, inputChannel.size());

		} finally {

			inputChannel.close();

			outputChannel.close();

		}

	}*/

	/*@FXML
	private void handleShowIdeaStatistics() {
		main.showIdeaStatistics();
	}

	@FXML
	private void handleBackUpDataBase(){
		String property = System.getProperty("user.dir");
		File sourceDBFile = new File(property + "\\kaizen.db");
		File destDBFile = new File(property + "\\BackupDB\\kaizenBackUp.db");
		
		try {
			copyFileUsingFileChannels(sourceDBFile, destDBFile);
		} catch (IOException e) {
			System.out.println("Nie udało się utowrzyć kopi zapasowej bazy!");
			e.printStackTrace();
		}
	}*/
	
	@FXML
	private void handleClose() {
		System.exit(0);
	}

}
