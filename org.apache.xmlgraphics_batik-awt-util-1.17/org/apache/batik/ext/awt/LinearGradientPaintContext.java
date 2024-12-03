/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import org.apache.batik.ext.awt.MultipleGradientPaint;
import org.apache.batik.ext.awt.MultipleGradientPaintContext;

final class LinearGradientPaintContext
extends MultipleGradientPaintContext {
    private float dgdX;
    private float dgdY;
    private float gc;
    private float pixSz;
    private static final int DEFAULT_IMPL = 1;
    private static final int ANTI_ALIAS_IMPL = 3;
    private int fillMethod;

    public LinearGradientPaintContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform t, RenderingHints hints, Point2D dStart, Point2D dEnd, float[] fractions, Color[] colors, MultipleGradientPaint.CycleMethodEnum cycleMethod, MultipleGradientPaint.ColorSpaceEnum colorSpace) throws NoninvertibleTransformException {
        super(cm, deviceBounds, userBounds, t, hints, fractions, colors, cycleMethod, colorSpace);
        Point2D.Float start = new Point2D.Float((float)dStart.getX(), (float)dStart.getY());
        Point2D.Float end = new Point2D.Float((float)dEnd.getX(), (float)dEnd.getY());
        float dx = end.x - start.x;
        float dy = end.y - start.y;
        float dSq = dx * dx + dy * dy;
        float constX = dx / dSq;
        float constY = dy / dSq;
        this.dgdX = this.a00 * constX + this.a10 * constY;
        this.dgdY = this.a01 * constX + this.a11 * constY;
        float dgdXAbs = Math.abs(this.dgdX);
        float dgdYAbs = Math.abs(this.dgdY);
        this.pixSz = dgdXAbs > dgdYAbs ? dgdXAbs : dgdYAbs;
        this.gc = (this.a02 - start.x) * constX + (this.a12 - start.y) * constY;
        Object colorRend = hints.get(RenderingHints.KEY_COLOR_RENDERING);
        Object rend = hints.get(RenderingHints.KEY_RENDERING);
        this.fillMethod = 1;
        if (cycleMethod == MultipleGradientPaint.REPEAT || this.hasDiscontinuity) {
            if (rend == RenderingHints.VALUE_RENDER_QUALITY) {
                this.fillMethod = 3;
            }
            if (colorRend == RenderingHints.VALUE_COLOR_RENDER_SPEED) {
                this.fillMethod = 1;
            } else if (colorRend == RenderingHints.VALUE_COLOR_RENDER_QUALITY) {
                this.fillMethod = 3;
            }
        }
    }

    protected void fillHardNoCycle(int[] pixels, int off, int adjust, int x, int y, int w, int h) {
        float initConst = this.dgdX * (float)x + this.gc;
        for (int i = 0; i < h; ++i) {
            float g = initConst + this.dgdY * (float)(y + i);
            int rowLimit = off + w;
            if (this.dgdX == 0.0f) {
                int val;
                if (g <= 0.0f) {
                    val = this.gradientUnderflow;
                } else if (g >= 1.0f) {
                    val = this.gradientOverflow;
                } else {
                    int gradIdx;
                    for (gradIdx = 0; gradIdx < this.gradientsLength - 1 && !(g < this.fractions[gradIdx + 1]); ++gradIdx) {
                    }
                    float delta = g - this.fractions[gradIdx];
                    float idx = delta * 255.0f / this.normalizedIntervals[gradIdx] + 0.5f;
                    val = this.gradients[gradIdx][(int)idx];
                }
                while (off < rowLimit) {
                    pixels[off++] = val;
                }
            } else {
                int step;
                int idx;
                int subGradLimit;
                int steps;
                double stepsD;
                int[] grad;
                float delta;
                int gradIdx;
                int postVal;
                int preVal;
                float preGradStepsF;
                float gradStepsF;
                if (this.dgdX >= 0.0f) {
                    gradStepsF = (1.0f - g) / this.dgdX;
                    preGradStepsF = (float)Math.ceil((0.0f - g) / this.dgdX);
                    preVal = this.gradientUnderflow;
                    postVal = this.gradientOverflow;
                } else {
                    gradStepsF = (0.0f - g) / this.dgdX;
                    preGradStepsF = (float)Math.ceil((1.0f - g) / this.dgdX);
                    preVal = this.gradientOverflow;
                    postVal = this.gradientUnderflow;
                }
                int gradSteps = gradStepsF > (float)w ? w : (int)gradStepsF;
                int preGradSteps = preGradStepsF > (float)w ? w : (int)preGradStepsF;
                int gradLimit = off + gradSteps;
                if (preGradSteps > 0) {
                    int preGradLimit = off + preGradSteps;
                    while (off < preGradLimit) {
                        pixels[off++] = preVal;
                    }
                    g += this.dgdX * (float)preGradSteps;
                }
                if (this.dgdX > 0.0f) {
                    for (gradIdx = 0; gradIdx < this.gradientsLength - 1 && !(g < this.fractions[gradIdx + 1]); ++gradIdx) {
                    }
                    while (off < gradLimit) {
                        delta = g - this.fractions[gradIdx];
                        grad = this.gradients[gradIdx];
                        stepsD = Math.ceil((this.fractions[gradIdx + 1] - g) / this.dgdX);
                        steps = stepsD > (double)w ? w : (int)stepsD;
                        subGradLimit = off + steps;
                        if (subGradLimit > gradLimit) {
                            subGradLimit = gradLimit;
                        }
                        idx = (int)(delta * 255.0f / this.normalizedIntervals[gradIdx] * 65536.0f) + 32768;
                        step = (int)(this.dgdX * 255.0f / this.normalizedIntervals[gradIdx] * 65536.0f);
                        while (off < subGradLimit) {
                            pixels[off++] = grad[idx >> 16];
                            idx += step;
                        }
                        g = (float)((double)g + (double)this.dgdX * stepsD);
                        ++gradIdx;
                    }
                } else {
                    for (gradIdx = this.gradientsLength - 1; gradIdx > 0 && !(g > this.fractions[gradIdx]); --gradIdx) {
                    }
                    while (off < gradLimit) {
                        delta = g - this.fractions[gradIdx];
                        grad = this.gradients[gradIdx];
                        stepsD = Math.ceil(delta / -this.dgdX);
                        steps = stepsD > (double)w ? w : (int)stepsD;
                        subGradLimit = off + steps;
                        if (subGradLimit > gradLimit) {
                            subGradLimit = gradLimit;
                        }
                        idx = (int)(delta * 255.0f / this.normalizedIntervals[gradIdx] * 65536.0f) + 32768;
                        step = (int)(this.dgdX * 255.0f / this.normalizedIntervals[gradIdx] * 65536.0f);
                        while (off < subGradLimit) {
                            pixels[off++] = grad[idx >> 16];
                            idx += step;
                        }
                        g = (float)((double)g + (double)this.dgdX * stepsD);
                        --gradIdx;
                    }
                }
                while (off < rowLimit) {
                    pixels[off++] = postVal;
                }
            }
            off += adjust;
        }
    }

    protected void fillSimpleNoCycle(int[] pixels, int off, int adjust, int x, int y, int w, int h) {
        float initConst = this.dgdX * (float)x + this.gc;
        float step = this.dgdX * (float)this.fastGradientArraySize;
        int fpStep = (int)(step * 65536.0f);
        int[] grad = this.gradient;
        for (int i = 0; i < h; ++i) {
            float g = initConst + this.dgdY * (float)(y + i);
            g *= (float)this.fastGradientArraySize;
            g = (float)((double)g + 0.5);
            int rowLimit = off + w;
            float check = this.dgdX * (float)this.fastGradientArraySize * (float)w;
            if (check < 0.0f) {
                check = -check;
            }
            if ((double)check < 0.3) {
                int val = g <= 0.0f ? this.gradientUnderflow : (g >= (float)this.fastGradientArraySize ? this.gradientOverflow : grad[(int)g]);
                while (off < rowLimit) {
                    pixels[off++] = val;
                }
            } else {
                int postVal;
                int preVal;
                int preGradSteps;
                int gradSteps;
                if (this.dgdX > 0.0f) {
                    gradSteps = (int)(((float)this.fastGradientArraySize - g) / step);
                    preGradSteps = (int)Math.ceil(0.0f - g / step);
                    preVal = this.gradientUnderflow;
                    postVal = this.gradientOverflow;
                } else {
                    gradSteps = (int)((0.0f - g) / step);
                    preGradSteps = (int)Math.ceil(((float)this.fastGradientArraySize - g) / step);
                    preVal = this.gradientOverflow;
                    postVal = this.gradientUnderflow;
                }
                if (gradSteps > w) {
                    gradSteps = w;
                }
                int gradLimit = off + gradSteps;
                if (preGradSteps > 0) {
                    if (preGradSteps > w) {
                        preGradSteps = w;
                    }
                    int preGradLimit = off + preGradSteps;
                    while (off < preGradLimit) {
                        pixels[off++] = preVal;
                    }
                    g += step * (float)preGradSteps;
                }
                int fpG = (int)(g * 65536.0f);
                while (off < gradLimit) {
                    pixels[off++] = grad[fpG >> 16];
                    fpG += fpStep;
                }
                while (off < rowLimit) {
                    pixels[off++] = postVal;
                }
            }
            off += adjust;
        }
    }

    protected void fillSimpleRepeat(int[] pixels, int off, int adjust, int x, int y, int w, int h) {
        float initConst = this.dgdX * (float)x + this.gc;
        float step = (this.dgdX - (float)((int)this.dgdX)) * (float)this.fastGradientArraySize;
        if (step < 0.0f) {
            step += (float)this.fastGradientArraySize;
        }
        int[] grad = this.gradient;
        for (int i = 0; i < h; ++i) {
            float g = initConst + this.dgdY * (float)(y + i);
            if ((g -= (float)((int)g)) < 0.0f) {
                g += 1.0f;
            }
            g *= (float)this.fastGradientArraySize;
            g = (float)((double)g + 0.5);
            int rowLimit = off + w;
            while (off < rowLimit) {
                int idx = (int)g;
                if (idx >= this.fastGradientArraySize) {
                    g -= (float)this.fastGradientArraySize;
                    idx -= this.fastGradientArraySize;
                }
                pixels[off++] = grad[idx];
                g += step;
            }
            off += adjust;
        }
    }

    protected void fillSimpleReflect(int[] pixels, int off, int adjust, int x, int y, int w, int h) {
        float initConst = this.dgdX * (float)x + this.gc;
        int[] grad = this.gradient;
        for (int i = 0; i < h; ++i) {
            float g = initConst + this.dgdY * (float)(y + i);
            g -= (float)(2 * (int)(g / 2.0f));
            float step = this.dgdX;
            if (g < 0.0f) {
                g = -g;
                step = -step;
            }
            if ((step -= 2.0f * ((float)((int)step) / 2.0f)) < 0.0f) {
                step = (float)((double)step + 2.0);
            }
            int reflectMax = 2 * this.fastGradientArraySize;
            g *= (float)this.fastGradientArraySize;
            g = (float)((double)g + 0.5);
            step *= (float)this.fastGradientArraySize;
            int rowLimit = off + w;
            while (off < rowLimit) {
                int idx = (int)g;
                if (idx >= reflectMax) {
                    g -= (float)reflectMax;
                    idx -= reflectMax;
                }
                pixels[off++] = idx <= this.fastGradientArraySize ? grad[idx] : grad[reflectMax - idx];
                g += step;
            }
            off += adjust;
        }
    }

    @Override
    protected void fillRaster(int[] pixels, int off, int adjust, int x, int y, int w, int h) {
        float initConst = this.dgdX * (float)x + this.gc;
        if (this.fillMethod == 3) {
            for (int i = 0; i < h; ++i) {
                float g = initConst + this.dgdY * (float)(y + i);
                int rowLimit = off + w;
                while (off < rowLimit) {
                    pixels[off++] = this.indexGradientAntiAlias(g, this.pixSz);
                    g += this.dgdX;
                }
                off += adjust;
            }
        } else if (!this.isSimpleLookup) {
            if (this.cycleMethod == MultipleGradientPaint.NO_CYCLE) {
                this.fillHardNoCycle(pixels, off, adjust, x, y, w, h);
            } else {
                for (int i = 0; i < h; ++i) {
                    float g = initConst + this.dgdY * (float)(y + i);
                    int rowLimit = off + w;
                    while (off < rowLimit) {
                        pixels[off++] = this.indexIntoGradientsArrays(g);
                        g += this.dgdX;
                    }
                    off += adjust;
                }
            }
        } else if (this.cycleMethod == MultipleGradientPaint.NO_CYCLE) {
            this.fillSimpleNoCycle(pixels, off, adjust, x, y, w, h);
        } else if (this.cycleMethod == MultipleGradientPaint.REPEAT) {
            this.fillSimpleRepeat(pixels, off, adjust, x, y, w, h);
        } else {
            this.fillSimpleReflect(pixels, off, adjust, x, y, w, h);
        }
    }
}

