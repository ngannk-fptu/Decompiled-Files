/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.gvt.AbstractGraphicsNode
 *  org.apache.batik.gvt.GraphicsNode
 *  org.apache.batik.util.SVGConstants
 */
package org.apache.batik.bridge;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.lang.ref.SoftReference;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.gvt.AbstractGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.SVGConstants;
import org.w3c.dom.Element;

public class MultiResGraphicsNode
extends AbstractGraphicsNode
implements SVGConstants {
    SoftReference[] srcs;
    Element[] srcElems;
    Dimension[] minSz;
    Dimension[] maxSz;
    Rectangle2D bounds;
    BridgeContext ctx;
    Element multiImgElem;

    public MultiResGraphicsNode(Element multiImgElem, Rectangle2D bounds, Element[] srcElems, Dimension[] minSz, Dimension[] maxSz, BridgeContext ctx) {
        this.multiImgElem = multiImgElem;
        this.srcElems = new Element[srcElems.length];
        this.minSz = new Dimension[srcElems.length];
        this.maxSz = new Dimension[srcElems.length];
        this.ctx = ctx;
        for (int i = 0; i < srcElems.length; ++i) {
            this.srcElems[i] = srcElems[i];
            this.minSz[i] = minSz[i];
            this.maxSz[i] = maxSz[i];
        }
        this.srcs = new SoftReference[srcElems.length];
        this.bounds = bounds;
    }

    public void primitivePaint(Graphics2D g2d) {
        double gnDevY1;
        double gnDevY0;
        double gnDevX1;
        double gnDevX0;
        AffineTransform at = g2d.getTransform();
        double scx = Math.sqrt(at.getShearY() * at.getShearY() + at.getScaleX() * at.getScaleX());
        double scy = Math.sqrt(at.getShearX() * at.getShearX() + at.getScaleY() * at.getScaleY());
        GraphicsNode gn = null;
        int idx = -1;
        double w = this.bounds.getWidth() * scx;
        double minDist = this.calcDist(w, this.minSz[0], this.maxSz[0]);
        int minIdx = 0;
        for (int i = 0; i < this.minSz.length; ++i) {
            double dist = this.calcDist(w, this.minSz[i], this.maxSz[i]);
            if (dist < minDist) {
                minDist = dist;
                minIdx = i;
            }
            if (this.minSz[i] != null && !(w >= (double)this.minSz[i].width) || this.maxSz[i] != null && !(w <= (double)this.maxSz[i].width) || idx != -1 && minIdx != i) continue;
            idx = i;
        }
        if (idx == -1) {
            idx = minIdx;
        }
        if ((gn = this.getGraphicsNode(idx)) == null) {
            return;
        }
        Rectangle2D gnBounds = gn.getBounds();
        if (gnBounds == null) {
            return;
        }
        double gnDevW = gnBounds.getWidth() * scx;
        double gnDevH = gnBounds.getHeight() * scy;
        double gnDevX = gnBounds.getX() * scx;
        double gnDevY = gnBounds.getY() * scy;
        if (gnDevW < 0.0) {
            gnDevX0 = gnDevX + gnDevW;
            gnDevX1 = gnDevX;
        } else {
            gnDevX0 = gnDevX;
            gnDevX1 = gnDevX + gnDevW;
        }
        if (gnDevH < 0.0) {
            gnDevY0 = gnDevY + gnDevH;
            gnDevY1 = gnDevY;
        } else {
            gnDevY0 = gnDevY;
            gnDevY1 = gnDevY + gnDevH;
        }
        gnDevW = (int)(Math.ceil(gnDevX1) - Math.floor(gnDevX0));
        gnDevH = (int)(Math.ceil(gnDevY1) - Math.floor(gnDevY0));
        scx = gnDevW / gnBounds.getWidth() / scx;
        scy = gnDevH / gnBounds.getHeight() / scy;
        AffineTransform nat = g2d.getTransform();
        nat = new AffineTransform(nat.getScaleX() * scx, nat.getShearY() * scx, nat.getShearX() * scy, nat.getScaleY() * scy, nat.getTranslateX(), nat.getTranslateY());
        g2d.setTransform(nat);
        gn.paint(g2d);
    }

    public double calcDist(double loc, Dimension min, Dimension max) {
        if (min == null) {
            if (max == null) {
                return 1.0E11;
            }
            return Math.abs(loc - (double)max.width);
        }
        if (max == null) {
            return Math.abs(loc - (double)min.width);
        }
        double mid = (double)(max.width + min.width) / 2.0;
        return Math.abs(loc - mid);
    }

    public Rectangle2D getPrimitiveBounds() {
        return this.bounds;
    }

    public Rectangle2D getGeometryBounds() {
        return this.bounds;
    }

    public Rectangle2D getSensitiveBounds() {
        return this.bounds;
    }

    public Shape getOutline() {
        return this.bounds;
    }

    public GraphicsNode getGraphicsNode(int idx) {
        Object o;
        if (this.srcs[idx] != null && (o = this.srcs[idx].get()) != null) {
            return (GraphicsNode)o;
        }
        try {
            GVTBuilder builder = this.ctx.getGVTBuilder();
            GraphicsNode gn = builder.build(this.ctx, this.srcElems[idx]);
            this.srcs[idx] = new SoftReference<GraphicsNode>(gn);
            return gn;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}

