package huffman.ui;

import huffman.ui.panels.CodeTablePanel;
import huffman.ui.panels.DecoderPanel;
import huffman.ui.panels.EncoderPanel;
import huffman.ui.panels.FilePanel;
import huffman.ui.panels.FrequencyPanel;
import huffman.ui.panels.VisualizerPanel;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import huffman.ui.theme.AppTheme;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.Icon;

public class MainWindow extends JFrame {

    public MainWindow() {
        super("Huffman Encoder");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 820);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setBackground(AppTheme.BG_BASE);
        setIconImage(createWindowIcon());

        // Custom title bar
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AppTheme.BG_BASE);
        setContentPane(root);

        // Header
        root.add(buildHeader(), BorderLayout.NORTH);

        // Different panels
        VisualizerPanel vis = new VisualizerPanel();
        FrequencyPanel freq = new FrequencyPanel();
        CodeTablePanel code = new CodeTablePanel(this);

        EncoderPanel encoder = new EncoderPanel(this, vis, freq, code);
        DecoderPanel decoder = new DecoderPanel(this, encoder);
        FilePanel filePane = new FilePanel(this, encoder);

        // Tabs
        JTabbedPane tabs = buildTabs();
        tabs.addTab("Encoder", encoder);
        tabs.addTab("Decoder", decoder);
        tabs.addTab("Tree View", vis.withControls());
        tabs.addTab("Frequencies", freq.inScrollPane());
        tabs.addTab("Code Table", code);
        tabs.addTab("File I/O", filePane);

        Color[] tabColors = {AppTheme.ACCENT, AppTheme.ACCENT2, AppTheme.SUCCESS, AppTheme.WARNING, AppTheme.ACCENT2, AppTheme.ACCENT};

        for (int i = 0; i < tabs.getTabCount(); i++) {
            final Color tc = tabColors[i];
            final int ti = i;
            tabs.setIconAt(i, new Icon() {
                public int getIconWidth() { return 16; }
                public int getIconHeight() { return 16; }
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(tc);
                    switch (ti) {
                        case 0 -> { // Encoder: lightning bolt
                            g2.fillPolygon(new int[]{x+10, x+4, x+8}, new int[]{y+1, y+8, y+8}, 3);
                            g2.fillPolygon(new int[]{x+6, x+10, x+4}, new int[]{y+8, y+8, y+15}, 3);
                        }
                        case 1 -> { // Decoder: padlock
                            g2.setStroke(new BasicStroke(2f));
                            g2.drawArc(x+4, y+1, 8, 7, 0, 180);
                            g2.fillRoundRect(x+3, y+7, 10, 7, 3, 3);
                        }
                        case 2 -> { // Tree view: tree
                            g2.fillPolygon(new int[]{x+8, x+3, x+13}, new int[]{y+2, y+11, y+11}, 3);
                            g2.fillRect(x+7, y+11, 2, 3);
                        }
                        case 3 -> { // Frequencies: bar chart
                            g2.fillRect(x+2, y+10, 3, 4);
                            g2.fillRect(x+6, y+6, 3, 8);
                            g2.fillRect(x+10, y+2, 3, 12);
                        }
                        case 4 -> { // Code Table: lines
                            for (int r = 0; r < 4; r++)
                                g2.fillRoundRect(x+2, y+2+r*3, 12, 2, 1, 1);
                        }
                        case 5 -> { // File I/O: folder
                            g2.fillRoundRect(x+1, y+5, 14, 9, 3, 3);
                            g2.fillRoundRect(x+1, y+3, 6, 4, 2, 2);
                        }
                    }
                    g2.dispose();
                }
            });
        }

        tabs.addChangeListener(e -> {
            for (int i = 0; i < tabs.getTabCount(); i++)
                tabs.setForegroundAt(i, i == tabs.getSelectedIndex() ? AppTheme.TEXT_PRIMARY : AppTheme.TEXT_SECONDARY);        
        });

        tabs.setForegroundAt(0, AppTheme.TEXT_PRIMARY);

        root.add(tabs, BorderLayout.CENTER);
        root.add(buildStatusBar(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Accent line at the bottom
                GradientPaint gp = new GradientPaint(0, getHeight() - 2, AppTheme.ACCENT, getWidth() / 2, getHeight() / 2, AppTheme.ACCENT2);
                g2.setPaint(gp);
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
                g2.dispose();
            }
        };
    }
}
