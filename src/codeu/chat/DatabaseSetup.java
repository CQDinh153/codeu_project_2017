package codeu.chat;

import codeu.chat.server.Database;
import codeu.chat.util.Logger;

import java.sql.*;

public class DatabaseSetup {
  private static final Logger.Log LOG = Logger.newLog(codeu.chat.server.Database.class);

  public static void main(String[] args) {

    Logger.enableConsoleOutput();

    try {

      // Load the driver
      Class.forName("org.sqlite.JDBC");

      // Construct the database URL
      String url = "jdbc:sqlite:" + args[0];

      LOG.info("Setting up database at " + args[0]);

      try {
        // Connect to the database
        // If the database does not exist, this will create it
        Connection conn = DriverManager.getConnection(url);

        LOG.info("Connection to SQLite Database established");

        Statement stmt = conn.createStatement();

        try {

          LOG.info("Creating Users table");
          stmt.execute("CREATE TABLE " + Database.usersTable);

          LOG.info("Creating Conversations table");
          stmt.execute("CREATE TABLE " + Database.conversationsTable);

          LOG.info("Creating Messages table");
          stmt.execute("CREATE TABLE " + Database.messagesTable);

        } catch (SQLException ex) {

          LOG.error("The database could not be set up");
          LOG.error(ex.getMessage());
          System.exit(1);

        }
      } catch (SQLException ex) {

        LOG.error("Could not connect to the database");
        LOG.error(ex.getMessage());
        System.exit(1);

      }
    } catch (ClassNotFoundException ex) {

      LOG.error("Could not load the JDBC driver");
      LOG.error(ex.getMessage());
      System.exit(1);

    }
    LOG.info("Setup Complete");
  }
}
