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

import codeu.chat.common.*;
import org.junit.After;
import org.junit.Test;
import org.junit.Before;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class BasicControllerTest {

  private Model model;
  private Database database;
  private BasicController controller;

  @Before
  public void doBefore() {
    model = new Model();
    database = new Database(System.getProperty("TestDatabase"));
    controller = new Controller(Uuids.NULL, model, database);
  }

  @After
  public void doAfter() throws SQLException{
    database.close();
  }

  @Test
  public void testAddUser() {

    final User user = controller.newUser("BasicTestUser");

    assertFalse(
        "Check that user has a valid reference",
        user == null);

  }

  @Test
  public void testAddConversation() {

    final User user = controller.newUser("BasicTestConversationUser");

    assertFalse(
        "Check that user has a valid reference",
        user == null);

    final Conversation conversation = controller.newConversation(
        "BasicTestConversation",
        user.id);

    assertFalse(
        "Check that conversation has a valid reference",
        conversation == null);
  }

  @Test
  public void testAddMessage() {

    final User user = controller.newUser("BasicTestMessageUser");

    assertFalse(
        "Check that user has a valid reference",
        user == null);

    final Conversation conversation = controller.newConversation(
        "BasicTestMessageConversation",
        user.id);

    assertFalse(
        "Check that conversation has a valid reference",
        conversation == null);

    final Message message = controller.newMessage(
        user.id,
        conversation.id,
        "Basic Test Message");

    assertFalse(
        "Check that the message has a valid reference",
        message == null);
  }
}
