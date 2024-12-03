/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import javax.media.jai.JaiI18N;
import javax.media.jai.WarpPolynomial;

public final class WarpAffine
extends WarpPolynomial {
    private float c1;
    private float c2;
    private float c3;
    private float c4;
    private float c5;
    private float c6;
    private float invc1;
    private float invc2;
    private float invc3;
    private float invc4;
    private float invc5;
    private float invc6;
    private AffineTransform transform;
    private AffineTransform invTransform;

    private static final float[] xCoeffsHelper(AffineTransform transform) {
        float[] coeffs = new float[]{(float)transform.getTranslateX(), (float)transform.getScaleX(), (float)transform.getShearX()};
        return coeffs;
    }

    private static final float[] yCoeffsHelper(AffineTransform transform) {
        float[] coeffs = new float[]{(float)transform.getTranslateY(), (float)transform.getShearY(), (float)transform.getScaleY()};
        return coeffs;
    }

    public WarpAffine(float[] xCoeffs, float[] yCoeffs, float preScaleX, float preScaleY, float postScaleX, float postScaleY) {
        super(xCoeffs, yCoeffs, preScaleX, preScaleY, postScaleX, postScaleY);
        if (xCoeffs.length != 3 || yCoeffs.length != 3) {
            throw new IllegalArgumentException(JaiI18N.getString("WarpAffine0"));
        }
        this.c1 = xCoeffs[0];
        this.c2 = xCoeffs[1];
        this.c3 = xCoeffs[2];
        this.c4 = yCoeffs[0];
        this.c5 = yCoeffs[1];
        this.c6 = yCoeffs[2];
        this.transform = this.getTransform();
        try {
            this.invTransform = this.transform.createInverse();
            this.invc1 = (float)this.invTransform.getTranslateX();
            this.invc2 = (float)this.invTransform.getScaleX();
            this.invc3 = (float)this.invTransform.getShearX();
            this.invc4 = (float)this.invTransform.getTranslateY();
            this.invc5 = (float)this.invTransform.getShearY();
            this.invc6 = (float)this.invTransform.getScaleY();
        }
        catch (NoninvertibleTransformException e) {
            this.invTransform = null;
        }
    }

    public WarpAffine(float[] xCoeffs, float[] yCoeffs) {
        this(xCoeffs, yCoeffs, 1.0f, 1.0f, 1.0f, 1.0f);
    }

    public WarpAffine(AffineTransform transform, float preScaleX, float preScaleY, float postScaleX, float postScaleY) {
        this(WarpAffine.xCoeffsHelper(transform), WarpAffine.yCoeffsHelper(transform), preScaleX, preScaleY, postScaleX, postScaleY);
    }

    public WarpAffine(AffineTransform transform) {
        this(transform, 1.0f, 1.0f, 1.0f, 1.0f);
    }

    public AffineTransform getTransform() {
        return new AffineTransform(this.c2, this.c5, this.c3, this.c6, this.c1, this.c4);
    }

    public float[] warpSparseRect(int x, int y, int width, int height, int periodX, int periodY, float[] destRect) {
        if (destRect == null) {
            destRect = new float[(width + periodX - 1) / periodX * ((height + periodY - 1) / periodY) * 2];
        }
        float px1 = (float)periodX * this.preScaleX;
        float dx = this.c2 * px1 * this.postScaleX;
        float dy = this.c5 * px1 * this.postScaleY;
        float x1 = ((float)x + 0.5f) * this.preScaleX;
        width += x;
        height += y;
        int index = 0;
        for (int j = y; j < height; j += periodY) {
            float y1 = ((float)j + 0.5f) * this.preScaleY;
            float wx = (this.c1 + this.c2 * x1 + this.c3 * y1) * this.postScaleX - 0.5f;
            float wy = (this.c4 + this.c5 * x1 + this.c6 * y1) * this.postScaleY - 0.5f;
            for (int i = x; i < width; i += periodX) {
                destRect[index++] = wx;
                destRect[index++] = wy;
                wx += dx;
                wy += dy;
            }
        }
        return destRect;
    }

    public Rectangle mapDestRect(Rectangle destRect) {
        if (destRect == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        int dx0 = destRect.x;
        int dx1 = destRect.x + destRect.width;
        int dy0 = destRect.y;
        int dy1 = destRect.y + destRect.height;
        float[] pt = this.mapDestPoint(dx0, dy0);
        float sx0 = pt[0];
        float sx1 = pt[0];
        float sy0 = pt[1];
        float sy1 = pt[1];
        pt = this.mapDestPoint(dx1, dy0);
        sx0 = Math.min(sx0, pt[0]);
        sx1 = Math.max(sx1, pt[0]);
        sy0 = Math.min(sy0, pt[1]);
        sy1 = Math.max(sy1, pt[1]);
        pt = this.mapDestPoint(dx0, dy1);
        sx0 = Math.min(sx0, pt[0]);
        sx1 = Math.max(sx1, pt[0]);
        sy0 = Math.min(sy0, pt[1]);
        sy1 = Math.max(sy1, pt[1]);
        pt = this.mapDestPoint(dx1, dy1);
        sx0 = Math.min(sx0, pt[0]);
        sx1 = Math.max(sx1, pt[0]);
        sy0 = Math.min(sy0, pt[1]);
        sy1 = Math.max(sy1, pt[1]);
        int x = (int)Math.floor(sx0);
        int y = (int)Math.floor(sy0);
        int w = (int)Math.ceil(sx1 - (float)x);
        int h = (int)Math.ceil(sy1 - (float)y);
        return new Rectangle(x, y, w, h);
    }

    public Rectangle mapSourceRect(Rectangle srcRect) {
        if (srcRect == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (this.invTransform == null) {
            return null;
        }
        int sx0 = srcRect.x;
        int sx1 = srcRect.x + srcRect.width;
        int sy0 = srcRect.y;
        int sy1 = srcRect.y + srcRect.height;
        float[] pt = this.mapSrcPoint(sx0, sy0);
        float dx0 = pt[0];
        float dx1 = pt[0];
        float dy0 = pt[1];
        float dy1 = pt[1];
        pt = this.mapSrcPoint(sx1, sy0);
        dx0 = Math.min(dx0, pt[0]);
        dx1 = Math.max(dx1, pt[0]);
        dy0 = Math.min(dy0, pt[1]);
        dy1 = Math.max(dy1, pt[1]);
        pt = this.mapSrcPoint(sx0, sy1);
        dx0 = Math.min(dx0, pt[0]);
        dx1 = Math.max(dx1, pt[0]);
        dy0 = Math.min(dy0, pt[1]);
        dy1 = Math.max(dy1, pt[1]);
        pt = this.mapSrcPoint(sx1, sy1);
        dx0 = Math.min(dx0, pt[0]);
        dx1 = Math.max(dx1, pt[0]);
        dy0 = Math.min(dy0, pt[1]);
        dy1 = Math.max(dy1, pt[1]);
        int x = (int)Math.floor(dx0);
        int y = (int)Math.floor(dy0);
        int w = (int)Math.ceil(dx1 - (float)x);
        int h = (int)Math.ceil(dy1 - (float)y);
        return new Rectangle(x, y, w, h);
    }

    private float[] mapDestPoint(int x, int y) {
        float fx = ((float)x + 0.5f) * this.preScaleX;
        float fy = ((float)y + 0.5f) * this.preScaleY;
        float[] p = new float[]{(this.c1 + this.c2 * fx + this.c3 * fy) * this.postScaleX - 0.5f, (this.c4 + this.c5 * fx + this.c6 * fy) * this.postScaleY - 0.5f};
        return p;
    }

    private float[] mapSrcPoint(int x, int y) {
        float fx = ((float)x + 0.5f) * this.preScaleX;
        float fy = ((float)y + 0.5f) * this.preScaleY;
        float[] p = new float[]{(this.invc1 + this.invc2 * fx + this.invc3 * fy) * this.postScaleX - 0.5f, (this.invc4 + this.invc5 * fx + this.invc6 * fy) * this.postScaleY - 0.5f};
        return p;
    }

    public Point2D mapDestPoint(Point2D destPt) {
        if (destPt == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        double dx = (destPt.getX() + 0.5) * (double)this.preScaleX;
        double dy = (destPt.getY() + 0.5) * (double)this.preScaleY;
        Point2D pt = (Point2D)destPt.clone();
        pt.setLocation(((double)this.c1 + (double)this.c2 * dx + (double)this.c3 * dy) * (double)this.postScaleX - 0.5, ((double)this.c4 + (double)this.c5 * dx + (double)this.c6 * dy) * (double)this.postScaleY - 0.5);
        return pt;
    }

    public Point2D mapSourcePoint(Point2D sourcePt) {
        if (sourcePt == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (this.invTransform == null) {
            return null;
        }
        double sx = (sourcePt.getX() + 0.5) / (double)this.postScaleX;
        double sy = (sourcePt.getY() + 0.5) / (double)this.postScaleY;
        Point2D pt = (Point2D)sourcePt.clone();
        pt.setLocation(((double)this.invc1 + (double)this.invc2 * sx + (double)this.invc3 * sy) / (double)this.preScaleX - 0.5, ((double)this.invc4 + (double)this.invc5 * sx + (double)this.invc6 * sy) / (double)this.preScaleY - 0.5);
        return pt;
    }
}

