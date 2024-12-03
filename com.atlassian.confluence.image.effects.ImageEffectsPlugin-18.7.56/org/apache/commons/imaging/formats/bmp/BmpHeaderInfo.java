/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.bmp;

class BmpHeaderInfo {
    public final byte identifier1;
    public final byte identifier2;
    public final int fileSize;
    public final int reserved;
    public final int bitmapDataOffset;
    public final int bitmapHeaderSize;
    public final int width;
    public final int height;
    public final int planes;
    public final int bitsPerPixel;
    public final int compression;
    public final int bitmapDataSize;
    public final int hResolution;
    public final int vResolution;
    public final int colorsUsed;
    public final int colorsImportant;
    public final int redMask;
    public final int greenMask;
    public final int blueMask;
    public final int alphaMask;
    public final int colorSpaceType;
    public final ColorSpace colorSpace;
    public final int gammaRed;
    public final int gammaGreen;
    public final int gammaBlue;
    public final int intent;
    public final int profileData;
    public final int profileSize;
    public final int reservedV5;

    BmpHeaderInfo(byte identifier1, byte identifier2, int fileSize, int reserved, int bitmapDataOffset, int bitmapHeaderSize, int width, int height, int planes, int bitsPerPixel, int compression, int bitmapDataSize, int hResolution, int vResolution, int colorsUsed, int colorsImportant, int redMask, int greenMask, int blueMask, int alphaMask, int colorSpaceType, ColorSpace colorSpace, int gammaRed, int gammaGreen, int gammaBlue, int intent, int profileData, int profileSize, int reservedV5) {
        this.identifier1 = identifier1;
        this.identifier2 = identifier2;
        this.fileSize = fileSize;
        this.reserved = reserved;
        this.bitmapDataOffset = bitmapDataOffset;
        this.bitmapHeaderSize = bitmapHeaderSize;
        this.width = width;
        this.height = height;
        this.planes = planes;
        this.bitsPerPixel = bitsPerPixel;
        this.compression = compression;
        this.bitmapDataSize = bitmapDataSize;
        this.hResolution = hResolution;
        this.vResolution = vResolution;
        this.colorsUsed = colorsUsed;
        this.colorsImportant = colorsImportant;
        this.redMask = redMask;
        this.greenMask = greenMask;
        this.blueMask = blueMask;
        this.alphaMask = alphaMask;
        this.colorSpaceType = colorSpaceType;
        this.colorSpace = colorSpace;
        this.gammaRed = gammaRed;
        this.gammaGreen = gammaGreen;
        this.gammaBlue = gammaBlue;
        this.intent = intent;
        this.profileData = profileData;
        this.profileSize = profileSize;
        this.reservedV5 = reservedV5;
    }

    static class ColorSpace {
        ColorSpaceCoordinate red;
        ColorSpaceCoordinate green;
        ColorSpaceCoordinate blue;

        ColorSpace() {
        }
    }

    static class ColorSpaceCoordinate {
        int x;
        int y;
        int z;

        ColorSpaceCoordinate() {
        }
    }
}

