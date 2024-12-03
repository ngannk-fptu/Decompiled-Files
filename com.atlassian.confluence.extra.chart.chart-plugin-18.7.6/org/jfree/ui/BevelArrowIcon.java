/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;
import javax.swing.UIManager;

public class BevelArrowIcon
implements Icon {
    public static final int UP = 0;
    public static final int DOWN = 1;
    private static final int DEFAULT_SIZE = 11;
    private Color edge1;
    private Color edge2;
    private Color fill;
    private int size;
    private int direction;

    public BevelArrowIcon(int direction, boolean isRaisedView, boolean isPressedView) {
        if (isRaisedView) {
            if (isPressedView) {
                this.init(UIManager.getColor("controlLtHighlight"), UIManager.getColor("controlDkShadow"), UIManager.getColor("controlShadow"), 11, direction);
            } else {
                this.init(UIManager.getColor("controlHighlight"), UIManager.getColor("controlShadow"), UIManager.getColor("control"), 11, direction);
            }
        } else if (isPressedView) {
            this.init(UIManager.getColor("controlDkShadow"), UIManager.getColor("controlLtHighlight"), UIManager.getColor("controlShadow"), 11, direction);
        } else {
            this.init(UIManager.getColor("controlShadow"), UIManager.getColor("controlHighlight"), UIManager.getColor("control"), 11, direction);
        }
    }

    public BevelArrowIcon(Color edge1, Color edge2, Color fill, int size, int direction) {
        this.init(edge1, edge2, fill, size, direction);
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        switch (this.direction) {
            case 1: {
                this.drawDownArrow(g, x, y);
                break;
            }
            case 0: {
                this.drawUpArrow(g, x, y);
            }
        }
    }

    public int getIconWidth() {
        return this.size;
    }

    public int getIconHeight() {
        return this.size;
    }

    private void init(Color edge1, Color edge2, Color fill, int size, int direction) {
        this.edge1 = edge1;
        this.edge2 = edge2;
        this.fill = fill;
        this.size = size;
        this.direction = direction;
    }

    private void drawDownArrow(Graphics g, int xo, int yo) {
        g.setColor(this.edge1);
        g.drawLine(xo, yo, xo + this.size - 1, yo);
        g.drawLine(xo, yo + 1, xo + this.size - 3, yo + 1);
        g.setColor(this.edge2);
        g.drawLine(xo + this.size - 2, yo + 1, xo + this.size - 1, yo + 1);
        int x = xo + 1;
        int y = yo + 2;
        int dx = this.size - 6;
        while (y + 1 < yo + this.size) {
            g.setColor(this.edge1);
            g.drawLine(x, y, x + 1, y);
            g.drawLine(x, y + 1, x + 1, y + 1);
            if (0 < dx) {
                g.setColor(this.fill);
                g.drawLine(x + 2, y, x + 1 + dx, y);
                g.drawLine(x + 2, y + 1, x + 1 + dx, y + 1);
            }
            g.setColor(this.edge2);
            g.drawLine(x + dx + 2, y, x + dx + 3, y);
            g.drawLine(x + dx + 2, y + 1, x + dx + 3, y + 1);
            ++x;
            y += 2;
            dx -= 2;
        }
        g.setColor(this.edge1);
        g.drawLine(xo + this.size / 2, yo + this.size - 1, xo + this.size / 2, yo + this.size - 1);
    }

    private void drawUpArrow(Graphics g, int xo, int yo) {
        g.setColor(this.edge1);
        int x = xo + this.size / 2;
        g.drawLine(x, yo, x, yo);
        --x;
        int y = yo + 1;
        int dx = 0;
        while (y + 3 < yo + this.size) {
            g.setColor(this.edge1);
            g.drawLine(x, y, x + 1, y);
            g.drawLine(x, y + 1, x + 1, y + 1);
            if (0 < dx) {
                g.setColor(this.fill);
                g.drawLine(x + 2, y, x + 1 + dx, y);
                g.drawLine(x + 2, y + 1, x + 1 + dx, y + 1);
            }
            g.setColor(this.edge2);
            g.drawLine(x + dx + 2, y, x + dx + 3, y);
            g.drawLine(x + dx + 2, y + 1, x + dx + 3, y + 1);
            --x;
            y += 2;
            dx += 2;
        }
        g.setColor(this.edge1);
        g.drawLine(xo, yo + this.size - 3, xo + 1, yo + this.size - 3);
        g.setColor(this.edge2);
        g.drawLine(xo + 2, yo + this.size - 2, xo + this.size - 1, yo + this.size - 2);
        g.drawLine(xo, yo + this.size - 1, xo + this.size, yo + this.size - 1);
    }
}

