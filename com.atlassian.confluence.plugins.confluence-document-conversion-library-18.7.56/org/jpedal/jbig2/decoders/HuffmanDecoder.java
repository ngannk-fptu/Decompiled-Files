/*
 * Decompiled with CFR 0.152.
 */
package org.jpedal.jbig2.decoders;

import java.io.IOException;
import org.jpedal.jbig2.JBIG2Exception;
import org.jpedal.jbig2.decoders.DecodeIntResult;
import org.jpedal.jbig2.io.StreamReader;

public class HuffmanDecoder {
    public static int jbig2HuffmanLOW = -3;
    public static int jbig2HuffmanOOB = -2;
    public static int jbig2HuffmanEOT = -1;
    private StreamReader reader;
    private static HuffmanDecoder ref;
    public static int[][] huffmanTableA;
    public static int[][] huffmanTableB;
    public static int[][] huffmanTableC;
    public static int[][] huffmanTableD;
    public static int[][] huffmanTableE;
    public static int[][] huffmanTableF;
    public static int[][] huffmanTableG;
    public static int[][] huffmanTableH;
    public static int[][] huffmanTableI;
    public static int[][] huffmanTableJ;
    public static int[][] huffmanTableK;
    public static int[][] huffmanTableL;
    public static int[][] huffmanTableM;
    public static int[][] huffmanTableN;
    public static int[][] huffmanTableO;

    private HuffmanDecoder() {
    }

    private HuffmanDecoder(StreamReader streamReader) {
        this.reader = streamReader;
    }

    public static void initiate(StreamReader streamReader) {
        ref = new HuffmanDecoder(streamReader);
    }

    public static HuffmanDecoder getInstance() throws JBIG2Exception {
        if (ref == null) {
            throw new JBIG2Exception("JBIG2HuffmanDecoder is uninitiated. Call JBIG2HuffmanDecoder.initiate(JBIG2File file) before getInstance()");
        }
        return ref;
    }

    public DecodeIntResult decodeInt(int[][] nArray) throws IOException {
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        while (nArray[n3][2] != jbig2HuffmanEOT) {
            int n4;
            while (n < nArray[n3][1]) {
                n4 = this.reader.readBit();
                n2 = n2 << 1 | n4;
                ++n;
            }
            if (n2 == nArray[n3][3]) {
                if (nArray[n3][2] == jbig2HuffmanOOB) {
                    return new DecodeIntResult(-1, false);
                }
                if (nArray[n3][2] == jbig2HuffmanLOW) {
                    int n5 = this.reader.readBits(32);
                    n4 = nArray[n3][0] - n5;
                } else if (nArray[n3][2] > 0) {
                    int n6 = this.reader.readBits(nArray[n3][2]);
                    n4 = nArray[n3][0] + n6;
                } else {
                    n4 = nArray[n3][0];
                }
                return new DecodeIntResult(n4, true);
            }
            ++n3;
        }
        return new DecodeIntResult(-1, false);
    }

    public int[][] buildTable(int[][] nArray, int n) {
        int n2;
        for (n2 = 0; n2 < n; ++n2) {
            int n3;
            int n4;
            for (n4 = n2; n4 < n && nArray[n4][1] == 0; ++n4) {
            }
            if (n4 == n) break;
            for (n3 = n4 + 1; n3 < n; ++n3) {
                if (nArray[n3][1] <= 0 || nArray[n3][1] >= nArray[n4][1]) continue;
                n4 = n3;
            }
            if (n4 == n2) continue;
            int[] nArray2 = nArray[n4];
            for (n3 = n4; n3 > n2; --n3) {
                nArray[n3] = nArray[n3 - 1];
            }
            nArray[n2] = nArray2;
        }
        nArray[n2] = nArray[n];
        n2 = 0;
        int n5 = 0;
        nArray[n2++][3] = n5++;
        while (nArray[n2][2] != jbig2HuffmanEOT) {
            n5 <<= nArray[n2][1] - nArray[n2 - 1][1];
            nArray[n2][3] = n5++;
            ++n2;
        }
        return nArray;
    }

    static {
        huffmanTableA = new int[][]{{0, 1, 4, 0}, {16, 2, 8, 2}, {272, 3, 16, 6}, {65808, 3, 32, 7}, {0, 0, jbig2HuffmanEOT, 0}};
        huffmanTableB = new int[][]{{0, 1, 0, 0}, {1, 2, 0, 2}, {2, 3, 0, 6}, {3, 4, 3, 14}, {11, 5, 6, 30}, {75, 6, 32, 62}, {0, 6, jbig2HuffmanOOB, 63}, {0, 0, jbig2HuffmanEOT, 0}};
        huffmanTableC = new int[][]{{0, 1, 0, 0}, {1, 2, 0, 2}, {2, 3, 0, 6}, {3, 4, 3, 14}, {11, 5, 6, 30}, {0, 6, jbig2HuffmanOOB, 62}, {75, 7, 32, 254}, {-256, 8, 8, 254}, {-257, 8, jbig2HuffmanLOW, 255}, {0, 0, jbig2HuffmanEOT, 0}};
        huffmanTableD = new int[][]{{1, 1, 0, 0}, {2, 2, 0, 2}, {3, 3, 0, 6}, {4, 4, 3, 14}, {12, 5, 6, 30}, {76, 5, 32, 31}, {0, 0, jbig2HuffmanEOT, 0}};
        huffmanTableE = new int[][]{{1, 1, 0, 0}, {2, 2, 0, 2}, {3, 3, 0, 6}, {4, 4, 3, 14}, {12, 5, 6, 30}, {76, 6, 32, 62}, {-255, 7, 8, 126}, {-256, 7, jbig2HuffmanLOW, 127}, {0, 0, jbig2HuffmanEOT, 0}};
        huffmanTableF = new int[][]{{0, 2, 7, 0}, {128, 3, 7, 2}, {256, 3, 8, 3}, {-1024, 4, 9, 8}, {-512, 4, 8, 9}, {-256, 4, 7, 10}, {-32, 4, 5, 11}, {512, 4, 9, 12}, {1024, 4, 10, 13}, {-2048, 5, 10, 28}, {-128, 5, 6, 29}, {-64, 5, 5, 30}, {-2049, 6, jbig2HuffmanLOW, 62}, {2048, 6, 32, 63}, {0, 0, jbig2HuffmanEOT, 0}};
        huffmanTableG = new int[][]{{-512, 3, 8, 0}, {256, 3, 8, 1}, {512, 3, 9, 2}, {1024, 3, 10, 3}, {-1024, 4, 9, 8}, {-256, 4, 7, 9}, {-32, 4, 5, 10}, {0, 4, 5, 11}, {128, 4, 7, 12}, {-128, 5, 6, 26}, {-64, 5, 5, 27}, {32, 5, 5, 28}, {64, 5, 6, 29}, {-1025, 5, jbig2HuffmanLOW, 30}, {2048, 5, 32, 31}, {0, 0, jbig2HuffmanEOT, 0}};
        huffmanTableH = new int[][]{{0, 2, 1, 0}, {0, 2, jbig2HuffmanOOB, 1}, {4, 3, 4, 4}, {-1, 4, 0, 10}, {22, 4, 4, 11}, {38, 4, 5, 12}, {2, 5, 0, 26}, {70, 5, 6, 27}, {134, 5, 7, 28}, {3, 6, 0, 58}, {20, 6, 1, 59}, {262, 6, 7, 60}, {646, 6, 10, 61}, {-2, 7, 0, 124}, {390, 7, 8, 125}, {-15, 8, 3, 252}, {-5, 8, 1, 253}, {-7, 9, 1, 508}, {-3, 9, 0, 509}, {-16, 9, jbig2HuffmanLOW, 510}, {1670, 9, 32, 511}, {0, 0, jbig2HuffmanEOT, 0}};
        huffmanTableI = new int[][]{{0, 2, jbig2HuffmanOOB, 0}, {-1, 3, 1, 2}, {1, 3, 1, 3}, {7, 3, 5, 4}, {-3, 4, 1, 10}, {43, 4, 5, 11}, {75, 4, 6, 12}, {3, 5, 1, 26}, {139, 5, 7, 27}, {267, 5, 8, 28}, {5, 6, 1, 58}, {39, 6, 2, 59}, {523, 6, 8, 60}, {1291, 6, 11, 61}, {-5, 7, 1, 124}, {779, 7, 9, 125}, {-31, 8, 4, 252}, {-11, 8, 2, 253}, {-15, 9, 2, 508}, {-7, 9, 1, 509}, {-32, 9, jbig2HuffmanLOW, 510}, {3339, 9, 32, 511}, {0, 0, jbig2HuffmanEOT, 0}};
        huffmanTableJ = new int[][]{{-2, 2, 2, 0}, {6, 2, 6, 1}, {0, 2, jbig2HuffmanOOB, 2}, {-3, 5, 0, 24}, {2, 5, 0, 25}, {70, 5, 5, 26}, {3, 6, 0, 54}, {102, 6, 5, 55}, {134, 6, 6, 56}, {198, 6, 7, 57}, {326, 6, 8, 58}, {582, 6, 9, 59}, {1094, 6, 10, 60}, {-21, 7, 4, 122}, {-4, 7, 0, 123}, {4, 7, 0, 124}, {2118, 7, 11, 125}, {-5, 8, 0, 252}, {5, 8, 0, 253}, {-22, 8, jbig2HuffmanLOW, 254}, {4166, 8, 32, 255}, {0, 0, jbig2HuffmanEOT, 0}};
        huffmanTableK = new int[][]{{1, 1, 0, 0}, {2, 2, 1, 2}, {4, 4, 0, 12}, {5, 4, 1, 13}, {7, 5, 1, 28}, {9, 5, 2, 29}, {13, 6, 2, 60}, {17, 7, 2, 122}, {21, 7, 3, 123}, {29, 7, 4, 124}, {45, 7, 5, 125}, {77, 7, 6, 126}, {141, 7, 32, 127}, {0, 0, jbig2HuffmanEOT, 0}};
        huffmanTableL = new int[][]{{1, 1, 0, 0}, {2, 2, 0, 2}, {3, 3, 1, 6}, {5, 5, 0, 28}, {6, 5, 1, 29}, {8, 6, 1, 60}, {10, 7, 0, 122}, {11, 7, 1, 123}, {13, 7, 2, 124}, {17, 7, 3, 125}, {25, 7, 4, 126}, {41, 8, 5, 254}, {73, 8, 32, 255}, {0, 0, jbig2HuffmanEOT, 0}};
        huffmanTableM = new int[][]{{1, 1, 0, 0}, {2, 3, 0, 4}, {7, 3, 3, 5}, {3, 4, 0, 12}, {5, 4, 1, 13}, {4, 5, 0, 28}, {15, 6, 1, 58}, {17, 6, 2, 59}, {21, 6, 3, 60}, {29, 6, 4, 61}, {45, 6, 5, 62}, {77, 7, 6, 126}, {141, 7, 32, 127}, {0, 0, jbig2HuffmanEOT, 0}};
        huffmanTableN = new int[][]{{0, 1, 0, 0}, {-2, 3, 0, 4}, {-1, 3, 0, 5}, {1, 3, 0, 6}, {2, 3, 0, 7}, {0, 0, jbig2HuffmanEOT, 0}};
        huffmanTableO = new int[][]{{0, 1, 0, 0}, {-1, 3, 0, 4}, {1, 3, 0, 5}, {-2, 4, 0, 12}, {2, 4, 0, 13}, {-4, 5, 1, 28}, {3, 5, 1, 29}, {-8, 6, 2, 60}, {5, 6, 2, 61}, {-24, 7, 4, 124}, {9, 7, 4, 125}, {-25, 7, jbig2HuffmanLOW, 126}, {25, 7, 32, 127}, {0, 0, jbig2HuffmanEOT, 0}};
    }
}

