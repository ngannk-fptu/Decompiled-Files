/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import javax.media.jai.ComponentSampleModelJAI;
import javax.media.jai.JaiI18N;
import javax.media.jai.RasterFactory;

public class FloatDoubleColorModel
extends ComponentColorModel {
    protected ColorSpace colorSpace;
    protected int colorSpaceType;
    protected int numColorComponents;
    protected int numComponents;
    protected int transparency;
    protected boolean hasAlpha;
    protected boolean isAlphaPremultiplied;

    private static int[] bitsHelper(int transferType, ColorSpace colorSpace, boolean hasAlpha) {
        int numBits = transferType == 4 ? 32 : 64;
        int numComponents = colorSpace.getNumComponents();
        if (hasAlpha) {
            ++numComponents;
        }
        int[] bits = new int[numComponents];
        for (int i = 0; i < numComponents; ++i) {
            bits[i] = numBits;
        }
        return bits;
    }

    public FloatDoubleColorModel(ColorSpace colorSpace, boolean hasAlpha, boolean isAlphaPremultiplied, int transparency, int transferType) {
        super(colorSpace, FloatDoubleColorModel.bitsHelper(transferType, colorSpace, hasAlpha), hasAlpha, isAlphaPremultiplied, transparency, transferType);
        if (transferType != 4 && transferType != 5) {
            throw new IllegalArgumentException(JaiI18N.getString("FloatDoubleColorModel0"));
        }
        this.colorSpace = colorSpace;
        this.colorSpaceType = colorSpace.getType();
        this.numComponents = this.numColorComponents = colorSpace.getNumComponents();
        if (hasAlpha) {
            ++this.numComponents;
        }
        this.transparency = transparency;
        this.hasAlpha = hasAlpha;
        this.isAlphaPremultiplied = isAlphaPremultiplied;
    }

    public int getRed(int pixel) {
        throw new IllegalArgumentException(JaiI18N.getString("FloatDoubleColorModel1"));
    }

    public int getGreen(int pixel) {
        throw new IllegalArgumentException(JaiI18N.getString("FloatDoubleColorModel2"));
    }

    public int getBlue(int pixel) {
        throw new IllegalArgumentException(JaiI18N.getString("FloatDoubleColorModel3"));
    }

    public int getAlpha(int pixel) {
        throw new IllegalArgumentException(JaiI18N.getString("FloatDoubleColorModel4"));
    }

    public int getRGB(int pixel) {
        throw new IllegalArgumentException(JaiI18N.getString("FloatDoubleColorModel5"));
    }

    private final int clamp(float value) {
        return value >= 0.0f ? (value > 255.0f ? 255 : (int)value) : 0;
    }

    private final int clamp(double value) {
        return value >= 0.0 ? (value > 255.0 ? 255 : (int)value) : 0;
    }

    private int getSample(Object inData, int sample) {
        float[] rgb;
        boolean needAlpha = this.hasAlpha && this.isAlphaPremultiplied;
        int type = this.colorSpaceType;
        boolean is_sRGB = this.colorSpace.isCS_sRGB();
        if (type == 6) {
            sample = 0;
            is_sRGB = true;
        }
        if (is_sRGB) {
            if (this.transferType == 4) {
                float[] fdata = (float[])inData;
                float fsample = fdata[sample] * 255.0f;
                if (needAlpha) {
                    float falp = fdata[this.numColorComponents];
                    if ((double)falp == 0.0) {
                        return 0;
                    }
                    return this.clamp(fsample / falp);
                }
                return this.clamp(fsample);
            }
            double[] ddata = (double[])inData;
            double dsample = ddata[sample] * 255.0;
            if (needAlpha) {
                double dalp = ddata[this.numColorComponents];
                if (dalp == 0.0) {
                    return 0;
                }
                return this.clamp(dsample / dalp);
            }
            return this.clamp(dsample);
        }
        if (this.transferType == 4) {
            float[] rgb2;
            float[] fdata = (float[])inData;
            if (needAlpha) {
                float falp = fdata[this.numColorComponents];
                if ((double)falp == 0.0) {
                    return 0;
                }
                float[] norm = new float[this.numColorComponents];
                for (int i = 0; i < this.numColorComponents; ++i) {
                    norm[i] = fdata[i] / falp;
                }
                rgb2 = this.colorSpace.toRGB(norm);
            } else {
                rgb2 = this.colorSpace.toRGB(fdata);
            }
            return (int)(rgb2[sample] * 255.0f + 0.5f);
        }
        double[] ddata = (double[])inData;
        float[] norm = new float[this.numColorComponents];
        if (needAlpha) {
            double dalp = ddata[this.numColorComponents];
            if (dalp == 0.0) {
                return 0;
            }
            for (int i = 0; i < this.numColorComponents; ++i) {
                norm[i] = (float)(ddata[i] / dalp);
            }
            rgb = this.colorSpace.toRGB(norm);
        } else {
            for (int i = 0; i < this.numColorComponents; ++i) {
                norm[i] = (float)ddata[i];
            }
            rgb = this.colorSpace.toRGB(norm);
        }
        return (int)((double)(rgb[sample] * 255.0f) + 0.5);
    }

    public int getRed(Object inData) {
        return this.getSample(inData, 0);
    }

    public int getGreen(Object inData) {
        return this.getSample(inData, 1);
    }

    public int getBlue(Object inData) {
        return this.getSample(inData, 2);
    }

    public int getAlpha(Object inData) {
        if (inData == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (!this.hasAlpha) {
            return 255;
        }
        if (this.transferType == 4) {
            float[] fdata = (float[])inData;
            return (int)(fdata[this.numColorComponents] * 255.0f + 0.5f);
        }
        double[] ddata = (double[])inData;
        return (int)(ddata[this.numColorComponents] * 255.0 + 0.5);
    }

    public int getRGB(Object inData) {
        int blue;
        int green;
        int red;
        boolean needAlpha = this.hasAlpha && this.isAlphaPremultiplied;
        int alpha = 255;
        if (this.colorSpace.isCS_sRGB()) {
            if (this.transferType == 4) {
                float[] fdata = (float[])inData;
                float fred = fdata[0];
                float fgreen = fdata[1];
                float fblue = fdata[2];
                float fscale = 255.0f;
                if (needAlpha) {
                    float falpha = fdata[3];
                    fscale /= falpha;
                    alpha = this.clamp(255.0f * falpha);
                }
                red = this.clamp(fred * fscale);
                green = this.clamp(fgreen * fscale);
                blue = this.clamp(fblue * fscale);
            } else {
                double[] ddata = (double[])inData;
                double dred = ddata[0];
                double dgreen = ddata[1];
                double dblue = ddata[2];
                double dscale = 255.0;
                if (needAlpha) {
                    double dalpha = ddata[3];
                    dscale /= dalpha;
                    alpha = this.clamp(255.0 * dalpha);
                }
                red = this.clamp(dred * dscale);
                green = this.clamp(dgreen * dscale);
                blue = this.clamp(dblue * dscale);
            }
        } else if (this.colorSpaceType == 6) {
            if (this.transferType == 4) {
                float[] fdata = (float[])inData;
                float fgray = fdata[0];
                if (needAlpha) {
                    float falp = fdata[1];
                    green = blue = this.clamp(fgray * 255.0f / falp);
                    red = blue;
                    alpha = this.clamp(255.0f * falp);
                } else {
                    green = blue = this.clamp(fgray * 255.0f);
                    red = blue;
                }
            } else {
                double[] ddata = (double[])inData;
                double dgray = ddata[0];
                if (needAlpha) {
                    double dalp = ddata[1];
                    green = blue = this.clamp(dgray * 255.0 / dalp);
                    red = blue;
                    alpha = this.clamp(255.0 * dalp);
                } else {
                    green = blue = this.clamp(dgray * 255.0);
                    red = blue;
                }
            }
        } else {
            float[] norm;
            if (this.transferType == 4) {
                float[] fdata = (float[])inData;
                if (needAlpha) {
                    float falp = fdata[this.numColorComponents];
                    float invfalp = 1.0f / falp;
                    norm = new float[this.numColorComponents];
                    for (int i = 0; i < this.numColorComponents; ++i) {
                        norm[i] = fdata[i] * invfalp;
                    }
                    alpha = this.clamp(255.0f * falp);
                } else {
                    norm = fdata;
                }
            } else {
                double[] ddata = (double[])inData;
                norm = new float[this.numColorComponents];
                if (needAlpha) {
                    double dalp = ddata[this.numColorComponents];
                    double invdalp = 1.0 / dalp;
                    for (int i = 0; i < this.numColorComponents; ++i) {
                        norm[i] = (float)(ddata[i] * invdalp);
                    }
                    alpha = this.clamp(255.0 * dalp);
                } else {
                    for (int i = 0; i < this.numColorComponents; ++i) {
                        norm[i] = (float)ddata[i];
                    }
                }
            }
            float[] rgb = this.colorSpace.toRGB(norm);
            red = this.clamp(rgb[0] * 255.0f);
            green = this.clamp(rgb[1] * 255.0f);
            blue = this.clamp(rgb[2] * 255.0f);
        }
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    public Object getDataElements(int rgb, Object pixel) {
        double[] doublePixel;
        if (this.transferType == 4) {
            float[] floatPixel;
            if (pixel == null) {
                floatPixel = new float[this.numComponents];
            } else {
                if (!(pixel instanceof float[])) {
                    throw new ClassCastException(JaiI18N.getString("FloatDoubleColorModel7"));
                }
                floatPixel = (float[])pixel;
                if (floatPixel.length < this.numComponents) {
                    throw new ArrayIndexOutOfBoundsException(JaiI18N.getString("FloatDoubleColorModel8"));
                }
            }
            float inv255 = 0.003921569f;
            if (this.colorSpace.isCS_sRGB()) {
                int alp = rgb >> 24 & 0xFF;
                int red = rgb >> 16 & 0xFF;
                int grn = rgb >> 8 & 0xFF;
                int blu = rgb & 0xFF;
                float norm = inv255;
                if (this.isAlphaPremultiplied) {
                    norm *= (float)alp;
                }
                floatPixel[0] = (float)red * norm;
                floatPixel[1] = (float)grn * norm;
                floatPixel[2] = (float)blu * norm;
                if (this.hasAlpha) {
                    floatPixel[3] = (float)alp * inv255;
                }
            } else if (this.colorSpaceType == 6) {
                float gray;
                floatPixel[0] = gray = (float)(rgb >> 16 & 0xFF) * (0.299f * inv255) + (float)(rgb >> 8 & 0xFF) * (0.587f * inv255) + (float)(rgb & 0xFF) * (0.114f * inv255);
                if (this.hasAlpha) {
                    int alpha = rgb >> 24 & 0xFF;
                    floatPixel[1] = (float)alpha * inv255;
                }
            } else {
                float[] norm = new float[]{(float)(rgb >> 16 & 0xFF) * inv255, (float)(rgb >> 8 & 0xFF) * inv255, (float)(rgb & 0xFF) * inv255};
                norm = this.colorSpace.fromRGB(norm);
                for (int i = 0; i < this.numColorComponents; ++i) {
                    floatPixel[i] = norm[i];
                }
                if (this.hasAlpha) {
                    int alpha = rgb >> 24 & 0xFF;
                    floatPixel[this.numColorComponents] = (float)alpha * inv255;
                }
            }
            return floatPixel;
        }
        if (pixel == null) {
            doublePixel = new double[this.numComponents];
        } else {
            if (!(pixel instanceof double[])) {
                throw new ClassCastException(JaiI18N.getString("FloatDoubleColorModel7"));
            }
            doublePixel = (double[])pixel;
            if (doublePixel.length < this.numComponents) {
                throw new ArrayIndexOutOfBoundsException(JaiI18N.getString("FloatDoubleColorModel8"));
            }
        }
        double inv255 = 0.00392156862745098;
        if (this.colorSpace.isCS_sRGB()) {
            int alp = rgb >> 24 & 0xFF;
            int red = rgb >> 16 & 0xFF;
            int grn = rgb >> 8 & 0xFF;
            int blu = rgb & 0xFF;
            double norm = inv255;
            if (this.isAlphaPremultiplied) {
                norm *= (double)alp;
            }
            doublePixel[0] = (double)red * norm;
            doublePixel[1] = (double)grn * norm;
            doublePixel[2] = (double)blu * norm;
            if (this.hasAlpha) {
                doublePixel[3] = (double)alp * inv255;
            }
        } else if (this.colorSpaceType == 6) {
            double gray;
            doublePixel[0] = gray = (double)(rgb >> 16 & 0xFF) * (0.299 * inv255) + (double)(rgb >> 8 & 0xFF) * (0.587 * inv255) + (double)(rgb & 0xFF) * (0.114 * inv255);
            if (this.hasAlpha) {
                int alpha = rgb >> 24 & 0xFF;
                doublePixel[1] = (double)alpha * inv255;
            }
        } else {
            float inv255F = 0.003921569f;
            float[] norm = new float[]{(float)(rgb >> 16 & 0xFF) * inv255F, (float)(rgb >> 8 & 0xFF) * inv255F, (float)(rgb & 0xFF) * inv255F};
            norm = this.colorSpace.fromRGB(norm);
            for (int i = 0; i < this.numColorComponents; ++i) {
                doublePixel[i] = norm[i];
            }
            if (this.hasAlpha) {
                int alpha = rgb >> 24 & 0xFF;
                doublePixel[this.numColorComponents] = (double)alpha * inv255;
            }
        }
        return doublePixel;
    }

    public int[] getComponents(int pixel, int[] components, int offset) {
        throw new IllegalArgumentException(JaiI18N.getString("FloatDoubleColorModel9"));
    }

    public int[] getComponents(Object pixel, int[] components, int offset) {
        throw new IllegalArgumentException(JaiI18N.getString("FloatDoubleColorModel9"));
    }

    public int getDataElement(int[] components, int offset) {
        throw new IllegalArgumentException(JaiI18N.getString("FloatDoubleColorModel9"));
    }

    public Object getDataElements(int[] components, int offset, Object obj) {
        if (components.length - offset < this.numComponents) {
            throw new IllegalArgumentException(this.numComponents + " " + JaiI18N.getString("FloatDoubleColorModel10"));
        }
        if (this.transferType == 4) {
            float[] pixel = obj == null ? new float[components.length] : (float[])obj;
            for (int i = 0; i < this.numComponents; ++i) {
                pixel[i] = components[offset + i];
            }
            return pixel;
        }
        double[] pixel = obj == null ? new double[components.length] : (double[])obj;
        for (int i = 0; i < this.numComponents; ++i) {
            pixel[i] = components[offset + i];
        }
        return pixel;
    }

    public ColorModel coerceData(WritableRaster raster, boolean isAlphaPremultiplied) {
        if (!this.hasAlpha || this.isAlphaPremultiplied == isAlphaPremultiplied) {
            return this;
        }
        int w = raster.getWidth();
        int h = raster.getHeight();
        int aIdx = raster.getNumBands() - 1;
        int rminX = raster.getMinX();
        int rY = raster.getMinY();
        if (raster.getTransferType() != this.transferType) {
            throw new IllegalArgumentException(JaiI18N.getString("FloatDoubleColorModel6"));
        }
        if (isAlphaPremultiplied) {
            switch (this.transferType) {
                case 4: {
                    float[] pixel = null;
                    int y = 0;
                    while (y < h) {
                        int rX = rminX;
                        int x = 0;
                        while (x < w) {
                            float fAlpha = (pixel = (float[])raster.getDataElements(rX, rY, pixel))[aIdx];
                            if (fAlpha != 0.0f) {
                                int c = 0;
                                while (c < aIdx) {
                                    int n = c++;
                                    pixel[n] = pixel[n] * fAlpha;
                                }
                                raster.setDataElements(rX, rY, pixel);
                            }
                            ++x;
                            ++rX;
                        }
                        ++y;
                        ++rY;
                    }
                    break;
                }
                case 5: {
                    double[] pixel = null;
                    int y = 0;
                    while (y < h) {
                        int rX = rminX;
                        int x = 0;
                        while (x < w) {
                            double dAlpha = (pixel = (double[])raster.getDataElements(rX, rY, pixel))[aIdx];
                            if (dAlpha != 0.0) {
                                int c = 0;
                                while (c < aIdx) {
                                    int n = c++;
                                    pixel[n] = pixel[n] * dAlpha;
                                }
                                raster.setDataElements(rX, rY, pixel);
                            }
                            ++x;
                            ++rX;
                        }
                        ++y;
                        ++rY;
                    }
                    break;
                }
                default: {
                    throw new RuntimeException(JaiI18N.getString("FloatDoubleColorModel0"));
                }
            }
            if (isAlphaPremultiplied) {
                // empty if block
            }
        } else {
            switch (this.transferType) {
                case 4: {
                    int y = 0;
                    while (y < h) {
                        int rX = rminX;
                        int x = 0;
                        while (x < w) {
                            float[] pixel = null;
                            float fAlpha = (pixel = (float[])raster.getDataElements(rX, rY, pixel))[aIdx];
                            if (fAlpha != 0.0f) {
                                float invFAlpha = 1.0f / fAlpha;
                                int c = 0;
                                while (c < aIdx) {
                                    int n = c++;
                                    pixel[n] = pixel[n] * invFAlpha;
                                }
                            }
                            raster.setDataElements(rX, rY, pixel);
                            ++x;
                            ++rX;
                        }
                        ++y;
                        ++rY;
                    }
                    break;
                }
                case 5: {
                    int y = 0;
                    while (y < h) {
                        int rX = rminX;
                        int x = 0;
                        while (x < w) {
                            double[] pixel = null;
                            double dAlpha = (pixel = (double[])raster.getDataElements(rX, rY, pixel))[aIdx];
                            if (dAlpha != 0.0) {
                                double invDAlpha = 1.0 / dAlpha;
                                int c = 0;
                                while (c < aIdx) {
                                    int n = c++;
                                    pixel[n] = pixel[n] * invDAlpha;
                                }
                            }
                            raster.setDataElements(rX, rY, pixel);
                            ++x;
                            ++rX;
                        }
                        ++y;
                        ++rY;
                    }
                    break;
                }
                default: {
                    throw new RuntimeException(JaiI18N.getString("FloatDoubleColorModel0"));
                }
            }
        }
        return new FloatDoubleColorModel(this.colorSpace, this.hasAlpha, isAlphaPremultiplied, this.transparency, this.transferType);
    }

    public boolean isCompatibleRaster(Raster raster) {
        SampleModel sm = raster.getSampleModel();
        return this.isCompatibleSampleModel(sm);
    }

    public WritableRaster createCompatibleWritableRaster(int w, int h) {
        SampleModel sm = this.createCompatibleSampleModel(w, h);
        return RasterFactory.createWritableRaster(sm, new Point(0, 0));
    }

    public SampleModel createCompatibleSampleModel(int w, int h) {
        int[] bandOffsets = new int[this.numComponents];
        for (int i = 0; i < this.numComponents; ++i) {
            bandOffsets[i] = i;
        }
        return new ComponentSampleModelJAI(this.transferType, w, h, this.numComponents, w * this.numComponents, bandOffsets);
    }

    public boolean isCompatibleSampleModel(SampleModel sm) {
        if (sm instanceof ComponentSampleModel) {
            if (sm.getNumBands() != this.getNumComponents()) {
                return false;
            }
            return sm.getDataType() == this.transferType;
        }
        return false;
    }

    public String toString() {
        return "FloatDoubleColorModel: " + super.toString();
    }
}

