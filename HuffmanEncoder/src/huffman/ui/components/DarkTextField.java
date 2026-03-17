package huffman.ui.components;

import huffman.ui.theme.AppTheme;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.BorderFactory;
import javax.swing.JTextField;

public class DarkTextField extends JTextField {

    private String placeholder = "";
    private boolean focused = false;

    public DarkTextField() {
        this(20);
    }

    public DarkTextField(int columns) {
        super(columns);
        setBackground(AppTheme.BG_INPUT);
        setForeground(AppTheme.TEXT_PRIMARY);
        setCaretColor(AppTheme.ACCENT);
        setFont(AppTheme.FONT_MONO);
        setOpaque(true);
        applyBorder(false);

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                focused = true;
                applyBorder(true);
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                focused = false;
                applyBorder(false);
                repaint();
            }
        });
    }

    public void setPlaceholder(String text) {
        this.placeholder = text == null ? "" : text;
        repaint();
    }

    public String getPlaceholder() {
        return placeholder;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!focused && getText().isEmpty() && !placeholder.isEmpty()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(AppTheme.TEXT_MUTED);
            g2.setFont(getFont().deriveFont(Font.ITALIC));
            Insets ins = getInsets();
            FontMetrics fontMetrics = g2.getFontMetrics();
            int ty = (getHeight() - fontMetrics.getHeight()) / 2 + fontMetrics.getAscent();
            g2.drawString(placeholder, ins.left, ty);
            g2.dispose();
        }
    }

    private void applyBorder(boolean hasFocus) {
        Color borderColor = hasFocus ? AppTheme.ACCENT : AppTheme.BORDER;
        setBorder(BorderFactory.createCompoundBorder(
            new AppTheme.RoundedBorder(borderColor, AppTheme.RADIUS_SM, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
    }
}
