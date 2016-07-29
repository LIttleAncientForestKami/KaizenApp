package com.agh.mbulawa.dao;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.agh.mbulawa.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDaoImpl implements UserDao {

	private static final String DB_DRIVER = "org.sqlite.JDBC";
	private static final String DB_URL = "jdbc:sqlite:kaizen.db";

    static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

	private Connection connection;
	private Statement statement;

	public UserDaoImpl() {
		try {
			File file = new File("kaizen.db");

			if (file.exists()) {
				logger.info("Wszystko ustawione prawidłowo");
			} else {
				Class.forName(UserDaoImpl.DB_DRIVER);
				createConnection();
				createTable();
				User user = new User("Main", "Main", "Main", "Main", "Main");
				user.setIsAdmin(2);
				addUser(user);
				closeConnection();
			}

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
            logger.error("Nie udało się nawiązać połaczenia z bazą: {}.", e.getCause(), e);
        }
	}

	public void closeConnection() {
		logger.trace("Zamykanie połączenia z bazą...");

		try {
			statement.close();
			connection.close();

            logger.trace("Poprawnie zakończono połączenie z bazą.");
        } catch (SQLException e) {
            logger.error("Wystąpił błąd podczas kończenia połączenia: {}.", e.getCause(), e);
        }
	}

	public void createTable() {
		String createUserTableQuery = "CREATE TABLE IF NOT EXISTS Pracownicy (id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ " Imię varchar(255), Nazwisko varchar(255), Login varchar(255), Wydział varchar(255), Hasło varchar(255), Administrator INTEGER)";
		try {

            if (statement.execute(createUserTableQuery)) {
                logger.info("Nie znaleziono tabeli Pracownicy...");
                logger.info("Tworzenie nowej tabeli.");
                logger.info("Pomyślnie utworzono tabele pracowników w bazie");
            } else {
                logger.info("Pomyślnie połączono z tabelą pomysłów w bazie");
            }
        } catch (SQLException e) {
            logger.error("Nie udało się odnaleźć lub utworzyć tabeli pracowników w bazie: {}.", e.getCause(), e);
		} finally {

		}
	}

	@Override
	public boolean addUser(User user) {

		String addUserQuery = "INSERT INTO Pracownicy VALUES (NULL, ?, ?, ?, ?, ?, ?)";

		logger.info("Dodawanie użytkownika do bazy...");

		try {

			PreparedStatement prepareStatement = connection.prepareStatement(addUserQuery);
			prepareStatement.setString(1, user.getFirstName());
			prepareStatement.setString(2, user.getLastName());
			prepareStatement.setString(3, user.getLogin());
			prepareStatement.setString(4, user.getFaculty());
			prepareStatement.setString(5, user.getPassword());
			prepareStatement.setInt(6, user.getIsAdmin());
			prepareStatement.executeUpdate();

			logger.info("Pomyślnie dodano użytkownika do bazy danych.");
		} catch (SQLException e) {
			logger.error("Nie udało się dodać użytkownika do bazy: {}.", e.getCause(), e);
		}
		return false;
	}

	@Override
	public User getUserById(int id) {

		String getUserQuery = "SELECT * FROM Pracownicy WHERE id = '" + id + "';";

        logger.info("Pobieranie danych pracownika z bazy...");

		try {

			ResultSet result = statement.executeQuery(getUserQuery);
			result.next();

			String firstName = result.getString(2);
			String lastName = result.getString(3);
			String login = result.getString(4);
			String faculty = result.getString(5);
			String pass = result.getString(6);

			int isAdmin = result.getInt(7);

			User user = new User(firstName, lastName, login, faculty, pass);

			user.setId(id);
			user.setIsAdmin(isAdmin);
			result.close();

            logger.trace("Pomyślnie pobrano dane pracownika o id: {} z bazy.", id);

			return user;
		} catch (SQLException e) {
            logger.error("Nie udało się pobrać danych pracownika {} w bazie: {}", id, e.getCause(), e);
		}
		return null;
	}

	@Override
	public List<User> getUsersList() {

		List<User> users = new ArrayList<User>();

		String getUserQuery = "SELECT * FROM Pracownicy";

        logger.trace("Pobieranie wszystkich pracowników z bazy...");

		try {

			ResultSet result = statement.executeQuery(getUserQuery);
			while (result.next()) {

				int id = result.getInt(1);
				String firstName = (result.getString(2));
				String lastName = (result.getString(3));
				String login = (result.getString(4));
				String faculty = (result.getString(5));
				String password = (result.getString(6));

				int isAdmin = result.getInt(7);

				User user = new User(firstName, lastName, login, faculty, password);
				user.setId(id);
				user.setIsAdmin(isAdmin);
				users.add(user);
			}
            logger.trace("Pomyślnie pobrano wszystkich pracowników z bazy danych.");
			result.close();

			return users;
		} catch (SQLException e) {
            logger.error("Nie udało się pobrać listy pracowników z bazy: {}.", e.getCause(), e);
		}
		return null;
	}

	@Override
	public boolean updateUser(User user) {

		String updateUserQuery = "UPDATE Pracownicy SET Imię = '" + user.getFirstName() + "', Nazwisko = '"
				+ user.getLastName() + "', Login = '" + user.getLogin() + "', Wydział = '" + user.getFaculty()
				+ "', Hasło = '" + user.getPassword() + "', Administrator ='" + user.getIsAdmin() + "' WHERE id = '"
				+ user.getId() + "'";
        logger.info("Aktualizacja pracownika o id: " + user.getId());
		try {

			statement.executeUpdate(updateUserQuery);

            logger.info("Pracownik został pomyślnie zaktualizowany");
		} catch (SQLException e) {
			logger.error("Nieudana aktualizacja pracownika: {}.", e.getCause(), e);
		}
		return false;
	}

	@Override
	public boolean removeUser(int id) {
        logger.trace("Usuwanie pracownika o id: {}.", id);

		String removeUserQuery = "DELETE FROM Pracownicy WHERE id='" + id + "'";
		try {
			statement.executeUpdate(removeUserQuery);

            logger.trace("Pomyślnie usunięto pracownika.");
		} catch (SQLException e) {
            logger.error("Nie udało się usunąć pracownika: {}.", e.getCause(), e);
		}
        //FIXME: always false?
		return false;
	}

	@Override
	public String getUserPassByLogin(String login) {

		String getUserPassQuery = "SELECT Hasło FROM Pracownicy WHERE Login = '" + login + "'";
		try {
			ResultSet result = statement.executeQuery(getUserPassQuery);
			result.next();
			String pass = result.getString(1);

			return pass;
		} catch (SQLException e) {
            logger.error("Nieprawidłowy login: {}.", e.getCause(), e);
			return null;
		}
	}

	@Override
	public int isUserAdmin(int id) {

		String getUserRightsQuery = "SELECT Administrator FROM Pracownicy WHERE id = '" + id + "'";
		try {
			ResultSet result = statement.executeQuery(getUserRightsQuery);
			result.next();
			int isAdmin = result.getInt(1);
			return isAdmin;

		} catch (SQLException e) {
            logger.error("Nieudane wybranie admina: {}.", e.getCause(), e);
			return -1;
		}
	}

	@Override
	public int getUserIdByLogin(String login) {
		String getUserIdQuery = "SELECT Id FROM Pracownicy WHERE Login = '" + login + "'";
		try {
			ResultSet result = statement.executeQuery(getUserIdQuery);
			result.next();
			int id = result.getInt(1);
			return id;
		} catch (SQLException e) {
            logger.error("Nieprawidłowy login: {}.", e.getCause(), e);
			return 0;
		}
	}
}