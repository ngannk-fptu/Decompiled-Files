/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.codec.tiff;

public enum CompressionValue {
    NONE(1),
    GROUP3_1D(2),
    GROUP3_2D(3),
    GROUP4(4),
    LZW(5),
    JPEG_BROKEN(6),
    JPEG_TTN2(7),
    PACKBITS(32773),
    DEFLATE(32946);

    private final int compressionValue;

    private CompressionValue(int compressionValue) {
        this.compressionValue = compressionValue;
    }

    int getValue() {
        return this.compressionValue;
    }

    public static CompressionValue getValue(String name) {
        if (name == null) {
            return PACKBITS;
        }
        for (CompressionValue cv : CompressionValue.values()) {
            if (!cv.toString().equalsIgnoreCase(name)) continue;
            return cv;
        }
        throw new IllegalArgumentException("Unknown compression value: " + name);
    }
}

