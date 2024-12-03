/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.png.scanlinefilters;

import java.io.IOException;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.formats.png.scanlinefilters.ScanlineFilter;

public class ScanlineFilterSub
implements ScanlineFilter {
    private final int bytesPerPixel;

    public ScanlineFilterSub(int bytesPerPixel) {
        this.bytesPerPixel = bytesPerPixel;
    }

    @Override
    public void unfilter(byte[] src, byte[] dst, byte[] up) throws ImageReadException, IOException {
        for (int i = 0; i < src.length; ++i) {
            int prevIndex = i - this.bytesPerPixel;
            dst[i] = prevIndex >= 0 ? (byte)((src[i] + dst[prevIndex]) % 256) : src[i];
        }
    }
}

