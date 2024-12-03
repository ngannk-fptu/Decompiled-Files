/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import javax.media.jai.WarpPolynomial;

public final class WarpGeneralPolynomial
extends WarpPolynomial {
    public WarpGeneralPolynomial(float[] xCoeffs, float[] yCoeffs, float preScaleX, float preScaleY, float postScaleX, float postScaleY) {
        super(xCoeffs, yCoeffs, preScaleX, preScaleY, postScaleX, postScaleY);
    }

    public WarpGeneralPolynomial(float[] xCoeffs, float[] yCoeffs) {
        this(xCoeffs, yCoeffs, 1.0f, 1.0f, 1.0f, 1.0f);
    }

    public float[] warpSparseRect(int x, int y, int width, int height, int periodX, int periodY, float[] destRect) {
        if (destRect == null) {
            destRect = new float[2 * ((width + periodX - 1) / periodX) * ((height + periodY - 1) / periodY)];
        }
        float[] xPows = new float[this.degree + 1];
        float[] yPows = new float[this.degree + 1];
        xPows[0] = 1.0f;
        yPows[0] = 1.0f;
        width += x;
        height += y;
        int index = 0;
        for (int j = y; j < height; j += periodY) {
            float y1 = ((float)j + 0.5f) * this.preScaleY;
            for (int n = 1; n <= this.degree; ++n) {
                yPows[n] = yPows[n - 1] * y1;
            }
            for (int i = x; i < width; i += periodX) {
                float x1 = ((float)i + 0.5f) * this.preScaleX;
                for (int n = 1; n <= this.degree; ++n) {
                    xPows[n] = xPows[n - 1] * x1;
                }
                float wx = 0.0f;
                float wy = 0.0f;
                int c = 0;
                for (int nx = 0; nx <= this.degree; ++nx) {
                    for (int ny = 0; ny <= nx; ++ny) {
                        float t = xPows[nx - ny] * yPows[ny];
                        wx += this.xCoeffs[c] * t;
                        wy += this.yCoeffs[c] * t;
                        ++c;
                    }
                }
                destRect[index++] = wx * this.postScaleX - 0.5f;
                destRect[index++] = wy * this.postScaleY - 0.5f;
            }
        }
        return destRect;
    }
}

