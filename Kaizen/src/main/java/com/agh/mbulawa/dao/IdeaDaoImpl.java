package com.agh.mbulawa.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.agh.mbulawa.model.Idea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdeaDaoImpl implements IdeaDao {

	private static final String DB_DRIVER = "org.sqlite.JDBC";
	private static final String DB_URL = "jdbc:sqlite:kaizen.db";

	private Connection connection;
	private Statement statement;

    static final Logger logger = LoggerFactory.getLogger(IdeaDaoImpl.class);

	public IdeaDaoImpl() {
		try {
			Class.forName(IdeaDaoImpl.DB_DRIVER);
			createConnection();
			createTable();
			closeConnection();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	public void createConnection() {
        logger.trace("Nawiązywanie połączenia z bazą...");

		try {
			connection = DriverManager.getConnection(DB_URL);
			statement = connection.createStatement();
            logger.trace("Pomyślnie połączono z bazą.");
		} catch (SQLException e) {
            logger.error("Nie udało się nawiązać połączenia z bazą: {}.", e.getCause(), e);
		}
	}

	public void closeConnection() {
        logger.trace("Zamykanie połączenia z bazą...");

		try {
			statement.close();
			connection.close();

            logger.trace("Poprawnie zakończono połączenie z bazą.");
		} catch (SQLException e) {
            logger.error("Wystąpił błąd podczas kończenia połączenia{}.", e.getCause(), e);
		}

	}

	public void createTable() {

		String createIdeasTableQuery = "CREATE TABLE IF NOT EXISTS Pomysły (id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ " Kategoria varchar(255), Nazwa varchar(255), Treść varchar(255), id_Pracownika int, data_dodania datetime default current_datetime, data_edycji datetime default current_datetime, status varchar(255))";
		try {
			if (statement.execute(createIdeasTableQuery)) {
                logger.info("Nie znaleziono tabeli Pomysłów...");
                logger.info("Tworzenie nowej tabeli.");
                logger.info("Pomyślnie utworzono table pomysłów w bazie");
			} else {
				logger.info("Pomyślnie połączono z tabelą pomysłów w bazie");
			}
		} catch (SQLException e) {
            logger.error("Nie udało się odnaleźć lub utworzyć tabeli w bazie: {}.", e.getCause(), e);
		} finally {

		}
	}

	@Override
	public boolean addIdea(Idea idea, int userId) {
		String addIdeaQuery = "INSERT INTO Pomysły VALUES (NULL, ?, ?, ?, ?, ?, ?, ?)";

		System.out.println("Dodawanie pomysłu do bazy...");

		java.util.Date date = new java.util.Date();

		Date sqlDate = new Date(date.getTime());

		try {
			// createConnection();
			PreparedStatement prepareStatement = connection.prepareStatement(addIdeaQuery);
			prepareStatement.setString(1, idea.getCategory());
			prepareStatement.setString(2, idea.getName());
			prepareStatement.setString(3, idea.getContent());
			prepareStatement.setInt(4, userId);
			prepareStatement.setDate(5, sqlDate);
			prepareStatement.setDate(6, sqlDate);
			prepareStatement.setString(7, idea.getStatus());
			prepareStatement.executeUpdate();
			// closeConnection();
			System.out.println("Pomyślnie dodano pomysł do bazy danych.");
			return true;
		} catch (SQLException e) {
			logger.error("Nie udało się dodać pomysłu do bazy: {}.", e.getCause(), e);
			return false;
		}

	}

	@Override
	public Idea getIdeaById(int id) {

		String getIdeaQuery = "SELECT * FROM Pomysły WHERE id = '" + id + "';";

		logger.trace("Pobieranie treści pomysłu z bazy...");

		try {
			ResultSet result = statement.executeQuery(getIdeaQuery);
			result.next();

			String category = result.getString(1);
			String name = result.getString(2);
			String content = result.getString(3);
			int userId = result.getInt(4);
			Date addDate = result.getDate(6);
			Date editDate = result.getDate(7);
			String status = result.getString(8);

			Idea idea = new Idea(category, name, content);

			idea.setId(id);
			idea.setUserId(userId);
			idea.setAddDate(addDate);
			idea.setEditDate(editDate);
			idea.setStatus(status);
			result.close();

			System.out.println("Pomyślnie pobrano treść pomysłu o id: " + id + "z bazy.");

			return idea;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Nie udało się pobrać treści pomysłu o id " + id + " z bazy.");
		}
		return null;
	}

	@Override
	public int getLastAddedIdeaId() {
		try {
			ResultSet result = statement.executeQuery("SELECT MAX(id) FROM Pomysły");
			result.next();
			int lastAddedId = result.getInt(1);
			result.close();
			return lastAddedId;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int getIdeaIdByName(String name) {
		try {
			ResultSet result = statement.executeQuery("SELECT id FROM Pomysły WHERE Nazwa='" + name + "'");
			result.next();
			int id = result.getInt(1);
			result.close();
			return id;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public List<Idea> getAllIdeasList() {
		List<Idea> ideas = new ArrayList<Idea>();

		String getIdeaQuery = "SELECT * FROM Pomysły";

		System.out.println("Pobieranie wszystkich pomysłów z bazy...");

		try {

			ResultSet result = statement.executeQuery(getIdeaQuery);
			while (result.next()) {

				int id = result.getInt(1);
				String category = result.getString(2);
				String name = result.getString(3);
				String content = result.getString(4);
				int userId = result.getInt(5);
				Date addDate = result.getDate(6);
				Date editDate = result.getDate(7);
				String status = result.getString(8);

				Idea idea = new Idea(category, name, content);
				idea.setId(id);
				idea.setUserId(userId);
				idea.setAddDate(addDate);
				idea.setEditDate(editDate);
				idea.setStatus(status);
				ideas.add(idea);
			}
			System.out.println("Pomyślnie pobrano wszystkie pomysły z bazy danych.");
			result.close();

			return ideas;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Nie udało się pobrać listy pomysłów z bazy.");
		}
		return null;
	}

	@Override
	public List<Idea> getUserIdeasList(int userId) {
		List<Idea> ideas = new ArrayList<Idea>();

		String getIdeaQuery = "SELECT * FROM Pomysły WHERE id_Pracownika = " + userId + "";

		System.out.println("Pobieranie pomysłów pracownika o id " + userId + " z bazy...");

		try {

			ResultSet result = statement.executeQuery(getIdeaQuery);
			while (result.next()) {

				int id = result.getInt(1);
				String category = result.getString(2);
				String name = result.getString(3);
				String content = result.getString(4);
				Date addDate = result.getDate(6);
				Date editDate = result.getDate(7);
				String status = result.getString(8);

				Idea idea = new Idea(category, name, content);
				idea.setId(id);
				idea.setUserId(userId);
				idea.setAddDate(addDate);
				idea.setEditDate(editDate);
				idea.setStatus(status);
				ideas.add(idea);
			}
			System.out.println("Pomyślnie pobrano pomysły pracownika o id = " + userId + " z bazy danych.");
			result.close();

			return ideas;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Nie udało się pobrać listy pomysłów z bazy.");
		}
		return null;
	}

	@Override
	public boolean updateIdea(Idea idea) {

		java.util.Date date = new java.util.Date();

		Date sqlDate = new Date(date.getTime());

		System.out.println(sqlDate);

		String updateIdeaQuery = "UPDATE Pomysły SET Kategoria= ?, Nazwa= ?, Treść= ?, data_edycji= ? WHERE id = ?";

		try {
			PreparedStatement prepareStatement = connection.prepareStatement(updateIdeaQuery);
			prepareStatement.setString(1, idea.getCategory());
			prepareStatement.setString(2, idea.getName());
			prepareStatement.setString(3, idea.getContent());
			prepareStatement.setDate(4, sqlDate);
			prepareStatement.setInt(5, idea.getId());
			prepareStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean removeIdea(int id) {

		String removeIdeaQuery = "DELETE FROM Pomysły WHERE id = " + id;
		System.out.println("Usuwanie pomysłu o id " + id + " z bazy...");
		try {
			statement.executeUpdate(removeIdeaQuery);
			System.out.println("Pomyślnie usunięto pomysł o id " + id);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public boolean changeIdeaStatus(int id, String status) {
		java.util.Date date = new java.util.Date();

		Date sqlDate = new Date(date.getTime());

		System.out.println(sqlDate);

		String updateIdeaQuery = "UPDATE Pomysły SET status= ? WHERE id = ?";

		try {
			PreparedStatement prepareStatement = connection.prepareStatement(updateIdeaQuery);
			prepareStatement.setString(1, status);
			prepareStatement.setInt(2, id);
			prepareStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

}
