/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.plugins.bmp;

interface DIB {
    public static final int TYPE_ICO = 1;
    public static final int TYPE_CUR = 2;
    public static final int BMP_FILE_HEADER_SIZE = 14;
    public static final int BITMAP_CORE_HEADER_SIZE = 12;
    public static final int OS2_V2_HEADER_16_SIZE = 16;
    public static final int OS2_V2_HEADER_SIZE = 64;
    public static final int BITMAP_INFO_HEADER_SIZE = 40;
    public static final int BITMAP_V2_INFO_HEADER_SIZE = 52;
    public static final int BITMAP_V3_INFO_HEADER_SIZE = 56;
    public static final int BITMAP_V4_INFO_HEADER_SIZE = 108;
    public static final int BITMAP_V5_INFO_HEADER_SIZE = 124;
    public static final int COMPRESSION_RGB = 0;
    public static final int COMPRESSION_RLE8 = 1;
    public static final int COMPRESSION_RLE4 = 2;
    public static final int COMPRESSION_BITFIELDS = 3;
    public static final int COMPRESSION_JPEG = 4;
    public static final int COMPRESSION_PNG = 5;
    public static final int COMPRESSION_ALPHA_BITFIELDS = 6;
    public static final int LCS_CALIBRATED_RGB = 0;
    public static final int LCS_sRGB = 1934772034;
    public static final int LCS_WINDOWS_COLOR_SPACE = 1466527264;
    public static final int PROFILE_LINKED = 1279872587;
    public static final int PROFILE_EMBEDDED = 1296188740;
    public static final long PNG_MAGIC = -8552249625308161526L;
}

