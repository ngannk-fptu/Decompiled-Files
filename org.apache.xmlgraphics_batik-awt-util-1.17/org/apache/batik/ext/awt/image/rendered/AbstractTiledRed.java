/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.util.HaltingThread
 */
package org.apache.batik.ext.awt.image.rendered;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.List;
import java.util.Map;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.rendered.AbstractRed;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.TileBlock;
import org.apache.batik.ext.awt.image.rendered.TileCache;
import org.apache.batik.ext.awt.image.rendered.TileGenerator;
import org.apache.batik.ext.awt.image.rendered.TileStore;
import org.apache.batik.util.HaltingThread;

public abstract class AbstractTiledRed
extends AbstractRed
implements TileGenerator {
    private TileStore tiles;
    private static int defaultTileSize = 128;

    public static int getDefaultTileSize() {
        return defaultTileSize;
    }

    protected AbstractTiledRed() {
    }

    protected AbstractTiledRed(Rectangle bounds, Map props) {
        super(bounds, props);
    }

    protected AbstractTiledRed(CachableRed src, Map props) {
        super(src, props);
    }

    protected AbstractTiledRed(CachableRed src, Rectangle bounds, Map props) {
        super(src, bounds, props);
    }

    protected AbstractTiledRed(CachableRed src, Rectangle bounds, ColorModel cm, SampleModel sm, Map props) {
        super(src, bounds, cm, sm, props);
    }

    protected AbstractTiledRed(CachableRed src, Rectangle bounds, ColorModel cm, SampleModel sm, int tileGridXOff, int tileGridYOff, Map props) {
        super(src, bounds, cm, sm, tileGridXOff, tileGridYOff, props);
    }

    @Override
    protected void init(CachableRed src, Rectangle bounds, ColorModel cm, SampleModel sm, int tileGridXOff, int tileGridYOff, Map props) {
        this.init(src, bounds, cm, sm, tileGridXOff, tileGridYOff, null, props);
    }

    protected void init(CachableRed src, Rectangle bounds, ColorModel cm, SampleModel sm, int tileGridXOff, int tileGridYOff, TileStore tiles, Map props) {
        super.init(src, bounds, cm, sm, tileGridXOff, tileGridYOff, props);
        this.tiles = tiles;
        if (this.tiles == null) {
            this.tiles = this.createTileStore();
        }
    }

    protected AbstractTiledRed(List srcs, Rectangle bounds, Map props) {
        super(srcs, bounds, props);
    }

    protected AbstractTiledRed(List srcs, Rectangle bounds, ColorModel cm, SampleModel sm, Map props) {
        super(srcs, bounds, cm, sm, props);
    }

    protected AbstractTiledRed(List srcs, Rectangle bounds, ColorModel cm, SampleModel sm, int tileGridXOff, int tileGridYOff, Map props) {
        super(srcs, bounds, cm, sm, tileGridXOff, tileGridYOff, props);
    }

    @Override
    protected void init(List srcs, Rectangle bounds, ColorModel cm, SampleModel sm, int tileGridXOff, int tileGridYOff, Map props) {
        super.init(srcs, bounds, cm, sm, tileGridXOff, tileGridYOff, props);
        this.tiles = this.createTileStore();
    }

    public TileStore getTileStore() {
        return this.tiles;
    }

    protected void setTileStore(TileStore tiles) {
        this.tiles = tiles;
    }

    protected TileStore createTileStore() {
        return TileCache.getTileMap(this);
    }

    @Override
    public WritableRaster copyData(WritableRaster wr) {
        this.copyToRasterByBlocks(wr);
        return wr;
    }

    @Override
    public Raster getData(Rectangle rect) {
        int xt0 = this.getXTile(rect.x);
        int xt1 = this.getXTile(rect.x + rect.width - 1);
        int yt0 = this.getYTile(rect.y);
        int yt1 = this.getYTile(rect.y + rect.height - 1);
        if (xt0 == xt1 && yt0 == yt1) {
            Raster r = this.getTile(xt0, yt0);
            return r.createChild(rect.x, rect.y, rect.width, rect.height, rect.x, rect.y, null);
        }
        return super.getData(rect);
    }

    @Override
    public Raster getTile(int x, int y) {
        return this.tiles.getTile(x, y);
    }

    @Override
    public Raster genTile(int x, int y) {
        WritableRaster wr = this.makeTile(x, y);
        this.genRect(wr);
        return wr;
    }

    public abstract void genRect(WritableRaster var1);

    public void setTile(int x, int y, Raster ras) {
        this.tiles.setTile(x, y, ras);
    }

    public void copyToRasterByBlocks(WritableRaster wr) {
        int ty;
        boolean is_INT_PACK = GraphicsUtil.is_INT_PACK_Data(this.getSampleModel(), false);
        Rectangle bounds = this.getBounds();
        Rectangle wrR = wr.getBounds();
        int tx0 = this.getXTile(wrR.x);
        int ty0 = this.getYTile(wrR.y);
        int tx1 = this.getXTile(wrR.x + wrR.width - 1);
        int ty1 = this.getYTile(wrR.y + wrR.height - 1);
        if (tx0 < this.minTileX) {
            tx0 = this.minTileX;
        }
        if (ty0 < this.minTileY) {
            ty0 = this.minTileY;
        }
        if (tx1 >= this.minTileX + this.numXTiles) {
            tx1 = this.minTileX + this.numXTiles - 1;
        }
        if (ty1 >= this.minTileY + this.numYTiles) {
            ty1 = this.minTileY + this.numYTiles - 1;
        }
        if (tx1 < tx0 || ty1 < ty0) {
            return;
        }
        int insideTx0 = tx0;
        int insideTx1 = tx1;
        int insideTy0 = ty0;
        int insideTy1 = ty1;
        int tx = tx0 * this.tileWidth + this.tileGridXOff;
        if (tx < wrR.x && bounds.x != wrR.x) {
            ++insideTx0;
        }
        if ((ty = ty0 * this.tileHeight + this.tileGridYOff) < wrR.y && bounds.y != wrR.y) {
            ++insideTy0;
        }
        if ((tx = (tx1 + 1) * this.tileWidth + this.tileGridXOff - 1) >= wrR.x + wrR.width && bounds.x + bounds.width != wrR.x + wrR.width) {
            --insideTx1;
        }
        if ((ty = (ty1 + 1) * this.tileHeight + this.tileGridYOff - 1) >= wrR.y + wrR.height && bounds.y + bounds.height != wrR.y + wrR.height) {
            --insideTy1;
        }
        int xtiles = insideTx1 - insideTx0 + 1;
        int ytiles = insideTy1 - insideTy0 + 1;
        boolean[] occupied = null;
        if (xtiles > 0 && ytiles > 0) {
            occupied = new boolean[xtiles * ytiles];
        }
        boolean[] got = new boolean[2 * (tx1 - tx0 + 1) + 2 * (ty1 - ty0 + 1)];
        int idx = 0;
        int numFound = 0;
        for (int y = ty0; y <= ty1; ++y) {
            for (int x = tx0; x <= tx1; ++x) {
                boolean found;
                Raster ras = this.tiles.getTileNoCompute(x, y);
                boolean bl = found = ras != null;
                if (y >= insideTy0 && y <= insideTy1 && x >= insideTx0 && x <= insideTx1) {
                    occupied[x - insideTx0 + (y - insideTy0) * xtiles] = found;
                } else {
                    got[idx++] = found;
                }
                if (!found) continue;
                ++numFound;
                if (is_INT_PACK) {
                    GraphicsUtil.copyData_INT_PACK(ras, wr);
                    continue;
                }
                GraphicsUtil.copyData_FALLBACK(ras, wr);
            }
        }
        if (xtiles > 0 && ytiles > 0) {
            TileBlock block = new TileBlock(insideTx0, insideTy0, xtiles, ytiles, occupied, 0, 0, xtiles, ytiles);
            this.drawBlock(block, wr);
        }
        Thread currentThread = Thread.currentThread();
        if (HaltingThread.hasBeenHalted()) {
            return;
        }
        idx = 0;
        for (ty = ty0; ty <= ty1; ++ty) {
            for (tx = tx0; tx <= tx1; ++tx) {
                Raster ras = this.tiles.getTileNoCompute(tx, ty);
                if (ty >= insideTy0 && ty <= insideTy1 && tx >= insideTx0 && tx <= insideTx1) {
                    if (ras != null) continue;
                    WritableRaster tile = this.makeTile(tx, ty);
                    if (is_INT_PACK) {
                        GraphicsUtil.copyData_INT_PACK(wr, tile);
                    } else {
                        GraphicsUtil.copyData_FALLBACK(wr, tile);
                    }
                    this.tiles.setTile(tx, ty, tile);
                    continue;
                }
                if (got[idx++]) continue;
                ras = this.getTile(tx, ty);
                if (HaltingThread.hasBeenHalted((Thread)currentThread)) {
                    return;
                }
                if (is_INT_PACK) {
                    GraphicsUtil.copyData_INT_PACK(ras, wr);
                    continue;
                }
                GraphicsUtil.copyData_FALLBACK(ras, wr);
            }
        }
    }

    @Override
    public void copyToRaster(WritableRaster wr) {
        Raster r;
        int x;
        int y;
        Rectangle wrR = wr.getBounds();
        int tx0 = this.getXTile(wrR.x);
        int ty0 = this.getYTile(wrR.y);
        int tx1 = this.getXTile(wrR.x + wrR.width - 1);
        int ty1 = this.getYTile(wrR.y + wrR.height - 1);
        if (tx0 < this.minTileX) {
            tx0 = this.minTileX;
        }
        if (ty0 < this.minTileY) {
            ty0 = this.minTileY;
        }
        if (tx1 >= this.minTileX + this.numXTiles) {
            tx1 = this.minTileX + this.numXTiles - 1;
        }
        if (ty1 >= this.minTileY + this.numYTiles) {
            ty1 = this.minTileY + this.numYTiles - 1;
        }
        boolean is_INT_PACK = GraphicsUtil.is_INT_PACK_Data(this.getSampleModel(), false);
        int xtiles = tx1 - tx0 + 1;
        boolean[] got = new boolean[xtiles * (ty1 - ty0 + 1)];
        for (y = ty0; y <= ty1; ++y) {
            for (x = tx0; x <= tx1; ++x) {
                r = this.tiles.getTileNoCompute(x, y);
                if (r == null) continue;
                got[x - tx0 + (y - ty0) * xtiles] = true;
                if (is_INT_PACK) {
                    GraphicsUtil.copyData_INT_PACK(r, wr);
                    continue;
                }
                GraphicsUtil.copyData_FALLBACK(r, wr);
            }
        }
        for (y = ty0; y <= ty1; ++y) {
            for (x = tx0; x <= tx1; ++x) {
                if (got[x - tx0 + (y - ty0) * xtiles]) continue;
                r = this.getTile(x, y);
                if (is_INT_PACK) {
                    GraphicsUtil.copyData_INT_PACK(r, wr);
                    continue;
                }
                GraphicsUtil.copyData_FALLBACK(r, wr);
            }
        }
    }

    protected void drawBlock(TileBlock block, WritableRaster wr) {
        TileBlock[] blocks = block.getBestSplit();
        if (blocks == null) {
            return;
        }
        this.drawBlockInPlace(blocks, wr);
    }

    protected void drawBlockAndCopy(TileBlock[] blocks, WritableRaster wr) {
        if (blocks.length == 1) {
            TileBlock curr = blocks[0];
            int xloc = curr.getXLoc() * this.tileWidth + this.tileGridXOff;
            int yloc = curr.getYLoc() * this.tileHeight + this.tileGridYOff;
            if (xloc == wr.getMinX() && yloc == wr.getMinY()) {
                this.drawBlockInPlace(blocks, wr);
                return;
            }
        }
        int workTileWidth = this.tileWidth;
        int workTileHeight = this.tileHeight;
        int maxTileSize = 0;
        for (TileBlock curr : blocks) {
            int sz = curr.getWidth() * workTileWidth * (curr.getHeight() * workTileHeight);
            if (sz <= maxTileSize) continue;
            maxTileSize = sz;
        }
        DataBufferInt dbi = new DataBufferInt(maxTileSize);
        int[] masks = new int[]{0xFF0000, 65280, 255, -16777216};
        boolean use_INT_PACK = GraphicsUtil.is_INT_PACK_Data(wr.getSampleModel(), false);
        Thread currentThread = Thread.currentThread();
        for (TileBlock curr : blocks) {
            int xloc = curr.getXLoc() * workTileWidth + this.tileGridXOff;
            int yloc = curr.getYLoc() * workTileHeight + this.tileGridYOff;
            Rectangle tb = new Rectangle(xloc, yloc, curr.getWidth() * workTileWidth, curr.getHeight() * workTileHeight);
            tb = tb.intersection(this.bounds);
            Point loc = new Point(tb.x, tb.y);
            WritableRaster child = Raster.createPackedRaster(dbi, tb.width, tb.height, tb.width, masks, loc);
            this.genRect(child);
            if (use_INT_PACK) {
                GraphicsUtil.copyData_INT_PACK(child, wr);
            } else {
                GraphicsUtil.copyData_FALLBACK(child, wr);
            }
            if (!HaltingThread.hasBeenHalted((Thread)currentThread)) continue;
            return;
        }
    }

    protected void drawBlockInPlace(TileBlock[] blocks, WritableRaster wr) {
        Thread currentThread = Thread.currentThread();
        int workTileWidth = this.tileWidth;
        int workTileHeight = this.tileHeight;
        for (TileBlock curr : blocks) {
            int xloc = curr.getXLoc() * workTileWidth + this.tileGridXOff;
            int yloc = curr.getYLoc() * workTileHeight + this.tileGridYOff;
            Rectangle tb = new Rectangle(xloc, yloc, curr.getWidth() * workTileWidth, curr.getHeight() * workTileHeight);
            tb = tb.intersection(this.bounds);
            WritableRaster child = wr.createWritableChild(tb.x, tb.y, tb.width, tb.height, tb.x, tb.y, null);
            this.genRect(child);
            if (!HaltingThread.hasBeenHalted((Thread)currentThread)) continue;
            return;
        }
    }
}

