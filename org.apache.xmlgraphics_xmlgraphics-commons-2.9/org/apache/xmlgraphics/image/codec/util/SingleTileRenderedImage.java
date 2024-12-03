/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.codec.util;

import java.awt.image.ColorModel;
import java.awt.image.Raster;
import org.apache.xmlgraphics.image.codec.util.PropertyUtil;
import org.apache.xmlgraphics.image.codec.util.SimpleRenderedImage;

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

    @Override
    public Raster getTile(int tileX, int tileY) {
        if (tileX != 0 || tileY != 0) {
            throw new IllegalArgumentException(PropertyUtil.getString("SingleTileRenderedImage0"));
        }
        return this.ras;
    }
}

