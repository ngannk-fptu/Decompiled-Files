/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codecimpl.util;

import com.sun.media.jai.codecimpl.util.JaiI18N;
import java.awt.image.DataBuffer;

public class DataBufferFloat
extends DataBuffer {
    protected float[][] bankdata;
    protected float[] data;

    public DataBufferFloat(int size) {
        super(4, size);
        this.data = new float[size];
        this.bankdata = new float[1][];
        this.bankdata[0] = this.data;
    }

    public DataBufferFloat(int size, int numBanks) {
        super(4, size, numBanks);
        this.bankdata = new float[numBanks][];
        for (int i = 0; i < numBanks; ++i) {
            this.bankdata[i] = new float[size];
        }
        this.data = this.bankdata[0];
    }

    public DataBufferFloat(float[] dataArray, int size) {
        super(4, size);
        if (dataArray.length < size) {
            throw new RuntimeException(JaiI18N.getString("DataBuffer0"));
        }
        this.data = dataArray;
        this.bankdata = new float[1][];
        this.bankdata[0] = this.data;
    }

    public DataBufferFloat(float[] dataArray, int size, int offset) {
        super(4, size, 1, offset);
        if (dataArray.length < size) {
            throw new RuntimeException(JaiI18N.getString("DataBuffer1"));
        }
        this.data = dataArray;
        this.bankdata = new float[1][];
        this.bankdata[0] = this.data;
    }

    public DataBufferFloat(float[][] dataArray, int size) {
        super(4, size, dataArray.length);
        this.bankdata = dataArray;
        this.data = this.bankdata[0];
    }

    public DataBufferFloat(float[][] dataArray, int size, int[] offsets) {
        super(4, size, dataArray.length, offsets);
        this.bankdata = dataArray;
        this.data = this.bankdata[0];
    }

    public float[] getData() {
        return this.data;
    }

    public float[] getData(int bank) {
        return this.bankdata[bank];
    }

    public float[][] getBankData() {
        return this.bankdata;
    }

    public int getElem(int i) {
        return Math.round(this.data[i + this.offset]);
    }

    public int getElem(int bank, int i) {
        return Math.round(this.bankdata[bank][i + this.offsets[bank]]);
    }

    public void setElem(int i, int val) {
        this.data[i + this.offset] = val;
    }

    public void setElem(int bank, int i, int val) {
        this.bankdata[bank][i + this.offsets[bank]] = val;
    }

    public float getElemFloat(int i) {
        return this.data[i + this.offset];
    }

    public float getElemFloat(int bank, int i) {
        return this.bankdata[bank][i + this.offsets[bank]];
    }

    public void setElemFloat(int i, float val) {
        this.data[i + this.offset] = val;
    }

    public void setElemFloat(int bank, int i, float val) {
        this.bankdata[bank][i + this.offsets[bank]] = val;
    }

    public double getElemDouble(int i) {
        return this.data[i + this.offset];
    }

    public double getElemDouble(int bank, int i) {
        return this.bankdata[bank][i + this.offsets[bank]];
    }

    public void setElemDouble(int i, double val) {
        this.data[i + this.offset] = (float)val;
    }

    public void setElemDouble(int bank, int i, double val) {
        this.bankdata[bank][i + this.offsets[bank]] = (float)val;
    }
}

