/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui.tabbedui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JApplet;
import javax.swing.JPanel;
import org.jfree.ui.tabbedui.AbstractTabbedUI;

public class TabbedApplet
extends JApplet {
    private AbstractTabbedUI tabbedUI;

    protected final AbstractTabbedUI getTabbedUI() {
        return this.tabbedUI;
    }

    public void init(AbstractTabbedUI tabbedUI) {
        this.tabbedUI = tabbedUI;
        this.tabbedUI.addPropertyChangeListener("jMenuBar", new MenuBarChangeListener());
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add((Component)tabbedUI, "Center");
        this.setContentPane(panel);
        this.setJMenuBar(tabbedUI.getJMenuBar());
    }

    private class MenuBarChangeListener
    implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("jMenuBar")) {
                TabbedApplet.this.setJMenuBar(TabbedApplet.this.getTabbedUI().getJMenuBar());
            }
        }
    }
}

