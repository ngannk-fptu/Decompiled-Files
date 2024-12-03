/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hpbf.model.qcbits;

import org.apache.poi.hpbf.model.qcbits.QCBit;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.StringUtil;

public abstract class QCPLCBit
extends QCBit {
    private static final int MAX_NUMBER_OF_PLCS = 1000;
    private final int numberOfPLCs;
    private final int typeOfPLCS;
    private int[] preData;
    private long[] plcValA;
    private long[] plcValB;

    private QCPLCBit(String thingType, String bitType, byte[] data) {
        super(thingType, bitType, data);
        this.numberOfPLCs = (int)LittleEndian.getUInt(data, 0);
        if (this.numberOfPLCs < 0) {
            throw new IllegalArgumentException("Invalid number of PLCs: " + this.numberOfPLCs);
        }
        this.typeOfPLCS = (int)LittleEndian.getUInt(data, 4);
        IOUtils.safelyAllocateCheck(this.numberOfPLCs, 1000);
        this.plcValA = new long[this.numberOfPLCs];
        this.plcValB = new long[this.numberOfPLCs];
    }

    public int getNumberOfPLCs() {
        return this.numberOfPLCs;
    }

    public int getTypeOfPLCS() {
        return this.typeOfPLCS;
    }

    public int[] getPreData() {
        return this.preData;
    }

    public long[] getPlcValA() {
        return this.plcValA;
    }

    public long[] getPlcValB() {
        return this.plcValB;
    }

    final void setPreData(int[] preData) {
        this.preData = (int[])preData.clone();
    }

    final void setPlcValA(long[] plcValA) {
        this.plcValA = (long[])plcValA.clone();
    }

    final void setPlcValB(long[] plcValB) {
        this.plcValB = (long[])plcValB.clone();
    }

    public static QCPLCBit createQCPLCBit(String thingType, String bitType, byte[] data) {
        int type = (int)LittleEndian.getUInt(data, 4);
        switch (type) {
            case 0: {
                return new Type0(thingType, bitType, data);
            }
            case 4: {
                return new Type4(thingType, bitType, data);
            }
            case 8: {
                return new Type8(thingType, bitType, data);
            }
            case 12: {
                return new Type12(thingType, bitType, data);
            }
        }
        throw new IllegalArgumentException("Sorry, I don't know how to deal with PLCs of type " + type);
    }

    public static class Type12
    extends QCPLCBit {
        private final String[] hyperlinks;
        private static final int oneStartsAt = 76;
        private static final int twoStartsAt = 104;
        private static final int threePlusIncrement = 22;

        private Type12(String thingType, String bitType, byte[] data) {
            super(thingType, bitType, data);
            int i;
            int cntPlcs = this.getNumberOfPLCs();
            this.hyperlinks = new String[data.length == 52 ? 0 : cntPlcs];
            int[] preData = new int[1 + cntPlcs + 1];
            for (int i2 = 0; i2 < preData.length; ++i2) {
                preData[i2] = (int)LittleEndian.getUInt(data, 8 + i2 * 4);
            }
            this.setPreData(preData);
            int at = 12 + cntPlcs * 4 + 4;
            int until = 52;
            if (cntPlcs == 1 && this.hyperlinks.length == 1) {
                until = 76;
            } else if (cntPlcs >= 2) {
                until = 104 + (cntPlcs - 2) * 22;
            }
            long[] plcValA = new long[(until - at) / 2];
            long[] plcValB = new long[]{};
            for (i = 0; i < plcValA.length; ++i) {
                plcValA[i] = LittleEndian.getUShort(data, at + i * 2);
            }
            this.setPlcValA(plcValA);
            this.setPlcValB(plcValB);
            at = until;
            for (i = 0; i < this.hyperlinks.length; ++i) {
                int len = LittleEndian.getUShort(data, at);
                int first = LittleEndian.getUShort(data, at + 2);
                if (first == 0) {
                    this.hyperlinks[i] = "";
                    at += len;
                    continue;
                }
                this.hyperlinks[i] = StringUtil.getFromUnicodeLE(data, at + 2, len);
                at += 2 + 2 * len;
            }
        }

        public int getNumberOfHyperlinks() {
            return this.hyperlinks.length;
        }

        public String getHyperlink(int number) {
            return this.hyperlinks[number];
        }

        public int getTextStartAt(int number) {
            return this.getPreData()[1 + number];
        }

        public int getAllTextEndAt() {
            return this.getPreData()[this.getNumberOfPLCs() + 1];
        }
    }

    public static class Type8
    extends QCPLCBit {
        private Type8(String thingType, String bitType, byte[] data) {
            super(thingType, bitType, data);
            int[] preData = new int[]{LittleEndian.getUShort(data, 8), LittleEndian.getUShort(data, 10), LittleEndian.getUShort(data, 12), LittleEndian.getUShort(data, 14), LittleEndian.getUShort(data, 16), LittleEndian.getUShort(data, 18), LittleEndian.getUShort(data, 20)};
            this.setPreData(preData);
            int cntPlcs = this.getNumberOfPLCs();
            long[] plcValA = new long[cntPlcs];
            long[] plcValB = new long[cntPlcs];
            for (int i = 0; i < cntPlcs; ++i) {
                plcValA[i] = LittleEndian.getUInt(data, 22 + 8 * i);
                plcValB[i] = LittleEndian.getUInt(data, 22 + 8 * i + 4);
            }
            this.setPlcValA(plcValA);
            this.setPlcValB(plcValB);
        }
    }

    public static class Type4
    extends QCPLCBit {
        private Type4(String thingType, String bitType, byte[] data) {
            super(thingType, bitType, data);
            int[] preData = new int[]{LittleEndian.getUShort(data, 8), LittleEndian.getUShort(data, 10), LittleEndian.getUShort(data, 12), LittleEndian.getUShort(data, 14)};
            this.setPreData(preData);
            int cntPlcs = this.getNumberOfPLCs();
            long[] plcValA = new long[cntPlcs];
            long[] plcValB = new long[cntPlcs];
            for (int i = 0; i < cntPlcs; ++i) {
                plcValA[i] = LittleEndian.getUInt(data, 16 + 8 * i);
                plcValB[i] = LittleEndian.getUInt(data, 16 + 8 * i + 4);
            }
            this.setPlcValA(plcValA);
            this.setPlcValB(plcValB);
        }
    }

    public static class Type0
    extends QCPLCBit {
        private Type0(String thingType, String bitType, byte[] data) {
            super(thingType, bitType, data);
            int[] preData = new int[]{LittleEndian.getUShort(data, 8), LittleEndian.getUShort(data, 10), LittleEndian.getUShort(data, 12), LittleEndian.getUShort(data, 14)};
            this.setPreData(preData);
            int cntPlcs = this.getNumberOfPLCs();
            long[] plcValA = new long[cntPlcs];
            long[] plcValB = new long[cntPlcs];
            for (int i = 0; i < cntPlcs; ++i) {
                plcValA[i] = LittleEndian.getUShort(data, 16 + 4 * i);
                plcValB[i] = LittleEndian.getUShort(data, 16 + 4 * i + 2);
            }
            this.setPlcValA(plcValA);
            this.setPlcValB(plcValB);
        }
    }
}

