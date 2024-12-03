/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.text.JTextComponent;

public final class JTextObserver
implements FocusListener {
    private static JTextObserver singleton;

    private JTextObserver() {
    }

    public static JTextObserver getInstance() {
        if (singleton == null) {
            singleton = new JTextObserver();
        }
        return singleton;
    }

    public void focusGained(FocusEvent e) {
        if (e.getSource() instanceof JTextComponent) {
            JTextComponent tex = (JTextComponent)e.getSource();
            tex.selectAll();
        }
    }

    public void focusLost(FocusEvent e) {
        if (e.getSource() instanceof JTextComponent) {
            JTextComponent tex = (JTextComponent)e.getSource();
            tex.select(0, 0);
        }
    }

    public static void addTextComponent(JTextComponent t) {
        if (singleton == null) {
            singleton = new JTextObserver();
        }
        t.addFocusListener(singleton);
    }

    public static void removeTextComponent(JTextComponent t) {
        if (singleton == null) {
            singleton = new JTextObserver();
        }
        t.removeFocusListener(singleton);
    }
}

