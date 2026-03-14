package huffman.ui.theme;

import java.awt.Color;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ThemeManager {

    public enum Theme {
        DARK,
        LIGHT,
    };

    // Singleton
    private static ThemeManager instance;

    public static ThemeManager getInstance() {
        if (instance == null) instance = new ThemeManager();
        return instance;
    }

    private Theme current = Theme.DARK;
    private final List<ChangeListener> listeners = new ArrayList<>();

    private ThemeManager() {}

    // Return the current active theme
    public Theme getCurrent() {
        return current;
    }

    // Returns {@code true} when the dark theme is active
    public boolean isDark() {
        return current == Theme.DARK;
    }

    /* Switches to the given theme, updates UIManager and notifies all
       registered {@link ChangeListeners}s. Does nothing if the theme is
       already active
    */
    public void setTheme(Theme theme) {
        if (this.current == theme) return;
        this.current = theme;
        applyDefaults();
        fireChange();
    }

    // Toggle between {@link Theme#DARK} and {@link Theme#LIGHT}
    public void toggle() {
        setTheme(current == Theme.DARK ? Theme.LIGHT : Theme.DARK);
    }

    public void addChangeListener(ChangeListener l) {
        if (!listeners.contains(l)) listeners.add(l);
    }

    public void removeChangeListener(ChangeListener l) {
        listeners.remove(l);
    }

    public void applyToWindow(Window window) {
        applyDefaults();
        SwingUtilities.updateComponentTreeUI(window);
        window.repaint();
    }

    private void applyDefaults() {
        if (current == Theme.DARK) {
            AppTheme.applyGlobalDefaults();
        } else {
            applyLightDefaults();
        }
    }

    private void applyLightDefaults() {
        Color bg        = new Color(244, 244, 247);
        Color panel     = new Color(255, 255, 255);
        Color card      = new Color(235, 235, 240);
        Color border    = new Color(200, 200, 216);
        Color fg        = new Color(26, 26, 46);
        Color fgSec     = new Color(85, 85, 122);
        Color fgMuted   = new Color(152, 152, 170);

        UIManager.put("Panel.background",             panel);
        UIManager.put("ScrollPane.background",        panel);
        UIManager.put("Viewport.background",          panel);
        UIManager.put("ScrollBar.thumb",              border);
        UIManager.put("ScrollBar.track",              bg);
        UIManager.put("ScrollBar.thumbDarkShadow",    bg);
        UIManager.put("ScrollBar.thumbHighlight",     border);
        UIManager.put("ScrollBar.width",              8);
        UIManager.put("TabbedPane.background",        bg);
        UIManager.put("TabbedPane.foreground",        fg);
        UIManager.put("TabbedPane.selected",          panel);
        UIManager.put("TabbedPane.contentAreaColor",  bg);
        UIManager.put("Table.background",             card);
        UIManager.put("Table.foreground",             fg);
        UIManager.put("Table.gridColor",              border);
        UIManager.put("Table.selectionBackground",    AppTheme.ACCENT.darker());
        UIManager.put("TableHeader.background",       bg);
        UIManager.put("TableHeader.foreground",       AppTheme.ACCENT);
        UIManager.put("ToolTip.background",           card);
        UIManager.put("ToolTip.foreground",           fg);
        UIManager.put("ToolTip.border",               BorderFactory.createLineBorder(border));
        UIManager.put("Label.foreground",             fg);
        UIManager.put("ComboBox.background",          card);
        UIManager.put("ComboBox.foreground",          fg);
        UIManager.put("Separator.background",         border);
        UIManager.put("Separator.foreground",         border);
    }

    private void fireChange() {
        ChangeEvent evt = new ChangeEvent(this);
        for (ChangeListener l : new ArrayList<>(listeners)) {
            l.stateChanged(evt);
        }
    }
}
