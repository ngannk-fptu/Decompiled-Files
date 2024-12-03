/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.rendered;

import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import org.apache.batik.ext.awt.image.rendered.AbstractRed;
import org.apache.batik.ext.awt.image.rendered.AbstractTiledRed;
import org.apache.batik.ext.awt.image.rendered.CachableRed;

public final class TurbulencePatternRed
extends AbstractRed {
    private StitchInfo stitchInfo = null;
    private static final AffineTransform IDENTITY = new AffineTransform();
    private double baseFrequencyX;
    private double baseFrequencyY;
    private int numOctaves;
    private int seed;
    private Rectangle2D tile;
    private AffineTransform txf;
    private boolean isFractalNoise;
    private int[] channels;
    double[] tx = new double[]{1.0, 0.0};
    double[] ty = new double[]{0.0, 1.0};
    private static final int RAND_m = Integer.MAX_VALUE;
    private static final int RAND_a = 16807;
    private static final int RAND_q = 127773;
    private static final int RAND_r = 2836;
    private static final int BSize = 256;
    private static final int BM = 255;
    private static final double PerlinN = 4096.0;
    private final int[] latticeSelector = new int[257];
    private final double[] gradient = new double[2056];

    public double getBaseFrequencyX() {
        return this.baseFrequencyX;
    }

    public double getBaseFrequencyY() {
        return this.baseFrequencyY;
    }

    public int getNumOctaves() {
        return this.numOctaves;
    }

    public int getSeed() {
        return this.seed;
    }

    public Rectangle2D getTile() {
        return (Rectangle2D)this.tile.clone();
    }

    public boolean isFractalNoise() {
        return this.isFractalNoise;
    }

    public boolean[] getChannels() {
        boolean[] channels = new boolean[4];
        for (int channel : this.channels) {
            channels[channel] = true;
        }
        return channels;
    }

    public final int setupSeed(int seed) {
        if (seed <= 0) {
            seed = -(seed % 0x7FFFFFFE) + 1;
        }
        if (seed > 0x7FFFFFFE) {
            seed = 0x7FFFFFFE;
        }
        return seed;
    }

    public final int random(int seed) {
        int result = 16807 * (seed % 127773) - 2836 * (seed / 127773);
        if (result <= 0) {
            result += Integer.MAX_VALUE;
        }
        return result;
    }

    private void initLattice(int seed) {
        int j;
        double s;
        int i;
        int k;
        seed = this.setupSeed(seed);
        for (k = 0; k < 4; ++k) {
            for (i = 0; i < 256; ++i) {
                seed = this.random(seed);
                double u = seed % 512 - 256;
                seed = this.random(seed);
                double v = seed % 512 - 256;
                s = 1.0 / Math.sqrt(u * u + v * v);
                this.gradient[i * 8 + k * 2] = u * s;
                this.gradient[i * 8 + k * 2 + 1] = v * s;
            }
        }
        for (i = 0; i < 256; ++i) {
            this.latticeSelector[i] = i;
        }
        while (--i > 0) {
            k = this.latticeSelector[i];
            seed = this.random(seed);
            j = seed % 256;
            this.latticeSelector[i] = this.latticeSelector[j];
            this.latticeSelector[j] = k;
            int s1 = i << 3;
            int s2 = j << 3;
            for (j = 0; j < 8; ++j) {
                s = this.gradient[s1 + j];
                this.gradient[s1 + j] = this.gradient[s2 + j];
                this.gradient[s2 + j] = s;
            }
        }
        this.latticeSelector[256] = this.latticeSelector[0];
        for (j = 0; j < 8; ++j) {
            this.gradient[2048 + j] = this.gradient[j];
        }
    }

    private static final double s_curve(double t) {
        return t * t * (3.0 - 2.0 * t);
    }

    private static final double lerp(double t, double a, double b) {
        return a + t * (b - a);
    }

    private final void noise2(double[] noise, double vec0, double vec1) {
        int b0 = (int)(vec0 += 4096.0) & 0xFF;
        int i = this.latticeSelector[b0];
        int j = this.latticeSelector[b0 + 1];
        double rx0 = vec0 - (double)((int)vec0);
        double rx1 = rx0 - 1.0;
        double sx = TurbulencePatternRed.s_curve(rx0);
        b0 = (int)(vec1 += 4096.0);
        int b1 = (j + b0 & 0xFF) << 3;
        b0 = (i + b0 & 0xFF) << 3;
        double ry0 = vec1 - (double)((int)vec1);
        double ry1 = ry0 - 1.0;
        double sy = TurbulencePatternRed.s_curve(ry0);
        switch (this.channels.length) {
            case 4: {
                noise[3] = TurbulencePatternRed.lerp(sy, TurbulencePatternRed.lerp(sx, rx0 * this.gradient[b0 + 6] + ry0 * this.gradient[b0 + 7], rx1 * this.gradient[b1 + 6] + ry0 * this.gradient[b1 + 7]), TurbulencePatternRed.lerp(sx, rx0 * this.gradient[b0 + 8 + 6] + ry1 * this.gradient[b0 + 8 + 7], rx1 * this.gradient[b1 + 8 + 6] + ry1 * this.gradient[b1 + 8 + 7]));
            }
            case 3: {
                noise[2] = TurbulencePatternRed.lerp(sy, TurbulencePatternRed.lerp(sx, rx0 * this.gradient[b0 + 4] + ry0 * this.gradient[b0 + 5], rx1 * this.gradient[b1 + 4] + ry0 * this.gradient[b1 + 5]), TurbulencePatternRed.lerp(sx, rx0 * this.gradient[b0 + 8 + 4] + ry1 * this.gradient[b0 + 8 + 5], rx1 * this.gradient[b1 + 8 + 4] + ry1 * this.gradient[b1 + 8 + 5]));
            }
            case 2: {
                noise[1] = TurbulencePatternRed.lerp(sy, TurbulencePatternRed.lerp(sx, rx0 * this.gradient[b0 + 2] + ry0 * this.gradient[b0 + 3], rx1 * this.gradient[b1 + 2] + ry0 * this.gradient[b1 + 3]), TurbulencePatternRed.lerp(sx, rx0 * this.gradient[b0 + 8 + 2] + ry1 * this.gradient[b0 + 8 + 3], rx1 * this.gradient[b1 + 8 + 2] + ry1 * this.gradient[b1 + 8 + 3]));
            }
            case 1: {
                noise[0] = TurbulencePatternRed.lerp(sy, TurbulencePatternRed.lerp(sx, rx0 * this.gradient[b0 + 0] + ry0 * this.gradient[b0 + 1], rx1 * this.gradient[b1 + 0] + ry0 * this.gradient[b1 + 1]), TurbulencePatternRed.lerp(sx, rx0 * this.gradient[b0 + 8 + 0] + ry1 * this.gradient[b0 + 8 + 1], rx1 * this.gradient[b1 + 8 + 0] + ry1 * this.gradient[b1 + 8 + 1]));
            }
        }
    }

    private final void noise2Stitch(double[] noise, double vec0, double vec1, StitchInfo stitchInfo) {
        double t = vec0 + 4096.0;
        int b0 = (int)t;
        int b1 = b0 + 1;
        if (b1 >= stitchInfo.wrapX) {
            if (b0 >= stitchInfo.wrapX) {
                b0 -= stitchInfo.width;
                b1 -= stitchInfo.width;
            } else {
                b1 -= stitchInfo.width;
            }
        }
        int i = this.latticeSelector[b0 & 0xFF];
        int j = this.latticeSelector[b1 & 0xFF];
        double rx0 = t - (double)((int)t);
        double rx1 = rx0 - 1.0;
        double sx = TurbulencePatternRed.s_curve(rx0);
        t = vec1 + 4096.0;
        b0 = (int)t;
        b1 = b0 + 1;
        if (b1 >= stitchInfo.wrapY) {
            if (b0 >= stitchInfo.wrapY) {
                b0 -= stitchInfo.height;
                b1 -= stitchInfo.height;
            } else {
                b1 -= stitchInfo.height;
            }
        }
        int b00 = (i + b0 & 0xFF) << 3;
        int b10 = (j + b0 & 0xFF) << 3;
        int b01 = (i + b1 & 0xFF) << 3;
        int b11 = (j + b1 & 0xFF) << 3;
        double ry0 = t - (double)((int)t);
        double ry1 = ry0 - 1.0;
        double sy = TurbulencePatternRed.s_curve(ry0);
        switch (this.channels.length) {
            case 4: {
                noise[3] = TurbulencePatternRed.lerp(sy, TurbulencePatternRed.lerp(sx, rx0 * this.gradient[b00 + 6] + ry0 * this.gradient[b00 + 7], rx1 * this.gradient[b10 + 6] + ry0 * this.gradient[b10 + 7]), TurbulencePatternRed.lerp(sx, rx0 * this.gradient[b01 + 6] + ry1 * this.gradient[b01 + 7], rx1 * this.gradient[b11 + 6] + ry1 * this.gradient[b11 + 7]));
            }
            case 3: {
                noise[2] = TurbulencePatternRed.lerp(sy, TurbulencePatternRed.lerp(sx, rx0 * this.gradient[b00 + 4] + ry0 * this.gradient[b00 + 5], rx1 * this.gradient[b10 + 4] + ry0 * this.gradient[b10 + 5]), TurbulencePatternRed.lerp(sx, rx0 * this.gradient[b01 + 4] + ry1 * this.gradient[b01 + 5], rx1 * this.gradient[b11 + 4] + ry1 * this.gradient[b11 + 5]));
            }
            case 2: {
                noise[1] = TurbulencePatternRed.lerp(sy, TurbulencePatternRed.lerp(sx, rx0 * this.gradient[b00 + 2] + ry0 * this.gradient[b00 + 3], rx1 * this.gradient[b10 + 2] + ry0 * this.gradient[b10 + 3]), TurbulencePatternRed.lerp(sx, rx0 * this.gradient[b01 + 2] + ry1 * this.gradient[b01 + 3], rx1 * this.gradient[b11 + 2] + ry1 * this.gradient[b11 + 3]));
            }
            case 1: {
                noise[0] = TurbulencePatternRed.lerp(sy, TurbulencePatternRed.lerp(sx, rx0 * this.gradient[b00 + 0] + ry0 * this.gradient[b00 + 1], rx1 * this.gradient[b10 + 0] + ry0 * this.gradient[b10 + 1]), TurbulencePatternRed.lerp(sx, rx0 * this.gradient[b01 + 0] + ry1 * this.gradient[b01 + 1], rx1 * this.gradient[b11 + 0] + ry1 * this.gradient[b11 + 1]));
            }
        }
    }

    private final int turbulence_4(double pointX, double pointY, double[] fSum) {
        int j;
        int i;
        double ratio = 255.0;
        pointX *= this.baseFrequencyX;
        pointY *= this.baseFrequencyY;
        fSum[3] = 0.0;
        fSum[2] = 0.0;
        fSum[1] = 0.0;
        fSum[0] = 0.0;
        for (int nOctave = this.numOctaves; nOctave > 0; --nOctave) {
            double px = pointX + 4096.0;
            int b0 = (int)px & 0xFF;
            i = this.latticeSelector[b0];
            j = this.latticeSelector[b0 + 1];
            double rx0 = px - (double)((int)px);
            double rx1 = rx0 - 1.0;
            double sx = TurbulencePatternRed.s_curve(rx0);
            double py = pointY + 4096.0;
            b0 = (int)py & 0xFF;
            int b1 = b0 + 1 & 0xFF;
            b1 = (j + b0 & 0xFF) << 3;
            b0 = (i + b0 & 0xFF) << 3;
            double ry0 = py - (double)((int)py);
            double ry1 = ry0 - 1.0;
            double sy = TurbulencePatternRed.s_curve(ry0);
            double n = TurbulencePatternRed.lerp(sy, TurbulencePatternRed.lerp(sx, rx0 * this.gradient[b0 + 0] + ry0 * this.gradient[b0 + 1], rx1 * this.gradient[b1 + 0] + ry0 * this.gradient[b1 + 1]), TurbulencePatternRed.lerp(sx, rx0 * this.gradient[b0 + 8 + 0] + ry1 * this.gradient[b0 + 8 + 1], rx1 * this.gradient[b1 + 8 + 0] + ry1 * this.gradient[b1 + 8 + 1]));
            fSum[0] = n < 0.0 ? fSum[0] - n * ratio : fSum[0] + n * ratio;
            n = TurbulencePatternRed.lerp(sy, TurbulencePatternRed.lerp(sx, rx0 * this.gradient[b0 + 2] + ry0 * this.gradient[b0 + 3], rx1 * this.gradient[b1 + 2] + ry0 * this.gradient[b1 + 3]), TurbulencePatternRed.lerp(sx, rx0 * this.gradient[b0 + 8 + 2] + ry1 * this.gradient[b0 + 8 + 3], rx1 * this.gradient[b1 + 8 + 2] + ry1 * this.gradient[b1 + 8 + 3]));
            fSum[1] = n < 0.0 ? fSum[1] - n * ratio : fSum[1] + n * ratio;
            n = TurbulencePatternRed.lerp(sy, TurbulencePatternRed.lerp(sx, rx0 * this.gradient[b0 + 4] + ry0 * this.gradient[b0 + 5], rx1 * this.gradient[b1 + 4] + ry0 * this.gradient[b1 + 5]), TurbulencePatternRed.lerp(sx, rx0 * this.gradient[b0 + 8 + 4] + ry1 * this.gradient[b0 + 8 + 5], rx1 * this.gradient[b1 + 8 + 4] + ry1 * this.gradient[b1 + 8 + 5]));
            fSum[2] = n < 0.0 ? fSum[2] - n * ratio : fSum[2] + n * ratio;
            n = TurbulencePatternRed.lerp(sy, TurbulencePatternRed.lerp(sx, rx0 * this.gradient[b0 + 6] + ry0 * this.gradient[b0 + 7], rx1 * this.gradient[b1 + 6] + ry0 * this.gradient[b1 + 7]), TurbulencePatternRed.lerp(sx, rx0 * this.gradient[b0 + 8 + 6] + ry1 * this.gradient[b0 + 8 + 7], rx1 * this.gradient[b1 + 8 + 6] + ry1 * this.gradient[b1 + 8 + 7]));
            fSum[3] = n < 0.0 ? fSum[3] - n * ratio : fSum[3] + n * ratio;
            ratio *= 0.5;
            pointX *= 2.0;
            pointY *= 2.0;
        }
        i = (int)fSum[0];
        j = (i & 0xFFFFFF00) == 0 ? i << 16 : ((i & Integer.MIN_VALUE) != 0 ? 0 : 0xFF0000);
        i = (int)fSum[1];
        j = (i & 0xFFFFFF00) == 0 ? (j |= i << 8) : (j |= (i & Integer.MIN_VALUE) != 0 ? 0 : 65280);
        i = (int)fSum[2];
        j = (i & 0xFFFFFF00) == 0 ? (j |= i) : (j |= (i & Integer.MIN_VALUE) != 0 ? 0 : 255);
        i = (int)fSum[3];
        j = (i & 0xFFFFFF00) == 0 ? (j |= i << 24) : (j |= (i & Integer.MIN_VALUE) != 0 ? 0 : -16777216);
        return j;
    }

    private final void turbulence(int[] rgb, double pointX, double pointY, double[] fSum, double[] noise) {
        fSum[3] = 0.0;
        fSum[2] = 0.0;
        fSum[1] = 0.0;
        fSum[0] = 0.0;
        double ratio = 255.0;
        pointX *= this.baseFrequencyX;
        pointY *= this.baseFrequencyY;
        switch (this.channels.length) {
            case 4: {
                for (int nOctave = 0; nOctave < this.numOctaves; ++nOctave) {
                    this.noise2(noise, pointX, pointY);
                    fSum[0] = noise[0] < 0.0 ? fSum[0] - noise[0] * ratio : fSum[0] + noise[0] * ratio;
                    fSum[1] = noise[1] < 0.0 ? fSum[1] - noise[1] * ratio : fSum[1] + noise[1] * ratio;
                    fSum[2] = noise[2] < 0.0 ? fSum[2] - noise[2] * ratio : fSum[2] + noise[2] * ratio;
                    fSum[3] = noise[3] < 0.0 ? fSum[3] - noise[3] * ratio : fSum[3] + noise[3] * ratio;
                    ratio *= 0.5;
                    pointX *= 2.0;
                    pointY *= 2.0;
                }
                rgb[0] = (int)fSum[0];
                if ((rgb[0] & 0xFFFFFF00) != 0) {
                    rgb[0] = (rgb[0] & Integer.MIN_VALUE) != 0 ? 0 : 255;
                }
                rgb[1] = (int)fSum[1];
                if ((rgb[1] & 0xFFFFFF00) != 0) {
                    rgb[1] = (rgb[1] & Integer.MIN_VALUE) != 0 ? 0 : 255;
                }
                rgb[2] = (int)fSum[2];
                if ((rgb[2] & 0xFFFFFF00) != 0) {
                    rgb[2] = (rgb[2] & Integer.MIN_VALUE) != 0 ? 0 : 255;
                }
                rgb[3] = (int)fSum[3];
                if ((rgb[3] & 0xFFFFFF00) == 0) break;
                rgb[3] = (rgb[3] & Integer.MIN_VALUE) != 0 ? 0 : 255;
                break;
            }
            case 3: {
                for (int nOctave = 0; nOctave < this.numOctaves; ++nOctave) {
                    this.noise2(noise, pointX, pointY);
                    fSum[2] = noise[2] < 0.0 ? fSum[2] - noise[2] * ratio : fSum[2] + noise[2] * ratio;
                    fSum[1] = noise[1] < 0.0 ? fSum[1] - noise[1] * ratio : fSum[1] + noise[1] * ratio;
                    fSum[0] = noise[0] < 0.0 ? fSum[0] - noise[0] * ratio : fSum[0] + noise[0] * ratio;
                    ratio *= 0.5;
                    pointX *= 2.0;
                    pointY *= 2.0;
                }
                rgb[2] = (int)fSum[2];
                if ((rgb[2] & 0xFFFFFF00) != 0) {
                    rgb[2] = (rgb[2] & Integer.MIN_VALUE) != 0 ? 0 : 255;
                }
                rgb[1] = (int)fSum[1];
                if ((rgb[1] & 0xFFFFFF00) != 0) {
                    rgb[1] = (rgb[1] & Integer.MIN_VALUE) != 0 ? 0 : 255;
                }
                rgb[0] = (int)fSum[0];
                if ((rgb[0] & 0xFFFFFF00) == 0) break;
                rgb[0] = (rgb[0] & Integer.MIN_VALUE) != 0 ? 0 : 255;
                break;
            }
            case 2: {
                for (int nOctave = 0; nOctave < this.numOctaves; ++nOctave) {
                    this.noise2(noise, pointX, pointY);
                    fSum[1] = noise[1] < 0.0 ? fSum[1] - noise[1] * ratio : fSum[1] + noise[1] * ratio;
                    fSum[0] = noise[0] < 0.0 ? fSum[0] - noise[0] * ratio : fSum[0] + noise[0] * ratio;
                    ratio *= 0.5;
                    pointX *= 2.0;
                    pointY *= 2.0;
                }
                rgb[1] = (int)fSum[1];
                if ((rgb[1] & 0xFFFFFF00) != 0) {
                    rgb[1] = (rgb[1] & Integer.MIN_VALUE) != 0 ? 0 : 255;
                }
                rgb[0] = (int)fSum[0];
                if ((rgb[0] & 0xFFFFFF00) == 0) break;
                rgb[0] = (rgb[0] & Integer.MIN_VALUE) != 0 ? 0 : 255;
                break;
            }
            case 1: {
                for (int nOctave = 0; nOctave < this.numOctaves; ++nOctave) {
                    this.noise2(noise, pointX, pointY);
                    fSum[0] = noise[0] < 0.0 ? fSum[0] - noise[0] * ratio : fSum[0] + noise[0] * ratio;
                    ratio *= 0.5;
                    pointX *= 2.0;
                    pointY *= 2.0;
                }
                rgb[0] = (int)fSum[0];
                if ((rgb[0] & 0xFFFFFF00) == 0) break;
                rgb[0] = (rgb[0] & Integer.MIN_VALUE) != 0 ? 0 : 255;
            }
        }
    }

    private final void turbulenceStitch(int[] rgb, double pointX, double pointY, double[] fSum, double[] noise, StitchInfo stitchInfo) {
        double ratio = 1.0;
        pointX *= this.baseFrequencyX;
        pointY *= this.baseFrequencyY;
        fSum[3] = 0.0;
        fSum[2] = 0.0;
        fSum[1] = 0.0;
        fSum[0] = 0.0;
        switch (this.channels.length) {
            case 4: {
                for (int nOctave = 0; nOctave < this.numOctaves; ++nOctave) {
                    this.noise2Stitch(noise, pointX, pointY, stitchInfo);
                    fSum[3] = noise[3] < 0.0 ? fSum[3] - noise[3] * ratio : fSum[3] + noise[3] * ratio;
                    fSum[2] = noise[2] < 0.0 ? fSum[2] - noise[2] * ratio : fSum[2] + noise[2] * ratio;
                    fSum[1] = noise[1] < 0.0 ? fSum[1] - noise[1] * ratio : fSum[1] + noise[1] * ratio;
                    fSum[0] = noise[0] < 0.0 ? fSum[0] - noise[0] * ratio : fSum[0] + noise[0] * ratio;
                    ratio *= 0.5;
                    pointX *= 2.0;
                    pointY *= 2.0;
                    stitchInfo.doubleFrequency();
                }
                rgb[3] = (int)(fSum[3] * 255.0);
                if ((rgb[3] & 0xFFFFFF00) != 0) {
                    rgb[3] = (rgb[3] & Integer.MIN_VALUE) != 0 ? 0 : 255;
                }
                rgb[2] = (int)(fSum[2] * 255.0);
                if ((rgb[2] & 0xFFFFFF00) != 0) {
                    rgb[2] = (rgb[2] & Integer.MIN_VALUE) != 0 ? 0 : 255;
                }
                rgb[1] = (int)(fSum[1] * 255.0);
                if ((rgb[1] & 0xFFFFFF00) != 0) {
                    rgb[1] = (rgb[1] & Integer.MIN_VALUE) != 0 ? 0 : 255;
                }
                rgb[0] = (int)(fSum[0] * 255.0);
                if ((rgb[0] & 0xFFFFFF00) == 0) break;
                rgb[0] = (rgb[0] & Integer.MIN_VALUE) != 0 ? 0 : 255;
                break;
            }
            case 3: {
                for (int nOctave = 0; nOctave < this.numOctaves; ++nOctave) {
                    this.noise2Stitch(noise, pointX, pointY, stitchInfo);
                    fSum[2] = noise[2] < 0.0 ? fSum[2] - noise[2] * ratio : fSum[2] + noise[2] * ratio;
                    fSum[1] = noise[1] < 0.0 ? fSum[1] - noise[1] * ratio : fSum[1] + noise[1] * ratio;
                    fSum[0] = noise[0] < 0.0 ? fSum[0] - noise[0] * ratio : fSum[0] + noise[0] * ratio;
                    ratio *= 0.5;
                    pointX *= 2.0;
                    pointY *= 2.0;
                    stitchInfo.doubleFrequency();
                }
                rgb[2] = (int)(fSum[2] * 255.0);
                if ((rgb[2] & 0xFFFFFF00) != 0) {
                    rgb[2] = (rgb[2] & Integer.MIN_VALUE) != 0 ? 0 : 255;
                }
                rgb[1] = (int)(fSum[1] * 255.0);
                if ((rgb[1] & 0xFFFFFF00) != 0) {
                    rgb[1] = (rgb[1] & Integer.MIN_VALUE) != 0 ? 0 : 255;
                }
                rgb[0] = (int)(fSum[0] * 255.0);
                if ((rgb[0] & 0xFFFFFF00) == 0) break;
                rgb[0] = (rgb[0] & Integer.MIN_VALUE) != 0 ? 0 : 255;
                break;
            }
            case 2: {
                for (int nOctave = 0; nOctave < this.numOctaves; ++nOctave) {
                    this.noise2Stitch(noise, pointX, pointY, stitchInfo);
                    fSum[1] = noise[1] < 0.0 ? fSum[1] - noise[1] * ratio : fSum[1] + noise[1] * ratio;
                    fSum[0] = noise[0] < 0.0 ? fSum[0] - noise[0] * ratio : fSum[0] + noise[0] * ratio;
                    ratio *= 0.5;
                    pointX *= 2.0;
                    pointY *= 2.0;
                    stitchInfo.doubleFrequency();
                }
                rgb[1] = (int)(fSum[1] * 255.0);
                if ((rgb[1] & 0xFFFFFF00) != 0) {
                    rgb[1] = (rgb[1] & Integer.MIN_VALUE) != 0 ? 0 : 255;
                }
                rgb[0] = (int)(fSum[0] * 255.0);
                if ((rgb[0] & 0xFFFFFF00) == 0) break;
                rgb[0] = (rgb[0] & Integer.MIN_VALUE) != 0 ? 0 : 255;
                break;
            }
            case 1: {
                for (int nOctave = 0; nOctave < this.numOctaves; ++nOctave) {
                    this.noise2Stitch(noise, pointX, pointY, stitchInfo);
                    fSum[0] = noise[0] < 0.0 ? fSum[0] - noise[0] * ratio : fSum[0] + noise[0] * ratio;
                    ratio *= 0.5;
                    pointX *= 2.0;
                    pointY *= 2.0;
                    stitchInfo.doubleFrequency();
                }
                rgb[0] = (int)(fSum[0] * 255.0);
                if ((rgb[0] & 0xFFFFFF00) == 0) break;
                rgb[0] = (rgb[0] & Integer.MIN_VALUE) != 0 ? 0 : 255;
            }
        }
    }

    private final int turbulenceFractal_4(double pointX, double pointY, double[] fSum) {
        int j;
        int i;
        double ratio = 127.5;
        pointX *= this.baseFrequencyX;
        pointY *= this.baseFrequencyY;
        fSum[3] = 127.5;
        fSum[2] = 127.5;
        fSum[1] = 127.5;
        fSum[0] = 127.5;
        for (int nOctave = this.numOctaves; nOctave > 0; --nOctave) {
            double px = pointX + 4096.0;
            int b0 = (int)px & 0xFF;
            i = this.latticeSelector[b0];
            j = this.latticeSelector[b0 + 1];
            double rx0 = px - (double)((int)px);
            double rx1 = rx0 - 1.0;
            double sx = TurbulencePatternRed.s_curve(rx0);
            double py = pointY + 4096.0;
            b0 = (int)py & 0xFF;
            int b1 = b0 + 1 & 0xFF;
            b1 = (j + b0 & 0xFF) << 3;
            b0 = (i + b0 & 0xFF) << 3;
            double ry0 = py - (double)((int)py);
            double ry1 = ry0 - 1.0;
            double sy = TurbulencePatternRed.s_curve(ry0);
            fSum[0] = fSum[0] + TurbulencePatternRed.lerp(sy, TurbulencePatternRed.lerp(sx, rx0 * this.gradient[b0 + 0] + ry0 * this.gradient[b0 + 1], rx1 * this.gradient[b1 + 0] + ry0 * this.gradient[b1 + 1]), TurbulencePatternRed.lerp(sx, rx0 * this.gradient[b0 + 8 + 0] + ry1 * this.gradient[b0 + 8 + 1], rx1 * this.gradient[b1 + 8 + 0] + ry1 * this.gradient[b1 + 8 + 1])) * ratio;
            fSum[1] = fSum[1] + TurbulencePatternRed.lerp(sy, TurbulencePatternRed.lerp(sx, rx0 * this.gradient[b0 + 2] + ry0 * this.gradient[b0 + 3], rx1 * this.gradient[b1 + 2] + ry0 * this.gradient[b1 + 3]), TurbulencePatternRed.lerp(sx, rx0 * this.gradient[b0 + 8 + 2] + ry1 * this.gradient[b0 + 8 + 3], rx1 * this.gradient[b1 + 8 + 2] + ry1 * this.gradient[b1 + 8 + 3])) * ratio;
            fSum[2] = fSum[2] + TurbulencePatternRed.lerp(sy, TurbulencePatternRed.lerp(sx, rx0 * this.gradient[b0 + 4] + ry0 * this.gradient[b0 + 5], rx1 * this.gradient[b1 + 4] + ry0 * this.gradient[b1 + 5]), TurbulencePatternRed.lerp(sx, rx0 * this.gradient[b0 + 8 + 4] + ry1 * this.gradient[b0 + 8 + 5], rx1 * this.gradient[b1 + 8 + 4] + ry1 * this.gradient[b1 + 8 + 5])) * ratio;
            fSum[3] = fSum[3] + TurbulencePatternRed.lerp(sy, TurbulencePatternRed.lerp(sx, rx0 * this.gradient[b0 + 6] + ry0 * this.gradient[b0 + 7], rx1 * this.gradient[b1 + 6] + ry0 * this.gradient[b1 + 7]), TurbulencePatternRed.lerp(sx, rx0 * this.gradient[b0 + 8 + 6] + ry1 * this.gradient[b0 + 8 + 7], rx1 * this.gradient[b1 + 8 + 6] + ry1 * this.gradient[b1 + 8 + 7])) * ratio;
            ratio *= 0.5;
            pointX *= 2.0;
            pointY *= 2.0;
        }
        i = (int)fSum[0];
        j = (i & 0xFFFFFF00) == 0 ? i << 16 : ((i & Integer.MIN_VALUE) != 0 ? 0 : 0xFF0000);
        i = (int)fSum[1];
        j = (i & 0xFFFFFF00) == 0 ? (j |= i << 8) : (j |= (i & Integer.MIN_VALUE) != 0 ? 0 : 65280);
        i = (int)fSum[2];
        j = (i & 0xFFFFFF00) == 0 ? (j |= i) : (j |= (i & Integer.MIN_VALUE) != 0 ? 0 : 255);
        i = (int)fSum[3];
        j = (i & 0xFFFFFF00) == 0 ? (j |= i << 24) : (j |= (i & Integer.MIN_VALUE) != 0 ? 0 : -16777216);
        return j;
    }

    private final void turbulenceFractal(int[] rgb, double pointX, double pointY, double[] fSum, double[] noise) {
        double ratio = 127.5;
        fSum[3] = 127.5;
        fSum[2] = 127.5;
        fSum[1] = 127.5;
        fSum[0] = 127.5;
        pointX *= this.baseFrequencyX;
        pointY *= this.baseFrequencyY;
        for (int nOctave = this.numOctaves; nOctave > 0; --nOctave) {
            this.noise2(noise, pointX, pointY);
            switch (this.channels.length) {
                case 4: {
                    fSum[3] = fSum[3] + noise[3] * ratio;
                }
                case 3: {
                    fSum[2] = fSum[2] + noise[2] * ratio;
                }
                case 2: {
                    fSum[1] = fSum[1] + noise[1] * ratio;
                }
                case 1: {
                    fSum[0] = fSum[0] + noise[0] * ratio;
                }
            }
            ratio *= 0.5;
            pointX *= 2.0;
            pointY *= 2.0;
        }
        switch (this.channels.length) {
            case 4: {
                rgb[3] = (int)fSum[3];
                if ((rgb[3] & 0xFFFFFF00) != 0) {
                    rgb[3] = (rgb[3] & Integer.MIN_VALUE) != 0 ? 0 : 255;
                }
            }
            case 3: {
                rgb[2] = (int)fSum[2];
                if ((rgb[2] & 0xFFFFFF00) != 0) {
                    rgb[2] = (rgb[2] & Integer.MIN_VALUE) != 0 ? 0 : 255;
                }
            }
            case 2: {
                rgb[1] = (int)fSum[1];
                if ((rgb[1] & 0xFFFFFF00) != 0) {
                    rgb[1] = (rgb[1] & Integer.MIN_VALUE) != 0 ? 0 : 255;
                }
            }
            case 1: {
                rgb[0] = (int)fSum[0];
                if ((rgb[0] & 0xFFFFFF00) == 0) break;
                rgb[0] = (rgb[0] & Integer.MIN_VALUE) != 0 ? 0 : 255;
            }
        }
    }

    private final void turbulenceFractalStitch(int[] rgb, double pointX, double pointY, double[] fSum, double[] noise, StitchInfo stitchInfo) {
        double ratio = 127.5;
        fSum[3] = 127.5;
        fSum[2] = 127.5;
        fSum[1] = 127.5;
        fSum[0] = 127.5;
        pointX *= this.baseFrequencyX;
        pointY *= this.baseFrequencyY;
        for (int nOctave = this.numOctaves; nOctave > 0; --nOctave) {
            this.noise2Stitch(noise, pointX, pointY, stitchInfo);
            switch (this.channels.length) {
                case 4: {
                    fSum[3] = fSum[3] + noise[3] * ratio;
                }
                case 3: {
                    fSum[2] = fSum[2] + noise[2] * ratio;
                }
                case 2: {
                    fSum[1] = fSum[1] + noise[1] * ratio;
                }
                case 1: {
                    fSum[0] = fSum[0] + noise[0] * ratio;
                }
            }
            ratio *= 0.5;
            pointX *= 2.0;
            pointY *= 2.0;
            stitchInfo.doubleFrequency();
        }
        switch (this.channels.length) {
            case 4: {
                rgb[3] = (int)fSum[3];
                if ((rgb[3] & 0xFFFFFF00) != 0) {
                    rgb[3] = (rgb[3] & Integer.MIN_VALUE) != 0 ? 0 : 255;
                }
            }
            case 3: {
                rgb[2] = (int)fSum[2];
                if ((rgb[2] & 0xFFFFFF00) != 0) {
                    rgb[2] = (rgb[2] & Integer.MIN_VALUE) != 0 ? 0 : 255;
                }
            }
            case 2: {
                rgb[1] = (int)fSum[1];
                if ((rgb[1] & 0xFFFFFF00) != 0) {
                    rgb[1] = (rgb[1] & Integer.MIN_VALUE) != 0 ? 0 : 255;
                }
            }
            case 1: {
                rgb[0] = (int)fSum[0];
                if ((rgb[0] & 0xFFFFFF00) == 0) break;
                rgb[0] = (rgb[0] & Integer.MIN_VALUE) != 0 ? 0 : 255;
            }
        }
    }

    @Override
    public WritableRaster copyData(WritableRaster dest) {
        if (dest == null) {
            throw new IllegalArgumentException("Cannot generate a noise pattern into a null raster");
        }
        int w = dest.getWidth();
        int h = dest.getHeight();
        DataBufferInt dstDB = (DataBufferInt)dest.getDataBuffer();
        int minX = dest.getMinX();
        int minY = dest.getMinY();
        SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)dest.getSampleModel();
        int dstOff = dstDB.getOffset() + sppsm.getOffset(minX - dest.getSampleModelTranslateX(), minY - dest.getSampleModelTranslateY());
        int[] destPixels = dstDB.getBankData()[0];
        int dstAdjust = sppsm.getScanlineStride() - w;
        int dp = dstOff;
        int[] rgb = new int[4];
        double[] fSum = new double[]{0.0, 0.0, 0.0, 0.0};
        double[] noise = new double[]{0.0, 0.0, 0.0, 0.0};
        double tx0 = this.tx[0];
        double tx1 = this.tx[1];
        double ty0 = this.ty[0] - (double)w * tx0;
        double ty1 = this.ty[1] - (double)w * tx1;
        double[] p = new double[]{minX, minY};
        this.txf.transform(p, 0, p, 0, 1);
        double point_0 = p[0];
        double point_1 = p[1];
        if (this.isFractalNoise) {
            if (this.stitchInfo == null) {
                if (this.channels.length == 4) {
                    for (int i = 0; i < h; ++i) {
                        int end = dp + w;
                        while (dp < end) {
                            destPixels[dp] = this.turbulenceFractal_4(point_0, point_1, fSum);
                            point_0 += tx0;
                            point_1 += tx1;
                            ++dp;
                        }
                        point_0 += ty0;
                        point_1 += ty1;
                        dp += dstAdjust;
                    }
                } else {
                    for (int i = 0; i < h; ++i) {
                        int end = dp + w;
                        while (dp < end) {
                            this.turbulenceFractal(rgb, point_0, point_1, fSum, noise);
                            destPixels[dp] = rgb[3] << 24 | rgb[0] << 16 | rgb[1] << 8 | rgb[2];
                            point_0 += tx0;
                            point_1 += tx1;
                            ++dp;
                        }
                        point_0 += ty0;
                        point_1 += ty1;
                        dp += dstAdjust;
                    }
                }
            } else {
                StitchInfo si = new StitchInfo();
                for (int i = 0; i < h; ++i) {
                    int end = dp + w;
                    while (dp < end) {
                        si.assign(this.stitchInfo);
                        this.turbulenceFractalStitch(rgb, point_0, point_1, fSum, noise, si);
                        destPixels[dp] = rgb[3] << 24 | rgb[0] << 16 | rgb[1] << 8 | rgb[2];
                        point_0 += tx0;
                        point_1 += tx1;
                        ++dp;
                    }
                    point_0 += ty0;
                    point_1 += ty1;
                    dp += dstAdjust;
                }
            }
        } else if (this.stitchInfo == null) {
            if (this.channels.length == 4) {
                for (int i = 0; i < h; ++i) {
                    int end = dp + w;
                    while (dp < end) {
                        destPixels[dp] = this.turbulence_4(point_0, point_1, fSum);
                        point_0 += tx0;
                        point_1 += tx1;
                        ++dp;
                    }
                    point_0 += ty0;
                    point_1 += ty1;
                    dp += dstAdjust;
                }
            } else {
                for (int i = 0; i < h; ++i) {
                    int end = dp + w;
                    while (dp < end) {
                        this.turbulence(rgb, point_0, point_1, fSum, noise);
                        destPixels[dp] = rgb[3] << 24 | rgb[0] << 16 | rgb[1] << 8 | rgb[2];
                        point_0 += tx0;
                        point_1 += tx1;
                        ++dp;
                    }
                    point_0 += ty0;
                    point_1 += ty1;
                    dp += dstAdjust;
                }
            }
        } else {
            StitchInfo si = new StitchInfo();
            for (int i = 0; i < h; ++i) {
                int end = dp + w;
                while (dp < end) {
                    si.assign(this.stitchInfo);
                    this.turbulenceStitch(rgb, point_0, point_1, fSum, noise, si);
                    destPixels[dp] = rgb[3] << 24 | rgb[0] << 16 | rgb[1] << 8 | rgb[2];
                    point_0 += tx0;
                    point_1 += tx1;
                    ++dp;
                }
                point_0 += ty0;
                point_1 += ty1;
                dp += dstAdjust;
            }
        }
        return dest;
    }

    public TurbulencePatternRed(double baseFrequencyX, double baseFrequencyY, int numOctaves, int seed, boolean isFractalNoise, Rectangle2D tile, AffineTransform txf, Rectangle devRect, ColorSpace cs, boolean alpha) {
        this.baseFrequencyX = baseFrequencyX;
        this.baseFrequencyY = baseFrequencyY;
        this.seed = seed;
        this.isFractalNoise = isFractalNoise;
        this.tile = tile;
        this.txf = txf;
        if (this.txf == null) {
            this.txf = IDENTITY;
        }
        int nChannels = cs.getNumComponents();
        if (alpha) {
            ++nChannels;
        }
        this.channels = new int[nChannels];
        for (int i = 0; i < this.channels.length; ++i) {
            this.channels[i] = i;
        }
        txf.deltaTransform(this.tx, 0, this.tx, 0, 1);
        txf.deltaTransform(this.ty, 0, this.ty, 0, 1);
        double[] vecX = new double[]{0.5, 0.0};
        double[] vecY = new double[]{0.0, 0.5};
        txf.deltaTransform(vecX, 0, vecX, 0, 1);
        txf.deltaTransform(vecY, 0, vecY, 0, 1);
        double dx = Math.max(Math.abs(vecX[0]), Math.abs(vecY[0]));
        int maxX = -((int)Math.round((Math.log(dx) + Math.log(baseFrequencyX)) / Math.log(2.0)));
        double dy = Math.max(Math.abs(vecX[1]), Math.abs(vecY[1]));
        int maxY = -((int)Math.round((Math.log(dy) + Math.log(baseFrequencyY)) / Math.log(2.0)));
        this.numOctaves = numOctaves > maxX ? maxX : numOctaves;
        int n = this.numOctaves = this.numOctaves > maxY ? maxY : this.numOctaves;
        if (this.numOctaves < 1 && numOctaves > 1) {
            this.numOctaves = 1;
        }
        if (this.numOctaves > 8) {
            this.numOctaves = 8;
        }
        if (tile != null) {
            double highFreq;
            double lowFreq = Math.floor(tile.getWidth() * baseFrequencyX) / tile.getWidth();
            this.baseFrequencyX = baseFrequencyX / lowFreq < (highFreq = Math.ceil(tile.getWidth() * baseFrequencyX) / tile.getWidth()) / baseFrequencyX ? lowFreq : highFreq;
            lowFreq = Math.floor(tile.getHeight() * baseFrequencyY) / tile.getHeight();
            highFreq = Math.ceil(tile.getHeight() * baseFrequencyY) / tile.getHeight();
            this.baseFrequencyY = baseFrequencyY / lowFreq < highFreq / baseFrequencyY ? lowFreq : highFreq;
            this.stitchInfo = new StitchInfo();
            this.stitchInfo.width = (int)(tile.getWidth() * this.baseFrequencyX);
            this.stitchInfo.height = (int)(tile.getHeight() * this.baseFrequencyY);
            this.stitchInfo.wrapX = (int)(tile.getX() * this.baseFrequencyX + 4096.0 + (double)this.stitchInfo.width);
            this.stitchInfo.wrapY = (int)(tile.getY() * this.baseFrequencyY + 4096.0 + (double)this.stitchInfo.height);
            if (this.stitchInfo.width == 0) {
                this.stitchInfo.width = 1;
            }
            if (this.stitchInfo.height == 0) {
                this.stitchInfo.height = 1;
            }
        }
        this.initLattice(seed);
        DirectColorModel cm = alpha ? new DirectColorModel(cs, 32, 0xFF0000, 65280, 255, -16777216, false, 3) : new DirectColorModel(cs, 24, 0xFF0000, 65280, 255, 0, false, 3);
        int tileSize = AbstractTiledRed.getDefaultTileSize();
        this.init((CachableRed)null, devRect, (ColorModel)cm, ((ColorModel)cm).createCompatibleSampleModel(tileSize, tileSize), 0, 0, null);
    }

    static final class StitchInfo {
        int width;
        int height;
        int wrapX;
        int wrapY;

        StitchInfo() {
        }

        StitchInfo(StitchInfo stitchInfo) {
            this.width = stitchInfo.width;
            this.height = stitchInfo.height;
            this.wrapX = stitchInfo.wrapX;
            this.wrapY = stitchInfo.wrapY;
        }

        final void assign(StitchInfo stitchInfo) {
            this.width = stitchInfo.width;
            this.height = stitchInfo.height;
            this.wrapX = stitchInfo.wrapX;
            this.wrapY = stitchInfo.wrapY;
        }

        final void doubleFrequency() {
            this.width *= 2;
            this.height *= 2;
            this.wrapX *= 2;
            this.wrapY *= 2;
            this.wrapX = (int)((double)this.wrapX - 4096.0);
            this.wrapY = (int)((double)this.wrapY - 4096.0);
        }
    }
}

