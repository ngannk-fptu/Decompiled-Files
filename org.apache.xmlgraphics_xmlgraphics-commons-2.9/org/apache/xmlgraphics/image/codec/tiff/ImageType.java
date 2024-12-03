/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.codec.tiff;

import java.awt.color.ColorSpace;
import org.apache.xmlgraphics.image.codec.tiff.TIFFEncodeParam;
import org.apache.xmlgraphics.image.codec.util.PropertyUtil;

enum ImageType {
    UNSUPPORTED(-1),
    BILEVEL_WHITE_IS_ZERO(0),
    BILEVEL_BLACK_IS_ZERO(1),
    GRAY(1),
    PALETTE(3),
    RGB(2),
    CMYK(5),
    YCBCR(6),
    CIELAB(8),
    GENERIC(1);

    private final int photometricInterpretation;

    private ImageType(int photometricInterpretation) {
        this.photometricInterpretation = photometricInterpretation;
    }

    int getPhotometricInterpretation() {
        return this.photometricInterpretation;
    }

    static ImageType getTypeFromRGB(int mapSize, byte[] r, byte[] g, byte[] b, int dataTypeSize, int numBands) {
        if (numBands == 1) {
            if (dataTypeSize == 1) {
                if (mapSize != 2) {
                    throw new IllegalArgumentException(PropertyUtil.getString("TIFFImageEncoder7"));
                }
                if (ImageType.isBlackZero(r, g, b)) {
                    return BILEVEL_BLACK_IS_ZERO;
                }
                if (ImageType.isWhiteZero(r, g, b)) {
                    return BILEVEL_WHITE_IS_ZERO;
                }
            }
            return PALETTE;
        }
        return UNSUPPORTED;
    }

    private static boolean rgbIsValueAt(byte[] r, byte[] g, byte[] b, byte value, int i) {
        return r[i] == value && g[i] == value && b[i] == value;
    }

    private static boolean bilevelColorValue(byte[] r, byte[] g, byte[] b, int blackValue, int whiteValue) {
        return ImageType.rgbIsValueAt(r, g, b, (byte)blackValue, 0) && ImageType.rgbIsValueAt(r, g, b, (byte)whiteValue, 1);
    }

    private static boolean isBlackZero(byte[] r, byte[] g, byte[] b) {
        return ImageType.bilevelColorValue(r, g, b, 0, 255);
    }

    private static boolean isWhiteZero(byte[] r, byte[] g, byte[] b) {
        return ImageType.bilevelColorValue(r, g, b, 255, 0);
    }

    static ImageType getTypeFromColorSpace(ColorSpace colorSpace, TIFFEncodeParam params) {
        switch (colorSpace.getType()) {
            case 9: {
                return CMYK;
            }
            case 6: {
                return GRAY;
            }
            case 1: {
                return CIELAB;
            }
            case 5: {
                if (params.getJPEGCompressRGBToYCbCr()) {
                    return YCBCR;
                }
                return RGB;
            }
            case 3: {
                return YCBCR;
            }
        }
        return GENERIC;
    }
}

