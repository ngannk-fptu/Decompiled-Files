/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.png.scanlinefilters;

import java.io.IOException;
import org.apache.commons.imaging.ImageReadException;

public interface ScanlineFilter {
    public void unfilter(byte[] var1, byte[] var2, byte[] var3) throws ImageReadException, IOException;
}

