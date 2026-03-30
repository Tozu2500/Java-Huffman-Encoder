package huffman.ui.panels;

import huffman.core.CompressionResult;
import huffman.core.HuffmanDecoder;
import huffman.ui.components.DarkButton;
import huffman.ui.components.DarkTextArea;
import huffman.ui.components.ToastNotification;
import huffman.ui.theme.AppTheme;
import huffman.util.ClipboardUtil;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public class DecoderPanel extends JPanel {

    private final DarkTextArea bitsInput = new DarkTextArea(6, 50);
    private final DarkTextArea textOutput = new DarkTextArea(6, 50);
    private final JFrame owner;
    private final EncoderPanel encoderPanel;

    public DecoderPanel(JFrame owner, EncoderPanel enc) {
        this.owner = owner;
        this.encoderPanel = enc;
        setOpaque(false);
        setLayout(new BorderLayout());
        build();
    }

    private void build() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        toolbar.setOpaque(false);
        DarkButton btnDecode = new DarkButton("Decode", DarkButton.Style.PRIMARY);
        DarkButton btnFromEncode = new DarkButton("Load from Encoder", DarkButton.Style.SECONDARY);
        DarkButton btnClear = new DarkButton("Clear", DarkButton.Style.GHOST);
        DarkButton btnCopyOut = new DarkButton("Copy Decoded Text", DarkButton.Style.GHOST);
        toolbar.add(btnDecode);
        toolbar.add(btnFromEncode);
        toolbar.add(Box.createHorizontalStrut(8));
        toolbar.add(btnClear);
        toolbar.add(btnCopyOut);

        bitsInput.setPlaceholder("Paste encoded bit string here (0s and 1s)...");
        textOutput.setPlaceholder("Decoded text will appear here...");
        textOutput.setEditable(false);

        JLabel hint = new JLabel("The decoder uses the Huffman Tree from the last Encoding operation.");
        hint.setForeground(AppTheme.TEXT_MUTED);
        hint.setFont(AppTheme.FONT_UI.deriveFont(11f));

        JPanel inputSec = buildSection("ENCODED BITS INPUT", bitsInput.inScrollPane());
        JPanel outputSec = buildSection("DECODED TEXT OUTPUT", textOutput.inScrollPane());

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, inputSec, outputSec);
        split.setOpaque(false);
        split.setDividerSize(5);
        split.setResizeWeight(0.5);
        split.setBorder(null);
        split.setDividerLocation(200);
        styleSplitDivider(split);

        JPanel south = new JPanel(new BorderLayout());
        south.setOpaque(false);
        south.add(hint, BorderLayout.WEST);

        add(toolbar, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        btnDecode.addActionListener(e -> decode());
        btnFromEncode.addActionListener(e -> loadFromEncoder());
        btnClear.addActionListener(e -> {
            bitsInput.setText("");
            textOutput.setText("");
        });

        btnCopyOut.addActionListener(e -> {
            String t = textOutput.getText();
            if (!t.isEmpty()) {
                ClipboardUtil.copy(t);
                ToastNotification.show(owner, "Decoded text copied!", ToastNotification.Type.SUCCESS);
            }
        });
    }

    private JPanel buildSection(String title, JComponent content) {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(10, 14, 4, 14));
        JLabel lbl = new JLabel(title);
        lbl.setFont(AppTheme.FONT_UI.deriveFont(Font.BOLD, 10f));
        lbl.setForeground(AppTheme.TEXT_MUTED);
        p.add(lbl, BorderLayout.NORTH);
        p.add(content, BorderLayout.CENTER);
        return p;
    }

    private void loadFromEncoder() {
        CompressionResult r = encoderPanel.getLastResult();
        if (r == null) {
            ToastNotification.show(owner, "No encode result found. Encode some text first.", ToastNotification.Type.WARNING);
            return;
        }

        bitsInput.setText(r.encodedBits);
        ToastNotification.show(owner, "Loaded encoded bits from Encoder tab.", ToastNotification.Type.INFO);
    }

    private void decode() {
        String bits = bitsInput.getText().replaceAll("[^01]", "");
        if (bits.isEmpty()) {
            ToastNotification.show(owner, "No bits to decode.", ToastNotification.Type.WARNING);
            return;
        }

        CompressionResult r = encoderPanel.getLastResult();
        if (r == null || r.treeRoot == null) {
            ToastNotification.show(owner, "Please encode text first to build the tree.", ToastNotification.Type.WARNING);
            return;
        }

        String decoded = HuffmanDecoder.decode(bits, r.treeRoot);
        textOutput.setText(decoded);
        ToastNotification.show(owner, "Decoded " + bits.length() + " bits -> " + decoded.length() + " chars.", ToastNotification.Type.SUCCESS);
    }

    private void styleSplitDivider(JSplitPane sp) {
        sp.setUI(new BasicSplitPaneUI() {
            public BasicSplitPaneDivider createDefaultDivider() {
                return new BasicSplitPaneDivider(this) {
                    public void paint(Graphics g) {
                        g.setColor(AppTheme.BORDER);
                        g.fillRect(0, 0, getWidth(), getHeight());
                    }
                };    
            }
        });
    }
}
