package codeu.chat.server;

import codeu.chat.common.*;
import codeu.chat.common.Time;
import codeu.chat.util.Logger;

import java.sql.*;

public class Database {
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

        // The column specifications for the tables
        // Uuids are stored as strings
        // Times are stored as their representation in ms in Integers since SQLite has up to 64 bit integers
        String userColumns =
          "id TEXT PRIMARY KEY NOT NULL, " +
            "name TEXT NOT NULL, " +
            "creation INTEGER NOT NULL";

        String conversationColumns =
          "id TEXT PRIMARY KEY NOT NULL, " +
            "owner TEXT NOT NULL, " +
            "creation INTEGER NOT NULL, " +
            "title TEXT NOT NULL";

        String messageColumns =
          "id TEXT PRIMARY KEY NOT NULL, " +
            "creation INTEGER NOT NULL, " +
            "author TEXT NOT NULL, " +
            "conversation TEXT NOT NULL, " +
            "content TEXT NOT NULL";

        ensureTableExists("users", userColumns);
        ensureTableExists("conversations", conversationColumns);
        ensureTableExists("messages", messageColumns);

      } catch (SQLException ex) {

        LOG.error("An exception occurred while connecting to the database");
        LOG.error(ex.getMessage());

      }
    } catch (ClassNotFoundException ex) {
      LOG.error("An exception occurred while loading the JDBC driver");
      LOG.error(ex.getMessage());
    }
  }

  // This function will create a table if it does not exist
  // If the table exists, this function does nothing
  private boolean ensureTableExists(String name, String columnSpec) {
    // Build the SQL command
    String sql = "CREATE TABLE IF NOT EXISTS " + name + " (" + columnSpec + ")";

    // Execute the command
    try {

      Statement stmt = conn.createStatement();
      return stmt.execute(sql);

    } catch (SQLException ex) {

      LOG.error("An exception occurred while creating the " + name + "table");
      LOG.error(ex.getMessage());

    }

    // This should only happen if an exception occurred
    return false;
  }

  // This function loads all of the values in a database in the correct order
  public void load(Controller controller) {
    loadUsers(controller);
    loadConversations(controller);
    loadMessages(controller);
  }

  // Load all of the users from the database into the controler
  private void loadUsers(Controller controller) {

    String sql = "SELECT * FROM users";

    try {

      // Get the users
      Statement stmt = conn.createStatement();
      ResultSet users = stmt.executeQuery(sql);

      // Advance the row until there are no more
      while (users.next()) {

        // Get a Uuid from the string in the database that is registered to this server id
        Uuid id = Uuids.fromString(controller.serverId, users.getString("id"));

        // Get the username from the database
        String name = users.getString("name");

        // Get the creation time from the database
        Time creation = Time.fromMs(users.getLong("creation"));

        // Create the user with the controller
        controller.newUser(id, name, creation);
      }

    } catch (SQLException ex) {

      LOG.error("An exception occurred while loading users");
      LOG.error(ex.getMessage());

    }
  }

  private void loadConversations(Controller controller) {

    String sql = "SELECT * FROM conversations";

    try {

      // Get the users
      Statement stmt = conn.createStatement();
      ResultSet conversations = stmt.executeQuery(sql);

      // Advance the row until there are no more
      while (conversations.next()) {

        // Get a Uuid from the string in the database that is registered to this server id
        Uuid id = Uuids.fromString(controller.serverId, conversations.getString("id"));

        // Get the conversation title from the database
        String title = conversations.getString("title");

        // Get the owner's Uuid from the string in the database that is registered to this server id
        Uuid owner = Uuids.fromString(controller.serverId, conversations.getString("id"));

        // Get the creation time from the database
        Time creation = Time.fromMs(conversations.getLong("creation"));

        // Create the conversation with the controller
        controller.newConversation(id, title, owner, creation);
      }

    } catch (SQLException ex) {

      LOG.error("An exception occurred while loading conversations");
      LOG.error(ex.getMessage());

    }
  }

  private void loadMessages(Controller controller) {

    String sql = "SELECT * FROM messages";

    try {

      // Get the users
      Statement stmt = conn.createStatement();
      ResultSet messages = stmt.executeQuery(sql);

      // Advance the row until there are no more
      while (messages.next()) {

        // Get a Uuid from the string in the database that is registered to this server id
        Uuid id = Uuids.fromString(controller.serverId, messages.getString("id"));

        // Get the creation time from the database
        Time creation = Time.fromMs(messages.getLong("creation"));

        // Get the author's Uuid from the string in the database that is registered to this server id
        Uuid author = Uuids.fromString(controller.serverId, messages.getString("author"));

        // Get the conversation's Uuid from the string in the database that is registered to this server id
        Uuid conversation = Uuids.fromString(controller.serverId, messages.getString("conversation"));

        // Get the message's content from the database
        String content = messages.getString("content");

        // Create the message with the controller
        controller.newMessage(id, author, conversation, content, creation);
      }

    } catch (SQLException ex) {

      LOG.error("An exception occurred while loading messages");
      LOG.error(ex.getMessage());

    }
  }

  public void saveMessage(Message msg, Uuid conversation) {
    String sql = "INSERT INTO messages(id, creation, author, conversation, content) VALUES (?, ?, ?, ?, ?)";
    try {
      PreparedStatement stmt = conn.prepareStatement(sql);
      // Set the id
      stmt.setString(1, msg.id.toString());
      // Set the creation time
      stmt.setLong(2, msg.creation.inMs());
      // Set the author id
      stmt.setString(3, msg.author.toString());
      // Set the conversation id
      stmt.setString(4, conversation.toString());
      // Set the content string
      stmt.setString(5, msg.content);
      // Update the row
      stmt.executeUpdate();

    } catch (SQLException ex) {

      LOG.error("An exception occurred while saving a message");
      LOG.error(ex.getMessage());

    }
  }

  public void saveConversation(Conversation conversation) {
    String sql = "INSERT INTO conversations(id, owner, creation, title) VALUES (?, ?, ?, ?)";
    try {
      PreparedStatement stmt = conn.prepareStatement(sql);
      // Set the id
      stmt.setString(1, conversation.id.toString());
      // Set the owner id
      stmt.setString(2, conversation.owner.toString());
      // Set the creation time
      stmt.setLong(3, conversation.creation.inMs());
      // Set the conversation title
      stmt.setString(4, conversation.title);
      // Update the row
      stmt.executeUpdate();

    } catch (SQLException ex) {

      LOG.error("An exception occurred while saving a conversation");
      LOG.error(ex.getMessage());

    }
  }

  public void saveUser(User user) {

    String sql = "INSERT INTO users(id, name, creation) VALUES (?, ?, ?)";

    try {

      PreparedStatement stmt = conn.prepareStatement(sql);
      // Set the id
      stmt.setString(1, user.id.toString());
      // Set the username
      stmt.setString(2, user.name);
      // Set the creation time
      stmt.setLong(3, user.creation.inMs());
      // Update the row
      stmt.executeUpdate();

    } catch (SQLException ex) {

      LOG.error("An exception occurred while saving a user");
      LOG.error(ex.getMessage());

    }
  }
}
