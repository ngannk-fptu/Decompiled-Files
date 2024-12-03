/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.png.scanlinefilters;

import java.io.IOException;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.formats.png.scanlinefilters.ScanlineFilter;

public class ScanlineFilterNone
implements ScanlineFilter {
    @Override
    public void unfilter(byte[] src, byte[] dst, byte[] up) throws ImageReadException, IOException {
        System.arraycopy(src, 0, dst, 0, src.length);
    }
}

