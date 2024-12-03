/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codecimpl;

import com.sun.media.jai.codec.ImageEncodeParam;
import com.sun.media.jai.codec.ImageEncoderImpl;
import com.sun.media.jai.codecimpl.JaiI18N;
import java.awt.Point;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.OutputStream;

final class WBMPImageEncoder
extends ImageEncoderImpl {
    private static int getNumBits(int intValue) {
        int numBits = 32;
        for (int mask = Integer.MIN_VALUE; mask != 0 && (intValue & mask) == 0; mask >>>= 1) {
            --numBits;
        }
        return numBits;
    }

    private static byte[] intToMultiByte(int intValue) {
        int numBitsLeft = WBMPImageEncoder.getNumBits(intValue);
        byte[] multiBytes = new byte[(numBitsLeft + 6) / 7];
        int maxIndex = multiBytes.length - 1;
        for (int b = 0; b <= maxIndex; ++b) {
            multiBytes[b] = (byte)(intValue >>> (maxIndex - b) * 7 & 0x7F);
            if (b == maxIndex) continue;
            int n = b;
            multiBytes[n] = (byte)(multiBytes[n] | 0xFFFFFF80);
        }
        return multiBytes;
    }

    public WBMPImageEncoder(OutputStream output, ImageEncodeParam param) {
        super(output, param);
    }

    public void encode(RenderedImage im) throws IOException {
        SampleModel sm = im.getSampleModel();
        int dataType = sm.getTransferType();
        if (dataType == 4 || dataType == 5) {
            throw new IllegalArgumentException(JaiI18N.getString("WBMPImageEncoder0"));
        }
        if (sm.getNumBands() != 1) {
            throw new IllegalArgumentException(JaiI18N.getString("WBMPImageEncoder1"));
        }
        if (sm.getSampleSize(0) != 1) {
            throw new IllegalArgumentException(JaiI18N.getString("WBMPImageEncoder2"));
        }
        int width = im.getWidth();
        int height = im.getHeight();
        this.output.write(0);
        this.output.write(0);
        this.output.write(WBMPImageEncoder.intToMultiByte(width));
        this.output.write(WBMPImageEncoder.intToMultiByte(height));
        Raster tile = null;
        if (sm.getDataType() != 0 || !(sm instanceof MultiPixelPackedSampleModel) || ((MultiPixelPackedSampleModel)sm).getDataBitOffset() != 0) {
            MultiPixelPackedSampleModel mppsm = new MultiPixelPackedSampleModel(0, width, height, 1, (width + 7) / 8, 0);
            WritableRaster raster = Raster.createWritableRaster(mppsm, new Point(im.getMinX(), im.getMinY()));
            raster.setRect(im.getData());
            tile = raster;
        } else {
            tile = im.getNumXTiles() == 1 && im.getNumYTiles() == 1 ? im.getTile(im.getMinTileX(), im.getMinTileY()) : im.getData();
        }
        boolean isWhiteZero = false;
        if (im.getColorModel() instanceof IndexColorModel) {
            IndexColorModel icm = (IndexColorModel)im.getColorModel();
            isWhiteZero = icm.getRed(0) + icm.getGreen(0) + icm.getBlue(0) > icm.getRed(1) + icm.getGreen(1) + icm.getBlue(1);
        }
        int lineStride = ((MultiPixelPackedSampleModel)sm).getScanlineStride();
        int bytesPerRow = (width + 7) / 8;
        byte[] bdata = ((DataBufferByte)tile.getDataBuffer()).getData();
        if (!isWhiteZero && lineStride == bytesPerRow) {
            this.output.write(bdata, 0, height * bytesPerRow);
        } else {
            int offset = 0;
            if (!isWhiteZero) {
                for (int row = 0; row < height; ++row) {
                    this.output.write(bdata, offset, bytesPerRow);
                    offset += lineStride;
                }
            } else {
                byte[] inverted = new byte[bytesPerRow];
                for (int row = 0; row < height; ++row) {
                    for (int col = 0; col < bytesPerRow; ++col) {
                        inverted[col] = ~bdata[col + offset];
                    }
                    this.output.write(inverted, 0, bytesPerRow);
                    offset += lineStride;
                }
            }
        }
    }
}

