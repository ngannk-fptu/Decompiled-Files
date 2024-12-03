/*
 * Decompiled with CFR 0.152.
 */
package org.jpedal.jbig2.decoders;

import java.io.IOException;
import org.jpedal.jbig2.JBIG2Exception;
import org.jpedal.jbig2.decoders.ArithmeticDecoderStats;
import org.jpedal.jbig2.decoders.DecodeIntResult;
import org.jpedal.jbig2.io.StreamReader;
import org.jpedal.jbig2.util.BinaryOperation;

public class ArithmeticDecoder {
    private StreamReader reader;
    private static ArithmeticDecoder instance;
    public ArithmeticDecoderStats genericRegionStats;
    public ArithmeticDecoderStats refinementRegionStats;
    public ArithmeticDecoderStats iadhStats;
    public ArithmeticDecoderStats iadwStats;
    public ArithmeticDecoderStats iaexStats;
    public ArithmeticDecoderStats iaaiStats;
    public ArithmeticDecoderStats iadtStats;
    public ArithmeticDecoderStats iaitStats;
    public ArithmeticDecoderStats iafsStats;
    public ArithmeticDecoderStats iadsStats;
    public ArithmeticDecoderStats iardxStats;
    public ArithmeticDecoderStats iardyStats;
    public ArithmeticDecoderStats iardwStats;
    public ArithmeticDecoderStats iardhStats;
    public ArithmeticDecoderStats iariStats;
    public ArithmeticDecoderStats iaidStats;
    int[] contextSize = new int[]{16, 13, 10, 10};
    int[] referredToContextSize = new int[]{13, 10};
    long buffer0;
    long buffer1;
    long c;
    long a;
    long previous;
    int counter;
    int[] qeTable = new int[]{1442906112, 872480768, 0x18010000, 180420608, 86048768, 0x2210000, 1442906112, 1409351680, 1208025088, 939589632, 0x30010000, 604045312, 0x1C010000, 0x16010000, 1442906112, 1409351680, 0x51010000, 1208025088, 939589632, 872480768, 0x30010000, 671154176, 604045312, 0x22010000, 0x1C010000, 0x18010000, 0x16010000, 0x14010000, 0x12010000, 0x11010000, 180420608, 163643392, 144769024, 86048768, 0x4410000, 44105728, 0x2210000, 0x1410000, 0x1110000, 0x850000, 0x490000, 0x250000, 0x150000, 589824, 327680, 65536, 1442906112};
    int[] nmpsTable = new int[]{1, 2, 3, 4, 5, 38, 7, 8, 9, 10, 11, 12, 13, 29, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 45, 46};
    int[] nlpsTable = new int[]{1, 6, 9, 12, 29, 33, 6, 14, 14, 14, 17, 18, 20, 21, 14, 14, 15, 16, 17, 18, 19, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 46};
    int[] switchTable = new int[]{1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    private ArithmeticDecoder() {
    }

    private ArithmeticDecoder(StreamReader streamReader) {
        this.reader = streamReader;
        this.genericRegionStats = new ArithmeticDecoderStats(2);
        this.refinementRegionStats = new ArithmeticDecoderStats(2);
        this.iadhStats = new ArithmeticDecoderStats(512);
        this.iadwStats = new ArithmeticDecoderStats(512);
        this.iaexStats = new ArithmeticDecoderStats(512);
        this.iaaiStats = new ArithmeticDecoderStats(512);
        this.iadtStats = new ArithmeticDecoderStats(512);
        this.iaitStats = new ArithmeticDecoderStats(512);
        this.iafsStats = new ArithmeticDecoderStats(512);
        this.iadsStats = new ArithmeticDecoderStats(512);
        this.iardxStats = new ArithmeticDecoderStats(512);
        this.iardyStats = new ArithmeticDecoderStats(512);
        this.iardwStats = new ArithmeticDecoderStats(512);
        this.iardhStats = new ArithmeticDecoderStats(512);
        this.iariStats = new ArithmeticDecoderStats(512);
        this.iaidStats = new ArithmeticDecoderStats(2);
    }

    public static void initiate(StreamReader streamReader) {
        instance = new ArithmeticDecoder(streamReader);
    }

    public static ArithmeticDecoder getInstance() throws JBIG2Exception {
        if (instance == null) {
            throw new JBIG2Exception("JArithmeticDecoder is uninitiated. Call JArithmeticDecoder.initiate(StreamReader reader) before getInstance()");
        }
        return instance;
    }

    public void resetIntStats(int n) {
        this.iadhStats.reset();
        this.iadwStats.reset();
        this.iaexStats.reset();
        this.iaaiStats.reset();
        this.iadtStats.reset();
        this.iaitStats.reset();
        this.iafsStats.reset();
        this.iadsStats.reset();
        this.iardxStats.reset();
        this.iardyStats.reset();
        this.iardwStats.reset();
        this.iardhStats.reset();
        this.iariStats.reset();
        if (this.iaidStats.getContextSize() == 1 << n + 1) {
            this.iaidStats.reset();
        } else {
            this.iaidStats = new ArithmeticDecoderStats(1 << n + 1);
        }
    }

    public void resetGenericStats(int n, ArithmeticDecoderStats arithmeticDecoderStats) {
        int n2 = this.contextSize[n];
        if (arithmeticDecoderStats != null && arithmeticDecoderStats.getContextSize() == n2) {
            if (this.genericRegionStats.getContextSize() == n2) {
                this.genericRegionStats.overwrite(arithmeticDecoderStats);
            } else {
                this.genericRegionStats = arithmeticDecoderStats.copy();
            }
        } else if (this.genericRegionStats.getContextSize() == n2) {
            this.genericRegionStats.reset();
        } else {
            this.genericRegionStats = new ArithmeticDecoderStats(1 << n2);
        }
    }

    public void resetRefinementStats(int n, ArithmeticDecoderStats arithmeticDecoderStats) {
        int n2 = this.referredToContextSize[n];
        if (arithmeticDecoderStats != null && arithmeticDecoderStats.getContextSize() == n2) {
            if (this.refinementRegionStats.getContextSize() == n2) {
                this.refinementRegionStats.overwrite(arithmeticDecoderStats);
            } else {
                this.refinementRegionStats = arithmeticDecoderStats.copy();
            }
        } else if (this.refinementRegionStats.getContextSize() == n2) {
            this.refinementRegionStats.reset();
        } else {
            this.refinementRegionStats = new ArithmeticDecoderStats(1 << n2);
        }
    }

    public void start() throws IOException {
        this.buffer0 = this.reader.readByte();
        this.buffer1 = this.reader.readByte();
        this.c = BinaryOperation.bit32Shift(this.buffer0 ^ 0xFFL, 16, 0);
        this.readByte();
        this.c = BinaryOperation.bit32Shift(this.c, 7, 0);
        this.counter -= 7;
        this.a = 0x80000000L;
    }

    public DecodeIntResult decodeInt(ArithmeticDecoderStats arithmeticDecoderStats) throws IOException {
        int n;
        long l;
        this.previous = 1L;
        int n2 = this.decodeIntBit(arithmeticDecoderStats);
        if (this.decodeIntBit(arithmeticDecoderStats) != 0) {
            if (this.decodeIntBit(arithmeticDecoderStats) != 0) {
                if (this.decodeIntBit(arithmeticDecoderStats) != 0) {
                    if (this.decodeIntBit(arithmeticDecoderStats) != 0) {
                        if (this.decodeIntBit(arithmeticDecoderStats) != 0) {
                            l = 0L;
                            for (n = 0; n < 32; ++n) {
                                l = BinaryOperation.bit32Shift(l, 1, 0) | (long)this.decodeIntBit(arithmeticDecoderStats);
                            }
                            l += 4436L;
                        } else {
                            l = 0L;
                            for (n = 0; n < 12; ++n) {
                                l = BinaryOperation.bit32Shift(l, 1, 0) | (long)this.decodeIntBit(arithmeticDecoderStats);
                            }
                            l += 340L;
                        }
                    } else {
                        l = 0L;
                        for (n = 0; n < 8; ++n) {
                            l = BinaryOperation.bit32Shift(l, 1, 0) | (long)this.decodeIntBit(arithmeticDecoderStats);
                        }
                        l += 84L;
                    }
                } else {
                    l = 0L;
                    for (n = 0; n < 6; ++n) {
                        l = BinaryOperation.bit32Shift(l, 1, 0) | (long)this.decodeIntBit(arithmeticDecoderStats);
                    }
                    l += 20L;
                }
            } else {
                l = this.decodeIntBit(arithmeticDecoderStats);
                l = BinaryOperation.bit32Shift(l, 1, 0) | (long)this.decodeIntBit(arithmeticDecoderStats);
                l = BinaryOperation.bit32Shift(l, 1, 0) | (long)this.decodeIntBit(arithmeticDecoderStats);
                l = BinaryOperation.bit32Shift(l, 1, 0) | (long)this.decodeIntBit(arithmeticDecoderStats);
                l += 4L;
            }
        } else {
            l = this.decodeIntBit(arithmeticDecoderStats);
            l = BinaryOperation.bit32Shift(l, 1, 0) | (long)this.decodeIntBit(arithmeticDecoderStats);
        }
        if (n2 != 0) {
            if (l == 0L) {
                return new DecodeIntResult((int)l, false);
            }
            n = (int)(-l);
        } else {
            n = (int)l;
        }
        return new DecodeIntResult(n, true);
    }

    public long decodeIAID(long l, ArithmeticDecoderStats arithmeticDecoderStats) throws IOException {
        this.previous = 1L;
        for (long i = 0L; i < l; ++i) {
            int n = this.decodeBit(this.previous, arithmeticDecoderStats);
            this.previous = BinaryOperation.bit32Shift(this.previous, 1, 0) | (long)n;
        }
        return this.previous - (long)(1 << (int)l);
    }

    public int decodeBit(long l, ArithmeticDecoderStats arithmeticDecoderStats) throws IOException {
        int n;
        int n2 = BinaryOperation.bit8Shift(arithmeticDecoderStats.getContextCodingTableValue((int)l), 1, 1);
        int n3 = arithmeticDecoderStats.getContextCodingTableValue((int)l) & 1;
        int n4 = this.qeTable[n2];
        this.a -= (long)n4;
        if (this.c < this.a) {
            if ((this.a & Integer.MIN_VALUE) != 0L) {
                n = n3;
            } else {
                if (this.a < (long)n4) {
                    n = 1 - n3;
                    if (this.switchTable[n2] != 0) {
                        arithmeticDecoderStats.setContextCodingTableValue((int)l, this.nlpsTable[n2] << 1 | 1 - n3);
                    } else {
                        arithmeticDecoderStats.setContextCodingTableValue((int)l, this.nlpsTable[n2] << 1 | n3);
                    }
                } else {
                    n = n3;
                    arithmeticDecoderStats.setContextCodingTableValue((int)l, this.nmpsTable[n2] << 1 | n3);
                }
                do {
                    if (this.counter == 0) {
                        this.readByte();
                    }
                    this.a = BinaryOperation.bit32Shift(this.a, 1, 0);
                    this.c = BinaryOperation.bit32Shift(this.c, 1, 0);
                    --this.counter;
                } while ((this.a & Integer.MIN_VALUE) == 0L);
            }
        } else {
            this.c -= this.a;
            if (this.a < (long)n4) {
                n = n3;
                arithmeticDecoderStats.setContextCodingTableValue((int)l, this.nmpsTable[n2] << 1 | n3);
            } else {
                n = 1 - n3;
                if (this.switchTable[n2] != 0) {
                    arithmeticDecoderStats.setContextCodingTableValue((int)l, this.nlpsTable[n2] << 1 | 1 - n3);
                } else {
                    arithmeticDecoderStats.setContextCodingTableValue((int)l, this.nlpsTable[n2] << 1 | n3);
                }
            }
            this.a = n4;
            do {
                if (this.counter == 0) {
                    this.readByte();
                }
                this.a = BinaryOperation.bit32Shift(this.a, 1, 0);
                this.c = BinaryOperation.bit32Shift(this.c, 1, 0);
                --this.counter;
            } while ((this.a & Integer.MIN_VALUE) == 0L);
        }
        return n;
    }

    private void readByte() throws IOException {
        if (this.buffer0 == 255L) {
            if (this.buffer1 > 143L) {
                this.counter = 8;
            } else {
                this.buffer0 = this.buffer1;
                this.buffer1 = this.reader.readByte();
                this.c = this.c + 65024L - BinaryOperation.bit32Shift(this.buffer0, 9, 0);
                this.counter = 7;
            }
        } else {
            this.buffer0 = this.buffer1;
            this.buffer1 = this.reader.readByte();
            this.c = this.c + 65280L - BinaryOperation.bit32Shift(this.buffer0, 8, 0);
            this.counter = 8;
        }
    }

    private int decodeIntBit(ArithmeticDecoderStats arithmeticDecoderStats) throws IOException {
        int n = this.decodeBit(this.previous, arithmeticDecoderStats);
        this.previous = this.previous < 256L ? BinaryOperation.bit32Shift(this.previous, 1, 0) | (long)n : (BinaryOperation.bit32Shift(this.previous, 1, 0) | (long)n) & 0x1FFL | 0x100L;
        return n;
    }
}

