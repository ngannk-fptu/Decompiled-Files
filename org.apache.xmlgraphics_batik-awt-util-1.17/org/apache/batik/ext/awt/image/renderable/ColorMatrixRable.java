/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.renderable;

import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.FilterColorInterpolation;

public interface ColorMatrixRable
extends FilterColorInterpolation {
    public static final int TYPE_MATRIX = 0;
    public static final int TYPE_SATURATE = 1;
    public static final int TYPE_HUE_ROTATE = 2;
    public static final int TYPE_LUMINANCE_TO_ALPHA = 3;

    public Filter getSource();

    public void setSource(Filter var1);

    public int getType();

    public float[][] getMatrix();
}

