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
import com.sun.media.jai.opimage.TransposeOpImage;
import com.sun.medialib.mlib.Image;
import com.sun.medialib.mlib.mediaLibImage;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.PlanarImage;

final class MlibTransposeOpImage
extends TransposeOpImage {
    public MlibTransposeOpImage(RenderedImage source, Map config, ImageLayout layout, int type) {
        super(source, config, layout, type);
    }

    public Raster computeTile(int tileX, int tileY) {
        Point org = new Point(this.tileXToX(tileX), this.tileYToY(tileY));
        WritableRaster dest = this.createWritableRaster(this.sampleModel, org);
        Rectangle destRect = this.getTileRect(tileX, tileY).intersection(this.getBounds());
        PlanarImage src = this.getSourceImage(0);
        Rectangle srcRect = this.mapDestRect(destRect, 0).intersection(src.getBounds());
        Raster[] sources = new Raster[]{src.getData(srcRect)};
        this.computeRect(sources, dest, destRect);
        if (src.overlapsMultipleTiles(srcRect)) {
            this.recycleTile(sources[0]);
        }
        return dest;
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        Raster source = sources[0];
        Rectangle srcRect = source.getBounds();
        int formatTag = MediaLibAccessor.findCompatibleTag(sources, dest);
        MediaLibAccessor srcAccessor = new MediaLibAccessor(source, srcRect, formatTag);
        MediaLibAccessor dstAccessor = new MediaLibAccessor(dest, destRect, formatTag);
        int numBands = this.getSampleModel().getNumBands();
        switch (dstAccessor.getDataType()) {
            case 0: 
            case 1: 
            case 2: 
            case 3: {
                mediaLibImage[] srcML = srcAccessor.getMediaLibImages();
                mediaLibImage[] dstML = dstAccessor.getMediaLibImages();
                switch (this.type) {
                    case 0: {
                        Image.FlipX((mediaLibImage)dstML[0], (mediaLibImage)srcML[0]);
                        break;
                    }
                    case 1: {
                        Image.FlipY((mediaLibImage)dstML[0], (mediaLibImage)srcML[0]);
                        break;
                    }
                    case 2: {
                        Image.FlipMainDiag((mediaLibImage)dstML[0], (mediaLibImage)srcML[0]);
                        break;
                    }
                    case 3: {
                        Image.FlipAntiDiag((mediaLibImage)dstML[0], (mediaLibImage)srcML[0]);
                        break;
                    }
                    case 4: {
                        Image.Rotate90((mediaLibImage)dstML[0], (mediaLibImage)srcML[0]);
                        break;
                    }
                    case 5: {
                        Image.Rotate180((mediaLibImage)dstML[0], (mediaLibImage)srcML[0]);
                        break;
                    }
                    case 6: {
                        Image.Rotate270((mediaLibImage)dstML[0], (mediaLibImage)srcML[0]);
                    }
                }
                break;
            }
            case 4: 
            case 5: {
                mediaLibImage[] srcML = srcAccessor.getMediaLibImages();
                mediaLibImage[] dstML = dstAccessor.getMediaLibImages();
                switch (this.type) {
                    case 0: {
                        Image.FlipX_Fp((mediaLibImage)dstML[0], (mediaLibImage)srcML[0]);
                        break;
                    }
                    case 1: {
                        Image.FlipY_Fp((mediaLibImage)dstML[0], (mediaLibImage)srcML[0]);
                        break;
                    }
                    case 2: {
                        Image.FlipMainDiag_Fp((mediaLibImage)dstML[0], (mediaLibImage)srcML[0]);
                        break;
                    }
                    case 3: {
                        Image.FlipAntiDiag_Fp((mediaLibImage)dstML[0], (mediaLibImage)srcML[0]);
                        break;
                    }
                    case 4: {
                        Image.Rotate90_Fp((mediaLibImage)dstML[0], (mediaLibImage)srcML[0]);
                        break;
                    }
                    case 5: {
                        Image.Rotate180_Fp((mediaLibImage)dstML[0], (mediaLibImage)srcML[0]);
                        break;
                    }
                    case 6: {
                        Image.Rotate270_Fp((mediaLibImage)dstML[0], (mediaLibImage)srcML[0]);
                    }
                }
                break;
            }
            default: {
                throw new RuntimeException(JaiI18N.getString("Generic2"));
            }
        }
        if (dstAccessor.isDataCopy()) {
            dstAccessor.copyDataToRaster();
        }
    }
}

