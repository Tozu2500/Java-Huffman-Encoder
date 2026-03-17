package huffman;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import huffman.ui.theme.AppTheme;

public class Main {

    public static void main(String[] args) {
        // Enable system-wide anti-aliasing
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        System.setProperty("sun.java2d.opengl", "true");

        SwingUtilities.invokeLater(() -> {
            try {
                // Use system look and feel as a base and then override
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {}
            AppTheme.applyGlobalDefaults();
            MainWindow win = new MainWindow();
            win.setVisible(true);
        });
    }

}
