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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.Border;

import codeu.chat.client.ClientContext;
import codeu.chat.client.Controller;
import codeu.chat.client.View;
import codeu.chat.util.Logger;

// Chat - top-level client application - Java Simple GUI (using Java Swing)
public final class ChatSimpleGui {

  private final long POLLING_PERIOD_MS = 1000;
  private final long POLLING_DELAY_MS = 0;

  private final static Logger.Log LOG = Logger.newLog(ChatSimpleGui.class);

  private JFrame mainFrame;

  private final ClientContext clientContext;

  /**
   * Constructor - sets up the Chat Application
   */
  public ChatSimpleGui(Controller controller, View view) {
    clientContext = new ClientContext(controller, view);
  }

  /**
   * Run the GUI client
   */
  public void run() {

    try {

      initialize();
      mainFrame.setVisible(true);

    } catch (Exception ex) {
      System.out.println("ERROR: Exception in ChatSimpleGui.run. Check log for details.");
      LOG.error(ex, "Exception in ChatSimpleGui.run");
      System.exit(1);
    }
  }

  /**
   * Initialize the GUI
   */
  private void initialize() {

	/* modifies look and feel of GUI */
    try {
      UIManager.LookAndFeelInfo[] laf = UIManager.getInstalledLookAndFeels();

      UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
    } catch (Exception e) {
      System.out.println("Problems editing the look and feel");
      System.exit(-1);
    }

    // Outermost frame.
    // NOTE: may have tweak size, or place in scrollable panel.
    mainFrame = new JFrame("Chat");
    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainFrame.setSize(850, 600);
    mainFrame.setLocation(300, 300);

	/* Adds a menu bar with exit and sign-in options */
    JMenuBar menuBar = new JMenuBar();
    Font menuFont = new Font("Lucida Grande", Font.PLAIN, 12);
    UIManager.put("Menu.font", menuFont);
    
    JMenu userMenu = new JMenu("Options");
    JMenuItem jmiSwitchUser = new JMenuItem("Manage Users");
    userMenu.add(jmiSwitchUser);
    JMenuItem jmiExit = new JMenuItem("Exit");
    userMenu.add(jmiExit);

    menuBar.add(userMenu);
    mainFrame.setJMenuBar(menuBar);

	/* Creates "manage users" window in advance to maintain current user sign-in */
    JFrame popUpFrame = new JFrame("Manage Users");
    popUpFrame.setSize(420, 400);
    popUpFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    popUpFrame.setLocation(360, 360);

    // Build main panels - Users, Conversations, Messages.
    final UserPanel usersViewPanel = new UserPanel(clientContext);

    popUpFrame.getContentPane().add(usersViewPanel);

	/* if "manage users" option is clicked, opens up sign-in window */
    jmiSwitchUser.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Manage Users")) {
          popUpFrame.setVisible(true);
        }
      }
    });
		
	/* allows user to close out of chat app */
    jmiExit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Exit"))
          System.exit(0);
      }
    });

    // Main View - outermost graphics panel.
    final JPanel mainViewPanel = new JPanel(new GridBagLayout());
    mainViewPanel.setBackground(new Color(85,83,112));

    final MessagePanel messagesViewPanel = new MessagePanel(clientContext);
    messagesViewPanel.setOpaque(false);
    final GridBagConstraints messagesViewC = new GridBagConstraints();

    // ConversationsPanel gets access to MessagesPanel
    final ConversationPanel conversationsViewPanel = new ConversationPanel(clientContext, messagesViewPanel);
    conversationsViewPanel.setOpaque(false);
    final GridBagConstraints conversationViewC = new GridBagConstraints();

    // Placement of main panels.
    conversationViewC.gridx = 0;
    conversationViewC.gridy = 0;
    conversationViewC.gridwidth = 1;
    conversationViewC.gridheight = 1;
    conversationViewC.fill = GridBagConstraints.BOTH;
    conversationViewC.weightx = 0.4;
    conversationViewC.weighty = 0.5;

    /* sets fixed size for the conversations panel */
    conversationsViewPanel.setPreferredSize(new Dimension(conversationsViewPanel.getPreferredSize().width, 300));
    conversationsViewPanel.setPreferredSize(new Dimension(conversationsViewPanel.getPreferredSize().height, 400));
    
    messagesViewC.gridx = 1;
    messagesViewC.gridy = 0;
    messagesViewC.gridwidth = 1;
    messagesViewC.gridheight = 1;
    messagesViewC.fill = GridBagConstraints.BOTH;
    messagesViewC.weightx = 0.6;
    messagesViewC.weighty = 0.5;

    mainViewPanel.add(conversationsViewPanel, conversationViewC);
    mainViewPanel.add(messagesViewPanel, messagesViewC);

    mainFrame.add(mainViewPanel);
    mainFrame.pack();

    // Poll the server for updates
    java.util.Timer pollingTimer = new java.util.Timer();
    pollingTimer.schedule(new TimerTask() {
      @Override
      public void run() {
        messagesViewPanel.getNewMessages();
        usersViewPanel.getNewUsers();
        conversationsViewPanel.getNewConversations();
      }
    }, POLLING_DELAY_MS, POLLING_PERIOD_MS);
  }

}
