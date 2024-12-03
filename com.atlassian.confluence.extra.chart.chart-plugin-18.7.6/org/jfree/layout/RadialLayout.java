/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.layout;

import java.awt.Checkbox;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Panel;
import java.io.Serializable;

public class RadialLayout
implements LayoutManager,
Serializable {
    private static final long serialVersionUID = -7582156799248315534L;
    private int minWidth = 0;
    private int minHeight = 0;
    private int maxCompWidth = 0;
    private int maxCompHeight = 0;
    private int preferredWidth = 0;
    private int preferredHeight = 0;
    private boolean sizeUnknown = true;

    public void addLayoutComponent(Component comp) {
    }

    public void removeLayoutComponent(Component comp) {
    }

    public void addLayoutComponent(String name, Component comp) {
    }

    public void removeLayoutComponent(String name, Component comp) {
    }

    private void setSizes(Container parent) {
        int nComps = parent.getComponentCount();
        this.preferredWidth = 0;
        this.preferredHeight = 0;
        this.minWidth = 0;
        this.minHeight = 0;
        for (int i = 0; i < nComps; ++i) {
            Component c = parent.getComponent(i);
            if (!c.isVisible()) continue;
            Dimension d = c.getPreferredSize();
            if (this.maxCompWidth < d.width) {
                this.maxCompWidth = d.width;
            }
            if (this.maxCompHeight < d.height) {
                this.maxCompHeight = d.height;
            }
            this.preferredWidth += d.width;
            this.preferredHeight += d.height;
        }
        this.preferredWidth /= 2;
        this.preferredHeight /= 2;
        this.minWidth = this.preferredWidth;
        this.minHeight = this.preferredHeight;
    }

    public Dimension preferredLayoutSize(Container parent) {
        Dimension dim = new Dimension(0, 0);
        this.setSizes(parent);
        Insets insets = parent.getInsets();
        dim.width = this.preferredWidth + insets.left + insets.right;
        dim.height = this.preferredHeight + insets.top + insets.bottom;
        this.sizeUnknown = false;
        return dim;
    }

    public Dimension minimumLayoutSize(Container parent) {
        Dimension dim = new Dimension(0, 0);
        Insets insets = parent.getInsets();
        dim.width = this.minWidth + insets.left + insets.right;
        dim.height = this.minHeight + insets.top + insets.bottom;
        this.sizeUnknown = false;
        return dim;
    }

    public void layoutContainer(Container parent) {
        Insets insets = parent.getInsets();
        int maxWidth = parent.getSize().width - (insets.left + insets.right);
        int maxHeight = parent.getSize().height - (insets.top + insets.bottom);
        int nComps = parent.getComponentCount();
        int x = 0;
        int y = 0;
        if (this.sizeUnknown) {
            this.setSizes(parent);
        }
        if (nComps < 2) {
            Component c = parent.getComponent(0);
            if (c.isVisible()) {
                Dimension d = c.getPreferredSize();
                c.setBounds(x, y, d.width, d.height);
            }
        } else {
            double radialCurrent = Math.toRadians(90.0);
            double radialIncrement = Math.PI * 2 / (double)nComps;
            int midX = maxWidth / 2;
            int midY = maxHeight / 2;
            int a = midX - this.maxCompWidth;
            int b = midY - this.maxCompHeight;
            for (int i = 0; i < nComps; ++i) {
                Component c = parent.getComponent(i);
                if (c.isVisible()) {
                    Dimension d = c.getPreferredSize();
                    x = (int)((double)midX - (double)a * Math.cos(radialCurrent) - d.getWidth() / 2.0 + (double)insets.left);
                    y = (int)((double)midY - (double)b * Math.sin(radialCurrent) - d.getHeight() / 2.0 + (double)insets.top);
                    c.setBounds(x, y, d.width, d.height);
                }
                radialCurrent += radialIncrement;
            }
        }
    }

    public String toString() {
        return this.getClass().getName();
    }

    public static void main(String[] args) throws Exception {
        Frame frame = new Frame();
        Panel panel = new Panel();
        panel.setLayout(new RadialLayout());
        panel.add(new Checkbox("One"));
        panel.add(new Checkbox("Two"));
        panel.add(new Checkbox("Three"));
        panel.add(new Checkbox("Four"));
        panel.add(new Checkbox("Five"));
        panel.add(new Checkbox("One"));
        panel.add(new Checkbox("Two"));
        panel.add(new Checkbox("Three"));
        panel.add(new Checkbox("Four"));
        panel.add(new Checkbox("Five"));
        frame.add(panel);
        frame.setSize(300, 500);
        frame.setVisible(true);
    }
}

