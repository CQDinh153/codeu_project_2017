// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package codeu.chat.server;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Test;
import org.junit.Before;

import codeu.chat.common.Conversation;
import codeu.chat.common.Message;
import codeu.chat.util.Time;
import codeu.chat.common.User;
import codeu.chat.util.Uuid;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class DatabaseTest {

  private Model model;
  private Database database;
  private Controller controller;

  private Uuid userId;
  private Uuid conversationUserId;
  private Uuid messageUserId;

  private Uuid conversationId;
  private Uuid messageConversationId;

  private Uuid messageId;

  @Before
  public void doBefore() {
    model = new Model();
    database = new Database(System.getProperty("TestDatabase"));
    controller = new Controller(Uuid.NULL, model, database);

    userId = controller.buildUuid(11);
    conversationUserId = controller.buildUuid(12);
    messageUserId = controller.buildUuid(13);

    conversationId = controller.buildUuid(14);
    messageConversationId = controller.buildUuid(15);

    messageId = controller.buildUuid(16);
  }

  @After
  public void doAfter() throws SQLException{
    database.close();
  }

  @Test
  public void testSaveUser() throws SQLException {

    final User user = controller.newUser(userId, "DatabaseTestUser", Time.now());

    assertFalse(
      "Check that user has a valid reference",
      user == null);
    assertTrue(
      "Check that the user has the correct id",
      Uuid.equals(user.id, userId));

    User dbUser = null;

    // Get the user from the database by the id
    ResultSet dbUserRow = database.executeQuery("SELECT * FROM users WHERE id=" + user.id.id());

    // Advance the row until there are no more
    assertTrue(
      "Check that there is a row that matches the user id",
      dbUserRow.next()
    );

    // Get the id of the user from the database
    final Uuid userID = controller.buildUuid(dbUserRow.getInt("id"));

    // Get the username from the database
    String name = dbUserRow.getString("name");

    // Get the creation time from the database
    Time creation = Time.fromMs(dbUserRow.getLong("creation"));

    // Create a new User based on the database info
    dbUser = new User(userID, name, creation);

    assertFalse(
      "Check that a user was loaded from the database",
      dbUser == null
    );

    assertTrue(
      "Check that the user that was loaded from the database has the same id as the one that was stored",
      dbUser.id.id() == user.id.id()
    );
    assertTrue(
      "Check that the user that was loaded from the database has the same name as the one that was stored",
      dbUser.name.equals(user.name)
    );
    assertTrue(
      "Check that the user that was loaded from the database has the same creation time as the one that was stored",
      dbUser.creation.inMs() == user.creation.inMs()
    );
  }

  @Test
  public void testSaveConversation() throws SQLException {

    final User user = controller.newUser(conversationUserId, "DatabaseTestConversationUser", Time.now());

    assertFalse(
      "Check that user has a valid reference",
      user == null);
    assertTrue(
      "Check that the user has the correct id",
      Uuid.equals(user.id, conversationUserId));

    final Conversation conversation = controller.newConversation(
      conversationId,
      "DatabaseTestConversation",
      user.id,
      Time.now());

    assertFalse(
      "Check that conversation has a valid reference",
      conversation == null);
    assertTrue(
      "Check that the conversation has the correct id",
      Uuid.equals(conversation.id, conversationId));

    Conversation dbConversation = null;

    // Get the conversation from the database by the id
    ResultSet dbConversationRow = database.executeQuery("SELECT * FROM conversations WHERE id=" + conversation.id.id());

    // Advance the row until there are no more
    assertTrue(
      "Check that there is a row that matches the conversation id",
      dbConversationRow.next()
    );

    // Get the id from the database
    final Uuid conversationID = controller.buildUuid(dbConversationRow.getInt("id"));

    // Get the owner id from the database
    final Uuid ownerID = controller.buildUuid(dbConversationRow.getInt("owner"));

    // Get the creation time from the database
    Time creation = Time.fromMs(dbConversationRow.getLong("creation"));


    // Get the title from the database
    String title = dbConversationRow.getString("title");

    // Create a new Conversation based on the database info
    dbConversation = new Conversation(conversationID, ownerID, creation, title);

    assertFalse(
      "Check that a conversation was loaded from the database",
      dbConversation == null
    );
    assertTrue(
      "Check that the conversation that was loaded from the database has the same id as the one that was stored",
      dbConversation.id.id() == conversation.id.id()
    );
    assertTrue(
      "Check that the conversation that was loaded from the database has the same owner as the one that was stored",
      dbConversation.owner.id() == conversation.owner.id()
    );
    assertTrue(
      "Check that the conversation that was loaded from the database has the same creation time as the one that was stored",
      dbConversation.creation.inMs() == conversation.creation.inMs()
    );
    assertTrue(
      "Check that the conversation that was loaded from the database has the same title as the one that was stored",
      dbConversation.title.equals(conversation.title)
    );
  }

  @Test
  public void testSaveMessage() throws SQLException {

    final User user = controller.newUser(messageUserId, "DatabaseTestMessageUser", Time.now());

    assertFalse(
      "Check that user has a valid reference",
      user == null);
    assertTrue(
      "Check that the user has the correct id",
      Uuid.equals(user.id, messageUserId));

    final Conversation conversation = controller.newConversation(
      messageConversationId,
      "DatabaseTestMessageConversation",
      user.id,
      Time.now());

    assertFalse(
      "Check that conversation has a valid reference",
      conversation == null);
    assertTrue(
      "Check that the conversation has the correct id",
      Uuid.equals(conversation.id, messageConversationId));

    final Message message = controller.newMessage(
      messageId,
      user.id,
      conversation.id,
      "Database Test Message",
      Time.now());

    assertFalse(
      "Check that the message has a valid reference",
      message == null);
    assertTrue(
      "Check that the message has the correct id",
      Uuid.equals(message.id, messageId));

    Message dbMessage = null;
    Conversation foundConv = null;
    User foundUser = null;

    // Get the conversation from the database by the id
    ResultSet dbMessageRow = database.executeQuery("SELECT * FROM messages WHERE id=" + message.id.id());

    // Advance the row until there are no more
    assertTrue(
      "Check that there is a row that matches the conversation id",
      dbMessageRow.next()
    );

    // Get the id from the database
    final Uuid messageID = controller.buildUuid(dbMessageRow.getInt("id"));

    // Get the creation time from the database
    Time creation = Time.fromMs(dbMessageRow.getLong("creation"));

    // Get the author id from the database
    final Uuid authorID = controller.buildUuid(dbMessageRow.getInt("author"));

    // Get the conversation id from the database
    final Uuid conversationID = controller.buildUuid(dbMessageRow.getInt("conversation"));

    // Get the content from the database
    String content = dbMessageRow.getString("content");

    // Find the author and conversation in the model based on their ids
    foundConv = model.conversationById().first(conversationID);
    foundUser = model.userById().first(authorID);

    assertFalse(
      "Check that there is a conversation corresponding to the id in the database",
      foundConv == null
    );

    assertFalse(
      "Check that there is a user corresponding to the id in the database",
      foundUser == null
    );

    // Create a new Conversation based on the database info
    dbMessage = new Message(messageID, Uuid.NULL, Uuid.NULL, creation, foundUser.id, content);
    if (!Uuid.equals(foundConv.lastMessage, Uuid.NULL)) {
      model.messageById().first(foundConv.lastMessage).next = messageID;
    }
    if (Uuid.equals(foundConv.firstMessage, Uuid.NULL)) {
      foundConv.firstMessage = messageID;
    }

    foundConv.lastMessage = messageID;

    assertFalse(
      "Check that a message was loaded from the database",
      dbMessage == null
    );
    assertTrue(
      "Check that the message that was loaded from the database has the same id as the one that was stored",
      dbMessage.id.id() == message.id.id()
    );
    assertTrue(
      "Check that the message that was loaded from the database has the same author as the one that was stored",
      dbMessage.author.id() == message.author.id()
    );
    assertTrue(
      "Check that the message that was loaded from the database has the same creation time as the one that was stored",
      dbMessage.creation.inMs() == message.creation.inMs()
    );
    assertTrue(
      "Check that the message that was loaded from the database has the same content as the one that was stored",
      dbMessage.content.equals(message.content)
    );
    assertTrue(
      "Check that the id in the database corresponds to the id of the conversation that contains the original message",
      foundConv.id.id() == conversation.id.id()
    );

  }
}
