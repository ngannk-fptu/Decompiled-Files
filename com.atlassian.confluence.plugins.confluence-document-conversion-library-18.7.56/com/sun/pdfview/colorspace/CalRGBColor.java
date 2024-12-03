/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.colorspace;

import com.sun.pdfview.PDFObject;
import com.sun.pdfview.function.FunctionType0;
import java.awt.color.ColorSpace;
import java.io.IOException;

public class CalRGBColor
extends ColorSpace {
    private static final float[] vonKriesM = new float[]{0.40024f, -0.2263f, 0.0f, 0.7076f, 1.16532f, 0.0f, -0.08081f, 0.0457f, 0.91822f};
    private static final float[] vonKriesMinv = new float[]{1.859936f, 0.361191f, 0.0f, -1.129382f, 0.638812f, 0.0f, 0.219897f, -6.0E-6f, 1.089064f};
    private static final float[] xyzToSRGB = new float[]{3.24071f, -0.969258f, 0.0556352f, -1.53726f, 1.87599f, -0.203996f, -0.498571f, 0.0415557f, 1.05707f};
    private static final float[] xyzToRGB = new float[]{2.04148f, -0.969258f, 0.0134455f, -0.564977f, 1.87599f, -0.118373f, -0.344713f, 0.0415557f, 1.01527f};
    float[] scale;
    float[] max;
    float[] white = new float[]{1.0f, 1.0f, 1.0f};
    float[] black = new float[]{0.0f, 0.0f, 0.0f};
    float[] matrix = new float[]{1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f};
    float[] gamma = new float[]{1.0f, 1.0f, 1.0f};
    static ColorSpace rgbCS = ColorSpace.getInstance(1000);
    static ColorSpace cieCS = ColorSpace.getInstance(1001);

    public CalRGBColor(PDFObject obj) throws IOException {
        super(1000, 3);
        int i;
        PDFObject ary = obj.getDictRef("WhitePoint");
        if (ary != null) {
            for (i = 0; i < 3; ++i) {
                this.white[i] = ary.getAt(i).getFloatValue();
            }
        }
        if ((ary = obj.getDictRef("BlackPoint")) != null) {
            for (i = 0; i < 3; ++i) {
                this.black[i] = ary.getAt(i).getFloatValue();
            }
        }
        if ((ary = obj.getDictRef("Gamma")) != null) {
            for (i = 0; i < 3; ++i) {
                this.gamma[i] = ary.getAt(i).getFloatValue();
            }
        }
        if ((ary = obj.getDictRef("Matrix")) != null) {
            for (i = 0; i < 9; ++i) {
                this.matrix[i] = ary.getAt(i).getFloatValue();
            }
        }
        float[] cieWhite = rgbCS.toCIEXYZ(new float[]{1.0f, 1.0f, 1.0f});
        float[] sourceWhite = this.matrixMult(this.white, vonKriesM, 3);
        float[] destWhite = this.matrixMult(cieWhite, vonKriesM, 3);
        this.scale = new float[]{destWhite[0] / sourceWhite[0], 0.0f, 0.0f, 0.0f, destWhite[1] / sourceWhite[1], 0.0f, 0.0f, 0.0f, destWhite[2] / sourceWhite[2]};
        this.scale = this.matrixMult(vonKriesM, this.scale, 3);
        this.scale = this.matrixMult(this.scale, vonKriesMinv, 3);
        this.max = this.matrixMult(this.white, this.scale, 3);
        this.max = this.ciexyzToSRGB(this.max);
    }

    @Override
    public int getNumComponents() {
        return 3;
    }

    @Override
    public float[] toRGB(float[] comp) {
        if (comp.length == 3) {
            float a = (float)Math.pow(comp[0], this.gamma[0]);
            float b = (float)Math.pow(comp[1], this.gamma[1]);
            float c = (float)Math.pow(comp[2], this.gamma[2]);
            float[] xyz = new float[]{this.matrix[0] * a + this.matrix[3] * b + this.matrix[6] * c, this.matrix[1] * a + this.matrix[4] * b + this.matrix[7] * c, this.matrix[2] * a + this.matrix[5] * b + this.matrix[8] * c};
            xyz = this.matrixMult(xyz, this.scale, 3);
            float[] rgb = this.ciexyzToSRGB(xyz);
            for (int i = 0; i < rgb.length; ++i) {
                rgb[i] = FunctionType0.interpolate(rgb[i], 0.0f, this.max[i], 0.0f, 1.0f);
                if (!((double)rgb[i] > 1.0)) continue;
                rgb[i] = 1.0f;
            }
            return rgb;
        }
        return this.black;
    }

    private float[] ciexyzToSRGB(float[] xyz) {
        float[] rgb = this.matrixMult(xyz, xyzToSRGB, 3);
        for (int i = 0; i < rgb.length; ++i) {
            if ((double)rgb[i] < 0.0) {
                rgb[i] = 0.0f;
            } else if ((double)rgb[i] > 1.0) {
                rgb[i] = 1.0f;
            }
            if ((double)rgb[i] < 0.003928) {
                int n = i;
                rgb[n] = (float)((double)rgb[n] * 12.92);
                continue;
            }
            rgb[i] = (float)(Math.pow(rgb[i], 0.4166666666666667) * 1.055 - 0.055);
        }
        return rgb;
    }

    @Override
    public float[] fromRGB(float[] rgbvalue) {
        return new float[3];
    }

    @Override
    public float[] fromCIEXYZ(float[] colorvalue) {
        return new float[3];
    }

    @Override
    public int getType() {
        return 5;
    }

    @Override
    public float[] toCIEXYZ(float[] colorvalue) {
        return new float[3];
    }

    float[] matrixMult(float[] a, float[] b, int len) {
        int rows = a.length / len;
        int cols = b.length / len;
        float[] out = new float[rows * cols];
        for (int i = 0; i < rows; ++i) {
            for (int k = 0; k < cols; ++k) {
                for (int j = 0; j < len; ++j) {
                    int n = i * cols + k;
                    out[n] = out[n] + a[i * len + j] * b[j * cols + k];
                }
            }
        }
        return out;
    }
}

