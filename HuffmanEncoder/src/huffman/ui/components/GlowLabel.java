package huffman.ui.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JLabel;

import huffman.ui.theme.AppTheme;

public class GlowLabel extends JLabel {

    private final Color glowColor;

    public GlowLabel(String text, Color glow) {
        super(text);
        this.glowColor = glow;
        setForeground(AppTheme.TEXT_PRIMARY);
        setFont(AppTheme.FONT_TITLE.deriveFont(Font.BOLD, 20f));
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Glow pass
        for (int i = 3; i > 0; i--) {
            g2.setFont(getFont());
            g2.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), 30));
            
            FontMetrics fm = g2.getFontMetrics();
            int x = getInsets().left, y = fm.getAscent() + getInsets().top;

            g2.drawString(getText(), x - i, y);
            g2.drawString(getText(), x + i, y);
            g2.drawString(getText(), x, y - i);
            g2.drawString(getText(), x, y + i);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
