/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import com.sun.media.jai.util.JDKWorkarounds;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.image.BandedSampleModel;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.TileObserver;
import java.awt.image.WritableRaster;
import java.awt.image.WritableRenderedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.JaiI18N;
import javax.media.jai.PlanarImage;
import javax.media.jai.PropertyChangeEventJAI;
import javax.media.jai.ROI;
import javax.media.jai.ROIShape;
import javax.media.jai.RasterFactory;
import javax.media.jai.RenderingChangeEvent;
import javax.media.jai.TileFactory;
import javax.media.jai.TiledImageGraphics;

public class TiledImage
extends PlanarImage
implements WritableRenderedImage,
PropertyChangeListener {
    protected int tilesX;
    protected int tilesY;
    protected int minTileX;
    protected int minTileY;
    protected WritableRaster[][] tiles;
    protected int[][] writers;
    protected Vector tileObservers = null;
    private boolean areBuffersShared = false;
    private TiledImage parent = null;
    private SampleModel ancestorSampleModel = null;
    private int[] bandList = null;
    private int[] numWritableTiles = null;
    private ROI srcROI = null;
    private Rectangle overlapBounds = null;

    private static SampleModel coerceSampleModel(SampleModel sampleModel, int sampleModelWidth, int sampleModelHeight) {
        return sampleModel.getWidth() == sampleModelWidth && sampleModel.getHeight() == sampleModelHeight ? sampleModel : sampleModel.createCompatibleSampleModel(sampleModelWidth, sampleModelHeight);
    }

    private void initTileGrid(TiledImage parent) {
        if (parent != null) {
            this.minTileX = parent.minTileX;
            this.minTileY = parent.minTileY;
        } else {
            this.minTileX = this.getMinTileX();
            this.minTileY = this.getMinTileY();
        }
        int maxTileX = this.getMaxTileX();
        int maxTileY = this.getMaxTileY();
        this.tilesX = maxTileX - this.minTileX + 1;
        this.tilesY = maxTileY - this.minTileY + 1;
    }

    public TiledImage(int minX, int minY, int width, int height, int tileGridXOffset, int tileGridYOffset, SampleModel tileSampleModel, ColorModel colorModel) {
        this(null, minX, minY, width, height, tileGridXOffset, tileGridYOffset, tileSampleModel, colorModel);
    }

    private TiledImage(TiledImage parent, int minX, int minY, int width, int height, int tileGridXOffset, int tileGridYOffset, SampleModel sampleModel, ColorModel colorModel) {
        super(new ImageLayout(minX, minY, width, height, tileGridXOffset, tileGridYOffset, sampleModel.getWidth(), sampleModel.getHeight(), sampleModel, colorModel), null, null);
        this.initTileGrid(parent);
        if (parent == null) {
            this.tiles = new WritableRaster[this.tilesX][this.tilesY];
            this.writers = new int[this.tilesX][this.tilesY];
            this.tileObservers = new Vector();
            this.numWritableTiles = new int[1];
            this.numWritableTiles[0] = 0;
            this.ancestorSampleModel = sampleModel;
        } else {
            this.parent = parent;
            this.tiles = parent.tiles;
            this.writers = parent.writers;
            this.tileObservers = parent.tileObservers;
            this.numWritableTiles = parent.numWritableTiles;
            this.ancestorSampleModel = parent.ancestorSampleModel;
        }
        this.tileFactory = (TileFactory)JAI.getDefaultInstance().getRenderingHint(JAI.KEY_TILE_FACTORY);
    }

    public TiledImage(Point origin, SampleModel sampleModel, int tileWidth, int tileHeight) {
        this(origin.x, origin.y, sampleModel.getWidth(), sampleModel.getHeight(), origin.x, origin.y, TiledImage.coerceSampleModel(sampleModel, tileWidth, tileHeight), PlanarImage.createColorModel(sampleModel));
    }

    public TiledImage(SampleModel sampleModel, int tileWidth, int tileHeight) {
        this(0, 0, sampleModel.getWidth(), sampleModel.getHeight(), 0, 0, TiledImage.coerceSampleModel(sampleModel, tileWidth, tileHeight), PlanarImage.createColorModel(sampleModel));
    }

    public TiledImage(RenderedImage source, int tileWidth, int tileHeight) {
        this(source.getMinX(), source.getMinY(), source.getWidth(), source.getHeight(), source.getTileGridXOffset(), source.getTileGridYOffset(), TiledImage.coerceSampleModel(source.getSampleModel(), tileWidth, tileHeight), source.getColorModel());
        this.set(source);
    }

    public TiledImage(RenderedImage source, boolean areBuffersShared) {
        this(source, source.getTileWidth(), source.getTileHeight());
        this.areBuffersShared = areBuffersShared;
    }

    public static TiledImage createInterleaved(int minX, int minY, int width, int height, int numBands, int dataType, int tileWidth, int tileHeight, int[] bandOffsets) {
        SampleModel sm = RasterFactory.createPixelInterleavedSampleModel(dataType, tileWidth, tileHeight, numBands, numBands * tileWidth, bandOffsets);
        return new TiledImage(minX, minY, width, height, minX, minY, sm, PlanarImage.createColorModel(sm));
    }

    public static TiledImage createBanded(int minX, int minY, int width, int height, int dataType, int tileWidth, int tileHeight, int[] bankIndices, int[] bandOffsets) {
        BandedSampleModel sm = new BandedSampleModel(dataType, tileWidth, tileHeight, tileWidth, bankIndices, bandOffsets);
        return new TiledImage(minX, minY, width, height, minX, minY, sm, PlanarImage.createColorModel(sm));
    }

    private void overlayPixels(WritableRaster tile, RenderedImage im, Rectangle rect) {
        WritableRaster child = tile.createWritableChild(rect.x, rect.y, rect.width, rect.height, rect.x, rect.y, this.bandList);
        im.copyData(child);
    }

    private void overlayPixels(WritableRaster tile, RenderedImage im, Area a) {
        ROIShape rs = new ROIShape(a);
        Rectangle bounds = rs.getBounds();
        LinkedList rectList = rs.getAsRectangleList(bounds.x, bounds.y, bounds.width, bounds.height);
        int numRects = rectList.size();
        for (int i = 0; i < numRects; ++i) {
            Rectangle rect = (Rectangle)rectList.get(i);
            WritableRaster child = tile.createWritableChild(rect.x, rect.y, rect.width, rect.height, rect.x, rect.y, this.bandList);
            im.copyData(child);
        }
    }

    private void overlayPixels(WritableRaster tile, RenderedImage im, Rectangle rect, int[][] bitmask) {
        Raster r = im.getData(rect);
        if (this.bandList != null) {
            tile = tile.createWritableChild(rect.x, rect.y, rect.width, rect.height, rect.x, rect.y, this.bandList);
        }
        Object data = r.getDataElements(rect.x, rect.y, null);
        int leftover = rect.width % 32;
        int bitWidth = (rect.width + 31) / 32 - (leftover > 0 ? 1 : 0);
        int y = rect.y;
        int j = 0;
        while (j < rect.height) {
            int b;
            int bit;
            int mask32;
            int i;
            int[] rowMask = bitmask[j];
            int x = rect.x;
            for (i = 0; i < bitWidth; ++i) {
                mask32 = rowMask[i];
                bit = Integer.MIN_VALUE;
                b = 0;
                while (b < 32) {
                    if ((mask32 & bit) != 0) {
                        r.getDataElements(x, y, data);
                        tile.setDataElements(x, y, data);
                    }
                    bit >>>= 1;
                    ++b;
                    ++x;
                }
            }
            if (leftover > 0) {
                mask32 = rowMask[i];
                bit = Integer.MIN_VALUE;
                b = 0;
                while (b < leftover) {
                    if ((mask32 & bit) != 0) {
                        r.getDataElements(x, y, data);
                        tile.setDataElements(x, y, data);
                    }
                    bit >>>= 1;
                    ++b;
                    ++x;
                }
            }
            ++j;
            ++y;
        }
    }

    public void set(RenderedImage im) {
        if (im == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (this.getNumSources() > 0 && im == this.getSourceImage(0)) {
            return;
        }
        Rectangle imRect = new Rectangle(im.getMinX(), im.getMinY(), im.getWidth(), im.getHeight());
        if ((imRect = imRect.intersection(this.getBounds())).isEmpty()) {
            return;
        }
        this.areBuffersShared = false;
        int txMin = this.XToTileX(imRect.x);
        int tyMin = this.YToTileY(imRect.y);
        int txMax = this.XToTileX(imRect.x + imRect.width - 1);
        int tyMax = this.YToTileY(imRect.y + imRect.height - 1);
        for (int j = tyMin; j <= tyMax; ++j) {
            for (int i = txMin; i <= txMax; ++i) {
                WritableRaster t = this.tiles[i - this.minTileX][j - this.minTileY];
                if (t == null || this.isTileLocked(i, j)) continue;
                Rectangle tileRect = this.getTileRect(i, j);
                if ((tileRect = tileRect.intersection(imRect)).isEmpty()) continue;
                this.overlayPixels(t, im, tileRect);
            }
        }
        PlanarImage src = PlanarImage.wrapRenderedImage(im);
        if (this.getNumSources() == 0) {
            this.addSource(src);
        } else {
            this.setSource(src, 0);
        }
        this.srcROI = null;
        this.overlapBounds = imRect;
        this.properties.addProperties(src);
    }

    public void set(RenderedImage im, ROI roi) {
        if (im == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (this.getNumSources() > 0 && im == this.getSourceImage(0)) {
            return;
        }
        Rectangle imRect = new Rectangle(im.getMinX(), im.getMinY(), im.getWidth(), im.getHeight());
        Rectangle overlap = imRect.intersection(roi.getBounds());
        if (overlap.isEmpty() || (overlap = overlap.intersection(this.getBounds())).isEmpty()) {
            return;
        }
        this.areBuffersShared = false;
        int txMin = this.XToTileX(overlap.x);
        int tyMin = this.YToTileY(overlap.y);
        int txMax = this.XToTileX(overlap.x + overlap.width - 1);
        int tyMax = this.YToTileY(overlap.y + overlap.height - 1);
        Shape roiShape = roi.getAsShape();
        Area roiArea = null;
        if (roiShape != null) {
            roiArea = new Area(roiShape);
        }
        for (int j = tyMin; j <= tyMax; ++j) {
            for (int i = txMin; i <= txMax; ++i) {
                Rectangle rect;
                WritableRaster t = this.tiles[i - this.minTileX][j - this.minTileY];
                if (t == null || this.isTileLocked(i, j) || (rect = this.getTileRect(i, j).intersection(overlap)).isEmpty()) continue;
                if (roiShape != null) {
                    Area a = new Area(rect);
                    a.intersect(roiArea);
                    if (a.isEmpty()) continue;
                    this.overlayPixels(t, im, a);
                    continue;
                }
                int[][] bitmask = roi.getAsBitmask(rect.x, rect.y, rect.width, rect.height, null);
                if (bitmask == null || bitmask.length <= 0) continue;
                this.overlayPixels(t, im, rect, bitmask);
            }
        }
        PlanarImage src = PlanarImage.wrapRenderedImage(im);
        if (this.getNumSources() == 0) {
            this.addSource(src);
        } else {
            this.setSource(src, 0);
        }
        this.srcROI = roi;
        this.overlapBounds = overlap;
        this.properties.addProperties(src);
    }

    public Graphics getGraphics() {
        return this.createGraphics();
    }

    public Graphics2D createGraphics() {
        int dataType = this.sampleModel.getDataType();
        if (dataType != 0 && dataType != 2 && dataType != 1 && dataType != 3) {
            throw new UnsupportedOperationException(JaiI18N.getString("TiledImage0"));
        }
        return new TiledImageGraphics(this);
    }

    public TiledImage getSubImage(int x, int y, int w, int h, int[] bandSelect, ColorModel cm) {
        SampleModel sm;
        Rectangle subImageBounds = new Rectangle(x, y, w, h);
        if (subImageBounds.isEmpty()) {
            return null;
        }
        Rectangle overlap = subImageBounds.intersection(this.getBounds());
        if (overlap.isEmpty()) {
            return null;
        }
        SampleModel sampleModel = sm = bandSelect != null ? this.getSampleModel().createSubsetSampleModel(bandSelect) : this.getSampleModel();
        if (cm == null && (bandSelect == null || bandSelect.length == this.getSampleModel().getNumBands())) {
            cm = this.getColorModel();
        }
        TiledImage subImage = new TiledImage(this, overlap.x, overlap.y, overlap.width, overlap.height, this.getTileGridXOffset(), this.getTileGridYOffset(), sm, cm);
        int[] subBandList = null;
        if (bandSelect != null) {
            if (this.bandList != null) {
                subBandList = new int[bandSelect.length];
                for (int band = 0; band < bandSelect.length; ++band) {
                    subBandList[band] = this.bandList[bandSelect[band]];
                }
            } else {
                subBandList = bandSelect;
            }
        } else {
            subBandList = this.bandList;
        }
        subImage.bandList = subBandList;
        return subImage;
    }

    public TiledImage getSubImage(int x, int y, int w, int h, int[] bandSelect) {
        SampleModel sm = bandSelect != null ? this.getSampleModel().createSubsetSampleModel(bandSelect) : this.getSampleModel();
        return this.getSubImage(x, y, w, h, bandSelect, TiledImage.createColorModel(sm));
    }

    public TiledImage getSubImage(int x, int y, int w, int h) {
        return this.getSubImage(x, y, w, h, null, null);
    }

    public TiledImage getSubImage(int[] bandSelect, ColorModel cm) {
        if (bandSelect == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        return this.getSubImage(this.getMinX(), this.getMinY(), this.getWidth(), this.getHeight(), bandSelect, cm);
    }

    public TiledImage getSubImage(int[] bandSelect) {
        if (bandSelect == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        return this.getSubImage(this.getMinX(), this.getMinY(), this.getWidth(), this.getHeight(), bandSelect);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void createTile(int tileX, int tileY) {
        PlanarImage src;
        PlanarImage planarImage = src = this.getNumSources() > 0 ? this.getSourceImage(0) : null;
        if (src == null && this.parent != null) {
            this.parent.createTile(tileX, tileY);
            return;
        }
        WritableRaster[][] writableRasterArray = this.tiles;
        synchronized (this.tiles) {
            if (this.tiles[tileX - this.minTileX][tileY - this.minTileY] == null) {
                if (this.areBuffersShared) {
                    Raster srcTile = src.getTile(tileX, tileY);
                    if (srcTile instanceof WritableRaster) {
                        this.tiles[tileX - this.minTileX][tileY - this.minTileY] = (WritableRaster)srcTile;
                    } else {
                        Point location = new Point(srcTile.getMinX(), srcTile.getMinY());
                        this.tiles[tileX - this.minTileX][tileY - this.minTileY] = Raster.createWritableRaster(this.sampleModel, srcTile.getDataBuffer(), location);
                    }
                    // ** MonitorExit[var4_4] (shouldn't be in output)
                    return;
                }
                this.tiles[tileX - this.minTileX][tileY - this.minTileY] = this.createWritableRaster(this.ancestorSampleModel, new Point(this.tileXToX(tileX), this.tileYToY(tileY)));
                WritableRaster tile = this.tiles[tileX - this.minTileX][tileY - this.minTileY];
                if (src != null) {
                    Rectangle tileRect = this.getTileRect(tileX, tileY);
                    Rectangle rect = this.overlapBounds.intersection(tileRect);
                    if (rect.isEmpty()) {
                        // ** MonitorExit[var4_4] (shouldn't be in output)
                        return;
                    }
                    if (this.srcROI != null) {
                        Shape roiShape = this.srcROI.getAsShape();
                        if (roiShape != null) {
                            Area a = new Area(rect);
                            a.intersect(new Area(roiShape));
                            if (!a.isEmpty()) {
                                this.overlayPixels(tile, (RenderedImage)src, a);
                            }
                        } else {
                            int[][] bitmask = this.srcROI.getAsBitmask(rect.x, rect.y, rect.width, rect.height, null);
                            this.overlayPixels(tile, src, rect, bitmask);
                        }
                    } else if (!rect.isEmpty()) {
                        if (this.bandList == null && rect.equals(tileRect)) {
                            if (tileRect.equals(tile.getBounds())) {
                                src.copyData(tile);
                            } else {
                                src.copyData(tile.createWritableChild(rect.x, rect.y, rect.width, rect.height, rect.x, rect.y, null));
                            }
                        } else {
                            this.overlayPixels(tile, (RenderedImage)src, rect);
                        }
                    }
                }
            }
            // ** MonitorExit[var4_4] (shouldn't be in output)
            return;
        }
    }

    public Raster getTile(int tileX, int tileY) {
        if (tileX < this.minTileX || tileY < this.minTileY || tileX > this.getMaxTileX() || tileY > this.getMaxTileY()) {
            return null;
        }
        this.createTile(tileX, tileY);
        if (this.bandList == null) {
            return this.tiles[tileX - this.minTileX][tileY - this.minTileY];
        }
        WritableRaster r = this.tiles[tileX - this.minTileX][tileY - this.minTileY];
        return r.createChild(r.getMinX(), r.getMinY(), r.getWidth(), r.getHeight(), r.getMinX(), r.getMinY(), this.bandList);
    }

    public WritableRaster getWritableTile(int tileX, int tileY) {
        if (tileX < this.minTileX || tileY < this.minTileY || tileX > this.getMaxTileX() || tileY > this.getMaxTileY()) {
            return null;
        }
        if (this.isTileLocked(tileX, tileY)) {
            return null;
        }
        this.createTile(tileX, tileY);
        int[] nArray = this.writers[tileX - this.minTileX];
        int n = tileY - this.minTileY;
        nArray[n] = nArray[n] + 1;
        if (this.writers[tileX - this.minTileX][tileY - this.minTileY] == 1) {
            this.numWritableTiles[0] = this.numWritableTiles[0] + 1;
            Enumeration e = this.tileObservers.elements();
            while (e.hasMoreElements()) {
                TileObserver t = (TileObserver)e.nextElement();
                t.tileUpdate(this, tileX, tileY, true);
            }
        }
        if (this.bandList == null) {
            return this.tiles[tileX - this.minTileX][tileY - this.minTileY];
        }
        WritableRaster wr = this.tiles[tileX - this.minTileX][tileY - this.minTileY];
        return wr.createWritableChild(wr.getMinX(), wr.getMinY(), wr.getWidth(), wr.getHeight(), wr.getMinX(), wr.getMinY(), this.bandList);
    }

    public void releaseWritableTile(int tileX, int tileY) {
        if (this.isTileLocked(tileX, tileY)) {
            return;
        }
        int[] nArray = this.writers[tileX - this.minTileX];
        int n = tileY - this.minTileY;
        nArray[n] = nArray[n] - 1;
        if (this.writers[tileX - this.minTileX][tileY - this.minTileY] < 0) {
            throw new RuntimeException(JaiI18N.getString("TiledImage1"));
        }
        if (this.writers[tileX - this.minTileX][tileY - this.minTileY] == 0) {
            this.numWritableTiles[0] = this.numWritableTiles[0] - 1;
            Enumeration e = this.tileObservers.elements();
            while (e.hasMoreElements()) {
                TileObserver t = (TileObserver)e.nextElement();
                t.tileUpdate(this, tileX, tileY, false);
            }
        }
    }

    protected boolean lockTile(int tileX, int tileY) {
        if (tileX < this.minTileX || tileY < this.minTileY || tileX > this.getMaxTileX() || tileY > this.getMaxTileY()) {
            return false;
        }
        if (this.isTileWritable(tileX, tileY)) {
            return false;
        }
        this.createTile(tileX, tileY);
        this.writers[tileX - this.minTileX][tileY - this.minTileY] = -1;
        return true;
    }

    protected boolean isTileLocked(int tileX, int tileY) {
        return this.writers[tileX - this.minTileX][tileY - this.minTileY] < 0;
    }

    public void setData(Raster r) {
        Rectangle rBounds = r.getBounds();
        if ((rBounds = rBounds.intersection(this.getBounds())).isEmpty()) {
            return;
        }
        int txMin = this.XToTileX(rBounds.x);
        int tyMin = this.YToTileY(rBounds.y);
        int txMax = this.XToTileX(rBounds.x + rBounds.width - 1);
        int tyMax = this.YToTileY(rBounds.y + rBounds.height - 1);
        for (int ty = tyMin; ty <= tyMax; ++ty) {
            for (int tx = txMin; tx <= txMax; ++tx) {
                WritableRaster wr = this.getWritableTile(tx, ty);
                if (wr == null) continue;
                Rectangle tileRect = this.getTileRect(tx, ty);
                if (tileRect.contains(rBounds)) {
                    JDKWorkarounds.setRect(wr, r, 0, 0);
                } else {
                    Rectangle xsect = rBounds.intersection(tileRect);
                    Raster rChild = r.createChild(xsect.x, xsect.y, xsect.width, xsect.height, xsect.x, xsect.y, null);
                    WritableRaster wChild = wr.createWritableChild(xsect.x, xsect.y, xsect.width, xsect.height, xsect.x, xsect.y, null);
                    JDKWorkarounds.setRect(wChild, rChild, 0, 0);
                }
                this.releaseWritableTile(tx, ty);
            }
        }
    }

    public void setData(Raster r, ROI roi) {
        Rectangle rBounds = r.getBounds();
        if ((rBounds = rBounds.intersection(this.getBounds())).isEmpty() || (rBounds = rBounds.intersection(roi.getBounds())).isEmpty()) {
            return;
        }
        LinkedList rectList = roi.getAsRectangleList(rBounds.x, rBounds.y, rBounds.width, rBounds.height);
        int txMin = this.XToTileX(rBounds.x);
        int tyMin = this.YToTileY(rBounds.y);
        int txMax = this.XToTileX(rBounds.x + rBounds.width - 1);
        int tyMax = this.YToTileY(rBounds.y + rBounds.height - 1);
        int numRects = rectList.size();
        for (int ty = tyMin; ty <= tyMax; ++ty) {
            for (int tx = txMin; tx <= txMax; ++tx) {
                WritableRaster wr = this.getWritableTile(tx, ty);
                if (wr == null) continue;
                Rectangle tileRect = this.getTileRect(tx, ty);
                for (int i = 0; i < numRects; ++i) {
                    Rectangle rect = (Rectangle)rectList.get(i);
                    if ((rect = rect.intersection(tileRect)).isEmpty()) continue;
                    Raster rChild = r.createChild(rect.x, rect.y, rect.width, rect.height, rect.x, rect.y, null);
                    WritableRaster wChild = wr.createWritableChild(rect.x, rect.y, rect.width, rect.height, rect.x, rect.y, null);
                    JDKWorkarounds.setRect(wChild, rChild, 0, 0);
                }
                this.releaseWritableTile(tx, ty);
            }
        }
    }

    public void addTileObserver(TileObserver observer) {
        this.tileObservers.addElement(observer);
    }

    public void removeTileObserver(TileObserver observer) {
        this.tileObservers.removeElement(observer);
    }

    public Point[] getWritableTileIndices() {
        Point[] indices = null;
        if (this.hasTileWriters()) {
            Vector<Point> v = new Vector<Point>();
            int count = 0;
            for (int j = 0; j < this.tilesY; ++j) {
                for (int i = 0; i < this.tilesX; ++i) {
                    if (this.writers[i][j] <= 0) continue;
                    v.addElement(new Point(i + this.minTileX, j + this.minTileY));
                    ++count;
                }
            }
            indices = new Point[count];
            for (int k = 0; k < count; ++k) {
                indices[k] = (Point)v.elementAt(k);
            }
        }
        return indices;
    }

    public boolean hasTileWriters() {
        return this.numWritableTiles[0] > 0;
    }

    public boolean isTileWritable(int tileX, int tileY) {
        return this.writers[tileX - this.minTileX][tileY - this.minTileY] > 0;
    }

    public void clearTiles() {
        if (this.hasTileWriters()) {
            throw new IllegalStateException(JaiI18N.getString("TiledImage2"));
        }
        this.tiles = null;
    }

    public void setSample(int x, int y, int b, int s) {
        int tileY;
        int tileX = this.XToTileX(x);
        WritableRaster t = this.getWritableTile(tileX, tileY = this.YToTileY(y));
        if (t != null) {
            t.setSample(x, y, b, s);
        }
        this.releaseWritableTile(tileX, tileY);
    }

    public int getSample(int x, int y, int b) {
        int tileX = this.XToTileX(x);
        int tileY = this.YToTileY(y);
        Raster t = this.getTile(tileX, tileY);
        return t.getSample(x, y, b);
    }

    public void setSample(int x, int y, int b, float s) {
        int tileY;
        int tileX = this.XToTileX(x);
        WritableRaster t = this.getWritableTile(tileX, tileY = this.YToTileY(y));
        if (t != null) {
            t.setSample(x, y, b, s);
        }
        this.releaseWritableTile(tileX, tileY);
    }

    public float getSampleFloat(int x, int y, int b) {
        int tileX = this.XToTileX(x);
        int tileY = this.YToTileY(y);
        Raster t = this.getTile(tileX, tileY);
        return t.getSampleFloat(x, y, b);
    }

    public void setSample(int x, int y, int b, double s) {
        int tileY;
        int tileX = this.XToTileX(x);
        WritableRaster t = this.getWritableTile(tileX, tileY = this.YToTileY(y));
        if (t != null) {
            t.setSample(x, y, b, s);
        }
        this.releaseWritableTile(tileX, tileY);
    }

    public double getSampleDouble(int x, int y, int b) {
        int tileX = this.XToTileX(x);
        int tileY = this.YToTileY(y);
        Raster t = this.getTile(tileX, tileY);
        return t.getSampleDouble(x, y, b);
    }

    public synchronized void propertyChange(PropertyChangeEvent evt) {
        PlanarImage src;
        PlanarImage planarImage = src = this.getNumSources() > 0 ? this.getSourceImage(0) : null;
        if (evt.getSource() == src && (evt instanceof RenderingChangeEvent || evt instanceof PropertyChangeEventJAI && evt.getPropertyName().equalsIgnoreCase("InvalidRegion"))) {
            Shape invalidRegion = evt instanceof RenderingChangeEvent ? ((RenderingChangeEvent)evt).getInvalidRegion() : (Shape)evt.getNewValue();
            Rectangle invalidBounds = invalidRegion.getBounds();
            if (invalidBounds.isEmpty()) {
                return;
            }
            Area invalidArea = new Area(invalidRegion);
            if (this.srcROI != null) {
                Shape roiShape = this.srcROI.getAsShape();
                if (roiShape != null) {
                    invalidArea.intersect(new Area(roiShape));
                } else {
                    LinkedList rectList = this.srcROI.getAsRectangleList(invalidBounds.x, invalidBounds.y, invalidBounds.width, invalidBounds.height);
                    Iterator it = rectList.iterator();
                    while (it.hasNext() && !invalidArea.isEmpty()) {
                        invalidArea.intersect(new Area((Rectangle)it.next()));
                    }
                }
            }
            if (invalidArea.isEmpty()) {
                return;
            }
            Point[] tileIndices = this.getTileIndices(invalidArea.getBounds());
            int numIndices = tileIndices.length;
            for (int i = 0; i < numIndices; ++i) {
                int tx = tileIndices[i].x;
                int ty = tileIndices[i].y;
                WritableRaster tile = this.tiles[tx][ty];
                if (tile == null || !invalidArea.intersects(tile.getBounds())) continue;
                this.tiles[tx][ty] = null;
            }
            if (this.eventManager.hasListeners("InvalidRegion")) {
                Shape oldInvalidRegion = new Rectangle();
                if (this.srcROI != null) {
                    Area oldInvalidArea = new Area(this.getBounds());
                    Shape roiShape = this.srcROI.getAsShape();
                    if (roiShape != null) {
                        oldInvalidArea.subtract(new Area(roiShape));
                    } else {
                        Rectangle oldInvalidBounds = oldInvalidArea.getBounds();
                        LinkedList rectList = this.srcROI.getAsRectangleList(oldInvalidBounds.x, oldInvalidBounds.y, oldInvalidBounds.width, oldInvalidBounds.height);
                        Iterator it = rectList.iterator();
                        while (it.hasNext() && !oldInvalidArea.isEmpty()) {
                            oldInvalidArea.subtract(new Area((Rectangle)it.next()));
                        }
                    }
                    oldInvalidRegion = oldInvalidArea;
                }
                PropertyChangeEventJAI irEvt = new PropertyChangeEventJAI(this, "InvalidRegion", oldInvalidRegion, invalidRegion);
                this.eventManager.firePropertyChange(irEvt);
                Vector sinks = this.getSinks();
                if (sinks != null) {
                    int numSinks = sinks.size();
                    for (int i = 0; i < numSinks; ++i) {
                        Object sink = sinks.get(i);
                        if (!(sink instanceof PropertyChangeListener)) continue;
                        ((PropertyChangeListener)sink).propertyChange(irEvt);
                    }
                }
            }
        }
    }
}

