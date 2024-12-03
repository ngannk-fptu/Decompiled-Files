/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.rendered;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.image.BandCombineOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import org.apache.batik.ext.awt.ColorSpaceHintKey;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.rendered.AbstractRed;
import org.apache.batik.ext.awt.image.rendered.CachableRed;

public class Any2LumRed
extends AbstractRed {
    boolean isColorConvertOpAplhaSupported = Any2LumRed.getColorConvertOpAplhaSupported();

    public Any2LumRed(CachableRed src) {
        super(src, src.getBounds(), Any2LumRed.fixColorModel(src), Any2LumRed.fixSampleModel(src), src.getTileGridXOffset(), src.getTileGridYOffset(), null);
        this.props.put("org.apache.batik.gvt.filter.Colorspace", ColorSpaceHintKey.VALUE_COLORSPACE_GREY);
    }

    @Override
    public WritableRaster copyData(WritableRaster wr) {
        CachableRed src = (CachableRed)this.getSources().get(0);
        SampleModel sm = src.getSampleModel();
        ColorModel srcCM = src.getColorModel();
        Raster srcRas = src.getData(wr.getBounds());
        if (srcCM == null) {
            float[][] matrix = null;
            if (sm.getNumBands() == 2) {
                matrix = new float[2][2];
                matrix[0][0] = 1.0f;
                matrix[1][1] = 1.0f;
            } else {
                matrix = new float[sm.getNumBands()][1];
                matrix[0][0] = 1.0f;
            }
            BandCombineOp op = new BandCombineOp(matrix, null);
            op.filter(srcRas, wr);
        } else {
            BufferedImage dstBI;
            WritableRaster srcWr = (WritableRaster)srcRas;
            if (srcCM.hasAlpha()) {
                GraphicsUtil.coerceData(srcWr, srcCM, false);
            }
            BufferedImage srcBI = new BufferedImage(srcCM, srcWr.createWritableTranslatedChild(0, 0), false, null);
            ColorModel dstCM = this.getColorModel();
            if (dstCM.hasAlpha() && !this.isColorConvertOpAplhaSupported) {
                PixelInterleavedSampleModel dstSM = (PixelInterleavedSampleModel)wr.getSampleModel();
                PixelInterleavedSampleModel smna = new PixelInterleavedSampleModel(dstSM.getDataType(), dstSM.getWidth(), dstSM.getHeight(), dstSM.getPixelStride(), dstSM.getScanlineStride(), new int[]{0});
                WritableRaster dstWr = Raster.createWritableRaster(smna, wr.getDataBuffer(), new Point(0, 0));
                dstWr = dstWr.createWritableChild(wr.getMinX() - wr.getSampleModelTranslateX(), wr.getMinY() - wr.getSampleModelTranslateY(), wr.getWidth(), wr.getHeight(), 0, 0, null);
                ComponentColorModel cmna = new ComponentColorModel(ColorSpace.getInstance(1003), new int[]{8}, false, false, 1, 0);
                dstBI = new BufferedImage(cmna, dstWr, false, null);
            } else {
                dstBI = new BufferedImage(dstCM, wr.createWritableTranslatedChild(0, 0), dstCM.isAlphaPremultiplied(), null);
            }
            ColorConvertOp op = new ColorConvertOp(null);
            op.filter(srcBI, dstBI);
            if (dstCM.hasAlpha()) {
                Any2LumRed.copyBand(srcWr, sm.getNumBands() - 1, wr, this.getSampleModel().getNumBands() - 1);
                if (dstCM.isAlphaPremultiplied()) {
                    GraphicsUtil.multiplyAlpha(wr);
                }
            }
        }
        return wr;
    }

    protected static ColorModel fixColorModel(CachableRed src) {
        ColorModel cm = src.getColorModel();
        if (cm != null) {
            if (cm.hasAlpha()) {
                return new ComponentColorModel(ColorSpace.getInstance(1003), new int[]{8, 8}, true, cm.isAlphaPremultiplied(), 3, 0);
            }
            return new ComponentColorModel(ColorSpace.getInstance(1003), new int[]{8}, false, false, 1, 0);
        }
        SampleModel sm = src.getSampleModel();
        if (sm.getNumBands() == 2) {
            return new ComponentColorModel(ColorSpace.getInstance(1003), new int[]{8, 8}, true, true, 3, 0);
        }
        return new ComponentColorModel(ColorSpace.getInstance(1003), new int[]{8}, false, false, 1, 0);
    }

    protected static SampleModel fixSampleModel(CachableRed src) {
        SampleModel sm = src.getSampleModel();
        int width = sm.getWidth();
        int height = sm.getHeight();
        ColorModel cm = src.getColorModel();
        if (cm != null) {
            if (cm.hasAlpha()) {
                return new PixelInterleavedSampleModel(0, width, height, 2, 2 * width, new int[]{0, 1});
            }
            return new PixelInterleavedSampleModel(0, width, height, 1, width, new int[]{0});
        }
        if (sm.getNumBands() == 2) {
            return new PixelInterleavedSampleModel(0, width, height, 2, 2 * width, new int[]{0, 1});
        }
        return new PixelInterleavedSampleModel(0, width, height, 1, width, new int[]{0});
    }

    protected static boolean getColorConvertOpAplhaSupported() {
        int size = 50;
        BufferedImage srcImage = new BufferedImage(size, size, 2);
        Graphics2D srcGraphics = srcImage.createGraphics();
        srcGraphics.setColor(Color.red);
        srcGraphics.fillRect(0, 0, size, size);
        srcGraphics.dispose();
        BufferedImage dstImage = new BufferedImage(size, size, 2);
        Graphics2D dstGraphics = dstImage.createGraphics();
        dstGraphics.setComposite(AlphaComposite.Clear);
        dstGraphics.fillRect(0, 0, size, size);
        dstGraphics.dispose();
        ColorSpace grayColorSpace = ColorSpace.getInstance(1003);
        ColorConvertOp op = new ColorConvertOp(grayColorSpace, null);
        op.filter(srcImage, dstImage);
        return Any2LumRed.getAlpha(srcImage) == Any2LumRed.getAlpha(dstImage);
    }

    protected static int getAlpha(BufferedImage bufferedImage) {
        int x = bufferedImage.getWidth() / 2;
        int y = bufferedImage.getHeight() / 2;
        return 0xFF & bufferedImage.getRGB(x, y) >> 24;
    }
}

