/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.JaiI18N;
import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.JDKWorkarounds;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.Map;
import java.util.Vector;
import javax.media.jai.ColorCube;
import javax.media.jai.ImageLayout;
import javax.media.jai.KernelJAI;
import javax.media.jai.PointOpImage;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFactory;
import javax.media.jai.RasterFormatTag;

final class OrderedDitherOpImage
extends PointOpImage {
    private static final int TYPE_OD_GENERAL = 0;
    private static final int TYPE_OD_BYTE_LUT_3BAND = 1;
    private static final int TYPE_OD_BYTE_LUT_NBAND = 2;
    private static final int DITHER_LUT_LENGTH_MAX = 262144;
    private static final int DITHER_LUT_CACHE_LENGTH_MAX = 4;
    private static Vector ditherLUTCache = new Vector(0, 4);
    private int odType = 0;
    protected int numBands;
    protected int[] dims;
    protected int[] mults;
    protected int adjustedOffset;
    protected int maskWidth;
    protected int maskHeight;
    protected byte[][] maskDataByte;
    protected int[][] maskDataInt;
    protected long[][] maskDataLong;
    protected float[][] maskDataFloat;
    protected DitherLUT odLUT = null;

    private static ImageLayout layoutHelper(ImageLayout layout, RenderedImage source, ColorCube colorMap) {
        ColorModel cm;
        ImageLayout il = layout == null ? new ImageLayout(source) : (ImageLayout)layout.clone();
        SampleModel sm = il.getSampleModel(source);
        if (colorMap.getNumBands() == 1 && colorMap.getNumEntries() == 2 && !ImageUtil.isBinary(il.getSampleModel(source))) {
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
        if ((layout == null || !il.isValid(512)) && source.getSampleModel().getDataType() == 0 && il.getSampleModel(null).getDataType() == 0 && colorMap.getDataType() == 0 && colorMap.getNumBands() == 3 && ((cm = source.getColorModel()) == null || cm != null && cm.getColorSpace().isCS_sRGB())) {
            int size = colorMap.getNumEntries();
            byte[][] cmap = new byte[3][256];
            for (int i = 0; i < 3; ++i) {
                int j;
                byte[] band = cmap[i];
                byte[] data = colorMap.getByteData(i);
                int offset = colorMap.getOffset(i);
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

    public OrderedDitherOpImage(RenderedImage source, Map config, ImageLayout layout, ColorCube colorMap, KernelJAI[] ditherMask) {
        super(source, OrderedDitherOpImage.layoutHelper(layout, source, colorMap), config, true);
        this.numBands = colorMap.getNumBands();
        this.mults = (int[])colorMap.getMultipliers().clone();
        this.dims = (int[])colorMap.getDimsLessOne().clone();
        this.adjustedOffset = colorMap.getAdjustedOffset();
        this.maskWidth = ditherMask[0].getWidth();
        this.maskHeight = ditherMask[0].getHeight();
        this.initializeDitherData(this.sampleModel.getTransferType(), ditherMask);
        this.permitInPlaceOperation();
    }

    private void initializeDitherData(int dataType, KernelJAI[] ditherMask) {
        switch (dataType) {
            case 0: {
                this.maskDataByte = new byte[ditherMask.length][];
                for (int i = 0; i < this.maskDataByte.length; ++i) {
                    float[] maskData = ditherMask[i].getKernelData();
                    this.maskDataByte[i] = new byte[maskData.length];
                    for (int j = 0; j < maskData.length; ++j) {
                        this.maskDataByte[i][j] = (byte)((int)(maskData[j] * 255.0f) & 0xFF);
                    }
                }
                this.initializeDitherLUT();
                break;
            }
            case 1: 
            case 2: {
                int scaleFactor = 65535;
                this.maskDataInt = new int[ditherMask.length][];
                for (int i = 0; i < this.maskDataInt.length; ++i) {
                    float[] maskData = ditherMask[i].getKernelData();
                    this.maskDataInt[i] = new int[maskData.length];
                    for (int j = 0; j < maskData.length; ++j) {
                        this.maskDataInt[i][j] = (int)(maskData[j] * (float)scaleFactor);
                    }
                }
                break;
            }
            case 3: {
                long scaleFactor = 0xFFFFFFFFL;
                this.maskDataLong = new long[ditherMask.length][];
                for (int i = 0; i < this.maskDataLong.length; ++i) {
                    float[] maskData = ditherMask[i].getKernelData();
                    this.maskDataLong[i] = new long[maskData.length];
                    for (int j = 0; j < maskData.length; ++j) {
                        this.maskDataLong[i][j] = (long)(maskData[j] * (float)scaleFactor);
                    }
                }
                break;
            }
            case 4: 
            case 5: {
                this.maskDataFloat = new float[ditherMask.length][];
                for (int i = 0; i < this.maskDataFloat.length; ++i) {
                    this.maskDataFloat[i] = ditherMask[i].getKernelData();
                }
                break;
            }
            default: {
                throw new RuntimeException(JaiI18N.getString("OrderedDitherOpImage0"));
            }
        }
    }

    private synchronized void initializeDitherLUT() {
        if (this.numBands * this.maskHeight * this.maskWidth * 256 > 262144) {
            this.odType = 0;
            return;
        }
        this.odType = this.numBands == 3 ? 1 : 2;
        int index = 0;
        while (index < ditherLUTCache.size()) {
            SoftReference lutRef = (SoftReference)ditherLUTCache.get(index);
            DitherLUT lut = (DitherLUT)lutRef.get();
            if (lut == null) {
                ditherLUTCache.remove(index);
                continue;
            }
            if (lut.equals(this.dims, this.mults, this.maskDataByte)) {
                this.odLUT = lut;
                break;
            }
            ++index;
        }
        if (this.odLUT == null) {
            this.odLUT = new DitherLUT(this.dims, this.mults, this.maskDataByte);
            if (ditherLUTCache.size() < 4) {
                ditherLUTCache.add(new SoftReference<DitherLUT>(this.odLUT));
            }
        }
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        RasterFormatTag[] formatTags = null;
        if (ImageUtil.isBinary(this.getSampleModel()) && !ImageUtil.isBinary(this.getSourceImage(0).getSampleModel())) {
            RenderedImage[] sourceArray = new RenderedImage[]{this.getSourceImage(0)};
            RasterFormatTag[] sourceTags = RasterAccessor.findCompatibleTags(sourceArray, sourceArray[0]);
            RasterFormatTag[] destTags = RasterAccessor.findCompatibleTags(sourceArray, this);
            formatTags = new RasterFormatTag[]{sourceTags[0], destTags[1]};
        } else {
            formatTags = this.getFormatTags();
        }
        RasterAccessor src = new RasterAccessor(sources[0], destRect, formatTags[0], this.getSource(0).getColorModel());
        RasterAccessor dst = new RasterAccessor(dest, destRect, formatTags[1], this.getColorModel());
        switch (src.getDataType()) {
            case 0: {
                this.computeRectByte(src, dst);
                break;
            }
            case 2: {
                this.computeRectShort(src, dst);
                break;
            }
            case 1: {
                this.computeRectUShort(src, dst);
                break;
            }
            case 3: {
                this.computeRectInt(src, dst);
                break;
            }
            case 4: {
                this.computeRectFloat(src, dst);
                break;
            }
            case 5: {
                this.computeRectDouble(src, dst);
                break;
            }
            default: {
                throw new RuntimeException(JaiI18N.getString("OrderedDitherOpImage1"));
            }
        }
        dst.copyDataToRaster();
    }

    private void computeRectByte(RasterAccessor src, RasterAccessor dst) {
        int sbands = src.getNumBands();
        int sLineStride = src.getScanlineStride();
        int sPixelStride = src.getPixelStride();
        int[] sBandOffsets = src.getBandOffsets();
        byte[][] sData = src.getByteDataArrays();
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dLineStride = dst.getScanlineStride();
        int dPixelStride = dst.getPixelStride();
        int dBandOffset = dst.getBandOffset(0);
        byte[] dData = dst.getByteDataArray(0);
        int xMod = dst.getX() % this.maskWidth;
        int y0 = dst.getY();
        switch (this.odType) {
            case 1: 
            case 2: {
                int[] srcLineOffsets = (int[])sBandOffsets.clone();
                int[] srcPixelOffsets = (int[])srcLineOffsets.clone();
                int dLineOffset = dBandOffset;
                for (int h = 0; h < dheight; ++h) {
                    int yMod = (y0 + h) % this.maskHeight;
                    if (this.odType == 1) {
                        this.computeLineByteLUT3(sData, srcPixelOffsets, sPixelStride, dData, dLineOffset, dPixelStride, dwidth, xMod, yMod);
                    } else {
                        this.computeLineByteLUTN(sData, srcPixelOffsets, sPixelStride, dData, dLineOffset, dPixelStride, dwidth, xMod, yMod);
                    }
                    for (int i = 0; i < sbands; ++i) {
                        int n = i;
                        srcLineOffsets[n] = srcLineOffsets[n] + sLineStride;
                        srcPixelOffsets[i] = srcLineOffsets[i];
                    }
                    dLineOffset += dLineStride;
                }
                break;
            }
            default: {
                this.computeRectByteGeneral(sData, sBandOffsets, sLineStride, sPixelStride, dData, dBandOffset, dLineStride, dPixelStride, dwidth, dheight, xMod, y0);
            }
        }
    }

    private void computeLineByteLUT3(byte[][] sData, int[] sPixelOffsets, int sPixelStride, byte[] dData, int dPixelOffset, int dPixelStride, int dwidth, int xMod, int yMod) {
        int ditherLUTBandStride = this.odLUT.ditherLUTBandStride;
        int ditherLUTRowStride = this.odLUT.ditherLUTRowStride;
        int ditherLUTColStride = this.odLUT.ditherLUTColStride;
        byte[] ditherLUT = this.odLUT.ditherLUT;
        int base = this.adjustedOffset;
        int dlut0 = yMod * ditherLUTRowStride;
        int dlut1 = dlut0 + ditherLUTBandStride;
        int dlut2 = dlut1 + ditherLUTBandStride;
        int dlutLimit = dlut0 + ditherLUTRowStride;
        int xDelta = xMod * ditherLUTColStride;
        int pDtab0 = dlut0 + xDelta;
        int pDtab1 = dlut1 + xDelta;
        int pDtab2 = dlut2 + xDelta;
        byte[] sData0 = sData[0];
        byte[] sData1 = sData[1];
        byte[] sData2 = sData[2];
        for (int count = dwidth; count > 0; --count) {
            int idx = (ditherLUT[pDtab0 + (sData0[sPixelOffsets[0]] & 0xFF)] & 0xFF) + (ditherLUT[pDtab1 + (sData1[sPixelOffsets[1]] & 0xFF)] & 0xFF) + (ditherLUT[pDtab2 + (sData2[sPixelOffsets[2]] & 0xFF)] & 0xFF);
            dData[dPixelOffset] = (byte)(idx + base & 0xFF);
            sPixelOffsets[0] = sPixelOffsets[0] + sPixelStride;
            sPixelOffsets[1] = sPixelOffsets[1] + sPixelStride;
            sPixelOffsets[2] = sPixelOffsets[2] + sPixelStride;
            dPixelOffset += dPixelStride;
            if ((pDtab0 += ditherLUTColStride) >= dlutLimit) {
                pDtab0 = dlut0;
                pDtab1 = dlut1;
                pDtab2 = dlut2;
                continue;
            }
            pDtab1 += ditherLUTColStride;
            pDtab2 += ditherLUTColStride;
        }
    }

    private void computeLineByteLUTN(byte[][] sData, int[] sPixelOffsets, int sPixelStride, byte[] dData, int dPixelOffset, int dPixelStride, int dwidth, int xMod, int yMod) {
        int ditherLUTBandStride = this.odLUT.ditherLUTBandStride;
        int ditherLUTRowStride = this.odLUT.ditherLUTRowStride;
        int ditherLUTColStride = this.odLUT.ditherLUTColStride;
        byte[] ditherLUT = this.odLUT.ditherLUT;
        int base = this.adjustedOffset;
        int dlutRow = yMod * ditherLUTRowStride;
        int dlutCol = dlutRow + xMod * ditherLUTColStride;
        int dlutLimit = dlutRow + ditherLUTRowStride;
        for (int count = dwidth; count > 0; --count) {
            int dlutBand = dlutCol;
            int idx = base;
            int i = 0;
            while (i < this.numBands) {
                idx += ditherLUT[dlutBand + (sData[i][sPixelOffsets[i]] & 0xFF)] & 0xFF;
                dlutBand += ditherLUTBandStride;
                int n = i++;
                sPixelOffsets[n] = sPixelOffsets[n] + sPixelStride;
            }
            dData[dPixelOffset] = (byte)(idx & 0xFF);
            dPixelOffset += dPixelStride;
            if ((dlutCol += ditherLUTColStride) < dlutLimit) continue;
            dlutCol = dlutRow;
        }
    }

    private void computeRectByteGeneral(byte[][] sData, int[] sBandOffsets, int sLineStride, int sPixelStride, byte[] dData, int dBandOffset, int dLineStride, int dPixelStride, int dwidth, int dheight, int xMod, int y0) {
        if (this.adjustedOffset > 0) {
            Arrays.fill(dData, (byte)(this.adjustedOffset & 0xFF));
        }
        int sbands = sBandOffsets.length;
        for (int b = 0; b < sbands; ++b) {
            byte[] s = sData[b];
            byte[] d = dData;
            byte[] maskData = this.maskDataByte[b];
            int sLineOffset = sBandOffsets[b];
            int dLineOffset = dBandOffset;
            for (int h = 0; h < dheight; ++h) {
                int yMod = (y0 + h) % this.maskHeight;
                int maskYBase = yMod * this.maskWidth;
                int maskLimit = maskYBase + this.maskWidth;
                int maskIndex = maskYBase + xMod;
                int sPixelOffset = sLineOffset;
                int dPixelOffset = dLineOffset;
                for (int w = 0; w < dwidth; ++w) {
                    int tmp = (s[sPixelOffset] & 0xFF) * this.dims[b];
                    int frac = tmp & 0xFF;
                    tmp >>= 8;
                    if (frac > (maskData[maskIndex] & 0xFF)) {
                        ++tmp;
                    }
                    int result = (d[dPixelOffset] & 0xFF) + tmp * this.mults[b];
                    d[dPixelOffset] = (byte)(result & 0xFF);
                    sPixelOffset += sPixelStride;
                    dPixelOffset += dPixelStride;
                    if (++maskIndex < maskLimit) continue;
                    maskIndex = maskYBase;
                }
                sLineOffset += sLineStride;
                dLineOffset += dLineStride;
            }
        }
        if (this.adjustedOffset < 0) {
            int length = dData.length;
            for (int i = 0; i < length; ++i) {
                dData[i] = (byte)((dData[i] & 0xFF) + this.adjustedOffset);
            }
        }
    }

    private void computeRectShort(RasterAccessor src, RasterAccessor dst) {
        int sbands = src.getNumBands();
        int sLineStride = src.getScanlineStride();
        int sPixelStride = src.getPixelStride();
        int[] sBandOffsets = src.getBandOffsets();
        short[][] sData = src.getShortDataArrays();
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dLineStride = dst.getScanlineStride();
        int dPixelStride = dst.getPixelStride();
        int dBandOffset = dst.getBandOffset(0);
        short[] dData = dst.getShortDataArray(0);
        if (this.adjustedOffset != 0) {
            Arrays.fill(dData, (short)(this.adjustedOffset & 0xFFFF));
        }
        int xMod = dst.getX() % this.maskWidth;
        int y0 = dst.getY();
        for (int b = 0; b < sbands; ++b) {
            short[] s = sData[b];
            short[] d = dData;
            int[] maskData = this.maskDataInt[b];
            int sLineOffset = sBandOffsets[b];
            int dLineOffset = dBandOffset;
            for (int h = 0; h < dheight; ++h) {
                int sPixelOffset = sLineOffset;
                int dPixelOffset = dLineOffset;
                sLineOffset += sLineStride;
                dLineOffset += dLineStride;
                int maskYBase = (y0 + h) % this.maskHeight * this.maskWidth;
                int maskLimit = maskYBase + this.maskWidth;
                int maskIndex = maskYBase + xMod;
                for (int w = 0; w < dwidth; ++w) {
                    int tmp = (s[sPixelOffset] - Short.MIN_VALUE) * this.dims[b];
                    int frac = tmp & 0xFFFF;
                    int result = (d[dPixelOffset] & 0xFFFF) + (tmp >> 16) * this.mults[b];
                    if (frac > maskData[maskIndex]) {
                        result += this.mults[b];
                    }
                    d[dPixelOffset] = (short)(result & 0xFFFF);
                    sPixelOffset += sPixelStride;
                    dPixelOffset += dPixelStride;
                    if (++maskIndex < maskLimit) continue;
                    maskIndex = maskYBase;
                }
            }
        }
    }

    private void computeRectUShort(RasterAccessor src, RasterAccessor dst) {
        int sbands = src.getNumBands();
        int sLineStride = src.getScanlineStride();
        int sPixelStride = src.getPixelStride();
        int[] sBandOffsets = src.getBandOffsets();
        short[][] sData = src.getShortDataArrays();
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dLineStride = dst.getScanlineStride();
        int dPixelStride = dst.getPixelStride();
        int dBandOffset = dst.getBandOffset(0);
        short[] dData = dst.getShortDataArray(0);
        if (this.adjustedOffset != 0) {
            Arrays.fill(dData, (short)(this.adjustedOffset & 0xFFFF));
        }
        int xMod = dst.getX() % this.maskWidth;
        int y0 = dst.getY();
        for (int b = 0; b < sbands; ++b) {
            short[] s = sData[b];
            short[] d = dData;
            int[] maskData = this.maskDataInt[b];
            int sLineOffset = sBandOffsets[b];
            int dLineOffset = dBandOffset;
            for (int h = 0; h < dheight; ++h) {
                int sPixelOffset = sLineOffset;
                int dPixelOffset = dLineOffset;
                sLineOffset += sLineStride;
                dLineOffset += dLineStride;
                int maskYBase = (y0 + h) % this.maskHeight * this.maskWidth;
                int maskLimit = maskYBase + this.maskWidth;
                int maskIndex = maskYBase + xMod;
                for (int w = 0; w < dwidth; ++w) {
                    int tmp = (s[sPixelOffset] & 0xFFFF) * this.dims[b];
                    int frac = tmp & 0xFFFF;
                    int result = (d[dPixelOffset] & 0xFFFF) + (tmp >> 16) * this.mults[b];
                    if (frac > maskData[maskIndex]) {
                        result += this.mults[b];
                    }
                    d[dPixelOffset] = (short)(result & 0xFFFF);
                    sPixelOffset += sPixelStride;
                    dPixelOffset += dPixelStride;
                    if (++maskIndex < maskLimit) continue;
                    maskIndex = maskYBase;
                }
            }
        }
    }

    private void computeRectInt(RasterAccessor src, RasterAccessor dst) {
        int sbands = src.getNumBands();
        int sLineStride = src.getScanlineStride();
        int sPixelStride = src.getPixelStride();
        int[] sBandOffsets = src.getBandOffsets();
        int[][] sData = src.getIntDataArrays();
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dLineStride = dst.getScanlineStride();
        int dPixelStride = dst.getPixelStride();
        int dBandOffset = dst.getBandOffset(0);
        int[] dData = dst.getIntDataArray(0);
        if (this.adjustedOffset != 0) {
            Arrays.fill(dData, this.adjustedOffset);
        }
        int xMod = dst.getX() % this.maskWidth;
        int y0 = dst.getY();
        for (int b = 0; b < sbands; ++b) {
            int[] s = sData[b];
            int[] d = dData;
            long[] maskData = this.maskDataLong[b];
            int sLineOffset = sBandOffsets[b];
            int dLineOffset = dBandOffset;
            for (int h = 0; h < dheight; ++h) {
                int sPixelOffset = sLineOffset;
                int dPixelOffset = dLineOffset;
                sLineOffset += sLineStride;
                dLineOffset += dLineStride;
                int maskYBase = (y0 + h) % this.maskHeight * this.maskWidth;
                int maskLimit = maskYBase + this.maskWidth;
                int maskIndex = maskYBase + xMod;
                for (int w = 0; w < dwidth; ++w) {
                    long tmp = ((long)s[sPixelOffset] - Integer.MIN_VALUE) * (long)this.dims[b];
                    long frac = tmp & 0xFFFFFFFFFFFFFFFFL;
                    int result = d[dPixelOffset] + (int)(tmp >> 32) * this.mults[b];
                    if (frac > maskData[maskIndex]) {
                        result += this.mults[b];
                    }
                    d[dPixelOffset] = result;
                    sPixelOffset += sPixelStride;
                    dPixelOffset += dPixelStride;
                    if (++maskIndex < maskLimit) continue;
                    maskIndex = maskYBase;
                }
            }
        }
    }

    private void computeRectFloat(RasterAccessor src, RasterAccessor dst) {
        int sbands = src.getNumBands();
        int sLineStride = src.getScanlineStride();
        int sPixelStride = src.getPixelStride();
        int[] sBandOffsets = src.getBandOffsets();
        float[][] sData = src.getFloatDataArrays();
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dLineStride = dst.getScanlineStride();
        int dPixelStride = dst.getPixelStride();
        int dBandOffset = dst.getBandOffset(0);
        float[] dData = dst.getFloatDataArray(0);
        if (this.adjustedOffset != 0) {
            Arrays.fill(dData, (float)this.adjustedOffset);
        }
        int xMod = dst.getX() % this.maskWidth;
        int y0 = dst.getY();
        for (int b = 0; b < sbands; ++b) {
            float[] s = sData[b];
            float[] d = dData;
            float[] maskData = this.maskDataFloat[b];
            int sLineOffset = sBandOffsets[b];
            int dLineOffset = dBandOffset;
            for (int h = 0; h < dheight; ++h) {
                int sPixelOffset = sLineOffset;
                int dPixelOffset = dLineOffset;
                sLineOffset += sLineStride;
                dLineOffset += dLineStride;
                int maskYBase = (y0 + h) % this.maskHeight * this.maskWidth;
                int maskLimit = maskYBase + this.maskWidth;
                int maskIndex = maskYBase + xMod;
                for (int w = 0; w < dwidth; ++w) {
                    int tmp = (int)(s[sPixelOffset] * (float)this.dims[b]);
                    float frac = s[sPixelOffset] * (float)this.dims[b] - (float)tmp;
                    float result = d[dPixelOffset] + (float)(tmp * this.mults[b]);
                    if (frac > maskData[maskIndex]) {
                        result += (float)this.mults[b];
                    }
                    d[dPixelOffset] = result;
                    sPixelOffset += sPixelStride;
                    dPixelOffset += dPixelStride;
                    if (++maskIndex < maskLimit) continue;
                    maskIndex = maskYBase;
                }
            }
        }
    }

    private void computeRectDouble(RasterAccessor src, RasterAccessor dst) {
        int sbands = src.getNumBands();
        int sLineStride = src.getScanlineStride();
        int sPixelStride = src.getPixelStride();
        int[] sBandOffsets = src.getBandOffsets();
        double[][] sData = src.getDoubleDataArrays();
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dLineStride = dst.getScanlineStride();
        int dPixelStride = dst.getPixelStride();
        int dBandOffset = dst.getBandOffset(0);
        double[] dData = dst.getDoubleDataArray(0);
        if (this.adjustedOffset != 0) {
            Arrays.fill(dData, (double)this.adjustedOffset);
        }
        int xMod = dst.getX() % this.maskWidth;
        int y0 = dst.getY();
        for (int b = 0; b < sbands; ++b) {
            double[] s = sData[b];
            double[] d = dData;
            float[] maskData = this.maskDataFloat[b];
            int sLineOffset = sBandOffsets[b];
            int dLineOffset = dBandOffset;
            for (int h = 0; h < dheight; ++h) {
                int sPixelOffset = sLineOffset;
                int dPixelOffset = dLineOffset;
                sLineOffset += sLineStride;
                dLineOffset += dLineStride;
                int maskYBase = (y0 + h) % this.maskHeight * this.maskWidth;
                int maskLimit = maskYBase + this.maskWidth;
                int maskIndex = maskYBase + xMod;
                for (int w = 0; w < dwidth; ++w) {
                    int tmp = (int)(s[sPixelOffset] * (double)this.dims[b]);
                    float frac = (float)(s[sPixelOffset] * (double)this.dims[b] - (double)tmp);
                    double result = d[dPixelOffset] + (double)(tmp * this.mults[b]);
                    if (frac > maskData[maskIndex]) {
                        result += (double)this.mults[b];
                    }
                    d[dPixelOffset] = result;
                    sPixelOffset += sPixelStride;
                    dPixelOffset += dPixelStride;
                    if (++maskIndex < maskLimit) continue;
                    maskIndex = maskYBase;
                }
            }
        }
    }

    private class DitherLUT {
        private int[] dimsCache;
        private int[] multsCache;
        private byte[][] maskDataCache;
        public int ditherLUTBandStride;
        public int ditherLUTRowStride;
        public int ditherLUTColStride;
        public byte[] ditherLUT;

        DitherLUT(int[] dims, int[] mults, byte[][] maskData) {
            this.dimsCache = (int[])dims.clone();
            this.multsCache = (int[])mults.clone();
            this.maskDataCache = new byte[maskData.length][];
            for (int i = 0; i < maskData.length; ++i) {
                this.maskDataCache[i] = (byte[])maskData[i].clone();
            }
            this.ditherLUTColStride = 256;
            this.ditherLUTRowStride = OrderedDitherOpImage.this.maskWidth * this.ditherLUTColStride;
            this.ditherLUTBandStride = OrderedDitherOpImage.this.maskHeight * this.ditherLUTRowStride;
            this.ditherLUT = new byte[OrderedDitherOpImage.this.numBands * this.ditherLUTBandStride];
            int pDithBand = 0;
            int maskSize2D = OrderedDitherOpImage.this.maskWidth * OrderedDitherOpImage.this.maskHeight;
            for (int band = 0; band < OrderedDitherOpImage.this.numBands; ++band) {
                int step = dims[band];
                int delta = mults[band];
                byte[] maskDataBand = maskData[band];
                int sum = 0;
                for (int gray = 0; gray < 256; ++gray) {
                    int tmp = sum;
                    int frac = tmp & 0xFF;
                    int bin = tmp >> 8;
                    int lowVal = bin * delta;
                    int highVal = lowVal + delta;
                    int pDith = pDithBand + gray;
                    for (int dcount = 0; dcount < maskSize2D; ++dcount) {
                        int threshold = maskDataBand[dcount] & 0xFF;
                        this.ditherLUT[pDith] = frac > threshold ? (byte)(highVal & 0xFF) : (byte)(lowVal & 0xFF);
                        pDith += 256;
                    }
                    sum += step;
                }
                pDithBand += this.ditherLUTBandStride;
            }
        }

        public boolean equals(int[] dims, int[] mults, byte[][] maskData) {
            int i;
            if (dims.length != this.dimsCache.length) {
                return false;
            }
            for (i = 0; i < dims.length; ++i) {
                if (dims[i] == this.dimsCache[i]) continue;
                return false;
            }
            if (mults.length != this.multsCache.length) {
                return false;
            }
            for (i = 0; i < mults.length; ++i) {
                if (mults[i] == this.multsCache[i]) continue;
                return false;
            }
            if (maskData.length != OrderedDitherOpImage.this.maskDataByte.length) {
                return false;
            }
            for (i = 0; i < maskData.length; ++i) {
                if (maskData[i].length != this.maskDataCache[i].length) {
                    return false;
                }
                byte[] refData = this.maskDataCache[i];
                byte[] data = maskData[i];
                for (int j = 0; j < maskData[i].length; ++j) {
                    if (data[j] == refData[j]) continue;
                    return false;
                }
            }
            return true;
        }
    }
}

