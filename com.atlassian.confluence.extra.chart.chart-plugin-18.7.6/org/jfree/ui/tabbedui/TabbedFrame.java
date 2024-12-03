/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui.tabbedui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jfree.ui.tabbedui.AbstractTabbedUI;

public class TabbedFrame
extends JFrame {
    private AbstractTabbedUI tabbedUI;

    public TabbedFrame() {
    }

    public TabbedFrame(String title) {
        super(title);
    }

    protected final AbstractTabbedUI getTabbedUI() {
        return this.tabbedUI;
    }

    public void init(AbstractTabbedUI tabbedUI) {
        this.tabbedUI = tabbedUI;
        this.tabbedUI.addPropertyChangeListener("jMenuBar", new MenuBarChangeListener());
        this.addWindowListener(new WindowAdapter(){

            public void windowClosing(WindowEvent e) {
                TabbedFrame.this.getTabbedUI().getCloseAction().actionPerformed(new ActionEvent(this, 1001, null, 0));
            }
        });
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
                TabbedFrame.this.setJMenuBar(TabbedFrame.this.getTabbedUI().getJMenuBar());
            }
        }
    }
}

