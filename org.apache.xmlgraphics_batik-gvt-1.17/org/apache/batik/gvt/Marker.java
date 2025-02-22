/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.gvt;

import java.awt.geom.Point2D;
import org.apache.batik.gvt.GraphicsNode;

public class Marker {
    protected double orient;
    protected GraphicsNode markerNode;
    protected Point2D ref;

    public Marker(GraphicsNode markerNode, Point2D ref, double orient) {
        if (markerNode == null) {
            throw new IllegalArgumentException();
        }
        if (ref == null) {
            throw new IllegalArgumentException();
        }
        this.markerNode = markerNode;
        this.ref = ref;
        this.orient = orient;
    }

    public Point2D getRef() {
        return (Point2D)this.ref.clone();
    }

    public double getOrient() {
        return this.orient;
    }

    public GraphicsNode getMarkerNode() {
        return this.markerNode;
    }
}

