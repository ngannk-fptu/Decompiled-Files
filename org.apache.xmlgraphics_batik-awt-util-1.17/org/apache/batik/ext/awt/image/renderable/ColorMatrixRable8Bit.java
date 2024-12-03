/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.renderable;

import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import org.apache.batik.ext.awt.image.renderable.AbstractColorInterpolationRable;
import org.apache.batik.ext.awt.image.renderable.ColorMatrixRable;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.rendered.ColorMatrixRed;

public final class ColorMatrixRable8Bit
extends AbstractColorInterpolationRable
implements ColorMatrixRable {
    private static float[][] MATRIX_LUMINANCE_TO_ALPHA = new float[][]{{0.0f, 0.0f, 0.0f, 0.0f, 0.0f}, {0.0f, 0.0f, 0.0f, 0.0f, 0.0f}, {0.0f, 0.0f, 0.0f, 0.0f, 0.0f}, {0.2125f, 0.7154f, 0.0721f, 0.0f, 0.0f}};
    private int type;
    private float[][] matrix;

    @Override
    public void setSource(Filter src) {
        this.init(src, null);
    }

    @Override
    public Filter getSource() {
        return (Filter)this.getSources().get(0);
    }

    @Override
    public int getType() {
        return this.type;
    }

    @Override
    public float[][] getMatrix() {
        return this.matrix;
    }

    private ColorMatrixRable8Bit() {
    }

    public static ColorMatrixRable buildMatrix(float[][] matrix) {
        if (matrix == null) {
            throw new IllegalArgumentException();
        }
        if (matrix.length != 4) {
            throw new IllegalArgumentException();
        }
        float[][] newMatrix = new float[4][];
        for (int i = 0; i < 4; ++i) {
            float[] m = matrix[i];
            if (m == null) {
                throw new IllegalArgumentException();
            }
            if (m.length != 5) {
                throw new IllegalArgumentException();
            }
            newMatrix[i] = new float[5];
            for (int j = 0; j < 5; ++j) {
                newMatrix[i][j] = m[j];
            }
        }
        ColorMatrixRable8Bit filter = new ColorMatrixRable8Bit();
        filter.type = 0;
        filter.matrix = newMatrix;
        return filter;
    }

    public static ColorMatrixRable buildSaturate(float s) {
        ColorMatrixRable8Bit filter = new ColorMatrixRable8Bit();
        filter.type = 1;
        filter.matrix = new float[][]{{0.213f + 0.787f * s, 0.715f - 0.715f * s, 0.072f - 0.072f * s, 0.0f, 0.0f}, {0.213f - 0.213f * s, 0.715f + 0.285f * s, 0.072f - 0.072f * s, 0.0f, 0.0f}, {0.213f - 0.213f * s, 0.715f - 0.715f * s, 0.072f + 0.928f * s, 0.0f, 0.0f}, {0.0f, 0.0f, 0.0f, 1.0f, 0.0f}};
        return filter;
    }

    public static ColorMatrixRable buildHueRotate(float a) {
        ColorMatrixRable8Bit filter = new ColorMatrixRable8Bit();
        filter.type = 2;
        float cos = (float)Math.cos(a);
        float sin = (float)Math.sin(a);
        float a00 = 0.213f + cos * 0.787f - sin * 0.213f;
        float a10 = 0.213f - cos * 0.212f + sin * 0.143f;
        float a20 = 0.213f - cos * 0.213f - sin * 0.787f;
        float a01 = 0.715f - cos * 0.715f - sin * 0.715f;
        float a11 = 0.715f + cos * 0.285f + sin * 0.14f;
        float a21 = 0.715f - cos * 0.715f + sin * 0.715f;
        float a02 = 0.072f - cos * 0.072f + sin * 0.928f;
        float a12 = 0.072f - cos * 0.072f - sin * 0.283f;
        float a22 = 0.072f + cos * 0.928f + sin * 0.072f;
        filter.matrix = new float[][]{{a00, a01, a02, 0.0f, 0.0f}, {a10, a11, a12, 0.0f, 0.0f}, {a20, a21, a22, 0.0f, 0.0f}, {0.0f, 0.0f, 0.0f, 1.0f, 0.0f}};
        return filter;
    }

    public static ColorMatrixRable buildLuminanceToAlpha() {
        ColorMatrixRable8Bit filter = new ColorMatrixRable8Bit();
        filter.type = 3;
        filter.matrix = MATRIX_LUMINANCE_TO_ALPHA;
        return filter;
    }

    @Override
    public RenderedImage createRendering(RenderContext rc) {
        RenderedImage srcRI = this.getSource().createRendering(rc);
        if (srcRI == null) {
            return null;
        }
        return new ColorMatrixRed(this.convertSourceCS(srcRI), this.matrix);
    }
}

