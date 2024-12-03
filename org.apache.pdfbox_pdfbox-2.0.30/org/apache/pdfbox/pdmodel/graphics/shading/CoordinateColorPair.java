/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.shading;

import java.awt.geom.Point2D;

class CoordinateColorPair {
    final Point2D coordinate;
    final float[] color;

    CoordinateColorPair(Point2D p, float[] c) {
        this.coordinate = p;
        this.color = (float[])c.clone();
    }
}

