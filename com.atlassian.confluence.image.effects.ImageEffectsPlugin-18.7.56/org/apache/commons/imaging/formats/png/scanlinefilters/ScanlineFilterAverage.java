/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.png.scanlinefilters;

import java.io.IOException;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.formats.png.scanlinefilters.ScanlineFilter;

public class ScanlineFilterAverage
implements ScanlineFilter {
    private final int bytesPerPixel;

    public ScanlineFilterAverage(int bytesPerPixel) {
        this.bytesPerPixel = bytesPerPixel;
    }

    @Override
    public void unfilter(byte[] src, byte[] dst, byte[] up) throws ImageReadException, IOException {
        for (int i = 0; i < src.length; ++i) {
            byte raw = 0;
            int prevIndex = i - this.bytesPerPixel;
            if (prevIndex >= 0) {
                raw = dst[prevIndex];
            }
            byte prior = 0;
            if (up != null) {
                prior = up[i];
            }
            int average = ((0xFF & raw) + (0xFF & prior)) / 2;
            dst[i] = (byte)((src[i] + average) % 256);
        }
    }
}

