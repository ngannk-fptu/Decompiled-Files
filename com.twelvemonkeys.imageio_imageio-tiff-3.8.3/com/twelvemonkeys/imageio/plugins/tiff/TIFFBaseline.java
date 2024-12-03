/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.plugins.tiff;

interface TIFFBaseline {
    public static final int COMPRESSION_NONE = 1;
    public static final int COMPRESSION_CCITT_MODIFIED_HUFFMAN_RLE = 2;
    public static final int COMPRESSION_PACKBITS = 32773;
    public static final int PHOTOMETRIC_WHITE_IS_ZERO = 0;
    public static final int PHOTOMETRIC_BLACK_IS_ZERO = 1;
    public static final int PHOTOMETRIC_RGB = 2;
    public static final int PHOTOMETRIC_PALETTE = 3;
    public static final int PHOTOMETRIC_MASK = 4;
    public static final int SAMPLEFORMAT_UINT = 1;
    public static final int PLANARCONFIG_CHUNKY = 1;
    public static final int EXTRASAMPLE_UNSPECIFIED = 0;
    public static final int EXTRASAMPLE_ASSOCIATED_ALPHA = 1;
    public static final int EXTRASAMPLE_UNASSOCIATED_ALPHA = 2;
    public static final int PREDICTOR_NONE = 1;
    public static final int RESOLUTION_UNIT_NONE = 1;
    public static final int RESOLUTION_UNIT_DPI = 2;
    public static final int RESOLUTION_UNIT_CENTIMETER = 3;
    public static final int FILL_LEFT_TO_RIGHT = 1;
    public static final int FILETYPE_REDUCEDIMAGE = 1;
    public static final int FILETYPE_PAGE = 2;
    public static final int FILETYPE_MASK = 4;
    public static final int ORIENTATION_TOPLEFT = 1;
}

