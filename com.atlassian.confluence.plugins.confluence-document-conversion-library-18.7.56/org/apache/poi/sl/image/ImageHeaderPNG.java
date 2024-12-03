/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.sl.image;

import java.util.Arrays;
import org.apache.poi.poifs.filesystem.FileMagic;

public final class ImageHeaderPNG {
    private static final int MAGIC_OFFSET = 16;
    private final byte[] data;

    public ImageHeaderPNG(byte[] data) {
        this.data = data;
    }

    public byte[] extractPNG() {
        byte[] newData;
        if (this.data.length >= 16 && FileMagic.valueOf(newData = Arrays.copyOfRange(this.data, 16, this.data.length)) == FileMagic.PNG) {
            return newData;
        }
        return this.data;
    }
}

