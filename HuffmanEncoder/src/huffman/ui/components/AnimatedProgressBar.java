package huffman.ui.components;

import huffman.ui.theme.AppTheme;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JPanel;
import javax.swing.Timer;

public class AnimatedProgressBar extends JPanel {

    private float targetValue = 0f;
    private float currentValue = 0f;
    private Color barColor;
    private String label = "";
    private final Timer animTimer;

    public AnimatedProgressBar() {
        this(AppTheme.ACCENT);
    }

    public AnimatedProgressBar(Color color) {
        this.barColor = color;
        setOpaque(false);
        setPreferredSize(new Dimension(200, 22));

        animTimer = new Timer(16, e -> {
            float diff = targetValue - currentValue;
            currentValue += diff * 0.12f;
            
            if (Math.abs(diff) < 0.001f) {
                currentValue = targetValue;
                ((Timer) e.getSource()).stop();
            }
            repaint();
        });
    }

    /**
     * Animates the bar to {@code value} (clamped to [0.0, 1.0])
     * Pass {@code 0.0} to reset to empty and {@code 1.0} for full
     */
    public void setValue(double value) {
        this.targetValue = (float) Math.max(0.0, Math.min(1.0, value));
        if (!animTimer.isRunning()) {
            animTimer.start();
        }
    }

    public void setPercent(int percent) {
        setValue(percent / 100.0);
    }

    public float getCurrentValue() {
        return currentValue;
    }

    public float getTargetValue() {
        return targetValue;
    }

    public void setBarColor(Color c) {
        this.barColor = c;
        repaint();
    }

    public void setLabel(String text) {
        this.label = text == null ? "" : text;
        repaint();
    }

    public void setValueImmediately(double value) {
        animTimer.stop();
        currentValue = targetValue = (float) Math.max(0.0, Math.min(1.0, value));
        repaint();
    }

    // Painting
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        int r = h / 2;    // Corner radius = half height -> pill shaped

        // Track
        g2.setColor(AppTheme.BG_CARD);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, r, r));
        g2.setColor(AppTheme.BORDER);
        g2.setStroke(new BasicStroke(1));
        g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, w - 1, h - 1, r, r));

        // Fill
        if (currentValue > 0.001f) {
            int fillW = Math.max(r, (int) (currentValue * (w - 2)));

            // Clip for interior tracking -> rounded ends aren't clobbered
            Shape savedClip = g2.getClip();
            g2.setClip(new RoundRectangle2D.Float(1, 1, w - 2, h - 2, r - 1, r - 1));

            GradientPaint gp = new GradientPaint(
                1, 0, barColor,
                    1 + fillW, 0, barColor.brighter());
            g2.setPaint(gp);
            g2.fillRoundRect(1, 1, fillW, h - 2, r - 1, r - 1);

            g2.setClip(savedClip);

            // Add a subtle glow effect along the filling edge
            AppTheme.paintGlow(g2, 1, 1, fillW, h - 2, barColor, 3);
        }

        // Center label & percentage display
        String text = label.isEmpty() ? String.format("%.0f%%", currentValue * 100) : label;

        g2.setFont(AppTheme.FONT_UI.deriveFont(Font.BOLD, 10f));

        FontMetrics fm = g2.getFontMetrics();

        int tx = (w - fm.stringWidth(text)) / 2;
        int ty = (h - fm.getHeight()) / 2 + fm.getAscent();

        // Text colour flips for readability once the the bar is more than half full
        g2.setColor(currentValue > 0.55f ? AppTheme.BG_BASE : AppTheme.TEXT_SECONDARY);
        g2.drawString(text, tx, ty);
        
        g2.dispose();
    }
}
