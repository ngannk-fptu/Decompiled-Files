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
import java.awt.image.ComponentSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.LookupTableJAI;
import javax.media.jai.PointOpImage;

final class MlibLookupOpImage
extends PointOpImage {
    private LookupTableJAI table;

    public MlibLookupOpImage(RenderedImage source, Map config, ImageLayout layout, LookupTableJAI table) {
        super(source, layout, config, true);
        this.table = table;
        SampleModel sm = source.getSampleModel();
        if (this.sampleModel.getTransferType() != table.getDataType() || this.sampleModel.getNumBands() != table.getDestNumBands(sm.getNumBands())) {
            this.sampleModel = table.getDestSampleModel(sm, this.tileWidth, this.tileHeight);
            if (this.colorModel != null && !JDKWorkarounds.areCompatibleDataModels(this.sampleModel, this.colorModel)) {
                this.colorModel = ImageUtil.getCompatibleColorModel(this.sampleModel, config);
            }
        }
        this.permitInPlaceOperation();
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        mediaLibImage[] dstMLI;
        Raster source = sources[0];
        Rectangle srcRect = this.mapDestRect(destRect, 0);
        int srcTag = MediaLibAccessor.findCompatibleTag(null, source);
        int dstTag = MediaLibAccessor.findCompatibleTag(null, dest);
        SampleModel sm = source.getSampleModel();
        if (sm.getNumBands() > 1) {
            int srcCopy = srcTag & 0x80;
            int dstCopy = dstTag & 0x80;
            int srcDtype = srcTag & 0x7F;
            int dstDtype = dstTag & 0x7F;
            if (!(srcCopy == 0 && dstCopy == 0 && MediaLibAccessor.isPixelSequential(sm) && MediaLibAccessor.isPixelSequential(this.sampleModel) && MediaLibAccessor.hasMatchingBandOffsets((ComponentSampleModel)sm, (ComponentSampleModel)this.sampleModel))) {
                srcTag = srcDtype | 0x80;
                dstTag = dstDtype | 0x80;
            }
        }
        MediaLibAccessor src = new MediaLibAccessor(source, srcRect, srcTag);
        MediaLibAccessor dst = new MediaLibAccessor(dest, destRect, dstTag);
        mediaLibImage[] srcMLI = src.getMediaLibImages();
        if (srcMLI.length < (dstMLI = dst.getMediaLibImages()).length) {
            mediaLibImage srcMLI0 = srcMLI[0];
            srcMLI = new mediaLibImage[dstMLI.length];
            for (int i = 0; i < dstMLI.length; ++i) {
                srcMLI[i] = srcMLI0;
            }
        }
        int[] bandOffsets = dst.getBandOffsets();
        Object table = this.getTableData(bandOffsets);
        int[] offsets = this.getTableOffsets(bandOffsets);
        for (int i = 0; i < dstMLI.length; ++i) {
            Image.LookUp2((mediaLibImage)dstMLI[i], (mediaLibImage)srcMLI[i], (Object)table, (int[])offsets);
        }
        if (dst.isDataCopy()) {
            dst.copyDataToRaster();
        }
    }

    private Object getTableData(int[] bandOffsets) {
        int tbands = this.table.getNumBands();
        int dbands = this.sampleModel.getNumBands();
        Object data = null;
        switch (this.table.getDataType()) {
            case 0: {
                byte[][] bdata = new byte[dbands][];
                if (tbands < dbands) {
                    for (int i = 0; i < dbands; ++i) {
                        bdata[i] = this.table.getByteData(0);
                    }
                } else {
                    for (int i = 0; i < dbands; ++i) {
                        bdata[i] = this.table.getByteData(bandOffsets[i]);
                    }
                }
                data = bdata;
                break;
            }
            case 1: 
            case 2: {
                short[][] sdata = new short[dbands][];
                if (tbands < dbands) {
                    for (int i = 0; i < dbands; ++i) {
                        sdata[i] = this.table.getShortData(0);
                    }
                } else {
                    for (int i = 0; i < dbands; ++i) {
                        sdata[i] = this.table.getShortData(bandOffsets[i]);
                    }
                }
                data = sdata;
                break;
            }
            case 3: {
                int[][] idata = new int[dbands][];
                if (tbands < dbands) {
                    for (int i = 0; i < dbands; ++i) {
                        idata[i] = this.table.getIntData(0);
                    }
                } else {
                    for (int i = 0; i < dbands; ++i) {
                        idata[i] = this.table.getIntData(bandOffsets[i]);
                    }
                }
                data = idata;
                break;
            }
            case 4: {
                float[][] fdata = new float[dbands][];
                if (tbands < dbands) {
                    for (int i = 0; i < dbands; ++i) {
                        fdata[i] = this.table.getFloatData(0);
                    }
                } else {
                    for (int i = 0; i < dbands; ++i) {
                        fdata[i] = this.table.getFloatData(bandOffsets[i]);
                    }
                }
                data = fdata;
                break;
            }
            case 5: {
                double[][] ddata = new double[dbands][];
                if (tbands < dbands) {
                    for (int i = 0; i < dbands; ++i) {
                        ddata[i] = this.table.getDoubleData(0);
                    }
                } else {
                    for (int i = 0; i < dbands; ++i) {
                        ddata[i] = this.table.getDoubleData(bandOffsets[i]);
                    }
                }
                data = ddata;
            }
        }
        return data;
    }

    private int[] getTableOffsets(int[] bandOffsets) {
        int tbands = this.table.getNumBands();
        int dbands = this.sampleModel.getNumBands();
        int[] offsets = new int[dbands];
        if (tbands < dbands) {
            for (int i = 0; i < dbands; ++i) {
                offsets[i] = this.table.getOffset(0);
            }
        } else {
            for (int i = 0; i < dbands; ++i) {
                offsets[i] = this.table.getOffset(bandOffsets[i]);
            }
        }
        return offsets;
    }
}

