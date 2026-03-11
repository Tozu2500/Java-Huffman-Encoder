package huffman.ui.components;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.Timer;

import huffman.ui.theme.AppTheme;

public class DarkButton extends JButton {

    public enum Style {
        PRIMARY,
        SECONDARY,
        GHOST,
        DANGER,
    };

    private float hoverAlpha = 0f;
    private float pressAlpha = 0f;
    private Timer hoverTimer;
    private boolean hovering = false;
    private final Style style;

    public DarkButton(String text) {
        this(text, Style.PRIMARY);
    }

    public DarkButton(String text, Style style) {
        super(text);
        this.style = style;
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(AppTheme.FONT_TITLE.deriveFont(13f));
        setForeground(fgColor());
        setPadding();

        hoverTimer = new Timer(12, e-> {
            float target = hovering ? 1f : 0f;
            hoverAlpha += (target - hoverAlpha) * 0.18f;
            if (Math.abs(hoverAlpha - target) < 0.01f) {
                hoverAlpha = target;
                ((Timer) e.getSource()).stop();
            }
            repaint();
        });

        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                hovering = true;
                hoverTimer.start();
            }

            public void mouseExited(MouseEvent e) {
                hovering = false;
                hoverTimer.start();
            }

            public void mousePressed(MouseEvent e) {
                pressAlpha = 1f;
                repaint();
            }

            public void mouseReleased(MouseEvent e) {
                pressAlpha = 0f;
                repaint();
            }
        });
    }

    private Color fgColor() {
        return switch (style) {
            case PRIMARY -> AppTheme.BG_BASE;
            case SECONDARY -> AppTheme.BG_BASE;
            case GHOST -> AppTheme.TEXT_SECONDARY;
            case DANGER -> AppTheme.TEXT_PRIMARY;
        };
    }

    public void setPadding() {
        switch (style) {
            case GHOST -> setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
            default -> setBorder(BorderFactory.createEmptyBorder(9, 22, 9, 22));
        }
    }

    
}
