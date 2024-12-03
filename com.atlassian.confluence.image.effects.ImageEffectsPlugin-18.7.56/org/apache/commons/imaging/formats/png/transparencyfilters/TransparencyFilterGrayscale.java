/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.png.transparencyfilters;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.BinaryFunctions;
import org.apache.commons.imaging.formats.png.transparencyfilters.TransparencyFilter;

public class TransparencyFilterGrayscale
extends TransparencyFilter {
    private final int transparentColor;

    public TransparencyFilterGrayscale(byte[] bytes) throws IOException {
        super(bytes);
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        this.transparentColor = BinaryFunctions.read2Bytes("transparentColor", is, "tRNS: Missing transparentColor", this.getByteOrder());
    }

    @Override
    public int filter(int rgb, int index) throws ImageReadException, IOException {
        if (index != this.transparentColor) {
            return rgb;
        }
        return 0;
    }
}

