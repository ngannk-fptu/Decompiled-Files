/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.renderer.category;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.io.Serializable;
import org.jfree.chart.HashUtilities;
import org.jfree.chart.renderer.category.BarPainter;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.ui.RectangleEdge;

public class GradientBarPainter
implements BarPainter,
Serializable {
    private double g1;
    private double g2;
    private double g3;

    public GradientBarPainter() {
        this(0.1, 0.2, 0.8);
    }

    public GradientBarPainter(double g1, double g2, double g3) {
        this.g1 = g1;
        this.g2 = g2;
        this.g3 = g3;
    }

    public void paintBar(Graphics2D g2, BarRenderer renderer, int row, int column, RectangularShape bar, RectangleEdge base) {
        GradientPaint gp;
        Rectangle2D[] regions;
        Color c1;
        Color c0;
        Paint itemPaint = renderer.getItemPaint(row, column);
        if (itemPaint instanceof Color) {
            c0 = (Color)itemPaint;
            c1 = c0.brighter();
        } else if (itemPaint instanceof GradientPaint) {
            GradientPaint gp2 = (GradientPaint)itemPaint;
            c0 = gp2.getColor1();
            c1 = gp2.getColor2();
        } else {
            c0 = Color.blue;
            c1 = Color.blue.brighter();
        }
        if (c0.getAlpha() == 0) {
            return;
        }
        if (base == RectangleEdge.TOP || base == RectangleEdge.BOTTOM) {
            regions = this.splitVerticalBar(bar, this.g1, this.g2, this.g3);
            gp = new GradientPaint((float)regions[0].getMinX(), 0.0f, c0, (float)regions[0].getMaxX(), 0.0f, Color.white);
            g2.setPaint(gp);
            g2.fill(regions[0]);
            gp = new GradientPaint((float)regions[1].getMinX(), 0.0f, Color.white, (float)regions[1].getMaxX(), 0.0f, c0);
            g2.setPaint(gp);
            g2.fill(regions[1]);
            gp = new GradientPaint((float)regions[2].getMinX(), 0.0f, c0, (float)regions[2].getMaxX(), 0.0f, c1);
            g2.setPaint(gp);
            g2.fill(regions[2]);
            gp = new GradientPaint((float)regions[3].getMinX(), 0.0f, c1, (float)regions[3].getMaxX(), 0.0f, c0);
            g2.setPaint(gp);
            g2.fill(regions[3]);
        } else if (base == RectangleEdge.LEFT || base == RectangleEdge.RIGHT) {
            regions = this.splitHorizontalBar(bar, this.g1, this.g2, this.g3);
            gp = new GradientPaint(0.0f, (float)regions[0].getMinY(), c0, 0.0f, (float)regions[0].getMaxX(), Color.white);
            g2.setPaint(gp);
            g2.fill(regions[0]);
            gp = new GradientPaint(0.0f, (float)regions[1].getMinY(), Color.white, 0.0f, (float)regions[1].getMaxY(), c0);
            g2.setPaint(gp);
            g2.fill(regions[1]);
            gp = new GradientPaint(0.0f, (float)regions[2].getMinY(), c0, 0.0f, (float)regions[2].getMaxY(), c1);
            g2.setPaint(gp);
            g2.fill(regions[2]);
            gp = new GradientPaint(0.0f, (float)regions[3].getMinY(), c1, 0.0f, (float)regions[3].getMaxY(), c0);
            g2.setPaint(gp);
            g2.fill(regions[3]);
        }
        if (renderer.isDrawBarOutline()) {
            Stroke stroke = renderer.getItemOutlineStroke(row, column);
            Paint paint = renderer.getItemOutlinePaint(row, column);
            if (stroke != null && paint != null) {
                g2.setStroke(stroke);
                g2.setPaint(paint);
                g2.draw(bar);
            }
        }
    }

    public void paintBarShadow(Graphics2D g2, BarRenderer renderer, int row, int column, RectangularShape bar, RectangleEdge base, boolean pegShadow) {
        Color c;
        Paint itemPaint = renderer.getItemPaint(row, column);
        if (itemPaint instanceof Color && (c = (Color)itemPaint).getAlpha() == 0) {
            return;
        }
        Rectangle2D shadow = this.createShadow(bar, renderer.getShadowXOffset(), renderer.getShadowYOffset(), base, pegShadow);
        g2.setPaint(renderer.getShadowPaint());
        g2.fill(shadow);
    }

    private Rectangle2D createShadow(RectangularShape bar, double xOffset, double yOffset, RectangleEdge base, boolean pegShadow) {
        double x0 = bar.getMinX();
        double x1 = bar.getMaxX();
        double y0 = bar.getMinY();
        double y1 = bar.getMaxY();
        if (base == RectangleEdge.TOP) {
            x0 += xOffset;
            x1 += xOffset;
            if (!pegShadow) {
                y0 += yOffset;
            }
            y1 += yOffset;
        } else if (base == RectangleEdge.BOTTOM) {
            x0 += xOffset;
            x1 += xOffset;
            y0 += yOffset;
            if (!pegShadow) {
                y1 += yOffset;
            }
        } else if (base == RectangleEdge.LEFT) {
            if (!pegShadow) {
                x0 += xOffset;
            }
            x1 += xOffset;
            y0 += yOffset;
            y1 += yOffset;
        } else if (base == RectangleEdge.RIGHT) {
            x0 += xOffset;
            if (!pegShadow) {
                x1 += xOffset;
            }
            y0 += yOffset;
            y1 += yOffset;
        }
        return new Rectangle2D.Double(x0, y0, x1 - x0, y1 - y0);
    }

    private Rectangle2D[] splitVerticalBar(RectangularShape bar, double a, double b, double c) {
        Rectangle2D[] result = new Rectangle2D[4];
        double x0 = bar.getMinX();
        double x1 = Math.rint(x0 + bar.getWidth() * a);
        double x2 = Math.rint(x0 + bar.getWidth() * b);
        double x3 = Math.rint(x0 + bar.getWidth() * c);
        result[0] = new Rectangle2D.Double(bar.getMinX(), bar.getMinY(), x1 - x0, bar.getHeight());
        result[1] = new Rectangle2D.Double(x1, bar.getMinY(), x2 - x1, bar.getHeight());
        result[2] = new Rectangle2D.Double(x2, bar.getMinY(), x3 - x2, bar.getHeight());
        result[3] = new Rectangle2D.Double(x3, bar.getMinY(), bar.getMaxX() - x3, bar.getHeight());
        return result;
    }

    private Rectangle2D[] splitHorizontalBar(RectangularShape bar, double a, double b, double c) {
        Rectangle2D[] result = new Rectangle2D[4];
        double y0 = bar.getMinY();
        double y1 = Math.rint(y0 + bar.getHeight() * a);
        double y2 = Math.rint(y0 + bar.getHeight() * b);
        double y3 = Math.rint(y0 + bar.getHeight() * c);
        result[0] = new Rectangle2D.Double(bar.getMinX(), bar.getMinY(), bar.getWidth(), y1 - y0);
        result[1] = new Rectangle2D.Double(bar.getMinX(), y1, bar.getWidth(), y2 - y1);
        result[2] = new Rectangle2D.Double(bar.getMinX(), y2, bar.getWidth(), y3 - y2);
        result[3] = new Rectangle2D.Double(bar.getMinX(), y3, bar.getWidth(), bar.getMaxY() - y3);
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof GradientBarPainter)) {
            return false;
        }
        GradientBarPainter that = (GradientBarPainter)obj;
        if (this.g1 != that.g1) {
            return false;
        }
        if (this.g2 != that.g2) {
            return false;
        }
        return this.g3 == that.g3;
    }

    public int hashCode() {
        int hash = 37;
        hash = HashUtilities.hashCode(hash, this.g1);
        hash = HashUtilities.hashCode(hash, this.g2);
        hash = HashUtilities.hashCode(hash, this.g3);
        return hash;
    }
}

