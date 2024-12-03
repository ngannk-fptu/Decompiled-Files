/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import com.sun.media.jai.util.DataBufferUtils;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.media.jai.JaiI18N;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFactory;
import javax.media.jai.RasterFormatTag;
import javax.media.jai.remote.SerializableState;
import javax.media.jai.remote.SerializerFactory;

public class LookupTableJAI
implements Serializable {
    transient DataBuffer data;
    private int[] tableOffsets;

    public LookupTableJAI(byte[] data) {
        if (data == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.data = new DataBufferByte(data, data.length);
        this.initOffsets(1, 0);
    }

    public LookupTableJAI(byte[] data, int offset) {
        if (data == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.initOffsets(1, offset);
        this.data = new DataBufferByte(data, data.length);
    }

    public LookupTableJAI(byte[][] data) {
        if (data == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.initOffsets(data.length, 0);
        this.data = new DataBufferByte(data, data[0].length);
    }

    public LookupTableJAI(byte[][] data, int offset) {
        if (data == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.initOffsets(data.length, offset);
        this.data = new DataBufferByte(data, data[0].length);
    }

    public LookupTableJAI(byte[][] data, int[] offsets) {
        if (data == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.initOffsets(data.length, offsets);
        this.data = new DataBufferByte(data, data[0].length);
    }

    public LookupTableJAI(short[] data, boolean isUShort) {
        if (data == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.initOffsets(1, 0);
        this.data = isUShort ? new DataBufferUShort(data, data.length) : new DataBufferShort(data, data.length);
    }

    public LookupTableJAI(short[] data, int offset, boolean isUShort) {
        if (data == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.initOffsets(1, offset);
        this.data = isUShort ? new DataBufferUShort(data, data.length) : new DataBufferShort(data, data.length);
    }

    public LookupTableJAI(short[][] data, boolean isUShort) {
        if (data == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.initOffsets(data.length, 0);
        this.data = isUShort ? new DataBufferUShort(data, data[0].length) : new DataBufferShort(data, data[0].length);
    }

    public LookupTableJAI(short[][] data, int offset, boolean isUShort) {
        if (data == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.initOffsets(data.length, offset);
        this.data = isUShort ? new DataBufferUShort(data, data[0].length) : new DataBufferShort(data, data[0].length);
    }

    public LookupTableJAI(short[][] data, int[] offsets, boolean isUShort) {
        if (data == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.initOffsets(data.length, offsets);
        this.data = isUShort ? new DataBufferUShort(data, data[0].length) : new DataBufferShort(data, data[0].length);
    }

    public LookupTableJAI(int[] data) {
        if (data == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.initOffsets(1, 0);
        this.data = new DataBufferInt(data, data.length);
    }

    public LookupTableJAI(int[] data, int offset) {
        if (data == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.initOffsets(1, offset);
        this.data = new DataBufferInt(data, data.length);
    }

    public LookupTableJAI(int[][] data) {
        if (data == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.initOffsets(data.length, 0);
        this.data = new DataBufferInt(data, data[0].length);
    }

    public LookupTableJAI(int[][] data, int offset) {
        if (data == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.initOffsets(data.length, offset);
        this.data = new DataBufferInt(data, data[0].length);
    }

    public LookupTableJAI(int[][] data, int[] offsets) {
        if (data == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.initOffsets(data.length, offsets);
        this.data = new DataBufferInt(data, data[0].length);
    }

    public LookupTableJAI(float[] data) {
        if (data == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.initOffsets(1, 0);
        this.data = DataBufferUtils.createDataBufferFloat(data, data.length);
    }

    public LookupTableJAI(float[] data, int offset) {
        if (data == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.initOffsets(1, offset);
        this.data = DataBufferUtils.createDataBufferFloat(data, data.length);
    }

    public LookupTableJAI(float[][] data) {
        if (data == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.initOffsets(data.length, 0);
        this.data = DataBufferUtils.createDataBufferFloat(data, data[0].length);
    }

    public LookupTableJAI(float[][] data, int offset) {
        if (data == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.initOffsets(data.length, offset);
        this.data = DataBufferUtils.createDataBufferFloat(data, data[0].length);
    }

    public LookupTableJAI(float[][] data, int[] offsets) {
        if (data == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.initOffsets(data.length, offsets);
        this.data = DataBufferUtils.createDataBufferFloat(data, data[0].length);
    }

    public LookupTableJAI(double[] data) {
        if (data == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.initOffsets(1, 0);
        this.data = DataBufferUtils.createDataBufferDouble(data, data.length);
    }

    public LookupTableJAI(double[] data, int offset) {
        if (data == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.initOffsets(1, offset);
        this.data = DataBufferUtils.createDataBufferDouble(data, data.length);
    }

    public LookupTableJAI(double[][] data) {
        if (data == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.initOffsets(data.length, 0);
        this.data = DataBufferUtils.createDataBufferDouble(data, data[0].length);
    }

    public LookupTableJAI(double[][] data, int offset) {
        if (data == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.initOffsets(data.length, offset);
        this.data = DataBufferUtils.createDataBufferDouble(data, data[0].length);
    }

    public LookupTableJAI(double[][] data, int[] offsets) {
        if (data == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.initOffsets(data.length, offsets);
        this.data = DataBufferUtils.createDataBufferDouble(data, data[0].length);
    }

    public DataBuffer getData() {
        return this.data;
    }

    public byte[][] getByteData() {
        return this.data instanceof DataBufferByte ? ((DataBufferByte)this.data).getBankData() : (byte[][])null;
    }

    public byte[] getByteData(int band) {
        return this.data instanceof DataBufferByte ? ((DataBufferByte)this.data).getData(band) : null;
    }

    public short[][] getShortData() {
        if (this.data instanceof DataBufferUShort) {
            return ((DataBufferUShort)this.data).getBankData();
        }
        if (this.data instanceof DataBufferShort) {
            return ((DataBufferShort)this.data).getBankData();
        }
        return null;
    }

    public short[] getShortData(int band) {
        if (this.data instanceof DataBufferUShort) {
            return ((DataBufferUShort)this.data).getData(band);
        }
        if (this.data instanceof DataBufferShort) {
            return ((DataBufferShort)this.data).getData(band);
        }
        return null;
    }

    public int[][] getIntData() {
        return this.data instanceof DataBufferInt ? ((DataBufferInt)this.data).getBankData() : (int[][])null;
    }

    public int[] getIntData(int band) {
        return this.data instanceof DataBufferInt ? ((DataBufferInt)this.data).getData(band) : null;
    }

    public float[][] getFloatData() {
        return this.data.getDataType() == 4 ? DataBufferUtils.getBankDataFloat(this.data) : (float[][])null;
    }

    public float[] getFloatData(int band) {
        return this.data.getDataType() == 4 ? DataBufferUtils.getDataFloat(this.data, band) : null;
    }

    public double[][] getDoubleData() {
        return this.data.getDataType() == 5 ? DataBufferUtils.getBankDataDouble(this.data) : (double[][])null;
    }

    public double[] getDoubleData(int band) {
        return this.data.getDataType() == 5 ? DataBufferUtils.getDataDouble(this.data, band) : null;
    }

    public int[] getOffsets() {
        return this.tableOffsets;
    }

    public int getOffset() {
        return this.tableOffsets[0];
    }

    public int getOffset(int band) {
        return this.tableOffsets[band];
    }

    public int getNumBands() {
        return this.data.getNumBanks();
    }

    public int getNumEntries() {
        return this.data.getSize();
    }

    public int getDataType() {
        return this.data.getDataType();
    }

    public int getDestNumBands(int srcNumBands) {
        int tblNumBands = this.getNumBands();
        return srcNumBands == 1 ? tblNumBands : srcNumBands;
    }

    public SampleModel getDestSampleModel(SampleModel srcSampleModel) {
        if (srcSampleModel == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        return this.getDestSampleModel(srcSampleModel, srcSampleModel.getWidth(), srcSampleModel.getHeight());
    }

    public SampleModel getDestSampleModel(SampleModel srcSampleModel, int width, int height) {
        if (srcSampleModel == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (!this.isIntegralDataType(srcSampleModel)) {
            return null;
        }
        return RasterFactory.createComponentSampleModel(srcSampleModel, this.getDataType(), width, height, this.getDestNumBands(srcSampleModel.getNumBands()));
    }

    public boolean isIntegralDataType(SampleModel sampleModel) {
        if (sampleModel == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        return this.isIntegralDataType(sampleModel.getTransferType());
    }

    public boolean isIntegralDataType(int dataType) {
        return dataType == 0 || dataType == 1 || dataType == 2 || dataType == 3;
    }

    public int lookup(int band, int value) {
        return this.data.getElem(band, value - this.tableOffsets[band]);
    }

    public float lookupFloat(int band, int value) {
        return this.data.getElemFloat(band, value - this.tableOffsets[band]);
    }

    public double lookupDouble(int band, int value) {
        return this.data.getElemDouble(band, value - this.tableOffsets[band]);
    }

    public WritableRaster lookup(WritableRaster src) {
        if (src == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        return this.lookup(src, src, src.getBounds());
    }

    public WritableRaster lookup(Raster src, WritableRaster dst, Rectangle rect) {
        SampleModel dstSampleModel;
        if (src == null) {
            throw new IllegalArgumentException(JaiI18N.getString("LookupTableJAI1"));
        }
        SampleModel srcSampleModel = src.getSampleModel();
        if (!this.isIntegralDataType(srcSampleModel)) {
            throw new IllegalArgumentException(JaiI18N.getString("LookupTableJAI2"));
        }
        rect = rect == null ? src.getBounds() : rect.intersection(src.getBounds());
        if (dst != null) {
            rect = rect.intersection(dst.getBounds());
        }
        if (dst == null) {
            dstSampleModel = this.getDestSampleModel(srcSampleModel, rect.width, rect.height);
            dst = RasterFactory.createWritableRaster(dstSampleModel, new Point(rect.x, rect.y));
        } else {
            dstSampleModel = dst.getSampleModel();
            if (dstSampleModel.getTransferType() != this.getDataType() || dstSampleModel.getNumBands() != this.getDestNumBands(srcSampleModel.getNumBands())) {
                throw new IllegalArgumentException(JaiI18N.getString("LookupTableJAI3"));
            }
        }
        int sTagID = RasterAccessor.findCompatibleTag(null, srcSampleModel);
        int dTagID = RasterAccessor.findCompatibleTag(null, dstSampleModel);
        RasterFormatTag sTag = new RasterFormatTag(srcSampleModel, sTagID);
        RasterFormatTag dTag = new RasterFormatTag(dstSampleModel, dTagID);
        RasterAccessor s = new RasterAccessor(src, rect, sTag, null);
        RasterAccessor d = new RasterAccessor(dst, rect, dTag, null);
        int srcNumBands = s.getNumBands();
        int srcDataType = s.getDataType();
        int tblNumBands = this.getNumBands();
        int tblDataType = this.getDataType();
        int dstWidth = d.getWidth();
        int dstHeight = d.getHeight();
        int dstNumBands = d.getNumBands();
        int dstDataType = d.getDataType();
        int srcLineStride = s.getScanlineStride();
        int srcPixelStride = s.getPixelStride();
        int[] srcBandOffsets = s.getBandOffsets();
        Object bSrcData = s.getByteDataArrays();
        Object sSrcData = s.getShortDataArrays();
        Object iSrcData = s.getIntDataArrays();
        if (srcNumBands < dstNumBands) {
            int offset0 = srcBandOffsets[0];
            srcBandOffsets = new int[dstNumBands];
            for (int i = 0; i < dstNumBands; ++i) {
                srcBandOffsets[i] = offset0;
            }
            switch (srcDataType) {
                case 0: {
                    byte[] bData0 = bSrcData[0];
                    bSrcData = new byte[dstNumBands][];
                    for (int i = 0; i < dstNumBands; ++i) {
                        bSrcData[i] = bData0;
                    }
                    break;
                }
                case 1: 
                case 2: {
                    short[] sData0 = sSrcData[0];
                    sSrcData = new short[dstNumBands][];
                    for (int i = 0; i < dstNumBands; ++i) {
                        sSrcData[i] = sData0;
                    }
                    break;
                }
                case 3: {
                    int[] iData0 = iSrcData[0];
                    iSrcData = new int[dstNumBands][];
                    for (int i = 0; i < dstNumBands; ++i) {
                        iSrcData[i] = iData0;
                    }
                    break;
                }
            }
        }
        int[] tblOffsets = this.getOffsets();
        Object bTblData = this.getByteData();
        Object sTblData = this.getShortData();
        Object iTblData = this.getIntData();
        Object fTblData = this.getFloatData();
        Object dTblData = this.getDoubleData();
        if (tblNumBands < dstNumBands) {
            int offset0 = tblOffsets[0];
            tblOffsets = new int[dstNumBands];
            for (int i = 0; i < dstNumBands; ++i) {
                tblOffsets[i] = offset0;
            }
            switch (tblDataType) {
                case 0: {
                    byte[] bData0 = bTblData[0];
                    bTblData = new byte[dstNumBands][];
                    for (int i = 0; i < dstNumBands; ++i) {
                        bTblData[i] = bData0;
                    }
                    break;
                }
                case 1: 
                case 2: {
                    short[] sData0 = sTblData[0];
                    sTblData = new short[dstNumBands][];
                    for (int i = 0; i < dstNumBands; ++i) {
                        sTblData[i] = sData0;
                    }
                    break;
                }
                case 3: {
                    int[] iData0 = iTblData[0];
                    iTblData = new int[dstNumBands][];
                    for (int i = 0; i < dstNumBands; ++i) {
                        iTblData[i] = iData0;
                    }
                    break;
                }
                case 4: {
                    float[] fData0 = fTblData[0];
                    fTblData = new float[dstNumBands][];
                    for (int i = 0; i < dstNumBands; ++i) {
                        fTblData[i] = fData0;
                    }
                    break;
                }
                case 5: {
                    double[] dData0 = dTblData[0];
                    dTblData = new double[dstNumBands][];
                    for (int i = 0; i < dstNumBands; ++i) {
                        dTblData[i] = dData0;
                    }
                    break;
                }
            }
        }
        int dstLineStride = d.getScanlineStride();
        int dstPixelStride = d.getPixelStride();
        int[] dstBandOffsets = d.getBandOffsets();
        byte[][] bDstData = d.getByteDataArrays();
        short[][] sDstData = d.getShortDataArrays();
        int[][] iDstData = d.getIntDataArrays();
        float[][] fDstData = d.getFloatDataArrays();
        double[][] dDstData = d.getDoubleDataArrays();
        block12 : switch (dstDataType) {
            case 0: {
                switch (srcDataType) {
                    case 0: {
                        this.lookup(srcLineStride, srcPixelStride, srcBandOffsets, (byte[][])bSrcData, dstWidth, dstHeight, dstNumBands, dstLineStride, dstPixelStride, dstBandOffsets, bDstData, tblOffsets, (byte[][])bTblData);
                        break;
                    }
                    case 1: {
                        this.lookupU(srcLineStride, srcPixelStride, srcBandOffsets, (short[][])sSrcData, dstWidth, dstHeight, dstNumBands, dstLineStride, dstPixelStride, dstBandOffsets, bDstData, tblOffsets, (byte[][])bTblData);
                        break;
                    }
                    case 2: {
                        this.lookup(srcLineStride, srcPixelStride, srcBandOffsets, (short[][])sSrcData, dstWidth, dstHeight, dstNumBands, dstLineStride, dstPixelStride, dstBandOffsets, bDstData, tblOffsets, (byte[][])bTblData);
                        break;
                    }
                    case 3: {
                        this.lookup(srcLineStride, srcPixelStride, srcBandOffsets, (int[][])iSrcData, dstWidth, dstHeight, dstNumBands, dstLineStride, dstPixelStride, dstBandOffsets, bDstData, tblOffsets, (byte[][])bTblData);
                    }
                }
                break;
            }
            case 1: 
            case 2: {
                switch (srcDataType) {
                    case 0: {
                        this.lookup(srcLineStride, srcPixelStride, srcBandOffsets, (byte[][])bSrcData, dstWidth, dstHeight, dstNumBands, dstLineStride, dstPixelStride, dstBandOffsets, sDstData, tblOffsets, (short[][])sTblData);
                        break;
                    }
                    case 1: {
                        this.lookupU(srcLineStride, srcPixelStride, srcBandOffsets, (short[][])sSrcData, dstWidth, dstHeight, dstNumBands, dstLineStride, dstPixelStride, dstBandOffsets, sDstData, tblOffsets, (short[][])sTblData);
                        break;
                    }
                    case 2: {
                        this.lookup(srcLineStride, srcPixelStride, srcBandOffsets, (short[][])sSrcData, dstWidth, dstHeight, dstNumBands, dstLineStride, dstPixelStride, dstBandOffsets, sDstData, tblOffsets, (short[][])sTblData);
                        break;
                    }
                    case 3: {
                        this.lookup(srcLineStride, srcPixelStride, srcBandOffsets, (int[][])iSrcData, dstWidth, dstHeight, dstNumBands, dstLineStride, dstPixelStride, dstBandOffsets, sDstData, tblOffsets, (short[][])sTblData);
                    }
                }
                break;
            }
            case 3: {
                switch (srcDataType) {
                    case 0: {
                        this.lookup(srcLineStride, srcPixelStride, srcBandOffsets, (byte[][])bSrcData, dstWidth, dstHeight, dstNumBands, dstLineStride, dstPixelStride, dstBandOffsets, iDstData, tblOffsets, (int[][])iTblData);
                        break;
                    }
                    case 1: {
                        this.lookupU(srcLineStride, srcPixelStride, srcBandOffsets, (short[][])sSrcData, dstWidth, dstHeight, dstNumBands, dstLineStride, dstPixelStride, dstBandOffsets, iDstData, tblOffsets, (int[][])iTblData);
                        break;
                    }
                    case 2: {
                        this.lookup(srcLineStride, srcPixelStride, srcBandOffsets, (short[][])sSrcData, dstWidth, dstHeight, dstNumBands, dstLineStride, dstPixelStride, dstBandOffsets, iDstData, tblOffsets, (int[][])iTblData);
                        break;
                    }
                    case 3: {
                        this.lookup(srcLineStride, srcPixelStride, srcBandOffsets, (int[][])iSrcData, dstWidth, dstHeight, dstNumBands, dstLineStride, dstPixelStride, dstBandOffsets, iDstData, tblOffsets, (int[][])iTblData);
                    }
                }
                break;
            }
            case 4: {
                switch (srcDataType) {
                    case 0: {
                        this.lookup(srcLineStride, srcPixelStride, srcBandOffsets, (byte[][])bSrcData, dstWidth, dstHeight, dstNumBands, dstLineStride, dstPixelStride, dstBandOffsets, fDstData, tblOffsets, (float[][])fTblData);
                        break;
                    }
                    case 1: {
                        this.lookupU(srcLineStride, srcPixelStride, srcBandOffsets, (short[][])sSrcData, dstWidth, dstHeight, dstNumBands, dstLineStride, dstPixelStride, dstBandOffsets, fDstData, tblOffsets, (float[][])fTblData);
                        break;
                    }
                    case 2: {
                        this.lookup(srcLineStride, srcPixelStride, srcBandOffsets, (short[][])sSrcData, dstWidth, dstHeight, dstNumBands, dstLineStride, dstPixelStride, dstBandOffsets, fDstData, tblOffsets, (float[][])fTblData);
                        break;
                    }
                    case 3: {
                        this.lookup(srcLineStride, srcPixelStride, srcBandOffsets, (int[][])iSrcData, dstWidth, dstHeight, dstNumBands, dstLineStride, dstPixelStride, dstBandOffsets, fDstData, tblOffsets, (float[][])fTblData);
                    }
                }
                break;
            }
            case 5: {
                switch (srcDataType) {
                    case 0: {
                        this.lookup(srcLineStride, srcPixelStride, srcBandOffsets, (byte[][])bSrcData, dstWidth, dstHeight, dstNumBands, dstLineStride, dstPixelStride, dstBandOffsets, dDstData, tblOffsets, (double[][])dTblData);
                        break block12;
                    }
                    case 1: {
                        this.lookupU(srcLineStride, srcPixelStride, srcBandOffsets, (short[][])sSrcData, dstWidth, dstHeight, dstNumBands, dstLineStride, dstPixelStride, dstBandOffsets, dDstData, tblOffsets, (double[][])dTblData);
                        break block12;
                    }
                    case 2: {
                        this.lookup(srcLineStride, srcPixelStride, srcBandOffsets, (short[][])sSrcData, dstWidth, dstHeight, dstNumBands, dstLineStride, dstPixelStride, dstBandOffsets, dDstData, tblOffsets, (double[][])dTblData);
                        break block12;
                    }
                    case 3: {
                        this.lookup(srcLineStride, srcPixelStride, srcBandOffsets, (int[][])iSrcData, dstWidth, dstHeight, dstNumBands, dstLineStride, dstPixelStride, dstBandOffsets, dDstData, tblOffsets, (double[][])dTblData);
                    }
                }
            }
        }
        d.copyDataToRaster();
        return dst;
    }

    private void lookup(int srcLineStride, int srcPixelStride, int[] srcBandOffsets, byte[][] srcData, int width, int height, int bands, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, byte[][] dstData, int[] tblOffsets, byte[][] tblData) {
        for (int b = 0; b < bands; ++b) {
            byte[] s = srcData[b];
            byte[] d = dstData[b];
            byte[] t = tblData[b];
            int srcLineOffset = srcBandOffsets[b];
            int dstLineOffset = dstBandOffsets[b];
            int tblOffset = tblOffsets[b];
            for (int h = 0; h < height; ++h) {
                int srcPixelOffset = srcLineOffset;
                int dstPixelOffset = dstLineOffset;
                srcLineOffset += srcLineStride;
                dstLineOffset += dstLineStride;
                for (int w = 0; w < width; ++w) {
                    d[dstPixelOffset] = t[(s[srcPixelOffset] & 0xFF) - tblOffset];
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
            }
        }
    }

    private void lookupU(int srcLineStride, int srcPixelStride, int[] srcBandOffsets, short[][] srcData, int width, int height, int bands, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, byte[][] dstData, int[] tblOffsets, byte[][] tblData) {
        for (int b = 0; b < bands; ++b) {
            short[] s = srcData[b];
            byte[] d = dstData[b];
            byte[] t = tblData[b];
            int srcLineOffset = srcBandOffsets[b];
            int dstLineOffset = dstBandOffsets[b];
            int tblOffset = tblOffsets[b];
            for (int h = 0; h < height; ++h) {
                int srcPixelOffset = srcLineOffset;
                int dstPixelOffset = dstLineOffset;
                srcLineOffset += srcLineStride;
                dstLineOffset += dstLineStride;
                for (int w = 0; w < width; ++w) {
                    d[dstPixelOffset] = t[(s[srcPixelOffset] & 0xFFFF) - tblOffset];
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
            }
        }
    }

    private void lookup(int srcLineStride, int srcPixelStride, int[] srcBandOffsets, short[][] srcData, int width, int height, int bands, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, byte[][] dstData, int[] tblOffsets, byte[][] tblData) {
        for (int b = 0; b < bands; ++b) {
            short[] s = srcData[b];
            byte[] d = dstData[b];
            byte[] t = tblData[b];
            int srcLineOffset = srcBandOffsets[b];
            int dstLineOffset = dstBandOffsets[b];
            int tblOffset = tblOffsets[b];
            for (int h = 0; h < height; ++h) {
                int srcPixelOffset = srcLineOffset;
                int dstPixelOffset = dstLineOffset;
                srcLineOffset += srcLineStride;
                dstLineOffset += dstLineStride;
                for (int w = 0; w < width; ++w) {
                    d[dstPixelOffset] = t[s[srcPixelOffset] - tblOffset];
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
            }
        }
    }

    private void lookup(int srcLineStride, int srcPixelStride, int[] srcBandOffsets, int[][] srcData, int width, int height, int bands, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, byte[][] dstData, int[] tblOffsets, byte[][] tblData) {
        for (int b = 0; b < bands; ++b) {
            int[] s = srcData[b];
            byte[] d = dstData[b];
            byte[] t = tblData[b];
            int srcLineOffset = srcBandOffsets[b];
            int dstLineOffset = dstBandOffsets[b];
            int tblOffset = tblOffsets[b];
            for (int h = 0; h < height; ++h) {
                int srcPixelOffset = srcLineOffset;
                int dstPixelOffset = dstLineOffset;
                srcLineOffset += srcLineStride;
                dstLineOffset += dstLineStride;
                for (int w = 0; w < width; ++w) {
                    d[dstPixelOffset] = t[s[srcPixelOffset] - tblOffset];
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
            }
        }
    }

    private void lookup(int srcLineStride, int srcPixelStride, int[] srcBandOffsets, byte[][] srcData, int width, int height, int bands, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, short[][] dstData, int[] tblOffsets, short[][] tblData) {
        for (int b = 0; b < bands; ++b) {
            byte[] s = srcData[b];
            short[] d = dstData[b];
            short[] t = tblData[b];
            int srcLineOffset = srcBandOffsets[b];
            int dstLineOffset = dstBandOffsets[b];
            int tblOffset = tblOffsets[b];
            for (int h = 0; h < height; ++h) {
                int srcPixelOffset = srcLineOffset;
                int dstPixelOffset = dstLineOffset;
                srcLineOffset += srcLineStride;
                dstLineOffset += dstLineStride;
                for (int w = 0; w < width; ++w) {
                    d[dstPixelOffset] = t[(s[srcPixelOffset] & 0xFF) - tblOffset];
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
            }
        }
    }

    private void lookupU(int srcLineStride, int srcPixelStride, int[] srcBandOffsets, short[][] srcData, int width, int height, int bands, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, short[][] dstData, int[] tblOffsets, short[][] tblData) {
        for (int b = 0; b < bands; ++b) {
            short[] s = srcData[b];
            short[] d = dstData[b];
            short[] t = tblData[b];
            int srcLineOffset = srcBandOffsets[b];
            int dstLineOffset = dstBandOffsets[b];
            int tblOffset = tblOffsets[b];
            for (int h = 0; h < height; ++h) {
                int srcPixelOffset = srcLineOffset;
                int dstPixelOffset = dstLineOffset;
                srcLineOffset += srcLineStride;
                dstLineOffset += dstLineStride;
                for (int w = 0; w < width; ++w) {
                    d[dstPixelOffset] = t[(s[srcPixelOffset] & 0xFFFF) - tblOffset];
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
            }
        }
    }

    private void lookup(int srcLineStride, int srcPixelStride, int[] srcBandOffsets, short[][] srcData, int width, int height, int bands, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, short[][] dstData, int[] tblOffsets, short[][] tblData) {
        for (int b = 0; b < bands; ++b) {
            short[] s = srcData[b];
            short[] d = dstData[b];
            short[] t = tblData[b];
            int srcLineOffset = srcBandOffsets[b];
            int dstLineOffset = dstBandOffsets[b];
            int tblOffset = tblOffsets[b];
            for (int h = 0; h < height; ++h) {
                int srcPixelOffset = srcLineOffset;
                int dstPixelOffset = dstLineOffset;
                srcLineOffset += srcLineStride;
                dstLineOffset += dstLineStride;
                for (int w = 0; w < width; ++w) {
                    d[dstPixelOffset] = t[s[srcPixelOffset] - tblOffset];
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
            }
        }
    }

    private void lookup(int srcLineStride, int srcPixelStride, int[] srcBandOffsets, int[][] srcData, int width, int height, int bands, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, short[][] dstData, int[] tblOffsets, short[][] tblData) {
        for (int b = 0; b < bands; ++b) {
            int[] s = srcData[b];
            short[] d = dstData[b];
            short[] t = tblData[b];
            int srcLineOffset = srcBandOffsets[b];
            int dstLineOffset = dstBandOffsets[b];
            int tblOffset = tblOffsets[b];
            for (int h = 0; h < height; ++h) {
                int srcPixelOffset = srcLineOffset;
                int dstPixelOffset = dstLineOffset;
                srcLineOffset += srcLineStride;
                dstLineOffset += dstLineStride;
                for (int w = 0; w < width; ++w) {
                    d[dstPixelOffset] = t[s[srcPixelOffset] - tblOffset];
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
            }
        }
    }

    private void lookup(int srcLineStride, int srcPixelStride, int[] srcBandOffsets, byte[][] srcData, int width, int height, int bands, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, int[][] dstData, int[] tblOffsets, int[][] tblData) {
        if (tblData == null) {
            for (int b = 0; b < bands; ++b) {
                byte[] s = srcData[b];
                int[] d = dstData[b];
                int srcLineOffset = srcBandOffsets[b];
                int dstLineOffset = dstBandOffsets[b];
                for (int h = 0; h < height; ++h) {
                    int srcPixelOffset = srcLineOffset;
                    int dstPixelOffset = dstLineOffset;
                    srcLineOffset += srcLineStride;
                    dstLineOffset += dstLineStride;
                    for (int w = 0; w < width; ++w) {
                        d[dstPixelOffset] = this.data.getElem(b, s[srcPixelOffset] & 0xFF);
                        srcPixelOffset += srcPixelStride;
                        dstPixelOffset += dstPixelStride;
                    }
                }
            }
        } else {
            for (int b = 0; b < bands; ++b) {
                byte[] s = srcData[b];
                int[] d = dstData[b];
                int[] t = tblData[b];
                int srcLineOffset = srcBandOffsets[b];
                int dstLineOffset = dstBandOffsets[b];
                int tblOffset = tblOffsets[b];
                for (int h = 0; h < height; ++h) {
                    int srcPixelOffset = srcLineOffset;
                    int dstPixelOffset = dstLineOffset;
                    srcLineOffset += srcLineStride;
                    dstLineOffset += dstLineStride;
                    for (int w = 0; w < width; ++w) {
                        d[dstPixelOffset] = t[(s[srcPixelOffset] & 0xFF) - tblOffset];
                        srcPixelOffset += srcPixelStride;
                        dstPixelOffset += dstPixelStride;
                    }
                }
            }
        }
    }

    private void lookupU(int srcLineStride, int srcPixelStride, int[] srcBandOffsets, short[][] srcData, int width, int height, int bands, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, int[][] dstData, int[] tblOffsets, int[][] tblData) {
        if (tblData == null) {
            for (int b = 0; b < bands; ++b) {
                short[] s = srcData[b];
                int[] d = dstData[b];
                int srcLineOffset = srcBandOffsets[b];
                int dstLineOffset = dstBandOffsets[b];
                for (int h = 0; h < height; ++h) {
                    int srcPixelOffset = srcLineOffset;
                    int dstPixelOffset = dstLineOffset;
                    srcLineOffset += srcLineStride;
                    dstLineOffset += dstLineStride;
                    for (int w = 0; w < width; ++w) {
                        d[dstPixelOffset] = this.data.getElem(b, s[srcPixelOffset] & 0xFFFF);
                        srcPixelOffset += srcPixelStride;
                        dstPixelOffset += dstPixelStride;
                    }
                }
            }
        } else {
            for (int b = 0; b < bands; ++b) {
                short[] s = srcData[b];
                int[] d = dstData[b];
                int[] t = tblData[b];
                int srcLineOffset = srcBandOffsets[b];
                int dstLineOffset = dstBandOffsets[b];
                int tblOffset = tblOffsets[b];
                for (int h = 0; h < height; ++h) {
                    int srcPixelOffset = srcLineOffset;
                    int dstPixelOffset = dstLineOffset;
                    srcLineOffset += srcLineStride;
                    dstLineOffset += dstLineStride;
                    for (int w = 0; w < width; ++w) {
                        d[dstPixelOffset] = t[(s[srcPixelOffset] & 0xFFFF) - tblOffset];
                        srcPixelOffset += srcPixelStride;
                        dstPixelOffset += dstPixelStride;
                    }
                }
            }
        }
    }

    private void lookup(int srcLineStride, int srcPixelStride, int[] srcBandOffsets, short[][] srcData, int width, int height, int bands, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, int[][] dstData, int[] tblOffsets, int[][] tblData) {
        if (tblData == null) {
            for (int b = 0; b < bands; ++b) {
                short[] s = srcData[b];
                int[] d = dstData[b];
                int srcLineOffset = srcBandOffsets[b];
                int dstLineOffset = dstBandOffsets[b];
                for (int h = 0; h < height; ++h) {
                    int srcPixelOffset = srcLineOffset;
                    int dstPixelOffset = dstLineOffset;
                    srcLineOffset += srcLineStride;
                    dstLineOffset += dstLineStride;
                    for (int w = 0; w < width; ++w) {
                        d[dstPixelOffset] = this.data.getElem(b, s[srcPixelOffset]);
                        srcPixelOffset += srcPixelStride;
                        dstPixelOffset += dstPixelStride;
                    }
                }
            }
        } else {
            for (int b = 0; b < bands; ++b) {
                short[] s = srcData[b];
                int[] d = dstData[b];
                int[] t = tblData[b];
                int srcLineOffset = srcBandOffsets[b];
                int dstLineOffset = dstBandOffsets[b];
                int tblOffset = tblOffsets[b];
                for (int h = 0; h < height; ++h) {
                    int srcPixelOffset = srcLineOffset;
                    int dstPixelOffset = dstLineOffset;
                    srcLineOffset += srcLineStride;
                    dstLineOffset += dstLineStride;
                    for (int w = 0; w < width; ++w) {
                        d[dstPixelOffset] = t[s[srcPixelOffset] - tblOffset];
                        srcPixelOffset += srcPixelStride;
                        dstPixelOffset += dstPixelStride;
                    }
                }
            }
        }
    }

    private void lookup(int srcLineStride, int srcPixelStride, int[] srcBandOffsets, int[][] srcData, int width, int height, int bands, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, int[][] dstData, int[] tblOffsets, int[][] tblData) {
        if (tblData == null) {
            for (int b = 0; b < bands; ++b) {
                int[] s = srcData[b];
                int[] d = dstData[b];
                int srcLineOffset = srcBandOffsets[b];
                int dstLineOffset = dstBandOffsets[b];
                for (int h = 0; h < height; ++h) {
                    int srcPixelOffset = srcLineOffset;
                    int dstPixelOffset = dstLineOffset;
                    srcLineOffset += srcLineStride;
                    dstLineOffset += dstLineStride;
                    for (int w = 0; w < width; ++w) {
                        d[dstPixelOffset] = this.data.getElem(b, s[srcPixelOffset]);
                        srcPixelOffset += srcPixelStride;
                        dstPixelOffset += dstPixelStride;
                    }
                }
            }
        } else {
            for (int b = 0; b < bands; ++b) {
                int[] s = srcData[b];
                int[] d = dstData[b];
                int[] t = tblData[b];
                int srcLineOffset = srcBandOffsets[b];
                int dstLineOffset = dstBandOffsets[b];
                int tblOffset = tblOffsets[b];
                for (int h = 0; h < height; ++h) {
                    int srcPixelOffset = srcLineOffset;
                    int dstPixelOffset = dstLineOffset;
                    srcLineOffset += srcLineStride;
                    dstLineOffset += dstLineStride;
                    for (int w = 0; w < width; ++w) {
                        d[dstPixelOffset] = t[s[srcPixelOffset] - tblOffset];
                        srcPixelOffset += srcPixelStride;
                        dstPixelOffset += dstPixelStride;
                    }
                }
            }
        }
    }

    private void lookup(int srcLineStride, int srcPixelStride, int[] srcBandOffsets, byte[][] srcData, int width, int height, int bands, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, float[][] dstData, int[] tblOffsets, float[][] tblData) {
        for (int b = 0; b < bands; ++b) {
            byte[] s = srcData[b];
            float[] d = dstData[b];
            float[] t = tblData[b];
            int srcLineOffset = srcBandOffsets[b];
            int dstLineOffset = dstBandOffsets[b];
            int tblOffset = tblOffsets[b];
            for (int h = 0; h < height; ++h) {
                int srcPixelOffset = srcLineOffset;
                int dstPixelOffset = dstLineOffset;
                srcLineOffset += srcLineStride;
                dstLineOffset += dstLineStride;
                for (int w = 0; w < width; ++w) {
                    d[dstPixelOffset] = t[(s[srcPixelOffset] & 0xFF) - tblOffset];
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
            }
        }
    }

    private void lookupU(int srcLineStride, int srcPixelStride, int[] srcBandOffsets, short[][] srcData, int width, int height, int bands, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, float[][] dstData, int[] tblOffsets, float[][] tblData) {
        for (int b = 0; b < bands; ++b) {
            short[] s = srcData[b];
            float[] d = dstData[b];
            float[] t = tblData[b];
            int srcLineOffset = srcBandOffsets[b];
            int dstLineOffset = dstBandOffsets[b];
            int tblOffset = tblOffsets[b];
            for (int h = 0; h < height; ++h) {
                int srcPixelOffset = srcLineOffset;
                int dstPixelOffset = dstLineOffset;
                srcLineOffset += srcLineStride;
                dstLineOffset += dstLineStride;
                for (int w = 0; w < width; ++w) {
                    d[dstPixelOffset] = t[(s[srcPixelOffset] & 0xFFFF) - tblOffset];
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
            }
        }
    }

    private void lookup(int srcLineStride, int srcPixelStride, int[] srcBandOffsets, short[][] srcData, int width, int height, int bands, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, float[][] dstData, int[] tblOffsets, float[][] tblData) {
        for (int b = 0; b < bands; ++b) {
            short[] s = srcData[b];
            float[] d = dstData[b];
            float[] t = tblData[b];
            int srcLineOffset = srcBandOffsets[b];
            int dstLineOffset = dstBandOffsets[b];
            int tblOffset = tblOffsets[b];
            for (int h = 0; h < height; ++h) {
                int srcPixelOffset = srcLineOffset;
                int dstPixelOffset = dstLineOffset;
                srcLineOffset += srcLineStride;
                dstLineOffset += dstLineStride;
                for (int w = 0; w < width; ++w) {
                    d[dstPixelOffset] = t[s[srcPixelOffset] - tblOffset];
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
            }
        }
    }

    private void lookup(int srcLineStride, int srcPixelStride, int[] srcBandOffsets, int[][] srcData, int width, int height, int bands, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, float[][] dstData, int[] tblOffsets, float[][] tblData) {
        for (int b = 0; b < bands; ++b) {
            int[] s = srcData[b];
            float[] d = dstData[b];
            float[] t = tblData[b];
            int srcLineOffset = srcBandOffsets[b];
            int dstLineOffset = dstBandOffsets[b];
            int tblOffset = tblOffsets[b];
            for (int h = 0; h < height; ++h) {
                int srcPixelOffset = srcLineOffset;
                int dstPixelOffset = dstLineOffset;
                srcLineOffset += srcLineStride;
                dstLineOffset += dstLineStride;
                for (int w = 0; w < width; ++w) {
                    d[dstPixelOffset] = t[s[srcPixelOffset] - tblOffset];
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
            }
        }
    }

    private void lookup(int srcLineStride, int srcPixelStride, int[] srcBandOffsets, byte[][] srcData, int width, int height, int bands, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, double[][] dstData, int[] tblOffsets, double[][] tblData) {
        for (int b = 0; b < bands; ++b) {
            byte[] s = srcData[b];
            double[] d = dstData[b];
            double[] t = tblData[b];
            int srcLineOffset = srcBandOffsets[b];
            int dstLineOffset = dstBandOffsets[b];
            int tblOffset = tblOffsets[b];
            for (int h = 0; h < height; ++h) {
                int srcPixelOffset = srcLineOffset;
                int dstPixelOffset = dstLineOffset;
                srcLineOffset += srcLineStride;
                dstLineOffset += dstLineStride;
                for (int w = 0; w < width; ++w) {
                    d[dstPixelOffset] = t[(s[srcPixelOffset] & 0xFF) - tblOffset];
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
            }
        }
    }

    private void lookupU(int srcLineStride, int srcPixelStride, int[] srcBandOffsets, short[][] srcData, int width, int height, int bands, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, double[][] dstData, int[] tblOffsets, double[][] tblData) {
        for (int b = 0; b < bands; ++b) {
            short[] s = srcData[b];
            double[] d = dstData[b];
            double[] t = tblData[b];
            int srcLineOffset = srcBandOffsets[b];
            int dstLineOffset = dstBandOffsets[b];
            int tblOffset = tblOffsets[b];
            for (int h = 0; h < height; ++h) {
                int srcPixelOffset = srcLineOffset;
                int dstPixelOffset = dstLineOffset;
                srcLineOffset += srcLineStride;
                dstLineOffset += dstLineStride;
                for (int w = 0; w < width; ++w) {
                    d[dstPixelOffset] = t[(s[srcPixelOffset] & 0xFFFF) - tblOffset];
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
            }
        }
    }

    private void lookup(int srcLineStride, int srcPixelStride, int[] srcBandOffsets, short[][] srcData, int width, int height, int bands, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, double[][] dstData, int[] tblOffsets, double[][] tblData) {
        for (int b = 0; b < bands; ++b) {
            short[] s = srcData[b];
            double[] d = dstData[b];
            double[] t = tblData[b];
            int srcLineOffset = srcBandOffsets[b];
            int dstLineOffset = dstBandOffsets[b];
            int tblOffset = tblOffsets[b];
            for (int h = 0; h < height; ++h) {
                int srcPixelOffset = srcLineOffset;
                int dstPixelOffset = dstLineOffset;
                srcLineOffset += srcLineStride;
                dstLineOffset += dstLineStride;
                for (int w = 0; w < width; ++w) {
                    d[dstPixelOffset] = t[s[srcPixelOffset] - tblOffset];
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
            }
        }
    }

    private void lookup(int srcLineStride, int srcPixelStride, int[] srcBandOffsets, int[][] srcData, int width, int height, int bands, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, double[][] dstData, int[] tblOffsets, double[][] tblData) {
        for (int b = 0; b < bands; ++b) {
            int[] s = srcData[b];
            double[] d = dstData[b];
            double[] t = tblData[b];
            int srcLineOffset = srcBandOffsets[b];
            int dstLineOffset = dstBandOffsets[b];
            int tblOffset = tblOffsets[b];
            for (int h = 0; h < height; ++h) {
                int srcPixelOffset = srcLineOffset;
                int dstPixelOffset = dstLineOffset;
                srcLineOffset += srcLineStride;
                dstLineOffset += dstLineStride;
                for (int w = 0; w < width; ++w) {
                    d[dstPixelOffset] = t[s[srcPixelOffset] - tblOffset];
                    srcPixelOffset += srcPixelStride;
                    dstPixelOffset += dstPixelStride;
                }
            }
        }
    }

    public int findNearestEntry(float[] pixel) {
        if (pixel == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        int dataType = this.data.getDataType();
        int numBands = this.getNumBands();
        int numEntries = this.getNumEntries();
        int index = -1;
        if (dataType == 0) {
            byte[][] buffer = this.getByteData();
            float minDistance = 0.0f;
            index = 0;
            for (int b = 0; b < numBands; ++b) {
                float delta = pixel[b] - (float)(buffer[b][0] & 0xFF);
                minDistance += delta * delta;
            }
            for (int i = 1; i < numEntries; ++i) {
                float distance = 0.0f;
                for (int b = 0; b < numBands; ++b) {
                    float delta = pixel[b] - (float)(buffer[b][i] & 0xFF);
                    distance += delta * delta;
                }
                if (!(distance < minDistance)) continue;
                minDistance = distance;
                index = i;
            }
        } else if (dataType == 2) {
            short[][] buffer = this.getShortData();
            float minDistance = 0.0f;
            index = 0;
            for (int b = 0; b < numBands; ++b) {
                float delta = pixel[b] - (float)buffer[b][0];
                minDistance += delta * delta;
            }
            for (int i = 1; i < numEntries; ++i) {
                float distance = 0.0f;
                for (int b = 0; b < numBands; ++b) {
                    float delta = pixel[b] - (float)buffer[b][i];
                    distance += delta * delta;
                }
                if (!(distance < minDistance)) continue;
                minDistance = distance;
                index = i;
            }
        } else if (dataType == 1) {
            short[][] buffer = this.getShortData();
            float minDistance = 0.0f;
            index = 0;
            for (int b = 0; b < numBands; ++b) {
                float delta = pixel[b] - (float)(buffer[b][0] & 0xFFFF);
                minDistance += delta * delta;
            }
            for (int i = 1; i < numEntries; ++i) {
                float distance = 0.0f;
                for (int b = 0; b < numBands; ++b) {
                    float delta = pixel[b] - (float)(buffer[b][i] & 0xFFFF);
                    distance += delta * delta;
                }
                if (!(distance < minDistance)) continue;
                minDistance = distance;
                index = i;
            }
        } else if (dataType == 3) {
            int[][] buffer = this.getIntData();
            float minDistance = 0.0f;
            index = 0;
            for (int b = 0; b < numBands; ++b) {
                float delta = pixel[b] - (float)buffer[b][0];
                minDistance += delta * delta;
            }
            for (int i = 1; i < numEntries; ++i) {
                float distance = 0.0f;
                for (int b = 0; b < numBands; ++b) {
                    float delta = pixel[b] - (float)buffer[b][i];
                    distance += delta * delta;
                }
                if (!(distance < minDistance)) continue;
                minDistance = distance;
                index = i;
            }
        } else if (dataType == 4) {
            float[][] buffer = this.getFloatData();
            float minDistance = 0.0f;
            index = 0;
            for (int b = 0; b < numBands; ++b) {
                float delta = pixel[b] - buffer[b][0];
                minDistance += delta * delta;
            }
            for (int i = 1; i < numEntries; ++i) {
                float distance = 0.0f;
                for (int b = 0; b < numBands; ++b) {
                    float delta = pixel[b] - buffer[b][i];
                    distance += delta * delta;
                }
                if (!(distance < minDistance)) continue;
                minDistance = distance;
                index = i;
            }
        } else if (dataType == 5) {
            double[][] buffer = this.getDoubleData();
            double minDistance = 0.0;
            index = 0;
            for (int b = 0; b < numBands; ++b) {
                double delta = (double)pixel[b] - buffer[b][0];
                minDistance += delta * delta;
            }
            for (int i = 1; i < numEntries; ++i) {
                double distance = 0.0;
                for (int b = 0; b < numBands; ++b) {
                    double delta = (double)pixel[b] - buffer[b][i];
                    distance += delta * delta;
                }
                if (!(distance < minDistance)) continue;
                minDistance = distance;
                index = i;
            }
        } else {
            throw new RuntimeException(JaiI18N.getString("LookupTableJAI0"));
        }
        return index == -1 ? index : index + this.getOffset();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(SerializerFactory.getState(this.data));
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        Object object = in.readObject();
        SerializableState ss = (SerializableState)object;
        this.data = (DataBuffer)ss.getObject();
    }

    private void initOffsets(int nbands, int offset) {
        this.tableOffsets = new int[nbands];
        for (int i = 0; i < nbands; ++i) {
            this.tableOffsets[i] = offset;
        }
    }

    private void initOffsets(int nbands, int[] offset) {
        this.tableOffsets = new int[nbands];
        for (int i = 0; i < nbands; ++i) {
            this.tableOffsets[i] = offset[i];
        }
    }
}

