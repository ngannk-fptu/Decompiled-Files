/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.JaiI18N;
import com.sun.media.jai.util.MathJAI;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;
import javax.media.jai.operator.DFTDescriptor;

public class FFT {
    public static final int SCALING_NONE = DFTDescriptor.SCALING_NONE.getValue();
    public static final int SCALING_UNITARY = DFTDescriptor.SCALING_UNITARY.getValue();
    public static final int SCALING_DIMENSIONS = DFTDescriptor.SCALING_DIMENSIONS.getValue();
    protected boolean lengthIsSet = false;
    protected int exponentSign;
    protected int scaleType;
    protected int length;
    private int nbits;
    private int[] index;
    private double scaleFactor;
    private double[] wr;
    private double[] wi;
    private double[] wrFCT;
    private double[] wiFCT;
    protected double[] real;
    protected double[] imag;

    public FFT(boolean negatedExponent, Integer scaleType, int length) {
        this.exponentSign = negatedExponent ? -1 : 1;
        this.scaleType = scaleType;
        this.setLength(length);
    }

    public void setLength(int length) {
        if (this.lengthIsSet && length == this.length) {
            return;
        }
        if (!MathJAI.isPositivePowerOf2(length)) {
            throw new RuntimeException(JaiI18N.getString("FFT0"));
        }
        this.length = length;
        if (this.scaleType == SCALING_NONE) {
            this.scaleFactor = 1.0;
        } else if (this.scaleType == SCALING_UNITARY) {
            this.scaleFactor = 1.0 / Math.sqrt(length);
        } else if (this.scaleType == SCALING_DIMENSIONS) {
            this.scaleFactor = 1.0 / (double)length;
        } else {
            throw new RuntimeException(JaiI18N.getString("FFT1"));
        }
        this.nbits = 0;
        for (int power = 1; power < length; power <<= 1) {
            ++this.nbits;
        }
        this.initBitReversalLUT();
        this.calculateCoefficientLUTs();
        if (!this.lengthIsSet || length > this.real.length) {
            this.real = new double[length];
            this.imag = new double[length];
        }
        this.lengthIsSet = true;
    }

    private void initBitReversalLUT() {
        this.index = new int[this.length];
        for (int i = 0; i < this.length; ++i) {
            int l = i;
            int power = this.length >> 1;
            int irev = 0;
            for (int k = 0; k < this.nbits; ++k) {
                int j = l & 1;
                if (j != 0) {
                    irev += power;
                }
                l >>= 1;
                power >>= 1;
                this.index[i] = irev;
            }
        }
    }

    private void calculateCoefficientLUTs() {
        this.wr = new double[this.nbits];
        this.wi = new double[this.nbits];
        int inode = 1;
        double cons = (double)this.exponentSign * Math.PI;
        for (int bit = 0; bit < this.nbits; ++bit) {
            this.wr[bit] = Math.cos(cons / (double)inode);
            this.wi[bit] = Math.sin(cons / (double)inode);
            inode *= 2;
        }
    }

    private void calculateFCTLUTs() {
        this.wrFCT = new double[this.length];
        this.wiFCT = new double[this.length];
        for (int i = 0; i < this.length; ++i) {
            double factor = i == 0 ? Math.sqrt(1.0 / (double)this.length) : Math.sqrt(2.0 / (double)this.length);
            double freq = Math.PI * (double)i / (2.0 * (double)this.length);
            this.wrFCT[i] = factor * Math.cos(freq);
            this.wiFCT[i] = factor * Math.sin(freq);
        }
    }

    public void setData(int dataType, Object realArg, int offsetReal, int strideReal, Object imagArg, int offsetImag, int strideImag, int count) {
        switch (dataType) {
            case 4: {
                float[] realFloat = (float[])realArg;
                if (imagArg != null) {
                    float[] imagFloat = (float[])imagArg;
                    if (offsetReal == offsetImag && strideReal == strideImag) {
                        for (int i = 0; i < count; ++i) {
                            this.real[i] = realFloat[offsetReal];
                            this.imag[i] = imagFloat[offsetReal];
                            offsetReal += strideReal;
                        }
                    } else {
                        for (int i = 0; i < count; ++i) {
                            this.real[i] = realFloat[offsetReal];
                            this.imag[i] = imagFloat[offsetImag];
                            offsetReal += strideReal;
                            offsetImag += strideImag;
                        }
                    }
                } else {
                    for (int i = 0; i < count; ++i) {
                        this.real[i] = realFloat[offsetReal];
                        offsetReal += strideReal;
                    }
                }
                break;
            }
            case 5: {
                double[] realDouble = (double[])realArg;
                if (strideReal == 1 && strideImag == 1) {
                    System.arraycopy(realDouble, offsetReal, this.real, 0, count);
                    if (imagArg == null) break;
                    System.arraycopy((double[])imagArg, offsetImag, this.imag, 0, count);
                    break;
                }
                if (imagArg != null) {
                    double[] imagDouble = (double[])imagArg;
                    if (offsetReal == offsetImag && strideReal == strideImag) {
                        for (int i = 0; i < count; ++i) {
                            this.real[i] = realDouble[offsetReal];
                            this.imag[i] = imagDouble[offsetReal];
                            offsetReal += strideReal;
                        }
                    } else {
                        for (int i = 0; i < count; ++i) {
                            this.real[i] = realDouble[offsetReal];
                            this.imag[i] = imagDouble[offsetImag];
                            offsetReal += strideReal;
                            offsetImag += strideImag;
                        }
                    }
                } else {
                    for (int i = 0; i < count; ++i) {
                        this.real[i] = realDouble[offsetReal];
                        offsetReal += strideReal;
                    }
                }
                break;
            }
            default: {
                throw new RuntimeException(dataType + JaiI18N.getString("FFT2"));
            }
        }
        if (count < this.length) {
            Arrays.fill(this.real, count, this.length, 0.0);
            if (imagArg != null) {
                Arrays.fill(this.imag, count, this.length, 0.0);
            }
        }
        if (imagArg == null) {
            Arrays.fill(this.imag, 0, this.length, 0.0);
        }
    }

    public void getData(int dataType, Object realArg, int offsetReal, int strideReal, Object imagArg, int offsetImag, int strideImag) {
        switch (dataType) {
            case 4: {
                float[] realFloat = (float[])realArg;
                if (imagArg != null) {
                    float[] imagFloat = (float[])imagArg;
                    if (offsetReal == offsetImag && strideReal == strideImag) {
                        for (int i = 0; i < this.length; ++i) {
                            int idx = this.index[i];
                            realFloat[offsetReal] = (float)this.real[idx];
                            imagFloat[offsetReal] = (float)this.imag[idx];
                            offsetReal += strideReal;
                        }
                    } else {
                        for (int i = 0; i < this.length; ++i) {
                            int idx = this.index[i];
                            realFloat[offsetReal] = (float)this.real[idx];
                            imagFloat[offsetImag] = (float)this.imag[idx];
                            offsetReal += strideReal;
                            offsetImag += strideImag;
                        }
                    }
                } else {
                    for (int i = 0; i < this.length; ++i) {
                        realFloat[offsetReal] = (float)this.real[this.index[i]];
                        offsetReal += strideReal;
                    }
                }
                break;
            }
            case 5: {
                double[] realDouble = (double[])realArg;
                if (imagArg != null) {
                    double[] imagDouble = (double[])imagArg;
                    if (offsetReal == offsetImag && strideReal == strideImag) {
                        for (int i = 0; i < this.length; ++i) {
                            int idx = this.index[i];
                            realDouble[offsetReal] = this.real[idx];
                            imagDouble[offsetReal] = this.imag[idx];
                            offsetReal += strideReal;
                        }
                    } else {
                        for (int i = 0; i < this.length; ++i) {
                            int idx = this.index[i];
                            realDouble[offsetReal] = this.real[idx];
                            imagDouble[offsetImag] = this.imag[idx];
                            offsetReal += strideReal;
                            offsetImag += strideImag;
                        }
                    }
                } else {
                    for (int i = 0; i < this.length; ++i) {
                        realDouble[offsetReal] = this.real[this.index[i]];
                        offsetReal += strideReal;
                    }
                }
                break;
            }
            default: {
                throw new RuntimeException(dataType + JaiI18N.getString("FFT2"));
            }
        }
    }

    public void setFCTData(int dataType, Object data, int offset, int stride, int count) {
        switch (dataType) {
            case 4: {
                int i;
                float[] realFloat = (float[])data;
                for (i = 0; i < count; ++i) {
                    this.imag[i] = realFloat[offset];
                    offset += stride;
                }
                for (i = count; i < this.length; ++i) {
                    this.imag[i] = 0.0;
                }
                int k = this.length - 1;
                int j = 0;
                for (int i2 = 0; i2 < k; ++i2) {
                    this.real[i2] = this.imag[j++];
                    this.real[k--] = this.imag[j++];
                }
                break;
            }
            case 5: {
                int i;
                double[] realDouble = (double[])data;
                for (i = 0; i < count; ++i) {
                    this.imag[i] = realDouble[offset];
                    offset += stride;
                }
                for (i = count; i < this.length; ++i) {
                    this.imag[i] = 0.0;
                }
                int k = this.length - 1;
                int j = 0;
                for (int i3 = 0; i3 < k; ++i3) {
                    this.real[i3] = this.imag[j++];
                    this.real[k--] = this.imag[j++];
                }
                break;
            }
            default: {
                throw new RuntimeException(dataType + JaiI18N.getString("FFT2"));
            }
        }
        Arrays.fill(this.imag, 0, this.length, 0.0);
    }

    public void getFCTData(int dataType, Object data, int offset, int stride) {
        if (this.wrFCT == null || this.wrFCT.length != this.length) {
            this.calculateFCTLUTs();
        }
        switch (dataType) {
            case 4: {
                float[] realFloat = (float[])data;
                for (int i = 0; i < this.length; ++i) {
                    int idx = this.index[i];
                    realFloat[offset] = (float)(this.wrFCT[i] * this.real[idx] + this.wiFCT[i] * this.imag[idx]);
                    offset += stride;
                }
                break;
            }
            case 5: {
                double[] realDouble = (double[])data;
                for (int i = 0; i < this.length; ++i) {
                    int idx = this.index[i];
                    realDouble[offset] = this.wrFCT[i] * this.real[idx] + this.wiFCT[i] * this.imag[idx];
                    offset += stride;
                }
                break;
            }
            default: {
                throw new RuntimeException(dataType + JaiI18N.getString("FFT2"));
            }
        }
    }

    public void setIFCTData(int dataType, Object data, int offset, int stride, int count) {
        if (this.wrFCT == null || this.wrFCT.length != this.length) {
            this.calculateFCTLUTs();
        }
        switch (dataType) {
            case 4: {
                float[] realFloat = (float[])data;
                for (int i = 0; i < count; ++i) {
                    float r = realFloat[offset];
                    this.real[i] = (double)r * this.wrFCT[i];
                    this.imag[i] = (double)r * this.wiFCT[i];
                    offset += stride;
                }
                break;
            }
            case 5: {
                double[] realDouble = (double[])data;
                for (int i = 0; i < count; ++i) {
                    double r = realDouble[offset];
                    this.real[i] = r * this.wrFCT[i];
                    this.imag[i] = r * this.wiFCT[i];
                    offset += stride;
                }
                break;
            }
            default: {
                throw new RuntimeException(dataType + JaiI18N.getString("FFT2"));
            }
        }
        if (count < this.length) {
            Arrays.fill(this.real, count, this.length, 0.0);
            Arrays.fill(this.imag, count, this.length, 0.0);
        }
    }

    public void getIFCTData(int dataType, Object data, int offset, int stride) {
        switch (dataType) {
            case 4: {
                float[] realFloat = (float[])data;
                int k = this.length - 1;
                for (int i = 0; i < k; ++i) {
                    realFloat[offset] = (float)this.real[this.index[i]];
                    realFloat[offset += stride] = (float)this.real[this.index[k--]];
                    offset += stride;
                }
                break;
            }
            case 5: {
                double[] realDouble = (double[])data;
                int k = this.length - 1;
                for (int i = 0; i < k; ++i) {
                    realDouble[offset] = (float)this.real[this.index[i]];
                    realDouble[offset += stride] = (float)this.real[this.index[k--]];
                    offset += stride;
                }
                break;
            }
            default: {
                throw new RuntimeException(dataType + JaiI18N.getString("FFT2"));
            }
        }
    }

    public void transform() {
        int i;
        Integer i18n = new Integer(this.length);
        NumberFormat numberFormatter = NumberFormat.getNumberInstance(Locale.getDefault());
        if (this.real.length < this.length || this.imag.length < this.length) {
            throw new RuntimeException(numberFormatter.format(i18n) + JaiI18N.getString("FFT3"));
        }
        int inode = 1;
        for (int l = 0; l < this.nbits; ++l) {
            double cosp = 1.0;
            double sinp = 0.0;
            int ipair = 2 * inode;
            for (int k = 0; k < inode; ++k) {
                for (i = k; i < this.length; i += ipair) {
                    int j = i + inode;
                    int iIndex = this.index[i];
                    int jIndex = this.index[j];
                    double rtemp = this.real[jIndex] * cosp - this.imag[jIndex] * sinp;
                    double itemp = this.imag[jIndex] * cosp + this.real[jIndex] * sinp;
                    this.real[jIndex] = this.real[iIndex] - rtemp;
                    this.imag[jIndex] = this.imag[iIndex] - itemp;
                    this.real[iIndex] = this.real[iIndex] + rtemp;
                    this.imag[iIndex] = this.imag[iIndex] + itemp;
                }
                double costmp = cosp;
                cosp = cosp * this.wr[l] - sinp * this.wi[l];
                sinp = costmp * this.wi[l] + sinp * this.wr[l];
            }
            inode *= 2;
        }
        if (this.scaleFactor != 1.0) {
            for (i = 0; i < this.length; ++i) {
                this.real[i] = this.real[i] * this.scaleFactor;
                this.imag[i] = this.imag[i] * this.scaleFactor;
            }
        }
    }
}

