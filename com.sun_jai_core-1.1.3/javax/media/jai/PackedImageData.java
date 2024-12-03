/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.Rectangle;
import java.awt.image.Raster;

public final class PackedImageData {
    public final Raster raster;
    public final Rectangle rect;
    public final byte[] data;
    public final int lineStride;
    public final int offset;
    public final int bitOffset;
    public final boolean coercedZeroOffset;
    public final boolean convertToDest;

    public PackedImageData(Raster raster, Rectangle rect, byte[] data, int lineStride, int offset, int bitOffset, boolean coercedZeroOffset, boolean convertToDest) {
        this.raster = raster;
        this.rect = rect;
        this.data = data;
        this.lineStride = lineStride;
        this.offset = offset;
        this.bitOffset = bitOffset;
        this.coercedZeroOffset = coercedZeroOffset;
        this.convertToDest = convertToDest;
    }
}

