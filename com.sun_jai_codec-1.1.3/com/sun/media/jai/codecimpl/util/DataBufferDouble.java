/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codecimpl.util;

import com.sun.media.jai.codecimpl.util.JaiI18N;
import java.awt.image.DataBuffer;

public class DataBufferDouble
extends DataBuffer {
    protected double[][] bankdata;
    protected double[] data;

    public DataBufferDouble(int size) {
        super(5, size);
        this.data = new double[size];
        this.bankdata = new double[1][];
        this.bankdata[0] = this.data;
    }

    public DataBufferDouble(int size, int numBanks) {
        super(5, size, numBanks);
        this.bankdata = new double[numBanks][];
        for (int i = 0; i < numBanks; ++i) {
            this.bankdata[i] = new double[size];
        }
        this.data = this.bankdata[0];
    }

    public DataBufferDouble(double[] dataArray, int size) {
        super(5, size);
        if (dataArray.length < size) {
            throw new RuntimeException(JaiI18N.getString("DataBuffer0"));
        }
        this.data = dataArray;
        this.bankdata = new double[1][];
        this.bankdata[0] = this.data;
    }

    public DataBufferDouble(double[] dataArray, int size, int offset) {
        super(5, size, 1, offset);
        if (dataArray.length < size) {
            throw new RuntimeException(JaiI18N.getString("DataBuffer1"));
        }
        this.data = dataArray;
        this.bankdata = new double[1][];
        this.bankdata[0] = this.data;
    }

    public DataBufferDouble(double[][] dataArray, int size) {
        super(5, size, dataArray.length);
        this.bankdata = dataArray;
        this.data = this.bankdata[0];
    }

    public DataBufferDouble(double[][] dataArray, int size, int[] offsets) {
        super(5, size, dataArray.length, offsets);
        this.bankdata = dataArray;
        this.data = this.bankdata[0];
    }

    public double[] getData() {
        return this.data;
    }

    public double[] getData(int bank) {
        return this.bankdata[bank];
    }

    public double[][] getBankData() {
        return this.bankdata;
    }

    public int getElem(int i) {
        return (int)this.data[i + this.offset];
    }

    public int getElem(int bank, int i) {
        return (int)this.bankdata[bank][i + this.offsets[bank]];
    }

    public void setElem(int i, int val) {
        this.data[i + this.offset] = val;
    }

    public void setElem(int bank, int i, int val) {
        this.bankdata[bank][i + this.offsets[bank]] = val;
    }

    public float getElemFloat(int i) {
        return (float)this.data[i + this.offset];
    }

    public float getElemFloat(int bank, int i) {
        return (float)this.bankdata[bank][i + this.offsets[bank]];
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
        this.data[i + this.offset] = val;
    }

    public void setElemDouble(int bank, int i, double val) {
        this.bankdata[bank][i + this.offsets[bank]] = val;
    }
}

