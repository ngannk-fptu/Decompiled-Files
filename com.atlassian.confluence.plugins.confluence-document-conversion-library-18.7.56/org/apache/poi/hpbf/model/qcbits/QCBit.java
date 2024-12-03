/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hpbf.model.qcbits;

public abstract class QCBit {
    private String thingType;
    private String bitType;
    private byte[] data;
    private int optA;
    private int optB;
    private int optC;
    private int dataOffset;

    public QCBit(String thingType, String bitType, byte[] data) {
        this.thingType = thingType;
        this.bitType = bitType;
        this.data = (byte[])data.clone();
    }

    public String getThingType() {
        return this.thingType;
    }

    public String getBitType() {
        return this.bitType;
    }

    public final byte[] getData() {
        return this.data;
    }

    protected final void setData(byte[] data) {
        this.data = (byte[])data.clone();
    }

    public int getOptA() {
        return this.optA;
    }

    public void setOptA(int optA) {
        this.optA = optA;
    }

    public int getOptB() {
        return this.optB;
    }

    public void setOptB(int optB) {
        this.optB = optB;
    }

    public int getOptC() {
        return this.optC;
    }

    public void setOptC(int optC) {
        this.optC = optC;
    }

    public int getDataOffset() {
        return this.dataOffset;
    }

    public void setDataOffset(int offset) {
        this.dataOffset = offset;
    }

    public int getLength() {
        return this.data.length;
    }
}

