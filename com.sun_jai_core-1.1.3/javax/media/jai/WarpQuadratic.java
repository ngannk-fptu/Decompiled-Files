/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.geom.Point2D;
import javax.media.jai.JaiI18N;
import javax.media.jai.WarpPolynomial;

public final class WarpQuadratic
extends WarpPolynomial {
    private float c1;
    private float c2;
    private float c3;
    private float c4;
    private float c5;
    private float c6;
    private float c7;
    private float c8;
    private float c9;
    private float c10;
    private float c11;
    private float c12;

    public WarpQuadratic(float[] xCoeffs, float[] yCoeffs, float preScaleX, float preScaleY, float postScaleX, float postScaleY) {
        super(xCoeffs, yCoeffs, preScaleX, preScaleY, postScaleX, postScaleY);
        if (xCoeffs.length != 6 || yCoeffs.length != 6) {
            throw new IllegalArgumentException(JaiI18N.getString("WarpQuadratic0"));
        }
        this.c1 = xCoeffs[0];
        this.c2 = xCoeffs[1];
        this.c3 = xCoeffs[2];
        this.c4 = xCoeffs[3];
        this.c5 = xCoeffs[4];
        this.c6 = xCoeffs[5];
        this.c7 = yCoeffs[0];
        this.c8 = yCoeffs[1];
        this.c9 = yCoeffs[2];
        this.c10 = yCoeffs[3];
        this.c11 = yCoeffs[4];
        this.c12 = yCoeffs[5];
    }

    public WarpQuadratic(float[] xCoeffs, float[] yCoeffs) {
        this(xCoeffs, yCoeffs, 1.0f, 1.0f, 1.0f, 1.0f);
    }

    public float[] warpSparseRect(int x, int y, int width, int height, int periodX, int periodY, float[] destRect) {
        if (destRect == null) {
            destRect = new float[(width + periodX - 1) / periodX * ((height + periodY - 1) / periodY) * 2];
        }
        float px1 = (float)periodX * this.preScaleX;
        float px2 = px1 * px1;
        float ddx = this.c4 * 2.0f * px2;
        float ddy = this.c10 * 2.0f * px2;
        float x1 = ((float)x + 0.5f) * this.preScaleX;
        float x2 = x1 * x1;
        width += x;
        height += y;
        int index = 0;
        for (int j = y; j < height; j += periodY) {
            float y1 = ((float)j + 0.5f) * this.preScaleY;
            float y2 = y1 * y1;
            float wx = this.c1 + this.c2 * x1 + this.c3 * y1 + this.c4 * x2 + this.c5 * x1 * y1 + this.c6 * y2;
            float wy = this.c7 + this.c8 * x1 + this.c9 * y1 + this.c10 * x2 + this.c11 * x1 * y1 + this.c12 * y2;
            float dx = this.c2 * px1 + this.c4 * (2.0f * x1 * px1 + px2) + this.c5 * px1 * y1;
            float dy = this.c8 * px1 + this.c10 * (2.0f * x1 * px1 + px2) + this.c11 * px1 * y1;
            for (int i = x; i < width; i += periodX) {
                destRect[index++] = wx * this.postScaleX - 0.5f;
                destRect[index++] = wy * this.postScaleY - 0.5f;
                wx += dx;
                wy += dy;
                dx += ddx;
                dy += ddy;
            }
        }
        return destRect;
    }

    public Point2D mapDestPoint(Point2D destPt) {
        if (destPt == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        double x1 = (destPt.getX() + 0.5) * (double)this.preScaleX;
        double x2 = x1 * x1;
        double y1 = (destPt.getY() + 0.5) * (double)this.preScaleY;
        double y2 = y1 * y1;
        double x = (double)this.c1 + (double)this.c2 * x1 + (double)this.c3 * y1 + (double)this.c4 * x2 + (double)this.c5 * x1 * y1 + (double)this.c6 * y2;
        double y = (double)this.c7 + (double)this.c8 * x1 + (double)this.c9 * y1 + (double)this.c10 * x2 + (double)this.c11 * x1 * y1 + (double)this.c12 * y2;
        Point2D pt = (Point2D)destPt.clone();
        pt.setLocation(x * (double)this.postScaleX - 0.5, y * (double)this.postScaleY - 0.5);
        return pt;
    }
}

