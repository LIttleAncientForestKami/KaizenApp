package com.agh.mbulawa.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.*;

/**
 * @author LAFK_pl, Tomasz.Borek@gmail.com
 */
abstract class ADao {

    private static final String DB_DRIVER = "org.sqlite.JDBC";
    private static final String DB_URL = "jdbc:sqlite:kaizen.db";

    static final Logger logger = LoggerFactory.getLogger(ADao.class);

    private Connection connection;
    private Statement statement;


    ADao() {
        try {
            File file = new File("kaizen.db");

            if (file.exists()) {
                logger.info("Wszystko ustawione prawidłowo");
            } else {
                Class.forName(ADao.DB_DRIVER);
                createConnection();
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

    public PreparedStatement prepareStatementFrom(String aQuery) throws SQLException {
        return connection.prepareStatement(aQuery);
    }

    public ResultSet run(String aQuery) throws SQLException {
        return statement.executeQuery(aQuery);
    }

    public void update(String aChangingQuery) throws SQLException {
        statement.executeUpdate(aChangingQuery);
    }

    public boolean executeOrNot(String createTableQuery) throws SQLException {
        return statement.execute(createTableQuery);
    }
}
