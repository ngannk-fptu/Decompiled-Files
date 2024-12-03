/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codecimpl.util;

import com.sun.media.jai.codecimpl.util.DataBufferUtils;
import com.sun.media.jai.codecimpl.util.JaiI18N;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.SampleModel;

public class ComponentSampleModelJAI
extends ComponentSampleModel {
    public ComponentSampleModelJAI(int dataType, int w, int h, int pixelStride, int scanlineStride, int[] bandOffsets) {
        super(dataType, w, h, pixelStride, scanlineStride, bandOffsets);
    }

    public ComponentSampleModelJAI(int dataType, int w, int h, int pixelStride, int scanlineStride, int[] bankIndices, int[] bandOffsets) {
        super(dataType, w, h, pixelStride, scanlineStride, bankIndices, bandOffsets);
    }

    private long getBufferSize() {
        int maxBandOff = this.bandOffsets[0];
        for (int i = 1; i < this.bandOffsets.length; ++i) {
            maxBandOff = Math.max(maxBandOff, this.bandOffsets[i]);
        }
        long size = 0L;
        if (maxBandOff >= 0) {
            size += (long)(maxBandOff + 1);
        }
        if (this.pixelStride > 0) {
            size += (long)(this.pixelStride * (this.width - 1));
        }
        if (this.scanlineStride > 0) {
            size += (long)(this.scanlineStride * (this.height - 1));
        }
        return size;
    }

    private int[] JAIorderBands(int[] orig, int step) {
        int i;
        int[] map = new int[orig.length];
        int[] ret = new int[orig.length];
        for (i = 0; i < map.length; ++i) {
            map[i] = i;
        }
        for (i = 0; i < ret.length; ++i) {
            int index = i;
            for (int j = i + 1; j < ret.length; ++j) {
                if (orig[map[index]] <= orig[map[j]]) continue;
                index = j;
            }
            ret[map[index]] = i * step;
            map[index] = map[i];
        }
        return ret;
    }

    public SampleModel createCompatibleSampleModel(int w, int h) {
        int i;
        int[] bandOff;
        Object ret = null;
        int minBandOff = this.bandOffsets[0];
        int maxBandOff = this.bandOffsets[0];
        for (int i2 = 1; i2 < this.bandOffsets.length; ++i2) {
            minBandOff = Math.min(minBandOff, this.bandOffsets[i2]);
            maxBandOff = Math.max(maxBandOff, this.bandOffsets[i2]);
        }
        int bands = this.bandOffsets.length;
        int pStride = Math.abs(this.pixelStride);
        int lStride = Math.abs(this.scanlineStride);
        int bStride = Math.abs(maxBandOff -= minBandOff);
        if (pStride > lStride) {
            if (pStride > bStride) {
                if (lStride > bStride) {
                    bandOff = new int[this.bandOffsets.length];
                    for (i = 0; i < bands; ++i) {
                        bandOff[i] = this.bandOffsets[i] - minBandOff;
                    }
                    lStride = bStride + 1;
                    pStride = lStride * h;
                } else {
                    bandOff = this.JAIorderBands(this.bandOffsets, lStride * h);
                    pStride = bands * lStride * h;
                }
            } else {
                pStride = lStride * h;
                bandOff = this.JAIorderBands(this.bandOffsets, pStride * w);
            }
        } else if (pStride > bStride) {
            bandOff = new int[this.bandOffsets.length];
            for (i = 0; i < bands; ++i) {
                bandOff[i] = this.bandOffsets[i] - minBandOff;
            }
            pStride = bStride + 1;
            lStride = pStride * w;
        } else if (lStride > bStride) {
            bandOff = this.JAIorderBands(this.bandOffsets, pStride * w);
            lStride = bands * pStride * w;
        } else {
            lStride = pStride * w;
            bandOff = this.JAIorderBands(this.bandOffsets, lStride * h);
        }
        int base = 0;
        if (this.scanlineStride < 0) {
            base += lStride * h;
            lStride *= -1;
        }
        if (this.pixelStride < 0) {
            base += pStride * w;
            pStride *= -1;
        }
        int i3 = 0;
        while (i3 < bands) {
            int n = i3++;
            bandOff[n] = bandOff[n] + base;
        }
        return new ComponentSampleModelJAI(this.dataType, w, h, pStride, lStride, this.bankIndices, bandOff);
    }

    public SampleModel createSubsetSampleModel(int[] bands) {
        int[] newBankIndices = new int[bands.length];
        int[] newBandOffsets = new int[bands.length];
        for (int i = 0; i < bands.length; ++i) {
            int b = bands[i];
            newBankIndices[i] = this.bankIndices[b];
            newBandOffsets[i] = this.bandOffsets[b];
        }
        return new ComponentSampleModelJAI(this.dataType, this.width, this.height, this.pixelStride, this.scanlineStride, newBankIndices, newBandOffsets);
    }

    public DataBuffer createDataBuffer() {
        DataBuffer dataBuffer = null;
        int size = (int)this.getBufferSize();
        switch (this.dataType) {
            case 0: {
                dataBuffer = new DataBufferByte(size, this.numBanks);
                break;
            }
            case 1: {
                dataBuffer = new DataBufferUShort(size, this.numBanks);
                break;
            }
            case 3: {
                dataBuffer = new DataBufferInt(size, this.numBanks);
                break;
            }
            case 2: {
                dataBuffer = new DataBufferShort(size, this.numBanks);
                break;
            }
            case 4: {
                dataBuffer = DataBufferUtils.createDataBufferFloat(size, this.numBanks);
                break;
            }
            case 5: {
                dataBuffer = DataBufferUtils.createDataBufferDouble(size, this.numBanks);
                break;
            }
            default: {
                throw new RuntimeException(JaiI18N.getString("RasterFactory3"));
            }
        }
        return dataBuffer;
    }

    public Object getDataElements(int x, int y, Object obj, DataBuffer data) {
        int type = this.getTransferType();
        int numDataElems = this.getNumDataElements();
        int pixelOffset = y * this.scanlineStride + x * this.pixelStride;
        switch (type) {
            case 0: {
                byte[] bdata = obj == null ? new byte[numDataElems] : (byte[])obj;
                for (int i = 0; i < numDataElems; ++i) {
                    bdata[i] = (byte)data.getElem(this.bankIndices[i], pixelOffset + this.bandOffsets[i]);
                }
                obj = bdata;
                break;
            }
            case 1: {
                short[] usdata = obj == null ? new short[numDataElems] : (short[])obj;
                for (int i = 0; i < numDataElems; ++i) {
                    usdata[i] = (short)data.getElem(this.bankIndices[i], pixelOffset + this.bandOffsets[i]);
                }
                obj = usdata;
                break;
            }
            case 3: {
                int[] idata = obj == null ? new int[numDataElems] : (int[])obj;
                for (int i = 0; i < numDataElems; ++i) {
                    idata[i] = data.getElem(this.bankIndices[i], pixelOffset + this.bandOffsets[i]);
                }
                obj = idata;
                break;
            }
            case 2: {
                short[] sdata = obj == null ? new short[numDataElems] : (short[])obj;
                for (int i = 0; i < numDataElems; ++i) {
                    sdata[i] = (short)data.getElem(this.bankIndices[i], pixelOffset + this.bandOffsets[i]);
                }
                obj = sdata;
                break;
            }
            case 4: {
                float[] fdata = obj == null ? new float[numDataElems] : (float[])obj;
                for (int i = 0; i < numDataElems; ++i) {
                    fdata[i] = data.getElemFloat(this.bankIndices[i], pixelOffset + this.bandOffsets[i]);
                }
                obj = fdata;
                break;
            }
            case 5: {
                double[] ddata = obj == null ? new double[numDataElems] : (double[])obj;
                for (int i = 0; i < numDataElems; ++i) {
                    ddata[i] = data.getElemDouble(this.bankIndices[i], pixelOffset + this.bandOffsets[i]);
                }
                obj = ddata;
                break;
            }
            default: {
                throw new RuntimeException(JaiI18N.getString("RasterFactory3"));
            }
        }
        return obj;
    }

    public Object getDataElements(int x, int y, int w, int h, Object obj, DataBuffer data) {
        int type = this.getTransferType();
        int numDataElems = this.getNumDataElements();
        int cnt = 0;
        Object o = null;
        switch (type) {
            case 0: {
                byte[] bdata = obj == null ? new byte[numDataElems * w * h] : (byte[])obj;
                for (int i = y; i < y + h; ++i) {
                    for (int j = x; j < x + w; ++j) {
                        o = this.getDataElements(j, i, o, data);
                        byte[] btemp = (byte[])o;
                        for (int k = 0; k < numDataElems; ++k) {
                            bdata[cnt++] = btemp[k];
                        }
                    }
                }
                obj = bdata;
                break;
            }
            case 1: {
                short[] usdata = obj == null ? new short[numDataElems * w * h] : (short[])obj;
                for (int i = y; i < y + h; ++i) {
                    for (int j = x; j < x + w; ++j) {
                        o = this.getDataElements(j, i, o, data);
                        short[] ustemp = (short[])o;
                        for (int k = 0; k < numDataElems; ++k) {
                            usdata[cnt++] = ustemp[k];
                        }
                    }
                }
                obj = usdata;
                break;
            }
            case 3: {
                int[] idata = obj == null ? new int[numDataElems * w * h] : (int[])obj;
                for (int i = y; i < y + h; ++i) {
                    for (int j = x; j < x + w; ++j) {
                        o = this.getDataElements(j, i, o, data);
                        int[] itemp = (int[])o;
                        for (int k = 0; k < numDataElems; ++k) {
                            idata[cnt++] = itemp[k];
                        }
                    }
                }
                obj = idata;
                break;
            }
            case 2: {
                short[] sdata = obj == null ? new short[numDataElems * w * h] : (short[])obj;
                for (int i = y; i < y + h; ++i) {
                    for (int j = x; j < x + w; ++j) {
                        o = this.getDataElements(j, i, o, data);
                        short[] stemp = (short[])o;
                        for (int k = 0; k < numDataElems; ++k) {
                            sdata[cnt++] = stemp[k];
                        }
                    }
                }
                obj = sdata;
                break;
            }
            case 4: {
                float[] fdata = obj == null ? new float[numDataElems * w * h] : (float[])obj;
                for (int i = y; i < y + h; ++i) {
                    for (int j = x; j < x + w; ++j) {
                        o = this.getDataElements(j, i, o, data);
                        float[] ftemp = (float[])o;
                        for (int k = 0; k < numDataElems; ++k) {
                            fdata[cnt++] = ftemp[k];
                        }
                    }
                }
                obj = fdata;
                break;
            }
            case 5: {
                double[] ddata = obj == null ? new double[numDataElems * w * h] : (double[])obj;
                for (int i = y; i < y + h; ++i) {
                    for (int j = x; j < x + w; ++j) {
                        o = this.getDataElements(j, i, o, data);
                        double[] dtemp = (double[])o;
                        for (int k = 0; k < numDataElems; ++k) {
                            ddata[cnt++] = dtemp[k];
                        }
                    }
                }
                obj = ddata;
                break;
            }
            default: {
                throw new RuntimeException(JaiI18N.getString("RasterFactory3"));
            }
        }
        return obj;
    }

    public void setDataElements(int x, int y, Object obj, DataBuffer data) {
        int type = this.getTransferType();
        int numDataElems = this.getNumDataElements();
        int pixelOffset = y * this.scanlineStride + x * this.pixelStride;
        switch (type) {
            case 0: {
                byte[] barray = (byte[])obj;
                for (int i = 0; i < numDataElems; ++i) {
                    data.setElem(this.bankIndices[i], pixelOffset + this.bandOffsets[i], barray[i] & 0xFF);
                }
                break;
            }
            case 1: {
                short[] usarray = (short[])obj;
                for (int i = 0; i < numDataElems; ++i) {
                    data.setElem(this.bankIndices[i], pixelOffset + this.bandOffsets[i], usarray[i] & 0xFFFF);
                }
                break;
            }
            case 3: {
                int[] iarray = (int[])obj;
                for (int i = 0; i < numDataElems; ++i) {
                    data.setElem(this.bankIndices[i], pixelOffset + this.bandOffsets[i], iarray[i]);
                }
                break;
            }
            case 2: {
                short[] sarray = (short[])obj;
                for (int i = 0; i < numDataElems; ++i) {
                    data.setElem(this.bankIndices[i], pixelOffset + this.bandOffsets[i], sarray[i]);
                }
                break;
            }
            case 4: {
                float[] farray = (float[])obj;
                for (int i = 0; i < numDataElems; ++i) {
                    data.setElemFloat(this.bankIndices[i], pixelOffset + this.bandOffsets[i], farray[i]);
                }
                break;
            }
            case 5: {
                double[] darray = (double[])obj;
                for (int i = 0; i < numDataElems; ++i) {
                    data.setElemDouble(this.bankIndices[i], pixelOffset + this.bandOffsets[i], darray[i]);
                }
                break;
            }
            default: {
                throw new RuntimeException(JaiI18N.getString("RasterFactory3"));
            }
        }
    }

    public void setDataElements(int x, int y, int w, int h, Object obj, DataBuffer data) {
        int cnt = 0;
        Object o = null;
        int type = this.getTransferType();
        int numDataElems = this.getNumDataElements();
        switch (type) {
            case 0: {
                byte[] barray = (byte[])obj;
                byte[] btemp = new byte[numDataElems];
                for (int i = y; i < y + h; ++i) {
                    for (int j = x; j < x + w; ++j) {
                        for (int k = 0; k < numDataElems; ++k) {
                            btemp[k] = barray[cnt++];
                        }
                        this.setDataElements(j, i, btemp, data);
                    }
                }
                break;
            }
            case 1: {
                short[] usarray = (short[])obj;
                short[] ustemp = new short[numDataElems];
                for (int i = y; i < y + h; ++i) {
                    for (int j = x; j < x + w; ++j) {
                        for (int k = 0; k < numDataElems; ++k) {
                            ustemp[k] = usarray[cnt++];
                        }
                        this.setDataElements(j, i, ustemp, data);
                    }
                }
                break;
            }
            case 3: {
                int[] iArray = (int[])obj;
                int[] itemp = new int[numDataElems];
                for (int i = y; i < y + h; ++i) {
                    for (int j = x; j < x + w; ++j) {
                        for (int k = 0; k < numDataElems; ++k) {
                            itemp[k] = iArray[cnt++];
                        }
                        this.setDataElements(j, i, itemp, data);
                    }
                }
                break;
            }
            case 2: {
                short[] sArray = (short[])obj;
                short[] stemp = new short[numDataElems];
                for (int i = y; i < y + h; ++i) {
                    for (int j = x; j < x + w; ++j) {
                        for (int k = 0; k < numDataElems; ++k) {
                            stemp[k] = sArray[cnt++];
                        }
                        this.setDataElements(j, i, stemp, data);
                    }
                }
                break;
            }
            case 4: {
                float[] fArray = (float[])obj;
                float[] ftemp = new float[numDataElems];
                for (int i = y; i < y + h; ++i) {
                    for (int j = x; j < x + w; ++j) {
                        for (int k = 0; k < numDataElems; ++k) {
                            ftemp[k] = fArray[cnt++];
                        }
                        this.setDataElements(j, i, ftemp, data);
                    }
                }
                break;
            }
            case 5: {
                double[] dArray = (double[])obj;
                double[] dtemp = new double[numDataElems];
                for (int i = y; i < y + h; ++i) {
                    for (int j = x; j < x + w; ++j) {
                        for (int k = 0; k < numDataElems; ++k) {
                            dtemp[k] = dArray[cnt++];
                        }
                        this.setDataElements(j, i, dtemp, data);
                    }
                }
                break;
            }
            default: {
                throw new RuntimeException(JaiI18N.getString("RasterFactory3"));
            }
        }
    }

    public void setSample(int x, int y, int b, float s, DataBuffer data) {
        data.setElemFloat(this.bankIndices[b], y * this.scanlineStride + x * this.pixelStride + this.bandOffsets[b], s);
    }

    public float getSampleFloat(int x, int y, int b, DataBuffer data) {
        float sample = data.getElemFloat(this.bankIndices[b], y * this.scanlineStride + x * this.pixelStride + this.bandOffsets[b]);
        return sample;
    }

    public void setSample(int x, int y, int b, double s, DataBuffer data) {
        data.setElemDouble(this.bankIndices[b], y * this.scanlineStride + x * this.pixelStride + this.bandOffsets[b], s);
    }

    public double getSampleDouble(int x, int y, int b, DataBuffer data) {
        double sample = data.getElemDouble(this.bankIndices[b], y * this.scanlineStride + x * this.pixelStride + this.bandOffsets[b]);
        return sample;
    }

    public double[] getPixels(int x, int y, int w, int h, double[] dArray, DataBuffer data) {
        int Offset = 0;
        double[] pixels = dArray != null ? dArray : new double[this.numBands * w * h];
        for (int i = y; i < h + y; ++i) {
            for (int j = x; j < w + x; ++j) {
                for (int k = 0; k < this.numBands; ++k) {
                    pixels[Offset++] = this.getSampleDouble(j, i, k, data);
                }
            }
        }
        return pixels;
    }

    public String toString() {
        String ret = "ComponentSampleModelJAI:   dataType=" + this.getDataType() + "  numBands=" + this.getNumBands() + "  width=" + this.getWidth() + "  height=" + this.getHeight() + "  bandOffsets=[ ";
        for (int i = 0; i < this.numBands; ++i) {
            ret = ret + this.getBandOffsets()[i] + " ";
        }
        ret = ret + "]";
        return ret;
    }
}

