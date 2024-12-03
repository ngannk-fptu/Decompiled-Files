/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.shading;

import java.awt.geom.Point2D;
import java.util.List;
import org.apache.pdfbox.pdmodel.graphics.shading.CoordinateColorPair;
import org.apache.pdfbox.pdmodel.graphics.shading.Patch;
import org.apache.pdfbox.pdmodel.graphics.shading.ShadedTriangle;

class TensorPatch
extends Patch {
    protected TensorPatch(Point2D[] tcp, float[][] color) {
        super(color);
        this.controlPoints = this.reshapeControlPoints(tcp);
        this.level = this.calcLevel();
        this.listOfTriangles = this.getTriangles();
    }

    private Point2D[][] reshapeControlPoints(Point2D[] tcp) {
        int i;
        Point2D[][] square = new Point2D[4][4];
        for (i = 0; i <= 3; ++i) {
            square[0][i] = tcp[i];
            square[3][i] = tcp[9 - i];
        }
        for (i = 1; i <= 2; ++i) {
            square[i][0] = tcp[12 - i];
            square[i][2] = tcp[12 + i];
            square[i][3] = tcp[3 + i];
        }
        square[1][1] = tcp[12];
        square[2][1] = tcp[15];
        return square;
    }

    private int[] calcLevel() {
        int[] l = new int[]{4, 4};
        Point2D[] ctlC1 = new Point2D[4];
        Point2D[] ctlC2 = new Point2D[4];
        for (int j = 0; j < 4; ++j) {
            ctlC1[j] = this.controlPoints[j][0];
            ctlC2[j] = this.controlPoints[j][3];
        }
        if (this.isEdgeALine(ctlC1) && this.isEdgeALine(ctlC2) && !this.isOnSameSideCC(this.controlPoints[1][1]) && !this.isOnSameSideCC(this.controlPoints[1][2]) && !this.isOnSameSideCC(this.controlPoints[2][1]) && !this.isOnSameSideCC(this.controlPoints[2][2])) {
            double lc1 = this.getLen(ctlC1[0], ctlC1[3]);
            double lc2 = this.getLen(ctlC2[0], ctlC2[3]);
            if (!(lc1 > 800.0) && !(lc2 > 800.0)) {
                l[0] = lc1 > 400.0 || lc2 > 400.0 ? 3 : (lc1 > 200.0 || lc2 > 200.0 ? 2 : 1);
            }
        }
        if (this.isEdgeALine(this.controlPoints[0]) && this.isEdgeALine(this.controlPoints[3]) && !this.isOnSameSideDD(this.controlPoints[1][1]) && !this.isOnSameSideDD(this.controlPoints[1][2]) && !this.isOnSameSideDD(this.controlPoints[2][1]) && !this.isOnSameSideDD(this.controlPoints[2][2])) {
            double ld1 = this.getLen(this.controlPoints[0][0], this.controlPoints[0][3]);
            double ld2 = this.getLen(this.controlPoints[3][0], this.controlPoints[3][3]);
            if (!(ld1 > 800.0) && !(ld2 > 800.0)) {
                l[1] = ld1 > 400.0 || ld2 > 400.0 ? 3 : (ld1 > 200.0 || ld2 > 200.0 ? 2 : 1);
            }
        }
        return l;
    }

    private boolean isOnSameSideCC(Point2D p) {
        double cc = this.edgeEquationValue(p, this.controlPoints[0][0], this.controlPoints[3][0]) * this.edgeEquationValue(p, this.controlPoints[0][3], this.controlPoints[3][3]);
        return cc > 0.0;
    }

    private boolean isOnSameSideDD(Point2D p) {
        double dd = this.edgeEquationValue(p, this.controlPoints[0][0], this.controlPoints[0][3]) * this.edgeEquationValue(p, this.controlPoints[3][0], this.controlPoints[3][3]);
        return dd > 0.0;
    }

    private List<ShadedTriangle> getTriangles() {
        CoordinateColorPair[][] patchCC = this.getPatchCoordinatesColor();
        return this.getShadedTriangles(patchCC);
    }

    @Override
    protected Point2D[] getFlag1Edge() {
        Point2D[] implicitEdge = new Point2D[4];
        for (int i = 0; i < 4; ++i) {
            implicitEdge[i] = this.controlPoints[i][3];
        }
        return implicitEdge;
    }

    @Override
    protected Point2D[] getFlag2Edge() {
        Point2D[] implicitEdge = new Point2D[4];
        for (int i = 0; i < 4; ++i) {
            implicitEdge[i] = this.controlPoints[3][3 - i];
        }
        return implicitEdge;
    }

    @Override
    protected Point2D[] getFlag3Edge() {
        Point2D[] implicitEdge = new Point2D[4];
        for (int i = 0; i < 4; ++i) {
            implicitEdge[i] = this.controlPoints[3 - i][0];
        }
        return implicitEdge;
    }

    private CoordinateColorPair[][] getPatchCoordinatesColor() {
        int numberOfColorComponents = this.cornerColor[0].length;
        double[][] bernsteinPolyU = this.getBernsteinPolynomials(this.level[0]);
        int szU = bernsteinPolyU[0].length;
        double[][] bernsteinPolyV = this.getBernsteinPolynomials(this.level[1]);
        int szV = bernsteinPolyV[0].length;
        CoordinateColorPair[][] patchCC = new CoordinateColorPair[szV][szU];
        double stepU = 1.0 / (double)(szU - 1);
        double stepV = 1.0 / (double)(szV - 1);
        double v = -stepV;
        for (int k = 0; k < szV; ++k) {
            v += stepV;
            double u = -stepU;
            for (int l = 0; l < szU; ++l) {
                double tmpx = 0.0;
                double tmpy = 0.0;
                for (int i = 0; i < 4; ++i) {
                    for (int j = 0; j < 4; ++j) {
                        tmpx += this.controlPoints[i][j].getX() * bernsteinPolyU[i][l] * bernsteinPolyV[j][k];
                        tmpy += this.controlPoints[i][j].getY() * bernsteinPolyU[i][l] * bernsteinPolyV[j][k];
                    }
                }
                Point2D.Double tmpC = new Point2D.Double(tmpx, tmpy);
                u += stepU;
                float[] paramSC = new float[numberOfColorComponents];
                for (int ci = 0; ci < numberOfColorComponents; ++ci) {
                    paramSC[ci] = (float)((1.0 - v) * ((1.0 - u) * (double)this.cornerColor[0][ci] + u * (double)this.cornerColor[3][ci]) + v * ((1.0 - u) * (double)this.cornerColor[1][ci] + u * (double)this.cornerColor[2][ci]));
                }
                patchCC[k][l] = new CoordinateColorPair(tmpC, paramSC);
            }
        }
        return patchCC;
    }

    private double[][] getBernsteinPolynomials(int lvl) {
        int sz = (1 << lvl) + 1;
        double[][] poly = new double[4][sz];
        double step = 1.0 / (double)(sz - 1);
        double t = -step;
        for (int i = 0; i < sz; ++i) {
            poly[0][i] = (1.0 - (t += step)) * (1.0 - t) * (1.0 - t);
            poly[1][i] = 3.0 * t * (1.0 - t) * (1.0 - t);
            poly[2][i] = 3.0 * t * t * (1.0 - t);
            poly[3][i] = t * t * t;
        }
        return poly;
    }
}

