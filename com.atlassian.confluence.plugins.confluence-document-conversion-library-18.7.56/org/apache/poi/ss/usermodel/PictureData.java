/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.usermodel;

public interface PictureData {
    public byte[] getData();

    public String suggestFileExtension();

    public String getMimeType();

    public int getPictureType();
}

