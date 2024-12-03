/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.image.codec.jpeg.JPEGCodec
 *  com.sun.image.codec.jpeg.JPEGDecodeParam
 *  com.sun.image.codec.jpeg.JPEGImageDecoder
 */
package com.sun.media.jai.codecimpl.fpx;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGDecodeParam;
import com.sun.image.codec.jpeg.JPEGImageDecoder;
import com.sun.media.jai.codec.FPXDecodeParam;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codecimpl.ImagingListenerProxy;
import com.sun.media.jai.codecimpl.SimpleRenderedImage;
import com.sun.media.jai.codecimpl.fpx.FPXUtils;
import com.sun.media.jai.codecimpl.fpx.JaiI18N;
import com.sun.media.jai.codecimpl.fpx.PropertySet;
import com.sun.media.jai.codecimpl.fpx.StructuredStorage;
import com.sun.media.jai.codecimpl.util.RasterFactory;
import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;

public class FPXImage
extends SimpleRenderedImage {
    private static final int SUBIMAGE_COLOR_SPACE_COLORLESS = 0;
    private static final int SUBIMAGE_COLOR_SPACE_MONOCHROME = 0;
    private static final int SUBIMAGE_COLOR_SPACE_PHOTOYCC = 0;
    private static final int SUBIMAGE_COLOR_SPACE_NIFRGB = 0;
    private static final String[] COLORSPACE_NAME = new String[]{"Colorless", "Monochrome", "PhotoYCC", "NIF RGB"};
    StructuredStorage storage;
    int numResolutions;
    int highestResWidth;
    int highestResHeight;
    float defaultDisplayHeight;
    float defaultDisplayWidth;
    int displayHeightWidthUnits;
    boolean[] subimageValid;
    int[] subimageWidth;
    int[] subimageHeight;
    int[][] subimageColor;
    int[] decimationMethod;
    float[] decimationPrefilterWidth;
    int highestResolution = -1;
    int maxJPEGTableIndex;
    byte[][] JPEGTable;
    int numChannels;
    int tileHeaderTableOffset;
    int tileHeaderEntryLength;
    SeekableStream subimageHeaderStream;
    SeekableStream subimageDataStream;
    int resolution;
    int tilesAcross;
    int[] bandOffsets = new int[]{0, 1, 2};
    private static final int[] RGBBits8 = new int[]{8, 8, 8};
    private static final ComponentColorModel colorModelRGB8 = new ComponentColorModel(ColorSpace.getInstance(1004), RGBBits8, false, false, 1, 0);
    private static final byte[] PhotoYCCToRGBLUT = new byte[]{0, 1, 1, 2, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 22, 23, 24, 25, 26, 28, 29, 30, 31, 33, 34, 35, 36, 38, 39, 40, 41, 43, 44, 45, 47, 48, 49, 51, 52, 53, 55, 56, 57, 59, 60, 61, 63, 64, 65, 67, 68, 70, 71, 72, 74, 75, 76, 78, 79, 81, 82, 83, 85, 86, 88, 89, 91, 92, 93, 95, 96, 98, 99, 101, 102, 103, 105, 106, 108, 109, 111, 112, 113, 115, 116, 118, 119, 121, 122, 123, 125, 126, -128, -127, -126, -124, -123, -122, -120, -119, -118, -116, -115, -114, -112, -111, -110, -108, -107, -106, -104, -103, -102, -101, -99, -98, -97, -96, -94, -93, -92, -91, -90, -88, -87, -86, -85, -84, -82, -81, -80, -79, -78, -77, -76, -74, -73, -72, -71, -70, -69, -68, -67, -66, -65, -64, -62, -61, -60, -59, -58, -57, -56, -55, -54, -53, -52, -52, -51, -50, -49, -48, -47, -46, -45, -44, -43, -43, -42, -41, -40, -39, -39, -38, -37, -36, -35, -35, -34, -33, -33, -32, -31, -31, -30, -29, -29, -28, -27, -27, -26, -26, -25, -25, -24, -23, -23, -22, -22, -21, -21, -20, -20, -20, -19, -19, -18, -18, -18, -17, -17, -16, -16, -16, -15, -15, -15, -14, -14, -14, -14, -13, -13, -13, -12, -12, -12, -12, -11, -11, -11, -11, -11, -10, -10, -10, -10, -10, -9, -9, -9, -9, -9, -9, -8, -8, -8, -8, -8, -8, -7, -7, -7, -7, -7, -7, -7, -7, -7, -6, -6, -6, -6, -6, -6, -6, -6, -6, -6, -5, -5, -5, -5, -5, -5, -5, -5, -5, -5, -5, -5, -5, -5, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    Hashtable properties = null;
    static /* synthetic */ Class class$com$sun$media$jai$codec$ImageCodec;

    public FPXImage(SeekableStream stream, FPXDecodeParam param) throws IOException {
        this.storage = new StructuredStorage(stream);
        this.readImageContents();
        if (param == null) {
            param = new FPXDecodeParam();
        }
        this.resolution = param.getResolution();
        this.readResolution();
        this.bandOffsets = new int[this.numChannels];
        for (int i = 0; i < this.numChannels; ++i) {
            this.bandOffsets[i] = i;
        }
        this.minX = 0;
        this.minY = 0;
        this.sampleModel = RasterFactory.createPixelInterleavedSampleModel(0, this.tileWidth, this.tileHeight, this.numChannels, this.numChannels * this.tileWidth, this.bandOffsets);
        this.colorModel = ImageCodec.createComponentColorModel(this.sampleModel);
    }

    private void readImageContents() throws IOException {
        int index;
        int i;
        this.storage.changeDirectoryToRoot();
        this.storage.changeDirectory("Data Object Store 000001");
        SeekableStream imageContents = this.storage.getStream("\u0005Image Contents");
        PropertySet icps = new PropertySet(imageContents);
        this.numResolutions = (int)icps.getUI4(0x1000000);
        this.highestResWidth = (int)icps.getUI4(0x1000002);
        this.highestResHeight = (int)icps.getUI4(0x1000003);
        this.displayHeightWidthUnits = (int)icps.getUI4(0x1000006, 0L);
        this.subimageValid = new boolean[this.numResolutions];
        this.subimageWidth = new int[this.numResolutions];
        this.subimageHeight = new int[this.numResolutions];
        this.subimageColor = new int[this.numResolutions][];
        this.decimationMethod = new int[this.numResolutions];
        this.decimationPrefilterWidth = new float[this.numResolutions];
        for (i = 0; i < this.numResolutions && icps.hasProperty(0x2000000 | (index = i << 16)); ++i) {
            this.highestResolution = i;
            this.subimageValid[i] = true;
            this.subimageWidth[i] = (int)icps.getUI4(0x2000000 | index);
            this.subimageHeight[i] = (int)icps.getUI4(0x2000001 | index);
            byte[] subimageColorBlob = icps.getBlob(0x2000002 | index);
            this.decimationMethod[i] = icps.getI4(0x2000004 | index);
            int numSubImages = FPXUtils.getIntLE(subimageColorBlob, 0);
            int numChannels = FPXUtils.getIntLE(subimageColorBlob, 4);
            this.subimageColor[i] = new int[numChannels];
            for (int c = 0; c < numChannels; ++c) {
                int color = FPXUtils.getIntLE(subimageColorBlob, 8 + 4 * c);
                this.subimageColor[i][c] = color & Integer.MAX_VALUE;
            }
        }
        this.maxJPEGTableIndex = (int)icps.getUI4(0x3000002, -1L);
        this.JPEGTable = new byte[this.maxJPEGTableIndex + 1][];
        for (i = 0; i <= this.maxJPEGTableIndex; ++i) {
            index = i << 16;
            this.JPEGTable[i] = (byte[])(icps.hasProperty(0x3000001 | index) ? icps.getBlob(0x3000001 | index) : null);
        }
    }

    private void readResolution() throws IOException {
        if (this.resolution == -1) {
            this.resolution = this.highestResolution;
        }
        this.storage.changeDirectoryToRoot();
        this.storage.changeDirectory("Data Object Store 000001");
        this.storage.changeDirectory("Resolution 000" + this.resolution);
        this.subimageHeaderStream = this.storage.getStream("Subimage 0000 Header");
        this.subimageHeaderStream.skip(28L);
        int headerLength = this.subimageHeaderStream.readIntLE();
        this.width = this.subimageHeaderStream.readIntLE();
        this.height = this.subimageHeaderStream.readIntLE();
        int numTiles = this.subimageHeaderStream.readIntLE();
        this.tileWidth = this.subimageHeaderStream.readIntLE();
        this.tileHeight = this.subimageHeaderStream.readIntLE();
        this.numChannels = this.subimageHeaderStream.readIntLE();
        this.tileHeaderTableOffset = this.subimageHeaderStream.readIntLE() + 28;
        this.tileHeaderEntryLength = this.subimageHeaderStream.readIntLE();
        this.subimageDataStream = this.storage.getStream("Subimage 0000 Data");
        this.tilesAcross = (this.width + this.tileWidth - 1) / this.tileWidth;
    }

    private int getTileOffset(int tileIndex) throws IOException {
        this.subimageHeaderStream.seek(this.tileHeaderTableOffset + 16 * tileIndex);
        return this.subimageHeaderStream.readIntLE() + 28;
    }

    private int getTileSize(int tileIndex) throws IOException {
        this.subimageHeaderStream.seek(this.tileHeaderTableOffset + 16 * tileIndex + 4);
        return this.subimageHeaderStream.readIntLE();
    }

    private int getCompressionType(int tileIndex) throws IOException {
        this.subimageHeaderStream.seek(this.tileHeaderTableOffset + 16 * tileIndex + 8);
        return this.subimageHeaderStream.readIntLE();
    }

    private int getCompressionSubtype(int tileIndex) throws IOException {
        this.subimageHeaderStream.seek(this.tileHeaderTableOffset + 16 * tileIndex + 12);
        return this.subimageHeaderStream.readIntLE();
    }

    private final byte PhotoYCCToNIFRed(float scaledY, float Cb, float Cr) {
        float red = scaledY + 1.8215f * Cr - 249.55f;
        if (red < 0.0f) {
            return 0;
        }
        if (red > 360.0f) {
            return -1;
        }
        byte r = PhotoYCCToRGBLUT[(int)red];
        return r;
    }

    private final byte PhotoYCCToNIFGreen(float scaledY, float Cb, float Cr) {
        float green = scaledY - 0.43031f * Cb - 0.9271f * Cr + 194.14f;
        if (green < 0.0f) {
            return 0;
        }
        if (green > 360.0f) {
            return -1;
        }
        byte g = PhotoYCCToRGBLUT[(int)green];
        return g;
    }

    private final byte PhotoYCCToNIFBlue(float scaledY, float Cb, float Cr) {
        float blue = scaledY + 2.2179f * Cb - 345.99f;
        if (blue < 0.0f) {
            return 0;
        }
        if (blue > 360.0f) {
            return -1;
        }
        byte b = PhotoYCCToRGBLUT[(int)blue];
        return b;
    }

    private final byte YCCToNIFRed(float Y, float Cb, float Cr) {
        float red = Y + 1.402f * Cr - 178.75499f;
        if (red < 0.0f) {
            return 0;
        }
        if (red > 255.0f) {
            return -1;
        }
        return (byte)red;
    }

    private final byte YCCToNIFGreen(float Y, float Cb, float Cr) {
        float green = Y - 0.34414f * Cb - 0.71414f * Cr + 134.9307f;
        if (green < 0.0f) {
            return 0;
        }
        if (green > 255.0f) {
            return -1;
        }
        return (byte)green;
    }

    private final byte YCCToNIFBlue(float Y, float Cb, float Cr) {
        float blue = Y + 1.772f * Cb - 225.93f;
        if (blue < 0.0f) {
            return 0;
        }
        if (blue > 255.0f) {
            return -1;
        }
        return (byte)blue;
    }

    private Raster getUncompressedTile(int tileX, int tileY) throws IOException {
        int tx = this.tileXToX(tileX);
        int ty = this.tileYToY(tileY);
        WritableRaster ras = RasterFactory.createInterleavedRaster(0, this.tileWidth, this.tileHeight, this.numChannels * this.tileWidth, this.numChannels, this.bandOffsets, new Point(tx, ty));
        DataBufferByte dataBuffer = (DataBufferByte)ras.getDataBuffer();
        byte[] data = dataBuffer.getData();
        int tileIndex = tileY * this.tilesAcross + tileX;
        this.subimageDataStream.seek(this.getTileOffset(tileIndex));
        this.subimageDataStream.readFully(data, 0, this.numChannels * this.tileWidth * this.tileHeight);
        if (this.subimageColor[this.resolution][0] >> 16 == 2) {
            int size = this.tileWidth * this.tileHeight;
            for (int i = 0; i < size; ++i) {
                float Y = data[3 * i] & 0xFF;
                float Cb = data[3 * i + 1] & 0xFF;
                float Cr = data[3 * i + 2] & 0xFF;
                float scaledY = Y * 1.3584f;
                byte red = this.PhotoYCCToNIFRed(scaledY, Cb, Cr);
                byte green = this.PhotoYCCToNIFGreen(scaledY, Cb, Cr);
                byte blue = this.PhotoYCCToNIFBlue(scaledY, Cb, Cr);
                data[3 * i] = red;
                data[3 * i + 1] = green;
                data[3 * i + 2] = blue;
            }
        }
        return ras;
    }

    private Raster getSingleColorCompressedTile(int tileX, int tileY) throws IOException {
        WritableRaster ras;
        block4: {
            int pixels;
            byte blue;
            byte green;
            byte red;
            byte alpha;
            block5: {
                int tx = this.tileXToX(tileX);
                int ty = this.tileYToY(tileY);
                ras = RasterFactory.createInterleavedRaster(0, this.tileWidth, this.tileHeight, this.numChannels * this.tileWidth, this.numChannels, this.bandOffsets, new Point(tx, ty));
                int subimageColorType = this.subimageColor[this.resolution][0] >> 16;
                DataBufferByte dataBuffer = (DataBufferByte)ras.getDataBuffer();
                byte[] data = dataBuffer.getData();
                int tileIndex = tileY * this.tilesAcross + tileX;
                int color = this.getCompressionSubtype(tileIndex);
                byte c0 = (byte)(color >> 0 & 0xFF);
                byte c1 = (byte)(color >> 8 & 0xFF);
                byte c2 = (byte)(color >> 16 & 0xFF);
                alpha = (byte)(color >> 24 & 0xFF);
                if (this.subimageColor[this.resolution][0] >> 16 == 2) {
                    float Y = c0 & 0xFF;
                    float Cb = c1 & 0xFF;
                    float Cr = c2 & 0xFF;
                    float scaledY = Y * 1.3584f;
                    red = this.PhotoYCCToNIFRed(scaledY, Cb, Cr);
                    green = this.PhotoYCCToNIFGreen(scaledY, Cb, Cr);
                    blue = this.PhotoYCCToNIFBlue(scaledY, Cb, Cr);
                } else {
                    red = c0;
                    green = c1;
                    blue = c2;
                }
                int index = 0;
                pixels = this.tileWidth * this.tileHeight;
                if (this.numChannels == 1 || this.numChannels == 2) break block4;
                if (this.numChannels != 3) break block5;
                for (int i = 0; i < pixels; ++i) {
                    data[index + 0] = red;
                    data[index + 1] = green;
                    data[index + 2] = blue;
                    index += 3;
                }
                break block4;
            }
            if (this.numChannels != 4) break block4;
            for (int i = 0; i < pixels; ++i) {
                data[index + 0] = red;
                data[index + 1] = green;
                data[index + 2] = blue;
                data[index + 3] = alpha;
                index += 4;
            }
        }
        return ras;
    }

    private Raster getJPEGCompressedTile(int tileX, int tileY) throws IOException {
        float Cr;
        float Cb;
        float Y;
        int i;
        int offset;
        JPEGImageDecoder dec;
        int tileIndex = tileY * this.tilesAcross + tileX;
        int tx = this.tileXToX(tileX);
        int ty = this.tileYToY(tileY);
        int subtype = this.getCompressionSubtype(tileIndex);
        int interleave = subtype >> 0 & 0xFF;
        int chroma = subtype >> 8 & 0xFF;
        int conversion = subtype >> 16 & 0xFF;
        int table = subtype >> 24 & 0xFF;
        JPEGDecodeParam param = null;
        if (table != 0) {
            ByteArrayInputStream tableStream = new ByteArrayInputStream(this.JPEGTable[table]);
            dec = JPEGCodec.createJPEGDecoder((InputStream)tableStream);
            Raster junk = dec.decodeAsRaster();
            param = dec.getJPEGDecodeParam();
        }
        this.subimageDataStream.seek(this.getTileOffset(tileIndex));
        dec = param != null ? JPEGCodec.createJPEGDecoder((InputStream)this.subimageDataStream, param) : JPEGCodec.createJPEGDecoder((InputStream)this.subimageDataStream);
        Raster ras = dec.decodeAsRaster().createTranslatedChild(tx, ty);
        DataBufferByte dataBuffer = (DataBufferByte)ras.getDataBuffer();
        byte[] data = dataBuffer.getData();
        int subimageColorType = this.subimageColor[this.resolution][0] >> 16;
        int size = this.tileWidth * this.tileHeight;
        if (conversion == 0 && subimageColorType == 2) {
            offset = 0;
            for (i = 0; i < size; ++i) {
                Y = data[offset] & 0xFF;
                Cb = data[offset + 1] & 0xFF;
                Cr = data[offset + 2] & 0xFF;
                float scaledY = Y * 1.3584f;
                byte red = this.PhotoYCCToNIFRed(scaledY, Cb, Cr);
                byte green = this.PhotoYCCToNIFGreen(scaledY, Cb, Cr);
                byte blue = this.PhotoYCCToNIFBlue(scaledY, Cb, Cr);
                data[offset] = red;
                data[offset + 1] = green;
                data[offset + 2] = blue;
                offset += this.numChannels;
            }
        } else if (conversion == 1 && subimageColorType == 3) {
            offset = 0;
            for (i = 0; i < size; ++i) {
                Y = data[offset] & 0xFF;
                Cb = data[offset + 1] & 0xFF;
                Cr = data[offset + 2] & 0xFF;
                byte red = this.YCCToNIFRed(Y, Cb, Cr);
                byte green = this.YCCToNIFGreen(Y, Cb, Cr);
                byte blue = this.YCCToNIFBlue(Y, Cb, Cr);
                data[offset] = red;
                data[offset + 1] = green;
                data[offset + 2] = blue;
                offset += this.numChannels;
            }
        }
        if (conversion == 1 && subimageColorType == 3 && this.numChannels == 4) {
            offset = 0;
            for (i = 0; i < size; ++i) {
                data[offset + 0] = (byte)(255 - data[offset + 0]);
                data[offset + 1] = (byte)(255 - data[offset + 1]);
                data[offset + 2] = (byte)(255 - data[offset + 2]);
                offset += 4;
            }
        }
        return ras;
    }

    public synchronized Raster getTile(int tileX, int tileY) {
        int tileIndex = tileY * this.tilesAcross + tileX;
        try {
            int ctype = this.getCompressionType(tileIndex);
            if (ctype == 0) {
                return this.getUncompressedTile(tileX, tileY);
            }
            if (ctype == 1) {
                return this.getSingleColorCompressedTile(tileX, tileY);
            }
            if (ctype == 2) {
                return this.getJPEGCompressedTile(tileX, tileY);
            }
            return null;
        }
        catch (IOException e) {
            ImagingListenerProxy.errorOccurred(JaiI18N.getString("FPXImage0"), e, class$com$sun$media$jai$codec$ImageCodec == null ? (class$com$sun$media$jai$codec$ImageCodec = FPXImage.class$("com.sun.media.jai.codec.ImageCodec")) : class$com$sun$media$jai$codec$ImageCodec, false);
            return null;
        }
    }

    private void addLPSTRProperty(String name, PropertySet ps, int id) {
        String s = ps.getLPSTR(id);
        if (s != null) {
            this.properties.put(name.toLowerCase(), s);
        }
    }

    private void addLPWSTRProperty(String name, PropertySet ps, int id) {
        String s = ps.getLPWSTR(id);
        if (s != null) {
            this.properties.put(name.toLowerCase(), s);
        }
    }

    private void addUI4Property(String name, PropertySet ps, int id) {
        if (ps.hasProperty(id)) {
            long i = ps.getUI4(id);
            this.properties.put(name.toLowerCase(), new Integer((int)i));
        }
    }

    private void getSummaryInformation() {
        SeekableStream summaryInformation = null;
        PropertySet sips = null;
        try {
            this.storage.changeDirectoryToRoot();
            summaryInformation = this.storage.getStream("\u0005SummaryInformation");
            sips = new PropertySet(summaryInformation);
        }
        catch (IOException e) {
            ImagingListenerProxy.errorOccurred(JaiI18N.getString("FPXImage1"), e, class$com$sun$media$jai$codec$ImageCodec == null ? (class$com$sun$media$jai$codec$ImageCodec = FPXImage.class$("com.sun.media.jai.codec.ImageCodec")) : class$com$sun$media$jai$codec$ImageCodec, false);
            return;
        }
        this.addLPSTRProperty("title", sips, 2);
        this.addLPSTRProperty("subject", sips, 3);
        this.addLPSTRProperty("author", sips, 4);
        this.addLPSTRProperty("keywords", sips, 5);
        this.addLPSTRProperty("comments", sips, 6);
        this.addLPSTRProperty("template", sips, 7);
        this.addLPSTRProperty("last saved by", sips, 8);
        this.addLPSTRProperty("revision number", sips, 9);
    }

    private void getImageInfo() {
        SeekableStream imageInfo = null;
        PropertySet iips = null;
        try {
            this.storage.changeDirectoryToRoot();
            imageInfo = this.storage.getStream("\u0005Image Info");
            if (imageInfo == null) {
                return;
            }
            iips = new PropertySet(imageInfo);
        }
        catch (IOException e) {
            ImagingListenerProxy.errorOccurred(JaiI18N.getString("FPXImage2"), e, class$com$sun$media$jai$codec$ImageCodec == null ? (class$com$sun$media$jai$codec$ImageCodec = FPXImage.class$("com.sun.media.jai.codec.ImageCodec")) : class$com$sun$media$jai$codec$ImageCodec, false);
            return;
        }
        this.addUI4Property("file source", iips, 0x21000000);
        this.addUI4Property("scene type", iips, 0x21000001);
        this.addLPWSTRProperty("software name/manufacturer/release", iips, 553648131);
        this.addLPWSTRProperty("user defined id", iips, 553648132);
        this.addLPWSTRProperty("copyright message", iips, 0x22000000);
        this.addLPWSTRProperty("legal broker for the original image", iips, 0x22000001);
        this.addLPWSTRProperty("legal broker for the digital image", iips, 0x22000002);
        this.addLPWSTRProperty("authorship", iips, 0x22000003);
        this.addLPWSTRProperty("intellectual property notes", iips, 0x22000004);
    }

    private synchronized void getProperties() {
        if (this.properties != null) {
            return;
        }
        this.properties = new Hashtable();
        this.getSummaryInformation();
        this.getImageInfo();
        this.properties.put("max_resolution", new Integer(this.highestResolution));
    }

    public String[] getPropertyNames() {
        this.getProperties();
        int len = this.properties.size();
        String[] names = new String[len];
        Enumeration enumeration = this.properties.keys();
        int count = 0;
        while (enumeration.hasMoreElements()) {
            names[count++] = (String)enumeration.nextElement();
        }
        return names;
    }

    public Object getProperty(String name) {
        this.getProperties();
        return this.properties.get(name.toLowerCase());
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

