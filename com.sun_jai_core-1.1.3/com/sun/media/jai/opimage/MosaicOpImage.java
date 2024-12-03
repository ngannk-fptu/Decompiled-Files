/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.JaiI18N;
import com.sun.media.jai.util.ImageUtil;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Vector;
import javax.media.jai.BorderExtender;
import javax.media.jai.BorderExtenderConstant;
import javax.media.jai.ImageLayout;
import javax.media.jai.OpImage;
import javax.media.jai.PlanarImage;
import javax.media.jai.ROI;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFormatTag;
import javax.media.jai.operator.MosaicDescriptor;
import javax.media.jai.operator.MosaicType;

public class MosaicOpImage
extends OpImage {
    private static final int WEIGHT_TYPE_ALPHA = 1;
    private static final int WEIGHT_TYPE_ROI = 2;
    private static final int WEIGHT_TYPE_THRESHOLD = 3;
    protected MosaicType mosaicType;
    protected PlanarImage[] sourceAlpha;
    protected ROI[] sourceROI;
    protected double[][] sourceThreshold;
    protected double[] backgroundValues;
    protected int numBands = this.sampleModel.getNumBands();
    protected int[] background;
    protected int[][] threshold;
    protected boolean isAlphaBitmask = false;
    private BorderExtender sourceExtender;
    private BorderExtender zeroExtender;
    private PlanarImage[] roiImage;

    private static final ImageLayout getLayout(Vector sources, ImageLayout layout) {
        int i;
        RenderedImage source0 = null;
        SampleModel targetSM = null;
        ColorModel targetCM = null;
        int numSources = sources.size();
        if (numSources > 0) {
            source0 = (RenderedImage)sources.get(0);
            targetSM = source0.getSampleModel();
            targetCM = source0.getColorModel();
        } else if (layout != null && layout.isValid(268)) {
            targetSM = layout.getSampleModel(null);
            if (targetSM == null) {
                throw new IllegalArgumentException(JaiI18N.getString("MosaicOpImage7"));
            }
        } else {
            throw new IllegalArgumentException(JaiI18N.getString("MosaicOpImage8"));
        }
        int dataType = targetSM.getDataType();
        int numBands = targetSM.getNumBands();
        int sampleSize = targetSM.getSampleSize(0);
        for (i = 1; i < numBands; ++i) {
            if (targetSM.getSampleSize(i) == sampleSize) continue;
            throw new IllegalArgumentException(JaiI18N.getString("MosaicOpImage1"));
        }
        if (numSources < 1) {
            return (ImageLayout)layout.clone();
        }
        for (i = 1; i < numSources; ++i) {
            RenderedImage source = (RenderedImage)sources.get(i);
            SampleModel sourceSM = source.getSampleModel();
            if (sourceSM.getDataType() != dataType) {
                throw new IllegalArgumentException(JaiI18N.getString("MosaicOpImage2"));
            }
            if (sourceSM.getNumBands() != numBands) {
                throw new IllegalArgumentException(JaiI18N.getString("MosaicOpImage3"));
            }
            for (int j = 0; j < numBands; ++j) {
                if (sourceSM.getSampleSize(j) == sampleSize) continue;
                throw new IllegalArgumentException(JaiI18N.getString("MosaicOpImage1"));
            }
        }
        ImageLayout mosaicLayout = layout == null ? new ImageLayout() : (ImageLayout)layout.clone();
        Rectangle mosaicBounds = new Rectangle();
        if (mosaicLayout.isValid(15)) {
            mosaicBounds.setBounds(mosaicLayout.getMinX(null), mosaicLayout.getMinY(null), mosaicLayout.getWidth(null), mosaicLayout.getHeight(null));
        } else if (numSources > 0) {
            mosaicBounds.setBounds(source0.getMinX(), source0.getMinY(), source0.getWidth(), source0.getHeight());
            for (int i2 = 1; i2 < numSources; ++i2) {
                RenderedImage source = (RenderedImage)sources.get(i2);
                Rectangle sourceBounds = new Rectangle(source.getMinX(), source.getMinY(), source.getWidth(), source.getHeight());
                mosaicBounds = mosaicBounds.union(sourceBounds);
            }
        }
        mosaicLayout.setMinX(mosaicBounds.x);
        mosaicLayout.setMinY(mosaicBounds.y);
        mosaicLayout.setWidth(mosaicBounds.width);
        mosaicLayout.setHeight(mosaicBounds.height);
        if (mosaicLayout.isValid(256)) {
            SampleModel destSM = mosaicLayout.getSampleModel(null);
            boolean unsetSampleModel = destSM.getNumBands() != numBands || destSM.getDataType() != dataType;
            for (int i3 = 0; !unsetSampleModel && i3 < numBands; ++i3) {
                if (destSM.getSampleSize(i3) == sampleSize) continue;
                unsetSampleModel = true;
            }
            if (unsetSampleModel) {
                mosaicLayout.unsetValid(256);
            }
        }
        return mosaicLayout;
    }

    public MosaicOpImage(Vector sources, ImageLayout layout, Map config, MosaicType mosaicType, PlanarImage[] sourceAlpha, ROI[] sourceROI, double[][] sourceThreshold, double[] backgroundValues) {
        super(sources, MosaicOpImage.getLayout(sources, layout), config, true);
        double sourceExtensionConstant;
        int i;
        int numSources = this.getNumSources();
        this.mosaicType = mosaicType;
        this.sourceAlpha = null;
        if (sourceAlpha != null) {
            for (i = 0; i < sourceAlpha.length; ++i) {
                if (sourceAlpha[i] == null) continue;
                SampleModel alphaSM = sourceAlpha[i].getSampleModel();
                if (alphaSM.getNumBands() != 1) {
                    throw new IllegalArgumentException(JaiI18N.getString("MosaicOpImage4"));
                }
                if (alphaSM.getDataType() != this.sampleModel.getDataType()) {
                    throw new IllegalArgumentException(JaiI18N.getString("MosaicOpImage5"));
                }
                if (alphaSM.getSampleSize(0) == this.sampleModel.getSampleSize(0)) continue;
                throw new IllegalArgumentException(JaiI18N.getString("MosaicOpImage6"));
            }
            this.sourceAlpha = new PlanarImage[numSources];
            System.arraycopy(sourceAlpha, 0, this.sourceAlpha, 0, Math.min(sourceAlpha.length, numSources));
        }
        this.sourceROI = null;
        if (sourceROI != null) {
            this.sourceROI = new ROI[numSources];
            System.arraycopy(sourceROI, 0, this.sourceROI, 0, Math.min(sourceROI.length, numSources));
        }
        boolean bl = this.isAlphaBitmask = mosaicType != MosaicDescriptor.MOSAIC_TYPE_BLEND || sourceAlpha == null || sourceAlpha.length < numSources;
        if (!this.isAlphaBitmask) {
            for (i = 0; i < numSources; ++i) {
                if (sourceAlpha[i] != null) continue;
                this.isAlphaBitmask = true;
                break;
            }
        }
        this.sourceThreshold = new double[numSources][this.numBands];
        if (sourceThreshold == null) {
            sourceThreshold = new double[][]{{1.0}};
        }
        for (i = 0; i < numSources; ++i) {
            if (i < ((double[][])sourceThreshold).length && sourceThreshold[i] != null) {
                if (sourceThreshold[i].length < this.numBands) {
                    Arrays.fill(this.sourceThreshold[i], sourceThreshold[i][0]);
                    continue;
                }
                System.arraycopy(sourceThreshold[i], 0, this.sourceThreshold[i], 0, this.numBands);
                continue;
            }
            this.sourceThreshold[i] = this.sourceThreshold[0];
        }
        this.threshold = new int[numSources][this.numBands];
        for (i = 0; i < numSources; ++i) {
            for (int j = 0; j < this.numBands; ++j) {
                this.threshold[i][j] = (int)this.sourceThreshold[i][j];
            }
        }
        this.backgroundValues = new double[this.numBands];
        if (backgroundValues == null) {
            backgroundValues = new double[]{0.0};
        }
        if (backgroundValues.length < this.numBands) {
            Arrays.fill(this.backgroundValues, backgroundValues[0]);
        } else {
            System.arraycopy(backgroundValues, 0, this.backgroundValues, 0, this.numBands);
        }
        this.background = new int[this.backgroundValues.length];
        int dataType = this.sampleModel.getDataType();
        block18: for (int i2 = 0; i2 < this.background.length; ++i2) {
            switch (dataType) {
                case 0: {
                    this.background[i2] = ImageUtil.clampRoundByte(this.backgroundValues[i2]);
                    continue block18;
                }
                case 1: {
                    this.background[i2] = ImageUtil.clampRoundUShort(this.backgroundValues[i2]);
                    continue block18;
                }
                case 2: {
                    this.background[i2] = ImageUtil.clampRoundShort(this.backgroundValues[i2]);
                    continue block18;
                }
                case 3: {
                    this.background[i2] = ImageUtil.clampRoundInt(this.backgroundValues[i2]);
                    continue block18;
                }
            }
        }
        switch (dataType) {
            case 0: {
                sourceExtensionConstant = 0.0;
                break;
            }
            case 1: {
                sourceExtensionConstant = 0.0;
                break;
            }
            case 2: {
                sourceExtensionConstant = -32768.0;
                break;
            }
            case 3: {
                sourceExtensionConstant = -2.147483648E9;
                break;
            }
            case 4: {
                sourceExtensionConstant = -3.4028234663852886E38;
                break;
            }
            default: {
                sourceExtensionConstant = -1.7976931348623157E308;
            }
        }
        BorderExtender borderExtender = this.sourceExtender = sourceExtensionConstant == 0.0 ? BorderExtender.createInstance(0) : new BorderExtenderConstant(new double[]{sourceExtensionConstant});
        if (sourceAlpha != null || sourceROI != null) {
            this.zeroExtender = BorderExtender.createInstance(0);
        }
        if (sourceROI != null) {
            this.roiImage = new PlanarImage[numSources];
            for (int i3 = 0; i3 < sourceROI.length; ++i3) {
                if (sourceROI[i3] == null) continue;
                this.roiImage[i3] = sourceROI[i3].getAsImage();
            }
        }
    }

    public Rectangle mapDestRect(Rectangle destRect, int sourceIndex) {
        if (destRect == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (sourceIndex < 0 || sourceIndex >= this.getNumSources()) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
        }
        return destRect.intersection(this.getSourceImage(sourceIndex).getBounds());
    }

    public Rectangle mapSourceRect(Rectangle sourceRect, int sourceIndex) {
        if (sourceRect == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (sourceIndex < 0 || sourceIndex >= this.getNumSources()) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
        }
        return sourceRect.intersection(this.getBounds());
    }

    public Raster computeTile(int tileX, int tileY) {
        int i;
        WritableRaster dest = this.createWritableRaster(this.sampleModel, new Point(this.tileXToX(tileX), this.tileYToY(tileY)));
        Rectangle destRect = this.getTileRect(tileX, tileY);
        int numSources = this.getNumSources();
        Raster[] rasterSources = new Raster[numSources];
        Raster[] alpha = this.sourceAlpha != null ? new Raster[numSources] : null;
        Raster[] roi = this.sourceROI != null ? new Raster[numSources] : null;
        for (i = 0; i < numSources; ++i) {
            PlanarImage source = this.getSourceImage(i);
            Rectangle srcRect = this.mapDestRect(destRect, i);
            Raster raster = rasterSources[i] = srcRect != null && srcRect.isEmpty() ? null : source.getExtendedData(destRect, this.sourceExtender);
            if (rasterSources[i] == null) continue;
            if (this.sourceAlpha != null && this.sourceAlpha[i] != null) {
                alpha[i] = this.sourceAlpha[i].getExtendedData(destRect, this.zeroExtender);
            }
            if (this.sourceROI == null || this.sourceROI[i] == null) continue;
            roi[i] = this.roiImage[i].getExtendedData(destRect, this.zeroExtender);
        }
        this.computeRect(rasterSources, dest, destRect, alpha, roi);
        for (i = 0; i < numSources; ++i) {
            PlanarImage source;
            Raster sourceData = rasterSources[i];
            if (sourceData == null || !(source = this.getSourceImage(i)).overlapsMultipleTiles(sourceData.getBounds())) continue;
            this.recycleTile(sourceData);
        }
        return dest;
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        this.computeRect(sources, dest, destRect, null, null);
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect, Raster[] alphaRaster, Raster[] roiRaster) {
        int numSources = sources.length;
        ArrayList<Raster> sourceList = new ArrayList<Raster>(numSources);
        for (int i = 0; i < numSources; ++i) {
            if (sources[i] == null) continue;
            sourceList.add(sources[i]);
        }
        int numNonNullSources = sourceList.size();
        if (numNonNullSources == 0) {
            ImageUtil.fillBackground(dest, destRect, this.backgroundValues);
            return;
        }
        SampleModel[] sourceSM = new SampleModel[numNonNullSources];
        for (int i = 0; i < numNonNullSources; ++i) {
            sourceSM[i] = ((Raster)sourceList.get(i)).getSampleModel();
        }
        int formatTagID = RasterAccessor.findCompatibleTag(sourceSM, dest.getSampleModel());
        RasterAccessor[] s = new RasterAccessor[numSources];
        for (int i = 0; i < numSources; ++i) {
            if (sources[i] == null) continue;
            RasterFormatTag formatTag = new RasterFormatTag(sources[i].getSampleModel(), formatTagID);
            s[i] = new RasterAccessor(sources[i], destRect, formatTag, null);
        }
        RasterAccessor d = new RasterAccessor(dest, destRect, new RasterFormatTag(dest.getSampleModel(), formatTagID), null);
        RasterAccessor[] a = new RasterAccessor[numSources];
        if (alphaRaster != null) {
            for (int i = 0; i < numSources; ++i) {
                if (alphaRaster[i] == null) continue;
                SampleModel alphaSM = alphaRaster[i].getSampleModel();
                int alphaFormatTagID = RasterAccessor.findCompatibleTag(null, alphaSM);
                RasterFormatTag alphaFormatTag = new RasterFormatTag(alphaSM, alphaFormatTagID);
                a[i] = new RasterAccessor(alphaRaster[i], destRect, alphaFormatTag, this.sourceAlpha[i].getColorModel());
            }
        }
        switch (d.getDataType()) {
            case 0: {
                this.computeRectByte(s, d, a, roiRaster);
                break;
            }
            case 1: {
                this.computeRectUShort(s, d, a, roiRaster);
                break;
            }
            case 2: {
                this.computeRectShort(s, d, a, roiRaster);
                break;
            }
            case 3: {
                this.computeRectInt(s, d, a, roiRaster);
                break;
            }
            case 4: {
                this.computeRectFloat(s, d, a, roiRaster);
                break;
            }
            case 5: {
                this.computeRectDouble(s, d, a, roiRaster);
            }
        }
        d.copyDataToRaster();
    }

    private void computeRectByte(RasterAccessor[] src, RasterAccessor dst, RasterAccessor[] alfa, Raster[] roi) {
        int numSources = src.length;
        int[] srcLineStride = new int[numSources];
        int[] srcPixelStride = new int[numSources];
        int[][] srcBandOffsets = new int[numSources][];
        byte[][][] srcData = new byte[numSources][][];
        for (int i = 0; i < numSources; ++i) {
            if (src[i] == null) continue;
            srcLineStride[i] = src[i].getScanlineStride();
            srcPixelStride[i] = src[i].getPixelStride();
            srcBandOffsets[i] = src[i].getBandOffsets();
            srcData[i] = src[i].getByteDataArrays();
        }
        int dstMinX = dst.getX();
        int dstMinY = dst.getY();
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstMaxX = dstMinX + dstWidth;
        int dstMaxY = dstMinY + dstHeight;
        int dstBands = dst.getNumBands();
        int dstLineStride = dst.getScanlineStride();
        int dstPixelStride = dst.getPixelStride();
        int[] dstBandOffsets = dst.getBandOffsets();
        byte[][] dstData = dst.getByteDataArrays();
        boolean hasAlpha = false;
        for (int i = 0; i < numSources; ++i) {
            if (alfa[i] == null) continue;
            hasAlpha = true;
            break;
        }
        int[] alfaLineStride = null;
        int[] alfaPixelStride = null;
        Object alfaBandOffsets = null;
        Object alfaData = null;
        if (hasAlpha) {
            alfaLineStride = new int[numSources];
            alfaPixelStride = new int[numSources];
            alfaBandOffsets = new int[numSources][];
            alfaData = new byte[numSources][][];
            for (int i = 0; i < numSources; ++i) {
                if (alfa[i] == null) continue;
                alfaLineStride[i] = alfa[i].getScanlineStride();
                alfaPixelStride[i] = alfa[i].getPixelStride();
                alfaBandOffsets[i] = alfa[i].getBandOffsets();
                alfaData[i] = alfa[i].getByteDataArrays();
            }
        }
        int[] weightTypes = new int[numSources];
        for (int i = 0; i < numSources; ++i) {
            weightTypes[i] = 3;
            if (alfa[i] != null) {
                weightTypes[i] = 1;
                continue;
            }
            if (this.sourceROI == null || this.sourceROI[i] == null) continue;
            weightTypes[i] = 2;
        }
        int[] sLineOffsets = new int[numSources];
        int[] sPixelOffsets = new int[numSources];
        byte[][] sBandData = new byte[numSources][];
        int[] aLineOffsets = null;
        int[] aPixelOffsets = null;
        Object aBandData = null;
        if (hasAlpha) {
            aLineOffsets = new int[numSources];
            aPixelOffsets = new int[numSources];
            aBandData = new byte[numSources][];
        }
        for (int b = 0; b < dstBands; ++b) {
            int dstX;
            int dPixelOffset;
            int s;
            int dstY;
            for (int s2 = 0; s2 < numSources; ++s2) {
                if (src[s2] != null) {
                    sBandData[s2] = srcData[s2][b];
                    sLineOffsets[s2] = srcBandOffsets[s2][b];
                }
                if (weightTypes[s2] != 1) continue;
                aBandData[s2] = alfaData[s2][0];
                aLineOffsets[s2] = alfaBandOffsets[s2][0];
            }
            byte[] dBandData = dstData[b];
            int dLineOffset = dstBandOffsets[b];
            if (this.mosaicType == MosaicDescriptor.MOSAIC_TYPE_OVERLAY) {
                for (dstY = dstMinY; dstY < dstMaxY; ++dstY) {
                    for (s = 0; s < numSources; ++s) {
                        if (src[s] != null) {
                            sPixelOffsets[s] = sLineOffsets[s];
                            int n = s;
                            sLineOffsets[n] = sLineOffsets[n] + srcLineStride[s];
                        }
                        if (alfa[s] == null) continue;
                        aPixelOffsets[s] = aLineOffsets[s];
                        int n = s;
                        aLineOffsets[n] = aLineOffsets[n] + alfaLineStride[s];
                    }
                    dPixelOffset = dLineOffset;
                    dLineOffset += dstLineStride;
                    for (dstX = dstMinX; dstX < dstMaxX; ++dstX) {
                        boolean setDestValue = false;
                        for (int s3 = 0; s3 < numSources; ++s3) {
                            if (src[s3] == null) continue;
                            byte sourceValue = sBandData[s3][sPixelOffsets[s3]];
                            int n = s3;
                            sPixelOffsets[n] = sPixelOffsets[n] + srcPixelStride[s3];
                            switch (weightTypes[s3]) {
                                case 1: {
                                    setDestValue = aBandData[s3][aPixelOffsets[s3]] != 0;
                                    int n2 = s3;
                                    aPixelOffsets[n2] = aPixelOffsets[n2] + alfaPixelStride[s3];
                                    break;
                                }
                                case 2: {
                                    setDestValue = roi[s3].getSample(dstX, dstY, 0) > 0;
                                    break;
                                }
                                default: {
                                    boolean bl = setDestValue = (double)(sourceValue & 0xFF) >= this.sourceThreshold[s3][b];
                                }
                            }
                            if (!setDestValue) continue;
                            dBandData[dPixelOffset] = sourceValue;
                            for (int k = s3 + 1; k < numSources; ++k) {
                                if (src[k] != null) {
                                    int n3 = k;
                                    sPixelOffsets[n3] = sPixelOffsets[n3] + srcPixelStride[k];
                                }
                                if (alfa[k] == null) continue;
                                int n4 = k;
                                aPixelOffsets[n4] = aPixelOffsets[n4] + alfaPixelStride[k];
                            }
                            break;
                        }
                        if (!setDestValue) {
                            dBandData[dPixelOffset] = (byte)this.background[b];
                        }
                        dPixelOffset += dstPixelStride;
                    }
                }
                continue;
            }
            for (dstY = dstMinY; dstY < dstMaxY; ++dstY) {
                for (s = 0; s < numSources; ++s) {
                    if (src[s] != null) {
                        sPixelOffsets[s] = sLineOffsets[s];
                        int n = s;
                        sLineOffsets[n] = sLineOffsets[n] + srcLineStride[s];
                    }
                    if (weightTypes[s] != 1) continue;
                    aPixelOffsets[s] = aLineOffsets[s];
                    int n = s;
                    aLineOffsets[n] = aLineOffsets[n] + alfaLineStride[s];
                }
                dPixelOffset = dLineOffset;
                dLineOffset += dstLineStride;
                for (dstX = dstMinX; dstX < dstMaxX; ++dstX) {
                    float numerator = 0.0f;
                    float denominator = 0.0f;
                    for (int s4 = 0; s4 < numSources; ++s4) {
                        if (src[s4] == null) continue;
                        byte sourceValue = sBandData[s4][sPixelOffsets[s4]];
                        int n = s4;
                        sPixelOffsets[n] = sPixelOffsets[n] + srcPixelStride[s4];
                        float weight = 0.0f;
                        switch (weightTypes[s4]) {
                            case 1: {
                                weight = aBandData[s4][aPixelOffsets[s4]] & 0xFF;
                                weight = weight > 0.0f && this.isAlphaBitmask ? 1.0f : (weight /= 255.0f);
                                int n5 = s4;
                                aPixelOffsets[n5] = aPixelOffsets[n5] + alfaPixelStride[s4];
                                break;
                            }
                            case 2: {
                                weight = roi[s4].getSample(dstX, dstY, 0) > 0 ? 1.0f : 0.0f;
                                break;
                            }
                            default: {
                                weight = (double)(sourceValue & 0xFF) >= this.sourceThreshold[s4][b] ? 1.0f : 0.0f;
                            }
                        }
                        numerator += weight * (float)(sourceValue & 0xFF);
                        denominator += weight;
                    }
                    dBandData[dPixelOffset] = (double)denominator == 0.0 ? (byte)this.background[b] : ImageUtil.clampRoundByte(numerator / denominator);
                    dPixelOffset += dstPixelStride;
                }
            }
        }
    }

    private void computeRectUShort(RasterAccessor[] src, RasterAccessor dst, RasterAccessor[] alfa, Raster[] roi) {
        int numSources = src.length;
        int[] srcLineStride = new int[numSources];
        int[] srcPixelStride = new int[numSources];
        int[][] srcBandOffsets = new int[numSources][];
        short[][][] srcData = new short[numSources][][];
        for (int i = 0; i < numSources; ++i) {
            if (src[i] == null) continue;
            srcLineStride[i] = src[i].getScanlineStride();
            srcPixelStride[i] = src[i].getPixelStride();
            srcBandOffsets[i] = src[i].getBandOffsets();
            srcData[i] = src[i].getShortDataArrays();
        }
        int dstMinX = dst.getX();
        int dstMinY = dst.getY();
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstMaxX = dstMinX + dstWidth;
        int dstMaxY = dstMinY + dstHeight;
        int dstBands = dst.getNumBands();
        int dstLineStride = dst.getScanlineStride();
        int dstPixelStride = dst.getPixelStride();
        int[] dstBandOffsets = dst.getBandOffsets();
        short[][] dstData = dst.getShortDataArrays();
        boolean hasAlpha = false;
        for (int i = 0; i < numSources; ++i) {
            if (alfa[i] == null) continue;
            hasAlpha = true;
            break;
        }
        int[] alfaLineStride = null;
        int[] alfaPixelStride = null;
        Object alfaBandOffsets = null;
        Object alfaData = null;
        if (hasAlpha) {
            alfaLineStride = new int[numSources];
            alfaPixelStride = new int[numSources];
            alfaBandOffsets = new int[numSources][];
            alfaData = new short[numSources][][];
            for (int i = 0; i < numSources; ++i) {
                if (alfa[i] == null) continue;
                alfaLineStride[i] = alfa[i].getScanlineStride();
                alfaPixelStride[i] = alfa[i].getPixelStride();
                alfaBandOffsets[i] = alfa[i].getBandOffsets();
                alfaData[i] = alfa[i].getShortDataArrays();
            }
        }
        int[] weightTypes = new int[numSources];
        for (int i = 0; i < numSources; ++i) {
            weightTypes[i] = 3;
            if (alfa[i] != null) {
                weightTypes[i] = 1;
                continue;
            }
            if (this.sourceROI == null || this.sourceROI[i] == null) continue;
            weightTypes[i] = 2;
        }
        int[] sLineOffsets = new int[numSources];
        int[] sPixelOffsets = new int[numSources];
        short[][] sBandData = new short[numSources][];
        int[] aLineOffsets = null;
        int[] aPixelOffsets = null;
        Object aBandData = null;
        if (hasAlpha) {
            aLineOffsets = new int[numSources];
            aPixelOffsets = new int[numSources];
            aBandData = new short[numSources][];
        }
        for (int b = 0; b < dstBands; ++b) {
            int dstX;
            int dPixelOffset;
            int s;
            int dstY;
            for (int s2 = 0; s2 < numSources; ++s2) {
                if (src[s2] != null) {
                    sBandData[s2] = srcData[s2][b];
                    sLineOffsets[s2] = srcBandOffsets[s2][b];
                }
                if (weightTypes[s2] != 1) continue;
                aBandData[s2] = alfaData[s2][0];
                aLineOffsets[s2] = alfaBandOffsets[s2][0];
            }
            short[] dBandData = dstData[b];
            int dLineOffset = dstBandOffsets[b];
            if (this.mosaicType == MosaicDescriptor.MOSAIC_TYPE_OVERLAY) {
                for (dstY = dstMinY; dstY < dstMaxY; ++dstY) {
                    for (s = 0; s < numSources; ++s) {
                        if (src[s] != null) {
                            sPixelOffsets[s] = sLineOffsets[s];
                            int n = s;
                            sLineOffsets[n] = sLineOffsets[n] + srcLineStride[s];
                        }
                        if (alfa[s] == null) continue;
                        aPixelOffsets[s] = aLineOffsets[s];
                        int n = s;
                        aLineOffsets[n] = aLineOffsets[n] + alfaLineStride[s];
                    }
                    dPixelOffset = dLineOffset;
                    dLineOffset += dstLineStride;
                    for (dstX = dstMinX; dstX < dstMaxX; ++dstX) {
                        boolean setDestValue = false;
                        for (int s3 = 0; s3 < numSources; ++s3) {
                            if (src[s3] == null) continue;
                            short sourceValue = sBandData[s3][sPixelOffsets[s3]];
                            int n = s3;
                            sPixelOffsets[n] = sPixelOffsets[n] + srcPixelStride[s3];
                            switch (weightTypes[s3]) {
                                case 1: {
                                    setDestValue = aBandData[s3][aPixelOffsets[s3]] != 0;
                                    int n2 = s3;
                                    aPixelOffsets[n2] = aPixelOffsets[n2] + alfaPixelStride[s3];
                                    break;
                                }
                                case 2: {
                                    setDestValue = roi[s3].getSample(dstX, dstY, 0) > 0;
                                    break;
                                }
                                default: {
                                    boolean bl = setDestValue = (double)(sourceValue & 0xFFFF) >= this.sourceThreshold[s3][b];
                                }
                            }
                            if (!setDestValue) continue;
                            dBandData[dPixelOffset] = sourceValue;
                            for (int k = s3 + 1; k < numSources; ++k) {
                                if (src[k] != null) {
                                    int n3 = k;
                                    sPixelOffsets[n3] = sPixelOffsets[n3] + srcPixelStride[k];
                                }
                                if (alfa[k] == null) continue;
                                int n4 = k;
                                aPixelOffsets[n4] = aPixelOffsets[n4] + alfaPixelStride[k];
                            }
                            break;
                        }
                        if (!setDestValue) {
                            dBandData[dPixelOffset] = (short)this.background[b];
                        }
                        dPixelOffset += dstPixelStride;
                    }
                }
                continue;
            }
            for (dstY = dstMinY; dstY < dstMaxY; ++dstY) {
                for (s = 0; s < numSources; ++s) {
                    if (src[s] != null) {
                        sPixelOffsets[s] = sLineOffsets[s];
                        int n = s;
                        sLineOffsets[n] = sLineOffsets[n] + srcLineStride[s];
                    }
                    if (weightTypes[s] != 1) continue;
                    aPixelOffsets[s] = aLineOffsets[s];
                    int n = s;
                    aLineOffsets[n] = aLineOffsets[n] + alfaLineStride[s];
                }
                dPixelOffset = dLineOffset;
                dLineOffset += dstLineStride;
                for (dstX = dstMinX; dstX < dstMaxX; ++dstX) {
                    float numerator = 0.0f;
                    float denominator = 0.0f;
                    for (int s4 = 0; s4 < numSources; ++s4) {
                        if (src[s4] == null) continue;
                        short sourceValue = sBandData[s4][sPixelOffsets[s4]];
                        int n = s4;
                        sPixelOffsets[n] = sPixelOffsets[n] + srcPixelStride[s4];
                        float weight = 0.0f;
                        switch (weightTypes[s4]) {
                            case 1: {
                                weight = aBandData[s4][aPixelOffsets[s4]] & 0xFFFF;
                                weight = weight > 0.0f && this.isAlphaBitmask ? 1.0f : (weight /= 65535.0f);
                                int n5 = s4;
                                aPixelOffsets[n5] = aPixelOffsets[n5] + alfaPixelStride[s4];
                                break;
                            }
                            case 2: {
                                weight = roi[s4].getSample(dstX, dstY, 0) > 0 ? 1.0f : 0.0f;
                                break;
                            }
                            default: {
                                weight = (double)(sourceValue & 0xFFFF) >= this.sourceThreshold[s4][b] ? 1.0f : 0.0f;
                            }
                        }
                        numerator += weight * (float)(sourceValue & 0xFFFF);
                        denominator += weight;
                    }
                    dBandData[dPixelOffset] = (double)denominator == 0.0 ? (short)this.background[b] : ImageUtil.clampRoundUShort(numerator / denominator);
                    dPixelOffset += dstPixelStride;
                }
            }
        }
    }

    private void computeRectShort(RasterAccessor[] src, RasterAccessor dst, RasterAccessor[] alfa, Raster[] roi) {
        int numSources = src.length;
        int[] srcLineStride = new int[numSources];
        int[] srcPixelStride = new int[numSources];
        int[][] srcBandOffsets = new int[numSources][];
        short[][][] srcData = new short[numSources][][];
        for (int i = 0; i < numSources; ++i) {
            if (src[i] == null) continue;
            srcLineStride[i] = src[i].getScanlineStride();
            srcPixelStride[i] = src[i].getPixelStride();
            srcBandOffsets[i] = src[i].getBandOffsets();
            srcData[i] = src[i].getShortDataArrays();
        }
        int dstMinX = dst.getX();
        int dstMinY = dst.getY();
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstMaxX = dstMinX + dstWidth;
        int dstMaxY = dstMinY + dstHeight;
        int dstBands = dst.getNumBands();
        int dstLineStride = dst.getScanlineStride();
        int dstPixelStride = dst.getPixelStride();
        int[] dstBandOffsets = dst.getBandOffsets();
        short[][] dstData = dst.getShortDataArrays();
        boolean hasAlpha = false;
        for (int i = 0; i < numSources; ++i) {
            if (alfa[i] == null) continue;
            hasAlpha = true;
            break;
        }
        int[] alfaLineStride = null;
        int[] alfaPixelStride = null;
        Object alfaBandOffsets = null;
        Object alfaData = null;
        if (hasAlpha) {
            alfaLineStride = new int[numSources];
            alfaPixelStride = new int[numSources];
            alfaBandOffsets = new int[numSources][];
            alfaData = new short[numSources][][];
            for (int i = 0; i < numSources; ++i) {
                if (alfa[i] == null) continue;
                alfaLineStride[i] = alfa[i].getScanlineStride();
                alfaPixelStride[i] = alfa[i].getPixelStride();
                alfaBandOffsets[i] = alfa[i].getBandOffsets();
                alfaData[i] = alfa[i].getShortDataArrays();
            }
        }
        int[] weightTypes = new int[numSources];
        for (int i = 0; i < numSources; ++i) {
            weightTypes[i] = 3;
            if (alfa[i] != null) {
                weightTypes[i] = 1;
                continue;
            }
            if (this.sourceROI == null || this.sourceROI[i] == null) continue;
            weightTypes[i] = 2;
        }
        int[] sLineOffsets = new int[numSources];
        int[] sPixelOffsets = new int[numSources];
        short[][] sBandData = new short[numSources][];
        int[] aLineOffsets = null;
        int[] aPixelOffsets = null;
        Object aBandData = null;
        if (hasAlpha) {
            aLineOffsets = new int[numSources];
            aPixelOffsets = new int[numSources];
            aBandData = new short[numSources][];
        }
        for (int b = 0; b < dstBands; ++b) {
            int dstX;
            int dPixelOffset;
            int s;
            int dstY;
            for (int s2 = 0; s2 < numSources; ++s2) {
                if (src[s2] != null) {
                    sBandData[s2] = srcData[s2][b];
                    sLineOffsets[s2] = srcBandOffsets[s2][b];
                }
                if (weightTypes[s2] != 1) continue;
                aBandData[s2] = alfaData[s2][0];
                aLineOffsets[s2] = alfaBandOffsets[s2][0];
            }
            short[] dBandData = dstData[b];
            int dLineOffset = dstBandOffsets[b];
            if (this.mosaicType == MosaicDescriptor.MOSAIC_TYPE_OVERLAY) {
                for (dstY = dstMinY; dstY < dstMaxY; ++dstY) {
                    for (s = 0; s < numSources; ++s) {
                        if (src[s] != null) {
                            sPixelOffsets[s] = sLineOffsets[s];
                            int n = s;
                            sLineOffsets[n] = sLineOffsets[n] + srcLineStride[s];
                        }
                        if (alfa[s] == null) continue;
                        aPixelOffsets[s] = aLineOffsets[s];
                        int n = s;
                        aLineOffsets[n] = aLineOffsets[n] + alfaLineStride[s];
                    }
                    dPixelOffset = dLineOffset;
                    dLineOffset += dstLineStride;
                    for (dstX = dstMinX; dstX < dstMaxX; ++dstX) {
                        boolean setDestValue = false;
                        for (int s3 = 0; s3 < numSources; ++s3) {
                            if (src[s3] == null) continue;
                            short sourceValue = sBandData[s3][sPixelOffsets[s3]];
                            int n = s3;
                            sPixelOffsets[n] = sPixelOffsets[n] + srcPixelStride[s3];
                            switch (weightTypes[s3]) {
                                case 1: {
                                    setDestValue = aBandData[s3][aPixelOffsets[s3]] != 0;
                                    int n2 = s3;
                                    aPixelOffsets[n2] = aPixelOffsets[n2] + alfaPixelStride[s3];
                                    break;
                                }
                                case 2: {
                                    setDestValue = roi[s3].getSample(dstX, dstY, 0) > 0;
                                    break;
                                }
                                default: {
                                    boolean bl = setDestValue = (double)sourceValue >= this.sourceThreshold[s3][b];
                                }
                            }
                            if (!setDestValue) continue;
                            dBandData[dPixelOffset] = sourceValue;
                            for (int k = s3 + 1; k < numSources; ++k) {
                                if (src[k] != null) {
                                    int n3 = k;
                                    sPixelOffsets[n3] = sPixelOffsets[n3] + srcPixelStride[k];
                                }
                                if (alfa[k] == null) continue;
                                int n4 = k;
                                aPixelOffsets[n4] = aPixelOffsets[n4] + alfaPixelStride[k];
                            }
                            break;
                        }
                        if (!setDestValue) {
                            dBandData[dPixelOffset] = (short)this.background[b];
                        }
                        dPixelOffset += dstPixelStride;
                    }
                }
                continue;
            }
            for (dstY = dstMinY; dstY < dstMaxY; ++dstY) {
                for (s = 0; s < numSources; ++s) {
                    if (src[s] != null) {
                        sPixelOffsets[s] = sLineOffsets[s];
                        int n = s;
                        sLineOffsets[n] = sLineOffsets[n] + srcLineStride[s];
                    }
                    if (weightTypes[s] != 1) continue;
                    aPixelOffsets[s] = aLineOffsets[s];
                    int n = s;
                    aLineOffsets[n] = aLineOffsets[n] + alfaLineStride[s];
                }
                dPixelOffset = dLineOffset;
                dLineOffset += dstLineStride;
                for (dstX = dstMinX; dstX < dstMaxX; ++dstX) {
                    float numerator = 0.0f;
                    float denominator = 0.0f;
                    for (int s4 = 0; s4 < numSources; ++s4) {
                        if (src[s4] == null) continue;
                        short sourceValue = sBandData[s4][sPixelOffsets[s4]];
                        int n = s4;
                        sPixelOffsets[n] = sPixelOffsets[n] + srcPixelStride[s4];
                        float weight = 0.0f;
                        switch (weightTypes[s4]) {
                            case 1: {
                                weight = aBandData[s4][aPixelOffsets[s4]];
                                weight = weight > 0.0f && this.isAlphaBitmask ? 1.0f : (weight /= 32767.0f);
                                int n5 = s4;
                                aPixelOffsets[n5] = aPixelOffsets[n5] + alfaPixelStride[s4];
                                break;
                            }
                            case 2: {
                                weight = roi[s4].getSample(dstX, dstY, 0) > 0 ? 1.0f : 0.0f;
                                break;
                            }
                            default: {
                                weight = (double)sourceValue >= this.sourceThreshold[s4][b] ? 1.0f : 0.0f;
                            }
                        }
                        numerator += weight * (float)sourceValue;
                        denominator += weight;
                    }
                    dBandData[dPixelOffset] = (double)denominator == 0.0 ? (short)this.background[b] : ImageUtil.clampRoundShort(numerator / denominator);
                    dPixelOffset += dstPixelStride;
                }
            }
        }
    }

    private void computeRectInt(RasterAccessor[] src, RasterAccessor dst, RasterAccessor[] alfa, Raster[] roi) {
        int numSources = src.length;
        int[] srcLineStride = new int[numSources];
        int[] srcPixelStride = new int[numSources];
        int[][] srcBandOffsets = new int[numSources][];
        int[][][] srcData = new int[numSources][][];
        for (int i = 0; i < numSources; ++i) {
            if (src[i] == null) continue;
            srcLineStride[i] = src[i].getScanlineStride();
            srcPixelStride[i] = src[i].getPixelStride();
            srcBandOffsets[i] = src[i].getBandOffsets();
            srcData[i] = src[i].getIntDataArrays();
        }
        int dstMinX = dst.getX();
        int dstMinY = dst.getY();
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstMaxX = dstMinX + dstWidth;
        int dstMaxY = dstMinY + dstHeight;
        int dstBands = dst.getNumBands();
        int dstLineStride = dst.getScanlineStride();
        int dstPixelStride = dst.getPixelStride();
        int[] dstBandOffsets = dst.getBandOffsets();
        int[][] dstData = dst.getIntDataArrays();
        boolean hasAlpha = false;
        for (int i = 0; i < numSources; ++i) {
            if (alfa[i] == null) continue;
            hasAlpha = true;
            break;
        }
        int[] alfaLineStride = null;
        int[] alfaPixelStride = null;
        Object alfaBandOffsets = null;
        Object alfaData = null;
        if (hasAlpha) {
            alfaLineStride = new int[numSources];
            alfaPixelStride = new int[numSources];
            alfaBandOffsets = new int[numSources][];
            alfaData = new int[numSources][][];
            for (int i = 0; i < numSources; ++i) {
                if (alfa[i] == null) continue;
                alfaLineStride[i] = alfa[i].getScanlineStride();
                alfaPixelStride[i] = alfa[i].getPixelStride();
                alfaBandOffsets[i] = alfa[i].getBandOffsets();
                alfaData[i] = alfa[i].getIntDataArrays();
            }
        }
        int[] weightTypes = new int[numSources];
        for (int i = 0; i < numSources; ++i) {
            weightTypes[i] = 3;
            if (alfa[i] != null) {
                weightTypes[i] = 1;
                continue;
            }
            if (this.sourceROI == null || this.sourceROI[i] == null) continue;
            weightTypes[i] = 2;
        }
        int[] sLineOffsets = new int[numSources];
        int[] sPixelOffsets = new int[numSources];
        int[][] sBandData = new int[numSources][];
        int[] aLineOffsets = null;
        int[] aPixelOffsets = null;
        Object aBandData = null;
        if (hasAlpha) {
            aLineOffsets = new int[numSources];
            aPixelOffsets = new int[numSources];
            aBandData = new int[numSources][];
        }
        for (int b = 0; b < dstBands; ++b) {
            int dstX;
            int dPixelOffset;
            int s;
            int dstY;
            for (int s2 = 0; s2 < numSources; ++s2) {
                if (src[s2] != null) {
                    sBandData[s2] = srcData[s2][b];
                    sLineOffsets[s2] = srcBandOffsets[s2][b];
                }
                if (weightTypes[s2] != 1) continue;
                aBandData[s2] = alfaData[s2][0];
                aLineOffsets[s2] = alfaBandOffsets[s2][0];
            }
            int[] dBandData = dstData[b];
            int dLineOffset = dstBandOffsets[b];
            if (this.mosaicType == MosaicDescriptor.MOSAIC_TYPE_OVERLAY) {
                for (dstY = dstMinY; dstY < dstMaxY; ++dstY) {
                    for (s = 0; s < numSources; ++s) {
                        if (src[s] != null) {
                            sPixelOffsets[s] = sLineOffsets[s];
                            int n = s;
                            sLineOffsets[n] = sLineOffsets[n] + srcLineStride[s];
                        }
                        if (alfa[s] == null) continue;
                        aPixelOffsets[s] = aLineOffsets[s];
                        int n = s;
                        aLineOffsets[n] = aLineOffsets[n] + alfaLineStride[s];
                    }
                    dPixelOffset = dLineOffset;
                    dLineOffset += dstLineStride;
                    for (dstX = dstMinX; dstX < dstMaxX; ++dstX) {
                        boolean setDestValue = false;
                        for (int s3 = 0; s3 < numSources; ++s3) {
                            if (src[s3] == null) continue;
                            int sourceValue = sBandData[s3][sPixelOffsets[s3]];
                            int n = s3;
                            sPixelOffsets[n] = sPixelOffsets[n] + srcPixelStride[s3];
                            switch (weightTypes[s3]) {
                                case 1: {
                                    setDestValue = aBandData[s3][aPixelOffsets[s3]] != 0;
                                    int n2 = s3;
                                    aPixelOffsets[n2] = aPixelOffsets[n2] + alfaPixelStride[s3];
                                    break;
                                }
                                case 2: {
                                    setDestValue = roi[s3].getSample(dstX, dstY, 0) > 0;
                                    break;
                                }
                                default: {
                                    boolean bl = setDestValue = (double)sourceValue >= this.sourceThreshold[s3][b];
                                }
                            }
                            if (!setDestValue) continue;
                            dBandData[dPixelOffset] = sourceValue;
                            for (int k = s3 + 1; k < numSources; ++k) {
                                if (src[k] != null) {
                                    int n3 = k;
                                    sPixelOffsets[n3] = sPixelOffsets[n3] + srcPixelStride[k];
                                }
                                if (alfa[k] == null) continue;
                                int n4 = k;
                                aPixelOffsets[n4] = aPixelOffsets[n4] + alfaPixelStride[k];
                            }
                            break;
                        }
                        if (!setDestValue) {
                            dBandData[dPixelOffset] = this.background[b];
                        }
                        dPixelOffset += dstPixelStride;
                    }
                }
                continue;
            }
            for (dstY = dstMinY; dstY < dstMaxY; ++dstY) {
                for (s = 0; s < numSources; ++s) {
                    if (src[s] != null) {
                        sPixelOffsets[s] = sLineOffsets[s];
                        int n = s;
                        sLineOffsets[n] = sLineOffsets[n] + srcLineStride[s];
                    }
                    if (weightTypes[s] != 1) continue;
                    aPixelOffsets[s] = aLineOffsets[s];
                    int n = s;
                    aLineOffsets[n] = aLineOffsets[n] + alfaLineStride[s];
                }
                dPixelOffset = dLineOffset;
                dLineOffset += dstLineStride;
                for (dstX = dstMinX; dstX < dstMaxX; ++dstX) {
                    double numerator = 0.0;
                    double denominator = 0.0;
                    for (int s4 = 0; s4 < numSources; ++s4) {
                        if (src[s4] == null) continue;
                        int sourceValue = sBandData[s4][sPixelOffsets[s4]];
                        int n = s4;
                        sPixelOffsets[n] = sPixelOffsets[n] + srcPixelStride[s4];
                        double weight = 0.0;
                        switch (weightTypes[s4]) {
                            case 1: {
                                weight = aBandData[s4][aPixelOffsets[s4]];
                                weight = weight > 0.0 && this.isAlphaBitmask ? 1.0 : (weight /= 2.147483647E9);
                                int n5 = s4;
                                aPixelOffsets[n5] = aPixelOffsets[n5] + alfaPixelStride[s4];
                                break;
                            }
                            case 2: {
                                weight = roi[s4].getSample(dstX, dstY, 0) > 0 ? 1.0 : 0.0;
                                break;
                            }
                            default: {
                                weight = (double)sourceValue >= this.sourceThreshold[s4][b] ? 1.0 : 0.0;
                            }
                        }
                        numerator += weight * (double)sourceValue;
                        denominator += weight;
                    }
                    dBandData[dPixelOffset] = denominator == 0.0 ? this.background[b] : ImageUtil.clampRoundInt(numerator / denominator);
                    dPixelOffset += dstPixelStride;
                }
            }
        }
    }

    private void computeRectFloat(RasterAccessor[] src, RasterAccessor dst, RasterAccessor[] alfa, Raster[] roi) {
        int numSources = src.length;
        int[] srcLineStride = new int[numSources];
        int[] srcPixelStride = new int[numSources];
        int[][] srcBandOffsets = new int[numSources][];
        float[][][] srcData = new float[numSources][][];
        for (int i = 0; i < numSources; ++i) {
            if (src[i] == null) continue;
            srcLineStride[i] = src[i].getScanlineStride();
            srcPixelStride[i] = src[i].getPixelStride();
            srcBandOffsets[i] = src[i].getBandOffsets();
            srcData[i] = src[i].getFloatDataArrays();
        }
        int dstMinX = dst.getX();
        int dstMinY = dst.getY();
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstMaxX = dstMinX + dstWidth;
        int dstMaxY = dstMinY + dstHeight;
        int dstBands = dst.getNumBands();
        int dstLineStride = dst.getScanlineStride();
        int dstPixelStride = dst.getPixelStride();
        int[] dstBandOffsets = dst.getBandOffsets();
        float[][] dstData = dst.getFloatDataArrays();
        boolean hasAlpha = false;
        for (int i = 0; i < numSources; ++i) {
            if (alfa[i] == null) continue;
            hasAlpha = true;
            break;
        }
        int[] alfaLineStride = null;
        int[] alfaPixelStride = null;
        Object alfaBandOffsets = null;
        Object alfaData = null;
        if (hasAlpha) {
            alfaLineStride = new int[numSources];
            alfaPixelStride = new int[numSources];
            alfaBandOffsets = new int[numSources][];
            alfaData = new float[numSources][][];
            for (int i = 0; i < numSources; ++i) {
                if (alfa[i] == null) continue;
                alfaLineStride[i] = alfa[i].getScanlineStride();
                alfaPixelStride[i] = alfa[i].getPixelStride();
                alfaBandOffsets[i] = alfa[i].getBandOffsets();
                alfaData[i] = alfa[i].getFloatDataArrays();
            }
        }
        int[] weightTypes = new int[numSources];
        for (int i = 0; i < numSources; ++i) {
            weightTypes[i] = 3;
            if (alfa[i] != null) {
                weightTypes[i] = 1;
                continue;
            }
            if (this.sourceROI == null || this.sourceROI[i] == null) continue;
            weightTypes[i] = 2;
        }
        int[] sLineOffsets = new int[numSources];
        int[] sPixelOffsets = new int[numSources];
        float[][] sBandData = new float[numSources][];
        int[] aLineOffsets = null;
        int[] aPixelOffsets = null;
        Object aBandData = null;
        if (hasAlpha) {
            aLineOffsets = new int[numSources];
            aPixelOffsets = new int[numSources];
            aBandData = new float[numSources][];
        }
        for (int b = 0; b < dstBands; ++b) {
            int dstX;
            int dPixelOffset;
            int s;
            int dstY;
            for (int s2 = 0; s2 < numSources; ++s2) {
                if (src[s2] != null) {
                    sBandData[s2] = srcData[s2][b];
                    sLineOffsets[s2] = srcBandOffsets[s2][b];
                }
                if (weightTypes[s2] != 1) continue;
                aBandData[s2] = alfaData[s2][0];
                aLineOffsets[s2] = alfaBandOffsets[s2][0];
            }
            float[] dBandData = dstData[b];
            int dLineOffset = dstBandOffsets[b];
            if (this.mosaicType == MosaicDescriptor.MOSAIC_TYPE_OVERLAY) {
                for (dstY = dstMinY; dstY < dstMaxY; ++dstY) {
                    for (s = 0; s < numSources; ++s) {
                        if (src[s] != null) {
                            sPixelOffsets[s] = sLineOffsets[s];
                            int n = s;
                            sLineOffsets[n] = sLineOffsets[n] + srcLineStride[s];
                        }
                        if (alfa[s] == null) continue;
                        aPixelOffsets[s] = aLineOffsets[s];
                        int n = s;
                        aLineOffsets[n] = aLineOffsets[n] + alfaLineStride[s];
                    }
                    dPixelOffset = dLineOffset;
                    dLineOffset += dstLineStride;
                    for (dstX = dstMinX; dstX < dstMaxX; ++dstX) {
                        boolean setDestValue = false;
                        for (int s3 = 0; s3 < numSources; ++s3) {
                            if (src[s3] == null) continue;
                            float sourceValue = sBandData[s3][sPixelOffsets[s3]];
                            int n = s3;
                            sPixelOffsets[n] = sPixelOffsets[n] + srcPixelStride[s3];
                            switch (weightTypes[s3]) {
                                case 1: {
                                    setDestValue = aBandData[s3][aPixelOffsets[s3]] != 0.0f;
                                    int n2 = s3;
                                    aPixelOffsets[n2] = aPixelOffsets[n2] + alfaPixelStride[s3];
                                    break;
                                }
                                case 2: {
                                    setDestValue = roi[s3].getSample(dstX, dstY, 0) > 0;
                                    break;
                                }
                                default: {
                                    boolean bl = setDestValue = (double)sourceValue >= this.sourceThreshold[s3][b];
                                }
                            }
                            if (!setDestValue) continue;
                            dBandData[dPixelOffset] = sourceValue;
                            for (int k = s3 + 1; k < numSources; ++k) {
                                if (src[k] != null) {
                                    int n3 = k;
                                    sPixelOffsets[n3] = sPixelOffsets[n3] + srcPixelStride[k];
                                }
                                if (alfa[k] == null) continue;
                                int n4 = k;
                                aPixelOffsets[n4] = aPixelOffsets[n4] + alfaPixelStride[k];
                            }
                            break;
                        }
                        if (!setDestValue) {
                            dBandData[dPixelOffset] = (float)this.backgroundValues[b];
                        }
                        dPixelOffset += dstPixelStride;
                    }
                }
                continue;
            }
            for (dstY = dstMinY; dstY < dstMaxY; ++dstY) {
                for (s = 0; s < numSources; ++s) {
                    if (src[s] != null) {
                        sPixelOffsets[s] = sLineOffsets[s];
                        int n = s;
                        sLineOffsets[n] = sLineOffsets[n] + srcLineStride[s];
                    }
                    if (weightTypes[s] != 1) continue;
                    aPixelOffsets[s] = aLineOffsets[s];
                    int n = s;
                    aLineOffsets[n] = aLineOffsets[n] + alfaLineStride[s];
                }
                dPixelOffset = dLineOffset;
                dLineOffset += dstLineStride;
                for (dstX = dstMinX; dstX < dstMaxX; ++dstX) {
                    float numerator = 0.0f;
                    float denominator = 0.0f;
                    for (int s4 = 0; s4 < numSources; ++s4) {
                        if (src[s4] == null) continue;
                        float sourceValue = sBandData[s4][sPixelOffsets[s4]];
                        int n = s4;
                        sPixelOffsets[n] = sPixelOffsets[n] + srcPixelStride[s4];
                        float weight = 0.0f;
                        switch (weightTypes[s4]) {
                            case 1: {
                                weight = aBandData[s4][aPixelOffsets[s4]];
                                if (weight > 0.0f && this.isAlphaBitmask) {
                                    weight = 1.0f;
                                }
                                int n5 = s4;
                                aPixelOffsets[n5] = aPixelOffsets[n5] + alfaPixelStride[s4];
                                break;
                            }
                            case 2: {
                                weight = roi[s4].getSample(dstX, dstY, 0) > 0 ? 1.0f : 0.0f;
                                break;
                            }
                            default: {
                                weight = (double)sourceValue >= this.sourceThreshold[s4][b] ? 1.0f : 0.0f;
                            }
                        }
                        numerator += weight * sourceValue;
                        denominator += weight;
                    }
                    dBandData[dPixelOffset] = (double)denominator == 0.0 ? (float)this.backgroundValues[b] : numerator / denominator;
                    dPixelOffset += dstPixelStride;
                }
            }
        }
    }

    private void computeRectDouble(RasterAccessor[] src, RasterAccessor dst, RasterAccessor[] alfa, Raster[] roi) {
        int numSources = src.length;
        int[] srcLineStride = new int[numSources];
        int[] srcPixelStride = new int[numSources];
        int[][] srcBandOffsets = new int[numSources][];
        double[][][] srcData = new double[numSources][][];
        for (int i = 0; i < numSources; ++i) {
            if (src[i] == null) continue;
            srcLineStride[i] = src[i].getScanlineStride();
            srcPixelStride[i] = src[i].getPixelStride();
            srcBandOffsets[i] = src[i].getBandOffsets();
            srcData[i] = src[i].getDoubleDataArrays();
        }
        int dstMinX = dst.getX();
        int dstMinY = dst.getY();
        int dstWidth = dst.getWidth();
        int dstHeight = dst.getHeight();
        int dstMaxX = dstMinX + dstWidth;
        int dstMaxY = dstMinY + dstHeight;
        int dstBands = dst.getNumBands();
        int dstLineStride = dst.getScanlineStride();
        int dstPixelStride = dst.getPixelStride();
        int[] dstBandOffsets = dst.getBandOffsets();
        double[][] dstData = dst.getDoubleDataArrays();
        boolean hasAlpha = false;
        for (int i = 0; i < numSources; ++i) {
            if (alfa[i] == null) continue;
            hasAlpha = true;
            break;
        }
        int[] alfaLineStride = null;
        int[] alfaPixelStride = null;
        Object alfaBandOffsets = null;
        Object alfaData = null;
        if (hasAlpha) {
            alfaLineStride = new int[numSources];
            alfaPixelStride = new int[numSources];
            alfaBandOffsets = new int[numSources][];
            alfaData = new double[numSources][][];
            for (int i = 0; i < numSources; ++i) {
                if (alfa[i] == null) continue;
                alfaLineStride[i] = alfa[i].getScanlineStride();
                alfaPixelStride[i] = alfa[i].getPixelStride();
                alfaBandOffsets[i] = alfa[i].getBandOffsets();
                alfaData[i] = alfa[i].getDoubleDataArrays();
            }
        }
        int[] weightTypes = new int[numSources];
        for (int i = 0; i < numSources; ++i) {
            weightTypes[i] = 3;
            if (alfa[i] != null) {
                weightTypes[i] = 1;
                continue;
            }
            if (this.sourceROI == null || this.sourceROI[i] == null) continue;
            weightTypes[i] = 2;
        }
        int[] sLineOffsets = new int[numSources];
        int[] sPixelOffsets = new int[numSources];
        double[][] sBandData = new double[numSources][];
        int[] aLineOffsets = null;
        int[] aPixelOffsets = null;
        Object aBandData = null;
        if (hasAlpha) {
            aLineOffsets = new int[numSources];
            aPixelOffsets = new int[numSources];
            aBandData = new double[numSources][];
        }
        for (int b = 0; b < dstBands; ++b) {
            int dstX;
            int dPixelOffset;
            int s;
            int dstY;
            for (int s2 = 0; s2 < numSources; ++s2) {
                if (src[s2] != null) {
                    sBandData[s2] = srcData[s2][b];
                    sLineOffsets[s2] = srcBandOffsets[s2][b];
                }
                if (weightTypes[s2] != 1) continue;
                aBandData[s2] = alfaData[s2][0];
                aLineOffsets[s2] = alfaBandOffsets[s2][0];
            }
            double[] dBandData = dstData[b];
            int dLineOffset = dstBandOffsets[b];
            if (this.mosaicType == MosaicDescriptor.MOSAIC_TYPE_OVERLAY) {
                for (dstY = dstMinY; dstY < dstMaxY; ++dstY) {
                    for (s = 0; s < numSources; ++s) {
                        if (src[s] != null) {
                            sPixelOffsets[s] = sLineOffsets[s];
                            int n = s;
                            sLineOffsets[n] = sLineOffsets[n] + srcLineStride[s];
                        }
                        if (alfa[s] == null) continue;
                        aPixelOffsets[s] = aLineOffsets[s];
                        int n = s;
                        aLineOffsets[n] = aLineOffsets[n] + alfaLineStride[s];
                    }
                    dPixelOffset = dLineOffset;
                    dLineOffset += dstLineStride;
                    for (dstX = dstMinX; dstX < dstMaxX; ++dstX) {
                        boolean setDestValue = false;
                        for (int s3 = 0; s3 < numSources; ++s3) {
                            if (src[s3] == null) continue;
                            double sourceValue = sBandData[s3][sPixelOffsets[s3]];
                            int n = s3;
                            sPixelOffsets[n] = sPixelOffsets[n] + srcPixelStride[s3];
                            switch (weightTypes[s3]) {
                                case 1: {
                                    setDestValue = aBandData[s3][aPixelOffsets[s3]] != 0.0;
                                    int n2 = s3;
                                    aPixelOffsets[n2] = aPixelOffsets[n2] + alfaPixelStride[s3];
                                    break;
                                }
                                case 2: {
                                    setDestValue = roi[s3].getSample(dstX, dstY, 0) > 0;
                                    break;
                                }
                                default: {
                                    boolean bl = setDestValue = sourceValue >= this.sourceThreshold[s3][b];
                                }
                            }
                            if (!setDestValue) continue;
                            dBandData[dPixelOffset] = sourceValue;
                            for (int k = s3 + 1; k < numSources; ++k) {
                                if (src[k] != null) {
                                    int n3 = k;
                                    sPixelOffsets[n3] = sPixelOffsets[n3] + srcPixelStride[k];
                                }
                                if (alfa[k] == null) continue;
                                int n4 = k;
                                aPixelOffsets[n4] = aPixelOffsets[n4] + alfaPixelStride[k];
                            }
                            break;
                        }
                        if (!setDestValue) {
                            dBandData[dPixelOffset] = this.backgroundValues[b];
                        }
                        dPixelOffset += dstPixelStride;
                    }
                }
                continue;
            }
            for (dstY = dstMinY; dstY < dstMaxY; ++dstY) {
                for (s = 0; s < numSources; ++s) {
                    if (src[s] != null) {
                        sPixelOffsets[s] = sLineOffsets[s];
                        int n = s;
                        sLineOffsets[n] = sLineOffsets[n] + srcLineStride[s];
                    }
                    if (weightTypes[s] != 1) continue;
                    aPixelOffsets[s] = aLineOffsets[s];
                    int n = s;
                    aLineOffsets[n] = aLineOffsets[n] + alfaLineStride[s];
                }
                dPixelOffset = dLineOffset;
                dLineOffset += dstLineStride;
                for (dstX = dstMinX; dstX < dstMaxX; ++dstX) {
                    double numerator = 0.0;
                    double denominator = 0.0;
                    for (int s4 = 0; s4 < numSources; ++s4) {
                        if (src[s4] == null) continue;
                        double sourceValue = sBandData[s4][sPixelOffsets[s4]];
                        int n = s4;
                        sPixelOffsets[n] = sPixelOffsets[n] + srcPixelStride[s4];
                        double weight = 0.0;
                        switch (weightTypes[s4]) {
                            case 1: {
                                weight = aBandData[s4][aPixelOffsets[s4]];
                                if (weight > 0.0 && this.isAlphaBitmask) {
                                    weight = 1.0;
                                }
                                int n5 = s4;
                                aPixelOffsets[n5] = aPixelOffsets[n5] + alfaPixelStride[s4];
                                break;
                            }
                            case 2: {
                                weight = roi[s4].getSample(dstX, dstY, 0) > 0 ? 1.0 : 0.0;
                                break;
                            }
                            default: {
                                weight = sourceValue >= this.sourceThreshold[s4][b] ? 1.0 : 0.0;
                            }
                        }
                        numerator += weight * sourceValue;
                        denominator += weight;
                    }
                    dBandData[dPixelOffset] = denominator == 0.0 ? this.backgroundValues[b] : numerator / denominator;
                    dPixelOffset += dstPixelStride;
                }
            }
        }
    }
}

