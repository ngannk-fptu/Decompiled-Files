/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.png.transparencyfilters;

import java.io.IOException;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.formats.png.transparencyfilters.TransparencyFilter;

public class TransparencyFilterIndexedColor
extends TransparencyFilter {
    public TransparencyFilterIndexedColor(byte[] bytes) {
        super(bytes);
    }

    @Override
    public int filter(int rgb, int index) throws ImageReadException, IOException {
        int length = this.getLength();
        if (index >= length) {
            return rgb;
        }
        if (index < 0 || index > length) {
            throw new ImageReadException("TransparencyFilterIndexedColor index: " + index + ", bytes.length: " + length);
        }
        byte alpha = this.getByte(index);
        return (0xFF & alpha) << 24 | 0xFFFFFF & rgb;
    }
}

