package huffman.ui.theme;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;

public class AppTheme {

    public static final Color BG_BASE = new Color(13, 15, 20);
    public static final Color BG_PANEL = new Color(21, 24, 32);
    public static final Color BG_CARD = new Color(28, 31, 43);
    public static final Color BG_INPUT = new Color(18, 20, 28);
    public static final Color BG_HOVER = new Color(35, 39, 58);

    public static final Color ACCENT = new Color(0, 229, 255);
    public static final Color ACCENT2 = new Color(187, 134, 252);
    public static final Color SUCCESS = new Color(0, 230, 118);
    public static final Color WARNING = new Color(255, 171, 64);
    public static final Color ERROR = new Color(255, 82, 82);

    public static final Color TEXT_PRIMARY = new Color(232, 234, 240);
    public static final Color TEXT_SECONDARY = new Color(138, 143, 168);
    public static final Color TEXT_MUTED = new Color(74, 80, 104);

    public static final Color BORDER = new Color(37, 40, 64);
    public static final Color BORDER_FOCUS = new Color(0, 229, 255);

    public static Font FONT_MONO;
    public static Font FONT_UI;
    public static Font FONT_TITLE;

    static {
        try {
            FONT_MONO = new Font("JetBrains Mono", Font.PLAIN, 13);
            if (!FONT_MONO.getFamily().equals("JetBrains Mono"))
                FONT_MONO = new Font("Courier New", Font.PLAIN, 13);
        } catch (Exception e) {
            FONT_MONO = new Font(Font.MONOSPACED, Font.PLAIN, 13);
        }
        FONT_UI = new Font("Segoe UI", Font.PLAIN, 13);
        FONT_TITLE = new Font("Segoe UI", Font.BOLD, 15);
    }

    public static final int RADIUS = 10;
    public static final int RADIUS_SM = 6;
    public static final int PAD = 16;

    public static void paintGlow(Graphics2D g2, int x, int y, int w, int h, Color c, int spread) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int i = spread; i > 0; i--) {
            float alpha = 0.04f * (spread - i + 1);
            g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), Math.min(255, (int)(alpha * 255))));
            g2.setStroke(new BasicStroke(i * 1.5f));
            g2.draw(new RoundRectangle2D.Float(x - i, y - i, w + i * 2, h + i * 2, RADIUS * i, RADIUS * i));
        }
    }

    public static class RoundedBorder extends AbstractBorder {
        private final Color color;
        private final int radius;
        private final int thickness;
        
        public RoundedBorder(Color c, int r, int t) {
            color = c;
            radius = r;
            thickness = t;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.draw(new RoundRectangle2D.Float(x + thickness / 2f, y + thickness / 2f,
                    w - thickness, h - thickness, radius, radius));
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius / 2, radius / 2, radius / 2, radius / 2);
        }
    }

    public static void applyGlobalDefaults() {
        UIManager.put("Panel.background", BG_PANEL);
        UIManager.put("ScrollPane.background", BG_PANEL);
        UIManager.put("Viewport.background", BG_PANEL);
        UIManager.put("ScrollBar.thumb", BG_HOVER);
        UIManager.put("ScrollBar.track", BG_BASE);
        UIManager.put("ScrollBar.thumbDarkShadow", BG_BASE);
        UIManager.put("ScrollBar.thumbHighlight", BG_HOVER);
        UIManager.put("ScrollBar.width", 8);
        UIManager.put("TabbedPane.background", BG_BASE);
        UIManager.put("TabbedPane.foreground", TEXT_PRIMARY);
        UIManager.put("TabbedPane.selected", BG_PANEL);
        UIManager.put("TabbedPane.contentAreaColor", BG_BASE);
        UIManager.put("Table.background", BG_CARD);
        UIManager.put("Table.foreground", TEXT_PRIMARY);
        UIManager.put("Table.gridColor", BORDER);
        UIManager.put("Table.selectionBackground", new Color(0x00, 0xE5, 0xFF, 0x40));
        UIManager.put("TableHeader.background", BG_BASE);
        UIManager.put("TableHeader.foreground", ACCENT);
        UIManager.put("ToolTip.background", BG_CARD);
        UIManager.put("ToolTip.foreground", TEXT_PRIMARY);
        UIManager.put("ToolTip.border", BorderFactory.createLineBorder(BORDER));
        UIManager.put("Label.foreground", TEXT_PRIMARY);
        UIManager.put("ComboBox.background", BG_INPUT);
        UIManager.put("ComboBox.foreground", TEXT_PRIMARY);
        UIManager.put("Separator.background", BORDER);
        UIManager.put("Separator.foreground", BORDER);
    }
}
