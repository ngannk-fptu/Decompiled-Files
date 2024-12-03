/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.util.JDKWorkarounds;
import java.awt.image.ColorModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.PointOpImage;

final class BandSelectOpImage
extends PointOpImage {
    private boolean areDataCopied;
    private int[] bandIndices;

    private static ImageLayout layoutHelper(ImageLayout layout, RenderedImage source, int[] bandIndices) {
        ImageLayout il = layout == null ? new ImageLayout() : (ImageLayout)layout.clone();
        SampleModel sourceSM = source.getSampleModel();
        int numBands = bandIndices.length;
        SampleModel sm = null;
        if (sourceSM instanceof SinglePixelPackedSampleModel && numBands < 3) {
            int[] nArray;
            int n = sourceSM.getWidth();
            int n2 = sourceSM.getHeight();
            int n3 = sourceSM.getWidth() * numBands;
            if (numBands == 1) {
                int[] nArray2 = new int[1];
                nArray = nArray2;
                nArray2[0] = 0;
            } else {
                int[] nArray3 = new int[2];
                nArray3[0] = 0;
                nArray = nArray3;
                nArray3[1] = 1;
            }
            sm = new PixelInterleavedSampleModel(0, n, n2, numBands, n3, nArray);
        } else {
            sm = sourceSM.createSubsetSampleModel(bandIndices);
        }
        il.setSampleModel(sm);
        ColorModel cm = il.getColorModel(null);
        if (cm != null && !JDKWorkarounds.areCompatibleDataModels(sm, cm)) {
            il.unsetValid(512);
        }
        il.setTileGridXOffset(source.getTileGridXOffset());
        il.setTileGridYOffset(source.getTileGridYOffset());
        il.setTileWidth(source.getTileWidth());
        il.setTileHeight(source.getTileHeight());
        return il;
    }

    public BandSelectOpImage(RenderedImage source, Map config, ImageLayout layout, int[] bandIndices) {
        super(BandSelectOpImage.vectorize(source), BandSelectOpImage.layoutHelper(layout, source, bandIndices), config, true);
        this.areDataCopied = source.getSampleModel() instanceof SinglePixelPackedSampleModel && bandIndices.length < 3;
        this.bandIndices = (int[])bandIndices.clone();
    }

    public boolean computesUniqueTiles() {
        return this.areDataCopied;
    }

    public Raster computeTile(int tileX, int tileY) {
        Raster tile = this.getSourceImage(0).getTile(tileX, tileY);
        if (this.areDataCopied) {
            tile = tile.createChild(tile.getMinX(), tile.getMinY(), tile.getWidth(), tile.getHeight(), tile.getMinX(), tile.getMinY(), this.bandIndices);
            WritableRaster raster = this.createTile(tileX, tileY);
            raster.setRect(tile);
            return raster;
        }
        return tile.createChild(tile.getMinX(), tile.getMinY(), tile.getWidth(), tile.getHeight(), tile.getMinX(), tile.getMinY(), this.bandIndices);
    }

    public Raster getTile(int tileX, int tileY) {
        return this.computeTile(tileX, tileY);
    }
}

