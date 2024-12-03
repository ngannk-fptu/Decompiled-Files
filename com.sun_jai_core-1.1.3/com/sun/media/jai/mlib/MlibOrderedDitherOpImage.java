/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.medialib.mlib.Image
 *  com.sun.medialib.mlib.mediaLibImage
 *  com.sun.medialib.mlib.mediaLibImageColormap
 */
package com.sun.media.jai.mlib;

import com.sun.media.jai.mlib.JaiI18N;
import com.sun.media.jai.mlib.MediaLibAccessor;
import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.JDKWorkarounds;
import com.sun.medialib.mlib.Image;
import com.sun.medialib.mlib.mediaLibImage;
import com.sun.medialib.mlib.mediaLibImageColormap;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ColorCube;
import javax.media.jai.ImageLayout;
import javax.media.jai.KernelJAI;
import javax.media.jai.PointOpImage;
import javax.media.jai.RasterFactory;

final class MlibOrderedDitherOpImage
extends PointOpImage {
    private static final int DMASK_SCALE_EXPONENT = 16;
    protected mediaLibImageColormap mlibColormap;
    protected int[][] dmask;
    protected int dmaskWidth;
    protected int dmaskHeight;
    protected int dmaskScale;

    static ImageLayout layoutHelper(ImageLayout layout, RenderedImage source, ColorCube colormap) {
        ColorModel cm;
        ImageLayout il = layout == null ? new ImageLayout(source) : (ImageLayout)layout.clone();
        SampleModel sm = il.getSampleModel(source);
        if (colormap.getNumBands() == 1 && colormap.getNumEntries() == 2 && !ImageUtil.isBinary(il.getSampleModel(source))) {
            sm = new MultiPixelPackedSampleModel(0, il.getTileWidth(source), il.getTileHeight(source), 1);
            il.setSampleModel(sm);
        }
        if (sm.getNumBands() != 1) {
            sm = RasterFactory.createComponentSampleModel(sm, sm.getTransferType(), sm.getWidth(), sm.getHeight(), 1);
            il.setSampleModel(sm);
            cm = il.getColorModel(null);
            if (cm != null && !JDKWorkarounds.areCompatibleDataModels(sm, cm)) {
                il.unsetValid(512);
            }
        }
        if ((layout == null || !il.isValid(512)) && source.getSampleModel().getDataType() == 0 && il.getSampleModel(null).getDataType() == 0 && colormap.getDataType() == 0 && colormap.getNumBands() == 3 && ((cm = source.getColorModel()) == null || cm != null && cm.getColorSpace().isCS_sRGB())) {
            int size = colormap.getNumEntries();
            byte[][] cmap = new byte[3][256];
            for (int i = 0; i < 3; ++i) {
                int j;
                byte[] band = cmap[i];
                byte[] data = colormap.getByteData(i);
                int offset = colormap.getOffset(i);
                int end = offset + size;
                for (j = 0; j < offset; ++j) {
                    band[j] = 0;
                }
                for (j = offset; j < end; ++j) {
                    band[j] = data[j - offset];
                }
                for (j = end; j < 256; ++j) {
                    band[j] = -1;
                }
            }
            il.setColorModel(new IndexColorModel(8, 256, cmap[0], cmap[1], cmap[2]));
        }
        return il;
    }

    public MlibOrderedDitherOpImage(RenderedImage source, Map config, ImageLayout layout, ColorCube colormap, KernelJAI[] ditherMask) {
        super(source, MlibOrderedDitherOpImage.layoutHelper(layout, source, colormap), config, true);
        this.mlibColormap = Image.ColorDitherInit((int[])colormap.getDimension(), (int)1, (int)(ImageUtil.isBinary(this.sampleModel) ? 0 : 1), (int)colormap.getNumBands(), (int)colormap.getNumEntries(), (int)colormap.getOffset(), (Object)colormap.getByteData());
        this.dmaskWidth = ditherMask[0].getWidth();
        this.dmaskHeight = ditherMask[0].getHeight();
        this.dmaskScale = 65536;
        int numMasks = ditherMask.length;
        this.dmask = new int[numMasks][];
        for (int k = 0; k < numMasks; ++k) {
            KernelJAI mask = ditherMask[k];
            if (mask.getWidth() != this.dmaskWidth || mask.getHeight() != this.dmaskHeight) {
                throw new IllegalArgumentException(JaiI18N.getString("MlibOrderedDitherOpImage0"));
            }
            float[] dmaskData = ditherMask[k].getKernelData();
            int numElements = dmaskData.length;
            this.dmask[k] = new int[numElements];
            int[] dm = this.dmask[k];
            for (int i = 0; i < numElements; ++i) {
                dm[i] = (int)(dmaskData[i] * (float)this.dmaskScale);
            }
        }
        this.permitInPlaceOperation();
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        int destFormatTag;
        int sourceFormatTag;
        Raster source = sources[0];
        if (ImageUtil.isBinary(dest.getSampleModel())) {
            sourceFormatTag = MediaLibAccessor.findCompatibleTag(sources, source);
            destFormatTag = dest.getSampleModel().getDataType() | 0x100 | 0;
        } else {
            sourceFormatTag = destFormatTag = MediaLibAccessor.findCompatibleTag(sources, dest);
        }
        MediaLibAccessor srcAccessor = new MediaLibAccessor(sources[0], destRect, sourceFormatTag, false);
        MediaLibAccessor dstAccessor = new MediaLibAccessor(dest, destRect, destFormatTag, true);
        mediaLibImage[] srcML = srcAccessor.getMediaLibImages();
        mediaLibImage[] dstML = dstAccessor.getMediaLibImages();
        Image.ColorOrderedDitherMxN((mediaLibImage)dstML[0], (mediaLibImage)srcML[0], (int[][])this.dmask, (int)this.dmaskWidth, (int)this.dmaskHeight, (int)16, (mediaLibImageColormap)this.mlibColormap);
        if (dstAccessor.isDataCopy()) {
            dstAccessor.clampDataArrays();
            dstAccessor.copyDataToRaster();
        }
    }
}

