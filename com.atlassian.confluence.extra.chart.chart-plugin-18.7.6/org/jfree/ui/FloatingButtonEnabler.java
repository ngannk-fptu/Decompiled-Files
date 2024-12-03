/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractButton;

public final class FloatingButtonEnabler
extends MouseAdapter {
    private static FloatingButtonEnabler singleton;

    private FloatingButtonEnabler() {
    }

    public static FloatingButtonEnabler getInstance() {
        if (singleton == null) {
            singleton = new FloatingButtonEnabler();
        }
        return singleton;
    }

    public void addButton(AbstractButton button) {
        button.addMouseListener(this);
        button.setBorderPainted(false);
    }

    public void removeButton(AbstractButton button) {
        button.addMouseListener(this);
        button.setBorderPainted(true);
    }

    public void mouseEntered(MouseEvent e) {
        AbstractButton button;
        if (e.getSource() instanceof AbstractButton && (button = (AbstractButton)e.getSource()).isEnabled()) {
            button.setBorderPainted(true);
        }
    }

    public void mouseExited(MouseEvent e) {
        if (e.getSource() instanceof AbstractButton) {
            AbstractButton button = (AbstractButton)e.getSource();
            button.setBorderPainted(false);
            if (button.getParent() != null) {
                button.getParent().repaint();
            }
        }
    }
}

