/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui.tabbedui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jfree.ui.tabbedui.RootEditor;
import org.jfree.util.Log;

public abstract class AbstractTabbedUI
extends JComponent {
    public static final String JMENUBAR_PROPERTY = "jMenuBar";
    public static final String GLOBAL_MENU_PROPERTY = "globalMenu";
    private ArrayList rootEditors;
    private JTabbedPane tabbedPane;
    private int selectedRootEditor = -1;
    private JComponent currentToolbar;
    private JPanel toolbarContainer = new JPanel();
    private Action closeAction;
    private JMenuBar jMenuBar;
    private boolean globalMenu;

    public AbstractTabbedUI() {
        this.toolbarContainer.setLayout(new BorderLayout());
        this.tabbedPane = new JTabbedPane(3);
        this.tabbedPane.addChangeListener(new TabChangeHandler(this.tabbedPane));
        this.rootEditors = new ArrayList();
        this.setLayout(new BorderLayout());
        this.add((Component)this.toolbarContainer, "North");
        this.add((Component)this.tabbedPane, "Center");
        this.closeAction = this.createCloseAction();
    }

    protected JTabbedPane getTabbedPane() {
        return this.tabbedPane;
    }

    public boolean isGlobalMenu() {
        return this.globalMenu;
    }

    public void setGlobalMenu(boolean globalMenu) {
        this.globalMenu = globalMenu;
        if (this.isGlobalMenu()) {
            this.setJMenuBar(this.updateGlobalMenubar());
        } else if (this.getRootEditorCount() > 0) {
            this.setJMenuBar(this.createEditorMenubar(this.getRootEditor(this.getSelectedEditor())));
        }
    }

    public JMenuBar getJMenuBar() {
        return this.jMenuBar;
    }

    protected void setJMenuBar(JMenuBar menuBar) {
        JMenuBar oldMenuBar = this.jMenuBar;
        this.jMenuBar = menuBar;
        this.firePropertyChange(JMENUBAR_PROPERTY, oldMenuBar, menuBar);
    }

    protected Action createCloseAction() {
        return new ExitAction();
    }

    public Action getCloseAction() {
        return this.closeAction;
    }

    protected abstract JMenu[] getPrefixMenus();

    protected abstract JMenu[] getPostfixMenus();

    private void addMenus(JMenuBar menuBar, JMenu[] customMenus) {
        for (int i = 0; i < customMenus.length; ++i) {
            menuBar.add(customMenus[i]);
        }
    }

    private JMenuBar updateGlobalMenubar() {
        JMenuBar menuBar = this.getJMenuBar();
        if (menuBar == null) {
            menuBar = new JMenuBar();
        } else {
            menuBar.removeAll();
        }
        this.addMenus(menuBar, this.getPrefixMenus());
        for (int i = 0; i < this.rootEditors.size(); ++i) {
            RootEditor editor = (RootEditor)this.rootEditors.get(i);
            this.addMenus(menuBar, editor.getMenus());
        }
        this.addMenus(menuBar, this.getPostfixMenus());
        return menuBar;
    }

    private JMenuBar createEditorMenubar(RootEditor root) {
        JMenuBar menuBar = this.getJMenuBar();
        if (menuBar == null) {
            menuBar = new JMenuBar();
        } else {
            menuBar.removeAll();
        }
        this.addMenus(menuBar, this.getPrefixMenus());
        if (this.isGlobalMenu()) {
            for (int i = 0; i < this.rootEditors.size(); ++i) {
                RootEditor editor = (RootEditor)this.rootEditors.get(i);
                this.addMenus(menuBar, editor.getMenus());
            }
        } else {
            this.addMenus(menuBar, root.getMenus());
        }
        this.addMenus(menuBar, this.getPostfixMenus());
        return menuBar;
    }

    public void addRootEditor(RootEditor rootPanel) {
        this.rootEditors.add(rootPanel);
        this.tabbedPane.add(rootPanel.getEditorName(), rootPanel.getMainPanel());
        rootPanel.addPropertyChangeListener("enabled", new TabEnableChangeListener());
        this.updateRootEditorEnabled(rootPanel);
        if (this.getRootEditorCount() == 1) {
            this.setSelectedEditor(0);
        } else if (this.isGlobalMenu()) {
            this.setJMenuBar(this.updateGlobalMenubar());
        }
    }

    public int getRootEditorCount() {
        return this.rootEditors.size();
    }

    public RootEditor getRootEditor(int pos) {
        return (RootEditor)this.rootEditors.get(pos);
    }

    public int getSelectedEditor() {
        return this.selectedRootEditor;
    }

    public void setSelectedEditor(int selectedEditor) {
        RootEditor container;
        boolean shouldBeActive;
        int i;
        int oldEditor = this.selectedRootEditor;
        if (oldEditor == selectedEditor) {
            return;
        }
        this.selectedRootEditor = selectedEditor;
        for (i = 0; i < this.rootEditors.size(); ++i) {
            shouldBeActive = i == selectedEditor;
            container = (RootEditor)this.rootEditors.get(i);
            if (!container.isActive() || shouldBeActive) continue;
            container.setActive(false);
        }
        if (this.currentToolbar != null) {
            this.closeToolbar();
            this.toolbarContainer.removeAll();
            this.currentToolbar = null;
        }
        for (i = 0; i < this.rootEditors.size(); ++i) {
            shouldBeActive = i == selectedEditor;
            container = (RootEditor)this.rootEditors.get(i);
            if (container.isActive() || !shouldBeActive) continue;
            container.setActive(true);
            this.setJMenuBar(this.createEditorMenubar(container));
            this.currentToolbar = container.getToolbar();
            if (this.currentToolbar != null) {
                this.toolbarContainer.add((Component)this.currentToolbar, "Center");
                this.toolbarContainer.setVisible(true);
                this.currentToolbar.setVisible(true);
            } else {
                this.toolbarContainer.setVisible(false);
            }
            this.getJMenuBar().repaint();
        }
    }

    private void closeToolbar() {
        if (this.currentToolbar != null) {
            Window w;
            if (this.currentToolbar.getParent() != this.toolbarContainer && (w = SwingUtilities.windowForComponent(this.currentToolbar)) != null) {
                w.setVisible(false);
                w.dispose();
            }
            this.currentToolbar.setVisible(false);
        }
    }

    protected abstract void attempExit();

    protected void updateRootEditorEnabled(RootEditor editor) {
        boolean enabled = editor.isEnabled();
        for (int i = 0; i < this.tabbedPane.getTabCount(); ++i) {
            Component tab = this.tabbedPane.getComponentAt(i);
            if (tab != editor.getMainPanel()) continue;
            this.tabbedPane.setEnabledAt(i, enabled);
            return;
        }
    }

    private class TabEnableChangeListener
    implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            if (!evt.getPropertyName().equals("enabled")) {
                Log.debug("PropertyName");
                return;
            }
            if (!(evt.getSource() instanceof RootEditor)) {
                Log.debug("Source");
                return;
            }
            RootEditor editor = (RootEditor)evt.getSource();
            AbstractTabbedUI.this.updateRootEditorEnabled(editor);
        }
    }

    private class TabChangeHandler
    implements ChangeListener {
        private final JTabbedPane pane;

        public TabChangeHandler(JTabbedPane pane) {
            this.pane = pane;
        }

        public void stateChanged(ChangeEvent e) {
            AbstractTabbedUI.this.setSelectedEditor(this.pane.getSelectedIndex());
        }
    }

    protected class ExitAction
    extends AbstractAction {
        public ExitAction() {
            this.putValue("Name", "Exit");
        }

        public void actionPerformed(ActionEvent e) {
            AbstractTabbedUI.this.attempExit();
        }
    }
}

