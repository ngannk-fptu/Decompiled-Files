/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.JaiI18N;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.OpImage;

final class BorderOpImage
extends OpImage {
    protected BorderExtender extender;

    public BorderOpImage(RenderedImage source, Map config, ImageLayout layout, int leftPad, int rightPad, int topPad, int bottomPad, BorderExtender extender) {
        super(BorderOpImage.vectorize(source), BorderOpImage.layoutHelper(layout, source, leftPad, rightPad, topPad, bottomPad), config, true);
        this.extender = extender;
    }

    private static ImageLayout layoutHelper(ImageLayout layout, RenderedImage source, int leftPad, int rightPad, int topPad, int bottomPad) {
        ImageLayout il = layout == null ? new ImageLayout() : (ImageLayout)layout.clone();
        il.setMinX(source.getMinX() - leftPad);
        il.setMinY(source.getMinY() - topPad);
        il.setWidth(source.getWidth() + leftPad + rightPad);
        il.setHeight(source.getHeight() + topPad + bottomPad);
        if (!il.isValid(16)) {
            il.setTileGridXOffset(il.getMinX(null));
        }
        if (!il.isValid(32)) {
            il.setTileGridYOffset(il.getMinY(null));
        }
        il.setSampleModel(source.getSampleModel());
        il.setColorModel(source.getColorModel());
        return il;
    }

    public Rectangle mapSourceRect(Rectangle sourceRect, int sourceIndex) {
        if (sourceRect == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (sourceIndex < 0 || sourceIndex >= this.getNumSources()) {
            throw new IllegalArgumentException(JaiI18N.getString("BorderOpImage0"));
        }
        return new Rectangle(sourceRect);
    }

    public Rectangle mapDestRect(Rectangle destRect, int sourceIndex) {
        if (destRect == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (sourceIndex < 0 || sourceIndex >= this.getNumSources()) {
            throw new IllegalArgumentException(JaiI18N.getString("BorderOpImage2"));
        }
        Rectangle srcBounds = this.getSourceImage(0).getBounds();
        return destRect.intersection(srcBounds);
    }

    public Raster computeTile(int tileX, int tileY) {
        WritableRaster dest = this.createTile(tileX, tileY);
        this.getSourceImage(0).copyExtendedData(dest, this.extender);
        return dest;
    }
}

