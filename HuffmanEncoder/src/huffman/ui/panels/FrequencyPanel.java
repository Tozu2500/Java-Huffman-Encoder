package huffman.ui.panels;

import huffman.ui.components.SlimScrollBarUI;
import huffman.ui.theme.AppTheme;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;

public class FrequencyPanel extends JPanel {

    private Map<Character, Integer> frequencies = new LinkedHashMap<>();
    private float animProgress = 0f;
    private Timer animTimer;

    public FrequencyPanel() {
        setOpaque(true);
        setBackground(AppTheme.BG_BASE);
    }

    public void setData(Map<Character, Integer> freq) {
        this.frequencies = freq;
        animProgress = 0f;
        
        if (animTimer != null) animTimer.stop();
        animTimer = new Timer(16, e -> {
            animProgress = Math.min(1f, animProgress + 0.04f);
            repaint();
            if (animProgress >= 1f) {
                ((Timer) e.getSource()).stop();
            }
        });
        animTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // If no data yet, draw a centered placeholder message and return early
        if (frequencies.isEmpty()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(AppTheme.TEXT_MUTED);
            g2.setFont(AppTheme.FONT_UI.deriveFont(14f));
            String msg = "Encode text to see frequency distribution";
            FontMetrics fm = g2.getFontMetrics();
            // Center the message horizontally and vertically
            g2.drawString(msg, (getWidth() - fm.stringWidth(msg)) / 2, getHeight() / 2);
            g2.dispose();
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        // Enable smooth rendering for shapes and text
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Chart layout constants: padding around edges and usable chart dimensions
        int pad = 40;
        int chartH = getHeight() - 80;
        int chartW = getWidth() - pad * 2;

        // Highest frequency value — used to scale bar heights proportionally
        int maxFreq = frequencies.values().stream().mapToInt(Integer::intValue).max().orElse(1);

        // Converting the freq. map to an ordered list for indexed iteration
        List<Map.Entry<Character, Integer>> entries = new ArrayList<>(frequencies.entrySet());
        int n = entries.size();
        if (n == 0) {
            g2.dispose();
            return;
        }

        // Bar width is clamped between 6 and 50px, shrinking when there are many characters
        int barW = Math.max(6, Math.min(50, (chartW - 10) / n - 4));
        int totalW = (barW + 4) * n; // total width of all bars including gaps
        // Center the bar group horizontally within chart area
        int startX = pad + Math.max(0, (chartW - totalW) / 2);

        // Axis: vertical line on the left, horizontal baseline at the bottom
        g2.setColor(AppTheme.BORDER);
        g2.drawLine(pad - 5, pad, pad - 5, pad + chartH);
        g2.drawLine(pad - 5, pad + chartH, getWidth() - pad, pad + chartH);

        // Y grid lines: draws 6 evenly spaced horizontal guides with frequency labels
        g2.setFont(AppTheme.FONT_UI.deriveFont(9f));
        for (int i = 0; i <= 5; i++) {
            // Compute Y position for this grid level (i=0 is baseline, i=5 is top)
            int gy = pad + chartH - (int)((double)i / 5 * chartH);
            g2.setColor(AppTheme.BORDER);
            // Dashed stroke for subtle grid lines
            g2.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                        10, new float[]{4, 4}, 0));
            g2.drawLine(pad - 5, gy, getWidth() - pad, gy);
            g2.setColor(AppTheme.TEXT_MUTED);
            g2.setStroke(new BasicStroke(1));
            // Label shows the frequency value that this grid line represents
            g2.drawString(String.valueOf(maxFreq * i / 5), 2, gy + 4);
        }

        // Bars: cycle through a fixed color palette for visual variety
        Color[] palette = {AppTheme.ACCENT, AppTheme.ACCENT2, AppTheme.SUCCESS, AppTheme.WARNING, AppTheme.ERROR};
        for (int i = 0; i < n; i++) {
            Map.Entry<Character, Integer> e = entries.get(i);
            int freq = e.getValue();
            // Bar height is proportional to frequency, scaled by animation progress (0→1)
            int barH = (int)((double) freq / maxFreq * chartH * animProgress);
            int x = startX + i * (barW + 4); // x position accounts for bar width + gap
            int y = pad + chartH - barH;      // y starts from the top of the bar

            Color col = palette[i % palette.length];

            // Gradient bar: full color at top, fades to semi-transparent at the baseline
            GradientPaint gp = new GradientPaint(x, y, col, x, pad + chartH,
                    new Color(col.getRed(), col.getGreen(), col.getBlue(), 60)
            );
            g2.setPaint(gp);
            g2.fill(new RoundRectangle2D.Float(x, y, barW, barH, 4, 4));
            
            // Value label on top: only draws, if the bar is tall enough to avoid overlap
            g2.setFont(AppTheme.FONT_UI.deriveFont(8f));
            g2.setColor(col);
            String val = String.valueOf(freq);
            FontMetrics fm = g2.getFontMetrics();
            if (barH > 14) {
                g2.drawString(val, x + (barW - fm.stringWidth(val)) / 2, y - 3);
            }

            // Char label at bottom: displays the character below the x-axis
            g2.setColor(AppTheme.TEXT_SECONDARY);
            String ch = displayChar(e.getKey());
            g2.drawString(ch, x + (barW - fm.stringWidth(ch)) / 2, pad + chartH + 14);
        }

        // Title drawn in the top-left corner of the panel
        g2.setFont(AppTheme.FONT_TITLE.deriveFont(Font.BOLD, 11f));
        g2.setColor(AppTheme.TEXT_MUTED);
        g2.drawString("CHARACTER FREQUENCY DISTRIBUTION", pad, 16);

        g2.dispose();
    }

    private String displayChar(char c) {
        return switch (c) {
            case ' ' -> "SP";
            case '\n' -> "↵";  // Unicode (U+21B5)
            case '\t' -> "⇥";  // Unicode (U+21E5)
            default -> String.valueOf(c);
        };
    }

    public JScrollPane inScrollPane() {
        JScrollPane sp = new JScrollPane(this);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getHorizontalScrollBar().setUI(new SlimScrollBarUI());
        sp.getVerticalScrollBar().setUI(new SlimScrollBarUI());
        return sp;
    }
}
