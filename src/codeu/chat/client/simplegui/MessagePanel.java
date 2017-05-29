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

package codeu.chat.client.simplegui;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import javax.swing.*;

import codeu.chat.client.ClientContext;
import codeu.chat.common.ConversationSummary;
import codeu.chat.common.Message;
import codeu.chat.common.User;

// NOTE: JPanel is serializable, but there is no need to serialize MessagePanel
// without the @SuppressWarnings, the compiler will complain of no override for serialVersionUID
@SuppressWarnings("serial")
public final class MessagePanel extends JPanel {

  // These objects are modified by the Conversation Panel.
  private final JLabel messageOwnerLabel = new JLabel("OWNER:", JLabel.RIGHT);
  private final JLabel messageConversationLabel = new JLabel("CONVERSATION:", JLabel.LEFT);
  // messageListModel is an instance variable so Conversation panel can update it.
  private final DefaultListModel<String> messageListModel = new DefaultListModel<>();
  private final JList<String> messageList = new JList<>(messageListModel);
  private final JTextField messageField = new JTextField(60); // area for user to type in messages
  private JScrollPane userListScrollPane;
  
  private final ClientContext clientContext;

  public MessagePanel(ClientContext clientContext) {
    super(new GridBagLayout());
    this.clientContext = clientContext;
    initialize();
  }

  /**
   * External agent calls this to trigger an update of this panel's contents.
   */
  public void update(ConversationSummary owningConversation) {

    final User u = (owningConversation == null) ?
      null :
      clientContext.user.lookup(owningConversation.owner);

    messageOwnerLabel.setForeground(new Color(13, 73, 109));
    messageOwnerLabel.setText("OWNER: " +
      ((u == null) ?
        ((owningConversation == null) ? "" : owningConversation.owner) :
        u.name));

    messageConversationLabel.setForeground(new Color(13, 73, 109));
    messageConversationLabel.setText("CONVERSATION: " +
      (owningConversation == null ? "" : owningConversation.title));

    getAllMessages(owningConversation);
  }

  private void initialize() {

    // This panel contains the messages in the current conversation.
    // It has a title bar with the current conversation and owner,
    // then a list panel with the messages, then a button bar.

    // Title bar - current conversation and owner
    final JPanel titlePanel = new JPanel(new GridBagLayout());
    titlePanel.setBackground(new Color(229, 229, 229));
    final GridBagConstraints titlePanelC = new GridBagConstraints();

    final JPanel titleConvPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    titleConvPanel.setBackground(new Color(229, 229, 229));
    final GridBagConstraints titleConvPanelC = new GridBagConstraints();
    titleConvPanelC.gridx = 0;
    titleConvPanelC.gridy = 0;
    titleConvPanelC.anchor = GridBagConstraints.PAGE_START;

    final JPanel titleOwnerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    titleConvPanel.setBackground(new Color(229, 229, 229));
    final GridBagConstraints titleOwnerPanelC = new GridBagConstraints();
    titleOwnerPanelC.gridx = 0;
    titleOwnerPanelC.gridy = 1;
    titleOwnerPanelC.anchor = GridBagConstraints.PAGE_START;

    // messageConversationLabel is an instance variable of Conversation panel
    // can update it.
    messageConversationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    messageConversationLabel.setForeground(new Color(13, 73, 109));
    messageConversationLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
    titleConvPanel.add(messageConversationLabel);

    // messageOwnerLabel is an instance variable of Conversation panel
    // can update it.
    messageOwnerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    messageOwnerLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
    messageOwnerLabel.setForeground(new Color(13, 73, 109));
    titleOwnerPanel.add(messageOwnerLabel);

    titlePanel.add(titleConvPanel, titleConvPanelC);
    titlePanel.add(titleOwnerPanel, titleOwnerPanelC);
    titlePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    // User List panel.
    final JPanel listShowPanel = new JPanel();
    listShowPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
    listShowPanel.setBackground(new Color(229, 229, 229));
    final GridBagConstraints listPanelC = new GridBagConstraints();

    messageList.setOpaque(false);
    messageList.setBackground(Color.WHITE);
    messageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    messageList.setVisibleRowCount(15);
    messageList.setSelectedIndex(-1);

    userListScrollPane = new JScrollPane(messageList);
    listShowPanel.add(userListScrollPane);
    userListScrollPane.setForeground(Color.WHITE);
    userListScrollPane.setMinimumSize(new Dimension(700, 280));
    userListScrollPane.setPreferredSize(new Dimension(700, 280));

    // Button panel
    final JPanel buttonPanel = new JPanel();
    buttonPanel.setBackground(new Color(229, 229, 229));
    final GridBagConstraints buttonPanelC = new GridBagConstraints();

    messageField.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
    messageField.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
    messageField.setBackground(new Color(13, 79, 109));
    messageField.setForeground(Color.WHITE);
    buttonPanel.add(messageField);
    messageField.setEditable(true);

    // allows user to hit enter key to submit a chat message
    messageField.addKeyListener(new KeyListener() {
      public void keyPressed(KeyEvent e) {
        if (e.getKeyChar() == KeyEvent.VK_ENTER) {
          sendMessage();
        }
      }

      /* mandatory functions to include, left empty */
      public void keyTyped(KeyEvent e) {
      }

      public void keyReleased(KeyEvent e) {

      }

    });

    final JButton addButton = new JButton("send");
    addButton.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));
    addButton.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
    addButton.setBackground(new Color(13, 79, 109));
    addButton.setForeground(Color.WHITE);
    addButton.setPreferredSize(new Dimension(44, 31));
    buttonPanel.add(addButton);

    // Placement of title, list panel, buttons, and current user panel.
    titlePanelC.gridx = 0;
    titlePanelC.gridy = 0;
    titlePanelC.gridwidth = 10;
    titlePanelC.gridheight = 1;
    titlePanelC.fill = GridBagConstraints.HORIZONTAL;
    titlePanelC.anchor = GridBagConstraints.FIRST_LINE_START;

    listPanelC.gridx = 0;
    listPanelC.gridy = 1;
    listPanelC.gridwidth = 20;
    listPanelC.gridheight = 8;
    listPanelC.fill = GridBagConstraints.BOTH;
    listPanelC.anchor = GridBagConstraints.FIRST_LINE_START;
    listPanelC.weighty = 0.8;

    buttonPanelC.gridx = 0;
    buttonPanelC.gridy = 11;
    buttonPanelC.gridwidth = 10;
    buttonPanelC.gridheight = 1;
    buttonPanelC.fill = GridBagConstraints.HORIZONTAL;
    buttonPanelC.anchor = GridBagConstraints.FIRST_LINE_START;

    this.add(titlePanel, titlePanelC);
    this.add(listShowPanel, listPanelC);
    this.add(buttonPanel, buttonPanelC);

    // User click Messages Add button - prompt for message body and add new Message to Conversation
    addButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        MessagePanel.this.getNewMessages();
        sendMessage();
      }
    });

    // Panel is set up. If there is a current conversation, Populate the conversation list.
    getAllMessages();
  }
  
  /** 
   * makes sure certain conditions are met before a user can send a message
   */
  private void sendMessage() {
    if (!clientContext.user.hasCurrent()) {
      JOptionPane.showMessageDialog(MessagePanel.this, "You are not signed in.");
    } else if (!clientContext.conversation.hasCurrent()) {
      JOptionPane.showMessageDialog(MessagePanel.this, "You must select a conversation.");
    } else {
      if (messageField.getText() != null && messageField.getText().length() > 0) {
        clientContext.message.addMessage(
          clientContext.user.getCurrent().id,
          clientContext.conversation.getCurrentId(),
          messageField.getText());
        MessagePanel.this.getAllMessages();
        messageField.setText("");

        // scrolls to bottom of messages panel
        JScrollBar verticalScroll = userListScrollPane.getVerticalScrollBar();
        verticalScroll.setValue(verticalScroll.getMaximum());
      }
    }
  }

  /**
   * Populate ListModel - updates display objects.
   */
  private void getNewMessages(ConversationSummary conversation) {

    // Get new messages
    clientContext.message.updateMessages(false);

    // Get all of the messages and store them in an ArrayList
    HashSet<String> messages = new HashSet<>();
    for (final Message m : clientContext.message.getConversationContents(conversation)) {
      // Display author name if available.  Otherwise display the author UUID.
      final String authorName = clientContext.user.getName(m.author);

      // Display message in the format Author: [Date Time]: Content
      final String displayString = String.format("%s: [%s]: %s",
        ((authorName == null) ? m.author : authorName), m.creation, m.content);

      // If this message has not been displayed, display it
      if (!messageListModel.contains(displayString)) {
        messageListModel.addElement(displayString);
      }

      // Remember that this message has been displayed
      messages.add(displayString);
    }

    // Remove any messages that no longer exist
    for (int i = 0; i < messageListModel.size(); i++) {
      if (!messages.contains(messageListModel.getElementAt(i))) {
        messageListModel.removeElementAt(i);
      }
    }
  }

  /**
   * Force the messages list to reload all of the titles
   */
  private void getAllMessages(ConversationSummary conversation) {
    messageListModel.clear();
    clientContext.message.updateMessages(true);
    getNewMessages(conversation);
  }

  /**
   * Default conversation is current conversation
   */
  public void getNewMessages() {
    getNewMessages(clientContext.conversation.getCurrent());
  }

  private void getAllMessages() {
    getAllMessages(clientContext.conversation.getCurrent());
  }
}