/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui.tabbedui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;

public class VerticalLayout
implements LayoutManager {
    private final boolean useSizeFromParent;

    public VerticalLayout() {
        this(true);
    }

    public VerticalLayout(boolean useParent) {
        this.useSizeFromParent = useParent;
    }

    public void addLayoutComponent(String name, Component comp) {
    }

    public void removeLayoutComponent(Component comp) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Dimension preferredLayoutSize(Container parent) {
        Object object = parent.getTreeLock();
        synchronized (object) {
            Insets ins = parent.getInsets();
            Component[] comps = parent.getComponents();
            int height = ins.top + ins.bottom;
            int width = ins.left + ins.right;
            for (int i = 0; i < comps.length; ++i) {
                if (!comps[i].isVisible()) continue;
                Dimension pref = comps[i].getPreferredSize();
                height += pref.height;
                if (pref.width <= width) continue;
                width = pref.width;
            }
            return new Dimension(width + ins.left + ins.right, height + ins.top + ins.bottom);
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
            int height = ins.top + ins.bottom;
            int width = ins.left + ins.right;
            for (int i = 0; i < comps.length; ++i) {
                if (!comps[i].isVisible()) continue;
                Dimension min = comps[i].getMinimumSize();
                height += min.height;
                if (min.width <= width) continue;
                width = min.width;
            }
            return new Dimension(width + ins.left + ins.right, height + ins.top + ins.bottom);
        }
    }

    public boolean isUseSizeFromParent() {
        return this.useSizeFromParent;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void layoutContainer(Container parent) {
        Object object = parent.getTreeLock();
        synchronized (object) {
            int width;
            Insets ins = parent.getInsets();
            int insHorizontal = ins.left + ins.right;
            if (this.isUseSizeFromParent()) {
                Rectangle bounds = parent.getBounds();
                width = bounds.width - insHorizontal;
            } else {
                width = this.preferredLayoutSize((Container)parent).width - insHorizontal;
            }
            Component[] comps = parent.getComponents();
            int y = ins.top;
            for (int i = 0; i < comps.length; ++i) {
                Component c = comps[i];
                if (!c.isVisible()) continue;
                Dimension dim = c.getPreferredSize();
                c.setBounds(ins.left, y, width, dim.height);
                y += dim.height;
            }
        }
    }
}

