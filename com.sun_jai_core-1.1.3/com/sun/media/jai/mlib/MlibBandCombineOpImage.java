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
import java.awt.image.ComponentSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.PointOpImage;
import javax.media.jai.RasterFactory;

final class MlibBandCombineOpImage
extends PointOpImage {
    private double[] cmat = new double[9];
    private double[] offset = new double[3];
    private boolean isOffsetNonZero = false;

    public MlibBandCombineOpImage(RenderedImage source, Map config, ImageLayout layout, double[][] matrix) {
        super(source, layout, config, true);
        int numBands = matrix.length;
        if (this.getSampleModel().getNumBands() != numBands) {
            this.sampleModel = RasterFactory.createComponentSampleModel(this.sampleModel, this.sampleModel.getDataType(), this.tileWidth, this.tileHeight, numBands);
            if (this.colorModel != null && !JDKWorkarounds.areCompatibleDataModels(this.sampleModel, this.colorModel)) {
                this.colorModel = ImageUtil.getCompatibleColorModel(this.sampleModel, config);
            }
        }
        ComponentSampleModel csm = (ComponentSampleModel)source.getSampleModel();
        int[] bankIndices = csm.getBankIndices();
        int[] bandOffsets = csm.getBandOffsets();
        if (bankIndices[0] == bankIndices[1] && bankIndices[0] == bankIndices[2] && bandOffsets[0] > bandOffsets[2]) {
            for (int j = 0; j < 3; ++j) {
                int k = 8 - 3 * j;
                for (int i = 0; i < 3; ++i) {
                    this.cmat[k--] = matrix[j][i];
                }
                this.offset[2 - j] = matrix[j][3];
                if (this.offset[j] == 0.0) continue;
                this.isOffsetNonZero = true;
            }
        } else {
            for (int j = 0; j < 3; ++j) {
                int k = 3 * j;
                for (int i = 0; i < 3; ++i) {
                    this.cmat[k++] = matrix[j][i];
                }
                this.offset[j] = matrix[j][3];
                if (this.offset[j] == 0.0) continue;
                this.isOffsetNonZero = true;
            }
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
        switch (dstAccessor.getDataType()) {
            case 0: 
            case 1: 
            case 2: 
            case 3: {
                for (int i = 0; i < dstML.length; ++i) {
                    if (this.isOffsetNonZero) {
                        Image.ColorConvert2((mediaLibImage)dstML[i], (mediaLibImage)srcML[i], (double[])this.cmat, (double[])this.offset);
                        continue;
                    }
                    Image.ColorConvert1((mediaLibImage)dstML[i], (mediaLibImage)srcML[i], (double[])this.cmat);
                }
                break;
            }
            case 4: 
            case 5: {
                for (int i = 0; i < dstML.length; ++i) {
                    if (this.isOffsetNonZero) {
                        Image.ColorConvert2_Fp((mediaLibImage)dstML[i], (mediaLibImage)srcML[i], (double[])this.cmat, (double[])this.offset);
                        continue;
                    }
                    Image.ColorConvert1_Fp((mediaLibImage)dstML[i], (mediaLibImage)srcML[i], (double[])this.cmat);
                }
                break;
            }
            default: {
                String className = this.getClass().getName();
                throw new RuntimeException(className + JaiI18N.getString("Generic2"));
            }
        }
        if (dstAccessor.isDataCopy()) {
            dstAccessor.clampDataArrays();
            dstAccessor.copyDataToRaster();
        }
    }
}

