package huffman.ui.panels;

import huffman.core.CompressionResult;
import huffman.core.FrequencyAnalyzer;
import huffman.core.HuffmanDecoder;
import huffman.core.HuffmanEncoder;
import huffman.core.HuffmanNode;
import huffman.core.HuffmanSerializer;
import huffman.core.HuffmanTree;
import huffman.ui.components.DarkButton;
import huffman.ui.components.MetricCard;
import huffman.ui.components.SlimScrollBarUI;
import huffman.ui.components.ToastNotification;
import huffman.ui.theme.AppTheme;
import huffman.util.FileUtil;
import huffman.util.StatsUtil;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FilePanel extends JPanel {

    private final JFrame owner;
    private final EncoderPanel encoderPanel;
    private final JTextArea logArea;
    private final MetricCard cardOrigSize = new MetricCard("Original", "—", "bytes", AppTheme.TEXT_SECONDARY);
    private final MetricCard cardCompSize = new MetricCard("Compressed", "—", "bytes", AppTheme.ACCENT);
    private final MetricCard cardRatio = new MetricCard("Ratio", "—", "% of orig", AppTheme.SUCCESS);
    private File lastOpenedFile;

    public FilePanel(JFrame owner, EncoderPanel enc) {
        this.owner = owner;
        this.encoderPanel = enc;
        setOpaque(false);
        setLayout(new BorderLayout(0, 0));

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setBackground(AppTheme.BG_INPUT);
        logArea.setForeground(AppTheme.TEXT_PRIMARY);
        logArea.setFont(AppTheme.FONT_MONO.deriveFont(12f));
        logArea.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        build();
    }

    private void build() {
        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        toolbar.setOpaque(false);
        DarkButton btnOpen = new DarkButton("Open Text File", DarkButton.Style.PRIMARY);
        DarkButton btnEncFile = new DarkButton("Encode & Save .huff", DarkButton.Style.SECONDARY);
        DarkButton btnDecFile = new DarkButton("Open .huff File", DarkButton.Style.GHOST);
        DarkButton btnClear = new DarkButton("Clear Log", DarkButton.Style.GHOST);
        
        toolbar.add(btnOpen);
        toolbar.add(btnEncFile);
        toolbar.add(Box.createHorizontalStrut(10));
        toolbar.add(btnDecFile);
        toolbar.add(btnClear);

        btnOpen.addActionListener(e -> openTextFile());
        btnEncFile.addActionListener(e -> encodeFile());
        btnDecFile.addActionListener(e -> decodeHuffFile());
        btnClear.addActionListener(e -> logArea.setText(""));

        // Metrics panel
        JPanel metrics = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        metrics.setOpaque(false);

        metrics.add(cardOrigSize);
        metrics.add(cardCompSize);
        metrics.add(cardRatio);

        // Log section
        JPanel logPanel = new JPanel(new BorderLayout(0, 4));
        logPanel.setOpaque(false);
        logPanel.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));

        JLabel logLbl = new JLabel("OPERATION LOG");
        logLbl.setFont(AppTheme.FONT_UI.deriveFont(Font.BOLD, 10f));
        logLbl.setForeground(AppTheme.TEXT_MUTED);

        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(new AppTheme.RoundedBorder(AppTheme.BORDER, AppTheme.RADIUS, 1));
        logScroll.getVerticalScrollBar().setUI(new SlimScrollBarUI());
        
        logPanel.add(logLbl, BorderLayout.NORTH);
        logPanel.add(logScroll, BorderLayout.CENTER);

        // Hint label
        JLabel hint = new JLabel("  Tip: Open a .txt file -> Encode -> Save as .huff. Then use 'Open .huff' to encode it.");
        hint.setForeground(AppTheme.TEXT_MUTED);
        hint.setFont(AppTheme.FONT_UI.deriveFont(11f));

        JPanel south = new JPanel(new BorderLayout());
        south.setOpaque(false);
        south.add(metrics, BorderLayout.NORTH);
        south.add(hint, BorderLayout.SOUTH);

        add(toolbar, BorderLayout.NORTH);
        add(logPanel, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);
    }

    private void openTextFile() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Text Files (*.txt)", "txt"));

        if (fc.showOpenDialog(owner) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        lastOpenedFile = fc.getSelectedFile();

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            protected String doInBackground() throws Exception {
                return FileUtil.readText(lastOpenedFile);
            }

            protected void done() {
                try {
                    String text = get();
                    log("Opened: " + lastOpenedFile.getName() + " (" + text.length() + " chars, " + lastOpenedFile.length() + " bytes");
                    ToastNotification.show(owner, "File loaded: " + lastOpenedFile.getName(), ToastNotification.Type.SUCCESS);
                    cardOrigSize.update(lastOpenedFile.length() + "", "bytes");
                } catch (Exception ex) {
                    log("Error " + ex.getMessage());
                }
            }
        };

        worker.execute();
    }

    private void encodeFile() {

        if (lastOpenedFile == null) {
            ToastNotification.show(owner, "Open a text file first.", ToastNotification.Type.WARNING); 
            return;
        }

        JFileChooser fc = styledChooser();

        fc.setFileFilter(new FileNameExtensionFilter("Huffman Files (*.huff)", "huff"));
        fc.setSelectedFile(new File(lastOpenedFile.getParent(),
                lastOpenedFile.getName().replaceAll("\\.[^.]+$","") + ".huff"));

        if (fc.showSaveDialog(owner) != JFileChooser.APPROVE_OPTION) return;

        File outFile = fc.getSelectedFile();
        if (!outFile.getName().endsWith(".huff")) outFile = new File(outFile.getPath() + ".huff");
        final File finalOut = outFile;

        SwingWorker<CompressionResult, Void> worker = new SwingWorker<>() {
            protected CompressionResult doInBackground() throws Exception {
                String text = FileUtil.readText(lastOpenedFile);

                Map<Character, Integer> freq = FrequencyAnalyzer.analyze(text);
                HuffmanTree tree = new HuffmanTree(freq);
                String bits = HuffmanEncoder.encode(text, tree.getCodes());

                double entropy = FrequencyAnalyzer.entropy(freq);
                double avgLen  = tree.averageCodeLength(freq);

                CompressionResult r = new CompressionResult(text, bits, tree.getCodes(), freq,
                        tree.getRoot(), entropy, avgLen);
                HuffmanSerializer.save(finalOut, r);
                return r;
            }

            protected void done() {
                try {
                    CompressionResult r = get();
                    log("⚡ Encoded: " + lastOpenedFile.getName() + " → " + finalOut.getName());
                    log("   Original : " + StatsUtil.formatBits(r.originalBits));
                    log("   Compressed: " + StatsUtil.formatBits(r.compressedBits));
                    log("   Savings  : " + String.format("%.1f%%", r.spaceSavingsPercent()));
                    log("   Saved to : " + finalOut.getAbsolutePath());
                    cardCompSize.update(String.valueOf(finalOut.length()), "bytes");
                    cardRatio.update(String.format("%.1f", r.compressionRatio * 100), "% of orig");
                    ToastNotification.show(owner, "Saved " + finalOut.getName(), ToastNotification.Type.SUCCESS);
                } catch (Exception ex) { log("❌ " + ex.getMessage()); }
            }
        };
        worker.execute();
    }

    private void decodeHuffFile() {
        JFileChooser fc = styledChooser();

        fc.setFileFilter(new FileNameExtensionFilter("Huffman Files (*.huff)", "huff"));

        if (fc.showOpenDialog(owner) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File inFile = fc.getSelectedFile();

        JFileChooser fc2 = styledChooser();

        fc2.setFileFilter(new FileNameExtensionFilter("Text Files (*.txt", "txt"));
        fc2.setSelectedFile(new File(inFile.getParent(), inFile.getName().replace(".huff", "_decoded.txt")));

        if (fc2.showSaveDialog(owner) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File outFile = fc2.getSelectedFile();

        SwingWorker<Integer, Void> worker = new SwingWorker<>() {
            protected Integer doInBackground() throws Exception {
                HuffmanSerializer.LoadResult lr = HuffmanSerializer.load(inFile);

                // Rebuild tree from codes
                HuffmanNode root = rebuildTreeFromCodes(lr.codes());
                String decoded = HuffmanDecoder.decode(lr.bits(), root);

                FileUtil.writeText(outFile, decoded);
                return decoded.length();
            }

            protected void done() {
                try {
                    int chars = get();
                    log("Decoded: " + inFile.getName() + " -> " + outFile.getName() + " (" + chars + " chars)");
                    ToastNotification.show(owner, "Decoded to " + outFile.getName(), ToastNotification.Type.SUCCESS);
                } catch (Exception ex) {
                    log("Error" + ex.getMessage());
                }
            }
        };

        worker.execute();
    }

    private HuffmanNode rebuildTreeFromCodes(Map<Character, String> codes) {
        if (codes.size() == 1) {
            var entry = codes.entrySet().iterator().next();

            HuffmanNode leaf = new HuffmanNode(entry.getKey(), 0);
            return new HuffmanNode(0, leaf, null);
        }

        HuffmanNode root = new HuffmanNode(0, null, null);
        for (var entry : codes.entrySet()) {
            char ch = entry.getKey();
            String code = entry.getValue();
            HuffmanNode current = root;

            for (int i = 0; i < code.length(); i++) {
                boolean isLast = (i == code.length() - 1);
                
                if (code.charAt(i) == '0') {
                    if (isLast) {
                        current.left = new HuffmanNode(ch, 0);
                    } else {
                        if (current.left == null) {
                            current.left = new HuffmanNode(0, null, null);
                        }
                        current = current.left;
                    }
                } else {
                    if (isLast) {
                        current.right = new HuffmanNode(ch, 0);
                    } else {
                        if (current.right == null) {
                            current.right = new HuffmanNode(0, null, null);
                        }
                        current = current.right;
                    }
                }
            }
        }

        return root;
    }

    private JFileChooser styledChooser() {
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(System.getProperty("user.home")));
        return fc;
    }

    private void log(String msg) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(msg + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
}
