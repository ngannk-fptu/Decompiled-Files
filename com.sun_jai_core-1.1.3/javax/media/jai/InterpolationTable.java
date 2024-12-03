/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import javax.media.jai.Interpolation;
import javax.media.jai.JaiI18N;

public class InterpolationTable
extends Interpolation {
    protected int precisionBits;
    private int round;
    private int numSubsamplesH;
    private int numSubsamplesV;
    protected double[] dataHd;
    protected double[] dataVd;
    protected float[] dataHf;
    protected float[] dataVf;
    protected int[] dataHi;
    protected int[] dataVi;

    public InterpolationTable(int keyX, int keyY, int width, int height, int subsampleBitsH, int subsampleBitsV, int precisionBits, int[] dataH, int[] dataV) {
        int i;
        this.leftPadding = keyX;
        this.topPadding = keyY;
        this.width = width;
        this.rightPadding = width - keyX - 1;
        this.precisionBits = precisionBits;
        if (precisionBits > 0) {
            this.round = 1 << precisionBits - 1;
        }
        this.subsampleBitsH = subsampleBitsH;
        this.numSubsamplesH = 1 << subsampleBitsH;
        int entriesH = width * this.numSubsamplesH;
        if (dataH.length != entriesH) {
            throw new IllegalArgumentException(JaiI18N.getString("InterpolationTable0"));
        }
        double prec = 1 << precisionBits;
        this.dataHi = (int[])dataH.clone();
        this.dataHf = new float[entriesH];
        this.dataHd = new double[entriesH];
        for (i = 0; i < entriesH; ++i) {
            double d = (double)this.dataHi[i] / prec;
            this.dataHf[i] = (float)d;
            this.dataHd[i] = d;
        }
        if (dataV != null) {
            this.height = height;
            this.subsampleBitsV = subsampleBitsV;
            this.numSubsamplesV = 1 << subsampleBitsV;
            int entriesV = height * this.numSubsamplesV;
            if (dataV.length != entriesV) {
                throw new IllegalArgumentException(JaiI18N.getString("InterpolationTable1"));
            }
            this.dataVi = (int[])dataV.clone();
            this.dataVf = new float[entriesV];
            this.dataVd = new double[entriesV];
            for (i = 0; i < entriesV; ++i) {
                double d = (double)this.dataVi[i] / prec;
                this.dataVf[i] = (float)d;
                this.dataVd[i] = d;
            }
        } else {
            this.height = width;
            this.subsampleBitsV = subsampleBitsH;
            this.numSubsamplesV = this.numSubsamplesH;
            this.dataVf = this.dataHf;
            this.dataVi = this.dataHi;
            this.dataVd = this.dataHd;
        }
        this.bottomPadding = this.height - keyY - 1;
    }

    public InterpolationTable(int key, int width, int subsampleBits, int precisionBits, int[] data) {
        this(key, key, width, width, subsampleBits, subsampleBits, precisionBits, data, (int[])null);
    }

    public InterpolationTable(int keyX, int keyY, int width, int height, int subsampleBitsH, int subsampleBitsV, int precisionBits, float[] dataH, float[] dataV) {
        int i;
        this.leftPadding = keyX;
        this.topPadding = keyY;
        this.width = width;
        this.rightPadding = width - keyX - 1;
        this.precisionBits = precisionBits;
        if (precisionBits > 0) {
            this.round = 1 << precisionBits - 1;
        }
        this.subsampleBitsH = subsampleBitsH;
        this.numSubsamplesH = 1 << subsampleBitsH;
        int entriesH = width * this.numSubsamplesH;
        if (dataH.length != entriesH) {
            throw new IllegalArgumentException(JaiI18N.getString("InterpolationTable0"));
        }
        float prec = 1 << precisionBits;
        this.dataHf = (float[])dataH.clone();
        this.dataHi = new int[entriesH];
        this.dataHd = new double[entriesH];
        for (i = 0; i < entriesH; ++i) {
            float f = this.dataHf[i];
            this.dataHi[i] = Math.round(f * prec);
            this.dataHd[i] = f;
        }
        if (dataV != null) {
            this.height = height;
            this.subsampleBitsV = subsampleBitsV;
            this.numSubsamplesV = 1 << subsampleBitsV;
            int entriesV = height * this.numSubsamplesV;
            if (dataV.length != entriesV) {
                throw new IllegalArgumentException(JaiI18N.getString("InterpolationTable1"));
            }
            this.dataVf = (float[])dataV.clone();
            this.dataVi = new int[entriesV];
            this.dataVd = new double[entriesV];
            for (i = 0; i < entriesV; ++i) {
                float f = this.dataVf[i];
                this.dataVi[i] = Math.round(f * prec);
                this.dataVd[i] = f;
            }
        } else {
            this.height = width;
            this.subsampleBitsV = subsampleBitsH;
            this.numSubsamplesV = this.numSubsamplesH;
            this.dataVf = this.dataHf;
            this.dataVi = this.dataHi;
            this.dataVd = this.dataHd;
        }
        this.bottomPadding = this.height - keyY - 1;
    }

    public InterpolationTable(int key, int width, int subsampleBits, int precisionBits, float[] data) {
        this(key, key, width, width, subsampleBits, subsampleBits, precisionBits, data, (float[])null);
    }

    public InterpolationTable(int keyX, int keyY, int width, int height, int subsampleBitsH, int subsampleBitsV, int precisionBits, double[] dataH, double[] dataV) {
        int i;
        this.leftPadding = keyX;
        this.topPadding = keyY;
        this.width = width;
        this.rightPadding = width - keyX - 1;
        this.precisionBits = precisionBits;
        if (precisionBits > 0) {
            this.round = 1 << precisionBits - 1;
        }
        this.subsampleBitsH = subsampleBitsH;
        this.numSubsamplesH = 1 << subsampleBitsH;
        int entriesH = width * this.numSubsamplesH;
        if (dataH.length != entriesH) {
            throw new IllegalArgumentException(JaiI18N.getString("InterpolationTable0"));
        }
        double prec = 1 << precisionBits;
        this.dataHd = (double[])dataH.clone();
        this.dataHi = new int[entriesH];
        this.dataHf = new float[entriesH];
        for (i = 0; i < entriesH; ++i) {
            double d = this.dataHd[i];
            this.dataHi[i] = (int)Math.round(d * prec);
            this.dataHf[i] = (float)d;
        }
        if (dataV != null) {
            this.height = height;
            this.subsampleBitsV = subsampleBitsV;
            this.numSubsamplesV = 1 << subsampleBitsV;
            int entriesV = height * this.numSubsamplesV;
            if (dataV.length != entriesV) {
                throw new IllegalArgumentException(JaiI18N.getString("InterpolationTable1"));
            }
            this.dataVd = (double[])dataV.clone();
            this.dataVi = new int[entriesV];
            this.dataVf = new float[entriesV];
            for (i = 0; i < entriesV; ++i) {
                double d = this.dataVd[i];
                this.dataVi[i] = (int)Math.round(d * prec);
                this.dataVf[i] = (float)d;
            }
        } else {
            this.height = width;
            this.subsampleBitsV = subsampleBitsH;
            this.numSubsamplesV = this.numSubsamplesH;
            this.dataVd = this.dataHd;
            this.dataVf = this.dataHf;
            this.dataVi = this.dataHi;
        }
        this.bottomPadding = this.height - keyY - 1;
    }

    public InterpolationTable(int key, int width, int subsampleBits, int precisionBits, double[] data) {
        this(key, key, width, width, subsampleBits, subsampleBits, precisionBits, data, null);
    }

    public int getPrecisionBits() {
        return this.precisionBits;
    }

    public int[] getHorizontalTableData() {
        return this.dataHi;
    }

    public int[] getVerticalTableData() {
        return this.dataVi;
    }

    public float[] getHorizontalTableDataFloat() {
        return this.dataHf;
    }

    public float[] getVerticalTableDataFloat() {
        return this.dataVf;
    }

    public double[] getHorizontalTableDataDouble() {
        return this.dataHd;
    }

    public double[] getVerticalTableDataDouble() {
        return this.dataVd;
    }

    public int interpolateH(int[] samples, int xfrac) {
        int sum = 0;
        int offset = this.width * xfrac;
        for (int i = 0; i < this.width; ++i) {
            sum += this.dataHi[offset + i] * samples[i];
        }
        return sum + this.round >> this.precisionBits;
    }

    public int interpolateV(int[] samples, int yfrac) {
        int sum = 0;
        int offset = this.width * yfrac;
        for (int i = 0; i < this.width; ++i) {
            sum += this.dataVi[offset + i] * samples[i];
        }
        return sum + this.round >> this.precisionBits;
    }

    public int interpolateH(int s0, int s1, int xfrac) {
        int offset = 2 * xfrac;
        int sum = this.dataHi[offset] * s0;
        return (sum += this.dataHi[offset + 1] * s1) + this.round >> this.precisionBits;
    }

    public int interpolateH(int s_, int s0, int s1, int s2, int xfrac) {
        int offset = 4 * xfrac;
        int sum = this.dataHi[offset] * s_;
        sum += this.dataHi[offset + 1] * s0;
        sum += this.dataHi[offset + 2] * s1;
        return (sum += this.dataHi[offset + 3] * s2) + this.round >> this.precisionBits;
    }

    public int interpolateV(int s0, int s1, int yfrac) {
        int offset = 2 * yfrac;
        int sum = this.dataVi[offset] * s0;
        return (sum += this.dataVi[offset + 1] * s1) + this.round >> this.precisionBits;
    }

    public int interpolateV(int s_, int s0, int s1, int s2, int yfrac) {
        int offset = 4 * yfrac;
        int sum = this.dataVi[offset] * s_;
        sum += this.dataVi[offset + 1] * s0;
        sum += this.dataVi[offset + 2] * s1;
        return (sum += this.dataVi[offset + 3] * s2) + this.round >> this.precisionBits;
    }

    public int interpolate(int s00, int s01, int s10, int s11, int xfrac, int yfrac) {
        int offsetX = 2 * xfrac;
        int sum0 = this.dataHi[offsetX] * s00 + this.dataHi[offsetX + 1] * s01;
        int sum1 = this.dataHi[offsetX] * s10 + this.dataHi[offsetX + 1] * s11;
        sum0 = sum0 + this.round >> this.precisionBits;
        sum1 = sum1 + this.round >> this.precisionBits;
        int offsetY = 2 * yfrac;
        int sum = this.dataVi[offsetY] * sum0 + this.dataVi[offsetY + 1] * sum1;
        return sum + this.round >> this.precisionBits;
    }

    public int interpolate(int s__, int s_0, int s_1, int s_2, int s0_, int s00, int s01, int s02, int s1_, int s10, int s11, int s12, int s2_, int s20, int s21, int s22, int xfrac, int yfrac) {
        int offsetX = 4 * xfrac;
        int offsetX1 = offsetX + 1;
        int offsetX2 = offsetX + 2;
        int offsetX3 = offsetX + 3;
        long sum_ = (long)this.dataHi[offsetX] * (long)s__;
        sum_ += (long)this.dataHi[offsetX1] * (long)s_0;
        sum_ += (long)this.dataHi[offsetX2] * (long)s_1;
        sum_ += (long)this.dataHi[offsetX3] * (long)s_2;
        long sum0 = (long)this.dataHi[offsetX] * (long)s0_;
        sum0 += (long)this.dataHi[offsetX1] * (long)s00;
        sum0 += (long)this.dataHi[offsetX2] * (long)s01;
        sum0 += (long)this.dataHi[offsetX3] * (long)s02;
        long sum1 = (long)this.dataHi[offsetX] * (long)s1_;
        sum1 += (long)this.dataHi[offsetX1] * (long)s10;
        sum1 += (long)this.dataHi[offsetX2] * (long)s11;
        sum1 += (long)this.dataHi[offsetX3] * (long)s12;
        long sum2 = (long)this.dataHi[offsetX] * (long)s2_;
        sum2 += (long)this.dataHi[offsetX1] * (long)s20;
        sum2 += (long)this.dataHi[offsetX2] * (long)s21;
        sum2 += (long)this.dataHi[offsetX3] * (long)s22;
        sum_ = sum_ + (long)this.round >> this.precisionBits;
        sum0 = sum0 + (long)this.round >> this.precisionBits;
        sum1 = sum1 + (long)this.round >> this.precisionBits;
        sum2 = sum2 + (long)this.round >> this.precisionBits;
        int offsetY = 4 * yfrac;
        long sum = (long)this.dataVi[offsetY] * sum_;
        sum += (long)this.dataVi[offsetY + 1] * sum0;
        sum += (long)this.dataVi[offsetY + 2] * sum1;
        return (int)((sum += (long)this.dataVi[offsetY + 3] * sum2) + (long)this.round >> this.precisionBits);
    }

    public int interpolateF(int s__, int s_0, int s_1, int s_2, int s0_, int s00, int s01, int s02, int s1_, int s10, int s11, int s12, int s2_, int s20, int s21, int s22, int xfrac, int yfrac) {
        int offsetX = 4 * xfrac;
        float sum_ = this.dataHf[offsetX] * (float)s__;
        sum_ += this.dataHf[offsetX + 1] * (float)s_0;
        sum_ += this.dataHf[offsetX + 2] * (float)s_1;
        sum_ += this.dataHf[offsetX + 3] * (float)s_2;
        float sum0 = this.dataHf[offsetX] * (float)s0_;
        sum0 += this.dataHf[offsetX + 1] * (float)s00;
        sum0 += this.dataHf[offsetX + 2] * (float)s01;
        sum0 += this.dataHf[offsetX + 3] * (float)s02;
        float sum1 = this.dataHf[offsetX] * (float)s1_;
        sum1 += this.dataHf[offsetX + 1] * (float)s10;
        sum1 += this.dataHf[offsetX + 2] * (float)s11;
        sum1 += this.dataHf[offsetX + 3] * (float)s12;
        float sum2 = this.dataHf[offsetX] * (float)s2_;
        sum2 += this.dataHf[offsetX + 1] * (float)s20;
        sum2 += this.dataHf[offsetX + 2] * (float)s21;
        int offsetY = 4 * yfrac;
        float sum = this.dataVf[offsetY] * sum_;
        sum += this.dataVf[offsetY + 1] * sum0;
        sum += this.dataVf[offsetY + 2] * sum1;
        int isum = (int)(sum += this.dataVf[offsetY + 3] * (sum2 += this.dataHf[offsetX + 3] * (float)s22));
        return isum;
    }

    public float interpolateH(float[] samples, float xfrac) {
        float sum = 0.0f;
        int ifrac = (int)(xfrac * (float)this.numSubsamplesH);
        int offset = this.width * ifrac;
        for (int i = 0; i < this.width; ++i) {
            sum += this.dataHf[offset + i] * samples[i];
        }
        return sum;
    }

    public float interpolateV(float[] samples, float yfrac) {
        float sum = 0.0f;
        int ifrac = (int)(yfrac * (float)this.numSubsamplesV);
        int offset = this.width * ifrac;
        for (int i = 0; i < this.width; ++i) {
            sum += this.dataVf[offset + i] * samples[i];
        }
        return sum;
    }

    public float interpolateH(float s0, float s1, float xfrac) {
        float sum = 0.0f;
        int ifrac = (int)(xfrac * (float)this.numSubsamplesH);
        int offset = 2 * ifrac;
        sum = this.dataHf[offset] * s0 + this.dataHf[offset + 1] * s1;
        return sum;
    }

    public float interpolateH(float s_, float s0, float s1, float s2, float xfrac) {
        int ifrac = (int)(xfrac * (float)this.numSubsamplesH);
        int offset = 4 * ifrac;
        float sum = this.dataHf[offset] * s_;
        sum += this.dataHf[offset + 1] * s0;
        sum += this.dataHf[offset + 2] * s1;
        return sum += this.dataHf[offset + 3] * s2;
    }

    public float interpolateV(float s0, float s1, float yfrac) {
        int ifrac = (int)(yfrac * (float)this.numSubsamplesV);
        int offset = 2 * ifrac;
        float sum = this.dataVf[offset] * s0;
        return sum += this.dataVf[offset + 1] * s1;
    }

    public float interpolateV(float s_, float s0, float s1, float s2, float yfrac) {
        int ifrac = (int)(yfrac * (float)this.numSubsamplesV);
        int offset = 4 * ifrac;
        float sum = this.dataVf[offset] * s_;
        sum += this.dataVf[offset + 1] * s0;
        sum += this.dataVf[offset + 2] * s1;
        return sum += this.dataVf[offset + 3] * s2;
    }

    public float interpolate(float s00, float s01, float s10, float s11, float xfrac, float yfrac) {
        int ifrac = (int)(xfrac * (float)this.numSubsamplesH);
        int offsetX = 2 * ifrac;
        float sum0 = this.dataHf[offsetX] * s00 + this.dataHf[offsetX + 1] * s01;
        float sum1 = this.dataHf[offsetX] * s10 + this.dataHf[offsetX + 1] * s11;
        ifrac = (int)(yfrac * (float)this.numSubsamplesV);
        int offsetY = 2 * ifrac;
        float sum = this.dataVf[offsetY] * sum0 + this.dataVf[offsetY + 1] * sum1;
        return sum;
    }

    public float interpolate(float s__, float s_0, float s_1, float s_2, float s0_, float s00, float s01, float s02, float s1_, float s10, float s11, float s12, float s2_, float s20, float s21, float s22, float xfrac, float yfrac) {
        int ifrac = (int)(xfrac * (float)this.numSubsamplesH);
        int offsetX = 4 * ifrac;
        int offsetX1 = offsetX + 1;
        int offsetX2 = offsetX + 2;
        int offsetX3 = offsetX + 3;
        float sum_ = this.dataHf[offsetX] * s__;
        sum_ += this.dataHf[offsetX1] * s_0;
        sum_ += this.dataHf[offsetX2] * s_1;
        sum_ += this.dataHf[offsetX3] * s_2;
        float sum0 = this.dataHf[offsetX] * s0_;
        sum0 += this.dataHf[offsetX1] * s00;
        sum0 += this.dataHf[offsetX2] * s01;
        sum0 += this.dataHf[offsetX3] * s02;
        float sum1 = this.dataHf[offsetX] * s1_;
        sum1 += this.dataHf[offsetX1] * s10;
        sum1 += this.dataHf[offsetX2] * s11;
        sum1 += this.dataHf[offsetX3] * s12;
        float sum2 = this.dataHf[offsetX] * s2_;
        sum2 += this.dataHf[offsetX1] * s20;
        sum2 += this.dataHf[offsetX2] * s21;
        sum2 += this.dataHf[offsetX3] * s22;
        ifrac = (int)(yfrac * (float)this.numSubsamplesV);
        int offsetY = 4 * ifrac;
        float sum = this.dataVf[offsetY] * sum_;
        sum += this.dataVf[offsetY + 1] * sum0;
        sum += this.dataVf[offsetY + 2] * sum1;
        return sum += this.dataVf[offsetY + 3] * sum2;
    }

    public double interpolateH(double[] samples, float xfrac) {
        double sum = 0.0;
        int ifrac = (int)(xfrac * (float)this.numSubsamplesH);
        int offset = this.width * ifrac;
        for (int i = 0; i < this.width; ++i) {
            sum += this.dataHd[offset + i] * samples[i];
        }
        return sum;
    }

    public double interpolateV(double[] samples, float yfrac) {
        double sum = 0.0;
        int ifrac = (int)(yfrac * (float)this.numSubsamplesV);
        int offset = this.width * ifrac;
        for (int i = 0; i < this.width; ++i) {
            sum += this.dataVd[offset + i] * samples[i];
        }
        return sum;
    }

    public double interpolateH(double s0, double s1, float xfrac) {
        double sum = 0.0;
        int ifrac = (int)(xfrac * (float)this.numSubsamplesH);
        int offset = 2 * ifrac;
        sum = this.dataHd[offset] * s0 + this.dataHd[offset + 1] * s1;
        return sum;
    }

    public double interpolateH(double s_, double s0, double s1, double s2, float xfrac) {
        int ifrac = (int)(xfrac * (float)this.numSubsamplesH);
        int offset = 4 * ifrac;
        double sum = this.dataHd[offset] * s_;
        sum += this.dataHd[offset + 1] * s0;
        sum += this.dataHd[offset + 2] * s1;
        return sum += this.dataHd[offset + 3] * s2;
    }

    public double interpolateV(double s0, double s1, float yfrac) {
        int ifrac = (int)(yfrac * (float)this.numSubsamplesV);
        int offset = 2 * ifrac;
        double sum = this.dataVd[offset] * s0;
        return sum += this.dataVd[offset + 1] * s1;
    }

    public double interpolateV(double s_, double s0, double s1, double s2, float yfrac) {
        int ifrac = (int)(yfrac * (float)this.numSubsamplesV);
        int offset = 4 * ifrac;
        double sum = this.dataVd[offset] * s_;
        sum += this.dataVd[offset + 1] * s0;
        sum += this.dataVd[offset + 2] * s1;
        return sum += this.dataVd[offset + 3] * s2;
    }

    public double interpolate(double s00, double s01, double s10, double s11, float xfrac, float yfrac) {
        int ifrac = (int)(xfrac * (float)this.numSubsamplesH);
        int offsetX = 2 * ifrac;
        double sum0 = this.dataHd[offsetX] * s00 + this.dataHd[offsetX + 1] * s01;
        double sum1 = this.dataHd[offsetX] * s10 + this.dataHd[offsetX + 1] * s11;
        ifrac = (int)(yfrac * (float)this.numSubsamplesV);
        int offsetY = 2 * ifrac;
        double sum = this.dataVd[offsetY] * sum0 + this.dataVd[offsetY + 1] * sum1;
        return sum;
    }

    public double interpolate(double s__, double s_0, double s_1, double s_2, double s0_, double s00, double s01, double s02, double s1_, double s10, double s11, double s12, double s2_, double s20, double s21, double s22, float xfrac, float yfrac) {
        int ifrac = (int)(xfrac * (float)this.numSubsamplesH);
        int offsetX = 4 * ifrac;
        int offsetX1 = offsetX + 1;
        int offsetX2 = offsetX + 2;
        int offsetX3 = offsetX + 3;
        double sum_ = this.dataHd[offsetX] * s__;
        sum_ += this.dataHd[offsetX1] * s_0;
        sum_ += this.dataHd[offsetX2] * s_1;
        sum_ += this.dataHd[offsetX3] * s_2;
        double sum0 = this.dataHd[offsetX] * s0_;
        sum0 += this.dataHd[offsetX1] * s00;
        sum0 += this.dataHd[offsetX2] * s01;
        sum0 += this.dataHd[offsetX3] * s02;
        double sum1 = this.dataHd[offsetX] * s1_;
        sum1 += this.dataHd[offsetX1] * s10;
        sum1 += this.dataHd[offsetX2] * s11;
        sum1 += this.dataHd[offsetX3] * s12;
        double sum2 = this.dataHd[offsetX] * s2_;
        sum2 += this.dataHd[offsetX1] * s20;
        sum2 += this.dataHd[offsetX2] * s21;
        sum2 += this.dataHd[offsetX3] * s22;
        ifrac = (int)(yfrac * (float)this.numSubsamplesV);
        int offsetY = 4 * ifrac;
        double sum = this.dataVd[offsetY] * sum_;
        sum += this.dataVd[offsetY + 1] * sum0;
        sum += this.dataVd[offsetY + 2] * sum1;
        return sum += this.dataVd[offsetY + 3] * sum2;
    }
}

