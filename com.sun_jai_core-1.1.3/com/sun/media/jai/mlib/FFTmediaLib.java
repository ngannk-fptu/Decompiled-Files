/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.medialib.mlib.Image
 */
package com.sun.media.jai.mlib;

import com.sun.media.jai.mlib.JaiI18N;
import com.sun.media.jai.opimage.FFT;
import com.sun.media.jai.util.MathJAI;
import com.sun.medialib.mlib.Image;

public class FFTmediaLib
extends FFT {
    private boolean specialUnitaryScaling = false;
    private static final double SQUARE_ROOT_OF_2 = Math.sqrt(2.0);

    public FFTmediaLib(boolean negatedExponent, Integer scaleType, int length) {
        super(negatedExponent, scaleType, length);
    }

    public void setLength(int length) {
        if (this.lengthIsSet && length == this.length) {
            return;
        }
        if (!MathJAI.isPositivePowerOf2(length)) {
            throw new RuntimeException(JaiI18N.getString("FFTmediaLib0"));
        }
        this.length = length;
        if (!this.lengthIsSet || length != this.real.length) {
            this.real = new double[length];
            this.imag = new double[length];
        }
        this.lengthIsSet = true;
        if (this.scaleType == SCALING_UNITARY) {
            int exponent = 0;
            int powerOfTwo = 1;
            while (powerOfTwo < length) {
                powerOfTwo <<= 1;
                ++exponent;
            }
            this.specialUnitaryScaling = exponent % 2 != 0;
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
                            realFloat[offsetReal] = (float)this.real[i];
                            imagFloat[offsetReal] = (float)this.imag[i];
                            offsetReal += strideReal;
                        }
                    } else {
                        for (int i = 0; i < this.length; ++i) {
                            realFloat[offsetReal] = (float)this.real[i];
                            imagFloat[offsetImag] = (float)this.imag[i];
                            offsetReal += strideReal;
                            offsetImag += strideImag;
                        }
                    }
                } else {
                    for (int i = 0; i < this.length; ++i) {
                        realFloat[offsetReal] = (float)this.real[i];
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
                            realDouble[offsetReal] = this.real[i];
                            imagDouble[offsetReal] = this.imag[i];
                            offsetReal += strideReal;
                        }
                    } else {
                        for (int i = 0; i < this.length; ++i) {
                            realDouble[offsetReal] = this.real[i];
                            imagDouble[offsetImag] = this.imag[i];
                            offsetReal += strideReal;
                            offsetImag += strideImag;
                        }
                    }
                } else {
                    for (int i = 0; i < this.length; ++i) {
                        realDouble[offsetReal] = this.real[i];
                        offsetReal += strideReal;
                    }
                }
                break;
            }
            default: {
                throw new RuntimeException(dataType + JaiI18N.getString("FFTmediaLib1"));
            }
        }
    }

    public void transform() {
        if (this.exponentSign < 0) {
            if (this.scaleType == SCALING_NONE) {
                Image.FFT_1((double[])this.real, (double[])this.imag);
            } else if (this.scaleType == SCALING_UNITARY) {
                Image.FFT_3((double[])this.real, (double[])this.imag);
                if (this.specialUnitaryScaling) {
                    int i = 0;
                    while (i < this.length) {
                        int n = i;
                        this.real[n] = this.real[n] * SQUARE_ROOT_OF_2;
                        int n2 = i++;
                        this.imag[n2] = this.imag[n2] * SQUARE_ROOT_OF_2;
                    }
                }
            } else if (this.scaleType == SCALING_DIMENSIONS) {
                Image.FFT_2((double[])this.real, (double[])this.imag);
            }
        } else if (this.scaleType == SCALING_NONE) {
            Image.IFFT_2((double[])this.real, (double[])this.imag);
        } else if (this.scaleType == SCALING_UNITARY) {
            Image.IFFT_3((double[])this.real, (double[])this.imag);
            if (this.specialUnitaryScaling) {
                int i = 0;
                while (i < this.length) {
                    int n = i;
                    this.real[n] = this.real[n] / SQUARE_ROOT_OF_2;
                    int n3 = i++;
                    this.imag[n3] = this.imag[n3] / SQUARE_ROOT_OF_2;
                }
            }
        } else if (this.scaleType == SCALING_DIMENSIONS) {
            Image.IFFT_1((double[])this.real, (double[])this.imag);
        }
    }
}

