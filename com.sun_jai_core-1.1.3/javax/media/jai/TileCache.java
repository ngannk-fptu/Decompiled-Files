/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.Point;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.util.Comparator;

public interface TileCache {
    public void add(RenderedImage var1, int var2, int var3, Raster var4);

    public void add(RenderedImage var1, int var2, int var3, Raster var4, Object var5);

    public void remove(RenderedImage var1, int var2, int var3);

    public Raster getTile(RenderedImage var1, int var2, int var3);

    public Raster[] getTiles(RenderedImage var1);

    public void removeTiles(RenderedImage var1);

    public void addTiles(RenderedImage var1, Point[] var2, Raster[] var3, Object var4);

    public Raster[] getTiles(RenderedImage var1, Point[] var2);

    public void flush();

    public void memoryControl();

    public void setTileCapacity(int var1);

    public int getTileCapacity();

    public void setMemoryCapacity(long var1);

    public long getMemoryCapacity();

    public void setMemoryThreshold(float var1);

    public float getMemoryThreshold();

    public void setTileComparator(Comparator var1);

    public Comparator getTileComparator();
}

