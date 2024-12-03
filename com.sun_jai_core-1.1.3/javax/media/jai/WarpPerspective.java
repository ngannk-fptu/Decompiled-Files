/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.Rectangle;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import javax.media.jai.JaiI18N;
import javax.media.jai.PerspectiveTransform;
import javax.media.jai.Warp;

public final class WarpPerspective
extends Warp {
    private PerspectiveTransform transform;
    private PerspectiveTransform invTransform;

    public WarpPerspective(PerspectiveTransform transform) {
        if (transform == null) {
            throw new IllegalArgumentException(JaiI18N.getString("WarpPerspective0"));
        }
        this.transform = transform;
        try {
            this.invTransform = transform.createInverse();
        }
        catch (NoninvertibleTransformException e) {
            this.invTransform = null;
        }
        catch (CloneNotSupportedException e) {
            this.invTransform = null;
        }
    }

    public PerspectiveTransform getTransform() {
        return (PerspectiveTransform)this.transform.clone();
    }

    public float[] warpSparseRect(int x, int y, int width, int height, int periodX, int periodY, float[] destRect) {
        if (destRect == null) {
            destRect = new float[2 * ((width + periodX - 1) / periodX) * ((height + periodY - 1) / periodY)];
        }
        double[][] matrix = new double[3][3];
        matrix = this.transform.getMatrix(matrix);
        float m00 = (float)matrix[0][0];
        float m01 = (float)matrix[0][1];
        float m02 = (float)matrix[0][2];
        float m10 = (float)matrix[1][0];
        float m11 = (float)matrix[1][1];
        float m12 = (float)matrix[1][2];
        float m20 = (float)matrix[2][0];
        float m21 = (float)matrix[2][1];
        float m22 = (float)matrix[2][2];
        float dx = m00 * (float)periodX;
        float dy = m10 * (float)periodX;
        float dw = m20 * (float)periodX;
        float sx = (float)x + 0.5f;
        width += x;
        height += y;
        int index = 0;
        for (int j = y; j < height; j += periodY) {
            float sy = (float)j + 0.5f;
            float wx = m00 * sx + m01 * sy + m02;
            float wy = m10 * sx + m11 * sy + m12;
            float w = m20 * sx + m21 * sy + m22;
            for (int i = x; i < width; i += periodX) {
                float ty;
                float tx;
                try {
                    tx = wx / w;
                    ty = wy / w;
                }
                catch (ArithmeticException e) {
                    tx = (float)i + 0.5f;
                    ty = (float)j + 0.5f;
                }
                destRect[index++] = tx - 0.5f;
                destRect[index++] = ty - 0.5f;
                wx += dx;
                wy += dy;
                w += dw;
            }
        }
        return destRect;
    }

    public Rectangle mapDestRect(Rectangle destRect) {
        if (destRect == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        int x0 = destRect.x;
        int x1 = destRect.x + destRect.width;
        int y0 = destRect.y;
        int y1 = destRect.y + destRect.height;
        Point2D[] pts = new Point2D[]{new Point2D.Float(x0, y0), new Point2D.Float(x1, y0), new Point2D.Float(x0, y1), new Point2D.Float(x1, y1)};
        this.transform.transform(pts, 0, pts, 0, 4);
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (int i = 0; i < 4; ++i) {
            int px = (int)pts[i].getX();
            int py = (int)pts[i].getY();
            minX = Math.min(minX, px);
            maxX = Math.max(maxX, px);
            minY = Math.min(minY, py);
            maxY = Math.max(maxY, py);
        }
        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    public Rectangle mapSourceRect(Rectangle srcRect) {
        if (srcRect == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (this.invTransform == null) {
            return null;
        }
        int x0 = srcRect.x;
        int x1 = srcRect.x + srcRect.width;
        int y0 = srcRect.y;
        int y1 = srcRect.y + srcRect.height;
        Point2D[] pts = new Point2D[]{new Point2D.Float(x0, y0), new Point2D.Float(x1, y0), new Point2D.Float(x0, y1), new Point2D.Float(x1, y1)};
        this.invTransform.transform(pts, 0, pts, 0, 4);
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (int i = 0; i < 4; ++i) {
            int px = (int)pts[i].getX();
            int py = (int)pts[i].getY();
            minX = Math.min(minX, px);
            maxX = Math.max(maxX, px);
            minY = Math.min(minY, py);
            maxY = Math.max(maxY, py);
        }
        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    public Point2D mapDestPoint(Point2D destPt) {
        if (destPt == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        return this.transform.transform(destPt, null);
    }

    public Point2D mapSourcePoint(Point2D sourcePt) {
        if (sourcePt == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        return this.invTransform != null ? this.invTransform.transform(sourcePt, null) : null;
    }
}

