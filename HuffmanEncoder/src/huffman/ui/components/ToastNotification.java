package huffman.ui.components;

import huffman.ui.theme.AppTheme;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.Timer;

public class ToastNotification extends JWindow {

    public enum Type {
        SUCCESS,
        WARNING,
        ERROR,
        INFO,
    };

    private float alpha = 0f;
    private Timer showTimer;
    private Timer hideTimer;

    public ToastNotification(JFrame owner, String message, Type type) {
        super(owner);
        setBackground(new Color(0, 0, 0, 0));
        Color accent = switch(type) {
            case SUCCESS -> AppTheme.SUCCESS;
            case WARNING -> AppTheme.WARNING;
            case ERROR -> AppTheme.ERROR;
            case INFO -> AppTheme.ACCENT;
        };

        JPanel panel = new JPanel(new BorderLayout(8, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g2.setColor(AppTheme.BG_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(accent);
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 12, 12));
                g2.fillRoundRect(0, getHeight() / 4, 3, getHeight() / 2, 3, 3);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            public boolean isOpaque() {
                return false;
            }
        };

        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        JLabel lbl = new JLabel(message);
        lbl.setForeground(AppTheme.TEXT_PRIMARY);
        lbl.setFont(AppTheme.FONT_UI.deriveFont(13f));
        
    }
}
