package huffman.ui.panels;

import huffman.ui.components.DarkButton;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;

import huffman.ui.components.MetricCard;
import huffman.ui.components.SlimScrollBarUI;
import huffman.ui.components.ToastNotification;
import huffman.ui.theme.AppTheme;
import huffman.util.FileUtil;

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
        DarkButton btnOpen = DarkButton("Open Text File", DarkButton.Style.PRIMARY);
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
                    log("Error ", + ex.getMessage());
                }
            }
        };

        worker.execute();
    }
}
