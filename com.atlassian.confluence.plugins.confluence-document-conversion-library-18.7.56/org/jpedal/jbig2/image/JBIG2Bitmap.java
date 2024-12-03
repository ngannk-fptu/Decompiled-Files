/*
 * Decompiled with CFR 0.152.
 */
package org.jpedal.jbig2.image;

import java.io.IOException;
import java.util.BitSet;
import org.jpedal.jbig2.JBIG2Exception;
import org.jpedal.jbig2.decoders.ArithmeticDecoder;
import org.jpedal.jbig2.decoders.DecodeIntResult;
import org.jpedal.jbig2.decoders.HuffmanDecoder;
import org.jpedal.jbig2.decoders.JBIG2StreamDecoder;
import org.jpedal.jbig2.decoders.MMRDecoder;
import org.jpedal.jbig2.image.BitmapPointer;
import org.jpedal.jbig2.util.BinaryOperation;

public class JBIG2Bitmap {
    private int width;
    private int height;
    private int line;
    private int bitmapNumber;
    private BitSet data;

    public JBIG2Bitmap(int n, int n2) {
        this.width = n;
        this.height = n2;
        this.line = n + 7 >> 3;
        this.data = new BitSet(n * n2);
    }

    public void readBitmap(boolean bl, int n, boolean bl2, boolean bl3, JBIG2Bitmap jBIG2Bitmap, short[] sArray, short[] sArray2, int n2) throws IOException, JBIG2Exception {
        block64: {
            block62: {
                MMRDecoder mMRDecoder;
                block63: {
                    if (!bl) break block62;
                    mMRDecoder = MMRDecoder.getInstance();
                    mMRDecoder.reset();
                    int[] nArray = new int[this.width + 2];
                    int[] nArray2 = new int[this.width + 2];
                    nArray2[0] = nArray2[1] = this.width;
                    for (int i = 0; i < this.height; ++i) {
                        int n3;
                        int n4;
                        int n5 = 0;
                        while (nArray2[n5] < this.width) {
                            nArray[n5] = nArray2[n5];
                            ++n5;
                        }
                        int n6 = this.width;
                        nArray[n5 + 1] = n6;
                        nArray[n5] = n6;
                        int n7 = 0;
                        int n8 = 0;
                        int n9 = 0;
                        block25: do {
                            n4 = mMRDecoder.get2DCode();
                            switch (n4) {
                                case 0: {
                                    if (nArray[n7] >= this.width) continue block25;
                                    n9 = nArray[n7 + 1];
                                    n7 += 2;
                                    break;
                                }
                                case 1: {
                                    int n10;
                                    if (n8 & true) {
                                        n4 = 0;
                                        do {
                                            n10 = mMRDecoder.getBlackCode();
                                            n4 += n10;
                                        } while (n10 >= 64);
                                        n3 = 0;
                                        do {
                                            n10 = mMRDecoder.getWhiteCode();
                                            n3 += n10;
                                        } while (n10 >= 64);
                                    } else {
                                        n4 = 0;
                                        do {
                                            n10 = mMRDecoder.getWhiteCode();
                                            n4 += n10;
                                        } while (n10 >= 64);
                                        n3 = 0;
                                        do {
                                            n10 = mMRDecoder.getBlackCode();
                                            n3 += n10;
                                        } while (n10 >= 64);
                                    }
                                    if (n4 <= 0 && n3 <= 0) continue block25;
                                    int n11 = n8++;
                                    int n12 = n9 + n4;
                                    nArray2[n11] = n12;
                                    n9 = n12;
                                    int n13 = n8++;
                                    int n14 = n9 + n3;
                                    nArray2[n13] = n14;
                                    n9 = n14;
                                    while (nArray[n7] <= n9 && nArray[n7] < this.width) {
                                        n7 += 2;
                                    }
                                    continue block25;
                                }
                                case 2: {
                                    int n15 = n8++;
                                    int n16 = nArray[n7];
                                    nArray2[n15] = n16;
                                    n9 = n16;
                                    if (nArray[n7] >= this.width) continue block25;
                                    ++n7;
                                    break;
                                }
                                case 3: {
                                    int n17 = n8++;
                                    int n18 = nArray[n7] + 1;
                                    nArray2[n17] = n18;
                                    n9 = n18;
                                    if (nArray[n7] >= this.width) continue block25;
                                    ++n7;
                                    while (nArray[n7] <= n9 && nArray[n7] < this.width) {
                                        n7 += 2;
                                    }
                                    continue block25;
                                }
                                case 5: {
                                    int n19 = n8++;
                                    int n20 = nArray[n7] + 2;
                                    nArray2[n19] = n20;
                                    n9 = n20;
                                    if (nArray[n7] >= this.width) continue block25;
                                    ++n7;
                                    while (nArray[n7] <= n9 && nArray[n7] < this.width) {
                                        n7 += 2;
                                    }
                                    continue block25;
                                }
                                case 7: {
                                    int n21 = n8++;
                                    int n22 = nArray[n7] + 3;
                                    nArray2[n21] = n22;
                                    n9 = n22;
                                    if (nArray[n7] >= this.width) continue block25;
                                    ++n7;
                                    while (nArray[n7] <= n9 && nArray[n7] < this.width) {
                                        n7 += 2;
                                    }
                                    continue block25;
                                }
                                case 4: {
                                    int n23 = n8++;
                                    int n24 = nArray[n7] - 1;
                                    nArray2[n23] = n24;
                                    n9 = n24;
                                    n7 = n7 > 0 ? --n7 : ++n7;
                                    while (nArray[n7] <= n9 && nArray[n7] < this.width) {
                                        n7 += 2;
                                    }
                                    continue block25;
                                }
                                case 6: {
                                    int n25 = n8++;
                                    int n26 = nArray[n7] - 2;
                                    nArray2[n25] = n26;
                                    n9 = n26;
                                    n7 = n7 > 0 ? --n7 : ++n7;
                                    while (nArray[n7] <= n9 && nArray[n7] < this.width) {
                                        n7 += 2;
                                    }
                                    continue block25;
                                }
                                case 8: {
                                    int n27 = n8++;
                                    int n28 = nArray[n7] - 3;
                                    nArray2[n27] = n28;
                                    n9 = n28;
                                    n7 = n7 > 0 ? --n7 : ++n7;
                                    while (nArray[n7] <= n9 && nArray[n7] < this.width) {
                                        n7 += 2;
                                    }
                                    continue block25;
                                }
                                default: {
                                    if (!JBIG2StreamDecoder.debug) continue block25;
                                    System.out.println("Illegal code in JBIG2 MMR bitmap data");
                                }
                            }
                        } while (n9 < this.width);
                        nArray2[n8++] = this.width;
                        n4 = 0;
                        while (nArray2[n4] < this.width) {
                            for (n3 = nArray2[n4]; n3 < nArray2[n4 + 1]; ++n3) {
                                this.setPixel(n3, i, 1);
                            }
                            n4 += 2;
                        }
                    }
                    if (n2 < 0) break block63;
                    mMRDecoder.skipTo(n2);
                    break block64;
                }
                if (mMRDecoder.get24Bits() == 4097L || !JBIG2StreamDecoder.debug) break block64;
                System.out.println("Missing EOFB in JBIG2 MMR bitmap data");
                break block64;
            }
            ArithmeticDecoder arithmeticDecoder = ArithmeticDecoder.getInstance();
            BitmapPointer bitmapPointer = new BitmapPointer(this);
            BitmapPointer bitmapPointer2 = new BitmapPointer(this);
            BitmapPointer bitmapPointer3 = new BitmapPointer(this);
            BitmapPointer bitmapPointer4 = new BitmapPointer(this);
            BitmapPointer bitmapPointer5 = new BitmapPointer(this);
            BitmapPointer bitmapPointer6 = new BitmapPointer(this);
            long l = 0L;
            if (bl2) {
                switch (n) {
                    case 0: {
                        l = 14675L;
                        break;
                    }
                    case 1: {
                        l = 1946L;
                        break;
                    }
                    case 2: {
                        l = 227L;
                        break;
                    }
                    case 3: {
                        l = 394L;
                    }
                }
            }
            boolean bl4 = false;
            block39: for (int i = 0; i < this.height; ++i) {
                int n29;
                if (bl2) {
                    n29 = arithmeticDecoder.decodeBit(l, arithmeticDecoder.genericRegionStats);
                    if (n29 != 0) {
                        boolean bl5 = bl4 = !bl4;
                    }
                    if (bl4) {
                        this.duplicateRow(i, i - 1);
                        continue;
                    }
                }
                switch (n) {
                    case 0: {
                        long l2;
                        int n30;
                        bitmapPointer.setPointer(0, i - 2);
                        long l3 = bitmapPointer.nextPixel();
                        l3 = BinaryOperation.bit32Shift(l3, 1, 0) | (long)bitmapPointer.nextPixel();
                        bitmapPointer2.setPointer(0, i - 1);
                        long l4 = bitmapPointer2.nextPixel();
                        l4 = BinaryOperation.bit32Shift(l4, 1, 0) | (long)bitmapPointer2.nextPixel();
                        l4 = BinaryOperation.bit32Shift(l4, 1, 0) | (long)bitmapPointer2.nextPixel();
                        long l5 = 0L;
                        bitmapPointer3.setPointer(sArray[0], i + sArray2[0]);
                        bitmapPointer4.setPointer(sArray[1], i + sArray2[1]);
                        bitmapPointer5.setPointer(sArray[2], i + sArray2[2]);
                        bitmapPointer6.setPointer(sArray[3], i + sArray2[3]);
                        for (n30 = 0; n30 < this.width; ++n30) {
                            l2 = BinaryOperation.bit32Shift(l3, 13, 0) | BinaryOperation.bit32Shift(l4, 8, 0) | BinaryOperation.bit32Shift(l5, 4, 0) | (long)(bitmapPointer3.nextPixel() << 3) | (long)(bitmapPointer4.nextPixel() << 2) | (long)(bitmapPointer5.nextPixel() << 1) | (long)bitmapPointer6.nextPixel();
                            if (bl3 && jBIG2Bitmap.getPixel(n30, i) != 0) {
                                n29 = 0;
                            } else {
                                n29 = arithmeticDecoder.decodeBit(l2, arithmeticDecoder.genericRegionStats);
                                if (n29 != 0) {
                                    this.setPixel(n30, i, 1);
                                }
                            }
                            l3 = (BinaryOperation.bit32Shift(l3, 1, 0) | (long)bitmapPointer.nextPixel()) & 7L;
                            l4 = (BinaryOperation.bit32Shift(l4, 1, 0) | (long)bitmapPointer2.nextPixel()) & 0x1FL;
                            l5 = (BinaryOperation.bit32Shift(l5, 1, 0) | (long)n29) & 0xFL;
                        }
                        continue block39;
                    }
                    case 1: {
                        long l2;
                        int n30;
                        bitmapPointer.setPointer(0, i - 2);
                        long l3 = bitmapPointer.nextPixel();
                        l3 = BinaryOperation.bit32Shift(l3, 1, 0) | (long)bitmapPointer.nextPixel();
                        l3 = BinaryOperation.bit32Shift(l3, 1, 0) | (long)bitmapPointer.nextPixel();
                        bitmapPointer2.setPointer(0, i - 1);
                        long l4 = bitmapPointer2.nextPixel();
                        l4 = BinaryOperation.bit32Shift(l4, 1, 0) | (long)bitmapPointer2.nextPixel();
                        l4 = BinaryOperation.bit32Shift(l4, 1, 0) | (long)bitmapPointer2.nextPixel();
                        long l5 = 0L;
                        bitmapPointer3.setPointer(sArray[0], i + sArray2[0]);
                        for (n30 = 0; n30 < this.width; ++n30) {
                            l2 = BinaryOperation.bit32Shift(l3, 9, 0) | BinaryOperation.bit32Shift(l4, 4, 0) | BinaryOperation.bit32Shift(l5, 1, 0) | (long)bitmapPointer3.nextPixel();
                            if (bl3 && jBIG2Bitmap.getPixel(n30, i) != 0) {
                                n29 = 0;
                            } else {
                                n29 = arithmeticDecoder.decodeBit(l2, arithmeticDecoder.genericRegionStats);
                                if (n29 != 0) {
                                    this.setPixel(n30, i, 1);
                                }
                            }
                            l3 = (BinaryOperation.bit32Shift(l3, 1, 0) | (long)bitmapPointer.nextPixel()) & 0xFL;
                            l4 = (BinaryOperation.bit32Shift(l4, 1, 0) | (long)bitmapPointer2.nextPixel()) & 0x1FL;
                            l5 = (BinaryOperation.bit32Shift(l5, 1, 0) | (long)n29) & 7L;
                        }
                        continue block39;
                    }
                    case 2: {
                        long l2;
                        int n30;
                        bitmapPointer.setPointer(0, i - 2);
                        long l3 = bitmapPointer.nextPixel();
                        l3 = BinaryOperation.bit32Shift(l3, 1, 0) | (long)bitmapPointer.nextPixel();
                        bitmapPointer2.setPointer(0, i - 1);
                        long l4 = bitmapPointer2.nextPixel();
                        l4 = BinaryOperation.bit32Shift(l4, 1, 0) | (long)bitmapPointer2.nextPixel();
                        long l5 = 0L;
                        bitmapPointer3.setPointer(sArray[0], i + sArray2[0]);
                        for (n30 = 0; n30 < this.width; ++n30) {
                            l2 = BinaryOperation.bit32Shift(l3, 7, 0) | BinaryOperation.bit32Shift(l4, 3, 0) | BinaryOperation.bit32Shift(l5, 1, 0) | (long)bitmapPointer3.nextPixel();
                            if (bl3 && jBIG2Bitmap.getPixel(n30, i) != 0) {
                                n29 = 0;
                            } else {
                                n29 = arithmeticDecoder.decodeBit(l2, arithmeticDecoder.genericRegionStats);
                                if (n29 != 0) {
                                    this.setPixel(n30, i, 1);
                                }
                            }
                            l3 = (BinaryOperation.bit32Shift(l3, 1, 0) | (long)bitmapPointer.nextPixel()) & 7L;
                            l4 = (BinaryOperation.bit32Shift(l4, 1, 0) | (long)bitmapPointer2.nextPixel()) & 0xFL;
                            l5 = (BinaryOperation.bit32Shift(l5, 1, 0) | (long)n29) & 3L;
                        }
                        continue block39;
                    }
                    case 3: {
                        long l2;
                        int n30;
                        bitmapPointer2.setPointer(0, i - 1);
                        long l4 = bitmapPointer2.nextPixel();
                        l4 = BinaryOperation.bit32Shift(l4, 1, 0) | (long)bitmapPointer2.nextPixel();
                        long l5 = 0L;
                        bitmapPointer3.setPointer(sArray[0], i + sArray2[0]);
                        for (n30 = 0; n30 < this.width; ++n30) {
                            l2 = BinaryOperation.bit32Shift(l4, 5, 0) | BinaryOperation.bit32Shift(l5, 1, 0) | (long)bitmapPointer3.nextPixel();
                            if (bl3 && jBIG2Bitmap.getPixel(n30, i) != 0) {
                                n29 = 0;
                            } else {
                                n29 = arithmeticDecoder.decodeBit(l2, arithmeticDecoder.genericRegionStats);
                                if (n29 != 0) {
                                    this.setPixel(n30, i, 1);
                                }
                            }
                            l4 = (BinaryOperation.bit32Shift(l4, 1, 0) | (long)bitmapPointer2.nextPixel()) & 0x1FL;
                            l5 = (BinaryOperation.bit32Shift(l5, 1, 0) | (long)n29) & 0xFL;
                        }
                        continue block39;
                    }
                }
            }
        }
    }

    public void readGenericRefinementRegion(int n, boolean bl, JBIG2Bitmap jBIG2Bitmap, int n2, int n3, short[] sArray, short[] sArray2) throws IOException, JBIG2Exception {
        BitmapPointer bitmapPointer;
        BitmapPointer bitmapPointer2;
        BitmapPointer bitmapPointer3;
        BitmapPointer bitmapPointer4;
        BitmapPointer bitmapPointer5;
        BitmapPointer bitmapPointer6;
        BitmapPointer bitmapPointer7;
        BitmapPointer bitmapPointer8;
        BitmapPointer bitmapPointer9;
        BitmapPointer bitmapPointer10;
        long l;
        ArithmeticDecoder arithmeticDecoder = ArithmeticDecoder.getInstance();
        if (n != 0) {
            l = 8L;
            bitmapPointer10 = new BitmapPointer(this);
            bitmapPointer9 = new BitmapPointer(this);
            bitmapPointer8 = new BitmapPointer(jBIG2Bitmap);
            bitmapPointer7 = new BitmapPointer(jBIG2Bitmap);
            bitmapPointer6 = new BitmapPointer(jBIG2Bitmap);
            bitmapPointer5 = new BitmapPointer(this);
            bitmapPointer4 = new BitmapPointer(this);
            bitmapPointer3 = new BitmapPointer(jBIG2Bitmap);
            bitmapPointer2 = new BitmapPointer(jBIG2Bitmap);
            bitmapPointer = new BitmapPointer(jBIG2Bitmap);
        } else {
            l = 16L;
            bitmapPointer10 = new BitmapPointer(this);
            bitmapPointer9 = new BitmapPointer(this);
            bitmapPointer8 = new BitmapPointer(jBIG2Bitmap);
            bitmapPointer7 = new BitmapPointer(jBIG2Bitmap);
            bitmapPointer6 = new BitmapPointer(jBIG2Bitmap);
            bitmapPointer5 = new BitmapPointer(this);
            bitmapPointer4 = new BitmapPointer(jBIG2Bitmap);
            bitmapPointer3 = new BitmapPointer(jBIG2Bitmap);
            bitmapPointer2 = new BitmapPointer(jBIG2Bitmap);
            bitmapPointer = new BitmapPointer(jBIG2Bitmap);
        }
        boolean bl2 = false;
        for (int i = 0; i < this.height; ++i) {
            long l2;
            int n4;
            int n5;
            long l3;
            long l4;
            long l5;
            long l6;
            long l7;
            long l8;
            if (n != 0) {
                bitmapPointer10.setPointer(0, i - 1);
                l8 = bitmapPointer10.nextPixel();
                bitmapPointer9.setPointer(-1, i);
                bitmapPointer8.setPointer(-n2, i - 1 - n3);
                bitmapPointer7.setPointer(-1 - n2, i - n3);
                l7 = bitmapPointer7.nextPixel();
                l7 = BinaryOperation.bit32Shift(l7, 1, 0) | (long)bitmapPointer7.nextPixel();
                bitmapPointer6.setPointer(-n2, i + 1 - n3);
                l6 = bitmapPointer6.nextPixel();
                l5 = 0L;
                l4 = 0L;
                l3 = 0L;
                if (bl) {
                    bitmapPointer3.setPointer(-1 - n2, i - 1 - n3);
                    l3 = bitmapPointer3.nextPixel();
                    l3 = BinaryOperation.bit32Shift(l3, 1, 0) | (long)bitmapPointer3.nextPixel();
                    l3 = BinaryOperation.bit32Shift(l3, 1, 0) | (long)bitmapPointer3.nextPixel();
                    bitmapPointer2.setPointer(-1 - n2, i - n3);
                    l4 = bitmapPointer2.nextPixel();
                    l4 = BinaryOperation.bit32Shift(l4, 1, 0) | (long)bitmapPointer2.nextPixel();
                    l4 = BinaryOperation.bit32Shift(l4, 1, 0) | (long)bitmapPointer2.nextPixel();
                    bitmapPointer.setPointer(-1 - n2, i + 1 - n3);
                    l5 = bitmapPointer.nextPixel();
                    l5 = BinaryOperation.bit32Shift(l5, 1, 0) | (long)bitmapPointer.nextPixel();
                    l5 = BinaryOperation.bit32Shift(l5, 1, 0) | (long)bitmapPointer.nextPixel();
                }
                for (n5 = 0; n5 < this.width; ++n5) {
                    l8 = (BinaryOperation.bit32Shift(l8, 1, 0) | (long)bitmapPointer10.nextPixel()) & 7L;
                    l7 = (BinaryOperation.bit32Shift(l7, 1, 0) | (long)bitmapPointer7.nextPixel()) & 7L;
                    l6 = (BinaryOperation.bit32Shift(l6, 1, 0) | (long)bitmapPointer6.nextPixel()) & 3L;
                    if (bl) {
                        l3 = (BinaryOperation.bit32Shift(l3, 1, 0) | (long)bitmapPointer3.nextPixel()) & 7L;
                        l4 = (BinaryOperation.bit32Shift(l4, 1, 0) | (long)bitmapPointer2.nextPixel()) & 7L;
                        l5 = (BinaryOperation.bit32Shift(l5, 1, 0) | (long)bitmapPointer.nextPixel()) & 7L;
                        n4 = arithmeticDecoder.decodeBit(l, arithmeticDecoder.refinementRegionStats);
                        if (n4 != 0) {
                            boolean bl3 = bl2 = !bl2;
                        }
                        if (l3 == 0L && l4 == 0L && l5 == 0L) {
                            this.setPixel(n5, i, 0);
                            continue;
                        }
                        if (l3 == 7L && l4 == 7L && l5 == 7L) {
                            this.setPixel(n5, i, 1);
                            continue;
                        }
                    }
                    if ((n4 = arithmeticDecoder.decodeBit(l2 = BinaryOperation.bit32Shift(l8, 7, 0) | (long)(bitmapPointer9.nextPixel() << 6) | (long)(bitmapPointer8.nextPixel() << 5) | BinaryOperation.bit32Shift(l7, 2, 0) | l6, arithmeticDecoder.refinementRegionStats)) != 1) continue;
                    this.setPixel(n5, i, 1);
                }
                continue;
            }
            bitmapPointer10.setPointer(0, i - 1);
            l8 = bitmapPointer10.nextPixel();
            bitmapPointer9.setPointer(-1, i);
            bitmapPointer8.setPointer(-n2, i - 1 - n3);
            long l9 = bitmapPointer8.nextPixel();
            bitmapPointer7.setPointer(-1 - n2, i - n3);
            l7 = bitmapPointer7.nextPixel();
            l7 = BinaryOperation.bit32Shift(l7, 1, 0) | (long)bitmapPointer7.nextPixel();
            bitmapPointer6.setPointer(-1 - n2, i + 1 - n3);
            l6 = bitmapPointer6.nextPixel();
            l6 = BinaryOperation.bit32Shift(l6, 1, 0) | (long)bitmapPointer6.nextPixel();
            bitmapPointer5.setPointer(sArray[0], i + sArray2[0]);
            bitmapPointer4.setPointer(sArray[1] - n2, i + sArray2[1] - n3);
            l5 = 0L;
            l4 = 0L;
            l3 = 0L;
            if (bl) {
                bitmapPointer3.setPointer(-1 - n2, i - 1 - n3);
                l3 = bitmapPointer3.nextPixel();
                l3 = BinaryOperation.bit32Shift(l3, 1, 0) | (long)bitmapPointer3.nextPixel();
                l3 = BinaryOperation.bit32Shift(l3, 1, 0) | (long)bitmapPointer3.nextPixel();
                bitmapPointer2.setPointer(-1 - n2, i - n3);
                l4 = bitmapPointer2.nextPixel();
                l4 = BinaryOperation.bit32Shift(l4, 1, 0) | (long)bitmapPointer2.nextPixel();
                l4 = BinaryOperation.bit32Shift(l4, 1, 0) | (long)bitmapPointer2.nextPixel();
                bitmapPointer.setPointer(-1 - n2, i + 1 - n3);
                l5 = bitmapPointer.nextPixel();
                l5 = BinaryOperation.bit32Shift(l5, 1, 0) | (long)bitmapPointer.nextPixel();
                l5 = BinaryOperation.bit32Shift(l5, 1, 0) | (long)bitmapPointer.nextPixel();
            }
            for (n5 = 0; n5 < this.width; ++n5) {
                l8 = (BinaryOperation.bit32Shift(l8, 1, 0) | (long)bitmapPointer10.nextPixel()) & 3L;
                l9 = (BinaryOperation.bit32Shift(l9, 1, 0) | (long)bitmapPointer8.nextPixel()) & 3L;
                l7 = (BinaryOperation.bit32Shift(l7, 1, 0) | (long)bitmapPointer7.nextPixel()) & 7L;
                l6 = (BinaryOperation.bit32Shift(l6, 1, 0) | (long)bitmapPointer6.nextPixel()) & 7L;
                if (bl) {
                    l3 = (BinaryOperation.bit32Shift(l3, 1, 0) | (long)bitmapPointer3.nextPixel()) & 7L;
                    l4 = (BinaryOperation.bit32Shift(l4, 1, 0) | (long)bitmapPointer2.nextPixel()) & 7L;
                    l5 = (BinaryOperation.bit32Shift(l5, 1, 0) | (long)bitmapPointer.nextPixel()) & 7L;
                    n4 = arithmeticDecoder.decodeBit(l, arithmeticDecoder.refinementRegionStats);
                    if (n4 == 1) {
                        boolean bl4 = bl2 = !bl2;
                    }
                    if (l3 == 0L && l4 == 0L && l5 == 0L) {
                        this.setPixel(n5, i, 0);
                        continue;
                    }
                    if (l3 == 7L && l4 == 7L && l5 == 7L) {
                        this.setPixel(n5, i, 1);
                        continue;
                    }
                }
                if ((n4 = arithmeticDecoder.decodeBit(l2 = BinaryOperation.bit32Shift(l8, 11, 0) | (long)(bitmapPointer9.nextPixel() << 10) | BinaryOperation.bit32Shift(l9, 8, 0) | BinaryOperation.bit32Shift(l7, 5, 0) | BinaryOperation.bit32Shift(l6, 2, 0) | (long)(bitmapPointer5.nextPixel() << 1) | (long)bitmapPointer4.nextPixel(), arithmeticDecoder.refinementRegionStats)) != 1) continue;
                this.setPixel(n5, i, 1);
            }
        }
    }

    public void readTextRegion(boolean bl, boolean bl2, int n, int n2, int n3, int[][] nArray, int n4, JBIG2Bitmap[] jBIG2BitmapArray, int n5, int n6, boolean bl3, int n7, int n8, int[][] nArray2, int[][] nArray3, int[][] nArray4, int[][] nArray5, int[][] nArray6, int[][] nArray7, int[][] nArray8, int[][] nArray9, int n9, short[] sArray, short[] sArray2, JBIG2StreamDecoder jBIG2StreamDecoder) throws JBIG2Exception, IOException {
        int n10 = 1 << n2;
        this.clear(n5);
        HuffmanDecoder huffmanDecoder = HuffmanDecoder.getInstance();
        ArithmeticDecoder arithmeticDecoder = ArithmeticDecoder.getInstance();
        int n11 = bl ? huffmanDecoder.decodeInt(nArray4).intResult() : arithmeticDecoder.decodeInt(arithmeticDecoder.iadtStats).intResult();
        n11 *= -n10;
        int n12 = 0;
        block12: for (int i = 0; i < n; ++i) {
            int n13 = bl ? huffmanDecoder.decodeInt(nArray4).intResult() : arithmeticDecoder.decodeInt(arithmeticDecoder.iadtStats).intResult();
            n11 += n13 * n10;
            int n14 = bl ? huffmanDecoder.decodeInt(nArray2).intResult() : arithmeticDecoder.decodeInt(arithmeticDecoder.iafsStats).intResult();
            int n15 = n12 += n14;
            while (true) {
                int n16;
                int n17;
                n13 = n10 == 1 ? 0 : (bl ? jBIG2StreamDecoder.readBits(n2) : arithmeticDecoder.decodeInt(arithmeticDecoder.iaitStats).intResult());
                int n18 = n11 + n13;
                long l = bl ? (nArray != null ? (long)huffmanDecoder.decodeInt(nArray).intResult() : (long)jBIG2StreamDecoder.readBits(n4)) : arithmeticDecoder.decodeIAID(n4, arithmeticDecoder.iaidStats);
                if (l >= (long)n3) {
                    if (!JBIG2StreamDecoder.debug) continue block12;
                    System.out.println("Invalid symbol number in JBIG2 text region");
                    continue block12;
                }
                JBIG2Bitmap jBIG2Bitmap = null;
                int n19 = bl2 ? (bl ? jBIG2StreamDecoder.readBit() : arithmeticDecoder.decodeInt(arithmeticDecoder.iariStats).intResult()) : 0;
                if (n19 != 0) {
                    int n20;
                    int n21;
                    if (bl) {
                        n17 = huffmanDecoder.decodeInt(nArray5).intResult();
                        n16 = huffmanDecoder.decodeInt(nArray6).intResult();
                        n21 = huffmanDecoder.decodeInt(nArray7).intResult();
                        n20 = huffmanDecoder.decodeInt(nArray8).intResult();
                        jBIG2StreamDecoder.consumeRemainingBits();
                        arithmeticDecoder.start();
                    } else {
                        n17 = arithmeticDecoder.decodeInt(arithmeticDecoder.iardwStats).intResult();
                        n16 = arithmeticDecoder.decodeInt(arithmeticDecoder.iardhStats).intResult();
                        n21 = arithmeticDecoder.decodeInt(arithmeticDecoder.iardxStats).intResult();
                        n20 = arithmeticDecoder.decodeInt(arithmeticDecoder.iardyStats).intResult();
                    }
                    n21 = (n17 >= 0 ? n17 : n17 - 1) / 2 + n21;
                    n20 = (n16 >= 0 ? n16 : n16 - 1) / 2 + n20;
                    jBIG2Bitmap = new JBIG2Bitmap(n17 + jBIG2BitmapArray[(int)l].width, n16 + jBIG2BitmapArray[(int)l].height);
                    jBIG2Bitmap.readGenericRefinementRegion(n9, false, jBIG2BitmapArray[(int)l], n21, n20, sArray, sArray2);
                } else {
                    jBIG2Bitmap = jBIG2BitmapArray[(int)l];
                }
                n17 = jBIG2Bitmap.getWidth() - 1;
                n16 = jBIG2Bitmap.getHeight() - 1;
                if (bl3) {
                    switch (n7) {
                        case 0: {
                            this.combine(jBIG2Bitmap, n18, n15, n6);
                            break;
                        }
                        case 1: {
                            this.combine(jBIG2Bitmap, n18, n15, n6);
                            break;
                        }
                        case 2: {
                            this.combine(jBIG2Bitmap, n18 - n17, n15, n6);
                            break;
                        }
                        case 3: {
                            this.combine(jBIG2Bitmap, n18 - n17, n15, n6);
                        }
                    }
                    n15 += n16;
                    continue block12;
                }
                switch (n7) {
                    case 0: {
                        this.combine(jBIG2Bitmap, n15, n18 - n16, n6);
                        break;
                    }
                    case 1: {
                        this.combine(jBIG2Bitmap, n15, n18, n6);
                        break;
                    }
                    case 2: {
                        this.combine(jBIG2Bitmap, n15, n18 - n16, n6);
                        break;
                    }
                    case 3: {
                        this.combine(jBIG2Bitmap, n15, n18, n6);
                    }
                }
                n15 += n17;
                DecodeIntResult decodeIntResult = bl ? huffmanDecoder.decodeInt(nArray3) : arithmeticDecoder.decodeInt(arithmeticDecoder.iadsStats);
                if (!decodeIntResult.booleanResult()) continue block12;
                n14 = decodeIntResult.intResult();
                n15 += n8 + n14;
            }
        }
    }

    public void clear(int n) {
        this.data.set(0, this.data.size(), n == 1);
    }

    public void combine(JBIG2Bitmap jBIG2Bitmap, int n, int n2, long l) {
        int n3 = jBIG2Bitmap.getWidth();
        int n4 = jBIG2Bitmap.getHeight();
        int n5 = 0;
        int n6 = 0;
        for (int i = n2; i < n2 + n4; ++i) {
            for (int j = n; j < n + n3; ++j) {
                int n7 = jBIG2Bitmap.getPixel(n6, n5);
                switch ((int)l) {
                    case 0: {
                        this.setPixel(j, i, this.getPixel(j, i) | n7);
                        break;
                    }
                    case 1: {
                        this.setPixel(j, i, this.getPixel(j, i) & n7);
                        break;
                    }
                    case 2: {
                        this.setPixel(j, i, this.getPixel(j, i) ^ n7);
                        break;
                    }
                    case 3: {
                        if (this.getPixel(j, i) == 1 && n7 == 1 || this.getPixel(j, i) == 0 && n7 == 0) {
                            this.setPixel(j, i, 1);
                            break;
                        }
                        this.setPixel(j, i, 0);
                        break;
                    }
                    case 4: {
                        this.setPixel(j, i, n7);
                    }
                }
                ++n6;
            }
            n6 = 0;
            ++n5;
        }
    }

    private void duplicateRow(int n, int n2) {
        for (int i = 0; i < this.width; ++i) {
            this.setPixel(i, n, this.getPixel(i, n2));
        }
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public byte[] getData(boolean bl) {
        int n;
        byte[] byArray = new byte[this.height * this.line];
        int n2 = 0;
        int n3 = 0;
        for (n = 0; n < this.height; ++n) {
            for (int i = 0; i < this.width; ++i) {
                if (this.data.get(n2)) {
                    int n4 = (n2 + n3) / 8;
                    int n5 = (n2 + n3) % 8;
                    int n6 = n4;
                    byArray[n6] = (byte)(byArray[n6] | 1 << 7 - n5);
                }
                ++n2;
            }
            n3 = this.line * 8 * (n + 1) - n2;
        }
        if (bl) {
            n = 0;
            while (n < byArray.length) {
                int n7 = n++;
                byArray[n7] = (byte)(byArray[n7] ^ 0xFF);
            }
        }
        return byArray;
    }

    public JBIG2Bitmap getSlice(int n, int n2, int n3, int n4) {
        JBIG2Bitmap jBIG2Bitmap = new JBIG2Bitmap(n3, n4);
        int n5 = 0;
        int n6 = 0;
        for (int i = n2; i < n4; ++i) {
            for (int j = n; j < n + n3; ++j) {
                jBIG2Bitmap.setPixel(n6, n5, this.getPixel(j, i));
                ++n6;
            }
            n6 = 0;
            ++n5;
        }
        return jBIG2Bitmap;
    }

    private void setPixel(int n, int n2, BitSet bitSet, int n3) {
        int n4 = n2 * this.width + n;
        bitSet.set(n4, n3 == 1);
    }

    public void setPixel(int n, int n2, int n3) {
        this.setPixel(n, n2, this.data, n3);
    }

    public int getPixel(int n, int n2) {
        return this.data.get(n2 * this.width + n) ? 1 : 0;
    }

    public void expand(int n, int n2) {
        BitSet bitSet = new BitSet(n * this.width);
        for (int i = 0; i < this.height; ++i) {
            for (int j = 0; j < this.width; ++j) {
                this.setPixel(j, i, bitSet, this.getPixel(j, i));
            }
        }
        this.height = n;
        this.data = bitSet;
    }

    public void setBitmapNumber(int n) {
        this.bitmapNumber = n;
    }

    public int getBitmapNumber() {
        return this.bitmapNumber;
    }
}

