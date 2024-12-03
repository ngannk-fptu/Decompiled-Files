/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  magick.MagickException
 *  magick.MagickImage
 *  magick.PixelPacket
 */
package com.twelvemonkeys.image;

import com.twelvemonkeys.image.CopyDither;
import com.twelvemonkeys.image.ImageUtil;
import com.twelvemonkeys.image.InverseColorMapIndexColorModel;
import com.twelvemonkeys.image.Magick;
import com.twelvemonkeys.image.MonochromeColorModel;
import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import magick.MagickException;
import magick.MagickImage;
import magick.PixelPacket;

public final class MagickUtil {
    private static final IndexColorModel CM_MONOCHROME = MonochromeColorModel.getInstance();
    private static final ColorModel CM_COLOR_ALPHA = new ComponentColorModel(ColorSpace.getInstance(1000), new int[]{8, 8, 8, 8}, true, true, 3, 0);
    private static final ColorModel CM_COLOR_OPAQUE = new ComponentColorModel(ColorSpace.getInstance(1000), new int[]{8, 8, 8}, false, false, 1, 0);
    private static final ColorModel CM_GRAY_ALPHA = new ComponentColorModel(ColorSpace.getInstance(1003), true, true, 3, 0);
    private static final ColorModel CM_GRAY_OPAQUE = new ComponentColorModel(ColorSpace.getInstance(1003), false, false, 1, 0);
    private static final int[] BAND_OFF_TRANS = new int[]{3, 2, 1, 0};
    private static final int[] BAND_OFF_OPAQUE = new int[]{2, 1, 0};
    private static final Point LOCATION_UPPER_LEFT = new Point(0, 0);
    private static final boolean DEBUG = Magick.DEBUG;

    private MagickUtil() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static BufferedImage toBuffered(MagickImage magickImage) throws MagickException {
        if (magickImage == null) {
            throw new IllegalArgumentException("image == null");
        }
        long l = 0L;
        if (DEBUG) {
            l = System.currentTimeMillis();
        }
        BufferedImage bufferedImage = null;
        try {
            switch (magickImage.getImageType()) {
                case 1: {
                    bufferedImage = MagickUtil.bilevelToBuffered(magickImage);
                    return bufferedImage;
                }
                case 2: {
                    bufferedImage = MagickUtil.grayToBuffered(magickImage, false);
                    return bufferedImage;
                }
                case 3: {
                    bufferedImage = MagickUtil.grayToBuffered(magickImage, true);
                    return bufferedImage;
                }
                case 4: {
                    bufferedImage = MagickUtil.paletteToBuffered(magickImage, false);
                    return bufferedImage;
                }
                case 5: {
                    bufferedImage = MagickUtil.paletteToBuffered(magickImage, true);
                    return bufferedImage;
                }
                case 6: {
                    bufferedImage = MagickUtil.rgbToBuffered(magickImage, false);
                    return bufferedImage;
                }
                case 7: {
                    bufferedImage = MagickUtil.rgbToBuffered(magickImage, true);
                    return bufferedImage;
                }
                case 8: {
                    bufferedImage = MagickUtil.cmykToBuffered(magickImage, false);
                    return bufferedImage;
                }
                case 9: {
                    bufferedImage = MagickUtil.cmykToBuffered(magickImage, true);
                    return bufferedImage;
                }
                default: {
                    throw new IllegalArgumentException("Unknown JMagick image type: " + magickImage.getImageType());
                }
            }
        }
        finally {
            if (DEBUG) {
                long l2 = System.currentTimeMillis() - l;
                System.out.println("Converted JMagick image type: " + magickImage.getImageType() + " to BufferedImage: " + bufferedImage);
                System.out.println("Conversion to BufferedImage: " + l2 + " ms");
            }
        }
    }

    public static MagickImage toMagick(BufferedImage bufferedImage) throws MagickException {
        if (bufferedImage == null) {
            throw new IllegalArgumentException("image == null");
        }
        long l = 0L;
        if (DEBUG) {
            l = System.currentTimeMillis();
        }
        try {
            ColorModel colorModel = bufferedImage.getColorModel();
            if (colorModel instanceof IndexColorModel) {
                MagickImage magickImage = MagickUtil.indexedToMagick(bufferedImage, (IndexColorModel)colorModel, colorModel.hasAlpha());
                return magickImage;
            }
            switch (colorModel.getColorSpace().getType()) {
                case 6: {
                    MagickImage magickImage = MagickUtil.grayToMagick(bufferedImage, colorModel.hasAlpha());
                    return magickImage;
                }
                case 5: {
                    MagickImage magickImage = MagickUtil.rgbToMagic(bufferedImage, colorModel.hasAlpha());
                    return magickImage;
                }
            }
            throw new IllegalArgumentException("Unknown buffered image type: " + bufferedImage);
        }
        finally {
            if (DEBUG) {
                long l2 = System.currentTimeMillis() - l;
                System.out.println("Conversion to MagickImage: " + l2 + " ms");
            }
        }
    }

    private static MagickImage rgbToMagic(BufferedImage bufferedImage, boolean bl) throws MagickException {
        MagickImage magickImage = new MagickImage();
        BufferedImage bufferedImage2 = ImageUtil.toBuffered(bufferedImage, bl ? 6 : 5);
        Raster raster = bufferedImage2.getRaster().getParent() != null ? bufferedImage2.getData(new Rectangle(bufferedImage2.getWidth(), bufferedImage2.getHeight())) : bufferedImage2.getRaster();
        magickImage.constituteImage(bufferedImage2.getWidth(), bufferedImage2.getHeight(), bl ? "ABGR" : "BGR", ((DataBufferByte)raster.getDataBuffer()).getData());
        return magickImage;
    }

    private static MagickImage grayToMagick(BufferedImage bufferedImage, boolean bl) throws MagickException {
        MagickImage magickImage = new MagickImage();
        BufferedImage bufferedImage2 = ImageUtil.toBuffered(bufferedImage, bl ? 6 : 10);
        Raster raster = bufferedImage2.getRaster().getParent() != null ? bufferedImage2.getData(new Rectangle(bufferedImage2.getWidth(), bufferedImage2.getHeight())) : bufferedImage2.getRaster();
        magickImage.constituteImage(bufferedImage2.getWidth(), bufferedImage2.getHeight(), bl ? "ABGR" : "I", ((DataBufferByte)raster.getDataBuffer()).getData());
        return magickImage;
    }

    private static MagickImage indexedToMagick(BufferedImage bufferedImage, IndexColorModel indexColorModel, boolean bl) throws MagickException {
        MagickImage magickImage = MagickUtil.rgbToMagic(bufferedImage, bl);
        int n = indexColorModel.getMapSize();
        magickImage.setNumberColors(n);
        return magickImage;
    }

    private static BufferedImage bilevelToBuffered(MagickImage magickImage) throws MagickException {
        BufferedImage bufferedImage = MagickUtil.grayToBuffered(magickImage, false);
        BufferedImage bufferedImage2 = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), 12, CM_MONOCHROME);
        ImageUtil.drawOnto(bufferedImage2, bufferedImage);
        return bufferedImage2;
    }

    private static BufferedImage grayToBuffered(MagickImage magickImage, boolean bl) throws MagickException {
        int[] nArray;
        Dimension dimension = magickImage.getDimension();
        int n = dimension.width * dimension.height;
        int n2 = bl ? 2 : 1;
        byte[] byArray = new byte[n * n2];
        magickImage.dispatchImage(0, 0, dimension.width, dimension.height, bl ? "AI" : "I", byArray);
        DataBufferByte dataBufferByte = new DataBufferByte(byArray, byArray.length);
        if (bl) {
            int[] nArray2 = new int[2];
            nArray2[0] = 1;
            nArray = nArray2;
            nArray2[1] = 0;
        } else {
            int[] nArray3 = new int[1];
            nArray = nArray3;
            nArray3[0] = 0;
        }
        int[] nArray4 = nArray;
        WritableRaster writableRaster = Raster.createInterleavedRaster(dataBufferByte, dimension.width, dimension.height, dimension.width * n2, n2, nArray4, LOCATION_UPPER_LEFT);
        return new BufferedImage(bl ? CM_GRAY_ALPHA : CM_GRAY_OPAQUE, writableRaster, bl, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static BufferedImage paletteToBuffered(MagickImage magickImage, boolean bl) throws MagickException {
        Object object;
        IndexColorModel indexColorModel;
        try {
            indexColorModel = MagickUtil.createIndexColorModel(magickImage.getColormap(), bl);
        }
        catch (MagickException magickException) {
            return MagickUtil.rgbToBuffered(magickImage, bl);
        }
        BufferedImage bufferedImage = MagickUtil.rgbToBuffered(magickImage, bl);
        BufferedImage bufferedImage2 = indexColorModel.getMapSize() <= 16 ? new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), 12, indexColorModel) : new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), 13, indexColorModel);
        if (bl) {
            object = bufferedImage2.createGraphics();
            try {
                ((Graphics2D)object).setComposite(AlphaComposite.Clear);
                ((Graphics)object).fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
            }
            finally {
                ((Graphics)object).dispose();
            }
        }
        object = new CopyDither(indexColorModel);
        object.filter(bufferedImage, bufferedImage2);
        return bufferedImage2;
    }

    public static IndexColorModel createIndexColorModel(PixelPacket[] pixelPacketArray, boolean bl) {
        int[] nArray = new int[pixelPacketArray.length];
        int n = bl ? nArray.length - 1 : -1;
        for (int i = pixelPacketArray.length - 1; i != 0; --i) {
            PixelPacket pixelPacket = pixelPacketArray[i];
            nArray[i] = bl ? 255 - (pixelPacket.getOpacity() & 0xFF) << 24 | (pixelPacket.getRed() & 0xFF) << 16 | (pixelPacket.getGreen() & 0xFF) << 8 | pixelPacket.getBlue() & 0xFF : (pixelPacket.getRed() & 0xFF) << 16 | (pixelPacket.getGreen() & 0xFF) << 8 | pixelPacket.getBlue() & 0xFF;
        }
        return new InverseColorMapIndexColorModel(8, nArray.length, nArray, 0, bl, n, 0);
    }

    private static BufferedImage rgbToBuffered(MagickImage magickImage, boolean bl) throws MagickException {
        Dimension dimension = magickImage.getDimension();
        int n = dimension.width * dimension.height;
        int n2 = bl ? 4 : 3;
        byte[] byArray = new byte[n * n2];
        magickImage.dispatchImage(0, 0, dimension.width, dimension.height, bl ? "ABGR" : "BGR", byArray);
        DataBufferByte dataBufferByte = new DataBufferByte(byArray, byArray.length);
        int[] nArray = bl ? BAND_OFF_TRANS : BAND_OFF_OPAQUE;
        WritableRaster writableRaster = Raster.createInterleavedRaster(dataBufferByte, dimension.width, dimension.height, dimension.width * n2, n2, nArray, LOCATION_UPPER_LEFT);
        return new BufferedImage(bl ? CM_COLOR_ALPHA : CM_COLOR_OPAQUE, writableRaster, bl, null);
    }

    private static BufferedImage cmykToBuffered(MagickImage magickImage, boolean bl) throws MagickException {
        int[] nArray;
        Dimension dimension = magickImage.getDimension();
        int n = dimension.width * dimension.height;
        ICC_Profile iCC_Profile = ICC_Profile.getInstance(magickImage.getColorProfile().getInfo());
        ICC_ColorSpace iCC_ColorSpace = new ICC_ColorSpace(iCC_Profile);
        int n2 = iCC_ColorSpace.getNumComponents() + (bl ? 1 : 0);
        int[] nArray2 = new int[n2];
        for (int i = 0; i < n2; ++i) {
            nArray2[i] = 8;
        }
        ComponentColorModel componentColorModel = bl ? new ComponentColorModel(iCC_ColorSpace, nArray2, true, true, 3, 0) : new ComponentColorModel(iCC_ColorSpace, nArray2, false, false, 1, 0);
        byte[] byArray = new byte[n * n2];
        magickImage.dispatchImage(0, 0, dimension.width, dimension.height, bl ? "ACMYK" : "CMYK", byArray);
        DataBufferByte dataBufferByte = new DataBufferByte(byArray, byArray.length);
        if (bl) {
            int[] nArray3 = new int[5];
            nArray3[0] = 0;
            nArray3[1] = 1;
            nArray3[2] = 2;
            nArray3[3] = 3;
            nArray = nArray3;
            nArray3[4] = 4;
        } else {
            int[] nArray4 = new int[4];
            nArray4[0] = 0;
            nArray4[1] = 1;
            nArray4[2] = 2;
            nArray = nArray4;
            nArray4[3] = 3;
        }
        int[] nArray5 = nArray;
        WritableRaster writableRaster = Raster.createInterleavedRaster(dataBufferByte, dimension.width, dimension.height, dimension.width * n2, n2, nArray5, LOCATION_UPPER_LEFT);
        return new BufferedImage(componentColorModel, writableRaster, bl, null);
    }
}

