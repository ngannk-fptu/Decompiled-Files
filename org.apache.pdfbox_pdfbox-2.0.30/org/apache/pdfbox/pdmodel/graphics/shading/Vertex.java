/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.shading;

import java.awt.geom.Point2D;

class Vertex {
    public Point2D point;
    public float[] color;

    Vertex(Point2D p, float[] c) {
        this.point = p;
        this.color = (float[])c.clone();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (float f : this.color) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(String.format("%3.2f", Float.valueOf(f)));
        }
        return "Vertex{ " + this.point + ", colors=[" + sb + "] }";
    }
}

