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

import java.util.Collection;
import java.sql.ResultSet;
import java.sql.SQLException;

import codeu.chat.common.BasicController;
import codeu.chat.common.Conversation;
import codeu.chat.common.Message;
import codeu.chat.common.RandomUuidGenerator;
import codeu.chat.common.RawController;
import codeu.chat.common.User;
import codeu.chat.util.Logger;
import codeu.chat.util.Time;
import codeu.chat.util.Uuid;


public final class Controller implements RawController, BasicController {
  private final static Logger.Log LOG = Logger.newLog(Controller.class);

  private final Model model;
  private final Database database;

  private final Uuid.Generator uuidGenerator;
  public final Uuid serverId;

  public Controller(Uuid serverId, Model model, Database database) {
    this.model = model;
    this.serverId = serverId;
    this.uuidGenerator = new RandomUuidGenerator(serverId, System.currentTimeMillis());
    this.database = database;
  }

  @Override
  public Message newMessage(Uuid author, Uuid conversation, String body) {
    return newMessage(createId(), author, conversation, body, Time.now());
  }

  @Override
  public User newUser(String name) {
    return newUser(createId(), name, Time.now());
  }

  @Override
  public Conversation newConversation(String title, Uuid owner) {
    return newConversation(createId(), title, owner, Time.now());
  }

  @Override
  public Message newMessage(Uuid id, Uuid author, Uuid conversation, String body, Time creationTime) {

    final User foundUser = model.userById().first(author);
    final Conversation foundConversation = model.conversationById().first(conversation);

    Message message = null;

    if (foundUser != null && foundConversation != null && isIdFree(id)) {

      message = new Message(id, Uuid.NULL, Uuid.NULL, creationTime, foundUser.id, body);

      // If saving the message succeeds, add it to the model
      if (database.saveMessage(message, foundConversation.id)) {
        model.add(message);
        LOG.info("Message added: %s", message.id);

        // Find and update the previous "last" message so that it's "next" value
        // will point to the new message.

        if (Uuid.equals(foundConversation.lastMessage, Uuid.NULL)) {

          // The conversation has no messages in it, that's why the last message is NULL (the first
          // message should be NULL too. Since there is no last message, then it is not possible
          // to update the last message's "next" value.

        } else {
          final Message lastMessage = model.messageById().first(foundConversation.lastMessage);
          lastMessage.next = message.id;
        }

        // If the first message points to NULL it means that the conversation was empty and that
        // the first message should be set to the new message. Otherwise the message should
        // not change.

        foundConversation.firstMessage =
          Uuid.equals(foundConversation.firstMessage, Uuid.NULL) ?
            message.id :
            foundConversation.firstMessage;

        // Update the conversation to point to the new last message as it has changed.

        foundConversation.lastMessage = message.id;

        if (!foundConversation.users.contains(foundUser)) {
          foundConversation.users.add(foundUser.id);
        }
      } else {
        message = null;
        LOG.info(
          "newMessage fail - save to database failed (message.id=%s message.author=%s message.conversation=%s message.creation=%s message.body=%s)",
          id,
          author,
          conversation,
          creationTime,
          body);
      }
    }

    return message;
  }

  @Override
  public User newUser(Uuid id, String name, Time creationTime) {

    User user = null;

    if (isIdFree(id)) {

      user = new User(id, name, creationTime);
      // If saving the user succeeds, add it to the model
      if (database.saveUser(user)) {
        model.add(user);

        LOG.info(
          "newUser success (user.id=%s user.name=%s user.creation=%s)",
          id,
          name,
          creationTime);
      } else {
        LOG.info(
          "newUser fail - save to database failed (user.id=%s user.name=%s user.time=%s)",
          id,
          name,
          creationTime);
      }

    } else {
      user = null;
      LOG.info(
        "newUser fail - id in use (user.id=%s user.name=%s user.time=%s)",
        id,
        name,
        creationTime);
    }

    return user;
  }

  @Override
  public Conversation newConversation(Uuid id, String title, Uuid owner, Time creationTime) {

    final User foundOwner = model.userById().first(owner);

    Conversation conversation = null;

    if (foundOwner != null && isIdFree(id)) {
      conversation = new Conversation(id, foundOwner.id, creationTime, title);

      // If saving the conversation succeeds, add it to the model
      if (database.saveConversation(conversation)) {
        model.add(conversation);

        LOG.info("Conversation added: " + conversation.id);
      } else {
        conversation = null;
        LOG.info(
          "newConversation fail - save to database failed (conversation.id=%s conversation.title=%s conversation.owner=%s conversation.time=%s)",
          id,
          title,
          owner,
          creationTime);
      }
    }

    return conversation;
  }

  public Uuid buildUuid(int id){
    return new Uuid(serverId, id);
  }

  // This function loads all of the values in a database in the correct order
  public void loadFromDatabase() {
    // Load all of the users from the database
    try {

      // Get the users
      ResultSet users = database.executeQuery("SELECT * FROM users");

      // Advance the row until there are no more
      while (users.next()) {

        // Get a Uuid from the string in the database that is registered to this server id
        final Uuid userID = buildUuid(users.getInt("id"));

        // Get the username from the database
        String name = users.getString("name");

        // Get the creation time from the database
        Time creation = Time.fromMs(users.getLong("creation"));

        if (isIdFree(userID)) {

          // Create the user and add it to the model
          model.add(new User(userID, name, creation));
          LOG.info("Loaded User: \nid: %s\nname: %s\ncreation: %s\n", userID, name, creation);

        }
      }
    } catch (SQLException ex) {

      LOG.error("An exception occurred while loading users");
      LOG.error(ex.getMessage());

    }

    // Load all of the conversations from the database
    try {
      // Get the users
      ResultSet conversations = database.executeQuery("SELECT * FROM conversations");

      // Advance the row until there are no more
      while (conversations.next()) {

        // Get a Uuid from the string in the database that is registered to this server id
        final Uuid conversationID = buildUuid(conversations.getInt("id"));

        // Get the conversation title from the database
        String title = conversations.getString("title");

        // Get the owner's Uuid from the string in the database that is registered to this server id
        final Uuid ownerID = buildUuid(conversations.getInt("owner"));

        // Get the User object that corresponds to this id from the model
        final User foundOwner = model.userById().first(ownerID);

        // Get the creation time from the database
        Time creation = Time.fromMs(conversations.getLong("creation"));

        if (foundOwner != null && isIdFree(conversationID)) {

          // Create the conversation and add it to the model
          model.add(new Conversation(conversationID, foundOwner.id, creation, title));
          LOG.info("Loaded Conversation: \nid: %s\ntitle: %s\nowner: %s\ncreation: %s\n", conversationID, title, foundOwner.name, creation);

        }
      }
    } catch (SQLException ex) {

      LOG.error("An exception occurred while loading conversations");
      LOG.error(ex.getMessage());

    }

    // Load all of the messages from the database
    try {

      // Get the users
      ResultSet messages = database.executeQuery("SELECT * FROM messages ORDER BY creation ASC");

      // Advance the row until there are no more
      while (messages.next()) {

        // Get a Uuid from the string in the database that is registered to this server id
        Uuid messageID = buildUuid(messages.getInt("id"));

        // Get the creation time from the database
        Time creation = Time.fromMs(messages.getLong("creation"));

        // Get the author's Uuid from the string in the database that is registered to this server id
        Uuid authorID = buildUuid(messages.getInt("author"));

        // Get the User object that corresponds to this id from the model
        User foundUser = model.userById().first(authorID);

        // Get the conversation's Uuid from the string in the database that is registered to this server id
        Uuid conversation = buildUuid(messages.getInt("conversation"));

        // Get the Conversation object that corresponds to this id from the model
        Conversation foundConversation = model.conversationById().first(conversation);

        // Get the message's content from the database
        String content = messages.getString("content");

        // If the message has a valid user and conversation, add it to the model
        if (foundUser != null && foundConversation != null && isIdFree(messageID)) {

          model.add(new Message(messageID, Uuid.NULL, Uuid.NULL, creation, foundUser.id, content));

          // Find and update the previous "last" message so that it's "next" value
          // will point to the new message.

          if (Uuid.equals(foundConversation.lastMessage, Uuid.NULL)) {

            // The conversation has no messages in it, that's why the last message is NULL (the first
            // message should be NULL too. Since there is no last message, then it is not possible
            // to update the last message's "next" value.

          } else {

            final Message lastMessage = model.messageById().first(foundConversation.lastMessage);
            lastMessage.next = messageID;

          }

          // If the first message points to NULL it means that the conversation was empty and that
          // the first message should be set to the new message. Otherwise the message should
          // not change.
          if (Uuid.equals(foundConversation.firstMessage, Uuid.NULL)) {
            foundConversation.firstMessage = messageID;
          }

          // Update the conversation to point to the new last message as it has changed.

          foundConversation.lastMessage = messageID;

          if (!foundConversation.users.contains(foundUser)) {
            foundConversation.users.add(foundUser.id);
          }

          LOG.info("Message loaded: \nid: %s\nauthor: %s\nconversation: %s\ncontent: %s\ncreation: %s\n", messageID, foundUser.name, foundConversation.title, content, creation);

        }
      }
    } catch (SQLException ex) {

      LOG.error("An exception occurred while loading messages");
      LOG.error(ex.getMessage());

    }

    // Print out the loaded users
    String usersLog = "Users:\n";
    for(User u:model.userById().all()){
      usersLog += u.name + "\n";
    }

    // Print out the loaded conversations with their messages
    String conversationsLog = "Conversations: \n";
    for(Conversation c:model.conversationById().all()){
      conversationsLog += c.title + ":\n";
      Message message = model.messageById().first(c.firstMessage);
      while (message != null) {
        conversationsLog += message.content + "\n";
        message = model.messageById().first(message.next);
      }
      conversationsLog += "\n";
    }

    // Print out the loaded messages
    String messagesLog = "Messages: \n";
    for(Message m:model.messageById().all()){
      messagesLog += m.content + "\n";
    }
    LOG.info("Loaded:\n%s\n%s\n%s", usersLog, conversationsLog, messagesLog);
  }

  private Uuid createId() {

    Uuid candidate;

    for (candidate = uuidGenerator.make();
         isIdInUse(candidate);
         candidate = uuidGenerator.make()) {

      // Assuming that "randomUuid" is actually well implemented, this
      // loop should never be needed, but just incase make sure that the
      // Uuid is not actually in use before returning it.

    }

    return candidate;
  }

  private boolean isIdInUse(Uuid id) {
    return model.messageById().first(id) != null ||
      model.conversationById().first(id) != null ||
      model.userById().first(id) != null;
  }

  private boolean isIdFree(Uuid id) {
    return !isIdInUse(id);
  }

}
