/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.png.transparencyfilters;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.BinaryFunctions;
import org.apache.commons.imaging.formats.png.transparencyfilters.TransparencyFilter;

public class TransparencyFilterTrueColor
extends TransparencyFilter {
    private final int transparentColor;

    public TransparencyFilterTrueColor(byte[] bytes) throws IOException {
        super(bytes);
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        int transparentRed = BinaryFunctions.read2Bytes("transparentRed", is, "tRNS: Missing transparentColor", this.getByteOrder());
        int transparentGreen = BinaryFunctions.read2Bytes("transparentGreen", is, "tRNS: Missing transparentColor", this.getByteOrder());
        int transparentBlue = BinaryFunctions.read2Bytes("transparentBlue", is, "tRNS: Missing transparentColor", this.getByteOrder());
        this.transparentColor = (0xFF & transparentRed) << 16 | (0xFF & transparentGreen) << 8 | (0xFF & transparentBlue) << 0;
    }

    @Override
    public int filter(int rgb, int sample) throws ImageReadException, IOException {
        if ((0xFFFFFF & rgb) == this.transparentColor) {
            return 0;
        }
        return rgb;
    }
}

