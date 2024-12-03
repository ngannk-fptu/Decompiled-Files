/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.decode;

public class CCITTFaxDecoder {
    static int[] table1 = new int[]{0, 1, 3, 7, 15, 31, 63, 127, 255};
    static int[] table2 = new int[]{0, 128, 192, 224, 240, 248, 252, 254, 255};
    static byte[] flipTable = new byte[]{0, -128, 64, -64, 32, -96, 96, -32, 16, -112, 80, -48, 48, -80, 112, -16, 8, -120, 72, -56, 40, -88, 104, -24, 24, -104, 88, -40, 56, -72, 120, -8, 4, -124, 68, -60, 36, -92, 100, -28, 20, -108, 84, -44, 52, -76, 116, -12, 12, -116, 76, -52, 44, -84, 108, -20, 28, -100, 92, -36, 60, -68, 124, -4, 2, -126, 66, -62, 34, -94, 98, -30, 18, -110, 82, -46, 50, -78, 114, -14, 10, -118, 74, -54, 42, -86, 106, -22, 26, -102, 90, -38, 58, -70, 122, -6, 6, -122, 70, -58, 38, -90, 102, -26, 22, -106, 86, -42, 54, -74, 118, -10, 14, -114, 78, -50, 46, -82, 110, -18, 30, -98, 94, -34, 62, -66, 126, -2, 1, -127, 65, -63, 33, -95, 97, -31, 17, -111, 81, -47, 49, -79, 113, -15, 9, -119, 73, -55, 41, -87, 105, -23, 25, -103, 89, -39, 57, -71, 121, -7, 5, -123, 69, -59, 37, -91, 101, -27, 21, -107, 85, -43, 53, -75, 117, -11, 13, -115, 77, -51, 45, -83, 109, -19, 29, -99, 93, -35, 61, -67, 125, -3, 3, -125, 67, -61, 35, -93, 99, -29, 19, -109, 83, -45, 51, -77, 115, -13, 11, -117, 75, -53, 43, -85, 107, -21, 27, -101, 91, -37, 59, -69, 123, -5, 7, -121, 71, -57, 39, -89, 103, -25, 23, -105, 87, -41, 55, -73, 119, -9, 15, -113, 79, -49, 47, -81, 111, -17, 31, -97, 95, -33, 63, -65, 127, -1};
    static short[] white = new short[]{6430, 6400, 6400, 6400, 3225, 3225, 3225, 3225, 944, 944, 944, 944, 976, 976, 976, 976, 1456, 1456, 1456, 1456, 1488, 1488, 1488, 1488, 718, 718, 718, 718, 718, 718, 718, 718, 750, 750, 750, 750, 750, 750, 750, 750, 1520, 1520, 1520, 1520, 1552, 1552, 1552, 1552, 428, 428, 428, 428, 428, 428, 428, 428, 428, 428, 428, 428, 428, 428, 428, 428, 654, 654, 654, 654, 654, 654, 654, 654, 1072, 1072, 1072, 1072, 1104, 1104, 1104, 1104, 1136, 1136, 1136, 1136, 1168, 1168, 1168, 1168, 1200, 1200, 1200, 1200, 1232, 1232, 1232, 1232, 622, 622, 622, 622, 622, 622, 622, 622, 1008, 1008, 1008, 1008, 1040, 1040, 1040, 1040, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 396, 396, 396, 396, 396, 396, 396, 396, 396, 396, 396, 396, 396, 396, 396, 396, 1712, 1712, 1712, 1712, 1744, 1744, 1744, 1744, 846, 846, 846, 846, 846, 846, 846, 846, 1264, 1264, 1264, 1264, 1296, 1296, 1296, 1296, 1328, 1328, 1328, 1328, 1360, 1360, 1360, 1360, 1392, 1392, 1392, 1392, 1424, 1424, 1424, 1424, 686, 686, 686, 686, 686, 686, 686, 686, 910, 910, 910, 910, 910, 910, 910, 910, 1968, 1968, 1968, 1968, 2000, 2000, 2000, 2000, 2032, 2032, 2032, 2032, 16, 16, 16, 16, 10257, 10257, 10257, 10257, 12305, 12305, 12305, 12305, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 878, 878, 878, 878, 878, 878, 878, 878, 1904, 1904, 1904, 1904, 1936, 1936, 1936, 1936, -18413, -18413, -16365, -16365, -14317, -14317, -10221, -10221, 590, 590, 590, 590, 590, 590, 590, 590, 782, 782, 782, 782, 782, 782, 782, 782, 1584, 1584, 1584, 1584, 1616, 1616, 1616, 1616, 1648, 1648, 1648, 1648, 1680, 1680, 1680, 1680, 814, 814, 814, 814, 814, 814, 814, 814, 1776, 1776, 1776, 1776, 1808, 1808, 1808, 1808, 1840, 1840, 1840, 1840, 1872, 1872, 1872, 1872, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, 14353, 14353, 14353, 14353, 16401, 16401, 16401, 16401, 22547, 22547, 24595, 24595, 20497, 20497, 20497, 20497, 18449, 18449, 18449, 18449, 26643, 26643, 28691, 28691, 30739, 30739, -32749, -32749, -30701, -30701, -28653, -28653, -26605, -26605, -24557, -24557, -22509, -22509, -20461, -20461, 8207, 8207, 8207, 8207, 8207, 8207, 8207, 8207, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 556, 556, 556, 556, 556, 556, 556, 556, 556, 556, 556, 556, 556, 556, 556, 556, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 460, 460, 460, 460, 460, 460, 460, 460, 460, 460, 460, 460, 460, 460, 460, 460, 492, 492, 492, 492, 492, 492, 492, 492, 492, 492, 492, 492, 492, 492, 492, 492, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232};
    static short[] additionalMakeup = new short[]{28679, 28679, 31752, -32759, -31735, -30711, -29687, -28663, 29703, 29703, 30727, 30727, -27639, -26615, -25591, -24567};
    static short[] initBlack = new short[]{3226, 6412, 200, 168, 38, 38, 134, 134, 100, 100, 100, 100, 68, 68, 68, 68};
    static short[] twoBitBlack = new short[]{292, 260, 226, 226};
    static short[] black = new short[]{62, 62, 30, 30, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 588, 588, 588, 588, 588, 588, 588, 588, 1680, 1680, 20499, 22547, 24595, 26643, 1776, 1776, 1808, 1808, -24557, -22509, -20461, -18413, 1904, 1904, 1936, 1936, -16365, -14317, 782, 782, 782, 782, 814, 814, 814, 814, -12269, -10221, 10257, 10257, 12305, 12305, 14353, 14353, 16403, 18451, 1712, 1712, 1744, 1744, 28691, 30739, -32749, -30701, -28653, -26605, 2061, 2061, 2061, 2061, 2061, 2061, 2061, 2061, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 750, 750, 750, 750, 1616, 1616, 1648, 1648, 1424, 1424, 1456, 1456, 1488, 1488, 1520, 1520, 1840, 1840, 1872, 1872, 1968, 1968, 8209, 8209, 524, 524, 524, 524, 524, 524, 524, 524, 556, 556, 556, 556, 556, 556, 556, 556, 1552, 1552, 1584, 1584, 2000, 2000, 2032, 2032, 976, 976, 1008, 1008, 1040, 1040, 1072, 1072, 1296, 1296, 1328, 1328, 718, 718, 718, 718, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 490, 490, 490, 490, 490, 490, 490, 490, 490, 490, 490, 490, 490, 490, 490, 490, 4113, 4113, 6161, 6161, 848, 848, 880, 880, 912, 912, 944, 944, 622, 622, 622, 622, 654, 654, 654, 654, 1104, 1104, 1136, 1136, 1168, 1168, 1200, 1200, 1232, 1232, 1264, 1264, 686, 686, 686, 686, 1360, 1360, 1392, 1392, 12, 12, 12, 12, 12, 12, 12, 12, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390};
    static byte[] twoDCodes = new byte[]{80, 88, 23, 71, 30, 30, 62, 62, 4, 4, 4, 4, 4, 4, 4, 4, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41};
    private int bitPointer;
    private int bytePointer;
    private byte[] data;
    private int w;
    private boolean align = false;
    private int fillOrder;
    private int changingElemSize = 0;
    private int[] prevChangingElems;
    private int[] currChangingElems;
    private int lastChangingElement = 0;
    private boolean fillBits = false;

    public CCITTFaxDecoder(int fillOrder, int w, int h) {
        this.fillOrder = fillOrder;
        this.w = w;
        this.bitPointer = 0;
        this.bytePointer = 0;
        this.prevChangingElems = new int[w];
        this.currChangingElems = new int[w];
    }

    private boolean align() {
        if (this.align && this.bitPointer != 0) {
            ++this.bytePointer;
            this.bitPointer = 0;
            return true;
        }
        return false;
    }

    protected boolean consumeEOL() {
        int next12Bits = this.nextNBits(12);
        if (next12Bits == 1) {
            return true;
        }
        this.updatePointer(12);
        return false;
    }

    private int decodeBlackCodeWord() {
        int code = -1;
        int runLength = 0;
        boolean isWhite = false;
        while (!isWhite) {
            int current = this.nextLesserThan8Bits(4);
            short entry = initBlack[current];
            int isT = entry & 1;
            int bits = entry >>> 1 & 0xF;
            code = entry >>> 5 & 0x7FF;
            if (code == 100) {
                current = this.nextNBits(9);
                entry = black[current];
                isT = entry & 1;
                bits = entry >>> 1 & 0xF;
                code = entry >>> 5 & 0x7FF;
                if (bits == 12) {
                    this.updatePointer(5);
                    current = this.nextLesserThan8Bits(4);
                    entry = additionalMakeup[current];
                    bits = entry >>> 1 & 7;
                    code = entry >>> 4 & 0xFFF;
                    runLength += code;
                    this.updatePointer(4 - bits);
                    continue;
                }
                if (bits == 15) {
                    throw new RuntimeException("EOL code word encountered in Black run.");
                }
                runLength += code;
                this.updatePointer(9 - bits);
                if (isT != 0) continue;
                isWhite = true;
                continue;
            }
            if (code == 200) {
                current = this.nextLesserThan8Bits(2);
                entry = twoBitBlack[current];
                code = entry >>> 5 & 0x7FF;
                runLength += code;
                bits = entry >>> 1 & 0xF;
                this.updatePointer(2 - bits);
                isWhite = true;
                continue;
            }
            runLength += code;
            this.updatePointer(4 - bits);
            isWhite = true;
        }
        return runLength;
    }

    protected void decodeNextScanline(byte[] buffer, int lineOffset, int bitOffset) {
        int bits = 0;
        int code = 0;
        int isT = 0;
        boolean isWhite = true;
        this.changingElemSize = 0;
        while (bitOffset < this.w) {
            short entry;
            int current;
            while (isWhite) {
                current = this.nextNBits(10);
                entry = white[current];
                isT = entry & 1;
                bits = entry >>> 1 & 0xF;
                if (bits == 12) {
                    int twoBits = this.nextLesserThan8Bits(2);
                    current = current << 2 & 0xC | twoBits;
                    entry = additionalMakeup[current];
                    bits = entry >>> 1 & 7;
                    code = entry >>> 4 & 0xFFF;
                    bitOffset += code;
                    this.updatePointer(4 - bits);
                    continue;
                }
                if (bits == 0) {
                    throw new RuntimeException("Invalid code encountered.");
                }
                if (bits == 15) {
                    this.updatePointer(10);
                    return;
                }
                code = entry >>> 5 & 0x7FF;
                bitOffset += code;
                this.updatePointer(10 - bits);
                if (isT != 0) continue;
                isWhite = false;
                this.currChangingElems[this.changingElemSize++] = bitOffset;
            }
            if (bitOffset == this.w) {
                this.align();
                break;
            }
            while (!isWhite) {
                current = this.nextLesserThan8Bits(4);
                entry = initBlack[current];
                isT = entry & 1;
                bits = entry >>> 1 & 0xF;
                code = entry >>> 5 & 0x7FF;
                if (code == 100) {
                    current = this.nextNBits(9);
                    entry = black[current];
                    isT = entry & 1;
                    bits = entry >>> 1 & 0xF;
                    code = entry >>> 5 & 0x7FF;
                    if (bits == 12) {
                        this.updatePointer(5);
                        current = this.nextLesserThan8Bits(4);
                        entry = additionalMakeup[current];
                        bits = entry >>> 1 & 7;
                        code = entry >>> 4 & 0xFFF;
                        this.setToBlack(buffer, lineOffset, bitOffset, code);
                        bitOffset += code;
                        this.updatePointer(4 - bits);
                        continue;
                    }
                    if (bits == 15) {
                        this.updatePointer(9);
                        return;
                    }
                    this.setToBlack(buffer, lineOffset, bitOffset, code);
                    bitOffset += code;
                    this.updatePointer(9 - bits);
                    if (isT != 0) continue;
                    isWhite = true;
                    this.currChangingElems[this.changingElemSize++] = bitOffset;
                    continue;
                }
                if (code == 200) {
                    current = this.nextLesserThan8Bits(2);
                    entry = twoBitBlack[current];
                    code = entry >>> 5 & 0x7FF;
                    bits = entry >>> 1 & 0xF;
                    this.setToBlack(buffer, lineOffset, bitOffset, code);
                    this.updatePointer(2 - bits);
                    isWhite = true;
                    this.currChangingElems[this.changingElemSize++] = bitOffset += code;
                    continue;
                }
                this.setToBlack(buffer, lineOffset, bitOffset, code);
                this.updatePointer(4 - bits);
                isWhite = true;
                this.currChangingElems[this.changingElemSize++] = bitOffset += code;
            }
            if (bitOffset != this.w) continue;
            this.align();
            break;
        }
        this.currChangingElems[this.changingElemSize++] = bitOffset;
    }

    public void decodeT41D(byte[] buffer, byte[] compData, int startX, int height) {
        this.data = compData;
        int scanlineStride = (this.w + 7) / 8;
        this.bitPointer = 0;
        this.bytePointer = 0;
        int lineOffset = 0;
        for (int i = 0; i < height; ++i) {
            this.consumeEOL();
            this.decodeNextScanline(buffer, lineOffset, startX);
            lineOffset += scanlineStride;
        }
    }

    public void decodeT42D(byte[] buffer, byte[] compData, int startX, int height) {
        this.data = compData;
        int scanlineStride = (this.w + 7) / 8;
        this.bitPointer = 0;
        this.bytePointer = 0;
        int[] b = new int[2];
        int currIndex = 0;
        if (this.readEOL(true) != 1) {
            throw new RuntimeException("First scanline must be 1D encoded.");
        }
        int lineOffset = 0;
        this.decodeNextScanline(buffer, lineOffset, startX);
        lineOffset += scanlineStride;
        for (int lines = 1; lines < height; ++lines) {
            if (this.readEOL(false) == 0) {
                int[] temp = this.prevChangingElems;
                this.prevChangingElems = this.currChangingElems;
                this.currChangingElems = temp;
                currIndex = 0;
                int a0 = -1;
                boolean isWhite = true;
                int bitOffset = startX;
                this.lastChangingElement = 0;
                while (bitOffset < this.w) {
                    this.getNextChangingElement(a0, isWhite, b);
                    int b1 = b[0];
                    int b2 = b[1];
                    int entry = this.nextLesserThan8Bits(7);
                    entry = twoDCodes[entry] & 0xFF;
                    int code = (entry & 0x78) >>> 3;
                    int bits = entry & 7;
                    if (code == 0) {
                        if (!isWhite) {
                            this.setToBlack(buffer, lineOffset, bitOffset, b2 - bitOffset);
                        }
                        bitOffset = a0 = b2;
                        this.updatePointer(7 - bits);
                        continue;
                    }
                    if (code == 1) {
                        int number;
                        this.updatePointer(7 - bits);
                        if (isWhite) {
                            number = this.decodeWhiteCodeWord();
                            this.currChangingElems[currIndex++] = bitOffset += number;
                            number = this.decodeBlackCodeWord();
                            this.setToBlack(buffer, lineOffset, bitOffset, number);
                            this.currChangingElems[currIndex++] = bitOffset += number;
                        } else {
                            number = this.decodeBlackCodeWord();
                            this.setToBlack(buffer, lineOffset, bitOffset, number);
                            this.currChangingElems[currIndex++] = bitOffset += number;
                            number = this.decodeWhiteCodeWord();
                            this.currChangingElems[currIndex++] = bitOffset += number;
                        }
                        a0 = bitOffset;
                        continue;
                    }
                    if (code <= 8) {
                        int a1 = b1 + (code - 5);
                        this.currChangingElems[currIndex++] = a1;
                        if (!isWhite) {
                            this.setToBlack(buffer, lineOffset, bitOffset, a1 - bitOffset);
                        }
                        bitOffset = a0 = a1;
                        isWhite = !isWhite;
                        this.updatePointer(7 - bits);
                        continue;
                    }
                    throw new RuntimeException("Invalid code encountered while decoding 2D group 3 compressed data.");
                }
                this.currChangingElems[currIndex++] = bitOffset;
                this.changingElemSize = currIndex;
            } else {
                this.decodeNextScanline(buffer, lineOffset, startX);
            }
            lineOffset += scanlineStride;
        }
    }

    public synchronized void decodeT6(byte[] buffer, byte[] compData, int startX, int height) {
        this.data = compData;
        int scanlineStride = (this.w + 7) / 8;
        this.bitPointer = 0;
        this.bytePointer = 0;
        int[] b = new int[2];
        int[] cce = this.currChangingElems;
        this.changingElemSize = 0;
        cce[this.changingElemSize++] = this.w;
        cce[this.changingElemSize++] = this.w;
        int lineOffset = 0;
        for (int lines = 0; lines < height; ++lines) {
            int a0 = -1;
            boolean isWhite = true;
            int[] temp = this.prevChangingElems;
            this.prevChangingElems = this.currChangingElems;
            this.currChangingElems = temp;
            cce = temp;
            int currIndex = 0;
            int bitOffset = startX;
            this.lastChangingElement = 0;
            while (bitOffset < this.w) {
                this.getNextChangingElement(a0, isWhite, b);
                int b1 = b[0];
                int b2 = b[1];
                int entry = this.nextLesserThan8Bits(7);
                entry = twoDCodes[entry] & 0xFF;
                int code = (entry & 0x78) >>> 3;
                int bits = entry & 7;
                if (code == 0) {
                    if (!isWhite) {
                        if (b2 > this.w) {
                            b2 = this.w;
                        }
                        this.setToBlack(buffer, lineOffset, bitOffset, b2 - bitOffset);
                    }
                    bitOffset = a0 = b2;
                    this.updatePointer(7 - bits);
                    continue;
                }
                if (code == 1) {
                    int number;
                    this.updatePointer(7 - bits);
                    if (isWhite) {
                        number = this.decodeWhiteCodeWord();
                        cce[currIndex++] = bitOffset += number;
                        number = this.decodeBlackCodeWord();
                        if (number > this.w - bitOffset) {
                            number = this.w - bitOffset;
                        }
                        this.setToBlack(buffer, lineOffset, bitOffset, number);
                        cce[currIndex++] = bitOffset += number;
                    } else {
                        number = this.decodeBlackCodeWord();
                        if (number > this.w - bitOffset) {
                            number = this.w - bitOffset;
                        }
                        this.setToBlack(buffer, lineOffset, bitOffset, number);
                        cce[currIndex++] = bitOffset += number;
                        number = this.decodeWhiteCodeWord();
                        cce[currIndex++] = bitOffset += number;
                    }
                    a0 = bitOffset;
                    continue;
                }
                if (code <= 8) {
                    int a1 = b1 + (code - 5);
                    cce[currIndex++] = a1;
                    if (!isWhite) {
                        if (a1 > this.w) {
                            a1 = this.w;
                        }
                        this.setToBlack(buffer, lineOffset, bitOffset, a1 - bitOffset);
                    }
                    bitOffset = a0 = a1;
                    isWhite = !isWhite;
                    this.updatePointer(7 - bits);
                    continue;
                }
                if (code == 11) {
                    if (this.nextLesserThan8Bits(3) != 7) {
                        throw new RuntimeException("Invalid code encountered while decoding 2D group 4 compressed data.");
                    }
                    int zeros = 0;
                    boolean exit = false;
                    while (!exit) {
                        while (this.nextLesserThan8Bits(1) != 1) {
                            ++zeros;
                        }
                        if (zeros > 5) {
                            if (!isWhite && (zeros -= 6) > 0) {
                                cce[currIndex++] = bitOffset;
                            }
                            bitOffset += zeros;
                            if (zeros > 0) {
                                isWhite = true;
                            }
                            if (this.nextLesserThan8Bits(1) == 0) {
                                if (!isWhite) {
                                    cce[currIndex++] = bitOffset;
                                }
                                isWhite = true;
                            } else {
                                if (isWhite) {
                                    cce[currIndex++] = bitOffset;
                                }
                                isWhite = false;
                            }
                            exit = true;
                        }
                        if (zeros == 5) {
                            if (!isWhite) {
                                cce[currIndex++] = bitOffset;
                            }
                            bitOffset += zeros;
                            isWhite = true;
                            continue;
                        }
                        cce[currIndex++] = bitOffset += zeros;
                        this.setToBlack(buffer, lineOffset, bitOffset, 1);
                        ++bitOffset;
                        isWhite = false;
                    }
                    continue;
                }
                this.updatePointer(7 - bits);
                bitOffset = this.w;
            }
            this.align();
            if (currIndex <= this.w) {
                cce[currIndex++] = bitOffset;
            }
            this.changingElemSize = currIndex;
            lineOffset += scanlineStride;
        }
    }

    private int decodeWhiteCodeWord() {
        int code = -1;
        int runLength = 0;
        boolean isWhite = true;
        while (isWhite) {
            int current = this.nextNBits(10);
            short entry = white[current];
            int isT = entry & 1;
            int bits = entry >>> 1 & 0xF;
            if (bits == 12) {
                int twoBits = this.nextLesserThan8Bits(2);
                current = current << 2 & 0xC | twoBits;
                entry = additionalMakeup[current];
                bits = entry >>> 1 & 7;
                code = entry >>> 4 & 0xFFF;
                runLength += code;
                this.updatePointer(4 - bits);
                continue;
            }
            if (bits == 0) {
                throw new RuntimeException("Invalid code encountered.");
            }
            if (bits == 15) {
                throw new RuntimeException("EOL code word encountered in White run.");
            }
            code = entry >>> 5 & 0x7FF;
            runLength += code;
            this.updatePointer(10 - bits);
            if (isT != 0) continue;
            isWhite = false;
        }
        return runLength;
    }

    private void getNextChangingElement(int a0, boolean isWhite, int[] ret) {
        int i;
        int start;
        int[] pce = this.prevChangingElems;
        int ces = this.changingElemSize;
        int n = start = this.lastChangingElement > 0 ? this.lastChangingElement - 1 : 0;
        start = isWhite ? (start &= 0xFFFFFFFE) : (start |= 1);
        for (i = start; i < ces; i += 2) {
            int temp = pce[i];
            if (temp <= a0) continue;
            this.lastChangingElement = i;
            ret[0] = temp;
            break;
        }
        if (i + 1 < ces) {
            ret[1] = pce[i + 1];
        }
    }

    public boolean isAlign() {
        return this.align;
    }

    public boolean isFillBits() {
        return this.fillBits;
    }

    private int nextLesserThan8Bits(int bitsToGet) {
        int i1;
        byte next;
        byte b;
        int l = this.data.length - 1;
        int bp = this.bytePointer;
        if (this.fillOrder == 1) {
            b = this.data[bp];
            next = bp == l ? (byte)0 : this.data[bp + 1];
        } else if (this.fillOrder == 2) {
            b = flipTable[this.data[bp] & 0xFF];
            next = bp == l ? (byte)0 : flipTable[this.data[bp + 1] & 0xFF];
        } else {
            throw new RuntimeException("tag must be either 1 or 2.");
        }
        int bitsLeft = 8 - this.bitPointer;
        int bitsFromNextByte = bitsToGet - bitsLeft;
        int shift = bitsLeft - bitsToGet;
        if (shift >= 0) {
            i1 = (b & table1[bitsLeft]) >>> shift;
            this.bitPointer += bitsToGet;
            if (this.bitPointer == 8) {
                this.bitPointer = 0;
                ++this.bytePointer;
            }
        } else {
            i1 = (b & table1[bitsLeft]) << -shift;
            int i2 = (next & table2[bitsFromNextByte]) >>> 8 - bitsFromNextByte;
            i1 |= i2;
            ++this.bytePointer;
            this.bitPointer = bitsFromNextByte;
        }
        return i1;
    }

    private int nextNBits(int bitsToGet) {
        byte next2next;
        byte next;
        byte b;
        int l = this.data.length - 1;
        int bp = this.bytePointer;
        if (this.fillOrder == 1) {
            b = this.data[bp];
            if (bp == l) {
                next = 0;
                next2next = 0;
            } else if (bp + 1 == l) {
                next = this.data[bp + 1];
                next2next = 0;
            } else {
                next = this.data[bp + 1];
                next2next = this.data[bp + 2];
            }
        } else if (this.fillOrder == 2) {
            b = flipTable[this.data[bp] & 0xFF];
            if (bp == l) {
                next = 0;
                next2next = 0;
            } else if (bp + 1 == l) {
                next = flipTable[this.data[bp + 1] & 0xFF];
                next2next = 0;
            } else {
                next = flipTable[this.data[bp + 1] & 0xFF];
                next2next = flipTable[this.data[bp + 2] & 0xFF];
            }
        } else {
            throw new RuntimeException("tag must be either 1 or 2.");
        }
        int bitsLeft = 8 - this.bitPointer;
        int bitsFromNextByte = bitsToGet - bitsLeft;
        int bitsFromNext2NextByte = 0;
        if (bitsFromNextByte > 8) {
            bitsFromNext2NextByte = bitsFromNextByte - 8;
            bitsFromNextByte = 8;
        }
        ++this.bytePointer;
        int i1 = (b & table1[bitsLeft]) << bitsToGet - bitsLeft;
        int i2 = (next & table2[bitsFromNextByte]) >>> 8 - bitsFromNextByte;
        int i3 = 0;
        if (bitsFromNext2NextByte != 0) {
            i2 <<= bitsFromNext2NextByte;
            i3 = (next2next & table2[bitsFromNext2NextByte]) >>> 8 - bitsFromNext2NextByte;
            i2 |= i3;
            ++this.bytePointer;
            this.bitPointer = bitsFromNext2NextByte;
        } else if (bitsFromNextByte == 8) {
            this.bitPointer = 0;
            ++this.bytePointer;
        } else {
            this.bitPointer = bitsFromNextByte;
        }
        int i = i1 | i2;
        return i;
    }

    private int readEOL(boolean isFirstEOL) {
        if (!this.seekEOL()) {
            throw new RuntimeException("EOL not found");
        }
        if (!this.fillBits) {
            int next12Bits = this.nextNBits(12);
            if (isFirstEOL && next12Bits == 0 && this.nextNBits(4) == 1) {
                this.fillBits = true;
                return 1;
            }
            if (next12Bits != 1) {
                throw new RuntimeException("Scanline must begin with EOL code word.");
            }
        } else {
            int bitsLeft = 8 - this.bitPointer;
            if (this.nextNBits(bitsLeft) != 0) {
                throw new RuntimeException("All fill bits preceding EOL code must be 0.");
            }
            if (bitsLeft < 4 && this.nextNBits(8) != 0) {
                throw new RuntimeException("All fill bits preceding EOL code must be 0.");
            }
            int next8 = this.nextNBits(8);
            if (isFirstEOL && (next8 & 0xF0) == 16) {
                this.fillBits = false;
                this.updatePointer(4);
            } else {
                while (next8 != 1) {
                    if (next8 != 0) {
                        throw new RuntimeException("0 bits expected before EOL");
                    }
                    next8 = this.nextNBits(8);
                }
            }
        }
        return this.nextLesserThan8Bits(1);
    }

    private boolean seekEOL() {
        int bitIndexMax = this.data.length * 8 - 1;
        int bitIndex = this.bytePointer * 8 + this.bitPointer;
        while (bitIndex <= bitIndexMax - 12) {
            int next12Bits = this.nextNBits(12);
            bitIndex += 12;
            while (next12Bits != 1 && bitIndex < bitIndexMax) {
                next12Bits = (next12Bits & 0x7FF) << 1 | this.nextLesserThan8Bits(1) & 1;
                ++bitIndex;
            }
            if (next12Bits != 1) continue;
            this.updatePointer(12);
            return true;
        }
        return false;
    }

    public void setAlign(boolean align) {
        this.align = align;
    }

    public void setFillBits(boolean fillBits) {
        this.fillBits = fillBits;
    }

    private void setToBlack(byte[] buffer, int lineOffset, int bitOffset, int numBits) {
        int bitNum = 8 * lineOffset + bitOffset;
        int lastBit = bitNum + numBits;
        int byteNum = bitNum >> 3;
        int shift = bitNum & 7;
        if (shift > 0) {
            byte val = buffer[byteNum];
            for (int maskVal = 1 << 7 - shift; maskVal > 0 && bitNum < lastBit; maskVal >>= 1, ++bitNum) {
                val = (byte)(val | maskVal);
            }
            buffer[byteNum] = val;
        }
        byteNum = bitNum >> 3;
        while (bitNum < lastBit - 7) {
            buffer[byteNum++] = -1;
            bitNum += 8;
        }
        while (bitNum < lastBit) {
            int n = byteNum = bitNum >> 3;
            buffer[n] = (byte)(buffer[n] | 1 << 7 - (bitNum & 7));
            ++bitNum;
        }
    }

    private void updatePointer(int bitsToMoveBack) {
        int i;
        if (bitsToMoveBack > 8) {
            this.bytePointer -= bitsToMoveBack / 8;
            bitsToMoveBack %= 8;
        }
        if ((i = this.bitPointer - bitsToMoveBack) < 0) {
            --this.bytePointer;
            this.bitPointer = 8 + i;
        } else {
            this.bitPointer = i;
        }
    }
}

