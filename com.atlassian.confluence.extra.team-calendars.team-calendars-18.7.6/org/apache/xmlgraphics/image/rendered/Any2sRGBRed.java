/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.rendered;

import java.awt.color.ColorSpace;
import java.awt.image.BandCombineOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import org.apache.xmlgraphics.image.GraphicsUtil;
import org.apache.xmlgraphics.image.rendered.AbstractRed;
import org.apache.xmlgraphics.image.rendered.CachableRed;

public class Any2sRGBRed
extends AbstractRed {
    boolean srcIsLsRGB;
    private static final double GAMMA = 2.4;
    private static final int[] linearToSRGBLut = new int[256];

    public Any2sRGBRed(CachableRed src) {
        super(src, src.getBounds(), Any2sRGBRed.fixColorModel(src), Any2sRGBRed.fixSampleModel(src), src.getTileGridXOffset(), src.getTileGridYOffset(), null);
        ColorModel srcCM = src.getColorModel();
        if (srcCM == null) {
            return;
        }
        ColorSpace srcCS = srcCM.getColorSpace();
        if (srcCS == ColorSpace.getInstance(1004)) {
            this.srcIsLsRGB = true;
        }
    }

    public static boolean is_INT_PACK_COMP(SampleModel sm) {
        if (!(sm instanceof SinglePixelPackedSampleModel)) {
            return false;
        }
        if (sm.getDataType() != 3) {
            return false;
        }
        SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)sm;
        int[] masks = sppsm.getBitMasks();
        if (masks.length != 3 && masks.length != 4) {
            return false;
        }
        if (masks[0] != 0xFF0000) {
            return false;
        }
        if (masks[1] != 65280) {
            return false;
        }
        if (masks[2] != 255) {
            return false;
        }
        return masks.length != 4 || masks[3] == -16777216;
    }

    public static WritableRaster applyLut_INT(WritableRaster wr, int[] lut) {
        SinglePixelPackedSampleModel sm = (SinglePixelPackedSampleModel)wr.getSampleModel();
        DataBufferInt db = (DataBufferInt)wr.getDataBuffer();
        int srcBase = db.getOffset() + sm.getOffset(wr.getMinX() - wr.getSampleModelTranslateX(), wr.getMinY() - wr.getSampleModelTranslateY());
        int[] pixels = db.getBankData()[0];
        int width = wr.getWidth();
        int height = wr.getHeight();
        int scanStride = sm.getScanlineStride();
        for (int y = 0; y < height; ++y) {
            int sp;
            int end = sp + width;
            for (sp = srcBase + y * scanStride; sp < end; ++sp) {
                int pix = pixels[sp];
                pixels[sp] = pix & 0xFF000000 | lut[pix >>> 16 & 0xFF] << 16 | lut[pix >>> 8 & 0xFF] << 8 | lut[pix & 0xFF];
            }
        }
        return wr;
    }

    @Override
    public WritableRaster copyData(WritableRaster wr) {
        CachableRed src = (CachableRed)this.getSources().get(0);
        ColorModel srcCM = src.getColorModel();
        SampleModel srcSM = src.getSampleModel();
        if (this.srcIsLsRGB && Any2sRGBRed.is_INT_PACK_COMP(wr.getSampleModel())) {
            src.copyData(wr);
            if (srcCM.hasAlpha()) {
                GraphicsUtil.coerceData(wr, srcCM, false);
            }
            Any2sRGBRed.applyLut_INT(wr, linearToSRGBLut);
            return wr;
        }
        if (srcCM == null) {
            float[][] matrix = null;
            switch (srcSM.getNumBands()) {
                case 1: {
                    matrix = new float[3][1];
                    matrix[0][0] = 1.0f;
                    matrix[1][0] = 1.0f;
                    matrix[2][0] = 1.0f;
                    break;
                }
                case 2: {
                    matrix = new float[4][2];
                    matrix[0][0] = 1.0f;
                    matrix[1][0] = 1.0f;
                    matrix[3][0] = 1.0f;
                    matrix[3][1] = 1.0f;
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
                    matrix = new float[4][srcSM.getNumBands()];
                    matrix[0][0] = 1.0f;
                    matrix[1][1] = 1.0f;
                    matrix[2][2] = 1.0f;
                    matrix[3][3] = 1.0f;
                }
            }
            Raster srcRas = src.getData(wr.getBounds());
            BandCombineOp op = new BandCombineOp(matrix, null);
            op.filter(srcRas, wr);
            return wr;
        }
        if (srcCM.getColorSpace() == ColorSpace.getInstance(1003)) {
            try {
                float[][] matrix = null;
                switch (srcSM.getNumBands()) {
                    case 1: {
                        matrix = new float[3][1];
                        matrix[0][0] = 1.0f;
                        matrix[1][0] = 1.0f;
                        matrix[2][0] = 1.0f;
                        break;
                    }
                    default: {
                        matrix = new float[4][2];
                        matrix[0][0] = 1.0f;
                        matrix[1][0] = 1.0f;
                        matrix[3][0] = 1.0f;
                        matrix[4][1] = 1.0f;
                    }
                }
                Raster srcRas = src.getData(wr.getBounds());
                BandCombineOp op = new BandCombineOp(matrix, null);
                op.filter(srcRas, wr);
            }
            catch (Throwable t) {
                t.printStackTrace();
            }
            return wr;
        }
        ColorModel dstCM = this.getColorModel();
        if (srcCM.getColorSpace() == dstCM.getColorSpace()) {
            if (Any2sRGBRed.is_INT_PACK_COMP(srcSM)) {
                src.copyData(wr);
            } else {
                GraphicsUtil.copyData(src.getData(wr.getBounds()), wr);
            }
            return wr;
        }
        Raster srcRas = src.getData(wr.getBounds());
        assert (srcRas instanceof WritableRaster);
        WritableRaster srcWr = (WritableRaster)srcRas;
        ColorModel srcBICM = srcCM;
        if (srcCM.hasAlpha()) {
            srcBICM = GraphicsUtil.coerceData(srcWr, srcCM, false);
        }
        BufferedImage srcBI = new BufferedImage(srcBICM, srcWr.createWritableTranslatedChild(0, 0), false, null);
        ColorConvertOp op = new ColorConvertOp(dstCM.getColorSpace(), null);
        BufferedImage dstBI = op.filter(srcBI, null);
        WritableRaster wr00 = wr.createWritableTranslatedChild(0, 0);
        for (int i = 0; i < dstCM.getColorSpace().getNumComponents(); ++i) {
            Any2sRGBRed.copyBand(dstBI.getRaster(), i, wr00, i);
        }
        if (dstCM.hasAlpha()) {
            Any2sRGBRed.copyBand(srcWr, srcSM.getNumBands() - 1, wr, this.getSampleModel().getNumBands() - 1);
        }
        return wr;
    }

    protected static ColorModel fixColorModel(CachableRed src) {
        ColorModel cm = src.getColorModel();
        if (cm != null) {
            if (cm.hasAlpha()) {
                return GraphicsUtil.sRGB_Unpre;
            }
            return GraphicsUtil.sRGB;
        }
        SampleModel sm = src.getSampleModel();
        switch (sm.getNumBands()) {
            case 1: {
                return GraphicsUtil.sRGB;
            }
            case 2: {
                return GraphicsUtil.sRGB_Unpre;
            }
            case 3: {
                return GraphicsUtil.sRGB;
            }
        }
        return GraphicsUtil.sRGB_Unpre;
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
        double exp = 0.4166666666666667;
        for (int i = 0; i < 256; ++i) {
            double value = (double)i * 0.00392156862745098;
            value = value <= 0.0031308 ? (value *= 12.92) : 1.055 * Math.pow(value, 0.4166666666666667) - 0.055;
            Any2sRGBRed.linearToSRGBLut[i] = (int)Math.round(value * 255.0);
        }
    }
}

