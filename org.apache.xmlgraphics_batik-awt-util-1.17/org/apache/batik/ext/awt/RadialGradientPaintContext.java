/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import org.apache.batik.ext.awt.MultipleGradientPaint;
import org.apache.batik.ext.awt.MultipleGradientPaintContext;
import org.apache.batik.ext.awt.RadialGradientPaint;

final class RadialGradientPaintContext
extends MultipleGradientPaintContext {
    private boolean isSimpleFocus = false;
    private boolean isNonCyclic = false;
    private float radius;
    private float centerX;
    private float centerY;
    private float focusX;
    private float focusY;
    private float radiusSq;
    private float constA;
    private float constB;
    private float trivial;
    private static final int FIXED_POINT_IMPL = 1;
    private static final int DEFAULT_IMPL = 2;
    private static final int ANTI_ALIAS_IMPL = 3;
    private int fillMethod;
    private static final float SCALEBACK = 0.999f;
    private float invSqStepFloat;
    private static final int MAX_PRECISION = 256;
    private int[] sqrtLutFixed = new int[256];

    public RadialGradientPaintContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform t, RenderingHints hints, float cx, float cy, float r, float fx, float fy, float[] fractions, Color[] colors, MultipleGradientPaint.CycleMethodEnum cycleMethod, MultipleGradientPaint.ColorSpaceEnum colorSpace) throws NoninvertibleTransformException {
        super(cm, deviceBounds, userBounds, t, hints, fractions, colors, cycleMethod, colorSpace);
        this.centerX = cx;
        this.centerY = cy;
        this.focusX = fx;
        this.focusY = fy;
        this.radius = r;
        this.isSimpleFocus = this.focusX == this.centerX && this.focusY == this.centerY;
        this.isNonCyclic = cycleMethod == RadialGradientPaint.NO_CYCLE;
        this.radiusSq = this.radius * this.radius;
        float dX = this.focusX - this.centerX;
        float dY = this.focusY - this.centerY;
        double dist = Math.sqrt(dX * dX + dY * dY);
        if (dist > (double)(this.radius * 0.999f)) {
            double angle = Math.atan2(dY, dX);
            this.focusX = (float)((double)(0.999f * this.radius) * Math.cos(angle)) + this.centerX;
            this.focusY = (float)((double)(0.999f * this.radius) * Math.sin(angle)) + this.centerY;
        }
        dX = this.focusX - this.centerX;
        this.trivial = (float)Math.sqrt(this.radiusSq - dX * dX);
        this.constA = this.a02 - this.centerX;
        this.constB = this.a12 - this.centerY;
        Object colorRend = hints.get(RenderingHints.KEY_COLOR_RENDERING);
        Object rend = hints.get(RenderingHints.KEY_RENDERING);
        this.fillMethod = 0;
        if (rend == RenderingHints.VALUE_RENDER_QUALITY || colorRend == RenderingHints.VALUE_COLOR_RENDER_QUALITY) {
            this.fillMethod = 3;
        }
        if (rend == RenderingHints.VALUE_RENDER_SPEED || colorRend == RenderingHints.VALUE_COLOR_RENDER_SPEED) {
            this.fillMethod = 2;
        }
        if (this.fillMethod == 0) {
            this.fillMethod = 2;
        }
        if (this.fillMethod == 2 && this.isSimpleFocus && this.isNonCyclic && this.isSimpleLookup) {
            this.calculateFixedPointSqrtLookupTable();
            this.fillMethod = 1;
        }
    }

    @Override
    protected void fillRaster(int[] pixels, int off, int adjust, int x, int y, int w, int h) {
        switch (this.fillMethod) {
            case 1: {
                this.fixedPointSimplestCaseNonCyclicFillRaster(pixels, off, adjust, x, y, w, h);
                break;
            }
            case 3: {
                this.antiAliasFillRaster(pixels, off, adjust, x, y, w, h);
                break;
            }
            default: {
                this.cyclicCircularGradientFillRaster(pixels, off, adjust, x, y, w, h);
            }
        }
    }

    private void fixedPointSimplestCaseNonCyclicFillRaster(int[] pixels, int off, int adjust, int x, int y, int w, int h) {
        float iSq = 0.0f;
        float indexFactor = (float)this.fastGradientArraySize / this.radius;
        float constX = this.a00 * (float)x + this.a01 * (float)y + this.constA;
        float constY = this.a10 * (float)x + this.a11 * (float)y + this.constB;
        float deltaX = indexFactor * this.a00;
        float deltaY = indexFactor * this.a10;
        int fixedArraySizeSq = this.fastGradientArraySize * this.fastGradientArraySize;
        int indexer = off;
        float temp = deltaX * deltaX + deltaY * deltaY;
        float gDeltaDelta = temp * 2.0f;
        if (temp > (float)fixedArraySizeSq) {
            int val = this.gradientOverflow;
            for (int j = 0; j < h; ++j) {
                int end = indexer + w;
                while (indexer < end) {
                    pixels[indexer] = val;
                    ++indexer;
                }
                indexer += adjust;
            }
            return;
        }
        for (int j = 0; j < h; ++j) {
            float dX = indexFactor * (this.a01 * (float)j + constX);
            float dY = indexFactor * (this.a11 * (float)j + constY);
            float g = dY * dY + dX * dX;
            float gDelta = (deltaY * dY + deltaX * dX) * 2.0f + temp;
            int end = indexer + w;
            while (indexer < end) {
                if (g >= (float)fixedArraySizeSq) {
                    pixels[indexer] = this.gradientOverflow;
                } else {
                    iSq = g * this.invSqStepFloat;
                    int iSqInt = (int)iSq;
                    int gIndex = this.sqrtLutFixed[iSqInt];
                    gIndex += (int)((iSq -= (float)iSqInt) * (float)(this.sqrtLutFixed[iSqInt + 1] - gIndex));
                    pixels[indexer] = this.gradient[gIndex];
                }
                g += gDelta;
                gDelta += gDeltaDelta;
                ++indexer;
            }
            indexer += adjust;
        }
    }

    private void calculateFixedPointSqrtLookupTable() {
        int i;
        float sqStepFloat = (float)(this.fastGradientArraySize * this.fastGradientArraySize) / 254.0f;
        int[] workTbl = this.sqrtLutFixed;
        for (i = 0; i < 255; ++i) {
            workTbl[i] = (int)Math.sqrt((float)i * sqStepFloat);
        }
        workTbl[i] = workTbl[i - 1];
        this.invSqStepFloat = 1.0f / sqStepFloat;
    }

    private void cyclicCircularGradientFillRaster(int[] pixels, int off, int adjust, int x, int y, int w, int h) {
        double constC = -this.radiusSq + this.centerX * this.centerX + this.centerY * this.centerY;
        float constX = this.a00 * (float)x + this.a01 * (float)y + this.a02;
        float constY = this.a10 * (float)x + this.a11 * (float)y + this.a12;
        float precalc2 = 2.0f * this.centerY;
        float precalc3 = -2.0f * this.centerX;
        int indexer = off;
        int pixInc = w + adjust;
        for (int j = 0; j < h; ++j) {
            float X = this.a01 * (float)j + constX;
            float Y = this.a11 * (float)j + constY;
            for (int i = 0; i < w; ++i) {
                double solutionY;
                double solutionX;
                if (X - this.focusX > -1.0E-6f && X - this.focusX < 1.0E-6f) {
                    solutionX = this.focusX;
                    solutionY = this.centerY;
                    solutionY += Y > this.focusY ? (double)this.trivial : (double)(-this.trivial);
                } else {
                    double slope = (Y - this.focusY) / (X - this.focusX);
                    double yintcpt = (double)Y - slope * (double)X;
                    double A = slope * slope + 1.0;
                    double B = (double)precalc3 + -2.0 * slope * ((double)this.centerY - yintcpt);
                    double C = constC + yintcpt * (yintcpt - (double)precalc2);
                    float det = (float)Math.sqrt(B * B - 4.0 * A * C);
                    solutionX = -B;
                    solutionX += X < this.focusX ? (double)(-det) : (double)det;
                    solutionY = slope * (solutionX /= 2.0 * A) + yintcpt;
                }
                float deltaXSq = (float)solutionX - this.focusX;
                deltaXSq *= deltaXSq;
                float deltaYSq = (float)solutionY - this.focusY;
                deltaYSq *= deltaYSq;
                float intersectToFocusSq = deltaXSq + deltaYSq;
                deltaXSq = X - this.focusX;
                deltaXSq *= deltaXSq;
                deltaYSq = Y - this.focusY;
                deltaYSq *= deltaYSq;
                float currentToFocusSq = deltaXSq + deltaYSq;
                float g = (float)Math.sqrt(currentToFocusSq / intersectToFocusSq);
                pixels[indexer + i] = this.indexIntoGradientsArrays(g);
                X += this.a00;
                Y += this.a10;
            }
            indexer += pixInc;
        }
    }

    private void antiAliasFillRaster(int[] pixels, int off, int adjust, int x, int y, int w, int h) {
        double currentToFocusSq;
        double intersectToFocusSq;
        double deltaYSq;
        double deltaXSq;
        double det;
        double C;
        double B;
        double A;
        double yintcpt;
        double slope;
        double solutionY;
        double solutionX;
        float dx;
        int i;
        double constC = -this.radiusSq + this.centerX * this.centerX + this.centerY * this.centerY;
        float precalc2 = 2.0f * this.centerY;
        float precalc3 = -2.0f * this.centerX;
        float constX = this.a00 * ((float)x - 0.5f) + this.a01 * ((float)y + 0.5f) + this.a02;
        float constY = this.a10 * ((float)x - 0.5f) + this.a11 * ((float)y + 0.5f) + this.a12;
        int indexer = off - 1;
        double[] prevGs = new double[w + 1];
        float X = constX - this.a01;
        float Y = constY - this.a11;
        for (i = 0; i <= w; ++i) {
            dx = X - this.focusX;
            if (dx > -1.0E-6f && dx < 1.0E-6f) {
                solutionX = this.focusX;
                solutionY = this.centerY;
                solutionY += Y > this.focusY ? (double)this.trivial : (double)(-this.trivial);
            } else {
                slope = (Y - this.focusY) / (X - this.focusX);
                yintcpt = (double)Y - slope * (double)X;
                A = slope * slope + 1.0;
                B = (double)precalc3 + -2.0 * slope * ((double)this.centerY - yintcpt);
                C = constC + yintcpt * (yintcpt - (double)precalc2);
                det = Math.sqrt(B * B - 4.0 * A * C);
                solutionX = -B;
                solutionX += X < this.focusX ? -det : det;
                solutionY = slope * (solutionX /= 2.0 * A) + yintcpt;
            }
            deltaXSq = solutionX - (double)this.focusX;
            deltaXSq *= deltaXSq;
            deltaYSq = solutionY - (double)this.focusY;
            deltaYSq *= deltaYSq;
            intersectToFocusSq = deltaXSq + deltaYSq;
            deltaXSq = X - this.focusX;
            deltaXSq *= deltaXSq;
            deltaYSq = Y - this.focusY;
            deltaYSq *= deltaYSq;
            currentToFocusSq = deltaXSq + deltaYSq;
            prevGs[i] = Math.sqrt(currentToFocusSq / intersectToFocusSq);
            X += this.a00;
            Y += this.a10;
        }
        for (int j = 0; j < h; ++j) {
            double g11;
            X = this.a01 * (float)j + constX;
            Y = this.a11 * (float)j + constY;
            double g10 = prevGs[0];
            dx = X - this.focusX;
            if (dx > -1.0E-6f && dx < 1.0E-6f) {
                solutionX = this.focusX;
                solutionY = this.centerY;
                solutionY += Y > this.focusY ? (double)this.trivial : (double)(-this.trivial);
            } else {
                slope = (Y - this.focusY) / (X - this.focusX);
                yintcpt = (double)Y - slope * (double)X;
                A = slope * slope + 1.0;
                B = (double)precalc3 + -2.0 * slope * ((double)this.centerY - yintcpt);
                C = constC + yintcpt * (yintcpt - (double)precalc2);
                det = Math.sqrt(B * B - 4.0 * A * C);
                solutionX = -B;
                solutionX += X < this.focusX ? -det : det;
                solutionY = slope * (solutionX /= 2.0 * A) + yintcpt;
            }
            deltaXSq = solutionX - (double)this.focusX;
            deltaXSq *= deltaXSq;
            deltaYSq = solutionY - (double)this.focusY;
            deltaYSq *= deltaYSq;
            intersectToFocusSq = deltaXSq + deltaYSq;
            deltaXSq = X - this.focusX;
            deltaXSq *= deltaXSq;
            deltaYSq = Y - this.focusY;
            deltaYSq *= deltaYSq;
            currentToFocusSq = deltaXSq + deltaYSq;
            prevGs[0] = g11 = Math.sqrt(currentToFocusSq / intersectToFocusSq);
            X += this.a00;
            Y += this.a10;
            for (i = 1; i <= w; ++i) {
                double g00 = g10;
                double g01 = g11;
                g10 = prevGs[i];
                dx = X - this.focusX;
                if (dx > -1.0E-6f && dx < 1.0E-6f) {
                    solutionX = this.focusX;
                    solutionY = this.centerY;
                    solutionY += Y > this.focusY ? (double)this.trivial : (double)(-this.trivial);
                } else {
                    slope = (Y - this.focusY) / (X - this.focusX);
                    yintcpt = (double)Y - slope * (double)X;
                    A = slope * slope + 1.0;
                    B = (double)precalc3 + -2.0 * slope * ((double)this.centerY - yintcpt);
                    C = constC + yintcpt * (yintcpt - (double)precalc2);
                    det = Math.sqrt(B * B - 4.0 * A * C);
                    solutionX = -B;
                    solutionX += X < this.focusX ? -det : det;
                    solutionY = slope * (solutionX /= 2.0 * A) + yintcpt;
                }
                deltaXSq = solutionX - (double)this.focusX;
                deltaXSq *= deltaXSq;
                deltaYSq = solutionY - (double)this.focusY;
                deltaYSq *= deltaYSq;
                intersectToFocusSq = deltaXSq + deltaYSq;
                deltaXSq = X - this.focusX;
                deltaXSq *= deltaXSq;
                deltaYSq = Y - this.focusY;
                deltaYSq *= deltaYSq;
                currentToFocusSq = deltaXSq + deltaYSq;
                prevGs[i] = g11 = Math.sqrt(currentToFocusSq / intersectToFocusSq);
                pixels[indexer + i] = this.indexGradientAntiAlias((float)((g00 + g01 + g10 + g11) / 4.0), (float)Math.max(Math.abs(g11 - g00), Math.abs(g10 - g01)));
                X += this.a00;
                Y += this.a10;
            }
            indexer += w + adjust;
        }
    }
}

