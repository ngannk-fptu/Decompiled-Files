/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.widget;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import javax.swing.JPanel;

public class DisplayJAI
extends JPanel
implements MouseListener,
MouseMotionListener {
    protected RenderedImage source = null;
    protected int originX = 0;
    protected int originY = 0;

    public DisplayJAI() {
        this.setLayout(null);
    }

    public DisplayJAI(RenderedImage image) {
        this.setLayout(null);
        if (image == null) {
            throw new IllegalArgumentException("image == null!");
        }
        this.source = image;
        int w = this.source.getWidth();
        int h = this.source.getHeight();
        Insets insets = this.getInsets();
        Dimension dim = new Dimension(w + insets.left + insets.right, h + insets.top + insets.bottom);
        this.setPreferredSize(dim);
    }

    public void setOrigin(int x, int y) {
        this.originX = x;
        this.originY = y;
        this.repaint();
    }

    public Point getOrigin() {
        return new Point(this.originX, this.originY);
    }

    public void set(RenderedImage im) {
        if (im == null) {
            throw new IllegalArgumentException("im == null!");
        }
        this.source = im;
        int w = this.source.getWidth();
        int h = this.source.getHeight();
        Insets insets = this.getInsets();
        Dimension dim = new Dimension(w + insets.left + insets.right, h + insets.top + insets.bottom);
        this.setPreferredSize(dim);
        this.revalidate();
        this.repaint();
    }

    public void set(RenderedImage im, int x, int y) {
        if (im == null) {
            throw new IllegalArgumentException("im == null!");
        }
        this.source = im;
        int w = this.source.getWidth();
        int h = this.source.getHeight();
        Insets insets = this.getInsets();
        Dimension dim = new Dimension(w + insets.left + insets.right, h + insets.top + insets.bottom);
        this.setPreferredSize(dim);
        this.originX = x;
        this.originY = y;
        this.revalidate();
        this.repaint();
    }

    public RenderedImage getSource() {
        return this.source;
    }

    public synchronized void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        if (this.source == null) {
            g2d.setColor(this.getBackground());
            g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
            return;
        }
        Rectangle clipBounds = g2d.getClipBounds();
        g2d.setColor(this.getBackground());
        g2d.fillRect(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height);
        Insets insets = this.getInsets();
        int tx = insets.left + this.originX;
        int ty = insets.top + this.originY;
        try {
            g2d.drawRenderedImage(this.source, AffineTransform.getTranslateInstance(tx, ty));
        }
        catch (OutOfMemoryError e) {
            // empty catch block
        }
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }
}

