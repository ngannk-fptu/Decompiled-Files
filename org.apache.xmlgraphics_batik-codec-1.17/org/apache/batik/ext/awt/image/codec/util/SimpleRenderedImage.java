/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.codec.util;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public abstract class SimpleRenderedImage
implements RenderedImage {
    protected int minX;
    protected int minY;
    protected int width;
    protected int height;
    protected int tileWidth;
    protected int tileHeight;
    protected int tileGridXOffset = 0;
    protected int tileGridYOffset = 0;
    protected SampleModel sampleModel = null;
    protected ColorModel colorModel = null;
    protected List sources = new ArrayList();
    protected Map properties = new HashMap();

    @Override
    public int getMinX() {
        return this.minX;
    }

    public final int getMaxX() {
        return this.getMinX() + this.getWidth();
    }

    @Override
    public int getMinY() {
        return this.minY;
    }

    public final int getMaxY() {
        return this.getMinY() + this.getHeight();
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    public Rectangle getBounds() {
        return new Rectangle(this.getMinX(), this.getMinY(), this.getWidth(), this.getHeight());
    }

    @Override
    public int getTileWidth() {
        return this.tileWidth;
    }

    @Override
    public int getTileHeight() {
        return this.tileHeight;
    }

    @Override
    public int getTileGridXOffset() {
        return this.tileGridXOffset;
    }

    @Override
    public int getTileGridYOffset() {
        return this.tileGridYOffset;
    }

    @Override
    public int getMinTileX() {
        return this.XToTileX(this.getMinX());
    }

    public int getMaxTileX() {
        return this.XToTileX(this.getMaxX() - 1);
    }

    @Override
    public int getNumXTiles() {
        return this.getMaxTileX() - this.getMinTileX() + 1;
    }

    @Override
    public int getMinTileY() {
        return this.YToTileY(this.getMinY());
    }

    public int getMaxTileY() {
        return this.YToTileY(this.getMaxY() - 1);
    }

    @Override
    public int getNumYTiles() {
        return this.getMaxTileY() - this.getMinTileY() + 1;
    }

    @Override
    public SampleModel getSampleModel() {
        return this.sampleModel;
    }

    @Override
    public ColorModel getColorModel() {
        return this.colorModel;
    }

    @Override
    public Object getProperty(String name) {
        name = name.toLowerCase();
        return this.properties.get(name);
    }

    @Override
    public String[] getPropertyNames() {
        String[] names = new String[this.properties.size()];
        this.properties.keySet().toArray(names);
        return names;
    }

    public String[] getPropertyNames(String prefix) {
        String[] propertyNames = this.getPropertyNames();
        if (propertyNames == null) {
            return null;
        }
        prefix = prefix.toLowerCase();
        ArrayList<String> names = new ArrayList<String>();
        for (String propertyName : propertyNames) {
            if (!propertyName.startsWith(prefix)) continue;
            names.add(propertyName);
        }
        if (names.size() == 0) {
            return null;
        }
        String[] prefixNames = new String[names.size()];
        names.toArray(prefixNames);
        return prefixNames;
    }

    public static int XToTileX(int x, int tileGridXOffset, int tileWidth) {
        if ((x -= tileGridXOffset) < 0) {
            x += 1 - tileWidth;
        }
        return x / tileWidth;
    }

    public static int YToTileY(int y, int tileGridYOffset, int tileHeight) {
        if ((y -= tileGridYOffset) < 0) {
            y += 1 - tileHeight;
        }
        return y / tileHeight;
    }

    public int XToTileX(int x) {
        return SimpleRenderedImage.XToTileX(x, this.getTileGridXOffset(), this.getTileWidth());
    }

    public int YToTileY(int y) {
        return SimpleRenderedImage.YToTileY(y, this.getTileGridYOffset(), this.getTileHeight());
    }

    public static int tileXToX(int tx, int tileGridXOffset, int tileWidth) {
        return tx * tileWidth + tileGridXOffset;
    }

    public static int tileYToY(int ty, int tileGridYOffset, int tileHeight) {
        return ty * tileHeight + tileGridYOffset;
    }

    public int tileXToX(int tx) {
        return tx * this.tileWidth + this.tileGridXOffset;
    }

    public int tileYToY(int ty) {
        return ty * this.tileHeight + this.tileGridYOffset;
    }

    public Vector getSources() {
        return null;
    }

    @Override
    public Raster getData() {
        Rectangle rect = new Rectangle(this.getMinX(), this.getMinY(), this.getWidth(), this.getHeight());
        return this.getData(rect);
    }

    @Override
    public Raster getData(Rectangle bounds) {
        int startX = this.XToTileX(bounds.x);
        int startY = this.YToTileY(bounds.y);
        int endX = this.XToTileX(bounds.x + bounds.width - 1);
        int endY = this.YToTileY(bounds.y + bounds.height - 1);
        if (startX == endX && startY == endY) {
            Raster tile = this.getTile(startX, startY);
            return tile.createChild(bounds.x, bounds.y, bounds.width, bounds.height, bounds.x, bounds.y, null);
        }
        SampleModel sm = this.sampleModel.createCompatibleSampleModel(bounds.width, bounds.height);
        WritableRaster dest = Raster.createWritableRaster(sm, bounds.getLocation());
        for (int j = startY; j <= endY; ++j) {
            for (int i = startX; i <= endX; ++i) {
                Raster tile = this.getTile(i, j);
                Rectangle intersectRect = bounds.intersection(tile.getBounds());
                Raster liveRaster = tile.createChild(intersectRect.x, intersectRect.y, intersectRect.width, intersectRect.height, intersectRect.x, intersectRect.y, null);
                dest.setDataElements(0, 0, liveRaster);
            }
        }
        return dest;
    }

    @Override
    public WritableRaster copyData(WritableRaster dest) {
        Rectangle bounds;
        if (dest == null) {
            bounds = this.getBounds();
            Point p = new Point(this.minX, this.minY);
            SampleModel sm = this.sampleModel.createCompatibleSampleModel(this.width, this.height);
            dest = Raster.createWritableRaster(sm, p);
        } else {
            bounds = dest.getBounds();
        }
        int startX = this.XToTileX(bounds.x);
        int startY = this.YToTileY(bounds.y);
        int endX = this.XToTileX(bounds.x + bounds.width - 1);
        int endY = this.YToTileY(bounds.y + bounds.height - 1);
        for (int j = startY; j <= endY; ++j) {
            for (int i = startX; i <= endX; ++i) {
                Raster tile = this.getTile(i, j);
                Rectangle intersectRect = bounds.intersection(tile.getBounds());
                Raster liveRaster = tile.createChild(intersectRect.x, intersectRect.y, intersectRect.width, intersectRect.height, intersectRect.x, intersectRect.y, null);
                dest.setDataElements(0, 0, liveRaster);
            }
        }
        return dest;
    }
}

