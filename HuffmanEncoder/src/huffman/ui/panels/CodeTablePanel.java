package huffman.ui.panels;

import huffman.ui.components.DarkButton;
import huffman.ui.components.SlimScrollBarUI;
import huffman.ui.components.ToastNotification;
import huffman.ui.theme.AppTheme;
import huffman.util.ClipboardUtil;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class CodeTablePanel extends JPanel {

    private final DefaultTableModel model;
    private final JTable table;
    private final JFrame owner;

    public CodeTablePanel(JFrame owner) {
        this.owner = owner;
        setOpaque(false);
        setLayout(new BorderLayout(0, 6));

        String[] cols = {"Char", "Display", "Frequency", "Code", "Code Length", "Contribution"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }

            public Class<?> getColumnClass(int c) {
                return c == 2 || c == 4 ? Integer.class : String.class;
            }
        };

        table = new JTable(model);
        styleTable();

        // App toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        toolbar.setOpaque(false);
        DarkButton btnCopyCsv = new DarkButton("Export CSV", DarkButton.Style.GHOST);
        DarkButton btnCopyAll = new DarkButton("Copy Table", DarkButton.Style.GHOST);

        JLabel info = new JLabel("Click column header to sort");
        info.setForeground(AppTheme.TEXT_MUTED);
        info.setFont(AppTheme.FONT_UI.deriveFont(11f));
        toolbar.add(btnCopyCsv);
        toolbar.add(btnCopyAll);
        toolbar.add(info);

        btnCopyCsv.addActionListener(e -> exportCsv());
        btnCopyAll.addActionListener(e -> copyTable());

        JScrollPane sp = new JScrollPane(table);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getVerticalScrollBar().setUI(new SlimScrollBarUI());

        add(toolbar, BorderLayout.NORTH);
        add(sp, BorderLayout.CENTER);
    }

    private void styleTable() {
        table.setBackground(AppTheme.BG_CARD);
        table.setForeground(AppTheme.TEXT_PRIMARY);
        table.setSelectionBackground(new Color(0, 229, 255, 48));
        table.setSelectionForeground(AppTheme.ACCENT);
        table.setGridColor(AppTheme.BORDER);
        table.setRowHeight(28);
        table.setFont(AppTheme.FONT_MONO.deriveFont(12f));
        table.setIntercellSpacing(new Dimension(6, 2));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setAutoCreateRowSorter(true);

        JTableHeader header = new JTableHeader();
        header.setBackground(AppTheme.BG_BASE);
        header.setForeground(AppTheme.ACCENT);
        header.setFont(AppTheme.FONT_UI.deriveFont(Font.BOLD, 11f));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER));

        // Center renderer
        DefaultTableCellRenderer centerR = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setHorizontalAlignment(col == 3 ? LEFT : CENTER);
                if (sel) {
                    setBackground(new Color(0, 229, 255, 48));
                    setForeground(col == 3 ? AppTheme.ACCENT2 : AppTheme.ACCENT);
                } else {
                    setBackground(row % 2 == 0 ? AppTheme.BG_CARD : AppTheme.BG_INPUT);
                    setForeground(col == 3 ? AppTheme.ACCENT2 : (col == 0 ? AppTheme.ACCENT : AppTheme.TEXT_PRIMARY));
                }
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return this;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerR);
        }

        // Column width values
        int[] widths = {50, 70, 90, 160, 100, 110};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    public void setData(Map<Character, String> codes, Map<Character, Integer> freq) {
        model.setRowCount(0);
        int total = freq.values().stream().mapToInt(Integer::intValue).sum();
        for (var e : codes.entrySet()) {
            char ch = e.getKey();
            String code = e.getValue();
            int f = freq.getOrDefault(ch, 0);
            double contrib = total > 0 ? (double)f / total * 100 : 0;
            model.addRow(new Object[]{
                    ch, displayChar(ch), f, code, code.length(), String.format("%.2f%%", contrib)
            });
        }
    }

    private String displayChar(char c) {
        return switch (c) {
            case ' ' -> "SPACE";
            case '\n' -> "NEWLINE";
            case '\t' -> "TAB";
            default -> String.valueOf(c);
        };
    }

    private void exportCsv() {
        StringBuilder sb = new StringBuilder("Char,Display,Frequency,Code,Code Length,Contribution\n");
        for (int r = 0; r < model.getRowCount(); r++) {
            for (int c = 0; c < model.getColumnCount(); c++) {
                if (c > 0) sb.append(',');
                sb.append(model.getValueAt(r, c));
            }
            sb.append('\n');
        }
        ClipboardUtil.copy(sb.toString());
        ToastNotification.show(owner, "CSV copied to clipboad!", ToastNotification.Type.SUCCESS);
    }

    private void copyTable() {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < model.getRowCount(); r++) {
            sb.append(model.getValueAt(r, 1)).append("\t")
                .append(model.getValueAt(r, 2)).append("\t")
                .append(model.getValueAt(r, 3)).append("\n");
        }
        ClipboardUtil.copy(sb.toString());
        ToastNotification.show(owner, "Table copied!", ToastNotification.Type.INFO);
    }
}
