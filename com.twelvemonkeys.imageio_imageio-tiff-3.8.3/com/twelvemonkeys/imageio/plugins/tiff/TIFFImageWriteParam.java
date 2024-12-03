/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.plugins.tiff;

import java.util.Locale;
import javax.imageio.ImageWriteParam;

public final class TIFFImageWriteParam
extends ImageWriteParam {
    TIFFImageWriteParam() {
        this(Locale.getDefault());
    }

    TIFFImageWriteParam(Locale locale) {
        super(locale);
        this.compressionTypes = new String[]{"None", "CCITT RLE", "CCITT T.4", "CCITT T.6", "LZW", "JPEG", "ZLib", "PackBits", "Deflate", null};
        this.compressionType = this.compressionTypes[0];
        this.canWriteCompressed = true;
    }

    @Override
    public float[] getCompressionQualityValues() {
        super.getCompressionQualityValues();
        return null;
    }

    @Override
    public String[] getCompressionQualityDescriptions() {
        super.getCompressionQualityDescriptions();
        return null;
    }

    static int getCompressionType(ImageWriteParam imageWriteParam) {
        if (imageWriteParam == null || imageWriteParam.getCompressionMode() != 2 || imageWriteParam.getCompressionType() == null || imageWriteParam.getCompressionType().equals("None")) {
            return 1;
        }
        if (imageWriteParam.getCompressionType().equals("PackBits")) {
            return 32773;
        }
        if (imageWriteParam.getCompressionType().equals("ZLib")) {
            return 8;
        }
        if (imageWriteParam.getCompressionType().equals("Deflate")) {
            return 32946;
        }
        if (imageWriteParam.getCompressionType().equals("LZW")) {
            return 5;
        }
        if (imageWriteParam.getCompressionType().equals("JPEG")) {
            return 7;
        }
        if (imageWriteParam.getCompressionType().equals("CCITT RLE")) {
            return 2;
        }
        if (imageWriteParam.getCompressionType().equals("CCITT T.4")) {
            return 3;
        }
        if (imageWriteParam.getCompressionType().equals("CCITT T.6")) {
            return 4;
        }
        throw new IllegalArgumentException(String.format("Unsupported compression type: %s", imageWriteParam.getCompressionType()));
    }
}

