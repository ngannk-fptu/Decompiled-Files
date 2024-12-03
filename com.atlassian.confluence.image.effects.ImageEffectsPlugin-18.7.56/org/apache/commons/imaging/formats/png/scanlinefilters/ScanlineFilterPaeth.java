/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.png.scanlinefilters;

import java.io.IOException;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.formats.png.scanlinefilters.ScanlineFilter;

public class ScanlineFilterPaeth
implements ScanlineFilter {
    private final int bytesPerPixel;

    public ScanlineFilterPaeth(int bytesPerPixel) {
        this.bytesPerPixel = bytesPerPixel;
    }

    private int paethPredictor(int a, int b, int c) {
        int p = a + b - c;
        int pa = Math.abs(p - a);
        int pb = Math.abs(p - b);
        int pc = Math.abs(p - c);
        if (pa <= pb && pa <= pc) {
            return a;
        }
        if (pb <= pc) {
            return b;
        }
        return c;
    }

    @Override
    public void unfilter(byte[] src, byte[] dst, byte[] up) throws ImageReadException, IOException {
        for (int i = 0; i < src.length; ++i) {
            byte left = 0;
            int prevIndex = i - this.bytesPerPixel;
            if (prevIndex >= 0) {
                left = dst[prevIndex];
            }
            byte above = 0;
            if (up != null) {
                above = up[i];
            }
            byte upperleft = 0;
            if (prevIndex >= 0 && up != null) {
                upperleft = up[prevIndex];
            }
            int paethPredictor = this.paethPredictor(0xFF & left, 0xFF & above, 0xFF & upperleft);
            dst[i] = (byte)((src[i] + paethPredictor) % 256);
        }
    }
}

