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
import codeu.chat.common.RawController;
import codeu.chat.common.User;
import codeu.chat.util.Time;
import codeu.chat.util.Uuid;

import java.sql.SQLException;

public final class RawControllerTest {

  private Model model;
  private Database database;
  private RawController controller;

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

    userId = new Uuid(1);
    conversationUserId = new Uuid(2);
    messageUserId = new Uuid(3);

    conversationId = new Uuid(4);
    messageConversationId = new Uuid(5);

    messageId = new Uuid(6);
  }

  @After
  public void doAfter() throws SQLException{
    database.close();
  }

  @Test
  public void testAddUser() {

    final User user = controller.newUser(userId, "RawTestUser", Time.now());

    assertFalse(
        "Check that user has a valid reference",
        user == null);
    assertTrue(
        "Check that the user has the correct id",
        Uuid.equals(user.id, userId));
  }

  @Test
  public void testAddConversation() {

    final User user = controller.newUser(conversationUserId, "RawTestConversationUser", Time.now());

    assertFalse(
        "Check that user has a valid reference",
        user == null);
    assertTrue(
        "Check that the user has the correct id",
        Uuid.equals(user.id, conversationUserId));

    final Conversation conversation = controller.newConversation(
        conversationId,
        "RawTestConversation",
        user.id,
        Time.now());

    assertFalse(
        "Check that conversation has a valid reference",
        conversation == null);
    assertTrue(
        "Check that the conversation has the correct id",
        Uuid.equals(conversation.id, conversationId));
  }

  @Test
  public void testAddMessage() {

    final User user = controller.newUser(messageUserId, "RawTestMessageUser", Time.now());

    assertFalse(
        "Check that user has a valid reference",
        user == null);
    assertTrue(
        "Check that the user has the correct id",
        Uuid.equals(user.id, messageUserId));

    final Conversation conversation = controller.newConversation(
        messageConversationId,
        "RawTestMessageConversation",
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
        "Raw Test Message",
        Time.now());

    assertFalse(
        "Check that the message has a valid reference",
        message == null);
    assertTrue(
        "Check that the message has the correct id",
        Uuid.equals(message.id, messageId));
  }
}
