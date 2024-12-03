/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;

public final class OverlayLayout
implements LayoutManager {
    private boolean ignoreInvisible;

    public OverlayLayout(boolean ignoreInvisible) {
        this.ignoreInvisible = ignoreInvisible;
    }

    public OverlayLayout() {
    }

    public void addLayoutComponent(String name, Component comp) {
    }

    public void removeLayoutComponent(Component comp) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void layoutContainer(Container parent) {
        Object object = parent.getTreeLock();
        synchronized (object) {
            Insets ins = parent.getInsets();
            Rectangle bounds = parent.getBounds();
            int width = bounds.width - ins.left - ins.right;
            int height = bounds.height - ins.top - ins.bottom;
            Component[] comps = parent.getComponents();
            for (int i = 0; i < comps.length; ++i) {
                Component c = comps[i];
                if (!comps[i].isVisible() && this.ignoreInvisible) continue;
                c.setBounds(ins.left, ins.top, width, height);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Dimension minimumLayoutSize(Container parent) {
        Object object = parent.getTreeLock();
        synchronized (object) {
            Insets ins = parent.getInsets();
            Component[] comps = parent.getComponents();
            int height = 0;
            int width = 0;
            for (int i = 0; i < comps.length; ++i) {
                if (!comps[i].isVisible() && this.ignoreInvisible) continue;
                Dimension pref = comps[i].getMinimumSize();
                if (pref.height > height) {
                    height = pref.height;
                }
                if (pref.width <= width) continue;
                width = pref.width;
            }
            return new Dimension(width + ins.left + ins.right, height + ins.top + ins.bottom);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Dimension preferredLayoutSize(Container parent) {
        Object object = parent.getTreeLock();
        synchronized (object) {
            Insets ins = parent.getInsets();
            Component[] comps = parent.getComponents();
            int height = 0;
            int width = 0;
            for (int i = 0; i < comps.length; ++i) {
                if (!comps[i].isVisible() && this.ignoreInvisible) continue;
                Dimension pref = comps[i].getPreferredSize();
                if (pref.height > height) {
                    height = pref.height;
                }
                if (pref.width <= width) continue;
                width = pref.width;
            }
            return new Dimension(width + ins.left + ins.right, height + ins.top + ins.bottom);
        }
    }
}

