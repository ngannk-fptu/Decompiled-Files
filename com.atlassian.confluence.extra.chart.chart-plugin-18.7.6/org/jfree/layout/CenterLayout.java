/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.io.Serializable;

public class CenterLayout
implements LayoutManager,
Serializable {
    private static final long serialVersionUID = 469319532333015042L;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Dimension preferredLayoutSize(Container parent) {
        Object object = parent.getTreeLock();
        synchronized (object) {
            Insets insets = parent.getInsets();
            if (parent.getComponentCount() > 0) {
                Component component = parent.getComponent(0);
                Dimension d = component.getPreferredSize();
                return new Dimension((int)d.getWidth() + insets.left + insets.right, (int)d.getHeight() + insets.top + insets.bottom);
            }
            return new Dimension(insets.left + insets.right, insets.top + insets.bottom);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Dimension minimumLayoutSize(Container parent) {
        Object object = parent.getTreeLock();
        synchronized (object) {
            Insets insets = parent.getInsets();
            if (parent.getComponentCount() > 0) {
                Component component = parent.getComponent(0);
                Dimension d = component.getMinimumSize();
                return new Dimension(d.width + insets.left + insets.right, d.height + insets.top + insets.bottom);
            }
            return new Dimension(insets.left + insets.right, insets.top + insets.bottom);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void layoutContainer(Container parent) {
        Object object = parent.getTreeLock();
        synchronized (object) {
            if (parent.getComponentCount() > 0) {
                Insets insets = parent.getInsets();
                Dimension parentSize = parent.getSize();
                Component component = parent.getComponent(0);
                Dimension componentSize = component.getPreferredSize();
                int xx = insets.left + Math.max((parentSize.width - insets.left - insets.right - componentSize.width) / 2, 0);
                int yy = insets.top + Math.max((parentSize.height - insets.top - insets.bottom - componentSize.height) / 2, 0);
                component.setBounds(xx, yy, componentSize.width, componentSize.height);
            }
        }
    }

    public void addLayoutComponent(Component comp) {
    }

    public void removeLayoutComponent(Component comp) {
    }

    public void addLayoutComponent(String name, Component comp) {
    }

    public void removeLayoutComponent(String name, Component comp) {
    }
}

