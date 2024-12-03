/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.iterator;

import com.sun.media.jai.iterator.JaiI18N;
import com.sun.media.jai.iterator.WrapperRI;
import java.awt.Point;
import java.awt.image.Raster;
import java.awt.image.TileObserver;
import java.awt.image.WritableRaster;
import java.awt.image.WritableRenderedImage;

public class WrapperWRI
extends WrapperRI
implements WritableRenderedImage {
    WritableRaster wras;

    public WrapperWRI(WritableRaster wras) {
        super(wras);
        this.wras = wras;
    }

    public void addTileObserver(TileObserver to) {
        throw new RuntimeException(JaiI18N.getString("WrapperWRI0"));
    }

    public void removeTileObserver(TileObserver to) {
        throw new RuntimeException(JaiI18N.getString("WrapperWRI0"));
    }

    public WritableRaster getWritableTile(int tileX, int tileY) {
        if (tileX != 0 || tileY != 0) {
            throw new IllegalArgumentException();
        }
        return this.wras;
    }

    public void releaseWritableTile(int tileX, int tileY) {
        if (tileX != 0 || tileY != 0) {
            throw new IllegalArgumentException();
        }
    }

    public boolean isTileWritable(int tileX, int tileY) {
        return true;
    }

    public Point[] getWritableTileIndices() {
        Point[] p = new Point[]{new Point(0, 0)};
        return p;
    }

    public boolean hasTileWriters() {
        return true;
    }

    public void setData(Raster r) {
        throw new RuntimeException(JaiI18N.getString("WrapperWRI0"));
    }
}

