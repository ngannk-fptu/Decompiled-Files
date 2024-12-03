/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.core.util.bean;

public class ImageInfo
extends com.atlassian.core.util.ImageInfo {
    public static final int FORMAT_JPEG = 0;
    public static final int FORMAT_GIF = 0;
    public static final int FORMAT_PNG = 2;
    public static final int FORMAT_BMP = 3;
    public static final int FORMAT_PCX = 4;
    public static final int FORMAT_IFF = 5;
    public static final int FORMAT_RAS = 6;
    public static final int FORMAT_PBM = 7;
    public static final int FORMAT_PGM = 8;
    public static final int FORMAT_PPM = 9;
    public static final int FORMAT_PSD = 10;
    public static final int FORMAT_SWF = 11;

    public boolean isValidImage() {
        return super.check();
    }
}

