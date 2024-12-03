/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.io.Serializable;
import javax.media.jai.JaiI18N;

public final class PerspectiveTransform
implements Cloneable,
Serializable {
    private static final double PERSPECTIVE_DIVIDE_EPSILON = 1.0E-10;
    double m00;
    double m01;
    double m02;
    double m10;
    double m11;
    double m12;
    double m20;
    double m21;
    double m22;

    public PerspectiveTransform() {
        this.m22 = 1.0;
        this.m11 = 1.0;
        this.m00 = 1.0;
        this.m21 = 0.0;
        this.m20 = 0.0;
        this.m12 = 0.0;
        this.m10 = 0.0;
        this.m02 = 0.0;
        this.m01 = 0.0;
    }

    public PerspectiveTransform(float m00, float m01, float m02, float m10, float m11, float m12, float m20, float m21, float m22) {
        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
    }

    public PerspectiveTransform(double m00, double m01, double m02, double m10, double m11, double m12, double m20, double m21, double m22) {
        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
    }

    public PerspectiveTransform(float[] flatmatrix) {
        if (flatmatrix == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.m00 = flatmatrix[0];
        this.m01 = flatmatrix[1];
        this.m02 = flatmatrix[2];
        this.m10 = flatmatrix[3];
        this.m11 = flatmatrix[4];
        this.m12 = flatmatrix[5];
        this.m20 = flatmatrix[6];
        this.m21 = flatmatrix[7];
        this.m22 = flatmatrix[8];
    }

    public PerspectiveTransform(float[][] matrix) {
        if (matrix == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.m00 = matrix[0][0];
        this.m01 = matrix[0][1];
        this.m02 = matrix[0][2];
        this.m10 = matrix[1][0];
        this.m11 = matrix[1][1];
        this.m12 = matrix[1][2];
        this.m20 = matrix[2][0];
        this.m21 = matrix[2][1];
        this.m22 = matrix[2][2];
    }

    public PerspectiveTransform(double[] flatmatrix) {
        if (flatmatrix == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.m00 = flatmatrix[0];
        this.m01 = flatmatrix[1];
        this.m02 = flatmatrix[2];
        this.m10 = flatmatrix[3];
        this.m11 = flatmatrix[4];
        this.m12 = flatmatrix[5];
        this.m20 = flatmatrix[6];
        this.m21 = flatmatrix[7];
        this.m22 = flatmatrix[8];
    }

    public PerspectiveTransform(double[][] matrix) {
        if (matrix == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.m00 = matrix[0][0];
        this.m01 = matrix[0][1];
        this.m02 = matrix[0][2];
        this.m10 = matrix[1][0];
        this.m11 = matrix[1][1];
        this.m12 = matrix[1][2];
        this.m20 = matrix[2][0];
        this.m21 = matrix[2][1];
        this.m22 = matrix[2][2];
    }

    public PerspectiveTransform(AffineTransform transform) {
        if (transform == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.m00 = transform.getScaleX();
        this.m01 = transform.getShearX();
        this.m02 = transform.getTranslateX();
        this.m10 = transform.getShearY();
        this.m11 = transform.getScaleY();
        this.m12 = transform.getTranslateY();
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = 1.0;
    }

    private final void makeAdjoint() {
        double m00p = this.m11 * this.m22 - this.m12 * this.m21;
        double m01p = this.m12 * this.m20 - this.m10 * this.m22;
        double m02p = this.m10 * this.m21 - this.m11 * this.m20;
        double m10p = this.m02 * this.m21 - this.m01 * this.m22;
        double m11p = this.m00 * this.m22 - this.m02 * this.m20;
        double m12p = this.m01 * this.m20 - this.m00 * this.m21;
        double m20p = this.m01 * this.m12 - this.m02 * this.m11;
        double m21p = this.m02 * this.m10 - this.m00 * this.m12;
        double m22p = this.m00 * this.m11 - this.m01 * this.m10;
        this.m00 = m00p;
        this.m01 = m10p;
        this.m02 = m20p;
        this.m10 = m01p;
        this.m11 = m11p;
        this.m12 = m21p;
        this.m20 = m02p;
        this.m21 = m12p;
        this.m22 = m22p;
    }

    private final void normalize() {
        double invscale = 1.0 / this.m22;
        this.m00 *= invscale;
        this.m01 *= invscale;
        this.m02 *= invscale;
        this.m10 *= invscale;
        this.m11 *= invscale;
        this.m12 *= invscale;
        this.m20 *= invscale;
        this.m21 *= invscale;
        this.m22 = 1.0;
    }

    private static final void getSquareToQuad(double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3, PerspectiveTransform tx) {
        double dx3 = x0 - x1 + x2 - x3;
        double dy3 = y0 - y1 + y2 - y3;
        tx.m22 = 1.0;
        if (dx3 == 0.0 && dy3 == 0.0) {
            tx.m00 = x1 - x0;
            tx.m01 = x2 - x1;
            tx.m02 = x0;
            tx.m10 = y1 - y0;
            tx.m11 = y2 - y1;
            tx.m12 = y0;
            tx.m20 = 0.0;
            tx.m21 = 0.0;
        } else {
            double dx1 = x1 - x2;
            double dy1 = y1 - y2;
            double dx2 = x3 - x2;
            double dy2 = y3 - y2;
            double invdet = 1.0 / (dx1 * dy2 - dx2 * dy1);
            tx.m20 = (dx3 * dy2 - dx2 * dy3) * invdet;
            tx.m21 = (dx1 * dy3 - dx3 * dy1) * invdet;
            tx.m00 = x1 - x0 + tx.m20 * x1;
            tx.m01 = x3 - x0 + tx.m21 * x3;
            tx.m02 = x0;
            tx.m10 = y1 - y0 + tx.m20 * y1;
            tx.m11 = y3 - y0 + tx.m21 * y3;
            tx.m12 = y0;
        }
    }

    public static PerspectiveTransform getSquareToQuad(double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3) {
        PerspectiveTransform tx = new PerspectiveTransform();
        PerspectiveTransform.getSquareToQuad(x0, y0, x1, y1, x2, y2, x3, y3, tx);
        return tx;
    }

    public static PerspectiveTransform getSquareToQuad(float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3) {
        return PerspectiveTransform.getSquareToQuad((double)x0, (double)y0, (double)x1, (double)y1, (double)x2, (double)y2, (double)x3, (double)y3);
    }

    public static PerspectiveTransform getQuadToSquare(double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3) {
        PerspectiveTransform tx = new PerspectiveTransform();
        PerspectiveTransform.getSquareToQuad(x0, y0, x1, y1, x2, y2, x3, y3, tx);
        tx.makeAdjoint();
        return tx;
    }

    public static PerspectiveTransform getQuadToSquare(float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3) {
        return PerspectiveTransform.getQuadToSquare((double)x0, (double)y0, (double)x1, (double)y1, (double)x2, (double)y2, (double)x3, (double)y3);
    }

    public static PerspectiveTransform getQuadToQuad(double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3, double x0p, double y0p, double x1p, double y1p, double x2p, double y2p, double x3p, double y3p) {
        PerspectiveTransform tx1 = PerspectiveTransform.getQuadToSquare(x0, y0, x1, y1, x2, y2, x3, y3);
        PerspectiveTransform tx2 = PerspectiveTransform.getSquareToQuad(x0p, y0p, x1p, y1p, x2p, y2p, x3p, y3p);
        tx1.concatenate(tx2);
        return tx1;
    }

    public static PerspectiveTransform getQuadToQuad(float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3, float x0p, float y0p, float x1p, float y1p, float x2p, float y2p, float x3p, float y3p) {
        return PerspectiveTransform.getQuadToQuad((double)x0, (double)y0, (double)x1, (double)y1, (double)x2, (double)y2, (double)x3, (double)y3, (double)x0p, (double)y0p, (double)x1p, (double)y1p, (double)x2p, (double)y2p, (double)x3p, (double)y3p);
    }

    public double getDeterminant() {
        return this.m00 * (this.m11 * this.m22 - this.m12 * this.m21) - this.m01 * (this.m10 * this.m22 - this.m12 * this.m20) + this.m02 * (this.m10 * this.m21 - this.m11 * this.m20);
    }

    public double[] getMatrix(double[] flatmatrix) {
        if (flatmatrix == null) {
            flatmatrix = new double[]{this.m00, this.m01, this.m02, this.m10, this.m11, this.m12, this.m20, this.m21, this.m22};
        }
        return flatmatrix;
    }

    public double[][] getMatrix(double[][] matrix) {
        if (matrix == null) {
            matrix = new double[3][3];
        }
        matrix[0][0] = this.m00;
        matrix[0][1] = this.m01;
        matrix[0][2] = this.m02;
        matrix[1][0] = this.m10;
        matrix[1][1] = this.m11;
        matrix[1][2] = this.m12;
        matrix[2][0] = this.m20;
        matrix[2][1] = this.m21;
        matrix[2][2] = this.m22;
        return matrix;
    }

    public void translate(double tx, double ty) {
        PerspectiveTransform Tx = new PerspectiveTransform();
        Tx.setToTranslation(tx, ty);
        this.concatenate(Tx);
    }

    public void rotate(double theta) {
        PerspectiveTransform Tx = new PerspectiveTransform();
        Tx.setToRotation(theta);
        this.concatenate(Tx);
    }

    public void rotate(double theta, double x, double y) {
        PerspectiveTransform Tx = new PerspectiveTransform();
        Tx.setToRotation(theta, x, y);
        this.concatenate(Tx);
    }

    public void scale(double sx, double sy) {
        PerspectiveTransform Tx = new PerspectiveTransform();
        Tx.setToScale(sx, sy);
        this.concatenate(Tx);
    }

    public void shear(double shx, double shy) {
        PerspectiveTransform Tx = new PerspectiveTransform();
        Tx.setToShear(shx, shy);
        this.concatenate(Tx);
    }

    public void setToIdentity() {
        this.m22 = 1.0;
        this.m11 = 1.0;
        this.m00 = 1.0;
        this.m21 = 0.0;
        this.m12 = 0.0;
        this.m20 = 0.0;
        this.m02 = 0.0;
        this.m10 = 0.0;
        this.m01 = 0.0;
    }

    public void setToTranslation(double tx, double ty) {
        this.m00 = 1.0;
        this.m01 = 0.0;
        this.m02 = tx;
        this.m10 = 0.0;
        this.m11 = 1.0;
        this.m12 = ty;
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = 1.0;
    }

    public void setToRotation(double theta) {
        this.m00 = Math.cos(theta);
        this.m01 = -Math.sin(theta);
        this.m02 = 0.0;
        this.m10 = -this.m01;
        this.m11 = this.m00;
        this.m12 = 0.0;
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = 1.0;
    }

    public void setToRotation(double theta, double x, double y) {
        this.setToRotation(theta);
        double sin = this.m10;
        double oneMinusCos = 1.0 - this.m00;
        this.m02 = x * oneMinusCos + y * sin;
        this.m12 = y * oneMinusCos - x * sin;
    }

    public void setToScale(double sx, double sy) {
        this.m00 = sx;
        this.m01 = 0.0;
        this.m02 = 0.0;
        this.m10 = 0.0;
        this.m11 = sy;
        this.m12 = 0.0;
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = 1.0;
    }

    public void setToShear(double shx, double shy) {
        this.m00 = 1.0;
        this.m01 = shx;
        this.m02 = 0.0;
        this.m10 = shy;
        this.m11 = 1.0;
        this.m12 = 0.0;
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = 1.0;
    }

    public void setTransform(AffineTransform Tx) {
        if (Tx == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.m00 = Tx.getScaleX();
        this.m01 = Tx.getShearX();
        this.m02 = Tx.getTranslateX();
        this.m10 = Tx.getShearY();
        this.m11 = Tx.getScaleY();
        this.m12 = Tx.getTranslateY();
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = 1.0;
    }

    public void setTransform(PerspectiveTransform Tx) {
        if (Tx == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.m00 = Tx.m00;
        this.m01 = Tx.m01;
        this.m02 = Tx.m02;
        this.m10 = Tx.m10;
        this.m11 = Tx.m11;
        this.m12 = Tx.m12;
        this.m20 = Tx.m20;
        this.m21 = Tx.m21;
        this.m22 = Tx.m22;
    }

    public void setTransform(float m00, float m10, float m20, float m01, float m11, float m21, float m02, float m12, float m22) {
        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
    }

    public void setTransform(double[][] matrix) {
        if (matrix == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.m00 = matrix[0][0];
        this.m01 = matrix[0][1];
        this.m02 = matrix[0][2];
        this.m10 = matrix[1][0];
        this.m11 = matrix[1][1];
        this.m12 = matrix[1][2];
        this.m20 = matrix[2][0];
        this.m21 = matrix[2][1];
        this.m22 = matrix[2][2];
    }

    public void concatenate(AffineTransform Tx) {
        if (Tx == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        double tx_m00 = Tx.getScaleX();
        double tx_m01 = Tx.getShearX();
        double tx_m02 = Tx.getTranslateX();
        double tx_m10 = Tx.getShearY();
        double tx_m11 = Tx.getScaleY();
        double tx_m12 = Tx.getTranslateY();
        double m00p = this.m00 * tx_m00 + this.m10 * tx_m01 + this.m20 * tx_m02;
        double m01p = this.m01 * tx_m00 + this.m11 * tx_m01 + this.m21 * tx_m02;
        double m02p = this.m02 * tx_m00 + this.m12 * tx_m01 + this.m22 * tx_m02;
        double m10p = this.m00 * tx_m10 + this.m10 * tx_m11 + this.m20 * tx_m12;
        double m11p = this.m01 * tx_m10 + this.m11 * tx_m11 + this.m21 * tx_m12;
        double m12p = this.m02 * tx_m10 + this.m12 * tx_m11 + this.m22 * tx_m12;
        double m20p = this.m20;
        double m21p = this.m21;
        double m22p = this.m22;
        this.m00 = m00p;
        this.m10 = m10p;
        this.m20 = m20p;
        this.m01 = m01p;
        this.m11 = m11p;
        this.m21 = m21p;
        this.m02 = m02p;
        this.m12 = m12p;
        this.m22 = m22p;
    }

    public void concatenate(PerspectiveTransform Tx) {
        if (Tx == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        double m00p = this.m00 * Tx.m00 + this.m10 * Tx.m01 + this.m20 * Tx.m02;
        double m10p = this.m00 * Tx.m10 + this.m10 * Tx.m11 + this.m20 * Tx.m12;
        double m20p = this.m00 * Tx.m20 + this.m10 * Tx.m21 + this.m20 * Tx.m22;
        double m01p = this.m01 * Tx.m00 + this.m11 * Tx.m01 + this.m21 * Tx.m02;
        double m11p = this.m01 * Tx.m10 + this.m11 * Tx.m11 + this.m21 * Tx.m12;
        double m21p = this.m01 * Tx.m20 + this.m11 * Tx.m21 + this.m21 * Tx.m22;
        double m02p = this.m02 * Tx.m00 + this.m12 * Tx.m01 + this.m22 * Tx.m02;
        double m12p = this.m02 * Tx.m10 + this.m12 * Tx.m11 + this.m22 * Tx.m12;
        double m22p = this.m02 * Tx.m20 + this.m12 * Tx.m21 + this.m22 * Tx.m22;
        this.m00 = m00p;
        this.m10 = m10p;
        this.m20 = m20p;
        this.m01 = m01p;
        this.m11 = m11p;
        this.m21 = m21p;
        this.m02 = m02p;
        this.m12 = m12p;
        this.m22 = m22p;
    }

    public void preConcatenate(AffineTransform Tx) {
        if (Tx == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        double tx_m00 = Tx.getScaleX();
        double tx_m01 = Tx.getShearX();
        double tx_m02 = Tx.getTranslateX();
        double tx_m10 = Tx.getShearY();
        double tx_m11 = Tx.getScaleY();
        double tx_m12 = Tx.getTranslateY();
        double m00p = tx_m00 * this.m00 + tx_m10 * this.m01;
        double m01p = tx_m01 * this.m00 + tx_m11 * this.m01;
        double m02p = tx_m02 * this.m00 + tx_m12 * this.m01 + this.m02;
        double m10p = tx_m00 * this.m10 + tx_m10 * this.m11;
        double m11p = tx_m01 * this.m10 + tx_m11 * this.m11;
        double m12p = tx_m02 * this.m10 + tx_m12 * this.m11 + this.m12;
        double m20p = tx_m00 * this.m20 + tx_m10 * this.m21;
        double m21p = tx_m01 * this.m20 + tx_m11 * this.m21;
        double m22p = tx_m02 * this.m20 + tx_m12 * this.m21 + this.m22;
        this.m00 = m00p;
        this.m10 = m10p;
        this.m20 = m20p;
        this.m01 = m01p;
        this.m11 = m11p;
        this.m21 = m21p;
        this.m02 = m02p;
        this.m12 = m12p;
        this.m22 = m22p;
    }

    public void preConcatenate(PerspectiveTransform Tx) {
        if (Tx == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        double m00p = Tx.m00 * this.m00 + Tx.m10 * this.m01 + Tx.m20 * this.m02;
        double m10p = Tx.m00 * this.m10 + Tx.m10 * this.m11 + Tx.m20 * this.m12;
        double m20p = Tx.m00 * this.m20 + Tx.m10 * this.m21 + Tx.m20 * this.m22;
        double m01p = Tx.m01 * this.m00 + Tx.m11 * this.m01 + Tx.m21 * this.m02;
        double m11p = Tx.m01 * this.m10 + Tx.m11 * this.m11 + Tx.m21 * this.m12;
        double m21p = Tx.m01 * this.m20 + Tx.m11 * this.m21 + Tx.m21 * this.m22;
        double m02p = Tx.m02 * this.m00 + Tx.m12 * this.m01 + Tx.m22 * this.m02;
        double m12p = Tx.m02 * this.m10 + Tx.m12 * this.m11 + Tx.m22 * this.m12;
        double m22p = Tx.m02 * this.m20 + Tx.m12 * this.m21 + Tx.m22 * this.m22;
        this.m00 = m00p;
        this.m10 = m10p;
        this.m20 = m20p;
        this.m01 = m01p;
        this.m11 = m11p;
        this.m21 = m21p;
        this.m02 = m02p;
        this.m12 = m12p;
        this.m22 = m22p;
    }

    public PerspectiveTransform createInverse() throws NoninvertibleTransformException, CloneNotSupportedException {
        PerspectiveTransform tx = (PerspectiveTransform)this.clone();
        tx.makeAdjoint();
        if (Math.abs(tx.m22) < 1.0E-10) {
            throw new NoninvertibleTransformException(JaiI18N.getString("PerspectiveTransform0"));
        }
        tx.normalize();
        return tx;
    }

    public PerspectiveTransform createAdjoint() throws CloneNotSupportedException {
        PerspectiveTransform tx = (PerspectiveTransform)this.clone();
        tx.makeAdjoint();
        return tx;
    }

    public Point2D transform(Point2D ptSrc, Point2D ptDst) {
        if (ptSrc == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (ptDst == null) {
            ptDst = ptSrc instanceof Point2D.Double ? new Point2D.Double() : new Point2D.Float();
        }
        double x = ptSrc.getX();
        double y = ptSrc.getY();
        double w = this.m20 * x + this.m21 * y + this.m22;
        ptDst.setLocation((this.m00 * x + this.m01 * y + this.m02) / w, (this.m10 * x + this.m11 * y + this.m12) / w);
        return ptDst;
    }

    public void transform(Point2D[] ptSrc, int srcOff, Point2D[] ptDst, int dstOff, int numPts) {
        if (ptSrc == null || ptDst == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        while (numPts-- > 0) {
            double y;
            double x;
            double w;
            Point2D dst;
            Point2D src = ptSrc[srcOff++];
            if ((dst = ptDst[dstOff++]) == null) {
                dst = src instanceof Point2D.Double ? new Point2D.Double() : new Point2D.Float();
                ptDst[dstOff - 1] = dst;
            }
            if ((w = this.m20 * (x = src.getX()) + this.m21 * (y = src.getY()) + this.m22) == 0.0) {
                dst.setLocation(x, y);
                continue;
            }
            dst.setLocation((this.m00 * x + this.m01 * y + this.m02) / w, (this.m10 * x + this.m11 * y + this.m12) / w);
        }
    }

    public void transform(float[] srcPts, int srcOff, float[] dstPts, int dstOff, int numPts) {
        if (srcPts == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (dstPts == null) {
            dstPts = new float[numPts * 2 + dstOff];
        }
        while (numPts-- > 0) {
            float y;
            float x;
            double w;
            if ((w = this.m20 * (double)(x = srcPts[srcOff++]) + this.m21 * (double)(y = srcPts[srcOff++]) + this.m22) == 0.0) {
                dstPts[dstOff++] = x;
                dstPts[dstOff++] = y;
                continue;
            }
            dstPts[dstOff++] = (float)((this.m00 * (double)x + this.m01 * (double)y + this.m02) / w);
            dstPts[dstOff++] = (float)((this.m10 * (double)x + this.m11 * (double)y + this.m12) / w);
        }
    }

    public void transform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts) {
        if (srcPts == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (dstPts == null) {
            dstPts = new double[numPts * 2 + dstOff];
        }
        while (numPts-- > 0) {
            double y;
            double x;
            double w;
            if ((w = this.m20 * (x = srcPts[srcOff++]) + this.m21 * (y = srcPts[srcOff++]) + this.m22) == 0.0) {
                dstPts[dstOff++] = x;
                dstPts[dstOff++] = y;
                continue;
            }
            dstPts[dstOff++] = (this.m00 * x + this.m01 * y + this.m02) / w;
            dstPts[dstOff++] = (this.m10 * x + this.m11 * y + this.m12) / w;
        }
    }

    public void transform(float[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts) {
        if (srcPts == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (dstPts == null) {
            dstPts = new double[numPts * 2 + dstOff];
        }
        while (numPts-- > 0) {
            float y;
            float x;
            double w;
            if ((w = this.m20 * (double)(x = srcPts[srcOff++]) + this.m21 * (double)(y = srcPts[srcOff++]) + this.m22) == 0.0) {
                dstPts[dstOff++] = x;
                dstPts[dstOff++] = y;
                continue;
            }
            dstPts[dstOff++] = (this.m00 * (double)x + this.m01 * (double)y + this.m02) / w;
            dstPts[dstOff++] = (this.m10 * (double)x + this.m11 * (double)y + this.m12) / w;
        }
    }

    public void transform(double[] srcPts, int srcOff, float[] dstPts, int dstOff, int numPts) {
        if (srcPts == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (dstPts == null) {
            dstPts = new float[numPts * 2 + dstOff];
        }
        while (numPts-- > 0) {
            double y;
            double x;
            double w;
            if ((w = this.m20 * (x = srcPts[srcOff++]) + this.m21 * (y = srcPts[srcOff++]) + this.m22) == 0.0) {
                dstPts[dstOff++] = (float)x;
                dstPts[dstOff++] = (float)y;
                continue;
            }
            dstPts[dstOff++] = (float)((this.m00 * x + this.m01 * y + this.m02) / w);
            dstPts[dstOff++] = (float)((this.m10 * x + this.m11 * y + this.m12) / w);
        }
    }

    public Point2D inverseTransform(Point2D ptSrc, Point2D ptDst) throws NoninvertibleTransformException {
        double w;
        if (ptSrc == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (ptDst == null) {
            ptDst = ptSrc instanceof Point2D.Double ? new Point2D.Double() : new Point2D.Float();
        }
        double x = ptSrc.getX();
        double y = ptSrc.getY();
        double tmp_x = (this.m11 * this.m22 - this.m12 * this.m21) * x + (this.m02 * this.m21 - this.m01 * this.m22) * y + (this.m01 * this.m12 - this.m02 * this.m11);
        double tmp_y = (this.m12 * this.m20 - this.m10 * this.m22) * x + (this.m00 * this.m22 - this.m02 * this.m20) * y + (this.m02 * this.m10 - this.m00 * this.m12);
        double wabs = w = (this.m10 * this.m21 - this.m11 * this.m20) * x + (this.m01 * this.m20 - this.m00 * this.m21) * y + (this.m00 * this.m11 - this.m01 * this.m10);
        if (w < 0.0) {
            wabs = -w;
        }
        if (wabs < 1.0E-10) {
            throw new NoninvertibleTransformException(JaiI18N.getString("PerspectiveTransform1"));
        }
        ptDst.setLocation(tmp_x / w, tmp_y / w);
        return ptDst;
    }

    public void inverseTransform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts) throws NoninvertibleTransformException {
        if (srcPts == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (dstPts == null) {
            dstPts = new double[numPts * 2 + dstOff];
        }
        while (numPts-- > 0) {
            double w;
            double x = srcPts[srcOff++];
            double y = srcPts[srcOff++];
            double tmp_x = (this.m11 * this.m22 - this.m12 * this.m21) * x + (this.m02 * this.m21 - this.m01 * this.m22) * y + (this.m01 * this.m12 - this.m02 * this.m11);
            double tmp_y = (this.m12 * this.m20 - this.m10 * this.m22) * x + (this.m00 * this.m22 - this.m02 * this.m20) * y + (this.m02 * this.m10 - this.m00 * this.m12);
            double wabs = w = (this.m10 * this.m21 - this.m11 * this.m20) * x + (this.m01 * this.m20 - this.m00 * this.m21) * y + (this.m00 * this.m11 - this.m01 * this.m10);
            if (w < 0.0) {
                wabs = -w;
            }
            if (wabs < 1.0E-10) {
                throw new NoninvertibleTransformException(JaiI18N.getString("PerspectiveTransform1"));
            }
            dstPts[dstOff++] = tmp_x / w;
            dstPts[dstOff++] = tmp_y / w;
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Perspective transform matrix\n");
        sb.append(this.m00);
        sb.append("\t");
        sb.append(this.m01);
        sb.append("\t");
        sb.append(this.m02);
        sb.append("\n");
        sb.append(this.m10);
        sb.append("\t");
        sb.append(this.m11);
        sb.append("\t");
        sb.append(this.m12);
        sb.append("\n");
        sb.append(this.m20);
        sb.append("\t");
        sb.append(this.m21);
        sb.append("\t");
        sb.append(this.m22);
        sb.append("\n");
        return new String(sb);
    }

    public boolean isIdentity() {
        return this.m01 == 0.0 && this.m02 == 0.0 && this.m10 == 0.0 && this.m12 == 0.0 && this.m20 == 0.0 && this.m21 == 0.0 && this.m22 != 0.0 && this.m00 / this.m22 == 1.0 && this.m11 / this.m22 == 1.0;
    }

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof PerspectiveTransform)) {
            return false;
        }
        PerspectiveTransform a = (PerspectiveTransform)obj;
        return this.m00 == a.m00 && this.m10 == a.m10 && this.m20 == a.m20 && this.m01 == a.m01 && this.m11 == a.m11 && this.m21 == a.m21 && this.m02 == a.m02 && this.m12 == a.m12 && this.m22 == a.m22;
    }
}

