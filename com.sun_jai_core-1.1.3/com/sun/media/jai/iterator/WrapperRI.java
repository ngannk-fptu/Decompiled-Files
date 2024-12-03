/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.iterator;

import com.sun.media.jai.iterator.JaiI18N;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Vector;

public class WrapperRI
implements RenderedImage {
    Raster ras;

    public WrapperRI(Raster ras) {
        this.ras = ras;
    }

    public Vector getSources() {
        return null;
    }

    public Object getProperty(String name) {
        return null;
    }

    public String[] getPropertyNames() {
        return null;
    }

    public ColorModel getColorModel() {
        return null;
    }

    public SampleModel getSampleModel() {
        return this.ras.getSampleModel();
    }

    public int getWidth() {
        return this.ras.getWidth();
    }

    public int getHeight() {
        return this.ras.getHeight();
    }

    public int getMinX() {
        return this.ras.getMinX();
    }

    public int getMinY() {
        return this.ras.getMinY();
    }

    public int getNumXTiles() {
        return 1;
    }

    public int getNumYTiles() {
        return 1;
    }

    public int getMinTileX() {
        return 0;
    }

    public int getMinTileY() {
        return 0;
    }

    public int getTileWidth() {
        return this.ras.getWidth();
    }

    public int getTileHeight() {
        return this.ras.getHeight();
    }

    public int getTileGridXOffset() {
        return this.ras.getMinX();
    }

    public int getTileGridYOffset() {
        return this.ras.getMinY();
    }

    public Raster getTile(int tileX, int tileY) {
        return this.ras;
    }

    public Raster getData() {
        throw new RuntimeException(JaiI18N.getString("WrapperRI0"));
    }

    public Raster getData(Rectangle rect) {
        throw new RuntimeException(JaiI18N.getString("WrapperRI0"));
    }

    public WritableRaster copyData(WritableRaster raster) {
        throw new RuntimeException(JaiI18N.getString("WrapperRI0"));
    }
}

