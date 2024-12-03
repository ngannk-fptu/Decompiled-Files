/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.medialib.mlib.Image
 */
package com.sun.media.jai.mlib;

import com.sun.media.jai.mlib.JaiI18N;
import com.sun.media.jai.opimage.FCT;
import com.sun.media.jai.util.MathJAI;
import com.sun.medialib.mlib.Image;
import java.util.Arrays;

public class FCTmediaLib
extends FCT {
    private int length;
    private boolean lengthIsSet = false;
    private double[] wr;
    private double[] wi;
    protected double[] real;
    protected double[] imag;

    public FCTmediaLib(boolean isForwardTransform, int length) {
        this.isForwardTransform = isForwardTransform;
        this.setLength(length);
    }

    public void setLength(int length) {
        if (this.lengthIsSet && length == this.length) {
            return;
        }
        if (!MathJAI.isPositivePowerOf2(length)) {
            throw new RuntimeException(JaiI18N.getString("FCTmediaLib0"));
        }
        this.length = length;
        if (this.real == null || length != this.real.length) {
            this.real = new double[length];
            this.imag = new double[length];
        }
        this.calculateFCTLUTs();
        this.lengthIsSet = true;
    }

    private void calculateFCTLUTs() {
        this.wr = new double[this.length];
        this.wi = new double[this.length];
        for (int i = 0; i < this.length; ++i) {
            double factor = i == 0 ? Math.sqrt(1.0 / (double)this.length) : Math.sqrt(2.0 / (double)this.length);
            double freq = Math.PI * (double)i / (2.0 * (double)this.length);
            this.wr[i] = factor * Math.cos(freq);
            this.wi[i] = factor * Math.sin(freq);
        }
    }

    public void setData(int dataType, Object data, int offset, int stride, int count) {
        if (this.isForwardTransform) {
            this.setFCTData(dataType, data, offset, stride, count);
        } else {
            this.setIFCTData(dataType, data, offset, stride, count);
        }
    }

    public void getData(int dataType, Object data, int offset, int stride) {
        if (this.isForwardTransform) {
            this.getFCTData(dataType, data, offset, stride);
        } else {
            this.getIFCTData(dataType, data, offset, stride);
        }
    }

    private void setFCTData(int dataType, Object data, int offset, int stride, int count) {
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
                throw new RuntimeException(dataType + JaiI18N.getString("FCTmediaLib1"));
            }
        }
        Arrays.fill(this.imag, 0, this.length, 0.0);
    }

    private void getFCTData(int dataType, Object data, int offset, int stride) {
        switch (dataType) {
            case 4: {
                float[] realFloat = (float[])data;
                for (int i = 0; i < this.length; ++i) {
                    realFloat[offset] = (float)(this.wr[i] * this.real[i] + this.wi[i] * this.imag[i]);
                    offset += stride;
                }
                break;
            }
            case 5: {
                double[] realDouble = (double[])data;
                for (int i = 0; i < this.length; ++i) {
                    realDouble[offset] = this.wr[i] * this.real[i] + this.wi[i] * this.imag[i];
                    offset += stride;
                }
                break;
            }
            default: {
                throw new RuntimeException(dataType + JaiI18N.getString("FCTmediaLib1"));
            }
        }
    }

    private void setIFCTData(int dataType, Object data, int offset, int stride, int count) {
        switch (dataType) {
            case 4: {
                float[] realFloat = (float[])data;
                for (int i = 0; i < count; ++i) {
                    float r = realFloat[offset];
                    this.real[i] = (double)r * this.wr[i];
                    this.imag[i] = (double)r * this.wi[i];
                    offset += stride;
                }
                break;
            }
            case 5: {
                double[] realDouble = (double[])data;
                for (int i = 0; i < count; ++i) {
                    double r = realDouble[offset];
                    this.real[i] = r * this.wr[i];
                    this.imag[i] = r * this.wi[i];
                    offset += stride;
                }
                break;
            }
            default: {
                throw new RuntimeException(dataType + JaiI18N.getString("FCTmediaLib1"));
            }
        }
        if (count < this.length) {
            Arrays.fill(this.real, count, this.length, 0.0);
            Arrays.fill(this.imag, count, this.length, 0.0);
        }
    }

    private void getIFCTData(int dataType, Object data, int offset, int stride) {
        switch (dataType) {
            case 4: {
                float[] realFloat = (float[])data;
                int k = this.length - 1;
                for (int i = 0; i < k; ++i) {
                    realFloat[offset] = (float)this.real[i];
                    realFloat[offset += stride] = (float)this.real[k--];
                    offset += stride;
                }
                break;
            }
            case 5: {
                double[] realDouble = (double[])data;
                int k = this.length - 1;
                for (int i = 0; i < k; ++i) {
                    realDouble[offset] = (float)this.real[i];
                    realDouble[offset += stride] = (float)this.real[k--];
                    offset += stride;
                }
                break;
            }
            default: {
                throw new RuntimeException(dataType + JaiI18N.getString("FCTmediaLib1"));
            }
        }
    }

    public void transform() {
        if (this.isForwardTransform) {
            Image.FFT_1((double[])this.real, (double[])this.imag);
        } else {
            Image.IFFT_2((double[])this.real, (double[])this.imag);
        }
    }
}

