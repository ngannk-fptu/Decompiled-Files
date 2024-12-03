/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.imageio.plugins.jpeg.Segment;
import java.io.DataInput;
import java.io.IOException;
import javax.imageio.IIOException;
import javax.imageio.plugins.jpeg.JPEGHuffmanTable;

final class HuffmanTable
extends Segment {
    private final short[][][] l = new short[4][2][16];
    private final short[][][][] v = new short[4][2][16][200];
    private final boolean[][] tc = new boolean[4][2];
    private static final int MSB = Integer.MIN_VALUE;

    private HuffmanTable() {
        super(65476);
    }

    void buildHuffTables(int[][][] nArray) throws IOException {
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 2; ++j) {
                if (!this.tc[i][j]) continue;
                this.buildHuffTable(nArray[i][j], this.l[i][j], this.v[i][j]);
            }
        }
    }

    private void buildHuffTable(int[] nArray, short[] sArray, short[][] sArray2) throws IOException {
        int n;
        int n2;
        int n3;
        int n4 = 256;
        int n5 = 0;
        for (n3 = 0; n3 < 8; ++n3) {
            for (n2 = 0; n2 < sArray[n3]; ++n2) {
                for (n = 0; n < n4 >> n3 + 1; ++n) {
                    nArray[n5] = sArray2[n3][n2] | n3 + 1 << 8;
                    ++n5;
                }
            }
        }
        n3 = 1;
        while (n5 < 256) {
            nArray[n5] = n3 | Integer.MIN_VALUE;
            ++n3;
            ++n5;
        }
        n3 = 1;
        n5 = 0;
        for (n2 = 8; n2 < 16; ++n2) {
            for (n = 0; n < sArray[n2]; ++n) {
                for (int i = 0; i < n4 >> n2 - 7; ++i) {
                    nArray[n3 * 256 + n5] = sArray2[n2][n] | n2 + 1 << 8;
                    ++n5;
                }
                if (n5 < 256) continue;
                if (n5 > 256) {
                    throw new IIOException("JPEG Huffman Table error");
                }
                n5 = 0;
                ++n3;
            }
        }
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("DHT[");
        for (int i = 0; i < this.tc.length; ++i) {
            for (int j = 0; j < this.tc[i].length; ++j) {
                if (!this.tc[i][j]) continue;
                if (stringBuilder.length() > 4) {
                    stringBuilder.append(", ");
                }
                stringBuilder.append("id: ");
                stringBuilder.append(i);
                stringBuilder.append(", class: ");
                stringBuilder.append(j);
            }
        }
        stringBuilder.append(']');
        return stringBuilder.toString();
    }

    public static Segment read(DataInput dataInput, int n) throws IOException {
        int n2 = 2;
        HuffmanTable huffmanTable = new HuffmanTable();
        while (n2 < n) {
            int n3;
            int n4 = dataInput.readUnsignedByte();
            ++n2;
            int n5 = n4 & 0xF;
            if (n5 > 3) {
                throw new IIOException("Unexpected JPEG Huffman Table Id (> 3):" + n5);
            }
            int n6 = n4 >> 4;
            if (n6 > 2) {
                throw new IIOException("Unexpected JPEG Huffman Table class (> 2): " + n6);
            }
            huffmanTable.tc[n5][n6] = true;
            for (n3 = 0; n3 < 16; ++n3) {
                huffmanTable.l[n5][n6][n3] = (short)dataInput.readUnsignedByte();
                ++n2;
            }
            for (n3 = 0; n3 < 16; ++n3) {
                for (int i = 0; i < huffmanTable.l[n5][n6][n3]; ++i) {
                    if (n2 > n) {
                        throw new IIOException("JPEG Huffman Table format error");
                    }
                    huffmanTable.v[n5][n6][n3][i] = (short)dataInput.readUnsignedByte();
                    ++n2;
                }
            }
        }
        if (n2 != n) {
            throw new IIOException("JPEG Huffman Table format error, bad segment length: " + n);
        }
        return huffmanTable;
    }

    public boolean isPresent(int n, int n2) {
        return this.tc[n][n2];
    }

    private short[] lengths(int n, int n2) {
        return this.l[n][n2];
    }

    private short[] tables(int n, int n2) {
        short[] sArray = this.lengths(n, n2);
        int n3 = 0;
        for (short s : sArray) {
            n3 += s;
        }
        short[] sArray2 = new short[n3];
        int n4 = 0;
        for (int i = 0; i < 16; ++i) {
            short[] sArray3 = this.v[n][n2][i];
            short s = sArray[i];
            System.arraycopy(sArray3, 0, sArray2, n4, s);
            n4 += s;
        }
        return sArray2;
    }

    JPEGHuffmanTable toNativeTable(int n, int n2) {
        return new JPEGHuffmanTable(this.lengths(n, n2), this.tables(n, n2));
    }
}

