/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.gvt;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import org.apache.batik.gvt.AbstractGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;

public class ProxyGraphicsNode
extends AbstractGraphicsNode {
    protected GraphicsNode source;

    public void setSource(GraphicsNode source) {
        this.source = source;
    }

    public GraphicsNode getSource() {
        return this.source;
    }

    @Override
    public void primitivePaint(Graphics2D g2d) {
        if (this.source != null) {
            this.source.paint(g2d);
        }
    }

    @Override
    public Rectangle2D getPrimitiveBounds() {
        if (this.source == null) {
            return null;
        }
        return this.source.getBounds();
    }

    @Override
    public Rectangle2D getTransformedPrimitiveBounds(AffineTransform txf) {
        if (this.source == null) {
            return null;
        }
        AffineTransform t = txf;
        if (this.transform != null) {
            t = new AffineTransform(txf);
            t.concatenate(this.transform);
        }
        return this.source.getTransformedPrimitiveBounds(t);
    }

    @Override
    public Rectangle2D getGeometryBounds() {
        if (this.source == null) {
            return null;
        }
        return this.source.getGeometryBounds();
    }

    @Override
    public Rectangle2D getTransformedGeometryBounds(AffineTransform txf) {
        if (this.source == null) {
            return null;
        }
        AffineTransform t = txf;
        if (this.transform != null) {
            t = new AffineTransform(txf);
            t.concatenate(this.transform);
        }
        return this.source.getTransformedGeometryBounds(t);
    }

    @Override
    public Rectangle2D getSensitiveBounds() {
        if (this.source == null) {
            return null;
        }
        return this.source.getSensitiveBounds();
    }

    @Override
    public Shape getOutline() {
        if (this.source == null) {
            return null;
        }
        return this.source.getOutline();
    }
}

