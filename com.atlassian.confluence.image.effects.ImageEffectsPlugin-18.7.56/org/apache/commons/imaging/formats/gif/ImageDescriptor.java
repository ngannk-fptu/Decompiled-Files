/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.gif;

import org.apache.commons.imaging.formats.gif.GifBlock;

class ImageDescriptor
extends GifBlock {
    final int imageLeftPosition;
    final int imageTopPosition;
    final int imageWidth;
    final int imageHeight;
    final byte packedFields;
    final boolean localColorTableFlag;
    final boolean interlaceFlag;
    final boolean sortFlag;
    final byte sizeOfLocalColorTable;
    final byte[] localColorTable;
    final byte[] imageData;

    ImageDescriptor(int blockCode, int imageLeftPosition, int imageTopPosition, int imageWidth, int imageHeight, byte packedFields, boolean localColorTableFlag, boolean interlaceFlag, boolean sortFlag, byte sizeofLocalColorTable, byte[] localColorTable, byte[] imageData) {
        super(blockCode);
        this.imageLeftPosition = imageLeftPosition;
        this.imageTopPosition = imageTopPosition;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.packedFields = packedFields;
        this.localColorTableFlag = localColorTableFlag;
        this.interlaceFlag = interlaceFlag;
        this.sortFlag = sortFlag;
        this.sizeOfLocalColorTable = sizeofLocalColorTable;
        this.localColorTable = localColorTable;
        this.imageData = imageData;
    }
}

