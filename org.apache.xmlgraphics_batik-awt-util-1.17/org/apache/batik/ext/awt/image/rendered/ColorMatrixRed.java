/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.rendered;

import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.rendered.AbstractRed;
import org.apache.batik.ext.awt.image.rendered.CachableRed;

public class ColorMatrixRed
extends AbstractRed {
    private float[][] matrix;

    public float[][] getMatrix() {
        return this.copyMatrix(this.matrix);
    }

    public void setMatrix(float[][] matrix) {
        float[][] tmp = this.copyMatrix(matrix);
        if (tmp == null) {
            throw new IllegalArgumentException();
        }
        if (tmp.length != 4) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < 4; ++i) {
            if (tmp[i].length == 5) continue;
            throw new IllegalArgumentException(String.valueOf(i) + " : " + tmp[i].length);
        }
        this.matrix = matrix;
    }

    private float[][] copyMatrix(float[][] m) {
        if (m == null) {
            return null;
        }
        float[][] cm = new float[m.length][];
        for (int i = 0; i < m.length; ++i) {
            if (m[i] == null) continue;
            cm[i] = new float[m[i].length];
            System.arraycopy(m[i], 0, cm[i], 0, m[i].length);
        }
        return cm;
    }

    public ColorMatrixRed(CachableRed src, float[][] matrix) {
        this.setMatrix(matrix);
        ColorModel srcCM = src.getColorModel();
        ColorSpace srcCS = null;
        if (srcCM != null) {
            srcCS = srcCM.getColorSpace();
        }
        ColorModel cm = srcCS == null ? GraphicsUtil.Linear_sRGB_Unpre : (srcCS == ColorSpace.getInstance(1004) ? GraphicsUtil.Linear_sRGB_Unpre : GraphicsUtil.sRGB_Unpre);
        SampleModel sm = cm.createCompatibleSampleModel(src.getWidth(), src.getHeight());
        this.init(src, src.getBounds(), cm, sm, src.getTileGridXOffset(), src.getTileGridYOffset(), null);
    }

    @Override
    public WritableRaster copyData(WritableRaster wr) {
        CachableRed src = (CachableRed)this.getSources().get(0);
        wr = src.copyData(wr);
        ColorModel cm = src.getColorModel();
        GraphicsUtil.coerceData(wr, cm, false);
        int minX = wr.getMinX();
        int minY = wr.getMinY();
        int w = wr.getWidth();
        int h = wr.getHeight();
        DataBufferInt dbf = (DataBufferInt)wr.getDataBuffer();
        int[] pixels = dbf.getBankData()[0];
        SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)wr.getSampleModel();
        int offset = dbf.getOffset() + sppsm.getOffset(minX - wr.getSampleModelTranslateX(), minY - wr.getSampleModelTranslateY());
        int scanStride = ((SinglePixelPackedSampleModel)wr.getSampleModel()).getScanlineStride();
        int adjust = scanStride - w;
        int p = offset;
        int i = 0;
        int j = 0;
        float a00 = this.matrix[0][0] / 255.0f;
        float a01 = this.matrix[0][1] / 255.0f;
        float a02 = this.matrix[0][2] / 255.0f;
        float a03 = this.matrix[0][3] / 255.0f;
        float a04 = this.matrix[0][4] / 255.0f;
        float a10 = this.matrix[1][0] / 255.0f;
        float a11 = this.matrix[1][1] / 255.0f;
        float a12 = this.matrix[1][2] / 255.0f;
        float a13 = this.matrix[1][3] / 255.0f;
        float a14 = this.matrix[1][4] / 255.0f;
        float a20 = this.matrix[2][0] / 255.0f;
        float a21 = this.matrix[2][1] / 255.0f;
        float a22 = this.matrix[2][2] / 255.0f;
        float a23 = this.matrix[2][3] / 255.0f;
        float a24 = this.matrix[2][4] / 255.0f;
        float a30 = this.matrix[3][0] / 255.0f;
        float a31 = this.matrix[3][1] / 255.0f;
        float a32 = this.matrix[3][2] / 255.0f;
        float a33 = this.matrix[3][3] / 255.0f;
        float a34 = this.matrix[3][4] / 255.0f;
        for (i = 0; i < h; ++i) {
            for (j = 0; j < w; ++j) {
                int pel = pixels[p];
                int a = pel >>> 24;
                int r = pel >> 16 & 0xFF;
                int g = pel >> 8 & 0xFF;
                int b = pel & 0xFF;
                int dr = (int)((a00 * (float)r + a01 * (float)g + a02 * (float)b + a03 * (float)a + a04) * 255.0f);
                int dg = (int)((a10 * (float)r + a11 * (float)g + a12 * (float)b + a13 * (float)a + a14) * 255.0f);
                int db = (int)((a20 * (float)r + a21 * (float)g + a22 * (float)b + a23 * (float)a + a24) * 255.0f);
                int da = (int)((a30 * (float)r + a31 * (float)g + a32 * (float)b + a33 * (float)a + a34) * 255.0f);
                if ((dr & 0xFFFFFF00) != 0) {
                    int n = dr = (dr & Integer.MIN_VALUE) != 0 ? 0 : 255;
                }
                if ((dg & 0xFFFFFF00) != 0) {
                    int n = dg = (dg & Integer.MIN_VALUE) != 0 ? 0 : 255;
                }
                if ((db & 0xFFFFFF00) != 0) {
                    int n = db = (db & Integer.MIN_VALUE) != 0 ? 0 : 255;
                }
                if ((da & 0xFFFFFF00) != 0) {
                    da = (da & Integer.MIN_VALUE) != 0 ? 0 : 255;
                }
                pixels[p++] = da << 24 | dr << 16 | dg << 8 | db;
            }
            p += adjust;
        }
        return wr;
    }
}

