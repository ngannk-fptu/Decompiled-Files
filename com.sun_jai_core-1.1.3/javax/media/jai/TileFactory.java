/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.Point;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

public interface TileFactory {
    public boolean canReclaimMemory();

    public boolean isMemoryCache();

    public long getMemoryUsed();

    public void flush();

    public WritableRaster createTile(SampleModel var1, Point var2);
}

