/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff;

import java.nio.ByteOrder;
import org.apache.commons.imaging.formats.tiff.TiffElement;

public class TiffHeader
extends TiffElement {
    public final ByteOrder byteOrder;
    public final int tiffVersion;
    public final long offsetToFirstIFD;

    public TiffHeader(ByteOrder byteOrder, int tiffVersion, long offsetToFirstIFD) {
        super(0L, 8);
        this.byteOrder = byteOrder;
        this.tiffVersion = tiffVersion;
        this.offsetToFirstIFD = offsetToFirstIFD;
    }

    @Override
    public String getElementDescription() {
        return "TIFF Header";
    }
}

