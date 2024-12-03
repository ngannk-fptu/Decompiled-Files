/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.util;

import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.JaiI18N;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

public final class JDKWorkarounds {
    private JDKWorkarounds() {
    }

    private static boolean setRectBilevel(WritableRaster dstRaster, Raster srcRaster, int dx, int dy) {
        int width = srcRaster.getWidth();
        int height = srcRaster.getHeight();
        int srcOffX = srcRaster.getMinX();
        int srcOffY = srcRaster.getMinY();
        int dstOffX = dx + srcOffX;
        int dstOffY = dy + srcOffY;
        int dminX = dstRaster.getMinX();
        int dminY = dstRaster.getMinY();
        int dwidth = dstRaster.getWidth();
        int dheight = dstRaster.getHeight();
        if (dstOffX + width > dminX + dwidth) {
            width = dminX + dwidth - dstOffX;
        }
        if (dstOffY + height > dminY + dheight) {
            height = dminY + dheight - dstOffY;
        }
        Rectangle rect = new Rectangle(dstOffX, dstOffY, width, height);
        byte[] binaryData = ImageUtil.getPackedBinaryData(srcRaster, rect);
        ImageUtil.setPackedBinaryData(binaryData, dstRaster, rect);
        return true;
    }

    public static void setRect(WritableRaster dstRaster, Raster srcRaster) {
        JDKWorkarounds.setRect(dstRaster, srcRaster, 0, 0);
    }

    public static void setRect(WritableRaster dstRaster, Raster srcRaster, int dx, int dy) {
        int dataType;
        SampleModel srcSampleModel = srcRaster.getSampleModel();
        SampleModel dstSampleModel = dstRaster.getSampleModel();
        if (srcSampleModel instanceof MultiPixelPackedSampleModel && dstSampleModel instanceof MultiPixelPackedSampleModel) {
            MultiPixelPackedSampleModel srcMPPSM = (MultiPixelPackedSampleModel)srcSampleModel;
            MultiPixelPackedSampleModel dstMPPSM = (MultiPixelPackedSampleModel)dstSampleModel;
            DataBuffer srcDB = srcRaster.getDataBuffer();
            DataBuffer dstDB = srcRaster.getDataBuffer();
            if (srcDB instanceof DataBufferByte && dstDB instanceof DataBufferByte && srcMPPSM.getPixelBitStride() == 1 && dstMPPSM.getPixelBitStride() == 1 && JDKWorkarounds.setRectBilevel(dstRaster, srcRaster, dx, dy)) {
                return;
            }
        }
        if ((dataType = dstRaster.getSampleModel().getDataType()) != 4 && dataType != 5) {
            dstRaster.setRect(dx, dy, srcRaster);
            return;
        }
        int width = srcRaster.getWidth();
        int height = srcRaster.getHeight();
        int srcOffX = srcRaster.getMinX();
        int srcOffY = srcRaster.getMinY();
        int dstOffX = dx + srcOffX;
        int dstOffY = dy + srcOffY;
        int dminX = dstRaster.getMinX();
        int dminY = dstRaster.getMinY();
        int dwidth = dstRaster.getWidth();
        int dheight = dstRaster.getHeight();
        if (dstOffX + width > dminX + dwidth) {
            width = dminX + dwidth - dstOffX;
        }
        if (dstOffY + height > dminY + dheight) {
            height = dminY + dheight - dstOffY;
        }
        switch (srcRaster.getSampleModel().getDataType()) {
            case 0: 
            case 1: 
            case 2: 
            case 3: {
                int[] iData = null;
                for (int startY = 0; startY < height; ++startY) {
                    iData = srcRaster.getPixels(srcOffX, srcOffY + startY, width, 1, iData);
                    dstRaster.setPixels(dstOffX, dstOffY + startY, width, 1, iData);
                }
                break;
            }
            case 4: {
                float[] fData = null;
                for (int startY = 0; startY < height; ++startY) {
                    fData = srcRaster.getPixels(srcOffX, srcOffY + startY, width, 1, fData);
                    dstRaster.setPixels(dstOffX, dstOffY + startY, width, 1, fData);
                }
                break;
            }
            case 5: {
                double[] dData = null;
                for (int startY = 0; startY < height; ++startY) {
                    dData = srcRaster.getPixels(srcOffX, srcOffY + startY, width, 1, dData);
                    dstRaster.setPixels(dstOffX, dstOffY + startY, width, 1, dData);
                }
                break;
            }
        }
    }

    public static boolean areCompatibleDataModels(SampleModel sm, ColorModel cm) {
        if (sm == null || cm == null) {
            throw new IllegalArgumentException(JaiI18N.getString("JDKWorkarounds0"));
        }
        if (!cm.isCompatibleSampleModel(sm)) {
            return false;
        }
        if (cm instanceof ComponentColorModel) {
            int numBands = sm.getNumBands();
            if (numBands != cm.getNumComponents()) {
                return false;
            }
            for (int b = 0; b < numBands; ++b) {
                if (sm.getSampleSize(b) >= cm.getComponentSize(b)) continue;
                return false;
            }
        }
        return true;
    }
}

