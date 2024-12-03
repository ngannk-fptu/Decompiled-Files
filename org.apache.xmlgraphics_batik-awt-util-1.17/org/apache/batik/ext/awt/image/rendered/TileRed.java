/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.util.HaltingThread
 */
package org.apache.batik.ext.awt.image.rendered;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.rendered.AbstractRed;
import org.apache.batik.ext.awt.image.rendered.AbstractTiledRed;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.TileCacheRed;
import org.apache.batik.ext.awt.image.rendered.TileGenerator;
import org.apache.batik.ext.awt.image.rendered.TileStore;
import org.apache.batik.util.HaltingThread;

public class TileRed
extends AbstractRed
implements TileGenerator {
    static final AffineTransform IDENTITY = new AffineTransform();
    Rectangle tiledRegion;
    int xStep;
    int yStep;
    TileStore tiles;
    private RenderingHints hints;
    final boolean is_INT_PACK;
    RenderedImage tile = null;
    WritableRaster raster = null;

    public TileRed(RenderedImage tile, Rectangle tiledRegion) {
        this(tile, tiledRegion, tile.getWidth(), tile.getHeight(), null);
    }

    public TileRed(RenderedImage tile, Rectangle tiledRegion, RenderingHints hints) {
        this(tile, tiledRegion, tile.getWidth(), tile.getHeight(), hints);
    }

    public TileRed(RenderedImage tile, Rectangle tiledRegion, int xStep, int yStep) {
        this(tile, tiledRegion, xStep, yStep, null);
    }

    public TileRed(RenderedImage tile, Rectangle tiledRegion, int xStep, int yStep, RenderingHints hints) {
        if (tiledRegion == null) {
            throw new IllegalArgumentException();
        }
        if (tile == null) {
            throw new IllegalArgumentException();
        }
        this.tiledRegion = tiledRegion;
        this.xStep = xStep;
        this.yStep = yStep;
        this.hints = hints;
        SampleModel sm = TileRed.fixSampleModel(tile, xStep, yStep, tiledRegion.width, tiledRegion.height);
        ColorModel cm = tile.getColorModel();
        double smSz = AbstractTiledRed.getDefaultTileSize();
        double stepSz = (double)xStep * (double)yStep;
        if (16.1 * (smSz *= smSz) > stepSz) {
            int xSz = xStep;
            int ySz = yStep;
            if (4.0 * stepSz <= smSz) {
                int mult = (int)Math.ceil(Math.sqrt(smSz / stepSz));
                xSz *= mult;
                ySz *= mult;
            }
            sm = sm.createCompatibleSampleModel(xSz, ySz);
            this.raster = Raster.createWritableRaster(sm, new Point(tile.getMinX(), tile.getMinY()));
        }
        this.is_INT_PACK = GraphicsUtil.is_INT_PACK_Data(sm, false);
        this.init((CachableRed)null, tiledRegion, cm, sm, tile.getMinX(), tile.getMinY(), null);
        if (this.raster != null) {
            WritableRaster fromRaster = this.raster.createWritableChild(tile.getMinX(), tile.getMinY(), xStep, yStep, tile.getMinX(), tile.getMinY(), null);
            this.fillRasterFrom(fromRaster, tile);
            this.fillOutRaster(this.raster);
        } else {
            this.tile = new TileCacheRed(GraphicsUtil.wrap(tile));
        }
    }

    @Override
    public WritableRaster copyData(WritableRaster wr) {
        int xOff = (int)Math.floor(wr.getMinX() / this.xStep) * this.xStep;
        int yOff = (int)Math.floor(wr.getMinY() / this.yStep) * this.yStep;
        int x0 = wr.getMinX() - xOff;
        int y0 = wr.getMinY() - yOff;
        int tx0 = this.getXTile(x0);
        int ty0 = this.getYTile(y0);
        int tx1 = this.getXTile(x0 + wr.getWidth() - 1);
        int ty1 = this.getYTile(y0 + wr.getHeight() - 1);
        for (int y = ty0; y <= ty1; ++y) {
            for (int x = tx0; x <= tx1; ++x) {
                Raster r = this.getTile(x, y);
                r = r.createChild(r.getMinX(), r.getMinY(), r.getWidth(), r.getHeight(), r.getMinX() + xOff, r.getMinY() + yOff, null);
                if (this.is_INT_PACK) {
                    GraphicsUtil.copyData_INT_PACK(r, wr);
                    continue;
                }
                GraphicsUtil.copyData_FALLBACK(r, wr);
            }
        }
        return wr;
    }

    @Override
    public Raster getTile(int x, int y) {
        if (this.raster != null) {
            int tx = this.tileGridXOff + x * this.tileWidth;
            int ty = this.tileGridYOff + y * this.tileHeight;
            return this.raster.createTranslatedChild(tx, ty);
        }
        return this.genTile(x, y);
    }

    @Override
    public Raster genTile(int x, int y) {
        int tx = this.tileGridXOff + x * this.tileWidth;
        int ty = this.tileGridYOff + y * this.tileHeight;
        if (this.raster != null) {
            return this.raster.createTranslatedChild(tx, ty);
        }
        Point pt = new Point(tx, ty);
        WritableRaster wr = Raster.createWritableRaster(this.sm, pt);
        this.fillRasterFrom(wr, this.tile);
        return wr;
    }

    public WritableRaster fillRasterFrom(WritableRaster wr, RenderedImage src) {
        ColorModel cm = this.getColorModel();
        BufferedImage bi = new BufferedImage(cm, wr.createWritableTranslatedChild(0, 0), cm.isAlphaPremultiplied(), null);
        Graphics2D g = GraphicsUtil.createGraphics(bi, this.hints);
        int minX = wr.getMinX();
        int minY = wr.getMinY();
        int maxX = wr.getWidth();
        int maxY = wr.getHeight();
        g.setComposite(AlphaComposite.Clear);
        g.setColor(new Color(0, 0, 0, 0));
        g.fillRect(0, 0, maxX, maxY);
        g.setComposite(AlphaComposite.SrcOver);
        g.translate(-minX, -minY);
        int x1 = src.getMinX() + src.getWidth() - 1;
        int y1 = src.getMinY() + src.getHeight() - 1;
        int tileTx = (int)Math.ceil((minX - x1) / this.xStep) * this.xStep;
        int tileTy = (int)Math.ceil((minY - y1) / this.yStep) * this.yStep;
        g.translate(tileTx, tileTy);
        int curX = tileTx - wr.getMinX() + src.getMinX();
        minX = curX;
        for (int curY = tileTy - wr.getMinY() + src.getMinY(); curY < maxY; curY += this.yStep) {
            if (HaltingThread.hasBeenHalted()) {
                return wr;
            }
            while (curX < maxX) {
                GraphicsUtil.drawImage(g, src);
                curX += this.xStep;
                g.translate(this.xStep, 0);
            }
            g.translate(minX - curX, this.yStep);
            curX = minX;
        }
        return wr;
    }

    protected void fillOutRaster(WritableRaster wr) {
        if (this.is_INT_PACK) {
            this.fillOutRaster_INT_PACK(wr);
        } else {
            this.fillOutRaster_FALLBACK(wr);
        }
    }

    protected void fillOutRaster_INT_PACK(WritableRaster wr) {
        int x0 = wr.getMinX();
        int y0 = wr.getMinY();
        int width = wr.getWidth();
        int height = wr.getHeight();
        SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)wr.getSampleModel();
        int scanStride = sppsm.getScanlineStride();
        DataBufferInt db = (DataBufferInt)wr.getDataBuffer();
        int[] pixels = db.getBankData()[0];
        int base = db.getOffset() + sppsm.getOffset(x0 - wr.getSampleModelTranslateX(), y0 - wr.getSampleModelTranslateY());
        int step = this.xStep;
        int x = this.xStep;
        while (x < width) {
            int y;
            int dstSP;
            int srcSP;
            int w = step;
            if (x + w > width) {
                w = width - x;
            }
            if (w >= 128) {
                srcSP = base;
                dstSP = base + x;
                for (y = 0; y < this.yStep; ++y) {
                    System.arraycopy(pixels, srcSP, pixels, dstSP, w);
                    srcSP += scanStride;
                    dstSP += scanStride;
                }
            } else {
                srcSP = base;
                dstSP = base + x;
                for (y = 0; y < this.yStep; ++y) {
                    int end = srcSP;
                    srcSP += w - 1;
                    dstSP += w - 1;
                    while (srcSP >= end) {
                        pixels[dstSP--] = pixels[srcSP--];
                    }
                    srcSP += scanStride + 1;
                    dstSP += scanStride + 1;
                }
            }
            x += step;
            step *= 2;
        }
        step = this.yStep;
        int y = this.yStep;
        while (y < height) {
            int h = step;
            if (y + h > height) {
                h = height - y;
            }
            int dstSP = base + y * scanStride;
            System.arraycopy(pixels, base, pixels, dstSP, h * scanStride);
            y += step;
            step *= 2;
        }
    }

    protected void fillOutRaster_FALLBACK(WritableRaster wr) {
        int width = wr.getWidth();
        int height = wr.getHeight();
        Object data = null;
        int step = this.xStep;
        int x = this.xStep;
        while (x < width) {
            int w = step;
            if (x + w > width) {
                w = width - x;
            }
            data = wr.getDataElements(0, 0, w, this.yStep, data);
            wr.setDataElements(x, 0, w, this.yStep, data);
            if ((x += w) >= width) break;
            if (x + w > width) {
                w = width - x;
            }
            wr.setDataElements(x, 0, w, this.yStep, data);
            if ((x += w) >= width) break;
            if (x + w > width) {
                w = width - x;
            }
            wr.setDataElements(x, 0, w, this.yStep, data);
            x += step;
            step *= 4;
        }
        step = this.yStep;
        int y = this.yStep;
        while (y < height) {
            int h = step;
            if (y + h > height) {
                h = height - y;
            }
            data = wr.getDataElements(0, 0, width, h, data);
            wr.setDataElements(0, y, width, h, data);
            y += h;
            if (h >= height) break;
            if (y + h > height) {
                h = height - y;
            }
            wr.setDataElements(0, y, width, h, data);
            y += h;
            if (h >= height) break;
            if (y + h > height) {
                h = height - y;
            }
            wr.setDataElements(0, y, width, h, data);
            y += h;
            y += step;
            step *= 4;
        }
    }

    protected static SampleModel fixSampleModel(RenderedImage src, int stepX, int stepY, int width, int height) {
        int h;
        int defSz = AbstractTiledRed.getDefaultTileSize();
        SampleModel sm = src.getSampleModel();
        int w = sm.getWidth();
        if (w < defSz) {
            w = defSz;
        }
        if (w > stepX) {
            w = stepX;
        }
        if ((h = sm.getHeight()) < defSz) {
            h = defSz;
        }
        if (h > stepY) {
            h = stepY;
        }
        return sm.createCompatibleSampleModel(w, h);
    }
}

