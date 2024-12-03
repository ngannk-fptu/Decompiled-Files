/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.imageio.plugins.jpeg.Segment;
import java.io.DataInput;
import java.io.IOException;
import javax.imageio.IIOException;
import javax.imageio.plugins.jpeg.JPEGQTable;

final class QuantizationTable
extends Segment {
    private static final int[] ZIGZAG = new int[]{0, 1, 5, 6, 14, 15, 27, 28, 2, 4, 7, 13, 16, 26, 29, 42, 3, 8, 12, 17, 25, 30, 41, 43, 9, 11, 18, 24, 31, 40, 44, 53, 10, 19, 23, 32, 39, 45, 52, 54, 20, 22, 33, 38, 46, 51, 55, 60, 21, 34, 37, 47, 50, 56, 59, 61, 35, 36, 48, 49, 57, 58, 62, 63};
    private final int[] precision = new int[4];
    private final boolean[] tq = new boolean[4];
    private final int[][] quantTables = new int[4][64];

    QuantizationTable() {
        super(65499);
    }

    void enhanceTables() {
        for (int i = 0; i < 4; ++i) {
            if (!this.tq[i]) continue;
            this.enhanceQuantizationTable(this.quantTables[i], ZIGZAG);
        }
    }

    private void enhanceQuantizationTable(int[] nArray, int[] nArray2) {
        int n;
        for (n = 0; n < 8; ++n) {
            int n2 = nArray2[n];
            nArray[n2] = nArray[n2] * 90;
            int n3 = nArray2[32 + n];
            nArray[n3] = nArray[n3] * 90;
            int n4 = nArray2[16 + n];
            nArray[n4] = nArray[n4] * 118;
            int n5 = nArray2[48 + n];
            nArray[n5] = nArray[n5] * 49;
            int n6 = nArray2[40 + n];
            nArray[n6] = nArray[n6] * 71;
            int n7 = nArray2[8 + n];
            nArray[n7] = nArray[n7] * 126;
            int n8 = nArray2[56 + n];
            nArray[n8] = nArray[n8] * 25;
            int n9 = nArray2[24 + n];
            nArray[n9] = nArray[n9] * 106;
        }
        for (n = 0; n < 8; ++n) {
            int n10 = nArray2[8 * n];
            nArray[n10] = nArray[n10] * 90;
            int n11 = nArray2[4 + 8 * n];
            nArray[n11] = nArray[n11] * 90;
            int n12 = nArray2[2 + 8 * n];
            nArray[n12] = nArray[n12] * 118;
            int n13 = nArray2[6 + 8 * n];
            nArray[n13] = nArray[n13] * 49;
            int n14 = nArray2[5 + 8 * n];
            nArray[n14] = nArray[n14] * 71;
            int n15 = nArray2[1 + 8 * n];
            nArray[n15] = nArray[n15] * 126;
            int n16 = nArray2[7 + 8 * n];
            nArray[n16] = nArray[n16] * 25;
            int n17 = nArray2[3 + 8 * n];
            nArray[n17] = nArray[n17] * 106;
        }
        n = 0;
        while (n < 64) {
            int n18 = n++;
            nArray[n18] = nArray[n18] >> 6;
        }
    }

    public String toString() {
        return "DQT[]";
    }

    public static QuantizationTable read(DataInput dataInput, int n) throws IOException {
        int n2 = 2;
        QuantizationTable quantizationTable = new QuantizationTable();
        while (n2 < n) {
            int n3;
            int n4 = dataInput.readUnsignedByte();
            ++n2;
            int n5 = n4 & 0xF;
            if (n5 > 3) {
                throw new IIOException("Unexpected JPEG Quantization Table Id (> 3): " + n5);
            }
            quantizationTable.precision[n5] = n4 >> 4;
            if (quantizationTable.precision[n5] == 0) {
                quantizationTable.precision[n5] = 8;
            } else if (quantizationTable.precision[n5] == 1) {
                quantizationTable.precision[n5] = 16;
            } else {
                throw new IIOException("Unexpected JPEG Quantization Table precision: " + quantizationTable.precision[n5]);
            }
            quantizationTable.tq[n5] = true;
            if (quantizationTable.precision[n5] == 8) {
                for (n3 = 0; n3 < 64; ++n3) {
                    if (n2 > n) {
                        throw new IIOException("JPEG Quantization Table format error");
                    }
                    quantizationTable.quantTables[n5][n3] = dataInput.readUnsignedByte();
                    ++n2;
                }
                continue;
            }
            for (n3 = 0; n3 < 64; ++n3) {
                if (n2 > n) {
                    throw new IIOException("JPEG Quantization Table format error");
                }
                quantizationTable.quantTables[n5][n3] = dataInput.readUnsignedShort();
                n2 += 2;
            }
        }
        if (n2 != n) {
            throw new IIOException("JPEG Quantization Table error, bad segment length: " + n);
        }
        return quantizationTable;
    }

    public boolean isPresent(int n) {
        return this.tq[n];
    }

    int precision(int n) {
        return this.precision[n];
    }

    int[] qTable(int n) {
        return this.quantTables[n];
    }

    JPEGQTable toNativeTable(int n) {
        int[] nArray = new int[this.quantTables[n].length];
        for (int i = 0; i < nArray.length; ++i) {
            nArray[i] = this.quantTables[n][ZIGZAG[i]];
        }
        return new JPEGQTable(nArray);
    }
}

