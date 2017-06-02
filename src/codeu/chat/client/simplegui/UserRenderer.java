package codeu.chat.client.simplegui;

import java.awt.*;
import java.awt.Component;

import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

// without the @SuppressWarnings, the compiler will complain of no override for serialVersionUID
@SuppressWarnings("serial")

// Purpose of this class is to allow for visual modifications
// of the DefaultListModel located in the UserPanel
public class UserRenderer extends JLabel implements ListCellRenderer<Object> {
  
  /**
   * Necessary method for ListCellRenderer interface; takes in
   * list (the JList)
   * value (the String value to display to the user)
   * index (index of the cell)
   * isSelected (if the cell is selected)
   * cellHasFocus (if the cell has focus)
   */
  public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

    // makes the user name actually visible in the list
    String s = value.toString();
    setText(s);

    setFont(new Font("Lucida Grande", Font.PLAIN, 12));
    setForeground(new Color(13, 79, 109));
    setBackground(new Color(238, 238, 238));
    setOpaque(true);
    
    setBorder(new EmptyBorder(5, 5, 5, 5));

    setEnabled(list.isEnabled());
    
    // if the cell is selected, a tooltip with the message's date/time will appear to the right
    if (isSelected) {
      setForeground(Color.WHITE);
      setBackground(new Color(13, 79, 109));
    }

    return this;

  }

}
