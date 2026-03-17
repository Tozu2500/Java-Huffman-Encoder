package huffman.ui.components;

import huffman.ui.theme.AppTheme;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class MetricCard extends JPanel {

    private String label;
    private String value;
    private String unit;
    private Color accentColor;

    public MetricCard(String label, String value, String unit, Color accent) {
        this.label = label;
        this.value = value;
        this.unit = unit;
        this.accentColor = accent;
        setOpaque(false);
        setPreferredSize(new Dimension(160, 90));
        setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
    }

    public void update(String value, String unit) {
        this.value = value;
        this.unit = unit;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Card background
        g2.setColor(AppTheme.BG_CARD);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, AppTheme.RADIUS, AppTheme.RADIUS));

        // Accent left bar
        g2.setColor(accentColor);
        g2.fillRoundRect(0, h/4, 3, h/2, 3, 3);

        // Border
        g2.setColor(AppTheme.BORDER);
        g2.setStroke(new BasicStroke(1));
        g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, w-1, h-1, AppTheme.RADIUS, AppTheme.RADIUS));

        Insets insets = getInsets();
        int cx = insets.left + 4;
        int cy = insets.top;

        // Label
        g2.setFont(AppTheme.FONT_UI.deriveFont(10f));
        g2.setColor(AppTheme.TEXT_MUTED);
        g2.drawString(label.toUpperCase(), cx, cy + 14);

        // Value
        g2.setFont(AppTheme.FONT_TITLE.deriveFont(Font.BOLD, 22f));
        g2.setColor(accentColor);
        g2.drawString(value, cx, cy + 42);

        // Unit
        if (unit != null && !unit.isEmpty()) {
            g2.setFont(AppTheme.FONT_UI.deriveFont(11f));
            g2.setColor(AppTheme.TEXT_SECONDARY);
            g2.drawString(unit, cx, cy + 60);
        }
        g2.dispose();
    }
}
