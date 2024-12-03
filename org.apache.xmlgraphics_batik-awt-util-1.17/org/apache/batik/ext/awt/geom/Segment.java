/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.geom;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public interface Segment
extends Cloneable {
    public double minX();

    public double maxX();

    public double minY();

    public double maxY();

    public Rectangle2D getBounds2D();

    public Point2D.Double evalDt(double var1);

    public Point2D.Double eval(double var1);

    public Segment getSegment(double var1, double var3);

    public Segment splitBefore(double var1);

    public Segment splitAfter(double var1);

    public void subdivide(Segment var1, Segment var2);

    public void subdivide(double var1, Segment var3, Segment var4);

    public double getLength();

    public double getLength(double var1);

    public SplitResults split(double var1);

    public static class SplitResults {
        Segment[] above;
        Segment[] below;

        SplitResults(Segment[] below, Segment[] above) {
            this.below = below;
            this.above = above;
        }

        Segment[] getBelow() {
            return this.below;
        }

        Segment[] getAbove() {
            return this.above;
        }
    }
}

