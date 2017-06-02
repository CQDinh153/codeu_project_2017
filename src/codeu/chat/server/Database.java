package codeu.chat.server;

import codeu.chat.common.*;
import codeu.chat.util.Logger;
import codeu.chat.util.Uuid;

import java.sql.*;

public class Database {
  // The column specifications for the tables
  // Uuids are stored as strings
  // Times are stored as their representation in ms in Integers since SQLite has up to 64 bit integers
  public static final String usersTable = "users(" +
    "id INTEGER PRIMARY KEY NOT NULL, " +
    "name TEXT NOT NULL, " +
    "creation INTEGER NOT NULL" +
    ")";
  public static final String conversationsTable = "conversations(" +
    "id INTEGER PRIMARY KEY NOT NULL, " +
    "owner INTEGER NOT NULL, " +
    "creation INTEGER NOT NULL, " +
    "title TEXT NOT NULL" +
    ")";
  public static final String messagesTable = "messages(" +
    "id INTEGER PRIMARY KEY NOT NULL, " +
    "creation INTEGER NOT NULL, " +
    "author INTEGER NOT NULL, " +
    "conversation INTEGER NOT NULL, " +
    "content TEXT NOT NULL" +
    ")";

  private PreparedStatement userStatement;
  private PreparedStatement conversationStatement;
  private PreparedStatement messageStatement;
  private Statement stmt;

  private Connection conn;

  private static final Logger.Log LOG = Logger.newLog(Database.class);

  public Database(String filename) {
    try {

      // Load the driver
      Class.forName("org.sqlite.JDBC");

      // Construct the database URL
      String url = "jdbc:sqlite:" + filename;

      try {
        // Connect to the database
        // If the database does not exist, this will create it
        conn = DriverManager.getConnection(url);

        LOG.info("Connection to SQLite Database established");

        // Create a Statement object to execute queries from
        stmt = conn.createStatement();

        // Create PreparedStatement objects that precompile the queries to add each type of object to the database
        messageStatement = conn.prepareStatement("INSERT INTO messages(id, creation, author, conversation, content) VALUES (?, ?, ?, ?, ?)");
        conversationStatement = conn.prepareStatement("INSERT INTO conversations(id, owner, creation, title) VALUES (?, ?, ?, ?)");
        userStatement = conn.prepareStatement("INSERT INTO users(id, name, creation) VALUES (?, ?, ?)");

      } catch (SQLException ex) {

        LOG.error("Could not connect to the database");
        LOG.error(ex.getMessage());
        System.exit(1); // Exit the program

      }
    } catch (ClassNotFoundException ex) {

      LOG.error("Could not load the JDBC driver");
      LOG.error(ex.getMessage());
      System.exit(1); // Exit the program

    }
  }

  public void close() throws SQLException {
    conn.close();
  }


  // Runs a SQL query on the database and returns
  // true if the result is a ResultSet
  // false if it is an update count or there are not results
  // Only implemented for completeness with execureQuery and executeUpdate
  public boolean execute(String query) throws SQLException {
    return stmt.execute(query);
  }

  // Runs a SQL query on the database and returns the ResultSet that the database returns
  // Use this to run queries like SELECT
  public ResultSet executeQuery(String query) throws SQLException {
    return stmt.executeQuery(query);
  }

  // Runs a SQL query on the database and returns the number of rows that were affected
  // Use this to run INSERT, UPDATE, or DELETE queries or queries that return nothing
  // If the query returns nothing, this returns 0
  public int executeUpdate(String query) throws SQLException {
    return stmt.executeUpdate(query);
  }

  // Save a message into the database
  // The conversation id is necessary to remember which conversation the message is in
  public boolean saveMessage(Message msg, Uuid conversation) {
    try {
      // Set the id
      messageStatement.setInt(1, msg.id.id());
      // Set the creation time
      messageStatement.setLong(2, msg.creation.inMs());
      // Set the author id
      messageStatement.setInt(3, msg.author.id());
      // Set the conversation id
      messageStatement.setInt(4, conversation.id());
      // Set the content string
      messageStatement.setString(5, msg.content);
      // Update the row
      int updatedRows = messageStatement.executeUpdate();

      if (updatedRows == 1) {

        // If one row was changed, return true
        return true;

      } else if (updatedRows == 0) {

        // If no rows were changed, return false
        return false;
      } else {

        // Throw an exception if the number of rows changed is not 0 or 1
        // This should never happen since the query can only affect 1 row at most
        throw new SQLException("Invalid number of rows were changed");

      }

    } catch (SQLException ex) {

      LOG.error("An exception occurred while saving a message\n" + ex.getMessage() + "\nError Code: " + ex.getErrorCode());

    }

    return false;
  }

  // Save a conversation into the database
  public boolean saveConversation(Conversation conversation) {
    try {
      // Set the id
      conversationStatement.setInt(1, conversation.id.id());
      // Set the owner id
      conversationStatement.setInt(2, conversation.owner.id());
      // Set the creation time
      conversationStatement.setLong(3, conversation.creation.inMs());
      // Set the conversation title
      conversationStatement.setString(4, conversation.title);
      // Update the row
      int updatedRows = conversationStatement.executeUpdate();

      if (updatedRows == 1) {

        // If one row was changed, return true
        return true;

      } else if (updatedRows == 0) {

        // If no rows were changed, return false
        return false;
      } else {

        // Throw an exception if the number of rows changed is not 0 or 1
        // This should never happen since the query can only affect 1 row at most
        throw new SQLException("Invalid number of rows were changed");

      }

    } catch (SQLException ex) {

      LOG.error("An exception occurred while saving a conversation\n" + ex.getMessage() + "\nError Code: " + ex.getErrorCode());

    }

    return false;
  }

  // Save a user into the database
  public boolean saveUser(User user) {
    try {
      // Set the id
      userStatement.setInt(1, user.id.id());
      // Set the username
      userStatement.setString(2, user.name);
      // Set the creation time
      userStatement.setLong(3, user.creation.inMs());

      // Update the row
      int updatedRows = userStatement.executeUpdate();

      if (updatedRows == 1) {

        // If one row was changed, return true
        return true;

      } else if (updatedRows == 0) {

        // If no rows were changed, return false
        return false;
      } else {

        // Throw an exception if the number of rows changed is not 0 or 1
        // This should never happen since the query can only affect 1 row at most
        throw new SQLException("Invalid number of rows were changed");

      }

    } catch (SQLException ex) {

      LOG.error("An exception occurred while saving a user\n" + ex.getMessage() + "\nError Code: " + ex.getErrorCode());

    }

    return false;
  }
}
