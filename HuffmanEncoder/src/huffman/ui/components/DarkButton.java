package huffman.ui.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.Timer;

import huffman.ui.theme.AppTheme;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

public class DarkButton extends JButton {
    public enum Style { PRIMARY, SECONDARY, GHOST, DANGER }

    private float hoverAlpha = 0f;
    private float pressAlpha = 0f;
    private Timer hoverTimer;
    private boolean hovering = false;
    private final Style style;

    public DarkButton(String text) { this(text, Style.PRIMARY); }

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

        hoverTimer = new Timer(12, e -> {
            float target = hovering ? 1f : 0f;
            hoverAlpha += (target - hoverAlpha) * 0.18f;
            if (Math.abs(hoverAlpha - target) < 0.01f) { hoverAlpha = target; ((Timer)e.getSource()).stop(); }
            repaint();
        });

        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { hovering = true; hoverTimer.start(); }
            public void mouseExited(MouseEvent e)  { hovering = false; hoverTimer.start(); }
            public void mousePressed(MouseEvent e) { pressAlpha = 1f; repaint(); }
            public void mouseReleased(MouseEvent e){ pressAlpha = 0f; repaint(); }
        });
    }

    private Color fgColor() {
        return switch (style) {
            case PRIMARY   -> AppTheme.BG_BASE;
            case SECONDARY -> AppTheme.BG_BASE;
            case GHOST     -> AppTheme.TEXT_SECONDARY;
            case DANGER    -> AppTheme.TEXT_PRIMARY;
        };
    }

    private void setPadding() {
        switch (style) {
            case GHOST -> setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
            default    -> setBorder(BorderFactory.createEmptyBorder(9, 22, 9, 22));
        }
    }

    @Override protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();
        RoundRectangle2D rr = new RoundRectangle2D.Float(0, 0, w, h, AppTheme.RADIUS, AppTheme.RADIUS);

        // base fill
        Color base = switch (style) {
            case PRIMARY   -> AppTheme.ACCENT;
            case SECONDARY -> new Color(0xBB86FC);
            case GHOST     -> new Color(0,0,0,0);
            case DANGER    -> AppTheme.ERROR;
        };
        if (style != Style.GHOST) {
            g2.setColor(base);
            g2.fill(rr);
        } else {
            g2.setColor(new Color(255,255,255, (int)(hoverAlpha * 15)));
            g2.fill(rr);
            g2.setColor(AppTheme.BORDER);
            g2.setStroke(new BasicStroke(1));
            g2.draw(rr);
        }

        // hover overlay
        if (hoverAlpha > 0.01f) {
            g2.setColor(new Color(0,0,0, (int)(hoverAlpha * 40)));
            g2.fill(rr);
        }

        // Press overlay
        if (pressAlpha > 0.01f) {
            g2.setColor(new Color(0, 0, 0, (int)(hoverAlpha * 60)));
            g2.fill(rr);
        }

        // Glow for primary
        if (style == Style.PRIMARY && isEnabled()) {
            AppTheme.paintGlow(g2, 0, 0, w, h, AppTheme.ACCENT, (int)(hoverAlpha * 6) + 2);
        }
        g2.dipose();
        super.paintComponent(g);
    }

    @Override
    public boolean isOpaque() {
        return false;
    }
}
