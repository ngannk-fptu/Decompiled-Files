/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codecimpl;

import com.sun.media.jai.codecimpl.TIFFFaxDecoder;

class TIFFFaxEncoder {
    private static final int WHITE = 0;
    private static final int BLACK = 1;
    private static byte[] byteTable = new byte[]{8, 7, 6, 6, 5, 5, 5, 5, 4, 4, 4, 4, 4, 4, 4, 4, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private static int[] termCodesBlack = new int[]{230686730, 0x40000003, -1073741822, -2147483646, 0x60000003, 0x30000004, 0x20000004, 402653189, 335544326, 0x10000006, 0x8000007, 0xA000007, 0xE000007, 0x4000008, 0x7000008, 0xC000009, 96469002, 0x600000A, 0x200000A, 216006667, 0xD00000B, 226492427, 115343371, 0x500000B, 48234507, 0x300000B, 0xCA0000C, 0xCB0000C, 0xCC0000C, 0xCD0000C, 109051916, 110100492, 111149068, 112197644, 220200972, 221249548, 222298124, 223346700, 224395276, 225443852, 0x6C0000C, 114294796, 228589580, 229638156, 88080396, 0x550000C, 90177548, 91226124, 104857612, 105906188, 85983244, 87031820, 37748748, 57671692, 58720268, 40894476, 41943052, 92274700, 93323276, 45088780, 0x2C0000C, 94371852, 0x660000C, 108003340};
    private static int[] termCodesWhite = new int[]{889192456, 469762054, 0x70000004, -2147483644, -1342177276, -1073741820, -536870908, -268435452, -1744830459, -1610612731, 939524101, 0x40000005, 0x20000006, 0xC000006, -805306362, -738197498, -1476395002, -1409286138, 1308622855, 402653191, 0x10000007, 771751943, 0x6000007, 0x8000007, 0x50000007, 1442840583, 637534215, 1207959559, 0x30000007, 0x2000008, 0x3000008, 436207624, 452984840, 301989896, 318767112, 335544328, 352321544, 369098760, 385875976, 0x28000008, 687865864, 704643080, 721420296, 738197512, 754974728, 0x4000008, 0x5000008, 0xA000008, 0xB000008, 1375731720, 1392508936, 1409286152, 0x55000008, 603979784, 620757000, 0x58000008, 1493172232, 1509949448, 1526726664, 1241513992, 1258291208, 838860808, 0x33000008, 872415240};
    private static int[] makeupCodesBlack = new int[]{0, 62914570, 0xC80000C, 0xC90000C, 95420428, 0x330000C, 54525964, 55574540, 56623117, 57147405, 38797325, 39321613, 39845901, 40370189, 59768845, 60293133, 60817421, 61341709, 61865997, 62390285, 42991629, 43515917, 44040205, 44564493, 0x2D0000D, 47710221, 52428813, 52953101, 0x100000B, 25165835, 27262987, 18874380, 19922956, 20971532, 22020108, 23068684, 24117260, 0x1C0000C, 30408716, 31457292, 32505868, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private static int[] makeupCodesWhite = new int[]{0, -671088635, -1879048187, 1543503878, 1845493767, 905969672, 922746888, 1677721608, 1694498824, 0x68000008, 1728053256, 0x66000009, 1719664649, 0x69000009, 1769996297, 1778384905, 1786773513, 1795162121, 1803550729, 1811939337, 1820327945, 1828716553, 1837105161, 1275068425, 1283457033, 1291845641, 0x60000006, 1300234249, 0x100000B, 25165835, 27262987, 18874380, 19922956, 20971532, 22020108, 23068684, 24117260, 0x1C0000C, 30408716, 31457292, 32505868, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private static int[] passMode = new int[]{0x10000004};
    private static int[] vertMode = new int[]{0x6000007, 0xC000006, 0x60000003, -2147483647, 0x40000003, 0x8000006, 0x4000007};
    private static int[] horzMode = new int[]{0x20000003};
    private static int[][] termCodes = new int[][]{termCodesWhite, termCodesBlack};
    private static int[][] makeupCodes = new int[][]{makeupCodesWhite, makeupCodesBlack};
    private static int[][] pass = new int[][]{passMode, passMode};
    private static int[][] vert = new int[][]{vertMode, vertMode};
    private static int[][] horz = new int[][]{horzMode, horzMode};
    private boolean inverseFill;
    private int bits;
    private int ndex;

    TIFFFaxEncoder(boolean inverseFill) {
        this.inverseFill = inverseFill;
    }

    private int nextState(byte[] data, int base, int bitOffset, int maxOffset) {
        int testbyte;
        int extra;
        if (data == null) {
            return maxOffset;
        }
        int next = base + (bitOffset >>> 3);
        if (next >= data.length) {
            return maxOffset;
        }
        int end = base + (maxOffset >>> 3);
        if (end == data.length) {
            --end;
        }
        if ((data[next] & 128 >>> (extra = bitOffset & 7)) != 0) {
            testbyte = ~data[next] & 255 >>> extra;
            while (next < end && testbyte == 0) {
                testbyte = ~data[++next] & 0xFF;
            }
        } else {
            testbyte = data[next] & 255 >>> extra;
            if (testbyte != 0) {
                bitOffset = (next - base) * 8 + byteTable[testbyte];
                return bitOffset < maxOffset ? bitOffset : maxOffset;
            }
            while (next < end) {
                if ((testbyte = data[++next] & 0xFF) == 0) continue;
                bitOffset = (next - base) * 8 + byteTable[testbyte];
                return bitOffset < maxOffset ? bitOffset : maxOffset;
            }
        }
        return (bitOffset = (next - base) * 8 + byteTable[testbyte]) < maxOffset ? bitOffset : maxOffset;
    }

    private void initBitBuf() {
        this.ndex = 0;
        this.bits = 0;
    }

    private int add1DBits(byte[] buf, int where, int count, int color) {
        int mask;
        int len = where;
        int sixtyfours = count >>> 6;
        count &= 0x3F;
        if (sixtyfours != 0) {
            while (sixtyfours > 40) {
                mask = makeupCodes[color][40];
                this.bits |= (mask & 0xFFF80000) >>> this.ndex;
                this.ndex += mask & 0xFFFF;
                while (this.ndex > 7) {
                    buf[len++] = (byte)(this.bits >>> 24);
                    this.bits <<= 8;
                    this.ndex -= 8;
                }
                sixtyfours -= 40;
            }
            mask = makeupCodes[color][sixtyfours];
            this.bits |= (mask & 0xFFF80000) >>> this.ndex;
            this.ndex += mask & 0xFFFF;
            while (this.ndex > 7) {
                buf[len++] = (byte)(this.bits >>> 24);
                this.bits <<= 8;
                this.ndex -= 8;
            }
        }
        mask = termCodes[color][count];
        this.bits |= (mask & 0xFFF80000) >>> this.ndex;
        this.ndex += mask & 0xFFFF;
        while (this.ndex > 7) {
            buf[len++] = (byte)(this.bits >>> 24);
            this.bits <<= 8;
            this.ndex -= 8;
        }
        return len - where;
    }

    private int add2DBits(byte[] buf, int where, int[][] mode, int entry) {
        int len = where;
        int color = 0;
        int mask = mode[color][entry];
        this.bits |= (mask & 0xFFF80000) >>> this.ndex;
        this.ndex += mask & 0xFFFF;
        while (this.ndex > 7) {
            buf[len++] = (byte)(this.bits >>> 24);
            this.bits <<= 8;
            this.ndex -= 8;
        }
        return len - where;
    }

    private int addEOL(boolean is1DMode, boolean addFill, boolean add1, byte[] buf, int where) {
        int len = where;
        if (addFill) {
            this.ndex += this.ndex <= 4 ? 4 - this.ndex : 12 - this.ndex;
        }
        if (is1DMode) {
            this.bits |= 0x100000 >>> this.ndex;
            this.ndex += 12;
        } else {
            this.bits |= (add1 ? 0x180000 : 0x100000) >>> this.ndex;
            this.ndex += 13;
        }
        while (this.ndex > 7) {
            buf[len++] = (byte)(this.bits >>> 24);
            this.bits <<= 8;
            this.ndex -= 8;
        }
        return len - where;
    }

    private int addEOFB(byte[] buf, int where) {
        int len = where;
        this.bits |= 0x100100 >>> this.ndex;
        this.ndex += 24;
        while (this.ndex > 0) {
            buf[len++] = (byte)(this.bits >>> 24);
            this.bits <<= 8;
            this.ndex -= 8;
        }
        return len - where;
    }

    private int encode1D(byte[] data, int rowOffset, int colOffset, int rowLength, byte[] compData, int compOffset) {
        int lineAddr = rowOffset;
        int bitIndex = colOffset;
        int last = bitIndex + rowLength;
        int outIndex = compOffset;
        int testbit = (data[lineAddr + (bitIndex >>> 3)] & 0xFF) >>> 7 - (bitIndex & 7) & 1;
        int currentColor = 1;
        if (testbit != 0) {
            outIndex += this.add1DBits(compData, outIndex, 0, 0);
        } else {
            currentColor = 0;
        }
        while (bitIndex < last) {
            int bitCount = this.nextState(data, lineAddr, bitIndex, last) - bitIndex;
            outIndex += this.add1DBits(compData, outIndex, bitCount, currentColor);
            bitIndex += bitCount;
            currentColor ^= 1;
        }
        return outIndex - compOffset;
    }

    synchronized int encodeRLE(byte[] data, int rowOffset, int colOffset, int rowLength, byte[] compData) {
        this.initBitBuf();
        int outIndex = this.encode1D(data, rowOffset, colOffset, rowLength, compData, 0);
        while (this.ndex > 0) {
            compData[outIndex++] = (byte)(this.bits >>> 24);
            this.bits <<= 8;
            this.ndex -= 8;
        }
        if (this.inverseFill) {
            byte[] flipTable = TIFFFaxDecoder.flipTable;
            for (int i = 0; i < outIndex; ++i) {
                compData[i] = flipTable[compData[i] & 0xFF];
            }
        }
        return outIndex;
    }

    synchronized int encodeT4(boolean is1DMode, boolean isEOLAligned, byte[] data, int lineStride, int colOffset, int width, int height, byte[] compData) {
        int i;
        byte[] refData = data;
        int lineAddr = 0;
        int outIndex = 0;
        this.initBitBuf();
        int KParameter = 2;
        for (int numRows = 0; numRows < height; ++numRows) {
            if (is1DMode || numRows % KParameter == 0) {
                outIndex += this.addEOL(is1DMode, isEOLAligned, true, compData, outIndex);
                outIndex += this.encode1D(data, lineAddr, colOffset, width, compData, outIndex);
            } else {
                outIndex += this.addEOL(is1DMode, isEOLAligned, false, compData, outIndex);
                int refAddr = lineAddr - lineStride;
                int a0 = colOffset;
                int last = a0 + width;
                int testbit = (data[lineAddr + (a0 >>> 3)] & 0xFF) >>> 7 - (a0 & 7) & 1;
                int a1 = testbit != 0 ? a0 : this.nextState(data, lineAddr, a0, last);
                testbit = (refData[refAddr + (a0 >>> 3)] & 0xFF) >>> 7 - (a0 & 7) & 1;
                int b1 = testbit != 0 ? a0 : this.nextState(refData, refAddr, a0, last);
                int color = 0;
                while (true) {
                    int b2;
                    if ((b2 = this.nextState(refData, refAddr, b1, last)) < a1) {
                        outIndex += this.add2DBits(compData, outIndex, pass, 0);
                        a0 = b2;
                    } else {
                        int tmp = b1 - a1 + 3;
                        if (tmp <= 6 && tmp >= 0) {
                            outIndex += this.add2DBits(compData, outIndex, vert, tmp);
                            a0 = a1;
                        } else {
                            int a2 = this.nextState(data, lineAddr, a1, last);
                            outIndex += this.add2DBits(compData, outIndex, horz, 0);
                            outIndex += this.add1DBits(compData, outIndex, a1 - a0, color);
                            outIndex += this.add1DBits(compData, outIndex, a2 - a1, color ^ 1);
                            a0 = a2;
                        }
                    }
                    if (a0 >= last) break;
                    color = (data[lineAddr + (a0 >>> 3)] & 0xFF) >>> 7 - (a0 & 7) & 1;
                    a1 = this.nextState(data, lineAddr, a0, last);
                    b1 = this.nextState(refData, refAddr, a0, last);
                    testbit = (refData[refAddr + (b1 >>> 3)] & 0xFF) >>> 7 - (b1 & 7) & 1;
                    if (testbit != color) continue;
                    b1 = this.nextState(refData, refAddr, b1, last);
                }
            }
            lineAddr += lineStride;
        }
        for (i = 0; i < 6; ++i) {
            outIndex += this.addEOL(is1DMode, isEOLAligned, true, compData, outIndex);
        }
        while (this.ndex > 0) {
            compData[outIndex++] = (byte)(this.bits >>> 24);
            this.bits <<= 8;
            this.ndex -= 8;
        }
        if (this.inverseFill) {
            for (i = 0; i < outIndex; ++i) {
                compData[i] = TIFFFaxDecoder.flipTable[compData[i] & 0xFF];
            }
        }
        return outIndex;
    }

    public synchronized int encodeT6(byte[] data, int lineStride, int colOffset, int width, int height, byte[] compData) {
        byte[] refData = null;
        int refAddr = 0;
        int lineAddr = 0;
        int outIndex = 0;
        this.initBitBuf();
        while (height-- != 0) {
            int a0 = colOffset;
            int last = a0 + width;
            int testbit = (data[lineAddr + (a0 >>> 3)] & 0xFF) >>> 7 - (a0 & 7) & 1;
            int a1 = testbit != 0 ? a0 : this.nextState(data, lineAddr, a0, last);
            testbit = refData == null ? 0 : (refData[refAddr + (a0 >>> 3)] & 0xFF) >>> 7 - (a0 & 7) & 1;
            int b1 = testbit != 0 ? a0 : this.nextState(refData, refAddr, a0, last);
            int color = 0;
            while (true) {
                int b2;
                if ((b2 = this.nextState(refData, refAddr, b1, last)) < a1) {
                    outIndex += this.add2DBits(compData, outIndex, pass, 0);
                    a0 = b2;
                } else {
                    int tmp = b1 - a1 + 3;
                    if (tmp <= 6 && tmp >= 0) {
                        outIndex += this.add2DBits(compData, outIndex, vert, tmp);
                        a0 = a1;
                    } else {
                        int a2 = this.nextState(data, lineAddr, a1, last);
                        outIndex += this.add2DBits(compData, outIndex, horz, 0);
                        outIndex += this.add1DBits(compData, outIndex, a1 - a0, color);
                        outIndex += this.add1DBits(compData, outIndex, a2 - a1, color ^ 1);
                        a0 = a2;
                    }
                }
                if (a0 >= last) break;
                color = (data[lineAddr + (a0 >>> 3)] & 0xFF) >>> 7 - (a0 & 7) & 1;
                a1 = this.nextState(data, lineAddr, a0, last);
                b1 = this.nextState(refData, refAddr, a0, last);
                testbit = refData == null ? 0 : (refData[refAddr + (b1 >>> 3)] & 0xFF) >>> 7 - (b1 & 7) & 1;
                if (testbit != color) continue;
                b1 = this.nextState(refData, refAddr, b1, last);
            }
            refData = data;
            refAddr = lineAddr;
            lineAddr += lineStride;
        }
        outIndex += this.addEOFB(compData, outIndex);
        if (this.inverseFill) {
            for (int i = 0; i < outIndex; ++i) {
                compData[i] = TIFFFaxDecoder.flipTable[compData[i] & 0xFF];
            }
        }
        return outIndex;
    }
}

