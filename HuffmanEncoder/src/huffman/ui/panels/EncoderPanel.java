package huffman.ui.panels;

import huffman.core.CompressionResult;
import huffman.core.FrequencyAnalyzer;
import huffman.core.HuffmanEncoder;
import huffman.core.HuffmanTree;
import huffman.ui.components.DarkButton;
import huffman.ui.components.DarkTextArea;
import huffman.ui.components.MetricCard;
import huffman.ui.components.SyntaxHighlightPane;
import huffman.ui.components.ToastNotification;
import huffman.ui.theme.AppTheme;
import huffman.util.ClipboardUtil;
import huffman.util.StatsUtil;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public class EncoderPanel extends JPanel {

    private final DarkTextArea inputArea = new DarkTextArea(8, 50);
    private final SyntaxHighlightPane outputPane = new SyntaxHighlightPane();
    private final MetricCard cardRatio = new MetricCard("Compression", "-", "ratio", AppTheme.ACCENT);
    private final MetricCard cardSaved = new MetricCard("Bits Saved", "-", "bits", AppTheme.SUCCESS);
    private final MetricCard cardEntropy = new MetricCard("Entropy", "-", "bits/sym", AppTheme.ACCENT2);
    private final MetricCard cardSymbols = new MetricCard("Symbols", "-", "unique", AppTheme.WARNING);
    private final JLabel statsLabel = new JLabel(" ");

    private CompressionResult lastResult;
    private final VisualizerPanel visualizerPanel;
    private final FrequencyPanel freqPanel;
    private final CodeTablePanel codePanel;
    private final JFrame owner;

    public EncoderPanel(JFrame owner, VisualizerPanel vis, FrequencyPanel freq, CodeTablePanel code) {
        this.owner = owner;
        this.visualizerPanel = vis;
        this.freqPanel = freq;
        this.codePanel = code;
        setOpaque(false);
        setLayout(new BorderLayout(0, 0));
        build();
    }

    private void build() {
        // Top toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        toolbar.setOpaque(false);
        DarkButton btnEncode = new DarkButton("Encode", DarkButton.Style.PRIMARY);
        DarkButton btnClear = new DarkButton("Clear", DarkButton.Style.GHOST);
        DarkButton btnPaste = new DarkButton("Paste", DarkButton.Style.GHOST);
        DarkButton btnCopyOut = new DarkButton("Copy Output", DarkButton.Style.SECONDARY);
        DarkButton btnStats = new DarkButton("Full Report", DarkButton.Style.GHOST);

        toolbar.add(btnEncode);
        toolbar.add(btnClear);
        toolbar.add(btnPaste);
        toolbar.add(Box.createHorizontalStrut(12));
        toolbar.add(btnCopyOut);
        toolbar.add(btnStats);

        btnEncode.addActionListener(e -> encode());
        btnClear.addActionListener(e -> {
            inputArea.setText("");
            outputPane.setBits("");
            resetCards();
        });

        btnPaste.addActionListener(e -> inputArea.setText(ClipboardUtil.paste()));

        btnCopyOut.addActionListener(e -> {
            if (lastResult != null) {
                ClipboardUtil.copy(lastResult.encodedBits);
                ToastNotification.show(owner, "Encoded bits copied!", ToastNotification.Type.SUCCESS);
            }
        });

        btnStats.addActionListener(e -> showFullReport());

        // Main split
        JPanel inputSection = buildSection("INPUT TEXT", inputArea.inScrollPane());
        JPanel outputSection = buildSection("ENCODED BITS (cyan=0, purple=1)", outputPane.inScrollPane());

        inputArea.setPlaceholder("Type or paste text to encode...");

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, inputSection, outputSection);
        split.setOpaque(false);
        split.setDividerSize(5);
        split.setResizeWeight(0.45);
        split.setBorder(null);
        split.setDividerLocation(220);
        styleScrollDivider(split);

        // Row of metrics
        JPanel metrics = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        metrics.setOpaque(false);
        metrics.add(cardRatio);
        metrics.add(cardSaved);
        metrics.add(cardEntropy);
        metrics.add(cardSymbols);
        metrics.add(Box.createHorizontalStrut(10));

        statsLabel.setForeground(AppTheme.TEXT_SECONDARY);
        statsLabel.setFont(AppTheme.FONT_UI.deriveFont(11f));

        metrics.add(statsLabel);

        add(toolbar, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
        add(metrics, BorderLayout.SOUTH);
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

    private void styleScrollDivider(JSplitPane sp) {
        sp.setUI(new BasicSplitPaneUI() {
            @Override
            public BasicSplitPaneDivider createDefaultDivider() {
                return new BasicSplitPaneDivider(this) {
                    @Override
                    public void paint(Graphics g) {
                        g.setColor(AppTheme.BORDER);
                        g.fillRect(0, 0, getWidth(), getHeight());
                    }
                };
            }
        });
    }

    private void encode() {
        String text = inputArea.getText();
        if (text.isEmpty()) {
            ToastNotification.show(owner, "Input text is empty.", ToastNotification.Type.WARNING);
            return;
        }

        SwingWorker<CompressionResult, Void> worker = new SwingWorker<>() {
            protected CompressionResult doInBackground() {
                Map<Character, Integer> freq = FrequencyAnalyzer.analyze(text);
                HuffmanTree tree = new HuffmanTree(freq);
                String bits = HuffmanEncoder.encode(text, tree.getCodes());
                double entropy = FrequencyAnalyzer.entropy(freq);
                double avgLen = tree.averageCodeLength(freq);
                return new CompressionResult(text, bits, tree.getCodes(), freq, tree.getRoot(), entropy, avgLen);
            }

            protected void done() {
                try {
                    lastResult = get();
                    outputPane.setBits(lastResult.encodedBits);
                    updateCards(lastResult);
                    visualizerPanel.setTree(lastResult.treeRoot, lastResult.codes);
                    freqPanel.setData(lastResult.frequencies);
                    codePanel.setData(lastResult.codes, lastResult.frequencies);
                    ToastNotification.show(owner, "Encoded successfully!", ToastNotification.Type.SUCCESS);
                } catch (Exception ex) {
                    ToastNotification.show(owner, "Error: " + ex.getMessage(), ToastNotification.Type.ERROR);
                }
            }
        };

        worker.execute();
    }

    private void updateCards(CompressionResult r) {
        cardRatio.update(String.format("%.1f%%", r.compressionRatio * 100), "of original");
        cardSaved.update(StatsUtil.formatBits(r.bitsSaved), "saved");
        cardEntropy.update(String.format("%.3f", r.entropy), "bits/symbol");
        cardSymbols.update(String.valueOf(r.frequencies.size()), "unique chars");
        statsLabel.setText(String.format("  %,d chars -> %,d bits  (avg code: %.2f bits/sym)",
                r.originalText.length(), r.compressedBits, r.avgCodeLength));
    }

    private void resetCards() {
        cardRatio.update("-", "ratio");
        cardSaved.update("-", "bits");
        cardEntropy.update("-", "bits/sym");
        cardSymbols.update("-", "unique");
        statsLabel.setText(" ");
        lastResult = null;
    }

    private void showFullReport() {
        if (lastResult == null) {
            ToastNotification.show(owner, "Encode some text first.", ToastNotification.Type.WARNING);
            return;
        }

        JTextArea ta = new JTextArea(StatsUtil.summaryReport(lastResult));
        ta.setFont(AppTheme.FONT_MONO);
        ta.setBackground(AppTheme.BG_CARD);
        ta.setForeground(AppTheme.TEXT_PRIMARY);
        ta.setEditable(false);
        JOptionPane.showMessageDialog(owner, new JScrollPane(ta), "Compression Report", JOptionPane.PLAIN_MESSAGE);
    }

    public CompressionResult getLastResult() {
        return lastResult;
    }
}
