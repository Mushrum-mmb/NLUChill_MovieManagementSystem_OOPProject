

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import View.MainFrame;

public class Main {
public static void main(String[] args) {
	  System.setProperty("awt.useSystemAAFontSettings", "on");
      System.setProperty("swing.aatext", "true");

      SwingUtilities.invokeLater(() -> {
          try {
              UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
          } catch (Exception ignored) {}
          MainFrame frame = new MainFrame();
          frame.setVisible(true);
      });
  }
}

