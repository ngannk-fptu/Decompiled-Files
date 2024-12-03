/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.rendered;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.BandCombineOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import org.apache.xmlgraphics.image.GraphicsUtil;
import org.apache.xmlgraphics.image.rendered.AbstractRed;
import org.apache.xmlgraphics.image.rendered.Any2sRGBRed;
import org.apache.xmlgraphics.image.rendered.CachableRed;

public class Any2LsRGBRed
extends AbstractRed {
    boolean srcIssRGB;
    private static final double GAMMA = 2.4;
    private static final double LFACT = 0.07739938080495357;
    private static final int[] sRGBToLsRGBLut = new int[256];

    public Any2LsRGBRed(CachableRed src) {
        super(src, src.getBounds(), Any2LsRGBRed.fixColorModel(src), Any2LsRGBRed.fixSampleModel(src), src.getTileGridXOffset(), src.getTileGridYOffset(), null);
        ColorModel srcCM = src.getColorModel();
        if (srcCM == null) {
            return;
        }
        ColorSpace srcCS = srcCM.getColorSpace();
        if (srcCS == ColorSpace.getInstance(1000)) {
            this.srcIssRGB = true;
        }
    }

    public static final double sRGBToLsRGB(double value) {
        if (value <= 0.003928) {
            return value * 0.07739938080495357;
        }
        return Math.pow((value + 0.055) / 1.055, 2.4);
    }

    @Override
    public WritableRaster copyData(WritableRaster wr) {
        CachableRed src = (CachableRed)this.getSources().get(0);
        ColorModel srcCM = src.getColorModel();
        SampleModel srcSM = src.getSampleModel();
        if (this.srcIssRGB && Any2sRGBRed.is_INT_PACK_COMP(wr.getSampleModel())) {
            src.copyData(wr);
            if (srcCM.hasAlpha()) {
                GraphicsUtil.coerceData(wr, srcCM, false);
            }
            Any2sRGBRed.applyLut_INT(wr, sRGBToLsRGBLut);
            return wr;
        }
        if (srcCM == null) {
            float[][] matrix = null;
            switch (srcSM.getNumBands()) {
                case 1: {
                    matrix = new float[1][3];
                    matrix[0][0] = 1.0f;
                    matrix[0][1] = 1.0f;
                    matrix[0][2] = 1.0f;
                    break;
                }
                case 2: {
                    matrix = new float[2][4];
                    matrix[0][0] = 1.0f;
                    matrix[0][1] = 1.0f;
                    matrix[0][2] = 1.0f;
                    matrix[1][3] = 1.0f;
                    break;
                }
                case 3: {
                    matrix = new float[3][3];
                    matrix[0][0] = 1.0f;
                    matrix[1][1] = 1.0f;
                    matrix[2][2] = 1.0f;
                    break;
                }
                default: {
                    matrix = new float[srcSM.getNumBands()][4];
                    matrix[0][0] = 1.0f;
                    matrix[1][1] = 1.0f;
                    matrix[2][2] = 1.0f;
                    matrix[3][3] = 1.0f;
                }
            }
            Raster srcRas = src.getData(wr.getBounds());
            BandCombineOp op = new BandCombineOp(matrix, null);
            op.filter(srcRas, wr);
        } else {
            WritableRaster srcWr;
            BufferedImage dstBI;
            ColorModel dstCM = this.getColorModel();
            if (!dstCM.hasAlpha()) {
                dstBI = new BufferedImage(dstCM, wr.createWritableTranslatedChild(0, 0), dstCM.isAlphaPremultiplied(), null);
            } else {
                SinglePixelPackedSampleModel dstSM = (SinglePixelPackedSampleModel)wr.getSampleModel();
                int[] masks = dstSM.getBitMasks();
                SinglePixelPackedSampleModel dstSMNoA = new SinglePixelPackedSampleModel(dstSM.getDataType(), dstSM.getWidth(), dstSM.getHeight(), dstSM.getScanlineStride(), new int[]{masks[0], masks[1], masks[2]});
                ColorModel dstCMNoA = GraphicsUtil.Linear_sRGB;
                WritableRaster dstWr = Raster.createWritableRaster(dstSMNoA, wr.getDataBuffer(), new Point(0, 0));
                dstWr = dstWr.createWritableChild(wr.getMinX() - wr.getSampleModelTranslateX(), wr.getMinY() - wr.getSampleModelTranslateY(), wr.getWidth(), wr.getHeight(), 0, 0, null);
                dstBI = new BufferedImage(dstCMNoA, dstWr, false, null);
            }
            ColorModel srcBICM = srcCM;
            if (srcCM.hasAlpha() && srcCM.isAlphaPremultiplied()) {
                Rectangle wrR = wr.getBounds();
                SampleModel sm = srcCM.createCompatibleSampleModel(wrR.width, wrR.height);
                srcWr = Raster.createWritableRaster(sm, new Point(wrR.x, wrR.y));
                src.copyData(srcWr);
                srcBICM = GraphicsUtil.coerceData(srcWr, srcCM, false);
            } else {
                Raster srcRas = src.getData(wr.getBounds());
                srcWr = GraphicsUtil.makeRasterWritable(srcRas);
            }
            BufferedImage srcBI = new BufferedImage(srcBICM, srcWr.createWritableTranslatedChild(0, 0), false, null);
            ColorConvertOp op = new ColorConvertOp(null);
            op.filter(srcBI, dstBI);
            if (dstCM.hasAlpha()) {
                Any2LsRGBRed.copyBand(srcWr, srcSM.getNumBands() - 1, wr, this.getSampleModel().getNumBands() - 1);
            }
        }
        return wr;
    }

    protected static ColorModel fixColorModel(CachableRed src) {
        ColorModel cm = src.getColorModel();
        if (cm != null) {
            if (cm.hasAlpha()) {
                return GraphicsUtil.Linear_sRGB_Unpre;
            }
            return GraphicsUtil.Linear_sRGB;
        }
        SampleModel sm = src.getSampleModel();
        switch (sm.getNumBands()) {
            case 1: {
                return GraphicsUtil.Linear_sRGB;
            }
            case 2: {
                return GraphicsUtil.Linear_sRGB_Unpre;
            }
            case 3: {
                return GraphicsUtil.Linear_sRGB;
            }
        }
        return GraphicsUtil.Linear_sRGB_Unpre;
    }

    protected static SampleModel fixSampleModel(CachableRed src) {
        SampleModel sm = src.getSampleModel();
        ColorModel cm = src.getColorModel();
        boolean alpha = false;
        if (cm != null) {
            alpha = cm.hasAlpha();
        } else {
            switch (sm.getNumBands()) {
                case 1: 
                case 3: {
                    alpha = false;
                    break;
                }
                default: {
                    alpha = true;
                }
            }
        }
        if (alpha) {
            return new SinglePixelPackedSampleModel(3, sm.getWidth(), sm.getHeight(), new int[]{0xFF0000, 65280, 255, -16777216});
        }
        return new SinglePixelPackedSampleModel(3, sm.getWidth(), sm.getHeight(), new int[]{0xFF0000, 65280, 255});
    }

    static {
        double scale = 0.00392156862745098;
        for (int i = 0; i < 256; ++i) {
            double value = Any2LsRGBRed.sRGBToLsRGB((double)i * 0.00392156862745098);
            Any2LsRGBRed.sRGBToLsRGBLut[i] = (int)Math.round(value * 255.0);
        }
    }
}

