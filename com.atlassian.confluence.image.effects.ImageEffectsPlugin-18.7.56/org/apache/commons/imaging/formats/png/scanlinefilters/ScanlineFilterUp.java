/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.png.scanlinefilters;

import java.io.IOException;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.formats.png.scanlinefilters.ScanlineFilter;

public class ScanlineFilterUp
implements ScanlineFilter {
    @Override
    public void unfilter(byte[] src, byte[] dst, byte[] up) throws ImageReadException, IOException {
        for (int i = 0; i < src.length; ++i) {
            dst[i] = up != null ? (byte)((src[i] + up[i]) % 256) : src[i];
        }
    }
}

