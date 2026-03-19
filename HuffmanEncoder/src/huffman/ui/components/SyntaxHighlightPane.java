package huffman.ui.components;

import huffman.ui.theme.AppTheme;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class SyntaxHighlightPane extends JTextPane {

    private static final Color COLOR_ZERO = new Color(0, 229, 255);
    private static final Color COLOR_ONE = new Color(187, 134, 252);
    private static final int CHUNK = 8;  // 8-bit grouping

    public SyntaxHighlightPane() {
        setOpaque(true);
        setBackground(AppTheme.BG_INPUT);
        setForeground(AppTheme.TEXT_PRIMARY);
        setFont(AppTheme.FONT_MONO.deriveFont(12f));
        setEditable(false);
        setBorder(BorderFactory.createCompoundBorder(
                new AppTheme.RoundedBorder(AppTheme.BORDER, AppTheme.RADIUS, 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
    }

    public void setBits(String bits) {
        StyledDocument doc = getStyledDocument();

        try {
            doc.remove(0, doc.getLength());
        } catch (BadLocationException ignored) {}

        StyleContext sc = StyleContext.getDefaultStyleContext();
        int i = 0;
        for (char c : bits.toCharArray()) {
            Color col = (c == '0') ? COLOR_ZERO : COLOR_ONE;
            AttributeSet as = sc.addAttribute(sc.getEmptySet(), StyleConstants.Foreground, col);
            try {
                doc.insertString(doc.getLength(), String.valueOf(c), as);
            } catch (BadLocationException ignored) {}
            i++;
            if (i % CHUNK == 0 && i < bits.length()) {
                AttributeSet sp = sc.addAttribute(sc.getEmptySet(), StyleConstants.Foreground, AppTheme.TEXT_MUTED);
                try {
                    doc.insertString(doc.getLength(), " ", sp);
                } catch (BadLocationException ignored) {}
            }
        }
        setCaretPosition(0);
    }

    public JScrollPane inScrollPane() {
        JScrollPane sp = new JScrollPane(this);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getVerticalScrollBar().setUI(new SlimScrollBarUI());
        sp.getHorizontalScrollBar().setUI(new SlimScrollBarUI());
        return sp;
    }
}
