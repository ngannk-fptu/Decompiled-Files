/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.image.Raster;
import java.awt.image.RenderedImage;

public interface CachedTile {
    public RenderedImage getOwner();

    public Raster getTile();

    public Object getTileCacheMetric();

    public long getTileTimeStamp();

    public long getTileSize();

    public int getAction();
}

