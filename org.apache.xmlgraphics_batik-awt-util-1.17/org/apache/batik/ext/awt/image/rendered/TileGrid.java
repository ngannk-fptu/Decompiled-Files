/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.util.HaltingThread
 */
package org.apache.batik.ext.awt.image.rendered;

import java.awt.image.Raster;
import org.apache.batik.ext.awt.image.rendered.LRUCache;
import org.apache.batik.ext.awt.image.rendered.TileGenerator;
import org.apache.batik.ext.awt.image.rendered.TileLRUMember;
import org.apache.batik.ext.awt.image.rendered.TileStore;
import org.apache.batik.util.HaltingThread;

public class TileGrid
implements TileStore {
    private static final boolean DEBUG = false;
    private static final boolean COUNT = false;
    private int xSz;
    private int ySz;
    private int minTileX;
    private int minTileY;
    private TileLRUMember[][] rasters = null;
    private TileGenerator source = null;
    private LRUCache cache = null;
    static int requests;
    static int misses;

    public TileGrid(int minTileX, int minTileY, int xSz, int ySz, TileGenerator source, LRUCache cache) {
        this.cache = cache;
        this.source = source;
        this.minTileX = minTileX;
        this.minTileY = minTileY;
        this.xSz = xSz;
        this.ySz = ySz;
        this.rasters = new TileLRUMember[ySz][];
    }

    @Override
    public void setTile(int x, int y, Raster ras) {
        TileLRUMember item;
        y -= this.minTileY;
        if ((x -= this.minTileX) < 0 || x >= this.xSz) {
            return;
        }
        if (y < 0 || y >= this.ySz) {
            return;
        }
        TileLRUMember[] row = this.rasters[y];
        if (ras == null) {
            if (row == null) {
                return;
            }
            TileLRUMember item2 = row[x];
            if (item2 == null) {
                return;
            }
            row[x] = null;
            this.cache.remove(item2);
            return;
        }
        if (row != null) {
            item = row[x];
            if (item == null) {
                row[x] = item = new TileLRUMember();
            }
        } else {
            row = new TileLRUMember[this.xSz];
            row[x] = item = new TileLRUMember();
            this.rasters[y] = row;
        }
        item.setRaster(ras);
        this.cache.add(item);
    }

    @Override
    public Raster getTileNoCompute(int x, int y) {
        y -= this.minTileY;
        if ((x -= this.minTileX) < 0 || x >= this.xSz) {
            return null;
        }
        if (y < 0 || y >= this.ySz) {
            return null;
        }
        TileLRUMember[] row = this.rasters[y];
        if (row == null) {
            return null;
        }
        TileLRUMember item = row[x];
        if (item == null) {
            return null;
        }
        Raster ret = item.retrieveRaster();
        if (ret != null) {
            this.cache.add(item);
        }
        return ret;
    }

    @Override
    public Raster getTile(int x, int y) {
        y -= this.minTileY;
        if ((x -= this.minTileX) < 0 || x >= this.xSz) {
            return null;
        }
        if (y < 0 || y >= this.ySz) {
            return null;
        }
        Raster ras = null;
        TileLRUMember[] row = this.rasters[y];
        TileLRUMember item = null;
        if (row != null) {
            item = row[x];
            if (item != null) {
                ras = item.retrieveRaster();
            } else {
                row[x] = item = new TileLRUMember();
            }
        } else {
            row = new TileLRUMember[this.xSz];
            this.rasters[y] = row;
            row[x] = item = new TileLRUMember();
        }
        if (ras == null) {
            ras = this.source.genTile(x + this.minTileX, y + this.minTileY);
            if (HaltingThread.hasBeenHalted()) {
                return ras;
            }
            item.setRaster(ras);
        }
        this.cache.add(item);
        return ras;
    }
}

