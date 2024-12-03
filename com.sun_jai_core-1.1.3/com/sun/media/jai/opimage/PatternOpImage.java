/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import java.awt.image.ColorModel;
import java.awt.image.Raster;
import javax.media.jai.ImageLayout;
import javax.media.jai.SourcelessOpImage;

public class PatternOpImage
extends SourcelessOpImage {
    protected Raster pattern;

    private static ImageLayout layoutHelper(Raster pattern, ColorModel colorModel) {
        return new ImageLayout(pattern.getMinX(), pattern.getMinY(), pattern.getWidth(), pattern.getHeight(), pattern.getSampleModel(), colorModel);
    }

    public PatternOpImage(Raster pattern, ColorModel colorModel, int minX, int minY, int width, int height) {
        super(PatternOpImage.layoutHelper(pattern, colorModel), null, pattern.getSampleModel(), minX, minY, width, height);
        this.pattern = pattern;
    }

    public Raster getTile(int tileX, int tileY) {
        return this.computeTile(tileX, tileY);
    }

    public Raster computeTile(int tileX, int tileY) {
        return this.pattern.createChild(this.tileGridXOffset, this.tileGridYOffset, this.tileWidth, this.tileHeight, this.tileXToX(tileX), this.tileYToY(tileY), null);
    }
}

