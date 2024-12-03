/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codecimpl;

import com.sun.media.jai.codecimpl.JaiI18N;
import com.sun.media.jai.codecimpl.SimpleRenderedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;

public class SingleTileRenderedImage
extends SimpleRenderedImage {
    Raster ras;

    public SingleTileRenderedImage(Raster ras, ColorModel colorModel) {
        this.ras = ras;
        this.tileGridXOffset = this.minX = ras.getMinX();
        this.tileGridYOffset = this.minY = ras.getMinY();
        this.tileWidth = this.width = ras.getWidth();
        this.tileHeight = this.height = ras.getHeight();
        this.sampleModel = ras.getSampleModel();
        this.colorModel = colorModel;
    }

    public Raster getTile(int tileX, int tileY) {
        if (tileX != 0 || tileY != 0) {
            throw new IllegalArgumentException(JaiI18N.getString("SingleTileRenderedImage0"));
        }
        return this.ras;
    }
}

