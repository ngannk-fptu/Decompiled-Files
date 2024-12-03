/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.gif;

class GifHeaderInfo {
    public final byte identifier1;
    public final byte identifier2;
    public final byte identifier3;
    public final byte version1;
    public final byte version2;
    public final byte version3;
    public final int logicalScreenWidth;
    public final int logicalScreenHeight;
    public final byte packedFields;
    public final byte backgroundColorIndex;
    public final byte pixelAspectRatio;
    public final boolean globalColorTableFlag;
    public final byte colorResolution;
    public final boolean sortFlag;
    public final byte sizeOfGlobalColorTable;

    GifHeaderInfo(byte identifier1, byte identifier2, byte identifier3, byte version1, byte version2, byte version3, int logicalScreenWidth, int logicalScreenHeight, byte packedFields, byte backgroundColorIndex, byte pixelAspectRatio, boolean globalColorTableFlag, byte colorResolution, boolean sortFlag, byte sizeOfGlobalColorTable) {
        this.identifier1 = identifier1;
        this.identifier2 = identifier2;
        this.identifier3 = identifier3;
        this.version1 = version1;
        this.version2 = version2;
        this.version3 = version3;
        this.logicalScreenWidth = logicalScreenWidth;
        this.logicalScreenHeight = logicalScreenHeight;
        this.packedFields = packedFields;
        this.backgroundColorIndex = backgroundColorIndex;
        this.pixelAspectRatio = pixelAspectRatio;
        this.globalColorTableFlag = globalColorTableFlag;
        this.colorResolution = colorResolution;
        this.sortFlag = sortFlag;
        this.sizeOfGlobalColorTable = sizeOfGlobalColorTable;
    }
}

