/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt;

import java.awt.Color;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.lang.ref.WeakReference;
import org.apache.batik.ext.awt.LinearGradientPaint;
import org.apache.batik.ext.awt.MultipleGradientPaint;
import org.apache.batik.ext.awt.image.GraphicsUtil;

abstract class MultipleGradientPaintContext
implements PaintContext {
    protected static final boolean DEBUG = false;
    protected ColorModel dataModel;
    protected ColorModel model;
    private static ColorModel lrgbmodel_NA = new DirectColorModel(ColorSpace.getInstance(1004), 24, 0xFF0000, 65280, 255, 0, false, 3);
    private static ColorModel srgbmodel_NA = new DirectColorModel(ColorSpace.getInstance(1000), 24, 0xFF0000, 65280, 255, 0, false, 3);
    private static ColorModel lrgbmodel_A = new DirectColorModel(ColorSpace.getInstance(1004), 32, 0xFF0000, 65280, 255, -16777216, false, 3);
    private static ColorModel srgbmodel_A = new DirectColorModel(ColorSpace.getInstance(1000), 32, 0xFF0000, 65280, 255, -16777216, false, 3);
    protected static ColorModel cachedModel;
    protected static WeakReference cached;
    protected WritableRaster saved;
    protected MultipleGradientPaint.CycleMethodEnum cycleMethod;
    protected MultipleGradientPaint.ColorSpaceEnum colorSpace;
    protected float a00;
    protected float a01;
    protected float a10;
    protected float a11;
    protected float a02;
    protected float a12;
    protected boolean isSimpleLookup = true;
    protected boolean hasDiscontinuity = false;
    protected int fastGradientArraySize;
    protected int[] gradient;
    protected int[][] gradients;
    protected int gradientAverage;
    protected int gradientUnderflow;
    protected int gradientOverflow;
    protected int gradientsLength;
    protected float[] normalizedIntervals;
    protected float[] fractions;
    private int transparencyTest;
    private static final int[] SRGBtoLinearRGB;
    private static final int[] LinearRGBtoSRGB;
    protected static final int GRADIENT_SIZE = 256;
    protected static final int GRADIENT_SIZE_INDEX = 255;
    private static final int MAX_GRADIENT_ARRAY_SIZE = 5000;

    protected MultipleGradientPaintContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform t, RenderingHints hints, float[] fractions, Color[] colors, MultipleGradientPaint.CycleMethodEnum cycleMethod, MultipleGradientPaint.ColorSpaceEnum colorSpace) throws NoninvertibleTransformException {
        boolean fixFirst = false;
        boolean fixLast = false;
        int len = fractions.length;
        if (fractions[0] != 0.0f) {
            fixFirst = true;
            ++len;
        }
        if (fractions[fractions.length - 1] != 1.0f) {
            fixLast = true;
            ++len;
        }
        for (int i = 0; i < fractions.length - 1; ++i) {
            if (fractions[i] != fractions[i + 1]) continue;
            --len;
        }
        this.fractions = new float[len];
        Color[] loColors = new Color[len - 1];
        Color[] hiColors = new Color[len - 1];
        this.normalizedIntervals = new float[len - 1];
        this.gradientUnderflow = colors[0].getRGB();
        this.gradientOverflow = colors[colors.length - 1].getRGB();
        int idx = 0;
        if (fixFirst) {
            this.fractions[0] = 0.0f;
            loColors[0] = colors[0];
            hiColors[0] = colors[0];
            this.normalizedIntervals[0] = fractions[0];
            ++idx;
        }
        for (int i = 0; i < fractions.length - 1; ++i) {
            if (fractions[i] == fractions[i + 1]) {
                if (colors[i].equals(colors[i + 1])) continue;
                this.hasDiscontinuity = true;
                continue;
            }
            this.fractions[idx] = fractions[i];
            loColors[idx] = colors[i];
            hiColors[idx] = colors[i + 1];
            this.normalizedIntervals[idx] = fractions[i + 1] - fractions[i];
            ++idx;
        }
        this.fractions[idx] = fractions[fractions.length - 1];
        if (fixLast) {
            loColors[idx] = hiColors[idx] = colors[colors.length - 1];
            this.normalizedIntervals[idx] = 1.0f - fractions[fractions.length - 1];
            this.fractions[++idx] = 1.0f;
        }
        AffineTransform tInv = t.createInverse();
        double[] m = new double[6];
        tInv.getMatrix(m);
        this.a00 = (float)m[0];
        this.a10 = (float)m[1];
        this.a01 = (float)m[2];
        this.a11 = (float)m[3];
        this.a02 = (float)m[4];
        this.a12 = (float)m[5];
        this.cycleMethod = cycleMethod;
        this.colorSpace = colorSpace;
        if (cm.getColorSpace() == lrgbmodel_A.getColorSpace()) {
            this.dataModel = lrgbmodel_A;
        } else if (cm.getColorSpace() == srgbmodel_A.getColorSpace()) {
            this.dataModel = srgbmodel_A;
        } else {
            throw new IllegalArgumentException("Unsupported ColorSpace for interpolation");
        }
        this.calculateGradientFractions(loColors, hiColors);
        this.model = GraphicsUtil.coerceColorModel(this.dataModel, cm.isAlphaPremultiplied());
    }

    protected final void calculateGradientFractions(Color[] loColors, Color[] hiColors) {
        if (this.colorSpace == LinearGradientPaint.LINEAR_RGB) {
            int[] workTbl = SRGBtoLinearRGB;
            for (int i = 0; i < loColors.length; ++i) {
                loColors[i] = MultipleGradientPaintContext.interpolateColor(workTbl, loColors[i]);
                hiColors[i] = MultipleGradientPaintContext.interpolateColor(workTbl, hiColors[i]);
            }
        }
        this.transparencyTest = -16777216;
        if (this.cycleMethod == MultipleGradientPaint.NO_CYCLE) {
            this.transparencyTest &= this.gradientUnderflow;
            this.transparencyTest &= this.gradientOverflow;
        }
        this.gradients = new int[this.fractions.length - 1][];
        this.gradientsLength = this.gradients.length;
        int n = this.normalizedIntervals.length;
        float Imin = 1.0f;
        float[] workTbl = this.normalizedIntervals;
        for (int i = 0; i < n; ++i) {
            Imin = Imin > workTbl[i] ? workTbl[i] : Imin;
        }
        int estimatedSize = 0;
        if (Imin == 0.0f) {
            estimatedSize = Integer.MAX_VALUE;
            this.hasDiscontinuity = true;
        } else {
            for (float aWorkTbl : workTbl) {
                estimatedSize = (int)((float)estimatedSize + aWorkTbl / Imin * 256.0f);
            }
        }
        if (estimatedSize > 5000) {
            this.calculateMultipleArrayGradient(loColors, hiColors);
            if (this.cycleMethod == MultipleGradientPaint.REPEAT && this.gradients[0][0] != this.gradients[this.gradients.length - 1][255]) {
                this.hasDiscontinuity = true;
            }
        } else {
            this.calculateSingleArrayGradient(loColors, hiColors, Imin);
            if (this.cycleMethod == MultipleGradientPaint.REPEAT && this.gradient[0] != this.gradient[this.fastGradientArraySize]) {
                this.hasDiscontinuity = true;
            }
        }
        if (this.transparencyTest >>> 24 == 255) {
            if (this.dataModel.getColorSpace() == lrgbmodel_NA.getColorSpace()) {
                this.dataModel = lrgbmodel_NA;
            } else if (this.dataModel.getColorSpace() == srgbmodel_NA.getColorSpace()) {
                this.dataModel = srgbmodel_NA;
            }
            this.model = this.dataModel;
        }
    }

    private static Color interpolateColor(int[] workTbl, Color inColor) {
        int oldColor = inColor.getRGB();
        int newColorValue = (workTbl[oldColor >> 24 & 0xFF] & 0xFF) << 24 | (workTbl[oldColor >> 16 & 0xFF] & 0xFF) << 16 | (workTbl[oldColor >> 8 & 0xFF] & 0xFF) << 8 | workTbl[oldColor & 0xFF] & 0xFF;
        return new Color(newColorValue, true);
    }

    private void calculateSingleArrayGradient(Color[] loColors, Color[] hiColors, float Imin) {
        this.isSimpleLookup = true;
        int gradientsTot = 1;
        int aveA = 32768;
        int aveR = 32768;
        int aveG = 32768;
        int aveB = 32768;
        for (int i = 0; i < this.gradients.length; ++i) {
            int nGradients = (int)(this.normalizedIntervals[i] / Imin * 255.0f);
            gradientsTot += nGradients;
            this.gradients[i] = new int[nGradients];
            int rgb1 = loColors[i].getRGB();
            int rgb2 = hiColors[i].getRGB();
            this.interpolate(rgb1, rgb2, this.gradients[i]);
            int argb = this.gradients[i][128];
            float norm = this.normalizedIntervals[i];
            aveA += (int)((float)(argb >> 8 & 0xFF0000) * norm);
            aveR += (int)((float)(argb & 0xFF0000) * norm);
            aveG += (int)((float)(argb << 8 & 0xFF0000) * norm);
            aveB += (int)((float)(argb << 16 & 0xFF0000) * norm);
            this.transparencyTest &= rgb1 & rgb2;
        }
        this.gradientAverage = (aveA & 0xFF0000) << 8 | aveR & 0xFF0000 | (aveG & 0xFF0000) >> 8 | (aveB & 0xFF0000) >> 16;
        this.gradient = new int[gradientsTot];
        int curOffset = 0;
        for (int[] gradient1 : this.gradients) {
            System.arraycopy(gradient1, 0, this.gradient, curOffset, gradient1.length);
            curOffset += gradient1.length;
        }
        this.gradient[this.gradient.length - 1] = hiColors[hiColors.length - 1].getRGB();
        if (this.colorSpace == LinearGradientPaint.LINEAR_RGB) {
            if (this.dataModel.getColorSpace() == ColorSpace.getInstance(1000)) {
                for (int i = 0; i < this.gradient.length; ++i) {
                    this.gradient[i] = MultipleGradientPaintContext.convertEntireColorLinearRGBtoSRGB(this.gradient[i]);
                }
                this.gradientAverage = MultipleGradientPaintContext.convertEntireColorLinearRGBtoSRGB(this.gradientAverage);
            }
        } else if (this.dataModel.getColorSpace() == ColorSpace.getInstance(1004)) {
            for (int i = 0; i < this.gradient.length; ++i) {
                this.gradient[i] = MultipleGradientPaintContext.convertEntireColorSRGBtoLinearRGB(this.gradient[i]);
            }
            this.gradientAverage = MultipleGradientPaintContext.convertEntireColorSRGBtoLinearRGB(this.gradientAverage);
        }
        this.fastGradientArraySize = this.gradient.length - 1;
    }

    private void calculateMultipleArrayGradient(Color[] loColors, Color[] hiColors) {
        int i;
        int j;
        this.isSimpleLookup = false;
        int aveA = 32768;
        int aveR = 32768;
        int aveG = 32768;
        int aveB = 32768;
        for (int i2 = 0; i2 < this.gradients.length; ++i2) {
            if (this.normalizedIntervals[i2] == 0.0f) continue;
            this.gradients[i2] = new int[256];
            int rgb1 = loColors[i2].getRGB();
            int rgb2 = hiColors[i2].getRGB();
            this.interpolate(rgb1, rgb2, this.gradients[i2]);
            int argb = this.gradients[i2][128];
            float norm = this.normalizedIntervals[i2];
            aveA += (int)((float)(argb >> 8 & 0xFF0000) * norm);
            aveR += (int)((float)(argb & 0xFF0000) * norm);
            aveG += (int)((float)(argb << 8 & 0xFF0000) * norm);
            aveB += (int)((float)(argb << 16 & 0xFF0000) * norm);
            this.transparencyTest &= rgb1;
            this.transparencyTest &= rgb2;
        }
        this.gradientAverage = (aveA & 0xFF0000) << 8 | aveR & 0xFF0000 | (aveG & 0xFF0000) >> 8 | (aveB & 0xFF0000) >> 16;
        if (this.colorSpace == LinearGradientPaint.LINEAR_RGB) {
            if (this.dataModel.getColorSpace() == ColorSpace.getInstance(1000)) {
                for (j = 0; j < this.gradients.length; ++j) {
                    for (i = 0; i < this.gradients[j].length; ++i) {
                        this.gradients[j][i] = MultipleGradientPaintContext.convertEntireColorLinearRGBtoSRGB(this.gradients[j][i]);
                    }
                }
                this.gradientAverage = MultipleGradientPaintContext.convertEntireColorLinearRGBtoSRGB(this.gradientAverage);
            }
        } else if (this.dataModel.getColorSpace() == ColorSpace.getInstance(1004)) {
            for (j = 0; j < this.gradients.length; ++j) {
                for (i = 0; i < this.gradients[j].length; ++i) {
                    this.gradients[j][i] = MultipleGradientPaintContext.convertEntireColorSRGBtoLinearRGB(this.gradients[j][i]);
                }
            }
            this.gradientAverage = MultipleGradientPaintContext.convertEntireColorSRGBtoLinearRGB(this.gradientAverage);
        }
    }

    private void interpolate(int rgb1, int rgb2, int[] output) {
        int nSteps = output.length;
        float stepSize = 1.0f / (float)nSteps;
        int a1 = rgb1 >> 24 & 0xFF;
        int r1 = rgb1 >> 16 & 0xFF;
        int g1 = rgb1 >> 8 & 0xFF;
        int b1 = rgb1 & 0xFF;
        int da = (rgb2 >> 24 & 0xFF) - a1;
        int dr = (rgb2 >> 16 & 0xFF) - r1;
        int dg = (rgb2 >> 8 & 0xFF) - g1;
        int db = (rgb2 & 0xFF) - b1;
        float tempA = 2.0f * (float)da * stepSize;
        float tempR = 2.0f * (float)dr * stepSize;
        float tempG = 2.0f * (float)dg * stepSize;
        float tempB = 2.0f * (float)db * stepSize;
        output[0] = rgb1;
        output[--nSteps] = rgb2;
        for (int i = 1; i < nSteps; ++i) {
            float fI = i;
            output[i] = (a1 + ((int)(fI * tempA) + 1 >> 1) & 0xFF) << 24 | (r1 + ((int)(fI * tempR) + 1 >> 1) & 0xFF) << 16 | (g1 + ((int)(fI * tempG) + 1 >> 1) & 0xFF) << 8 | b1 + ((int)(fI * tempB) + 1 >> 1) & 0xFF;
        }
    }

    private static int convertEntireColorLinearRGBtoSRGB(int rgb) {
        int a1 = rgb >> 24 & 0xFF;
        int r1 = rgb >> 16 & 0xFF;
        int g1 = rgb >> 8 & 0xFF;
        int b1 = rgb & 0xFF;
        int[] workTbl = LinearRGBtoSRGB;
        r1 = workTbl[r1];
        g1 = workTbl[g1];
        b1 = workTbl[b1];
        return a1 << 24 | r1 << 16 | g1 << 8 | b1;
    }

    private static int convertEntireColorSRGBtoLinearRGB(int rgb) {
        int a1 = rgb >> 24 & 0xFF;
        int r1 = rgb >> 16 & 0xFF;
        int g1 = rgb >> 8 & 0xFF;
        int b1 = rgb & 0xFF;
        int[] workTbl = SRGBtoLinearRGB;
        r1 = workTbl[r1];
        g1 = workTbl[g1];
        b1 = workTbl[b1];
        return a1 << 24 | r1 << 16 | g1 << 8 | b1;
    }

    protected final int indexIntoGradientsArrays(float position) {
        if (this.cycleMethod == MultipleGradientPaint.NO_CYCLE) {
            if (position >= 1.0f) {
                return this.gradientOverflow;
            }
            if (position <= 0.0f) {
                return this.gradientUnderflow;
            }
        } else {
            if (this.cycleMethod == MultipleGradientPaint.REPEAT) {
                if ((position -= (float)((int)position)) < 0.0f) {
                    position += 1.0f;
                }
                int w = 0;
                int c1 = 0;
                int c2 = 0;
                if (this.isSimpleLookup) {
                    int idx1 = (int)(position *= (float)this.gradient.length);
                    if (idx1 + 1 < this.gradient.length) {
                        return this.gradient[idx1];
                    }
                    w = (int)((position - (float)idx1) * 65536.0f);
                    c1 = this.gradient[idx1];
                    c2 = this.gradient[0];
                } else {
                    for (int i = 0; i < this.gradientsLength; ++i) {
                        if (!(position < this.fractions[i + 1])) continue;
                        float delta = position - this.fractions[i];
                        int index = (int)(delta = delta / this.normalizedIntervals[i] * 256.0f);
                        if (index + 1 < this.gradients[i].length || i + 1 < this.gradientsLength) {
                            return this.gradients[i][index];
                        }
                        w = (int)((delta - (float)index) * 65536.0f);
                        c1 = this.gradients[i][index];
                        c2 = this.gradients[0][0];
                        break;
                    }
                }
                return ((c1 >> 8 & 0xFF0000) + ((c2 >>> 24) - (c1 >>> 24)) * w & 0xFF0000) << 8 | (c1 & 0xFF0000) + ((c2 >> 16 & 0xFF) - (c1 >> 16 & 0xFF)) * w & 0xFF0000 | ((c1 << 8 & 0xFF0000) + ((c2 >> 8 & 0xFF) - (c1 >> 8 & 0xFF)) * w & 0xFF0000) >> 8 | ((c1 << 16 & 0xFF0000) + ((c2 & 0xFF) - (c1 & 0xFF)) * w & 0xFF0000) >> 16;
            }
            if (position < 0.0f) {
                position = -position;
            }
            int part = (int)position;
            position -= (float)part;
            if ((part & 1) == 1) {
                position = 1.0f - position;
            }
        }
        if (this.isSimpleLookup) {
            return this.gradient[(int)(position * (float)this.fastGradientArraySize)];
        }
        for (int i = 0; i < this.gradientsLength; ++i) {
            if (!(position < this.fractions[i + 1])) continue;
            float delta = position - this.fractions[i];
            int index = (int)(delta / this.normalizedIntervals[i] * 255.0f);
            return this.gradients[i][index];
        }
        return this.gradientOverflow;
    }

    protected final int indexGradientAntiAlias(float position, float sz) {
        if (this.cycleMethod == MultipleGradientPaint.NO_CYCLE) {
            int interior;
            float frac;
            float p1 = position - sz / 2.0f;
            float p2 = position + sz / 2.0f;
            if (p1 >= 1.0f) {
                return this.gradientOverflow;
            }
            if (p2 <= 0.0f) {
                return this.gradientUnderflow;
            }
            float top_weight = 0.0f;
            float bottom_weight = 0.0f;
            if (p2 >= 1.0f) {
                top_weight = (p2 - 1.0f) / sz;
                if (p1 <= 0.0f) {
                    bottom_weight = -p1 / sz;
                    frac = 1.0f;
                    interior = this.gradientAverage;
                } else {
                    frac = 1.0f - p1;
                    interior = this.getAntiAlias(p1, true, 1.0f, false, 1.0f - p1, 1.0f);
                }
            } else if (p1 <= 0.0f) {
                bottom_weight = -p1 / sz;
                frac = p2;
                interior = this.getAntiAlias(0.0f, true, p2, false, p2, 1.0f);
            } else {
                return this.getAntiAlias(p1, true, p2, false, sz, 1.0f);
            }
            int norm = (int)(65536.0f * frac / sz);
            int pA = (interior >>> 20 & 0xFF0) * norm >> 16;
            int pR = (interior >> 12 & 0xFF0) * norm >> 16;
            int pG = (interior >> 4 & 0xFF0) * norm >> 16;
            int pB = (interior << 4 & 0xFF0) * norm >> 16;
            if (bottom_weight != 0.0f) {
                int bPix = this.gradientUnderflow;
                norm = (int)(65536.0f * bottom_weight);
                pA += (bPix >>> 20 & 0xFF0) * norm >> 16;
                pR += (bPix >> 12 & 0xFF0) * norm >> 16;
                pG += (bPix >> 4 & 0xFF0) * norm >> 16;
                pB += (bPix << 4 & 0xFF0) * norm >> 16;
            }
            if (top_weight != 0.0f) {
                int tPix = this.gradientOverflow;
                norm = (int)(65536.0f * top_weight);
                pA += (tPix >>> 20 & 0xFF0) * norm >> 16;
                pR += (tPix >> 12 & 0xFF0) * norm >> 16;
                pG += (tPix >> 4 & 0xFF0) * norm >> 16;
                pB += (tPix << 4 & 0xFF0) * norm >> 16;
            }
            return (pA & 0xFF0) << 20 | (pR & 0xFF0) << 12 | (pG & 0xFF0) << 4 | (pB & 0xFF0) >> 4;
        }
        int intSz = (int)sz;
        float weight = 1.0f;
        if (intSz != 0 && (double)(weight = (sz -= (float)intSz) / ((float)intSz + sz)) < 0.1) {
            return this.gradientAverage;
        }
        if ((double)sz > 0.99) {
            return this.gradientAverage;
        }
        float p1 = position - sz / 2.0f;
        float p2 = position + sz / 2.0f;
        boolean p1_up = true;
        boolean p2_up = false;
        if (this.cycleMethod == MultipleGradientPaint.REPEAT) {
            p1 -= (float)((int)p1);
            p2 -= (float)((int)p2);
            if (p1 < 0.0f) {
                p1 += 1.0f;
            }
            if (p2 < 0.0f) {
                p2 += 1.0f;
            }
        } else {
            if (p2 < 0.0f) {
                p1 = -p1;
                p1_up = !p1_up;
                p2 = -p2;
                p2_up = !p2_up;
            } else if (p1 < 0.0f) {
                p1 = -p1;
                p1_up = !p1_up;
            }
            int part1 = (int)p1;
            p1 -= (float)part1;
            int part2 = (int)p2;
            p2 -= (float)part2;
            if ((part1 & 1) == 1) {
                p1 = 1.0f - p1;
                boolean bl = p1_up = !p1_up;
            }
            if ((part2 & 1) == 1) {
                p2 = 1.0f - p2;
                boolean bl = p2_up = !p2_up;
            }
            if (p1 > p2 && !p1_up && p2_up) {
                float t = p1;
                p1 = p2;
                p2 = t;
                p1_up = true;
                p2_up = false;
            }
        }
        return this.getAntiAlias(p1, p1_up, p2, p2_up, sz, weight);
    }

    private final int getAntiAlias(float p1, boolean p1_up, float p2, boolean p2_up, float sz, float weight) {
        int idx2;
        int idx1;
        int ach = 0;
        int rch = 0;
        int gch = 0;
        int bch = 0;
        if (this.isSimpleLookup) {
            int pix;
            int i;
            idx1 = (int)(p1 *= (float)this.fastGradientArraySize);
            idx2 = (int)(p2 *= (float)this.fastGradientArraySize);
            if (p1_up && !p2_up && idx1 <= idx2) {
                if (idx1 == idx2) {
                    return this.gradient[idx1];
                }
                for (i = idx1 + 1; i < idx2; ++i) {
                    pix = this.gradient[i];
                    ach += pix >>> 20 & 0xFF0;
                    rch += pix >>> 12 & 0xFF0;
                    gch += pix >>> 4 & 0xFF0;
                    bch += pix << 4 & 0xFF0;
                }
            } else {
                int iEnd;
                int iStart;
                if (p1_up) {
                    iStart = idx1 + 1;
                    iEnd = this.fastGradientArraySize;
                } else {
                    iStart = 0;
                    iEnd = idx1;
                }
                for (i = iStart; i < iEnd; ++i) {
                    pix = this.gradient[i];
                    ach += pix >>> 20 & 0xFF0;
                    rch += pix >>> 12 & 0xFF0;
                    gch += pix >>> 4 & 0xFF0;
                    bch += pix << 4 & 0xFF0;
                }
                if (p2_up) {
                    iStart = idx2 + 1;
                    iEnd = this.fastGradientArraySize;
                } else {
                    iStart = 0;
                    iEnd = idx2;
                }
                for (i = iStart; i < iEnd; ++i) {
                    pix = this.gradient[i];
                    ach += pix >>> 20 & 0xFF0;
                    rch += pix >>> 12 & 0xFF0;
                    gch += pix >>> 4 & 0xFF0;
                    bch += pix << 4 & 0xFF0;
                }
            }
            int isz = (int)(65536.0f / (sz * (float)this.fastGradientArraySize));
            ach = ach * isz >> 16;
            rch = rch * isz >> 16;
            gch = gch * isz >> 16;
            bch = bch * isz >> 16;
            int norm = p1_up ? (int)((1.0f - (p1 - (float)idx1)) * (float)isz) : (int)((p1 - (float)idx1) * (float)isz);
            pix = this.gradient[idx1];
            ach += (pix >>> 20 & 0xFF0) * norm >> 16;
            rch += (pix >>> 12 & 0xFF0) * norm >> 16;
            gch += (pix >>> 4 & 0xFF0) * norm >> 16;
            bch += (pix << 4 & 0xFF0) * norm >> 16;
            norm = p2_up ? (int)((1.0f - (p2 - (float)idx2)) * (float)isz) : (int)((p2 - (float)idx2) * (float)isz);
            pix = this.gradient[idx2];
            ach += (pix >>> 20 & 0xFF0) * norm >> 16;
            rch += (pix >>> 12 & 0xFF0) * norm >> 16;
            gch += (pix >>> 4 & 0xFF0) * norm >> 16;
            bch += (pix << 4 & 0xFF0) * norm >> 16;
            ach = ach + 8 >> 4;
            rch = rch + 8 >> 4;
            gch = gch + 8 >> 4;
            bch = bch + 8 >> 4;
        } else {
            int pix;
            idx1 = 0;
            idx2 = 0;
            int i1 = -1;
            int i2 = -1;
            float f1 = 0.0f;
            float f2 = 0.0f;
            for (int i = 0; i < this.gradientsLength; ++i) {
                if (p1 < this.fractions[i + 1] && i1 == -1) {
                    i1 = i;
                    f1 = p1 - this.fractions[i];
                    f1 = f1 / this.normalizedIntervals[i] * 255.0f;
                    idx1 = (int)f1;
                    if (i2 != -1) break;
                }
                if (!(p2 < this.fractions[i + 1]) || i2 != -1) continue;
                i2 = i;
                f2 = p2 - this.fractions[i];
                f2 = f2 / this.normalizedIntervals[i] * 255.0f;
                idx2 = (int)f2;
                if (i1 != -1) break;
            }
            if (i1 == -1) {
                i1 = this.gradients.length - 1;
                idx1 = 255;
                f1 = 255;
            }
            if (i2 == -1) {
                i2 = this.gradients.length - 1;
                idx2 = 255;
                f2 = 255;
            }
            if (i1 == i2 && idx1 <= idx2 && p1_up && !p2_up) {
                return this.gradients[i1][idx1 + idx2 + 1 >> 1];
            }
            int base = (int)(65536.0f / sz);
            if (i1 < i2 && p1_up && !p2_up) {
                int norm = (int)((float)base * this.normalizedIntervals[i1] * (255.0f - f1) / 255.0f);
                pix = this.gradients[i1][idx1 + 256 >> 1];
                ach += (pix >>> 20 & 0xFF0) * norm >> 16;
                rch += (pix >>> 12 & 0xFF0) * norm >> 16;
                gch += (pix >>> 4 & 0xFF0) * norm >> 16;
                bch += (pix << 4 & 0xFF0) * norm >> 16;
                for (int i = i1 + 1; i < i2; ++i) {
                    norm = (int)((float)base * this.normalizedIntervals[i]);
                    pix = this.gradients[i][128];
                    ach += (pix >>> 20 & 0xFF0) * norm >> 16;
                    rch += (pix >>> 12 & 0xFF0) * norm >> 16;
                    gch += (pix >>> 4 & 0xFF0) * norm >> 16;
                    bch += (pix << 4 & 0xFF0) * norm >> 16;
                }
                norm = (int)((float)base * this.normalizedIntervals[i2] * f2 / 255.0f);
                pix = this.gradients[i2][idx2 + 1 >> 1];
                ach += (pix >>> 20 & 0xFF0) * norm >> 16;
                rch += (pix >>> 12 & 0xFF0) * norm >> 16;
                gch += (pix >>> 4 & 0xFF0) * norm >> 16;
                bch += (pix << 4 & 0xFF0) * norm >> 16;
            } else {
                int i;
                int iEnd;
                int iStart;
                int norm;
                if (p1_up) {
                    norm = (int)((float)base * this.normalizedIntervals[i1] * (255.0f - f1) / 255.0f);
                    pix = this.gradients[i1][idx1 + 256 >> 1];
                } else {
                    norm = (int)((float)base * this.normalizedIntervals[i1] * f1 / 255.0f);
                    pix = this.gradients[i1][idx1 + 1 >> 1];
                }
                ach += (pix >>> 20 & 0xFF0) * norm >> 16;
                rch += (pix >>> 12 & 0xFF0) * norm >> 16;
                gch += (pix >>> 4 & 0xFF0) * norm >> 16;
                bch += (pix << 4 & 0xFF0) * norm >> 16;
                if (p2_up) {
                    norm = (int)((float)base * this.normalizedIntervals[i2] * (255.0f - f2) / 255.0f);
                    pix = this.gradients[i2][idx2 + 256 >> 1];
                } else {
                    norm = (int)((float)base * this.normalizedIntervals[i2] * f2 / 255.0f);
                    pix = this.gradients[i2][idx2 + 1 >> 1];
                }
                ach += (pix >>> 20 & 0xFF0) * norm >> 16;
                rch += (pix >>> 12 & 0xFF0) * norm >> 16;
                gch += (pix >>> 4 & 0xFF0) * norm >> 16;
                bch += (pix << 4 & 0xFF0) * norm >> 16;
                if (p1_up) {
                    iStart = i1 + 1;
                    iEnd = this.gradientsLength;
                } else {
                    iStart = 0;
                    iEnd = i1;
                }
                for (i = iStart; i < iEnd; ++i) {
                    norm = (int)((float)base * this.normalizedIntervals[i]);
                    pix = this.gradients[i][128];
                    ach += (pix >>> 20 & 0xFF0) * norm >> 16;
                    rch += (pix >>> 12 & 0xFF0) * norm >> 16;
                    gch += (pix >>> 4 & 0xFF0) * norm >> 16;
                    bch += (pix << 4 & 0xFF0) * norm >> 16;
                }
                if (p2_up) {
                    iStart = i2 + 1;
                    iEnd = this.gradientsLength;
                } else {
                    iStart = 0;
                    iEnd = i2;
                }
                for (i = iStart; i < iEnd; ++i) {
                    norm = (int)((float)base * this.normalizedIntervals[i]);
                    pix = this.gradients[i][128];
                    ach += (pix >>> 20 & 0xFF0) * norm >> 16;
                    rch += (pix >>> 12 & 0xFF0) * norm >> 16;
                    gch += (pix >>> 4 & 0xFF0) * norm >> 16;
                    bch += (pix << 4 & 0xFF0) * norm >> 16;
                }
            }
            ach = ach + 8 >> 4;
            rch = rch + 8 >> 4;
            gch = gch + 8 >> 4;
            bch = bch + 8 >> 4;
        }
        if (weight != 1.0f) {
            int aveW = (int)(65536.0f * (1.0f - weight));
            int aveA = (this.gradientAverage >>> 24 & 0xFF) * aveW;
            int aveR = (this.gradientAverage >> 16 & 0xFF) * aveW;
            int aveG = (this.gradientAverage >> 8 & 0xFF) * aveW;
            int aveB = (this.gradientAverage & 0xFF) * aveW;
            int iw = (int)(weight * 65536.0f);
            ach = ach * iw + aveA >> 16;
            rch = rch * iw + aveR >> 16;
            gch = gch * iw + aveG >> 16;
            bch = bch * iw + aveB >> 16;
        }
        return ach << 24 | rch << 16 | gch << 8 | bch;
    }

    private static int convertSRGBtoLinearRGB(int color) {
        float input = (float)color / 255.0f;
        float output = input <= 0.04045f ? input / 12.92f : (float)Math.pow(((double)input + 0.055) / 1.055, 2.4);
        int o = Math.round(output * 255.0f);
        return o;
    }

    private static int convertLinearRGBtoSRGB(int color) {
        float input = (float)color / 255.0f;
        float output = input <= 0.0031308f ? input * 12.92f : 1.055f * (float)Math.pow(input, 0.4166666666666667) - 0.055f;
        int o = Math.round(output * 255.0f);
        return o;
    }

    @Override
    public final Raster getRaster(int x, int y, int w, int h) {
        if (w == 0 || h == 0) {
            return null;
        }
        WritableRaster raster = this.saved;
        if (raster == null || raster.getWidth() < w || raster.getHeight() < h) {
            this.saved = raster = MultipleGradientPaintContext.getCachedRaster(this.dataModel, w, h);
            raster = raster.createWritableChild(raster.getMinX(), raster.getMinY(), w, h, 0, 0, null);
        }
        DataBufferInt rasterDB = (DataBufferInt)raster.getDataBuffer();
        int[] pixels = rasterDB.getBankData()[0];
        int off = rasterDB.getOffset();
        int scanlineStride = ((SinglePixelPackedSampleModel)raster.getSampleModel()).getScanlineStride();
        int adjust = scanlineStride - w;
        this.fillRaster(pixels, off, adjust, x, y, w, h);
        GraphicsUtil.coerceData(raster, this.dataModel, this.model.isAlphaPremultiplied());
        return raster;
    }

    protected abstract void fillRaster(int[] var1, int var2, int var3, int var4, int var5, int var6, int var7);

    protected static final synchronized WritableRaster getCachedRaster(ColorModel cm, int w, int h) {
        WritableRaster ras;
        if (cm == cachedModel && cached != null && (ras = (WritableRaster)cached.get()) != null && ras.getWidth() >= w && ras.getHeight() >= h) {
            cached = null;
            return ras;
        }
        if (w < 32) {
            w = 32;
        }
        if (h < 32) {
            h = 32;
        }
        return cm.createCompatibleWritableRaster(w, h);
    }

    protected static final synchronized void putCachedRaster(ColorModel cm, WritableRaster ras) {
        WritableRaster cras;
        if (cached != null && (cras = (WritableRaster)cached.get()) != null) {
            int cw = cras.getWidth();
            int ch = cras.getHeight();
            int iw = ras.getWidth();
            int ih = ras.getHeight();
            if (cw >= iw && ch >= ih) {
                return;
            }
            if (cw * ch >= iw * ih) {
                return;
            }
        }
        cachedModel = cm;
        cached = new WeakReference<WritableRaster>(ras);
    }

    @Override
    public final void dispose() {
        if (this.saved != null) {
            MultipleGradientPaintContext.putCachedRaster(this.model, this.saved);
            this.saved = null;
        }
    }

    @Override
    public final ColorModel getColorModel() {
        return this.model;
    }

    static {
        SRGBtoLinearRGB = new int[256];
        LinearRGBtoSRGB = new int[256];
        for (int k = 0; k < 256; ++k) {
            MultipleGradientPaintContext.SRGBtoLinearRGB[k] = MultipleGradientPaintContext.convertSRGBtoLinearRGB(k);
            MultipleGradientPaintContext.LinearRGBtoSRGB[k] = MultipleGradientPaintContext.convertLinearRGBtoSRGB(k);
        }
    }
}

