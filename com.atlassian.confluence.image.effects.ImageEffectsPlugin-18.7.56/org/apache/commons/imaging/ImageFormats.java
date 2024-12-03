/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging;

import org.apache.commons.imaging.ImageFormat;

public enum ImageFormats implements ImageFormat
{
    UNKNOWN,
    BMP,
    DCX,
    GIF,
    ICNS,
    ICO,
    JBIG2,
    JPEG,
    PAM,
    PSD,
    PBM,
    PGM,
    PNM,
    PPM,
    PCX,
    PNG,
    RGBE,
    TGA,
    TIFF,
    WBMP,
    XBM,
    XPM;


    @Override
    public String getName() {
        return this.name();
    }

    @Override
    public String getExtension() {
        return this.name();
    }
}

