package codeu.chat.client.simplegui;

import java.awt.*;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

// without the @SuppressWarnings, the compiler will complain of no override for serialVersionUID
@SuppressWarnings("serial")

public class ConversationRenderer extends JLabel implements ListCellRenderer<Object> {

  /**
   * Necessary method for ListCellRenderer interface; takes in
   * list (the JList)
   * value (the String value to display to the user)
   * index (index of the cell)
   * isSelected (if the cell is selected)
   * cellHasFocus (if the cell has focus)
   */
  public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
    
    // makes the conversation name actually visible in the list
    String s = value.toString();
    setText(s);
    
    setFont(new Font("Lucida Grande", Font.PLAIN, 14));
    setForeground(new Color(188, 32, 49));
    setBackground(new Color(238, 238, 238));
    setOpaque(true);
    
    setEnabled(list.isEnabled());
    
    if (isSelected) {
      setBackground(Color.WHITE);
      
    }
    
    if (cellHasFocus) {
      
      
    }
    
    return this;

  }

}
