package codeu.chat.client.simplegui;

import java.awt.*;
import java.awt.Component;

import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

@SuppressWarnings("serial")

//Purpose of this class is to allow for visual modifications
//of the DefaultListModel located in the MessagePanel
public class MessageRenderer extends JLabel implements ListCellRenderer<Object> {

  /**
   * Necessary method for ListCellRenderer interface; takes in
   * list (the JList)
   * value (the String value to display to the user)
   * index (index of the cell)
   * isSelected (if the cell is selected)
   * cellHasFocus (if the cell has focus)
   */
  public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

    String s = value.toString();
    String usernameLabel = s.substring(0, s.indexOf(":"));
    String timeLabel = s.substring(s.indexOf("[") + 1, s.indexOf("]"));
    String messageLabel = s.substring(s.indexOf("]") + 3);
    
    String finalText = usernameLabel + ": ";
    // enables text wrapping and bolds username
    finalText = String.format("<html><div WIDTH=%d><b>%s</b>%s</div><html>", 650, finalText, messageLabel);
    setText(finalText);

    setFont(new Font("Lucida Grande", Font.PLAIN, 14));
    setForeground(new Color(19, 103, 154));
    setBackground(new Color(238, 238, 238));
    setOpaque(true);
    
    setBorder(new EmptyBorder(5, 5, 5, 5));

    setEnabled(list.isEnabled());

    setToolTipText(timeLabel);
    
    // if the cell is selected, a tooltip with the message's date/time will appear to the right
    if (isSelected) {
      setForeground(Color.WHITE);
      setBackground(new Color(19, 103, 154));
    }

    return this;

  }

}
