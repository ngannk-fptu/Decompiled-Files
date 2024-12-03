/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.util.JDKWorkarounds;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import java.util.Vector;
import javax.media.jai.ImageLayout;
import javax.media.jai.PixelAccessor;
import javax.media.jai.PointOpImage;
import javax.media.jai.RasterFactory;
import javax.media.jai.UnpackedImageData;

class BandMergeOpImage
extends PointOpImage {
    ColorModel[] colorModels;

    public BandMergeOpImage(Vector sources, Map config, ImageLayout layout) {
        super(sources, BandMergeOpImage.layoutHelper(sources, layout), config, true);
        this.permitInPlaceOperation();
        int numSrcs = sources.size();
        this.colorModels = new ColorModel[numSrcs];
        for (int i = 0; i < numSrcs; ++i) {
            this.colorModels[i] = ((RenderedImage)sources.get(i)).getColorModel();
        }
    }

    private static int totalNumBands(Vector sources) {
        int total = 0;
        for (int i = 0; i < sources.size(); ++i) {
            RenderedImage image = (RenderedImage)sources.get(i);
            if (image.getColorModel() instanceof IndexColorModel) {
                total += image.getColorModel().getNumComponents();
                continue;
            }
            total += image.getSampleModel().getNumBands();
        }
        return total;
    }

    private static ImageLayout layoutHelper(Vector sources, ImageLayout il) {
        ColorModel cm;
        ImageLayout layout = il == null ? new ImageLayout() : (ImageLayout)il.clone();
        int numSources = sources.size();
        int destNumBands = BandMergeOpImage.totalNumBands(sources);
        int destDataType = 0;
        RenderedImage srci = (RenderedImage)sources.get(0);
        Rectangle destBounds = new Rectangle(srci.getMinX(), srci.getMinY(), srci.getWidth(), srci.getHeight());
        for (int i = 0; i < numSources; ++i) {
            srci = (RenderedImage)sources.get(i);
            destBounds = destBounds.intersection(new Rectangle(srci.getMinX(), srci.getMinY(), srci.getWidth(), srci.getHeight()));
            int typei = srci.getSampleModel().getTransferType();
            destDataType = typei > destDataType ? typei : destDataType;
        }
        SampleModel sm = layout.getSampleModel((RenderedImage)sources.get(0));
        if (sm.getNumBands() < destNumBands) {
            int[] destOffsets = new int[destNumBands];
            for (int i = 0; i < destNumBands; ++i) {
                destOffsets[i] = i;
            }
            int destTileWidth = sm.getWidth();
            int destTileHeight = sm.getHeight();
            if (layout.isValid(64)) {
                destTileWidth = layout.getTileWidth((RenderedImage)sources.get(0));
            }
            if (layout.isValid(128)) {
                destTileHeight = layout.getTileHeight((RenderedImage)sources.get(0));
            }
            sm = RasterFactory.createComponentSampleModel(sm, destDataType, destTileWidth, destTileHeight, destNumBands);
            layout.setSampleModel(sm);
        }
        if ((cm = layout.getColorModel(null)) != null && !JDKWorkarounds.areCompatibleDataModels(sm, cm)) {
            layout.unsetValid(512);
        }
        return layout;
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        int destType = dest.getTransferType();
        switch (destType) {
            case 0: {
                this.byteLoop(sources, dest, destRect);
                break;
            }
            case 1: 
            case 2: {
                this.shortLoop(sources, dest, destRect);
                break;
            }
            case 3: {
                this.intLoop(sources, dest, destRect);
                break;
            }
            case 4: {
                this.floatLoop(sources, dest, destRect);
                break;
            }
            case 5: {
                this.doubleLoop(sources, dest, destRect);
                break;
            }
            default: {
                throw new RuntimeException();
            }
        }
    }

    private void byteLoop(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        int nSrcs = sources.length;
        int[] snbands = new int[nSrcs];
        PixelAccessor[] pas = new PixelAccessor[nSrcs];
        for (int i = 0; i < nSrcs; ++i) {
            pas[i] = new PixelAccessor(sources[i].getSampleModel(), this.colorModels[i]);
            snbands[i] = this.colorModels[i] instanceof IndexColorModel ? this.colorModels[i].getNumComponents() : sources[i].getNumBands();
        }
        int dnbands = dest.getNumBands();
        int destType = dest.getTransferType();
        PixelAccessor d = new PixelAccessor(dest.getSampleModel(), null);
        UnpackedImageData dimd = d.getPixels(dest, destRect, destType, true);
        byte[][] dstdata = (byte[][])dimd.data;
        int db = 0;
        for (int sindex = 0; sindex < nSrcs; ++sindex) {
            UnpackedImageData simd = this.colorModels[sindex] instanceof IndexColorModel ? pas[sindex].getComponents(sources[sindex], destRect, sources[sindex].getSampleModel().getTransferType()) : pas[sindex].getPixels(sources[sindex], destRect, sources[sindex].getSampleModel().getTransferType(), false);
            int srcPixelStride = simd.pixelStride;
            int srcLineStride = simd.lineStride;
            int dstPixelStride = dimd.pixelStride;
            int dstLineStride = dimd.lineStride;
            int dRectWidth = destRect.width;
            for (int sb = 0; sb < snbands[sindex] && db < dnbands; ++sb, ++db) {
                byte[] dstdatabandb = dstdata[db];
                byte[][] srcdata = (byte[][])simd.data;
                byte[] srcdatabandsb = srcdata[sb];
                int srcstart = simd.bandOffsets[sb];
                int dststart = dimd.bandOffsets[db];
                int y = 0;
                while (y < destRect.height) {
                    int i = 0;
                    int srcpos = srcstart;
                    int dstpos = dststart;
                    while (i < dRectWidth) {
                        dstdatabandb[dstpos] = srcdatabandsb[srcpos];
                        ++i;
                        srcpos += srcPixelStride;
                        dstpos += dstPixelStride;
                    }
                    ++y;
                    srcstart += srcLineStride;
                    dststart += dstLineStride;
                }
            }
        }
        d.setPixels(dimd);
    }

    private void shortLoop(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        int nSrcs = sources.length;
        int[] snbands = new int[nSrcs];
        PixelAccessor[] pas = new PixelAccessor[nSrcs];
        for (int i = 0; i < nSrcs; ++i) {
            pas[i] = new PixelAccessor(sources[i].getSampleModel(), this.colorModels[i]);
            snbands[i] = this.colorModels[i] instanceof IndexColorModel ? this.colorModels[i].getNumComponents() : sources[i].getNumBands();
        }
        int dnbands = dest.getNumBands();
        int destType = dest.getTransferType();
        PixelAccessor d = new PixelAccessor(dest.getSampleModel(), null);
        UnpackedImageData dimd = d.getPixels(dest, destRect, destType, true);
        short[][] dstdata = (short[][])dimd.data;
        int db = 0;
        for (int sindex = 0; sindex < nSrcs; ++sindex) {
            UnpackedImageData simd = this.colorModels[sindex] instanceof IndexColorModel ? pas[sindex].getComponents(sources[sindex], destRect, sources[sindex].getSampleModel().getTransferType()) : pas[sindex].getPixels(sources[sindex], destRect, sources[sindex].getSampleModel().getTransferType(), false);
            int srcPixelStride = simd.pixelStride;
            int srcLineStride = simd.lineStride;
            int dstPixelStride = dimd.pixelStride;
            int dstLineStride = dimd.lineStride;
            int dRectWidth = destRect.width;
            int sb = 0;
            while (sb < snbands[sindex]) {
                if (db < dnbands) {
                    short[][] srcdata = (short[][])simd.data;
                    int srcstart = simd.bandOffsets[sb];
                    int dststart = dimd.bandOffsets[db];
                    int y = 0;
                    while (y < destRect.height) {
                        int i = 0;
                        int srcpos = srcstart;
                        int dstpos = dststart;
                        while (i < dRectWidth) {
                            dstdata[db][dstpos] = srcdata[sb][srcpos];
                            ++i;
                            srcpos += srcPixelStride;
                            dstpos += dstPixelStride;
                        }
                        ++y;
                        srcstart += srcLineStride;
                        dststart += dstLineStride;
                    }
                }
                ++sb;
                ++db;
            }
        }
        d.setPixels(dimd);
    }

    private void intLoop(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        int nSrcs = sources.length;
        int[] snbands = new int[nSrcs];
        PixelAccessor[] pas = new PixelAccessor[nSrcs];
        for (int i = 0; i < nSrcs; ++i) {
            pas[i] = new PixelAccessor(sources[i].getSampleModel(), this.colorModels[i]);
            snbands[i] = this.colorModels[i] instanceof IndexColorModel ? this.colorModels[i].getNumComponents() : sources[i].getNumBands();
        }
        int dnbands = dest.getNumBands();
        int destType = dest.getTransferType();
        PixelAccessor d = new PixelAccessor(dest.getSampleModel(), null);
        UnpackedImageData dimd = d.getPixels(dest, destRect, destType, true);
        int[][] dstdata = (int[][])dimd.data;
        int db = 0;
        for (int sindex = 0; sindex < nSrcs; ++sindex) {
            UnpackedImageData simd = this.colorModels[sindex] instanceof IndexColorModel ? pas[sindex].getComponents(sources[sindex], destRect, sources[sindex].getSampleModel().getTransferType()) : pas[sindex].getPixels(sources[sindex], destRect, sources[sindex].getSampleModel().getTransferType(), false);
            int srcPixelStride = simd.pixelStride;
            int srcLineStride = simd.lineStride;
            int dstPixelStride = dimd.pixelStride;
            int dstLineStride = dimd.lineStride;
            int dRectWidth = destRect.width;
            int sb = 0;
            while (sb < snbands[sindex]) {
                if (db < dnbands) {
                    int[][] srcdata = (int[][])simd.data;
                    int srcstart = simd.bandOffsets[sb];
                    int dststart = dimd.bandOffsets[db];
                    int y = 0;
                    while (y < destRect.height) {
                        int i = 0;
                        int srcpos = srcstart;
                        int dstpos = dststart;
                        while (i < dRectWidth) {
                            dstdata[db][dstpos] = srcdata[sb][srcpos];
                            ++i;
                            srcpos += srcPixelStride;
                            dstpos += dstPixelStride;
                        }
                        ++y;
                        srcstart += srcLineStride;
                        dststart += dstLineStride;
                    }
                }
                ++sb;
                ++db;
            }
        }
        d.setPixels(dimd);
    }

    private void floatLoop(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        int nSrcs = sources.length;
        int[] snbands = new int[nSrcs];
        PixelAccessor[] pas = new PixelAccessor[nSrcs];
        for (int i = 0; i < nSrcs; ++i) {
            pas[i] = new PixelAccessor(sources[i].getSampleModel(), this.colorModels[i]);
            snbands[i] = this.colorModels[i] instanceof IndexColorModel ? this.colorModels[i].getNumComponents() : sources[i].getNumBands();
        }
        int dnbands = dest.getNumBands();
        int destType = dest.getTransferType();
        PixelAccessor d = new PixelAccessor(dest.getSampleModel(), null);
        UnpackedImageData dimd = d.getPixels(dest, destRect, destType, true);
        float[][] dstdata = (float[][])dimd.data;
        int db = 0;
        for (int sindex = 0; sindex < nSrcs; ++sindex) {
            UnpackedImageData simd = this.colorModels[sindex] instanceof IndexColorModel ? pas[sindex].getComponents(sources[sindex], destRect, sources[sindex].getSampleModel().getTransferType()) : pas[sindex].getPixels(sources[sindex], destRect, sources[sindex].getSampleModel().getTransferType(), false);
            int srcPixelStride = simd.pixelStride;
            int srcLineStride = simd.lineStride;
            int dstPixelStride = dimd.pixelStride;
            int dstLineStride = dimd.lineStride;
            int dRectWidth = destRect.width;
            int sb = 0;
            while (sb < snbands[sindex]) {
                if (db < dnbands) {
                    float[][] srcdata = (float[][])simd.data;
                    int srcstart = simd.bandOffsets[sb];
                    int dststart = dimd.bandOffsets[db];
                    int y = 0;
                    while (y < destRect.height) {
                        int i = 0;
                        int srcpos = srcstart;
                        int dstpos = dststart;
                        while (i < dRectWidth) {
                            dstdata[db][dstpos] = srcdata[sb][srcpos];
                            ++i;
                            srcpos += srcPixelStride;
                            dstpos += dstPixelStride;
                        }
                        ++y;
                        srcstart += srcLineStride;
                        dststart += dstLineStride;
                    }
                }
                ++sb;
                ++db;
            }
        }
        d.setPixels(dimd);
    }

    private void doubleLoop(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        int nSrcs = sources.length;
        int[] snbands = new int[nSrcs];
        PixelAccessor[] pas = new PixelAccessor[nSrcs];
        for (int i = 0; i < nSrcs; ++i) {
            pas[i] = new PixelAccessor(sources[i].getSampleModel(), this.colorModels[i]);
            snbands[i] = this.colorModels[i] instanceof IndexColorModel ? this.colorModels[i].getNumComponents() : sources[i].getNumBands();
        }
        int dnbands = dest.getNumBands();
        int destType = dest.getTransferType();
        PixelAccessor d = new PixelAccessor(dest.getSampleModel(), null);
        UnpackedImageData dimd = d.getPixels(dest, destRect, destType, true);
        double[][] dstdata = (double[][])dimd.data;
        int db = 0;
        for (int sindex = 0; sindex < nSrcs; ++sindex) {
            UnpackedImageData simd = this.colorModels[sindex] instanceof IndexColorModel ? pas[sindex].getComponents(sources[sindex], destRect, sources[sindex].getSampleModel().getTransferType()) : pas[sindex].getPixels(sources[sindex], destRect, sources[sindex].getSampleModel().getTransferType(), false);
            int srcPixelStride = simd.pixelStride;
            int srcLineStride = simd.lineStride;
            int dstPixelStride = dimd.pixelStride;
            int dstLineStride = dimd.lineStride;
            int dRectWidth = destRect.width;
            int sb = 0;
            while (sb < snbands[sindex]) {
                if (db < dnbands) {
                    double[][] srcdata = (double[][])simd.data;
                    int srcstart = simd.bandOffsets[sb];
                    int dststart = dimd.bandOffsets[db];
                    int y = 0;
                    while (y < destRect.height) {
                        int i = 0;
                        int srcpos = srcstart;
                        int dstpos = dststart;
                        while (i < dRectWidth) {
                            dstdata[db][dstpos] = srcdata[sb][srcpos];
                            ++i;
                            srcpos += srcPixelStride;
                            dstpos += dstPixelStride;
                        }
                        ++y;
                        srcstart += srcLineStride;
                        dststart += dstLineStride;
                    }
                }
                ++sb;
                ++db;
            }
        }
        d.setPixels(dimd);
    }
}

