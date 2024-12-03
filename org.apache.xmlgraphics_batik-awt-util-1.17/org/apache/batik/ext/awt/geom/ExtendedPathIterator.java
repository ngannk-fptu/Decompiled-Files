/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.geom;

public interface ExtendedPathIterator {
    public static final int SEG_CLOSE = 4;
    public static final int SEG_MOVETO = 0;
    public static final int SEG_LINETO = 1;
    public static final int SEG_QUADTO = 2;
    public static final int SEG_CUBICTO = 3;
    public static final int SEG_ARCTO = 4321;
    public static final int WIND_EVEN_ODD = 0;
    public static final int WIND_NON_ZERO = 1;

    public int currentSegment();

    public int currentSegment(double[] var1);

    public int currentSegment(float[] var1);

    public int getWindingRule();

    public boolean isDone();

    public void next();
}

