/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.image;

class InverseColorMap {
    static final int QUANTBITS = 5;
    static final int TRUNCBITS = 3;
    static final int QUANTMASK_BLUE = 31;
    static final int QUANTMASK_GREEN = 992;
    static final int QUANTMASK_RED = 31744;
    static final int MAXQUANTVAL = 32;
    byte[] rgbMapByte;
    int[] rgbMapInt;
    int numColors;
    int maxColor;
    byte[] inverseRGB;
    int transparentIndex = -1;

    InverseColorMap(byte[] byArray) {
        this(byArray, -1);
    }

    InverseColorMap(int[] nArray) {
        this(nArray, -1);
    }

    InverseColorMap(byte[] byArray, int n) {
        this.rgbMapByte = byArray;
        this.numColors = this.rgbMapByte.length / 4;
        this.transparentIndex = n;
        this.inverseRGB = new byte[32768];
        this.initIRGB(new int[32768]);
    }

    InverseColorMap(int[] nArray, int n) {
        this.rgbMapInt = nArray;
        this.numColors = this.rgbMapInt.length;
        this.transparentIndex = n;
        this.inverseRGB = new byte[32768];
        this.initIRGB(new int[32768]);
    }

    void initIRGB(int[] nArray) {
        for (int i = 0; i < this.numColors; ++i) {
            int n;
            int n2;
            int n3;
            if (i == this.transparentIndex) continue;
            if (this.rgbMapByte != null) {
                n3 = this.rgbMapByte[i * 4] & 0xFF;
                n2 = this.rgbMapByte[i * 4 + 1] & 0xFF;
                n = this.rgbMapByte[i * 4 + 2] & 0xFF;
            } else if (this.rgbMapInt != null) {
                n3 = this.rgbMapInt[i] >> 16 & 0xFF;
                n2 = this.rgbMapInt[i] >> 8 & 0xFF;
                n = this.rgbMapInt[i] & 0xFF;
            } else {
                throw new IllegalStateException("colormap == null");
            }
            int n4 = n3 - 4;
            int n5 = n2 - 4;
            int n6 = n - 4;
            n4 = n4 * n4 + n5 * n5 + n6 * n6;
            int n7 = 2 * (64 - (n3 << 3));
            int n8 = 2 * (64 - (n2 << 3));
            int n9 = 2 * (64 - (n << 3));
            int n10 = 0;
            int n11 = 0;
            int n12 = n7;
            while (n11 < 32) {
                int n13 = 0;
                n5 = n4;
                int n14 = n8;
                while (n13 < 32) {
                    int n15 = 0;
                    n6 = n5;
                    int n16 = n9;
                    while (n15 < 32) {
                        if (i == 0 || nArray[n10] > n6) {
                            nArray[n10] = n6;
                            this.inverseRGB[n10] = (byte)i;
                        }
                        n6 += n16;
                        ++n15;
                        ++n10;
                        n16 += 128;
                    }
                    n5 += n14;
                    ++n13;
                    n14 += 128;
                }
                n4 += n12;
                ++n11;
                n12 += 128;
            }
        }
    }

    public final int getIndexNearest(int n) {
        return this.inverseRGB[(n >> 9 & 0x7C00) + (n >> 6 & 0x3E0) + (n >> 3 & 0x1F)] & 0xFF;
    }

    public final int getIndexNearest(int n, int n2, int n3) {
        return this.inverseRGB[(n << 7 & 0x7C00) + (n2 << 2 & 0x3E0) + (n3 >> 3 & 0x1F)] & 0xFF;
    }
}

