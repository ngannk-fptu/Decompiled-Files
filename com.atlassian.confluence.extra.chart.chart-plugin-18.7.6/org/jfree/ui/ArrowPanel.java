/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;

public class ArrowPanel
extends JPanel {
    public static final int UP = 0;
    public static final int DOWN = 1;
    private int type = 0;
    private Rectangle2D available = new Rectangle2D.Float();

    public ArrowPanel(int type) {
        this.type = type;
        this.setPreferredSize(new Dimension(14, 9));
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        Dimension size = this.getSize();
        Insets insets = this.getInsets();
        this.available.setRect(insets.left, insets.top, size.getWidth() - (double)insets.left - (double)insets.right, size.getHeight() - (double)insets.top - (double)insets.bottom);
        g2.translate(insets.left, insets.top);
        g2.fill(this.getArrow(this.type));
    }

    private Shape getArrow(int t) {
        switch (t) {
            case 0: {
                return this.getUpArrow();
            }
            case 1: {
                return this.getDownArrow();
            }
        }
        return this.getUpArrow();
    }

    private Shape getUpArrow() {
        Polygon result = new Polygon();
        result.addPoint(7, 2);
        result.addPoint(2, 7);
        result.addPoint(12, 7);
        return result;
    }

    private Shape getDownArrow() {
        Polygon result = new Polygon();
        result.addPoint(7, 7);
        result.addPoint(2, 2);
        result.addPoint(12, 2);
        return result;
    }
}

