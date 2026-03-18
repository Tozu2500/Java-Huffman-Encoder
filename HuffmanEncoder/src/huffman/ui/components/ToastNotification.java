package huffman.ui.components;

import huffman.ui.theme.AppTheme;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
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
        panel.add(lbl, BorderLayout.CENTER);
        setContentPane(panel);
        pack();

        // Position bottom-right of owner
        if (owner != null) {
            Rectangle ob = owner.getBounds();
            setLocation(ob.x + ob.width - getWidth() - 20, ob.y + ob.height - getHeight() - 40);
        }

        // Fade in effect
        showTimer = new Timer(16, e -> {
            alpha = Math.min(1f, alpha + 0.08f);
            repaint();
            if (alpha >= 1f) {
                ((Timer) e.getSource()).stop();
                scheduleHide();
            }
        });
        setVisible(true);
        showTimer.start();
    }

    private void scheduleHide() {
        Timer delay = new Timer(2500, e -> {
            ((Timer) e.getSource()).stop();
            hideTimer = new Timer(16, ev -> {
                alpha = Math.max(0f, alpha - 0.05f);
                repaint();
                if (alpha <= 0f) {
                    ((Timer) ev.getSource()).stop();
                    dispose();
                }
            });
        });

        delay.setRepeats(false);
        delay.start();
    }

    public static void show(JFrame owner, String msg, Type type) {
        SwingUtilities.invokeLater(() -> new ToastNotification(owner, msg, type));
    }
}
