/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.geom.Point2D;
import javax.media.jai.JaiI18N;
import javax.media.jai.WarpPolynomial;

public final class WarpCubic
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
    private float c13;
    private float c14;
    private float c15;
    private float c16;
    private float c17;
    private float c18;
    private float c19;
    private float c20;

    public WarpCubic(float[] xCoeffs, float[] yCoeffs, float preScaleX, float preScaleY, float postScaleX, float postScaleY) {
        super(xCoeffs, yCoeffs, preScaleX, preScaleY, postScaleX, postScaleY);
        if (xCoeffs.length != 10 || yCoeffs.length != 10) {
            throw new IllegalArgumentException(JaiI18N.getString("WarpCubic0"));
        }
        this.c1 = xCoeffs[0];
        this.c2 = xCoeffs[1];
        this.c3 = xCoeffs[2];
        this.c4 = xCoeffs[3];
        this.c5 = xCoeffs[4];
        this.c6 = xCoeffs[5];
        this.c7 = xCoeffs[6];
        this.c8 = xCoeffs[7];
        this.c9 = xCoeffs[8];
        this.c10 = xCoeffs[9];
        this.c11 = yCoeffs[0];
        this.c12 = yCoeffs[1];
        this.c13 = yCoeffs[2];
        this.c14 = yCoeffs[3];
        this.c15 = yCoeffs[4];
        this.c16 = yCoeffs[5];
        this.c17 = yCoeffs[6];
        this.c18 = yCoeffs[7];
        this.c19 = yCoeffs[8];
        this.c20 = yCoeffs[9];
    }

    public WarpCubic(float[] xCoeffs, float[] yCoeffs) {
        this(xCoeffs, yCoeffs, 1.0f, 1.0f, 1.0f, 1.0f);
    }

    public float[] warpSparseRect(int x, int y, int width, int height, int periodX, int periodY, float[] destRect) {
        if (destRect == null) {
            destRect = new float[(width + periodX - 1) / periodX * ((height + periodY - 1) / periodY) * 2];
        }
        float px1 = (float)periodX * this.preScaleX;
        float px2 = px1 * px1;
        float px3 = px2 * px1;
        float dddx = this.c7 * 6.0f * px3;
        float dddy = this.c17 * 6.0f * px3;
        float x1 = ((float)x + 0.5f) * this.preScaleX;
        float x2 = x1 * x1;
        float x3 = x2 * x1;
        width += x;
        height += y;
        int index = 0;
        for (int j = y; j < height; j += periodY) {
            float y1 = ((float)j + 0.5f) * this.preScaleY;
            float y2 = y1 * y1;
            float y3 = y2 * y1;
            float wx = this.c1 + this.c2 * x1 + this.c3 * y1 + this.c4 * x2 + this.c5 * x1 * y1 + this.c6 * y2 + this.c7 * x3 + this.c8 * x2 * y1 + this.c9 * x1 * y2 + this.c10 * y3;
            float wy = this.c11 + this.c12 * x1 + this.c13 * y1 + this.c14 * x2 + this.c15 * x1 * y1 + this.c16 * y2 + this.c17 * x3 + this.c18 * x2 * y1 + this.c19 * x1 * y2 + this.c20 * y3;
            float dx = this.c2 * px1 + this.c4 * (2.0f * x1 * px1 + px2) + this.c5 * px1 * y1 + this.c7 * (3.0f * x2 * px1 + 3.0f * x1 * px2 + px3) + this.c8 * (2.0f * x1 * px1 + px2) * y1 + this.c9 * px1 * y2;
            float dy = this.c12 * px1 + this.c14 * (2.0f * x1 * px1 + px2) + this.c15 * px1 * y1 + this.c17 * (3.0f * x2 * px1 + 3.0f * x1 * px2 + px3) + this.c18 * (2.0f * x1 * px1 + px2) * y1 + this.c19 * px1 * y2;
            float ddx = this.c4 * 2.0f * px2 + this.c7 * (6.0f * x1 * px2 + 6.0f * px3) + this.c8 * 2.0f * px2 * y1;
            float ddy = this.c14 * 2.0f * px2 + this.c17 * (6.0f * x1 * px2 + 6.0f * px3) + this.c18 * 2.0f * px2 * y1;
            for (int i = x; i < width; i += periodX) {
                destRect[index++] = wx * this.postScaleX - 0.5f;
                destRect[index++] = wy * this.postScaleY - 0.5f;
                wx += dx;
                wy += dy;
                dx += ddx;
                dy += ddy;
                ddx += dddx;
                ddy += dddy;
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
        double x3 = x2 * x1;
        double y1 = (destPt.getY() + 0.5) * (double)this.preScaleY;
        double y2 = y1 * y1;
        double y3 = y2 * y1;
        double sx = (double)this.c1 + (double)this.c2 * x1 + (double)this.c3 * y1 + (double)this.c4 * x2 + (double)this.c5 * x1 * y1 + (double)this.c6 * y2 + (double)this.c7 * x3 + (double)this.c8 * x2 * y1 + (double)this.c9 * x1 * y2 + (double)this.c10 * y3;
        double sy = (double)this.c11 + (double)this.c12 * x1 + (double)this.c13 * y1 + (double)this.c14 * x2 + (double)this.c15 * x1 * y1 + (double)this.c16 * y2 + (double)this.c17 * x3 + (double)this.c18 * x2 * y1 + (double)this.c19 * x1 * y2 + (double)this.c20 * y3;
        Point2D pt = (Point2D)destPt.clone();
        pt.setLocation(sx * (double)this.postScaleX - 0.5, sy * (double)this.postScaleY - 0.5);
        return pt;
    }
}

