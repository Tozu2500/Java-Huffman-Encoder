package huffman.ui.components;

import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import huffman.ui.theme.AppTheme;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;

public class DarkTextArea extends JTextArea {

    private String placeholder = "";
    private boolean showPlaceholder = true;

    public DarkTextArea(int rows, int cols) {
        super(rows, cols);
        setBackground(AppTheme.BG_INPUT);
        setForeground(AppTheme.TEXT_PRIMARY);
        setCaretColor(AppTheme.ACCENT);
        setFont(AppTheme.FONT_MONO);
        setLineWrap(true);
        setWrapStyleWord(true);
        setOpaque(true);
        setBorder(BorderFactory.createCompoundBorder(
            new AppTheme.RoundedBorder(AppTheme.BORDER, AppTheme.RADIUS, 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        getDocument().addDocumentListener(new DocumentListener() {
            void update() {
                showPlaceholder = getText().isEmpty();
                repaint();
            }

            public void insertUpdate(DocumentEvent e) {
                update();
            }

            public void removeUpdate(DocumentEvent e) {
                update();
            }

            public void changedUpdate(DocumentEvent e) {
                update();
            }
        });

        addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                repaint();
            }

            public void focusLost(FocusEvent e) {
                repaint();
            }
        });
    }

    public void setPlaceholder(String s) {
        this.placeholder = p;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (showPlaceholder && !placeholder.isEmpty()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(AppTheme.TEXT_MUTED);
            g2.setFont(getFont().deriveFont(Font.ITALIC));
            Insets ins = getInsets();
            g2.drawString(placeholder, ins.left + 2, ins.top + getFont().getSize());
            g2.dispose();
        }
    }

    public JScrollPane inScrollPane() {
        JScrollPane sp = new JScrollPane(this,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getVerticalScrollBar().setUI(new SlimScrollBarUI());
        return sp;
    }
}
