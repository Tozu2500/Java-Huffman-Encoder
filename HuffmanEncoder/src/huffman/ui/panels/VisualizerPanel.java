package huffman.ui.panels;

import huffman.core.HuffmanNode;
import huffman.ui.components.DarkButton;
import huffman.ui.theme.AppTheme;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class VisualizerPanel extends JPanel {

    private HuffmanNode root;
    private Map<Character, String> codes;
    private float animProgress = 0f;
    private Timer animTimer;
    private double offsetX = 0;
    private double offsetY = 0;
    private double zoom = 1.0;
    private Point dragStart;
    private static final int NODE_R = 22;
    private static final int LEVEL_H = 70;

    // Node positions computed ONCE
    private final Map<HuffmanNode, Point2D.Double> positions = new HashMap<>();
    private int treeWidth = 800;

    public VisualizerPanel() {
        setOpaque(true);
        setBackground(AppTheme.BG_BASE);
        setPreferredSize(new Dimension(900, 500));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragStart = e.getPoint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragStart != null) {
                    offsetX += e.getX() - dragStart.x;
                    offsetY += e.getY() - dragStart.y;
                    dragStart = e.getPoint();
                    repaint();
                }
            }
        });

        // Zooming
        addMouseWheelListener(e -> {
            double factor = e.getWheelRotation() < 0 ? 1.1 : 0.9;
            zoom = Math.max(0.2, Math.min(4.0, zoom * factor));
            repaint();
        });
    }

    public void setTree(HuffmanNode root, Map<Character, String> codes) {
        this.root = root;
        this.codes = codes;
        positions.clear();

        if (root != null) {
            computePositions(root, 0, 0, treeWidth, 0);
        }

        animProgress = 0f;

        if (animTimer != null) {
            animTimer.stop();
        }

        animTimer = new Timer(16, e -> {
            animProgress = Math.min(1f, animProgress + 0.035f);
            repaint();
            if (animProgress >= 1f) {
                ((Timer)e.getSource()).stop();
            }
        });

        animTimer.start();
        resetView();
    }

    private void computePositions(HuffmanNode node, int depth, double xLeft, double xRight, int level) {
        if (node == null) return;
        double x = (xLeft + xRight) / 2.0;
        double y = depth * LEVEL_H + 40;

        positions.put(node, new Point2D.Double(x, y));

        double half = (xRight - xLeft) / 2.0;

        computePositions(node.left, depth + 1, xLeft, x, level);
        computePositions(node.right, depth + 1, x, xRight, level);
    }

    private void resetView() {
        offsetX = 0;
        offsetY = 20;
        zoom = 1.0;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Background grid
        drawGrid(g2);

        if (root == null) {
            g2.setColor(AppTheme.TEXT_MUTED);
            g2.setFont(AppTheme.FONT_UI.deriveFont(14f));
            String msg = "Encode text to visualize the Huffman tree";
            
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(msg, (getWidth() - fm.stringWidth(msg)) / 2, getHeight() / 2);
            g2.dispose();

            return;
        }

        g2.translate(getWidth() / 2.0 + offsetX, 20 + offsetY);
        g2.scale(zoom, zoom);
        g2.translate(-treeWidth / 2.0, 0);

        drawEdges(g2, root);
        drawNodes(g2, root);
        g2.dispose();
    }

    private void drawGrid(Graphics2D g2) {
        g2.setColor(new Color(30, 35, 50));
        int step = 30;

        // Grid lines on the X scale
        for (int x = 0; x < getWidth(); x += step) {
            g2.drawLine(x, 0, x, getHeight());
        }

        // Grid lines on the Y scale
        for (int y = 0; y < getHeight(); y += step) {
            g2.drawLine(0, y, getHeight(), y);
        }
    }

    private void drawEdges(Graphics2D g2, HuffmanNode node) {
        if (node == null) return;
        Point2D.Double pos = positions.get(node);
        if (pos == null) return;

        if (node.left != null) {
            Point2D.Double lp = positions.get(node.left);
            if (lp != null) {
                drawEdge(g2, pos, lp, "0", AppTheme.ACCENT);
                drawEdges(g2, node.left);
            }
        }

        if (node.right != null) {
            Point2D.Double rp = positions.get(node.right);
            if (rp != null) {
                drawEdge(g2, pos, rp, "1", AppTheme.ACCENT2);
                drawEdges(g2, node.right);
            }
        }
    }

    private void drawEdge(Graphics2D g2, Point2D.Double from, Point2D.Double to, String label, Color col) {
        // Animated revealing
        double ax = from.x + (to.x - from.x) * animProgress;
        double ay = from.y + (to.y - from.y) * animProgress;

        g2.setStroke(new BasicStroke(1.5f));
        g2.setColor(new Color(col.getRed(), col.getGreen(), col.getBlue(), 80));
        g2.draw(new Line2D.Double(from.x, from.y, ax, ay));

        // Edge label midpoint
        double mx = (from.x + ax) / 2.0;
        double my = (from.y + ay) / 2.0;
        g2.setFont(AppTheme.FONT_MONO.deriveFont(Font.BOLD, 10f));
        g2.setColor(col);
        g2.drawString(label, (float)(mx - 4), (float)(my));
    }

    private void drawNodes(Graphics2D g2, HuffmanNode node) {
        if (node == null) return;
        Point2D.Double pos = positions.get(node);
        if (pos == null) return;

        boolean isLeaf = node.isLeaf();
        Color fill = isLeaf ? new Color(26, 42, 58) : AppTheme.BG_CARD;
        Color border = isLeaf ? AppTheme.ACCENT : AppTheme.ACCENT2;

        // Glowing effect
        int glowAlpha = (int)(15 * animProgress);
        for (int i = 4; i > 0; i--) {
            g2.setColor(new Color(border.getRed(), border.getGreen(), border.getBlue(),
                    Math.max(0, Math.min(255, glowAlpha))));
            g2.fill(new Ellipse2D.Double(pos.x - NODE_R - i * 2, pos.y - NODE_R - i * 2,
                    (NODE_R + i * 2) * 2, (NODE_R + i * 2) * 2));
        }

        // Fill
        g2.setColor(fill);
        g2.fill(new Ellipse2D.Double(pos.x - NODE_R, pos.y - NODE_R, NODE_R * 2, NODE_R * 2));

        // Border
        g2.setColor(border);
        g2.setStroke(new BasicStroke(1.8f));
        g2.draw(new Ellipse2D.Double(pos.x - NODE_R, pos.y - NODE_R, NODE_R * 2, NODE_R * 2));

        // Text
        g2.setFont(AppTheme.FONT_MONO.deriveFont(Font.BOLD, 10f));
        FontMetrics fm = g2.getFontMetrics();
        String freq = String.valueOf(node.frequency);

        if (isLeaf) {
            String ch = displayChar(node.ch);
            g2.setColor(AppTheme.ACCENT);
            g2.drawString(ch, (float)(pos.x - fm.stringWidth(ch) / 2.0), (float)(pos.y - 2));
            g2.setFont(AppTheme.FONT_UI.deriveFont(8f));
            fm = g2.getFontMetrics();
            g2.setColor(AppTheme.TEXT_SECONDARY);
            g2.drawString(freq, (float)(pos.x - fm.stringWidth(freq) / 2.0), (float)(pos.y + 12));
        } else {
            g2.setColor(AppTheme.ACCENT2);
            g2.drawString(freq, (float)(pos.x - fm.stringWidth(freq) / 2.0), (float)(pos.y + 4));
        }

        drawNodes(g2, node.left);
        drawNodes(g2, node.right);
    }

    private String displayChar(char c) {
        return switch (c) {
            case ' ' -> "SPC";
            case '\n' -> "NL";
            case '\t' -> "TAB";
            default -> String.valueOf(c);
        };
    }

    public JPanel withControls() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        controls.setOpaque(false);

        DarkButton btnReset = new DarkButton("Reset View", DarkButton.Style.GHOST);
        DarkButton btnZoomIn = new DarkButton("+", DarkButton.Style.GHOST);
        DarkButton btnZoomOut = new DarkButton("-", DarkButton.Style.GHOST);

        JLabel zoomLbl = new JLabel("Scroll to zoom --- Drag to pan");
        zoomLbl.setForeground(AppTheme.TEXT_MUTED);
        zoomLbl.setFont(AppTheme.FONT_UI.deriveFont(11f));

        btnReset.addActionListener(e -> resetView());

        btnZoomIn.addActionListener(e -> {
            zoom = Math.min(4.0, zoom * 1.2);
            repaint();
        });

        btnZoomOut.addActionListener(e -> {
            zoom = Math.max(0.2, zoom / 1.2);
            repaint();
        });

        controls.add(btnZoomIn);
        controls.add(btnZoomOut);
        controls.add(btnReset);
        controls.add(zoomLbl);

        wrapper.add(controls, BorderLayout.NORTH);
        wrapper.add(this, BorderLayout.CENTER);
        return wrapper;
    }
}
