/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codecimpl;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codecimpl.ImagingListenerProxy;
import com.sun.media.jai.codecimpl.JaiI18N;
import com.sun.media.jai.codecimpl.SimpleRenderedImage;
import com.sun.media.jai.codecimpl.util.ImagingException;
import com.sun.media.jai.codecimpl.util.RasterFactory;
import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

class BMPImage
extends SimpleRenderedImage {
    private BufferedInputStream inputStream;
    private long bitmapFileSize;
    private long bitmapOffset;
    private long compression;
    private long imageSize;
    private byte[] palette;
    private int imageType;
    private int numBands;
    private boolean isBottomUp;
    private int bitsPerPixel;
    private int redMask;
    private int greenMask;
    private int blueMask;
    private int alphaMask;
    private static final int VERSION_2_1_BIT = 0;
    private static final int VERSION_2_4_BIT = 1;
    private static final int VERSION_2_8_BIT = 2;
    private static final int VERSION_2_24_BIT = 3;
    private static final int VERSION_3_1_BIT = 4;
    private static final int VERSION_3_4_BIT = 5;
    private static final int VERSION_3_8_BIT = 6;
    private static final int VERSION_3_24_BIT = 7;
    private static final int VERSION_3_NT_16_BIT = 8;
    private static final int VERSION_3_NT_32_BIT = 9;
    private static final int VERSION_4_1_BIT = 10;
    private static final int VERSION_4_4_BIT = 11;
    private static final int VERSION_4_8_BIT = 12;
    private static final int VERSION_4_16_BIT = 13;
    private static final int VERSION_4_24_BIT = 14;
    private static final int VERSION_4_32_BIT = 15;
    private static final int LCS_CALIBRATED_RGB = 0;
    private static final int LCS_sRGB = 1;
    private static final int LCS_CMYK = 2;
    private static final int BI_RGB = 0;
    private static final int BI_RLE8 = 1;
    private static final int BI_RLE4 = 2;
    private static final int BI_BITFIELDS = 3;
    private WritableRaster theTile;

    public BMPImage(InputStream stream) {
        String message;
        block77: {
            this.theTile = null;
            this.inputStream = stream instanceof BufferedInputStream ? (BufferedInputStream)stream : new BufferedInputStream(stream);
            try {
                this.inputStream.mark(Integer.MAX_VALUE);
                if (this.readUnsignedByte(this.inputStream) != 66 || this.readUnsignedByte(this.inputStream) != 77) {
                    throw new RuntimeException(JaiI18N.getString("BMPImageDecoder0"));
                }
                this.bitmapFileSize = this.readDWord(this.inputStream);
                this.readWord(this.inputStream);
                this.readWord(this.inputStream);
                this.bitmapOffset = this.readDWord(this.inputStream);
                long size = this.readDWord(this.inputStream);
                if (size == 12L) {
                    this.width = this.readWord(this.inputStream);
                    this.height = this.readWord(this.inputStream);
                } else {
                    this.width = this.readLong(this.inputStream);
                    this.height = this.readLong(this.inputStream);
                }
                int planes = this.readWord(this.inputStream);
                this.bitsPerPixel = this.readWord(this.inputStream);
                this.properties.put("color_planes", new Integer(planes));
                this.properties.put("bits_per_pixel", new Integer(this.bitsPerPixel));
                this.numBands = 3;
                if (size == 12L) {
                    this.properties.put("bmp_version", "BMP v. 2.x");
                    if (this.bitsPerPixel == 1) {
                        this.imageType = 0;
                    } else if (this.bitsPerPixel == 4) {
                        this.imageType = 1;
                    } else if (this.bitsPerPixel == 8) {
                        this.imageType = 2;
                    } else if (this.bitsPerPixel == 24) {
                        this.imageType = 3;
                    }
                    int numberOfEntries = (int)((this.bitmapOffset - 14L - size) / 3L);
                    int sizeOfPalette = numberOfEntries * 3;
                    this.palette = new byte[sizeOfPalette];
                    this.inputStream.read(this.palette, 0, sizeOfPalette);
                    this.properties.put("palette", this.palette);
                    break block77;
                }
                this.compression = this.readDWord(this.inputStream);
                this.imageSize = this.readDWord(this.inputStream);
                long xPelsPerMeter = this.readLong(this.inputStream);
                long yPelsPerMeter = this.readLong(this.inputStream);
                long colorsUsed = this.readDWord(this.inputStream);
                long colorsImportant = this.readDWord(this.inputStream);
                switch ((int)this.compression) {
                    case 0: {
                        this.properties.put("compression", "BI_RGB");
                        break;
                    }
                    case 1: {
                        this.properties.put("compression", "BI_RLE8");
                        break;
                    }
                    case 2: {
                        this.properties.put("compression", "BI_RLE4");
                        break;
                    }
                    case 3: {
                        this.properties.put("compression", "BI_BITFIELDS");
                    }
                }
                this.properties.put("x_pixels_per_meter", new Long(xPelsPerMeter));
                this.properties.put("y_pixels_per_meter", new Long(yPelsPerMeter));
                this.properties.put("colors_used", new Long(colorsUsed));
                this.properties.put("colors_important", new Long(colorsImportant));
                if (size == 40L) {
                    switch ((int)this.compression) {
                        case 0: 
                        case 1: 
                        case 2: {
                            int numberOfEntries = (int)((this.bitmapOffset - 14L - size) / 4L);
                            int sizeOfPalette = numberOfEntries * 4;
                            this.palette = new byte[sizeOfPalette];
                            this.inputStream.read(this.palette, 0, sizeOfPalette);
                            this.properties.put("palette", this.palette);
                            if (this.bitsPerPixel == 1) {
                                this.imageType = 4;
                            } else if (this.bitsPerPixel == 4) {
                                this.imageType = 5;
                            } else if (this.bitsPerPixel == 8) {
                                this.imageType = 6;
                            } else if (this.bitsPerPixel == 24) {
                                this.imageType = 7;
                            } else if (this.bitsPerPixel == 16) {
                                this.imageType = 8;
                                this.redMask = 31744;
                                this.greenMask = 992;
                                this.blueMask = 31;
                                this.properties.put("red_mask", new Integer(this.redMask));
                                this.properties.put("green_mask", new Integer(this.greenMask));
                                this.properties.put("blue_mask", new Integer(this.blueMask));
                            } else if (this.bitsPerPixel == 32) {
                                this.imageType = 9;
                                this.redMask = 0xFF0000;
                                this.greenMask = 65280;
                                this.blueMask = 255;
                                this.properties.put("red_mask", new Integer(this.redMask));
                                this.properties.put("green_mask", new Integer(this.greenMask));
                                this.properties.put("blue_mask", new Integer(this.blueMask));
                            }
                            this.properties.put("bmp_version", "BMP v. 3.x");
                            break block77;
                        }
                        case 3: {
                            int sizeOfPalette;
                            if (this.bitsPerPixel == 16) {
                                this.imageType = 8;
                            } else if (this.bitsPerPixel == 32) {
                                this.imageType = 9;
                            }
                            this.redMask = (int)this.readDWord(this.inputStream);
                            this.greenMask = (int)this.readDWord(this.inputStream);
                            this.blueMask = (int)this.readDWord(this.inputStream);
                            this.properties.put("red_mask", new Integer(this.redMask));
                            this.properties.put("green_mask", new Integer(this.greenMask));
                            this.properties.put("blue_mask", new Integer(this.blueMask));
                            if (colorsUsed != 0L) {
                                sizeOfPalette = (int)colorsUsed * 4;
                                this.palette = new byte[sizeOfPalette];
                                this.inputStream.read(this.palette, 0, sizeOfPalette);
                                this.properties.put("palette", this.palette);
                            }
                            this.properties.put("bmp_version", "BMP v. 3.x NT");
                            break block77;
                        }
                        default: {
                            throw new RuntimeException(JaiI18N.getString("BMPImageDecoder1"));
                        }
                    }
                }
                if (size == 108L) {
                    this.properties.put("bmp_version", "BMP v. 4.x");
                    this.redMask = (int)this.readDWord(this.inputStream);
                    this.greenMask = (int)this.readDWord(this.inputStream);
                    this.blueMask = (int)this.readDWord(this.inputStream);
                    this.alphaMask = (int)this.readDWord(this.inputStream);
                    long csType = this.readDWord(this.inputStream);
                    int redX = this.readLong(this.inputStream);
                    int redY = this.readLong(this.inputStream);
                    int redZ = this.readLong(this.inputStream);
                    int greenX = this.readLong(this.inputStream);
                    int greenY = this.readLong(this.inputStream);
                    int greenZ = this.readLong(this.inputStream);
                    int blueX = this.readLong(this.inputStream);
                    int blueY = this.readLong(this.inputStream);
                    int blueZ = this.readLong(this.inputStream);
                    long gammaRed = this.readDWord(this.inputStream);
                    long gammaGreen = this.readDWord(this.inputStream);
                    long gammaBlue = this.readDWord(this.inputStream);
                    int numberOfEntries = (int)((this.bitmapOffset - 14L - size) / 4L);
                    int sizeOfPalette = numberOfEntries * 4;
                    this.palette = new byte[sizeOfPalette];
                    this.inputStream.read(this.palette, 0, sizeOfPalette);
                    if (this.palette != null || this.palette.length != 0) {
                        this.properties.put("palette", this.palette);
                    }
                    switch ((int)csType) {
                        case 0: {
                            this.properties.put("color_space", "LCS_CALIBRATED_RGB");
                            this.properties.put("redX", new Integer(redX));
                            this.properties.put("redY", new Integer(redY));
                            this.properties.put("redZ", new Integer(redZ));
                            this.properties.put("greenX", new Integer(greenX));
                            this.properties.put("greenY", new Integer(greenY));
                            this.properties.put("greenZ", new Integer(greenZ));
                            this.properties.put("blueX", new Integer(blueX));
                            this.properties.put("blueY", new Integer(blueY));
                            this.properties.put("blueZ", new Integer(blueZ));
                            this.properties.put("gamma_red", new Long(gammaRed));
                            this.properties.put("gamma_green", new Long(gammaGreen));
                            this.properties.put("gamma_blue", new Long(gammaBlue));
                            throw new RuntimeException(JaiI18N.getString("BMPImageDecoder2"));
                        }
                        case 1: {
                            this.properties.put("color_space", "LCS_sRGB");
                            break;
                        }
                        case 2: {
                            this.properties.put("color_space", "LCS_CMYK");
                            throw new RuntimeException(JaiI18N.getString("BMPImageDecoder2"));
                        }
                    }
                    if (this.bitsPerPixel == 1) {
                        this.imageType = 10;
                    } else if (this.bitsPerPixel == 4) {
                        this.imageType = 11;
                    } else if (this.bitsPerPixel == 8) {
                        this.imageType = 12;
                    } else if (this.bitsPerPixel == 16) {
                        this.imageType = 13;
                        if ((int)this.compression == 0) {
                            this.redMask = 31744;
                            this.greenMask = 992;
                            this.blueMask = 31;
                        }
                    } else if (this.bitsPerPixel == 24) {
                        this.imageType = 14;
                    } else if (this.bitsPerPixel == 32) {
                        this.imageType = 15;
                        if ((int)this.compression == 0) {
                            this.redMask = 0xFF0000;
                            this.greenMask = 65280;
                            this.blueMask = 255;
                        }
                    }
                    this.properties.put("red_mask", new Integer(this.redMask));
                    this.properties.put("green_mask", new Integer(this.greenMask));
                    this.properties.put("blue_mask", new Integer(this.blueMask));
                    this.properties.put("alpha_mask", new Integer(this.alphaMask));
                    break block77;
                }
                this.properties.put("bmp_version", "BMP v. 5.x");
                throw new RuntimeException(JaiI18N.getString("BMPImageDecoder4"));
            }
            catch (IOException ioe) {
                message = JaiI18N.getString("BMPImageDecoder5");
                ImagingListenerProxy.errorOccurred(message, new ImagingException(message, ioe), this, false);
            }
        }
        if (this.height > 0) {
            this.isBottomUp = true;
        } else {
            this.isBottomUp = false;
            this.height = Math.abs(this.height);
        }
        this.tileWidth = this.width;
        this.tileHeight = this.height;
        if (this.bitsPerPixel == 1 || this.bitsPerPixel == 4 || this.bitsPerPixel == 8) {
            int off;
            byte[] b;
            byte[] g;
            byte[] r;
            int size;
            this.numBands = 1;
            this.sampleModel = this.bitsPerPixel == 8 ? RasterFactory.createPixelInterleavedSampleModel(0, this.width, this.height, this.numBands) : new MultiPixelPackedSampleModel(0, this.width, this.height, this.bitsPerPixel);
            if (this.imageType == 0 || this.imageType == 1 || this.imageType == 2) {
                size = this.palette.length / 3;
                if (size > 256) {
                    size = 256;
                }
                r = new byte[size];
                g = new byte[size];
                b = new byte[size];
                for (int i = 0; i < size; ++i) {
                    off = 3 * i;
                    b[i] = this.palette[off];
                    g[i] = this.palette[off + 1];
                    r[i] = this.palette[off + 2];
                }
            } else {
                size = this.palette.length / 4;
                if (size > 256) {
                    size = 256;
                }
                r = new byte[size];
                g = new byte[size];
                b = new byte[size];
                for (int i = 0; i < size; ++i) {
                    off = 4 * i;
                    b[i] = this.palette[off];
                    g[i] = this.palette[off + 1];
                    r[i] = this.palette[off + 2];
                }
            }
            this.colorModel = ImageCodec.isIndicesForGrayscale(r, g, b) ? ImageCodec.createComponentColorModel(this.sampleModel) : new IndexColorModel(this.bitsPerPixel, size, r, g, b);
        } else if (this.bitsPerPixel == 16) {
            this.numBands = 3;
            this.sampleModel = new SinglePixelPackedSampleModel(1, this.width, this.height, new int[]{this.redMask, this.greenMask, this.blueMask});
            this.colorModel = new DirectColorModel(ColorSpace.getInstance(1000), 16, this.redMask, this.greenMask, this.blueMask, 0, false, 1);
        } else if (this.bitsPerPixel == 32) {
            int[] nArray;
            int n = this.numBands = this.alphaMask == 0 ? 3 : 4;
            if (this.numBands == 3) {
                int[] nArray2 = new int[3];
                nArray2[0] = this.redMask;
                nArray2[1] = this.greenMask;
                nArray = nArray2;
                nArray2[2] = this.blueMask;
            } else {
                int[] nArray3 = new int[4];
                nArray3[0] = this.redMask;
                nArray3[1] = this.greenMask;
                nArray3[2] = this.blueMask;
                nArray = nArray3;
                nArray3[3] = this.alphaMask;
            }
            int[] bitMasks = nArray;
            this.sampleModel = new SinglePixelPackedSampleModel(3, this.width, this.height, bitMasks);
            this.colorModel = new DirectColorModel(ColorSpace.getInstance(1000), 32, this.redMask, this.greenMask, this.blueMask, this.alphaMask, false, 3);
        } else {
            this.numBands = 3;
            this.sampleModel = RasterFactory.createPixelInterleavedSampleModel(0, this.width, this.height, this.numBands);
            this.colorModel = ImageCodec.createComponentColorModel(this.sampleModel);
        }
        try {
            this.inputStream.reset();
            this.inputStream.skip(this.bitmapOffset);
        }
        catch (IOException ioe) {
            message = JaiI18N.getString("BMPImageDecoder9");
            ImagingListenerProxy.errorOccurred(message, new ImagingException(message, ioe), this, false);
        }
    }

    private void read1Bit(byte[] bdata, int paletteEntries) {
        int i;
        int padding = 0;
        int bytesPerScanline = (int)Math.ceil((double)this.width / 8.0);
        int remainder = bytesPerScanline % 4;
        if (remainder != 0) {
            padding = 4 - remainder;
        }
        int imSize = (bytesPerScanline + padding) * this.height;
        byte[] values = new byte[imSize];
        try {
            for (int bytesRead = 0; bytesRead < imSize; bytesRead += this.inputStream.read(values, bytesRead, imSize - bytesRead)) {
            }
        }
        catch (IOException ioe) {
            String message = JaiI18N.getString("BMPImageDecoder6");
            ImagingListenerProxy.errorOccurred(message, new ImagingException(message, ioe), this, false);
        }
        if (this.isBottomUp) {
            for (i = 0; i < this.height; ++i) {
                System.arraycopy(values, imSize - (i + 1) * (bytesPerScanline + padding), bdata, i * bytesPerScanline, bytesPerScanline);
            }
        } else {
            for (i = 0; i < this.height; ++i) {
                System.arraycopy(values, i * (bytesPerScanline + padding), bdata, i * bytesPerScanline, bytesPerScanline);
            }
        }
    }

    private void read4Bit(byte[] bdata, int paletteEntries) {
        int i;
        int padding = 0;
        int bytesPerScanline = (int)Math.ceil((double)this.width / 2.0);
        int remainder = bytesPerScanline % 4;
        if (remainder != 0) {
            padding = 4 - remainder;
        }
        int imSize = (bytesPerScanline + padding) * this.height;
        byte[] values = new byte[imSize];
        try {
            for (int bytesRead = 0; bytesRead < imSize; bytesRead += this.inputStream.read(values, bytesRead, imSize - bytesRead)) {
            }
        }
        catch (IOException ioe) {
            String message = JaiI18N.getString("BMPImageDecoder6");
            ImagingListenerProxy.errorOccurred(message, new ImagingException(message, ioe), this, false);
        }
        if (this.isBottomUp) {
            for (i = 0; i < this.height; ++i) {
                System.arraycopy(values, imSize - (i + 1) * (bytesPerScanline + padding), bdata, i * bytesPerScanline, bytesPerScanline);
            }
        } else {
            for (i = 0; i < this.height; ++i) {
                System.arraycopy(values, i * (bytesPerScanline + padding), bdata, i * bytesPerScanline, bytesPerScanline);
            }
        }
    }

    private void read8Bit(byte[] bdata, int paletteEntries) {
        int i;
        int padding = 0;
        int bitsPerScanline = this.width * 8;
        if (bitsPerScanline % 32 != 0) {
            padding = (bitsPerScanline / 32 + 1) * 32 - bitsPerScanline;
            padding = (int)Math.ceil((double)padding / 8.0);
        }
        int imSize = (this.width + padding) * this.height;
        byte[] values = new byte[imSize];
        try {
            for (int bytesRead = 0; bytesRead < imSize; bytesRead += this.inputStream.read(values, bytesRead, imSize - bytesRead)) {
            }
        }
        catch (IOException ioe) {
            String message = JaiI18N.getString("BMPImageDecoder6");
            ImagingListenerProxy.errorOccurred(message, new ImagingException(message, ioe), this, false);
        }
        if (this.isBottomUp) {
            for (i = 0; i < this.height; ++i) {
                System.arraycopy(values, imSize - (i + 1) * (this.width + padding), bdata, i * this.width, this.width);
            }
        } else {
            for (i = 0; i < this.height; ++i) {
                System.arraycopy(values, i * (this.width + padding), bdata, i * this.width, this.width);
            }
        }
    }

    private void read24Bit(byte[] bdata) {
        int imSize;
        int padding = 0;
        int bitsPerScanline = this.width * 24;
        if (bitsPerScanline % 32 != 0) {
            padding = (bitsPerScanline / 32 + 1) * 32 - bitsPerScanline;
            padding = (int)Math.ceil((double)padding / 8.0);
        }
        if ((imSize = (int)this.imageSize) == 0) {
            imSize = (int)(this.bitmapFileSize - this.bitmapOffset);
        }
        byte[] values = new byte[imSize];
        try {
            for (int bytesRead = 0; bytesRead < imSize; bytesRead += this.inputStream.read(values, bytesRead, imSize - bytesRead)) {
            }
        }
        catch (IOException ioe) {
            String message = JaiI18N.getString("BMPImageDecoder4");
            ImagingListenerProxy.errorOccurred(message, new ImagingException(message, ioe), this, false);
        }
        int l = 0;
        if (this.isBottomUp) {
            int max = this.width * this.height * 3 - 1;
            int count = -padding;
            for (int i = 0; i < this.height; ++i) {
                l = max - (i + 1) * this.width * 3 + 1;
                count += padding;
                for (int j = 0; j < this.width; ++j) {
                    bdata[l++] = values[count++];
                    bdata[l++] = values[count++];
                    bdata[l++] = values[count++];
                }
            }
        } else {
            int count = -padding;
            for (int i = 0; i < this.height; ++i) {
                count += padding;
                for (int j = 0; j < this.width; ++j) {
                    bdata[l++] = values[count++];
                    bdata[l++] = values[count++];
                    bdata[l++] = values[count++];
                }
            }
        }
    }

    private void read16Bit(short[] sdata) {
        int imSize;
        int padding = 0;
        int bitsPerScanline = this.width * 16;
        if (bitsPerScanline % 32 != 0) {
            padding = (bitsPerScanline / 32 + 1) * 32 - bitsPerScanline;
            padding = (int)Math.ceil((double)padding / 8.0);
        }
        if ((imSize = (int)this.imageSize) == 0) {
            imSize = (int)(this.bitmapFileSize - this.bitmapOffset);
        }
        int l = 0;
        try {
            if (this.isBottomUp) {
                int max = this.width * this.height - 1;
                for (int i = 0; i < this.height; ++i) {
                    l = max - (i + 1) * this.width + 1;
                    for (int j = 0; j < this.width; ++j) {
                        sdata[l++] = (short)(this.readWord(this.inputStream) & 0xFFFF);
                    }
                    for (int m = 0; m < padding; ++m) {
                        this.inputStream.read();
                    }
                }
            } else {
                for (int i = 0; i < this.height; ++i) {
                    for (int j = 0; j < this.width; ++j) {
                        sdata[l++] = (short)(this.readWord(this.inputStream) & 0xFFFF);
                    }
                    for (int m = 0; m < padding; ++m) {
                        this.inputStream.read();
                    }
                }
            }
        }
        catch (IOException ioe) {
            String message = JaiI18N.getString("BMPImageDecoder6");
            ImagingListenerProxy.errorOccurred(message, new ImagingException(message, ioe), this, false);
        }
    }

    private void read32Bit(int[] idata) {
        int imSize = (int)this.imageSize;
        if (imSize == 0) {
            imSize = (int)(this.bitmapFileSize - this.bitmapOffset);
        }
        int l = 0;
        try {
            if (this.isBottomUp) {
                int max = this.width * this.height - 1;
                for (int i = 0; i < this.height; ++i) {
                    l = max - (i + 1) * this.width + 1;
                    for (int j = 0; j < this.width; ++j) {
                        idata[l++] = (int)this.readDWord(this.inputStream);
                    }
                }
            } else {
                for (int i = 0; i < this.height; ++i) {
                    for (int j = 0; j < this.width; ++j) {
                        idata[l++] = (int)this.readDWord(this.inputStream);
                    }
                }
            }
        }
        catch (IOException ioe) {
            String message = JaiI18N.getString("BMPImageDecoder6");
            ImagingListenerProxy.errorOccurred(message, new ImagingException(message, ioe), this, false);
        }
    }

    private void readRLE8(byte[] bdata) {
        int imSize = (int)this.imageSize;
        if (imSize == 0) {
            imSize = (int)(this.bitmapFileSize - this.bitmapOffset);
        }
        int padding = 0;
        int remainder = this.width % 4;
        if (remainder != 0) {
            padding = 4 - remainder;
        }
        byte[] values = new byte[imSize];
        try {
            for (int bytesRead = 0; bytesRead < imSize; bytesRead += this.inputStream.read(values, bytesRead, imSize - bytesRead)) {
            }
        }
        catch (IOException ioe) {
            String message = JaiI18N.getString("BMPImageDecoder6");
            ImagingListenerProxy.errorOccurred(message, new ImagingException(message, ioe), this, false);
        }
        byte[] val = this.decodeRLE8(imSize, padding, values);
        imSize = this.width * this.height;
        if (this.isBottomUp) {
            int bytesPerScanline = this.width;
            for (int i = 0; i < this.height; ++i) {
                System.arraycopy(val, imSize - (i + 1) * bytesPerScanline, bdata, i * bytesPerScanline, bytesPerScanline);
            }
        } else {
            bdata = val;
        }
    }

    private byte[] decodeRLE8(int imSize, int padding, byte[] values) {
        byte[] val = new byte[this.width * this.height];
        int count = 0;
        int l = 0;
        boolean flag = false;
        while (count != imSize) {
            int value;
            if ((value = values[count++] & 0xFF) == 0) {
                switch (values[count++] & 0xFF) {
                    case 0: {
                        break;
                    }
                    case 1: {
                        flag = true;
                        break;
                    }
                    case 2: {
                        int xoff = values[count++] & 0xFF;
                        int yoff = values[count] & 0xFF;
                        l += xoff + yoff * this.width;
                        break;
                    }
                    default: {
                        int end = values[count - 1] & 0xFF;
                        for (int i = 0; i < end; ++i) {
                            val[l++] = (byte)(values[count++] & 0xFF);
                        }
                        if (!this.isEven(end)) {
                            ++count;
                            break;
                        } else {
                            break;
                        }
                    }
                }
            } else {
                for (int i = 0; i < value; ++i) {
                    val[l++] = (byte)(values[count] & 0xFF);
                }
                ++count;
            }
            if (!flag) continue;
            break;
        }
        return val;
    }

    private int[] readRLE4() {
        int imSize = (int)this.imageSize;
        if (imSize == 0) {
            imSize = (int)(this.bitmapFileSize - this.bitmapOffset);
        }
        int padding = 0;
        int remainder = this.width % 4;
        if (remainder != 0) {
            padding = 4 - remainder;
        }
        int[] values = new int[imSize];
        try {
            for (int i = 0; i < imSize; ++i) {
                values[i] = this.inputStream.read();
            }
        }
        catch (IOException ioe) {
            String message = JaiI18N.getString("BMPImageDecoder6");
            ImagingListenerProxy.errorOccurred(message, new ImagingException(message, ioe), this, false);
        }
        int[] val = this.decodeRLE4(imSize, padding, values);
        if (this.isBottomUp) {
            int[] inverted = val;
            val = new int[this.width * this.height];
            int l = 0;
            for (int i = this.height - 1; i >= 0; --i) {
                int index = i * this.width;
                int lineEnd = l + this.width;
                while (l != lineEnd) {
                    val[l++] = inverted[index++];
                }
            }
        }
        return val;
    }

    private int[] decodeRLE4(int imSize, int padding, int[] values) {
        int[] val = new int[this.width * this.height];
        int count = 0;
        int l = 0;
        boolean flag = false;
        while (count != imSize) {
            int value;
            if ((value = values[count++]) == 0) {
                switch (values[count++]) {
                    case 0: {
                        break;
                    }
                    case 1: {
                        flag = true;
                        break;
                    }
                    case 2: {
                        int xoff = values[count++];
                        int yoff = values[count];
                        l += xoff + yoff * this.width;
                        break;
                    }
                    default: {
                        int end = values[count - 1];
                        for (int i = 0; i < end; ++i) {
                            val[l++] = this.isEven(i) ? (values[count] & 0xF0) >> 4 : values[count++] & 0xF;
                        }
                        if (!this.isEven(end)) {
                            ++count;
                        }
                        if (!this.isEven((int)Math.ceil(end / 2))) {
                            ++count;
                            break;
                        } else {
                            break;
                        }
                    }
                }
            } else {
                int[] alternate = new int[]{(values[count] & 0xF0) >> 4, values[count] & 0xF};
                for (int i = 0; i < value; ++i) {
                    val[l++] = alternate[i % 2];
                }
                ++count;
            }
            if (!flag) continue;
            break;
        }
        return val;
    }

    private boolean isEven(int number) {
        return number % 2 == 0;
    }

    private int readUnsignedByte(InputStream stream) throws IOException {
        return stream.read() & 0xFF;
    }

    private int readUnsignedShort(InputStream stream) throws IOException {
        int b1 = this.readUnsignedByte(stream);
        int b2 = this.readUnsignedByte(stream);
        return (b2 << 8 | b1) & 0xFFFF;
    }

    private int readShort(InputStream stream) throws IOException {
        int b1 = this.readUnsignedByte(stream);
        int b2 = this.readUnsignedByte(stream);
        return b2 << 8 | b1;
    }

    private int readWord(InputStream stream) throws IOException {
        return this.readUnsignedShort(stream);
    }

    private long readUnsignedInt(InputStream stream) throws IOException {
        int b1 = this.readUnsignedByte(stream);
        int b2 = this.readUnsignedByte(stream);
        int b3 = this.readUnsignedByte(stream);
        int b4 = this.readUnsignedByte(stream);
        long l = b4 << 24 | b3 << 16 | b2 << 8 | b1;
        return l & 0xFFFFFFFFFFFFFFFFL;
    }

    private int readInt(InputStream stream) throws IOException {
        int b1 = this.readUnsignedByte(stream);
        int b2 = this.readUnsignedByte(stream);
        int b3 = this.readUnsignedByte(stream);
        int b4 = this.readUnsignedByte(stream);
        return b4 << 24 | b3 << 16 | b2 << 8 | b1;
    }

    private long readDWord(InputStream stream) throws IOException {
        return this.readUnsignedInt(stream);
    }

    private int readLong(InputStream stream) throws IOException {
        return this.readInt(stream);
    }

    private synchronized Raster computeTile(int tileX, int tileY) {
        if (this.theTile != null) {
            return this.theTile;
        }
        Point org = new Point(this.tileXToX(tileX), this.tileYToY(tileY));
        WritableRaster tile = RasterFactory.createWritableRaster(this.sampleModel, org);
        byte[] bdata = null;
        short[] sdata = null;
        int[] idata = null;
        if (this.sampleModel.getDataType() == 0) {
            bdata = ((DataBufferByte)tile.getDataBuffer()).getData();
        } else if (this.sampleModel.getDataType() == 1) {
            sdata = ((DataBufferUShort)tile.getDataBuffer()).getData();
        } else if (this.sampleModel.getDataType() == 3) {
            idata = ((DataBufferInt)tile.getDataBuffer()).getData();
        }
        block0 : switch (this.imageType) {
            case 0: {
                this.read1Bit(bdata, 3);
                break;
            }
            case 1: {
                this.read4Bit(bdata, 3);
                break;
            }
            case 2: {
                this.read8Bit(bdata, 3);
                break;
            }
            case 3: {
                this.read24Bit(bdata);
                break;
            }
            case 4: {
                this.read1Bit(bdata, 4);
                break;
            }
            case 5: {
                switch ((int)this.compression) {
                    case 0: {
                        this.read4Bit(bdata, 4);
                        break block0;
                    }
                    case 2: {
                        int[] pixels = this.readRLE4();
                        tile.setPixels(0, 0, this.width, this.height, pixels);
                        break block0;
                    }
                }
                throw new RuntimeException(JaiI18N.getString("BMPImageDecoder3"));
            }
            case 6: {
                switch ((int)this.compression) {
                    case 0: {
                        this.read8Bit(bdata, 4);
                        break block0;
                    }
                    case 1: {
                        this.readRLE8(bdata);
                        break block0;
                    }
                }
                throw new RuntimeException(JaiI18N.getString("BMPImageDecoder3"));
            }
            case 7: {
                this.read24Bit(bdata);
                break;
            }
            case 8: {
                this.read16Bit(sdata);
                break;
            }
            case 9: {
                this.read32Bit(idata);
                break;
            }
            case 10: {
                this.read1Bit(bdata, 4);
                break;
            }
            case 11: {
                switch ((int)this.compression) {
                    case 0: {
                        this.read4Bit(bdata, 4);
                        break;
                    }
                    case 2: {
                        int[] pixels = this.readRLE4();
                        tile.setPixels(0, 0, this.width, this.height, pixels);
                        break;
                    }
                    default: {
                        throw new RuntimeException(JaiI18N.getString("BMPImageDecoder3"));
                    }
                }
            }
            case 12: {
                switch ((int)this.compression) {
                    case 0: {
                        this.read8Bit(bdata, 4);
                        break block0;
                    }
                    case 1: {
                        this.readRLE8(bdata);
                        break block0;
                    }
                }
                throw new RuntimeException(JaiI18N.getString("BMPImageDecoder3"));
            }
            case 13: {
                this.read16Bit(sdata);
                break;
            }
            case 14: {
                this.read24Bit(bdata);
                break;
            }
            case 15: {
                this.read32Bit(idata);
            }
        }
        this.theTile = tile;
        return tile;
    }

    public synchronized Raster getTile(int tileX, int tileY) {
        if (tileX != 0 || tileY != 0) {
            throw new IllegalArgumentException(JaiI18N.getString("BMPImageDecoder7"));
        }
        return this.computeTile(tileX, tileY);
    }

    public void dispose() {
        this.theTile = null;
    }
}

