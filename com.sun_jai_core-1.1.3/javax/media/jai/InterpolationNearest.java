/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import javax.media.jai.Interpolation;

public final class InterpolationNearest
extends Interpolation {
    public InterpolationNearest() {
        super(1, 1, 0, 0, 0, 0, 0, 0);
    }

    public int interpolateH(int[] samples, int xfrac) {
        return samples[0];
    }

    public int interpolateV(int[] samples, int yfrac) {
        return samples[0];
    }

    public int interpolate(int[][] samples, int xfrac, int yfrac) {
        return samples[0][0];
    }

    public int interpolateH(int s0, int s1, int xfrac) {
        return s0;
    }

    public int interpolateV(int s0, int s1, int yfrac) {
        return s0;
    }

    public int interpolate(int s00, int s01, int s10, int s11, int xfrac, int yfrac) {
        return s00;
    }

    public int interpolate(int s__, int s_0, int s_1, int s_2, int s0_, int s00, int s01, int s02, int s1_, int s10, int s11, int s12, int s2_, int s20, int s21, int s22, int xfrac, int yfrac) {
        return s00;
    }

    public float interpolateH(float[] samples, float xfrac) {
        return samples[0];
    }

    public float interpolateV(float[] samples, float yfrac) {
        return samples[0];
    }

    public float interpolate(float[][] samples, float xfrac, float yfrac) {
        return samples[0][0];
    }

    public float interpolateH(float s0, float s1, float xfrac) {
        return s0;
    }

    public float interpolateV(float s0, float s1, float yfrac) {
        return s0;
    }

    public float interpolate(float s00, float s01, float s10, float s11, float xfrac, float yfrac) {
        return s00;
    }

    public float interpolate(float s__, float s_0, float s_1, float s_2, float s0_, float s00, float s01, float s02, float s1_, float s10, float s11, float s12, float s2_, float s20, float s21, float s22, float xfrac, float yfrac) {
        return s00;
    }

    public double interpolateH(double[] samples, float xfrac) {
        return samples[0];
    }

    public double interpolateV(double[] samples, float yfrac) {
        return samples[0];
    }

    public double interpolate(double[][] samples, float xfrac, float yfrac) {
        return samples[0][0];
    }

    public double interpolateH(double s0, double s1, float xfrac) {
        return s0;
    }

    public double interpolateV(double s0, double s1, float yfrac) {
        return s0;
    }

    public double interpolate(double s00, double s01, double s10, double s11, float xfrac, float yfrac) {
        return s00;
    }

    public double interpolate(double s__, double s_0, double s_1, double s_2, double s0_, double s00, double s01, double s02, double s1_, double s10, double s11, double s12, double s2_, double s20, double s21, double s22, float xfrac, float yfrac) {
        return s00;
    }
}

