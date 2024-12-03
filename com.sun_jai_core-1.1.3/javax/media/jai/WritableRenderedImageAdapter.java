/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.Point;
import java.awt.image.Raster;
import java.awt.image.TileObserver;
import java.awt.image.WritableRaster;
import java.awt.image.WritableRenderedImage;
import javax.media.jai.JaiI18N;
import javax.media.jai.RenderedImageAdapter;

public final class WritableRenderedImageAdapter
extends RenderedImageAdapter
implements WritableRenderedImage {
    private WritableRenderedImage theWritableImage;

    public WritableRenderedImageAdapter(WritableRenderedImage im) {
        super(im);
        this.theWritableImage = im;
    }

    public final void addTileObserver(TileObserver tileObserver) {
        if (tileObserver == null) {
            throw new IllegalArgumentException(JaiI18N.getString("WritableRenderedImageAdapter0"));
        }
        this.theWritableImage.addTileObserver(tileObserver);
    }

    public final void removeTileObserver(TileObserver tileObserver) {
        if (tileObserver == null) {
            throw new IllegalArgumentException(JaiI18N.getString("WritableRenderedImageAdapter0"));
        }
        this.theWritableImage.removeTileObserver(tileObserver);
    }

    public final WritableRaster getWritableTile(int tileX, int tileY) {
        return this.theWritableImage.getWritableTile(tileX, tileY);
    }

    public final void releaseWritableTile(int tileX, int tileY) {
        this.theWritableImage.releaseWritableTile(tileX, tileY);
    }

    public final boolean isTileWritable(int tileX, int tileY) {
        return this.theWritableImage.isTileWritable(tileX, tileY);
    }

    public final Point[] getWritableTileIndices() {
        return this.theWritableImage.getWritableTileIndices();
    }

    public final boolean hasTileWriters() {
        return this.theWritableImage.hasTileWriters();
    }

    public final void setData(Raster raster) {
        if (raster == null) {
            throw new IllegalArgumentException(JaiI18N.getString("WritableRenderedImageAdapter1"));
        }
        this.theWritableImage.setData(raster);
    }
}

