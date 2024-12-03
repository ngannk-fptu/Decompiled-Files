/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.image.Kernel;
import java.io.Serializable;
import javax.media.jai.JaiI18N;

public class KernelJAI
implements Serializable {
    public static final KernelJAI ERROR_FILTER_FLOYD_STEINBERG = new KernelJAI(3, 2, 1, 0, new float[]{0.0f, 0.0f, 0.4375f, 0.1875f, 0.3125f, 0.0625f});
    public static final KernelJAI ERROR_FILTER_JARVIS = new KernelJAI(5, 3, 2, 0, new float[]{0.0f, 0.0f, 0.0f, 0.14583333f, 0.104166664f, 0.0625f, 0.104166664f, 0.14583333f, 0.104166664f, 0.0625f, 0.020833334f, 0.0625f, 0.104166664f, 0.0625f, 0.020833334f});
    public static final KernelJAI ERROR_FILTER_STUCKI = new KernelJAI(5, 3, 2, 0, new float[]{0.0f, 0.0f, 0.0f, 0.16666667f, 0.11904762f, 0.04761905f, 0.0952381f, 0.1904762f, 0.0952381f, 0.04761905f, 0.023809524f, 0.04761905f, 0.0952381f, 0.04761905f, 0.023809524f});
    public static final KernelJAI[] DITHER_MASK_441 = new KernelJAI[]{new KernelJAI(4, 4, 1, 1, new float[]{0.9375f, 0.4375f, 0.8125f, 0.3125f, 0.1875f, 0.6875f, 0.0625f, 0.5625f, 0.75f, 0.25f, 0.875f, 0.375f, 0.0f, 0.5f, 0.125f, 0.625f})};
    public static final KernelJAI[] DITHER_MASK_443 = new KernelJAI[]{new KernelJAI(4, 4, 1, 1, new float[]{0.0f, 0.5f, 0.125f, 0.625f, 0.75f, 0.25f, 0.875f, 0.375f, 0.1875f, 0.6875f, 0.0625f, 0.5625f, 0.9375f, 0.4375f, 0.8125f, 0.3125f}), new KernelJAI(4, 4, 1, 1, new float[]{0.625f, 0.125f, 0.5f, 0.0f, 0.375f, 0.875f, 0.25f, 0.75f, 0.5625f, 0.0625f, 0.6875f, 0.1875f, 0.3125f, 0.8125f, 0.4375f, 0.9375f}), new KernelJAI(4, 4, 1, 1, new float[]{0.9375f, 0.4375f, 0.8125f, 0.3125f, 0.1875f, 0.6875f, 0.0625f, 0.5625f, 0.75f, 0.25f, 0.875f, 0.375f, 0.0f, 0.5f, 0.125f, 0.625f})};
    public static final KernelJAI GRADIENT_MASK_SOBEL_VERTICAL = new KernelJAI(3, 3, 1, 1, new float[]{-1.0f, -2.0f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 2.0f, 1.0f});
    public static final KernelJAI GRADIENT_MASK_SOBEL_HORIZONTAL = new KernelJAI(3, 3, 1, 1, new float[]{-1.0f, 0.0f, 1.0f, -2.0f, 0.0f, 2.0f, -1.0f, 0.0f, 1.0f});
    protected int width;
    protected int height;
    protected int xOrigin;
    protected int yOrigin;
    protected float[] data = null;
    protected float[] dataH = null;
    protected float[] dataV = null;
    protected boolean isSeparable = false;
    protected boolean isHorizontallySymmetric = false;
    protected boolean isVerticallySymmetric = false;
    protected KernelJAI rotatedKernel = null;

    private synchronized void checkSeparable() {
        block15: {
            float sumV;
            float sumH;
            int j;
            float fac;
            float floatZeroTol;
            block14: {
                floatZeroTol = 1.0E-5f;
                if (this.isSeparable) {
                    return;
                }
                if (this.width <= 1 || this.height <= 1) {
                    return;
                }
                float maxData = 0.0f;
                int imax = 0;
                int jmax = 0;
                for (int k = 0; k < this.data.length; ++k) {
                    float tmp = Math.abs(this.data[k]);
                    if (!(tmp > maxData)) continue;
                    imax = k;
                    maxData = tmp;
                }
                if (maxData < floatZeroTol / (float)this.data.length) {
                    this.isSeparable = false;
                    return;
                }
                float[] tmpRow = new float[this.width];
                fac = 1.0f / this.data[imax];
                jmax = imax % this.width;
                imax /= this.width;
                for (int j2 = 0; j2 < this.width; ++j2) {
                    tmpRow[j2] = this.data[imax * this.width + j2] * fac;
                }
                int i = 0;
                int i0 = 0;
                while (i < this.height) {
                    for (j = 0; j < this.width; ++j) {
                        float tmp = Math.abs(this.data[i0 + jmax] * tmpRow[j] - this.data[i0 + j]);
                        if (!(tmp > floatZeroTol)) continue;
                        this.isSeparable = false;
                        return;
                    }
                    ++i;
                    i0 += this.width;
                }
                this.dataH = tmpRow;
                this.dataV = new float[this.height];
                for (i = 0; i < this.height; ++i) {
                    this.dataV[i] = this.data[jmax + i * this.width];
                }
                this.isSeparable = true;
                sumH = 0.0f;
                sumV = 0.0f;
                for (j = 0; j < this.width; ++j) {
                    sumH += this.dataH[j];
                }
                for (j = 0; j < this.height; ++j) {
                    sumV += this.dataV[j];
                }
                if (!(Math.abs(sumH) >= Math.abs(sumV)) || !(Math.abs(sumH) > floatZeroTol)) break block14;
                fac = 1.0f / sumH;
                j = 0;
                while (j < this.width) {
                    int n = j++;
                    this.dataH[n] = this.dataH[n] * fac;
                }
                j = 0;
                while (j < this.height) {
                    int n = j++;
                    this.dataV[n] = this.dataV[n] * sumH;
                }
                break block15;
            }
            if (!(Math.abs(sumH) < Math.abs(sumV)) || !(Math.abs(sumV) > floatZeroTol)) break block15;
            fac = 1.0f / sumV;
            j = 0;
            while (j < this.width) {
                int n = j++;
                this.dataH[n] = this.dataH[n] * sumV;
            }
            j = 0;
            while (j < this.height) {
                int n = j++;
                this.dataV[n] = this.dataV[n] * fac;
            }
        }
    }

    private void classifyKernel() {
        if (!this.isSeparable) {
            this.checkSeparable();
        }
        this.isHorizontallySymmetric = false;
        this.isVerticallySymmetric = false;
    }

    public KernelJAI(int width, int height, int xOrigin, int yOrigin, float[] data) {
        if (data == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.width = width;
        this.height = height;
        this.xOrigin = xOrigin;
        this.yOrigin = yOrigin;
        this.data = (float[])data.clone();
        if (width <= 0) {
            throw new IllegalArgumentException(JaiI18N.getString("KernelJAI0"));
        }
        if (height <= 0) {
            throw new IllegalArgumentException(JaiI18N.getString("KernelJAI1"));
        }
        if (width * height != data.length) {
            throw new IllegalArgumentException(JaiI18N.getString("KernelJAI2"));
        }
        this.classifyKernel();
    }

    public KernelJAI(int width, int height, int xOrigin, int yOrigin, float[] dataH, float[] dataV) {
        if (dataH == null || dataV == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (width <= 0) {
            throw new IllegalArgumentException(JaiI18N.getString("KernelJAI0"));
        }
        if (height <= 0) {
            throw new IllegalArgumentException(JaiI18N.getString("KernelJAI1"));
        }
        if (width != dataH.length) {
            throw new IllegalArgumentException(JaiI18N.getString("KernelJAI3"));
        }
        if (height != dataV.length) {
            throw new IllegalArgumentException(JaiI18N.getString("KernelJAI4"));
        }
        this.width = width;
        this.height = height;
        this.xOrigin = xOrigin;
        this.yOrigin = yOrigin;
        this.dataH = (float[])dataH.clone();
        this.dataV = (float[])dataV.clone();
        this.data = new float[dataH.length * dataV.length];
        int rowOffset = 0;
        for (int i = 0; i < dataV.length; ++i) {
            float vValue = dataV[i];
            for (int j = 0; j < dataH.length; ++j) {
                this.data[rowOffset + j] = vValue * dataH[j];
            }
            rowOffset += dataH.length;
        }
        this.isSeparable = true;
        this.classifyKernel();
    }

    public KernelJAI(int width, int height, float[] data) {
        this(width, height, width / 2, height / 2, data);
    }

    public KernelJAI(Kernel k) {
        this(k.getWidth(), k.getHeight(), k.getXOrigin(), k.getYOrigin(), k.getKernelData(null));
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getXOrigin() {
        return this.xOrigin;
    }

    public int getYOrigin() {
        return this.yOrigin;
    }

    public float[] getKernelData() {
        return (float[])this.data.clone();
    }

    public float[] getHorizontalKernelData() {
        if (this.dataH == null) {
            return null;
        }
        return (float[])this.dataH.clone();
    }

    public float[] getVerticalKernelData() {
        if (this.dataV == null) {
            return null;
        }
        return (float[])this.dataV.clone();
    }

    public float getElement(int xIndex, int yIndex) {
        if (!this.isSeparable) {
            return this.data[yIndex * this.width + xIndex];
        }
        return this.dataH[xIndex] * this.dataV[yIndex];
    }

    public boolean isSeparable() {
        return this.isSeparable;
    }

    public boolean isHorizontallySymmetric() {
        return this.isHorizontallySymmetric;
    }

    public boolean isVerticallySymmetric() {
        return this.isVerticallySymmetric;
    }

    public int getLeftPadding() {
        return this.xOrigin;
    }

    public int getRightPadding() {
        return this.width - this.xOrigin - 1;
    }

    public int getTopPadding() {
        return this.yOrigin;
    }

    public int getBottomPadding() {
        return this.height - this.yOrigin - 1;
    }

    public KernelJAI getRotatedKernel() {
        if (this.rotatedKernel == null) {
            if (this.isSeparable) {
                int i;
                float[] rotDataH = new float[this.width];
                float[] rotDataV = new float[this.height];
                for (i = 0; i < this.width; ++i) {
                    rotDataH[i] = this.dataH[this.width - 1 - i];
                }
                for (i = 0; i < this.height; ++i) {
                    rotDataV[i] = this.dataV[this.height - 1 - i];
                }
                this.rotatedKernel = new KernelJAI(this.width, this.height, this.width - 1 - this.xOrigin, this.height - 1 - this.yOrigin, rotDataH, rotDataV);
            } else {
                int length = this.data.length;
                float[] newData = new float[this.data.length];
                for (int i = 0; i < length; ++i) {
                    newData[i] = this.data[length - 1 - i];
                }
                this.rotatedKernel = new KernelJAI(this.width, this.height, this.width - 1 - this.xOrigin, this.height - 1 - this.yOrigin, newData);
            }
        }
        return this.rotatedKernel;
    }
}

