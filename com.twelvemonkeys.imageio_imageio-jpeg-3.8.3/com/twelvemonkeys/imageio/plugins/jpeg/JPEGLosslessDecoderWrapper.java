/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.imageio.plugins.jpeg.JPEGImageReader;
import com.twelvemonkeys.imageio.plugins.jpeg.JPEGLosslessDecoder;
import com.twelvemonkeys.imageio.plugins.jpeg.Segment;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.io.IOException;
import java.util.List;
import javax.imageio.IIOException;
import javax.imageio.stream.ImageInputStream;

final class JPEGLosslessDecoderWrapper {
    private final JPEGImageReader listenerDelegate;

    JPEGLosslessDecoderWrapper(JPEGImageReader jPEGImageReader) {
        this.listenerDelegate = jPEGImageReader;
    }

    BufferedImage readImage(List<Segment> list, ImageInputStream imageInputStream) throws IOException {
        JPEGLosslessDecoder jPEGLosslessDecoder = new JPEGLosslessDecoder(list, imageInputStream, this.listenerDelegate);
        int[][] nArray = jPEGLosslessDecoder.decode();
        int n = jPEGLosslessDecoder.getDimX();
        int n2 = jPEGLosslessDecoder.getDimY();
        if (jPEGLosslessDecoder.getNumComponents() == 1) {
            switch (jPEGLosslessDecoder.getPrecision()) {
                case 8: {
                    return this.to8Bit1ComponentGrayScale(nArray, n, n2);
                }
                case 10: 
                case 12: 
                case 14: 
                case 16: {
                    return this.to16Bit1ComponentGrayScale(nArray, jPEGLosslessDecoder.getPrecision(), n, n2);
                }
            }
        } else if (jPEGLosslessDecoder.getNumComponents() == 3) {
            switch (jPEGLosslessDecoder.getPrecision()) {
                case 8: {
                    return this.to24Bit3ComponentRGB(nArray, n, n2);
                }
            }
        }
        throw new IIOException("JPEG Lossless with " + jPEGLosslessDecoder.getPrecision() + " bit precision and " + jPEGLosslessDecoder.getNumComponents() + " component(s) not supported");
    }

    Raster readRaster(List<Segment> list, ImageInputStream imageInputStream) throws IOException {
        return this.readImage(list, imageInputStream).getRaster();
    }

    private BufferedImage to16Bit1ComponentGrayScale(int[][] nArray, int n, int n2, int n3) {
        Object object;
        BufferedImage bufferedImage;
        if (n == 16) {
            bufferedImage = new BufferedImage(n2, n3, 11);
        } else {
            object = new ComponentColorModel(ColorSpace.getInstance(1003), new int[]{n}, false, false, 1, 1);
            bufferedImage = new BufferedImage((ColorModel)object, ((ColorModel)object).createCompatibleWritableRaster(n2, n3), ((ColorModel)object).isAlphaPremultiplied(), null);
        }
        object = ((DataBufferUShort)bufferedImage.getRaster().getDataBuffer()).getData();
        for (int i = 0; i < ((short[])object).length; ++i) {
            object[i] = (short)nArray[0][i];
        }
        return bufferedImage;
    }

    private BufferedImage to8Bit1ComponentGrayScale(int[][] nArray, int n, int n2) {
        BufferedImage bufferedImage = new BufferedImage(n, n2, 10);
        byte[] byArray = ((DataBufferByte)bufferedImage.getRaster().getDataBuffer()).getData();
        for (int i = 0; i < byArray.length; ++i) {
            byArray[i] = (byte)nArray[0][i];
        }
        return bufferedImage;
    }

    private BufferedImage to24Bit3ComponentRGB(int[][] nArray, int n, int n2) {
        BufferedImage bufferedImage = new BufferedImage(n, n2, 5);
        byte[] byArray = ((DataBufferByte)bufferedImage.getRaster().getDataBuffer()).getData();
        for (int i = 0; i < byArray.length / 3; ++i) {
            byArray[i * 3 + 2] = (byte)nArray[0][i];
            byArray[i * 3 + 1] = (byte)nArray[1][i];
            byArray[i * 3] = (byte)nArray[2][i];
        }
        return bufferedImage;
    }
}

