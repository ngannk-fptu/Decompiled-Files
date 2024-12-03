/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import com.sun.media.jai.util.PolyWarpSolver;
import java.awt.geom.Point2D;
import javax.media.jai.JaiI18N;
import javax.media.jai.Warp;
import javax.media.jai.WarpAffine;
import javax.media.jai.WarpCubic;
import javax.media.jai.WarpGeneralPolynomial;
import javax.media.jai.WarpQuadratic;

public abstract class WarpPolynomial
extends Warp {
    protected float[] xCoeffs;
    protected float[] yCoeffs;
    protected float preScaleX;
    protected float preScaleY;
    protected float postScaleX;
    protected float postScaleY;
    protected int degree;

    public WarpPolynomial(float[] xCoeffs, float[] yCoeffs, float preScaleX, float preScaleY, float postScaleX, float postScaleY) {
        int numCoeffs;
        if (xCoeffs == null || yCoeffs == null || xCoeffs.length < 1 || yCoeffs.length < 1 || xCoeffs.length != yCoeffs.length) {
            throw new IllegalArgumentException(JaiI18N.getString("WarpPolynomial0"));
        }
        this.degree = -1;
        for (numCoeffs = xCoeffs.length; numCoeffs > 0; numCoeffs -= this.degree + 1) {
            ++this.degree;
        }
        if (numCoeffs != 0) {
            throw new IllegalArgumentException(JaiI18N.getString("WarpPolynomial0"));
        }
        this.xCoeffs = (float[])xCoeffs.clone();
        this.yCoeffs = (float[])yCoeffs.clone();
        this.preScaleX = preScaleX;
        this.preScaleY = preScaleY;
        this.postScaleX = postScaleX;
        this.postScaleY = postScaleY;
    }

    public WarpPolynomial(float[] xCoeffs, float[] yCoeffs) {
        this(xCoeffs, yCoeffs, 1.0f, 1.0f, 1.0f, 1.0f);
    }

    public float[] getXCoeffs() {
        return (float[])this.xCoeffs.clone();
    }

    public float[] getYCoeffs() {
        return (float[])this.yCoeffs.clone();
    }

    public float[][] getCoeffs() {
        float[][] coeffs = new float[][]{(float[])this.xCoeffs.clone(), (float[])this.yCoeffs.clone()};
        return coeffs;
    }

    public float getPreScaleX() {
        return this.preScaleX;
    }

    public float getPreScaleY() {
        return this.preScaleY;
    }

    public float getPostScaleX() {
        return this.postScaleX;
    }

    public float getPostScaleY() {
        return this.postScaleY;
    }

    public int getDegree() {
        return this.degree;
    }

    public static WarpPolynomial createWarp(float[] sourceCoords, int sourceOffset, float[] destCoords, int destOffset, int numCoords, float preScaleX, float preScaleY, float postScaleX, float postScaleY, int degree) {
        int minNumPoints = (degree + 1) * (degree + 2);
        if (sourceOffset + minNumPoints > sourceCoords.length || destOffset + minNumPoints > destCoords.length) {
            throw new IllegalArgumentException(JaiI18N.getString("WarpPolynomial1"));
        }
        float[] coeffs = PolyWarpSolver.getCoeffs(sourceCoords, sourceOffset, destCoords, destOffset, numCoords, preScaleX, preScaleY, postScaleX, postScaleY, degree);
        int numCoeffs = coeffs.length / 2;
        float[] xCoeffs = new float[numCoeffs];
        float[] yCoeffs = new float[numCoeffs];
        for (int i = 0; i < numCoeffs; ++i) {
            xCoeffs[i] = coeffs[i];
            yCoeffs[i] = coeffs[i + numCoeffs];
        }
        if (degree == 1) {
            return new WarpAffine(xCoeffs, yCoeffs, preScaleX, preScaleY, postScaleX, postScaleY);
        }
        if (degree == 2) {
            return new WarpQuadratic(xCoeffs, yCoeffs, preScaleX, preScaleY, postScaleX, postScaleY);
        }
        if (degree == 3) {
            return new WarpCubic(xCoeffs, yCoeffs, preScaleX, preScaleY, postScaleX, postScaleY);
        }
        return new WarpGeneralPolynomial(xCoeffs, yCoeffs, preScaleX, preScaleY, postScaleX, postScaleY);
    }

    public Point2D mapDestPoint(Point2D destPt) {
        if (destPt == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        double dx = (destPt.getX() + 0.5) * (double)this.preScaleX;
        double dy = (destPt.getY() + 0.5) * (double)this.preScaleY;
        double sx = 0.0;
        double sy = 0.0;
        int c = 0;
        for (int nx = 0; nx <= this.degree; ++nx) {
            for (int ny = 0; ny <= nx; ++ny) {
                double t = Math.pow(dx, nx - ny) * Math.pow(dy, ny);
                sx += (double)this.xCoeffs[c] * t;
                sy += (double)this.yCoeffs[c] * t;
                ++c;
            }
        }
        Point2D pt = (Point2D)destPt.clone();
        pt.setLocation(sx * (double)this.postScaleX - 0.5, sy * (double)this.postScaleY - 0.5);
        return pt;
    }
}

