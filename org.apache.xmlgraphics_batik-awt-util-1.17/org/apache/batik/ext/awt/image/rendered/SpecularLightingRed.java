/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.rendered;

import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.Light;
import org.apache.batik.ext.awt.image.SpotLight;
import org.apache.batik.ext.awt.image.rendered.AbstractTiledRed;
import org.apache.batik.ext.awt.image.rendered.BumpMap;
import org.apache.batik.ext.awt.image.rendered.CachableRed;

public class SpecularLightingRed
extends AbstractTiledRed {
    private double ks;
    private double specularExponent;
    private Light light;
    private BumpMap bumpMap;
    private double scaleX;
    private double scaleY;
    private Rectangle litRegion;
    private boolean linear;

    public SpecularLightingRed(double ks, double specularExponent, Light light, BumpMap bumpMap, Rectangle litRegion, double scaleX, double scaleY, boolean linear) {
        this.ks = ks;
        this.specularExponent = specularExponent;
        this.light = light;
        this.bumpMap = bumpMap;
        this.litRegion = litRegion;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.linear = linear;
        ColorModel cm = linear ? GraphicsUtil.Linear_sRGB_Unpre : GraphicsUtil.sRGB_Unpre;
        int tw = litRegion.width;
        int th = litRegion.height;
        int defSz = AbstractTiledRed.getDefaultTileSize();
        if (tw > defSz) {
            tw = defSz;
        }
        if (th > defSz) {
            th = defSz;
        }
        SampleModel sm = cm.createCompatibleSampleModel(tw, th);
        this.init((CachableRed)null, litRegion, cm, sm, litRegion.x, litRegion.y, null);
    }

    @Override
    public WritableRaster copyData(WritableRaster wr) {
        this.copyToRaster(wr);
        return wr;
    }

    @Override
    public void genRect(WritableRaster wr) {
        double scaleX = this.scaleX;
        double scaleY = this.scaleY;
        double[] lightColor = this.light.getColor(this.linear);
        int w = wr.getWidth();
        int h = wr.getHeight();
        int minX = wr.getMinX();
        int minY = wr.getMinY();
        DataBufferInt db = (DataBufferInt)wr.getDataBuffer();
        int[] pixels = db.getBankData()[0];
        SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)wr.getSampleModel();
        int offset = db.getOffset() + sppsm.getOffset(minX - wr.getSampleModelTranslateX(), minY - wr.getSampleModelTranslateY());
        int scanStride = sppsm.getScanlineStride();
        int adjust = scanStride - w;
        int p = offset;
        int a = 0;
        int i = 0;
        int j = 0;
        double x = scaleX * (double)minX;
        double y = scaleY * (double)minY;
        double norm = 0.0;
        int pixel = 0;
        double mult = lightColor[0] > lightColor[1] ? lightColor[0] : lightColor[1];
        mult = mult > lightColor[2] ? mult : lightColor[2];
        double scale = 255.0 / mult;
        pixel = (int)(lightColor[0] * scale + 0.5);
        int tmp = (int)(lightColor[1] * scale + 0.5);
        pixel = pixel << 8 | tmp;
        tmp = (int)(lightColor[2] * scale + 0.5);
        pixel = pixel << 8 | tmp;
        mult *= 255.0 * this.ks;
        double[][][] NA = this.bumpMap.getNormalArray(minX, minY, w, h);
        if (this.light instanceof SpotLight) {
            SpotLight slight = (SpotLight)this.light;
            double[][] LA = new double[w][4];
            for (i = 0; i < h; ++i) {
                double[][] NR = NA[i];
                slight.getLightRow4(x, y + (double)i * scaleY, scaleX, w, NR, LA);
                for (j = 0; j < w; ++j) {
                    double[] N = NR[j];
                    double[] L = LA[j];
                    double vs = L[3];
                    if (vs == 0.0) {
                        a = 0;
                    } else {
                        L[2] = L[2] + 1.0;
                        norm = L[0] * L[0] + L[1] * L[1] + L[2] * L[2];
                        double dot = N[0] * L[0] + N[1] * L[1] + N[2] * L[2];
                        a = (int)(mult * (vs *= Math.pow(dot / (norm = Math.sqrt(norm)), this.specularExponent)) + 0.5);
                        if ((a & 0xFFFFFF00) != 0) {
                            a = (a & Integer.MIN_VALUE) != 0 ? 0 : 255;
                        }
                    }
                    pixels[p++] = a << 24 | pixel;
                }
                p += adjust;
            }
        } else if (!this.light.isConstant()) {
            double[][] LA = new double[w][4];
            for (i = 0; i < h; ++i) {
                double[][] NR = NA[i];
                this.light.getLightRow(x, y + (double)i * scaleY, scaleX, w, NR, LA);
                for (j = 0; j < w; ++j) {
                    double[] N = NR[j];
                    double[] L = LA[j];
                    L[2] = L[2] + 1.0;
                    norm = L[0] * L[0] + L[1] * L[1] + L[2] * L[2];
                    norm = Math.sqrt(norm);
                    double dot = N[0] * L[0] + N[1] * L[1] + N[2] * L[2];
                    a = (int)(mult * (norm = Math.pow(dot / norm, this.specularExponent)) + 0.5);
                    if ((a & 0xFFFFFF00) != 0) {
                        a = (a & Integer.MIN_VALUE) != 0 ? 0 : 255;
                    }
                    pixels[p++] = a << 24 | pixel;
                }
                p += adjust;
            }
        } else {
            double[] L = new double[3];
            this.light.getLight(0.0, 0.0, 0.0, L);
            L[2] = L[2] + 1.0;
            norm = Math.sqrt(L[0] * L[0] + L[1] * L[1] + L[2] * L[2]);
            if (norm > 0.0) {
                L[0] = L[0] / norm;
                L[1] = L[1] / norm;
                L[2] = L[2] / norm;
            }
            for (i = 0; i < h; ++i) {
                double[][] NR = NA[i];
                for (j = 0; j < w; ++j) {
                    double[] N = NR[j];
                    a = (int)(mult * Math.pow(N[0] * L[0] + N[1] * L[1] + N[2] * L[2], this.specularExponent) + 0.5);
                    if ((a & 0xFFFFFF00) != 0) {
                        a = (a & Integer.MIN_VALUE) != 0 ? 0 : 255;
                    }
                    pixels[p++] = a << 24 | pixel;
                }
                p += adjust;
            }
        }
    }
}

