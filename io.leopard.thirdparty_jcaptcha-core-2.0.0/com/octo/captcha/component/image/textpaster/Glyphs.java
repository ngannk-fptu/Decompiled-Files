/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.component.image.textpaster;

import java.awt.Shape;
import java.awt.font.GlyphMetrics;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class Glyphs {
    List<GlyphVector> vectors = new ArrayList<GlyphVector>();

    public void addGlyphVector(GlyphVector glyph) {
        this.vectors.add(glyph);
    }

    public int size() {
        return this.vectors.size();
    }

    public GlyphVector get(int index) {
        return this.vectors.get(index);
    }

    public double getBoundsX(int index) {
        return this.getBounds(index).getX();
    }

    public double getBoundsY(int index) {
        return this.getBounds(index).getY();
    }

    public double getBoundsWidth(int index) {
        return this.getBounds(index).getWidth();
    }

    public double getBoundsHeight(int index) {
        return this.getBounds(index).getHeight();
    }

    public double getX(int index) {
        return this.get(index).getGlyphPosition(0).getX();
    }

    public double getY(int index) {
        return this.get(index).getGlyphPosition(0).getY();
    }

    public Shape getOutline(int index) {
        return this.get(index).getOutline();
    }

    public double getBoundsX() {
        return this.getBounds().getX();
    }

    public double getBoundsY() {
        return this.getBounds().getY();
    }

    public double getBoundsWidth() {
        return this.getBounds().getWidth();
    }

    public double getBoundsHeight() {
        return this.getBounds().getHeight();
    }

    public double getMaxX(int index) {
        return this.getBounds(index).getMaxX();
    }

    public double getMaxY(int index) {
        return this.getBounds(index).getMaxY();
    }

    public double getMinX(int index) {
        return this.getBounds(index).getMinX();
    }

    public double getMinY(int index) {
        return this.getBounds(index).getMinX();
    }

    public GlyphVector getGlyphVector(int index) {
        return this.vectors.get(index);
    }

    public Rectangle2D getBounds(int index) {
        return this.vectors.get(index).getVisualBounds();
    }

    public Rectangle2D getBounds() {
        Rectangle2D bounds = this.size() > 0 ? this.getBounds(0) : new Rectangle2D.Double(0.0, 0.0, 0.0, 0.0);
        for (int i = 1; i < this.size(); ++i) {
            bounds = bounds.createUnion(this.getBounds(i));
        }
        return bounds;
    }

    public GlyphMetrics getMetrics(int index) {
        return this.get(index).getGlyphMetrics(0);
    }

    public double getLSB(int index) {
        return this.getMetrics(index).getLSB();
    }

    public double getRSB(int index) {
        return this.getMetrics(index).getRSB();
    }

    public double getAdvance(int index) {
        return this.getMetrics(index).getAdvance();
    }

    public double getInternalWidth(int index) {
        return this.getAdvance(index) - this.getRSB(index) - this.getLSB(index);
    }

    public Rectangle2D getInternalBounds(int index) {
        return this.getMetrics(index).getBounds2D();
    }

    public double getInternalBoundsX(int index) {
        return this.getInternalBounds(index).getX();
    }

    public double getInternalBoundsY(int index) {
        return this.getInternalBounds(index).getY();
    }

    public double getInternalBoundsWidth(int index) {
        return this.getInternalBounds(index).getWidth();
    }

    public double getInternalBoundsHeigth(int index) {
        return this.getInternalBounds(index).getHeight();
    }

    public double getAdvanceX(int index) {
        return this.getMetrics(index).getAdvanceX();
    }

    public double getAdvanceY(int index) {
        return this.getMetrics(index).getAdvanceY();
    }

    public double getMaxHeight() {
        double max = 0.0;
        for (int i = 1; i < this.size(); ++i) {
            max = Math.max(this.getBoundsHeight(i), max);
        }
        return max;
    }

    public double getMaxWidth() {
        double max = 0.0;
        for (int i = 1; i < this.size(); ++i) {
            max = Math.max(this.getBoundsWidth(i), max);
        }
        return max;
    }

    public void translate(double x, double y) {
        for (int i = 0; i < this.size(); ++i) {
            this.translate(i, x, y);
        }
    }

    public void translate(int index, double x, double y) {
        this.setPosition(index, x + this.getX(index), y + this.getY(index));
    }

    public void setPosition(int index, double x, double y) {
        this.vectors.get(index).setGlyphPosition(0, new Point2D.Double(x, y));
    }

    public void addAffineTransform(AffineTransform at) {
        for (int i = 0; i < this.size(); ++i) {
            this.addAffineTransform(i, at);
        }
    }

    public void addAffineTransform(int index, AffineTransform at) {
        AffineTransform t = this.vectors.get(index).getGlyphTransform(0);
        if (t == null) {
            t = at;
        } else {
            t.concatenate(at);
        }
        this.vectors.get(index).setGlyphTransform(0, t);
    }

    public void rotate(int index, double angle) {
        this.get(index).setGlyphTransform(0, AffineTransform.getRotateInstance(angle, this.getBoundsX(index) + this.getBoundsWidth(index) / 2.0, this.getBoundsY(index) + this.getBoundsHeight(index) / 2.0));
    }

    public String toString() {
        String R = "\n";
        String RS = "\n\t";
        String RSS = "\n\t\t";
        StringBuffer buf = new StringBuffer();
        buf.append("{Glyphs=");
        for (int i = 0; i < this.size(); ++i) {
            buf.append("\n\t");
            buf.append("{GlyphVector=" + i + " : ");
            for (int j = 0; j < this.get(i).getNumGlyphs(); ++j) {
                buf.append("Glyph=" + j);
                buf.append("; Bounds=");
                buf.append(this.get(i).getGlyphVisualBounds(j).getBounds2D());
                buf.append("; Font=");
                buf.append(this.get(i).getFont());
            }
            buf.append("}");
        }
        buf.append("\n");
        buf.append("Bounds : ");
        buf.append(this.getBounds());
        buf.append("}");
        return buf.toString();
    }
}

