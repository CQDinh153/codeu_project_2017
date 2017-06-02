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
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.event.ListSelectionListener;

import codeu.chat.client.ClientContext;
import codeu.chat.common.User;

// NOTE: JPanel is serializable, but there is no need to serialize UserPanel
// without the @SuppressWarnings, the compiler will complain of no override for serialVersionUID
@SuppressWarnings("serial")
public final class UserPanel extends JPanel {

  private final ClientContext clientContext;
  private final DefaultListModel<String> userListModel = new DefaultListModel<>();
  private final JList<String> userList = new JList<>(userListModel);

  public UserPanel(ClientContext clientContext) {
    super(new GridBagLayout());
    this.clientContext = clientContext;
    initialize();
  }

  private void initialize() {

    // This panel contains from top to bottom; a title bar, a list of users,
    // information about the current (selected) user, and a button bar.

    // Title bar - includes name of currently signed-in user.
    final JPanel titlePanel = new JPanel(new GridBagLayout());
    final GridBagConstraints titlePanelC = new GridBagConstraints();

    final JLabel titleLabel = new JLabel("not signed in", JLabel.CENTER);
    titleLabel.setFont(new Font("Lucida Grande", Font.BOLD, 12));
    titleLabel.setForeground(new Color(13, 79, 109));
    final GridBagConstraints titleLabelC = new GridBagConstraints();
    titleLabelC.gridx = 0;
    titleLabelC.gridy = 0;
    titleLabelC.anchor = GridBagConstraints.PAGE_START;

    titlePanel.add(titleLabel, titleLabelC);
    titlePanel.add(titleLabel);
    titlePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    // User List panel.
    final JPanel listShowPanel = new JPanel();
    final GridBagConstraints listPanelC = new GridBagConstraints();

    userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    userList.setVisibleRowCount(10);
    userList.setSelectedIndex(-1);
    userList.setCellRenderer(new UserRenderer());
    userList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    final JScrollPane userListScrollPane = new JScrollPane(userList);
    listShowPanel.add(userListScrollPane);
    userListScrollPane.setPreferredSize(new Dimension(280, 150));

    // scrollbar visual components
    userListScrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI()
    {
      protected JButton createDecreaseButton(int orientation) {
        JButton button = super.createDecreaseButton(orientation);
        modifyButton(button);
        return button;
      }

      protected JButton createIncreaseButton(int orientation) {
        JButton button = super.createIncreaseButton(orientation);
        modifyButton(button);
        return button;
      }

      // helper method to change increment & decrement buttons to a default style
      private JButton modifyButton(JButton button) {
        button.setBackground(new Color(188, 32, 49, 0));
        button.setForeground(new Color(188, 32, 49, 0));
        button.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        return button;
      }

      protected void configureScrollBarColors() {
        trackColor = new Color(255, 255, 255, 0);
        thumbColor = new Color(13, 79, 109);
      }
    });

    // Current User panel
    final JPanel currentPanel = new JPanel();
    final GridBagConstraints currentPanelC = new GridBagConstraints();

    final JTextArea userInfoPanel = new JTextArea();
    userInfoPanel.setEditable(false);

    final JScrollPane userInfoScrollPane = new JScrollPane(userInfoPanel);
    currentPanel.add(userInfoScrollPane);
    userInfoScrollPane.setPreferredSize(new Dimension(280, 85));

    // Button bar
    final JPanel buttonPanel = new JPanel();
    final GridBagConstraints buttonPanelC = new GridBagConstraints();

    JButton userUpdateButton = new JButton("update");
    userUpdateButton = changeButton(userUpdateButton);
    
    JButton userSignInButton = new JButton("sign in");
    userSignInButton = changeButton(userSignInButton);

    JButton userAddButton = new JButton("add");
    userAddButton = changeButton(userAddButton);
    
    buttonPanel.add(userUpdateButton);
    buttonPanel.add(userSignInButton);
    buttonPanel.add(userAddButton);

    // Placement of title, list panel, buttons, and current user panel.
    titlePanelC.gridx = 0;
    titlePanelC.gridy = 0;
    titlePanelC.gridwidth = 10;
    titlePanelC.gridheight = 1;
    titlePanelC.fill = GridBagConstraints.HORIZONTAL;
    titlePanelC.anchor = GridBagConstraints.FIRST_LINE_START;

    listPanelC.gridx = 0;
    listPanelC.gridy = 1;
    listPanelC.gridwidth = 10;
    listPanelC.gridheight = 8;
    listPanelC.fill = GridBagConstraints.BOTH;
    listPanelC.anchor = GridBagConstraints.FIRST_LINE_START;
    listPanelC.weighty = 0.8;

    currentPanelC.gridx = 0;
    currentPanelC.gridy = 9;
    currentPanelC.gridwidth = 10;
    currentPanelC.gridheight = 3;
    currentPanelC.fill = GridBagConstraints.HORIZONTAL;
    currentPanelC.anchor = GridBagConstraints.FIRST_LINE_START;

    buttonPanelC.gridx = 0;
    buttonPanelC.gridy = 12;
    buttonPanelC.gridwidth = 10;
    buttonPanelC.gridheight = 1;
    buttonPanelC.fill = GridBagConstraints.HORIZONTAL;
    buttonPanelC.anchor = GridBagConstraints.FIRST_LINE_START;

    this.add(titlePanel, titlePanelC);
    this.add(listShowPanel, listPanelC);
    this.add(buttonPanel, buttonPanelC);
    this.add(currentPanel, currentPanelC);

    userUpdateButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        final String selected = userList.getSelectedValue();
        UserPanel.this.getAllUsers();
        userList.setSelectedValue(selected, false);
      }
    });

    userSignInButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (userList.getSelectedIndex() != -1) {
          final String data = userList.getSelectedValue();
          clientContext.user.signInUser(data);
          titleLabel.setText("Hello " + data);
        }
      }
    });

    userAddButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        UserPanel.this.getNewUsers();
        final String s = (String) JOptionPane.showInputDialog(
            UserPanel.this, "Enter user name:", "Add User", JOptionPane.PLAIN_MESSAGE,
            null, null, "");
        if (s != null && s.length() > 0) {
          UserPanel.this.getAllUsers();
          Pattern pattern = Pattern.compile("[^a-zA-Z0-9._-]");
          boolean isNonAlphaNumeric = pattern.matcher(s).find();

          // No duplicate names are allowed
          if (UserPanel.this.userListModel.contains(s)){
            JOptionPane.showMessageDialog(UserPanel.this, "User already exists");
          }

          // sets a username length limit
          else if(s.length() < 5 || s.length() > 40) {
            JOptionPane.showMessageDialog(UserPanel.this, "Username must be between 5 to 40 characters long");
          }

          // if special characters other than A-Z, 0-9, periods, or underscores are detected
          else if (isNonAlphaNumeric){
            JOptionPane.showMessageDialog(UserPanel.this, "Username must not contain characters other than letters, numbers, underscores, dashes, or periods");
          }

          else {
            clientContext.user.addUser(s);
            UserPanel.this.getNewUsers();
          }
          userList.setSelectedValue(s, true);
        }
      }
    });

    userList.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        if (userList.getSelectedIndex() != -1) {
          final String data = userList.getSelectedValue();
          userInfoPanel.setText(clientContext.user.showUserInfo(data));
        }
      }
    });

    getAllUsers();
  }
  
  // changes the look of a button to match the UI color scheme
  private JButton changeButton(JButton button) {
    
    button.setBackground(new Color(13, 79, 109));
    button.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
    button.setForeground(Color.WHITE);
    button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    
    return button;
    
  }

  // Populate ListModel - updates display objects.
  public void getNewUsers() {

    // Get all of the users
    clientContext.user.updateUsers();

    // Store all of the names that are received in a HashSet
    HashSet<String> names = new HashSet<>();
    for (final User u : clientContext.user.getUsers()) {
      // If the user's name has not been displayed, display it
      if (!userListModel.contains(u.name)){
        userListModel.addElement(u.name);
      }

      // Remember that the name has been displayed
      names.add(u.name);
    }

    // Remove any titles that no longer exist
    for (int i = 0; i < userListModel.size(); i++) {
      if (!names.contains(userListModel.getElementAt(i))){
        userListModel.removeElementAt(i);
      }
    }
  }

  // Force the conversations list to reload all of the titles
  private void getAllUsers() {
    userListModel.clear();
    getNewUsers();
  }
}
