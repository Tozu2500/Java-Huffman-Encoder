package huffman.ui;

import huffman.ui.components.GlowLabel;
import huffman.ui.panels.CodeTablePanel;
import huffman.ui.panels.DecoderPanel;
import huffman.ui.panels.EncoderPanel;
import huffman.ui.panels.FilePanel;
import huffman.ui.panels.FrequencyPanel;
import huffman.ui.panels.VisualizerPanel;
import huffman.ui.theme.AppTheme;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

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

        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        // Logo area
        JPanel logoArea = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        logoArea.setOpaque(false);

        JLabel logo = new GlowLabel("⟨H⟩ HUFFMAN", AppTheme.ACCENT);
        logo.setFont(AppTheme.FONT_TITLE.deriveFont(Font.BOLD, 22f));

        JLabel subtitle = new JLabel("Lossless Compression Studio");
        subtitle.setForeground(AppTheme.TEXT_SECONDARY);
        subtitle.setFont(AppTheme.FONT_UI.deriveFont(12f));
        
        logoArea.add(logo);
        logoArea.add(Box.createHorizontalStrut(0));
        logoArea.add(subtitle);

        // Right info
        JPanel rightInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        rightInfo.setOpaque(false);

        String[] badges = {"Algorithm: Huffman", "Lossless", "UTF-8"};
        Color[] badgeColors = {AppTheme.ACCENT, AppTheme.SUCCESS, AppTheme.ACCENT2};

        for (int i = 0; i < badges.length; i++) {
            rightInfo.add(makeBadge(badges[i], badgeColors[i]));
        }

        header.add(logoArea, BorderLayout.WEST);
        header.add(rightInfo, BorderLayout.EAST);
        return header;
    }

    private JLabel makeBadge(String text, Color col) {
        JLabel lbl = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(col.getRed(), col.getGreen(), col.getBlue(), 30));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(col);
                g2.setStroke(new BasicStroke(1));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }

            public boolean isOpaque() {
                return false;
            }
        };

        lbl.setFont(AppTheme.FONT_UI.deriveFont(10f));
        lbl.setForeground(col);
        lbl.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));

        return lbl;
    }

    private JTabbedPane buildTabs() {
        JTabbedPane tb = new JTabbedPane();
        tb.setOpaque(false);
        tb.setBackground(AppTheme.BG_BASE);
        tb.setForeground(AppTheme.TEXT_SECONDARY);
        tb.setFont(AppTheme.FONT_UI.deriveFont(13f));
        tb.setBorder(BorderFactory.createEmptyBorder());
        tb.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        tb.setUI(new BasicTabbedPaneUI() {
            @Override
            protected void installDefaults() {
                super.installDefaults();
                highlight = AppTheme.BG_BASE;
                lightHighlight = AppTheme.BG_BASE;
                shadow = AppTheme.BG_BASE;
                darkShadow = AppTheme.BG_BASE;
                focus = AppTheme.BG_BASE;
            }

            protected int getTabLabelShiftY() {
                return 0;
            }

            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (isSelected) {
                    g2.setColor(AppTheme.BG_PANEL);
                    g2.fillRoundRect(x, y, w, h, 8, 8);
                    GradientPaint gp = new GradientPaint(x, y+h-2, AppTheme.ACCENT, x+w/2, y+h-2, AppTheme.ACCENT2);
                    g2.setPaint(gp);
                    g2.fillRect(x+4, y+h-2, w-8, 2);
                } else {
                    g2.setColor(AppTheme.BG_BASE);
                    g2.fillRect(x, y, w, h);
                }
                g2.dispose();
            }

            @Override
            protected void paintTabBorder(Graphics g, int tp2, int ti, int x, int y, int w, int h, boolean sel) {}

            @Override
            protected void paintFocusIndicator(Graphics g, int tp2, Rectangle[] rs, int ti, Rectangle ir, Rectangle tr, boolean sel) {}

            @Override
            protected void paintContentBorder(Graphics g, int tp2, int si) {
                g.setColor(AppTheme.BORDER);
                g.fillRect(0, calculateTabAreaHeight(tp2, runCount, maxTabHeight) + tabPane.getInsets().top - 1,
                        tabPane.getWidth(), 1);
            }
        });

        return tb;
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 4));
        bar.setBackground(AppTheme.BG_BASE);
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AppTheme.BORDER));

        JLabel lbl = new JLabel("Ready  -  Huffman Encoder  -  Java");
        lbl.setForeground(AppTheme.TEXT_MUTED);
        lbl.setFont(AppTheme.FONT_UI.deriveFont(10f));
        bar.add(lbl);

        return bar;
    }

    // Drawing a 32x32 custom JFrame icon with an H letter
    private Image createWindowIcon() {
        // Set icon size to 32x32 pixels
        int iconSize = 32;
        
        // Create a transparent image with ARGB color model to support transparency
        BufferedImage iconImage = new BufferedImage(iconSize, iconSize, BufferedImage.TYPE_INT_ARGB);
        
        // Get graphics context to draw on the image
        Graphics2D graphics = iconImage.createGraphics();
        
        // Enable high-quality rendering for smooth shapes and text
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Draw background: filled rounded rectangle with panel background color
        graphics.setColor(AppTheme.BG_PANEL);
        graphics.fillRoundRect(0, 0, iconSize, iconSize, 8, 8);
        
        // Draw border: rounded rectangle outline in accent color
        graphics.setColor(AppTheme.ACCENT);
        graphics.setStroke(new BasicStroke(2));
        graphics.drawRoundRect(1, 1, iconSize - 3, iconSize - 3, 8, 8);
        
        // Draw letter: bold "H" centered in the icon
        graphics.setFont(new Font("Segoe UI", Font.BOLD, 22));
        FontMetrics fontMetrics = graphics.getFontMetrics();
        int textX = (iconSize - fontMetrics.stringWidth("H")) / 2;
        int textY = (iconSize - fontMetrics.getHeight()) / 2 + fontMetrics.getAscent();
        graphics.drawString("H", textX, textY);
        
        // Clean up graphics resources
        graphics.dispose();
        
        return iconImage;
    }
}
