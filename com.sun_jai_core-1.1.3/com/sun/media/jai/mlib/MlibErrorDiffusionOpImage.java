/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.medialib.mlib.Image
 *  com.sun.medialib.mlib.mediaLibImage
 *  com.sun.medialib.mlib.mediaLibImageColormap
 */
package com.sun.media.jai.mlib;

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
import javax.media.jai.LookupTableJAI;
import javax.media.jai.RasterFactory;
import javax.media.jai.UntiledOpImage;

final class MlibErrorDiffusionOpImage
extends UntiledOpImage {
    private static final int KERNEL_SCALE_EXPONENT = 16;
    protected mediaLibImageColormap mlibColormap;
    protected int[] kernel;
    protected int kernelWidth;
    protected int kernelHeight;
    protected int kernelKeyX;
    protected int kernelKeyY;
    protected int kernelScale;

    static ImageLayout layoutHelper(ImageLayout layout, RenderedImage source, LookupTableJAI colormap) {
        ColorModel cm;
        ImageLayout il = layout == null ? new ImageLayout() : (ImageLayout)layout.clone();
        il.setMinX(source.getMinX());
        il.setMinY(source.getMinY());
        il.setWidth(source.getWidth());
        il.setHeight(source.getHeight());
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
        if ((layout == null || !il.isValid(512)) && source.getSampleModel().getDataType() == 0 && sm.getDataType() == 0 && colormap.getDataType() == 0 && colormap.getNumBands() == 3 && ((cm = source.getColorModel()) == null || cm != null && cm.getColorSpace().isCS_sRGB())) {
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

    public MlibErrorDiffusionOpImage(RenderedImage source, Map config, ImageLayout layout, LookupTableJAI colormap, KernelJAI errorKernel) {
        super(source, config, layout);
        this.mlibColormap = Image.ColorDitherInit((int[])(colormap instanceof ColorCube ? ((ColorCube)colormap).getDimension() : null), (int)1, (int)(ImageUtil.isBinary(this.sampleModel) ? 0 : 1), (int)colormap.getNumBands(), (int)colormap.getNumEntries(), (int)colormap.getOffset(), (Object)colormap.getByteData());
        this.kernelWidth = errorKernel.getWidth();
        this.kernelHeight = errorKernel.getHeight();
        this.kernelKeyX = errorKernel.getXOrigin();
        this.kernelKeyY = errorKernel.getYOrigin();
        this.kernelScale = 65536;
        float[] kernelData = errorKernel.getKernelData();
        int numElements = kernelData.length;
        this.kernel = new int[numElements];
        for (int i = 0; i < numElements; ++i) {
            this.kernel[i] = (int)(kernelData[i] * (float)this.kernelScale);
        }
    }

    protected void computeImage(Raster[] sources, WritableRaster dest, Rectangle destRect) {
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
        Image.ColorErrorDiffusionMxN((mediaLibImage)dstML[0], (mediaLibImage)srcML[0], (int[])this.kernel, (int)this.kernelWidth, (int)this.kernelHeight, (int)this.kernelKeyX, (int)this.kernelKeyY, (int)16, (mediaLibImageColormap)this.mlibColormap);
        if (dstAccessor.isDataCopy()) {
            dstAccessor.clampDataArrays();
            dstAccessor.copyDataToRaster();
        }
    }
}

