/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import org.jfree.ui.Drawable;
import org.jfree.ui.ExtendedDrawable;

public class DrawablePanel
extends JPanel {
    private Drawable drawable;

    public DrawablePanel() {
        this.setOpaque(false);
    }

    public Drawable getDrawable() {
        return this.drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
        this.revalidate();
        this.repaint();
    }

    public Dimension getPreferredSize() {
        if (this.drawable instanceof ExtendedDrawable) {
            ExtendedDrawable ed = (ExtendedDrawable)this.drawable;
            return ed.getPreferredSize();
        }
        return super.getPreferredSize();
    }

    public Dimension getMinimumSize() {
        if (this.drawable instanceof ExtendedDrawable) {
            ExtendedDrawable ed = (ExtendedDrawable)this.drawable;
            return ed.getPreferredSize();
        }
        return super.getMinimumSize();
    }

    public boolean isOpaque() {
        if (this.drawable == null) {
            return false;
        }
        return super.isOpaque();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.drawable == null) {
            return;
        }
        Graphics2D g2 = (Graphics2D)g.create(0, 0, this.getWidth(), this.getHeight());
        this.drawable.draw(g2, new Rectangle2D.Double(0.0, 0.0, this.getWidth(), this.getHeight()));
        g2.dispose();
    }
}

