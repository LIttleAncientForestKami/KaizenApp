package com.agh.mbulawa.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.agh.mbulawa.model.User;

public class UserDaoImpl implements UserDao {

	private static final String DB_DRIVER = "org.sqlite.JDBC";
	private static final String DB_URL = "jdbc:sqlite:kaizen.db";

	private Connection connection;
	private Statement statement;

	public UserDaoImpl() {
		try {
			Class.forName(UserDaoImpl.DB_DRIVER);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void createConnection() {

		System.out.println("Nawiązywanie połączenia z bazą...");

		try {
			connection = DriverManager.getConnection(DB_URL);
			statement = connection.createStatement();
			System.out.println("Pomyślnie połączono z bazą.");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Nie udało się nawiązać połaczenia z bazą.");
		}
	}

	public void closeConnection() {

		System.out.println("Zamykanie połączenia z bazą...");

		try {
			statement.close();
			// connection.commit();
			connection.close();

			System.out.println("Poprawnie zakończono połączenie z bazą.");
		} catch (SQLException e) {
			System.out.println("Wystąpił błąd podczas kończenia połączenia.");
			e.printStackTrace();
		}

	}

	public void createTable() {
		String createUserTableQuery = "CREATE TABLE IF NOT EXISTS Pracownicy (id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ " Imię varchar(255), Nazwisko varchar(255), Login varchar(255), Hasło varchar(255), Administrator INTEGER)";
		try {

			boolean execute = statement.execute(createUserTableQuery);
			if (execute) {
				System.out.println("Nie znaleziono tabeli Pracownicy...");
				System.out.println("Tworzenie nowej tabeli.");
				System.out.println("Pomyślnie utworzono table pracowników w bazie");
			} else {
				System.out.println("Pomyślnie połączono z tabelą pracowników w bazie");
			}

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Nie udało się odnaleźć lub utworzyć tabeli prcowników w bazie.");
		} finally {

		}
	}

	@Override
	public boolean addUser(User user) {

		String addUserQuery = "INSERT INTO Pracownicy VALUES (NULL, ?, ?, ?, ?, ?)";

		System.out.println("Dodawanie urzytkownika do bazy...");

		try {

			PreparedStatement prepareStatement = connection.prepareStatement(addUserQuery);
			prepareStatement.setString(1, user.getFirstName());
			prepareStatement.setString(2, user.getLastName());
			prepareStatement.setString(3, user.getLogin());
			prepareStatement.setString(4, user.getPassword());
			prepareStatement.setInt(5, user.getIsAdmin());
			prepareStatement.executeUpdate();

			System.out.println("Pomyślnie dodano użytkownika do bazy danych.");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Nie udało się dodać użytkownika do bazy.");
		}
		return false;
	}

	@Override
	public User getUserById(int id) {

		String getUserQuery = "SELECT * FROM Pracownicy WHERE id = '" + id + "';";

		System.out.println("Pobieranie danych pracownika z bazy...");

		try {

			ResultSet result = statement.executeQuery(getUserQuery);
			result.next();

			String firstName = result.getString(2);
			String lastName = result.getString(3);
			String login = result.getString(4);
			String pass = result.getString(5);

			int isAdmin = result.getInt(6);

			User user = new User(firstName, lastName, login, pass);

			user.setId(id);
			user.setIsAdmin(isAdmin);
			result.close();

			System.out.println("Pomyślnie pobrano dane pracownika o id: " + id + "z bazy.");

			return user;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Nie udało się pobrać danych pracownika o id" + id + " z bazy.");
		}
		return null;
	}

	@Override
	public List<User> getUsersList() {

		List<User> users = new ArrayList<User>();

		String getUserQuery = "SELECT * FROM Pracownicy";

		System.out.println("Pobieranie wszystkich pracowników z bazy...");

		try {

			ResultSet result = statement.executeQuery(getUserQuery);
			while (result.next()) {

				int id = result.getInt(1);
				String firstName = (result.getString(2));
				String lastName = (result.getString(3));
				String login = (result.getString(4));
				String password = (result.getString(5));

				int isAdmin = result.getInt(6);

				User user = new User(firstName, lastName, login, password);
				user.setId(id);
				user.setIsAdmin(isAdmin);
				users.add(user);
			}
			System.out.println("Pomyślnie pobrano wszystkich pracowników z bazy danych.");
			result.close();

			return users;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Nie udało się pobrać listy pracowników z bazy.");
		}
		return null;
	}

	@Override
	public boolean updateUser(User user) {

		String updateUserQuery = "UPDATE Pracownicy SET Imię = '" + user.getFirstName() + "', Nazwisko = '"
				+ user.getLastName() + "', Login = '" + user.getLogin() + "', Hasło = '" + user.getPassword()
				+ "', Administrator ='" + user.getIsAdmin() + "' WHERE id = '" + user.getId() + "'";
		System.out.println("Aktualizacja pracownika o id: " + user.getId());
		try {

			statement.executeUpdate(updateUserQuery);

			System.out.println("Pracownik został pomyślnie zaktualizowany");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean removeUser(int id) {

		System.out.println("Usuwanie pracownika o id: " + id + "...");

		String removeUserQuery = "DELETE FROM Pracownicy WHERE id='" + id + "'";
		try {

			statement.executeUpdate(removeUserQuery);

			System.out.println("Pomyślnie usunięto pracownika.");
		} catch (SQLException e) {
			System.out.println("Nie udało się usunąć pracownika.");
			e.printStackTrace();
		}

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
			System.out.println("Nieprawidłowy login");
			return null;
		}
	}

	@Override
	public int isUserAdmin(int id) {
		
		String getUserRightsQuery = "SELECT Administrator FROM Pracownicy WHERE id = '" + id + "'";
		try{
			ResultSet result = statement.executeQuery(getUserRightsQuery);
			result.next();
			int isAdmin = result.getInt(1);
			System.out.println(isAdmin);
			
			return isAdmin;
			
		}catch(SQLException e) {
			e.printStackTrace();
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
			System.out.println("Nieprawidłowy login");
			return 0;
		}
	}

}
