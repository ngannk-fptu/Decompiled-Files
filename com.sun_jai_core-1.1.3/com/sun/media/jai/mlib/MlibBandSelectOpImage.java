/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.medialib.mlib.Image
 *  com.sun.medialib.mlib.mediaLibImage
 */
package com.sun.media.jai.mlib;

import com.sun.media.jai.mlib.MediaLibAccessor;
import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.JDKWorkarounds;
import com.sun.medialib.mlib.Image;
import com.sun.medialib.mlib.mediaLibImage;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.PointOpImage;
import javax.media.jai.RasterFactory;

final class MlibBandSelectOpImage
extends PointOpImage {
    private int cmask = 0;

    public MlibBandSelectOpImage(RenderedImage source, Map config, ImageLayout layout, int[] bandIndices) {
        super(source, layout, config, true);
        int numBands = bandIndices.length;
        if (this.getSampleModel().getNumBands() != numBands) {
            this.sampleModel = RasterFactory.createComponentSampleModel(this.sampleModel, this.sampleModel.getDataType(), this.tileWidth, this.tileHeight, numBands);
            if (this.colorModel != null && !JDKWorkarounds.areCompatibleDataModels(this.sampleModel, this.colorModel)) {
                this.colorModel = ImageUtil.getCompatibleColorModel(this.sampleModel, config);
            }
        }
        int maxShift = source.getSampleModel().getNumBands() - 1;
        for (int i = 0; i < bandIndices.length; ++i) {
            this.cmask |= 1 << maxShift - bandIndices[i];
        }
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        Raster source = sources[0];
        Rectangle srcRect = this.mapDestRect(destRect, 0);
        int formatTag = MediaLibAccessor.findCompatibleTag(sources, dest);
        MediaLibAccessor srcAccessor = new MediaLibAccessor(source, srcRect, formatTag);
        MediaLibAccessor dstAccessor = new MediaLibAccessor(dest, destRect, formatTag);
        mediaLibImage[] srcML = srcAccessor.getMediaLibImages();
        mediaLibImage[] dstML = dstAccessor.getMediaLibImages();
        for (int i = 0; i < dstML.length; ++i) {
            Image.ChannelExtract((mediaLibImage)dstML[i], (mediaLibImage)srcML[i], (int)this.cmask);
        }
        if (dstAccessor.isDataCopy()) {
            dstAccessor.clampDataArrays();
            dstAccessor.copyDataToRaster();
        }
    }
}

