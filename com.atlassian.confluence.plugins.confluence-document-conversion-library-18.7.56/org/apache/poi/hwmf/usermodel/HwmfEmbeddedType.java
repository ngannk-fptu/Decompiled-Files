/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwmf.usermodel;

public enum HwmfEmbeddedType {
    BITMAP(".bitmap"),
    WMF(".wmf"),
    EMF(".emf"),
    EPS(".eps"),
    JPEG(".jpg"),
    GIF(".gif"),
    TIFF(".tiff"),
    PNG(".png"),
    BMP(".bmp"),
    UNKNOWN(".dat");

    public final String extension;

    private HwmfEmbeddedType(String extension) {
        this.extension = extension;
    }
}

