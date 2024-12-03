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
import com.sun.media.jai.opimage.MosaicOpImage;
import com.sun.medialib.mlib.Image;
import com.sun.medialib.mlib.mediaLibImage;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Vector;
import javax.media.jai.ImageLayout;
import javax.media.jai.PlanarImage;
import javax.media.jai.ROI;
import javax.media.jai.operator.MosaicDescriptor;
import javax.media.jai.operator.MosaicType;

final class MlibMosaicOpImage
extends MosaicOpImage {
    private int[] glow;
    private int[] ghigh;
    private int shift;

    public MlibMosaicOpImage(Vector sources, ImageLayout layout, Map config, MosaicType mosaicType, PlanarImage[] sourceAlpha, ROI[] sourceROI, double[][] sourceThreshold, double[] backgroundValues) {
        super(sources, layout, config, mosaicType, sourceAlpha, sourceROI, sourceThreshold, backgroundValues);
        int numSources = sources.size();
        int dataType = this.sampleModel.getDataType();
        if (dataType == 4 || dataType == 5) {
            throw new UnsupportedOperationException(JaiI18N.getString("MlibMosaicOpImage0"));
        }
        for (int i = 0; i < numSources; ++i) {
            int j = 0;
            while (j < this.numBands) {
                int[] nArray = this.threshold[i];
                int n = j++;
                nArray[n] = nArray[n] - 1;
            }
        }
        int minValue = -2147483647;
        int maxValue = Integer.MAX_VALUE;
        switch (dataType) {
            case 0: {
                minValue = 0;
                maxValue = 255;
                this.shift = 8;
                break;
            }
            case 1: {
                minValue = 0;
                maxValue = 65535;
                this.shift = 16;
                break;
            }
            case 2: {
                minValue = Short.MIN_VALUE;
                maxValue = Short.MAX_VALUE;
                this.shift = 16;
                break;
            }
            case 3: {
                minValue = Integer.MIN_VALUE;
                maxValue = Integer.MAX_VALUE;
                this.shift = 32;
                break;
            }
        }
        this.glow = new int[this.numBands];
        Arrays.fill(this.glow, minValue);
        this.ghigh = new int[this.numBands];
        Arrays.fill(this.ghigh, maxValue);
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect, Raster[] alphaRaster, Raster[] roiRaster) {
        int numSources = sources.length;
        ArrayList<Raster> sourceList = new ArrayList<Raster>(numSources);
        for (int i = 0; i < numSources; ++i) {
            if (sources[i] == null) continue;
            sourceList.add(sources[i]);
        }
        int numNonNullSources = sourceList.size();
        Raster[] nonNullSources = null;
        if (numNonNullSources != 0) {
            nonNullSources = new Raster[numNonNullSources];
            sourceList.toArray(nonNullSources);
        }
        int formatTag = MediaLibAccessor.findCompatibleTag(nonNullSources, dest);
        MediaLibAccessor dstAccessor = new MediaLibAccessor(dest, destRect, formatTag);
        mediaLibImage[] dst = dstAccessor.getMediaLibImages();
        int[] mlibBackground = dstAccessor.getIntParameters(0, this.background);
        if (numNonNullSources == 0) {
            Image.Clear((mediaLibImage)dst[0], (int[])mlibBackground);
            return;
        }
        MediaLibAccessor[] srcAccessor = new MediaLibAccessor[numSources];
        for (int i = 0; i < numSources; ++i) {
            if (sources[i] == null) continue;
            srcAccessor[i] = new MediaLibAccessor(sources[i], destRect, formatTag);
        }
        int[][] mlibThreshold = new int[numSources][];
        mediaLibImage[][] src = new mediaLibImage[numSources][];
        for (int i = 0; i < numSources; ++i) {
            if (srcAccessor[i] == null) continue;
            src[i] = srcAccessor[i].getMediaLibImages();
            mlibThreshold[i] = srcAccessor[i].getIntParameters(0, this.threshold[i]);
        }
        mediaLibImage tmpIm1 = null;
        mediaLibImage tmpImN = null;
        mediaLibImage[] tmpIm1Array = new mediaLibImage[]{tmpIm1};
        mediaLibImage[] tmpImNArray = new mediaLibImage[]{tmpImN};
        if (this.mosaicType == MosaicDescriptor.MOSAIC_TYPE_OVERLAY) {
            Image.Clear((mediaLibImage)dst[0], (int[])mlibBackground);
            for (int i = numSources - 1; i >= 0; --i) {
                if (src[i] == null) continue;
                mediaLibImage weight = this.getWeightImage(destRect, formatTag, dst[0], src[i][0], this.sourceAlpha != null && this.sourceAlpha[i] != null ? alphaRaster[i] : null, this.sourceROI != null && this.sourceROI[i] != null ? roiRaster[i] : null, mlibThreshold[i], tmpIm1Array, tmpImNArray);
                Image.Blend2((mediaLibImage)dst[0], (mediaLibImage)src[i][0], (mediaLibImage)weight);
            }
        } else if (this.mosaicType == MosaicDescriptor.MOSAIC_TYPE_BLEND) {
            tmpIm1 = new mediaLibImage(dst[0].getType(), 1, dst[0].getWidth(), dst[0].getHeight());
            tmpImN = new mediaLibImage(dst[0].getType(), dst[0].getChannels(), dst[0].getWidth(), dst[0].getHeight());
            mediaLibImage[] alphas = new mediaLibImage[numNonNullSources];
            mediaLibImage[] srcs = new mediaLibImage[numNonNullSources];
            int sourceCount = 0;
            for (int i = 0; i < numSources; ++i) {
                if (src[i] == null) continue;
                srcs[sourceCount] = src[i][0];
                alphas[sourceCount] = this.getWeightImage(destRect, formatTag, dst[0], src[i][0], this.sourceAlpha != null && this.sourceAlpha[i] != null ? alphaRaster[i] : null, this.sourceROI != null && this.sourceROI[i] != null ? roiRaster[i] : null, mlibThreshold[i], null, null);
                ++sourceCount;
            }
            if (sourceCount != numNonNullSources) {
                mediaLibImage[] srcsNew = new mediaLibImage[sourceCount];
                System.arraycopy(srcs, 0, srcsNew, 0, sourceCount);
                srcs = srcsNew;
                mediaLibImage[] alphasNew = new mediaLibImage[sourceCount];
                System.arraycopy(alphas, 0, alphasNew, 0, sourceCount);
                alphas = alphasNew;
            }
            Image.BlendMulti((mediaLibImage)dst[0], (mediaLibImage[])srcs, (mediaLibImage[])alphas, (int[])mlibBackground);
        }
        if (dstAccessor.isDataCopy()) {
            dstAccessor.clampDataArrays();
            dstAccessor.copyDataToRaster();
        }
    }

    private mediaLibImage getWeightImage(Rectangle destRect, int formatTag, mediaLibImage dst, mediaLibImage src, Raster alphaRaster, Raster roiRaster, int[] thresh, mediaLibImage[] tmpIm1, mediaLibImage[] tmpImN) {
        mediaLibImage weight = null;
        if (alphaRaster != null) {
            MediaLibAccessor alphaAccessor = new MediaLibAccessor(alphaRaster, destRect, formatTag);
            mediaLibImage[] alphaML = alphaAccessor.getMediaLibImages();
            if (this.isAlphaBitmask) {
                if (tmpIm1 == null) {
                    tmpIm1 = new mediaLibImage[]{null};
                }
                if (tmpIm1[0] == null) {
                    tmpIm1[0] = new mediaLibImage(src.getType(), 1, src.getWidth(), src.getHeight());
                }
                Image.Thresh1((mediaLibImage)tmpIm1[0], (mediaLibImage)alphaML[0], (int[])new int[]{0}, (int[])new int[]{this.ghigh[0]}, (int[])new int[]{this.glow[0]});
                weight = tmpIm1[0];
            } else {
                weight = alphaML[0];
            }
        } else if (roiRaster != null) {
            int roiFmtTag = MediaLibAccessor.findCompatibleTag(null, roiRaster);
            MediaLibAccessor roiAccessor = new MediaLibAccessor(roiRaster, destRect, roiFmtTag, true);
            mediaLibImage[] roi = roiAccessor.getMediaLibImages();
            if (tmpIm1 == null) {
                tmpIm1 = new mediaLibImage[]{null};
            }
            if (tmpIm1[0] == null) {
                tmpIm1[0] = new mediaLibImage(src.getType(), 1, src.getWidth(), src.getHeight());
            }
            if (tmpIm1[0].getType() != roi[0].getType()) {
                if (tmpIm1[0] == null) {
                    tmpIm1[0] = new mediaLibImage(src.getType(), 1, src.getWidth(), src.getHeight());
                }
                Image.DataTypeConvert((mediaLibImage)tmpIm1[0], (mediaLibImage)roi[0]);
            } else {
                tmpIm1[0] = roi[0];
            }
            Image.Thresh1((mediaLibImage)tmpIm1[0], (int[])new int[]{0}, (int[])new int[]{this.ghigh[0]}, (int[])new int[]{this.glow[0]});
            weight = tmpIm1[0];
        } else {
            if (tmpImN == null) {
                tmpImN = new mediaLibImage[]{null};
            }
            if (tmpImN[0] == null) {
                tmpImN[0] = dst.createCompatibleImage();
            }
            weight = tmpImN[0];
            Image.Thresh1((mediaLibImage)weight, (mediaLibImage)src, (int[])thresh, (int[])this.ghigh, (int[])this.glow);
        }
        return weight;
    }
}

