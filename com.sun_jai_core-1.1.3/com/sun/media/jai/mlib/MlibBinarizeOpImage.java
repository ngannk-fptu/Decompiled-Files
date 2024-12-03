/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.medialib.mlib.Image
 *  com.sun.medialib.mlib.mediaLibImage
 */
package com.sun.media.jai.mlib;

import com.sun.media.jai.mlib.JaiI18N;
import com.sun.media.jai.mlib.MediaLibAccessor;
import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.JDKWorkarounds;
import com.sun.medialib.mlib.Image;
import com.sun.medialib.mlib.mediaLibImage;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.PointOpImage;

class MlibBinarizeOpImage
extends PointOpImage {
    private double thresh;

    public MlibBinarizeOpImage(RenderedImage source, ImageLayout layout, Map config, double thresh) {
        super(source, MlibBinarizeOpImage.layoutHelper(source, layout, config), config, true);
        this.thresh = thresh;
    }

    private static ImageLayout layoutHelper(RenderedImage source, ImageLayout il, Map config) {
        ColorModel cm;
        ImageLayout layout = il == null ? new ImageLayout() : (ImageLayout)il.clone();
        SampleModel sm = layout.getSampleModel(source);
        if (!ImageUtil.isBinary(sm)) {
            sm = new MultiPixelPackedSampleModel(0, layout.getTileWidth(source), layout.getTileHeight(source), 1);
            layout.setSampleModel(sm);
        }
        if ((cm = layout.getColorModel(null)) == null || !JDKWorkarounds.areCompatibleDataModels(sm, cm)) {
            layout.setColorModel(ImageUtil.getCompatibleColorModel(sm, config));
        }
        return layout;
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        Raster source = sources[0];
        Rectangle srcRect = this.mapDestRect(destRect, 0);
        int sourceFormatTag = MediaLibAccessor.findCompatibleTag(sources, source);
        int destFormatTag = dest.getSampleModel().getDataType() | 0x100 | 0;
        MediaLibAccessor srcAccessor = new MediaLibAccessor(source, srcRect, sourceFormatTag, false);
        MediaLibAccessor dstAccessor = new MediaLibAccessor(dest, destRect, destFormatTag, true);
        switch (srcAccessor.getDataType()) {
            case 0: 
            case 1: 
            case 2: 
            case 3: {
                mediaLibImage[] srcML = srcAccessor.getMediaLibImages();
                mediaLibImage[] dstML = dstAccessor.getMediaLibImages();
                for (int i = 0; i < dstML.length; ++i) {
                    Image.Thresh1((mediaLibImage)dstML[i], (mediaLibImage)srcML[i], (int[])new int[]{(int)this.thresh - 1}, (int[])new int[]{1}, (int[])new int[]{0});
                }
                break;
            }
            case 4: 
            case 5: {
                mediaLibImage[] srcML = srcAccessor.getMediaLibImages();
                mediaLibImage[] dstML = dstAccessor.getMediaLibImages();
                for (int i = 0; i < dstML.length; ++i) {
                    Image.Thresh1_Fp((mediaLibImage)dstML[i], (mediaLibImage)srcML[i], (double[])new double[]{this.thresh}, (double[])new double[]{1.0}, (double[])new double[]{0.0});
                }
                break;
            }
            default: {
                String className = this.getClass().getName();
                throw new RuntimeException(JaiI18N.getString("Generic2"));
            }
        }
        if (dstAccessor.isDataCopy()) {
            dstAccessor.clampDataArrays();
            dstAccessor.copyDataToRaster();
        }
    }
}

