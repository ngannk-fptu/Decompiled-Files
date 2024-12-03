/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.util;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Arrays;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.util.Vector;

public final class Matrix
implements Cloneable {
    public static final int SIZE = 9;
    private float[] single;
    private static final float MAX_FLOAT_VALUE = Float.MAX_VALUE;

    public Matrix() {
        this.single = new float[]{1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f};
    }

    private Matrix(float[] src) {
        this.single = src;
    }

    public Matrix(COSArray array) {
        this.single = new float[9];
        this.single[0] = ((COSNumber)array.getObject(0)).floatValue();
        this.single[1] = ((COSNumber)array.getObject(1)).floatValue();
        this.single[3] = ((COSNumber)array.getObject(2)).floatValue();
        this.single[4] = ((COSNumber)array.getObject(3)).floatValue();
        this.single[6] = ((COSNumber)array.getObject(4)).floatValue();
        this.single[7] = ((COSNumber)array.getObject(5)).floatValue();
        this.single[8] = 1.0f;
    }

    public Matrix(float a, float b, float c, float d, float e, float f) {
        this.single = new float[9];
        this.single[0] = a;
        this.single[1] = b;
        this.single[3] = c;
        this.single[4] = d;
        this.single[6] = e;
        this.single[7] = f;
        this.single[8] = 1.0f;
    }

    public Matrix(AffineTransform at) {
        this.single = new float[9];
        this.single[0] = (float)at.getScaleX();
        this.single[1] = (float)at.getShearY();
        this.single[3] = (float)at.getShearX();
        this.single[4] = (float)at.getScaleY();
        this.single[6] = (float)at.getTranslateX();
        this.single[7] = (float)at.getTranslateY();
        this.single[8] = 1.0f;
    }

    public static Matrix createMatrix(COSBase base) {
        if (!(base instanceof COSArray)) {
            return new Matrix();
        }
        COSArray array = (COSArray)base;
        if (array.size() < 6) {
            return new Matrix();
        }
        for (int i = 0; i < 6; ++i) {
            if (array.getObject(i) instanceof COSNumber) continue;
            return new Matrix();
        }
        return new Matrix(array);
    }

    @Deprecated
    public void reset() {
        Arrays.fill(this.single, 0.0f);
        this.single[0] = 1.0f;
        this.single[4] = 1.0f;
        this.single[8] = 1.0f;
    }

    public AffineTransform createAffineTransform() {
        return new AffineTransform(this.single[0], this.single[1], this.single[3], this.single[4], this.single[6], this.single[7]);
    }

    @Deprecated
    public void setFromAffineTransform(AffineTransform af) {
        this.single[0] = (float)af.getScaleX();
        this.single[1] = (float)af.getShearY();
        this.single[3] = (float)af.getShearX();
        this.single[4] = (float)af.getScaleY();
        this.single[6] = (float)af.getTranslateX();
        this.single[7] = (float)af.getTranslateY();
    }

    public float getValue(int row, int column) {
        return this.single[row * 3 + column];
    }

    public void setValue(int row, int column, float value) {
        this.single[row * 3 + column] = value;
    }

    public float[][] getValues() {
        float[][] retval = new float[3][3];
        retval[0][0] = this.single[0];
        retval[0][1] = this.single[1];
        retval[0][2] = this.single[2];
        retval[1][0] = this.single[3];
        retval[1][1] = this.single[4];
        retval[1][2] = this.single[5];
        retval[2][0] = this.single[6];
        retval[2][1] = this.single[7];
        retval[2][2] = this.single[8];
        return retval;
    }

    @Deprecated
    public double[][] getValuesAsDouble() {
        double[][] retval = new double[3][3];
        retval[0][0] = this.single[0];
        retval[0][1] = this.single[1];
        retval[0][2] = this.single[2];
        retval[1][0] = this.single[3];
        retval[1][1] = this.single[4];
        retval[1][2] = this.single[5];
        retval[2][0] = this.single[6];
        retval[2][1] = this.single[7];
        retval[2][2] = this.single[8];
        return retval;
    }

    public void concatenate(Matrix matrix) {
        matrix.multiply(this, this);
    }

    public void translate(Vector vector) {
        this.concatenate(Matrix.getTranslateInstance(vector.getX(), vector.getY()));
    }

    public void translate(float tx, float ty) {
        this.concatenate(Matrix.getTranslateInstance(tx, ty));
    }

    public void scale(float sx, float sy) {
        this.concatenate(Matrix.getScaleInstance(sx, sy));
    }

    public void rotate(double theta) {
        this.concatenate(Matrix.getRotateInstance(theta, 0.0f, 0.0f));
    }

    public Matrix multiply(Matrix other) {
        return this.multiply(other, new Matrix());
    }

    @Deprecated
    public Matrix multiply(Matrix other, Matrix result) {
        float[] c = result != null && result != other && result != this ? result.single : new float[9];
        this.multiplyArrays(this.single, other.single, c);
        if (!(Matrix.isFinite(c[0]) && Matrix.isFinite(c[1]) && Matrix.isFinite(c[2]) && Matrix.isFinite(c[3]) && Matrix.isFinite(c[4]) && Matrix.isFinite(c[5]) && Matrix.isFinite(c[6]) && Matrix.isFinite(c[7]) && Matrix.isFinite(c[8]))) {
            throw new IllegalArgumentException("Multiplying two matrices produces illegal values");
        }
        if (result == null) {
            return new Matrix(c);
        }
        result.single = c;
        return result;
    }

    private static boolean isFinite(float f) {
        return Math.abs(f) <= Float.MAX_VALUE;
    }

    private void multiplyArrays(float[] a, float[] b, float[] c) {
        c[0] = a[0] * b[0] + a[1] * b[3] + a[2] * b[6];
        c[1] = a[0] * b[1] + a[1] * b[4] + a[2] * b[7];
        c[2] = a[0] * b[2] + a[1] * b[5] + a[2] * b[8];
        c[3] = a[3] * b[0] + a[4] * b[3] + a[5] * b[6];
        c[4] = a[3] * b[1] + a[4] * b[4] + a[5] * b[7];
        c[5] = a[3] * b[2] + a[4] * b[5] + a[5] * b[8];
        c[6] = a[6] * b[0] + a[7] * b[3] + a[8] * b[6];
        c[7] = a[6] * b[1] + a[7] * b[4] + a[8] * b[7];
        c[8] = a[6] * b[2] + a[7] * b[5] + a[8] * b[8];
    }

    public void transform(Point2D point) {
        float x = (float)point.getX();
        float y = (float)point.getY();
        float a = this.single[0];
        float b = this.single[1];
        float c = this.single[3];
        float d = this.single[4];
        float e = this.single[6];
        float f = this.single[7];
        point.setLocation(x * a + y * c + e, x * b + y * d + f);
    }

    public Point2D.Float transformPoint(float x, float y) {
        float a = this.single[0];
        float b = this.single[1];
        float c = this.single[3];
        float d = this.single[4];
        float e = this.single[6];
        float f = this.single[7];
        return new Point2D.Float(x * a + y * c + e, x * b + y * d + f);
    }

    public Vector transform(Vector vector) {
        float a = this.single[0];
        float b = this.single[1];
        float c = this.single[3];
        float d = this.single[4];
        float e = this.single[6];
        float f = this.single[7];
        float x = vector.getX();
        float y = vector.getY();
        return new Vector(x * a + y * c + e, x * b + y * d + f);
    }

    @Deprecated
    public Matrix extractScaling() {
        Matrix matrix = new Matrix();
        matrix.single[0] = this.single[0];
        matrix.single[4] = this.single[4];
        return matrix;
    }

    public static Matrix getScaleInstance(float x, float y) {
        return new Matrix(x, 0.0f, 0.0f, y, 0.0f, 0.0f);
    }

    @Deprecated
    public Matrix extractTranslating() {
        Matrix matrix = new Matrix();
        matrix.single[6] = this.single[6];
        matrix.single[7] = this.single[7];
        return matrix;
    }

    @Deprecated
    public static Matrix getTranslatingInstance(float x, float y) {
        return new Matrix(1.0f, 0.0f, 0.0f, 1.0f, x, y);
    }

    public static Matrix getTranslateInstance(float x, float y) {
        return new Matrix(1.0f, 0.0f, 0.0f, 1.0f, x, y);
    }

    public static Matrix getRotateInstance(double theta, float tx, float ty) {
        float cosTheta = (float)Math.cos(theta);
        float sinTheta = (float)Math.sin(theta);
        return new Matrix(cosTheta, sinTheta, -sinTheta, cosTheta, tx, ty);
    }

    public static Matrix concatenate(Matrix a, Matrix b) {
        return b.multiply(a);
    }

    public Matrix clone() {
        return new Matrix((float[])this.single.clone());
    }

    public float getScalingFactorX() {
        if (this.single[1] != 0.0f) {
            return (float)Math.sqrt(Math.pow(this.single[0], 2.0) + Math.pow(this.single[1], 2.0));
        }
        return this.single[0];
    }

    public float getScalingFactorY() {
        if (this.single[3] != 0.0f) {
            return (float)Math.sqrt(Math.pow(this.single[3], 2.0) + Math.pow(this.single[4], 2.0));
        }
        return this.single[4];
    }

    public float getScaleX() {
        return this.single[0];
    }

    public float getShearY() {
        return this.single[1];
    }

    public float getShearX() {
        return this.single[3];
    }

    public float getScaleY() {
        return this.single[4];
    }

    public float getTranslateX() {
        return this.single[6];
    }

    public float getTranslateY() {
        return this.single[7];
    }

    @Deprecated
    public float getXPosition() {
        return this.single[6];
    }

    @Deprecated
    public float getYPosition() {
        return this.single[7];
    }

    public COSArray toCOSArray() {
        COSArray array = new COSArray();
        array.add(new COSFloat(this.single[0]));
        array.add(new COSFloat(this.single[1]));
        array.add(new COSFloat(this.single[3]));
        array.add(new COSFloat(this.single[4]));
        array.add(new COSFloat(this.single[6]));
        array.add(new COSFloat(this.single[7]));
        return array;
    }

    public String toString() {
        return "[" + this.single[0] + "," + this.single[1] + "," + this.single[3] + "," + this.single[4] + "," + this.single[6] + "," + this.single[7] + "]";
    }

    public int hashCode() {
        return Arrays.hashCode(this.single);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        return Arrays.equals(this.single, ((Matrix)obj).single);
    }
}

