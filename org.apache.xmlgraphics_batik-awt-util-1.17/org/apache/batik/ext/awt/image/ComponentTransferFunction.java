/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image;

public interface ComponentTransferFunction {
    public static final int IDENTITY = 0;
    public static final int TABLE = 1;
    public static final int DISCRETE = 2;
    public static final int LINEAR = 3;
    public static final int GAMMA = 4;

    public int getType();

    public float getSlope();

    public float[] getTableValues();

    public float getIntercept();

    public float getAmplitude();

    public float getExponent();

    public float getOffset();
}

