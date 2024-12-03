/*
 * Decompiled with CFR 0.152.
 */
package org.jpedal.jbig2.segment.symboldictionary;

import java.io.IOException;
import java.util.ArrayList;
import org.jpedal.jbig2.JBIG2Exception;
import org.jpedal.jbig2.decoders.ArithmeticDecoderStats;
import org.jpedal.jbig2.decoders.DecodeIntResult;
import org.jpedal.jbig2.decoders.HuffmanDecoder;
import org.jpedal.jbig2.decoders.JBIG2StreamDecoder;
import org.jpedal.jbig2.image.JBIG2Bitmap;
import org.jpedal.jbig2.segment.Segment;
import org.jpedal.jbig2.segment.symboldictionary.SymbolDictionaryFlags;
import org.jpedal.jbig2.segment.tables.JBIG2CodeTable;
import org.jpedal.jbig2.util.BinaryOperation;

public class SymbolDictionarySegment
extends Segment {
    private int noOfExportedSymbols;
    private int noOfNewSymbols;
    short[] symbolDictionaryAdaptiveTemplateX = new short[4];
    short[] symbolDictionaryAdaptiveTemplateY = new short[4];
    short[] symbolDictionaryRAdaptiveTemplateX = new short[2];
    short[] symbolDictionaryRAdaptiveTemplateY = new short[2];
    private JBIG2Bitmap[] bitmaps;
    private SymbolDictionaryFlags symbolDictionaryFlags = new SymbolDictionaryFlags();
    private ArithmeticDecoderStats genericRegionStats;
    private ArithmeticDecoderStats refinementRegionStats;

    public SymbolDictionarySegment(JBIG2StreamDecoder jBIG2StreamDecoder) {
        super(jBIG2StreamDecoder);
    }

    public void readSegment() throws IOException, JBIG2Exception {
        int n;
        int n2;
        int n3;
        int n4;
        Object object;
        int n5;
        int n6;
        if (JBIG2StreamDecoder.debug) {
            System.out.println("==== Read Segment Symbol Dictionary ====");
        }
        this.readSymbolDictionaryFlags();
        ArrayList<Segment> arrayList = new ArrayList<Segment>();
        int n7 = 0;
        int n8 = this.segmentHeader.getReferredToSegmentCount();
        int[] nArray = this.segmentHeader.getReferredToSegments();
        for (n6 = 0; n6 < n8; ++n6) {
            Segment segment = this.decoder.findSegment(nArray[n6]);
            int n9 = segment.getSegmentHeader().getSegmentType();
            if (n9 == 0) {
                n7 += ((SymbolDictionarySegment)segment).getNoOfExportedSymbols();
                continue;
            }
            if (n9 != 53) continue;
            arrayList.add(segment);
        }
        n6 = 0;
        for (n5 = 1; n5 < n7 + this.noOfNewSymbols; n5 <<= 1) {
            ++n6;
        }
        JBIG2Bitmap[] jBIG2BitmapArray = new JBIG2Bitmap[n7 + this.noOfNewSymbols];
        int n10 = 0;
        SymbolDictionarySegment symbolDictionarySegment = null;
        for (n5 = 0; n5 < n8; ++n5) {
            object = this.decoder.findSegment(nArray[n5]);
            if (((Segment)object).getSegmentHeader().getSegmentType() != 0) continue;
            symbolDictionarySegment = (SymbolDictionarySegment)object;
            for (int i = 0; i < symbolDictionarySegment.getNoOfExportedSymbols(); ++i) {
                jBIG2BitmapArray[n10++] = symbolDictionarySegment.getBitmaps()[i];
            }
        }
        object = null;
        int[][] nArray2 = null;
        int[][] nArray3 = null;
        int[][] nArray4 = null;
        boolean bl = this.symbolDictionaryFlags.getFlagValue(SymbolDictionaryFlags.SD_HUFF) != 0;
        int n11 = this.symbolDictionaryFlags.getFlagValue(SymbolDictionaryFlags.SD_HUFF_DH);
        int n12 = this.symbolDictionaryFlags.getFlagValue(SymbolDictionaryFlags.SD_HUFF_DW);
        int n13 = this.symbolDictionaryFlags.getFlagValue(SymbolDictionaryFlags.SD_HUFF_BM_SIZE);
        int n14 = this.symbolDictionaryFlags.getFlagValue(SymbolDictionaryFlags.SD_HUFF_AGG_INST);
        n5 = 0;
        if (bl) {
            object = n11 == 0 ? (Object)HuffmanDecoder.huffmanTableD : (n11 == 1 ? HuffmanDecoder.huffmanTableE : ((JBIG2CodeTable)arrayList.get(n5++)).getHuffTable());
            nArray2 = n12 == 0 ? HuffmanDecoder.huffmanTableB : (n12 == 1 ? HuffmanDecoder.huffmanTableC : ((JBIG2CodeTable)arrayList.get(n5++)).getHuffTable());
            nArray3 = n13 == 0 ? HuffmanDecoder.huffmanTableA : ((JBIG2CodeTable)arrayList.get(n5++)).getHuffTable();
            nArray4 = n14 == 0 ? HuffmanDecoder.huffmanTableA : ((JBIG2CodeTable)arrayList.get(n5++)).getHuffTable();
        }
        int n15 = this.symbolDictionaryFlags.getFlagValue(SymbolDictionaryFlags.BITMAP_CC_USED);
        int n16 = this.symbolDictionaryFlags.getFlagValue(SymbolDictionaryFlags.SD_TEMPLATE);
        if (!bl) {
            if (n15 != 0 && symbolDictionarySegment != null) {
                this.arithmeticDecoder.resetGenericStats(n16, symbolDictionarySegment.getGenericRegionStats());
            } else {
                this.arithmeticDecoder.resetGenericStats(n16, null);
            }
            this.arithmeticDecoder.resetIntStats(n6);
            this.arithmeticDecoder.start();
        }
        int n17 = this.symbolDictionaryFlags.getFlagValue(SymbolDictionaryFlags.SD_REF_AGG);
        int n18 = this.symbolDictionaryFlags.getFlagValue(SymbolDictionaryFlags.SD_R_TEMPLATE);
        if (n17 != 0) {
            if (n15 != 0 && symbolDictionarySegment != null) {
                this.arithmeticDecoder.resetRefinementStats(n18, symbolDictionarySegment.getRefinementRegionStats());
            } else {
                this.arithmeticDecoder.resetRefinementStats(n18, null);
            }
        }
        int[] nArray5 = new int[this.noOfNewSymbols];
        int n19 = 0;
        n5 = 0;
        while (n5 < this.noOfNewSymbols) {
            Object object2;
            int n20;
            n4 = 0;
            n4 = bl ? this.huffDecoder.decodeInt((int[][])object).intResult() : this.arithmeticDecoder.decodeInt(this.arithmeticDecoder.iadhStats).intResult();
            if (n4 < 0 && -n4 >= n19 && JBIG2StreamDecoder.debug) {
                System.out.println("Bad delta-height value in JBIG2 symbol dictionary");
            }
            n19 += n4;
            n3 = 0;
            n2 = 0;
            n = n5;
            while (true) {
                n20 = 0;
                object2 = bl ? this.huffDecoder.decodeInt(nArray2) : this.arithmeticDecoder.decodeInt(this.arithmeticDecoder.iadwStats);
                if (!((DecodeIntResult)object2).booleanResult()) break;
                n20 = ((DecodeIntResult)object2).intResult();
                if (n20 < 0 && -n20 >= n3 && JBIG2StreamDecoder.debug) {
                    System.out.println("Bad delta-width value in JBIG2 symbol dictionary");
                }
                n3 += n20;
                if (bl && n17 == 0) {
                    nArray5[n5] = n3;
                    n2 += n3;
                } else if (n17 == 1) {
                    int n21 = 0;
                    n21 = bl ? this.huffDecoder.decodeInt(nArray4).intResult() : this.arithmeticDecoder.decodeInt(this.arithmeticDecoder.iaaiStats).intResult();
                    if (n21 == 1) {
                        int n22 = 0;
                        int n23 = 0;
                        int n24 = 0;
                        if (bl) {
                            n22 = this.decoder.readBits(n6);
                            n23 = this.huffDecoder.decodeInt(HuffmanDecoder.huffmanTableO).intResult();
                            n24 = this.huffDecoder.decodeInt(HuffmanDecoder.huffmanTableO).intResult();
                            this.decoder.consumeRemainingBits();
                            this.arithmeticDecoder.start();
                        } else {
                            n22 = (int)this.arithmeticDecoder.decodeIAID(n6, this.arithmeticDecoder.iaidStats);
                            n23 = this.arithmeticDecoder.decodeInt(this.arithmeticDecoder.iardxStats).intResult();
                            n24 = this.arithmeticDecoder.decodeInt(this.arithmeticDecoder.iardyStats).intResult();
                        }
                        JBIG2Bitmap jBIG2Bitmap = jBIG2BitmapArray[n22];
                        JBIG2Bitmap jBIG2Bitmap2 = new JBIG2Bitmap(n3, n19);
                        jBIG2Bitmap2.readGenericRefinementRegion(n18, false, jBIG2Bitmap, n23, n24, this.symbolDictionaryRAdaptiveTemplateX, this.symbolDictionaryRAdaptiveTemplateY);
                        jBIG2BitmapArray[n7 + n5] = jBIG2Bitmap2;
                    } else {
                        JBIG2Bitmap jBIG2Bitmap = new JBIG2Bitmap(n3, n19);
                        jBIG2Bitmap.readTextRegion(bl, true, n21, 0, n7 + n5, null, n6, jBIG2BitmapArray, 0, 0, false, 1, 0, HuffmanDecoder.huffmanTableF, HuffmanDecoder.huffmanTableH, HuffmanDecoder.huffmanTableK, HuffmanDecoder.huffmanTableO, HuffmanDecoder.huffmanTableO, HuffmanDecoder.huffmanTableO, HuffmanDecoder.huffmanTableO, HuffmanDecoder.huffmanTableA, n18, this.symbolDictionaryRAdaptiveTemplateX, this.symbolDictionaryRAdaptiveTemplateY, this.decoder);
                        jBIG2BitmapArray[n7 + n5] = jBIG2Bitmap;
                    }
                } else {
                    JBIG2Bitmap jBIG2Bitmap = new JBIG2Bitmap(n3, n19);
                    jBIG2Bitmap.readBitmap(false, n16, false, false, null, this.symbolDictionaryAdaptiveTemplateX, this.symbolDictionaryAdaptiveTemplateY, 0);
                    jBIG2BitmapArray[n7 + n5] = jBIG2Bitmap;
                }
                ++n5;
            }
            if (!bl || n17 != 0) continue;
            n20 = this.huffDecoder.decodeInt(nArray3).intResult();
            this.decoder.consumeRemainingBits();
            object2 = new JBIG2Bitmap(n2, n19);
            if (n20 == 0) {
                int n25;
                int n26;
                int n27 = n2 % 8;
                int n28 = (int)Math.ceil((double)n2 / 8.0);
                short[] sArray = new short[n2];
                this.decoder.readByte(sArray);
                short[][] sArray2 = new short[n19][n28];
                int n29 = 0;
                for (n26 = 0; n26 < n19; ++n26) {
                    for (n25 = 0; n25 < n28; ++n25) {
                        sArray2[n26][n25] = sArray[n29];
                        ++n29;
                    }
                }
                n26 = 0;
                n25 = 0;
                for (int i = 0; i < n19; ++i) {
                    for (int j = 0; j < n28; ++j) {
                        int n30;
                        short s;
                        int n31;
                        short s2;
                        if (j == n28 - 1) {
                            s2 = sArray2[i][j];
                            for (n31 = 7; n31 >= n27; --n31) {
                                s = (short)(1 << n31);
                                n30 = (s2 & s) >> n31;
                                ((JBIG2Bitmap)object2).setPixel(n25, n26, n30);
                                ++n25;
                            }
                            ++n26;
                            n25 = 0;
                            continue;
                        }
                        s2 = sArray2[i][j];
                        for (n31 = 7; n31 >= 0; --n31) {
                            s = (short)(1 << n31);
                            n30 = (s2 & s) >> n31;
                            ((JBIG2Bitmap)object2).setPixel(n25, n26, n30);
                            ++n25;
                        }
                    }
                }
            } else {
                ((JBIG2Bitmap)object2).readBitmap(true, 0, false, false, null, null, null, n20);
            }
            int n32 = 0;
            while (n < n5) {
                jBIG2BitmapArray[n7 + n] = ((JBIG2Bitmap)object2).getSlice(n32, 0, nArray5[n], n19);
                n32 += nArray5[n];
                ++n;
            }
        }
        this.bitmaps = new JBIG2Bitmap[this.noOfExportedSymbols];
        n5 = 0;
        n4 = 0;
        n3 = 0;
        while (n5 < n7 + this.noOfNewSymbols) {
            n2 = 0;
            n2 = bl ? this.huffDecoder.decodeInt(HuffmanDecoder.huffmanTableA).intResult() : this.arithmeticDecoder.decodeInt(this.arithmeticDecoder.iaexStats).intResult();
            if (n3 != 0) {
                for (n = 0; n < n2; ++n) {
                    this.bitmaps[n4++] = jBIG2BitmapArray[n5++];
                }
            } else {
                n5 += n2;
            }
            n3 = n3 == 0 ? 1 : 0;
        }
        n2 = this.symbolDictionaryFlags.getFlagValue(SymbolDictionaryFlags.BITMAP_CC_RETAINED);
        if (!bl && n2 == 1) {
            this.setGenericRegionStats(this.genericRegionStats.copy());
            if (n17 == 1) {
                this.setRefinementRegionStats(this.refinementRegionStats.copy());
            }
        }
        this.decoder.consumeRemainingBits();
    }

    private void readSymbolDictionaryFlags() throws IOException {
        short[] sArray = new short[2];
        this.decoder.readByte(sArray);
        int n = BinaryOperation.getInt16(sArray);
        this.symbolDictionaryFlags.setFlags(n);
        if (JBIG2StreamDecoder.debug) {
            System.out.println("symbolDictionaryFlags = " + n);
        }
        int n2 = this.symbolDictionaryFlags.getFlagValue(SymbolDictionaryFlags.SD_HUFF);
        int n3 = this.symbolDictionaryFlags.getFlagValue(SymbolDictionaryFlags.SD_TEMPLATE);
        if (n2 == 0) {
            if (n3 == 0) {
                this.symbolDictionaryAdaptiveTemplateX[0] = this.readATValue();
                this.symbolDictionaryAdaptiveTemplateY[0] = this.readATValue();
                this.symbolDictionaryAdaptiveTemplateX[1] = this.readATValue();
                this.symbolDictionaryAdaptiveTemplateY[1] = this.readATValue();
                this.symbolDictionaryAdaptiveTemplateX[2] = this.readATValue();
                this.symbolDictionaryAdaptiveTemplateY[2] = this.readATValue();
                this.symbolDictionaryAdaptiveTemplateX[3] = this.readATValue();
                this.symbolDictionaryAdaptiveTemplateY[3] = this.readATValue();
            } else {
                this.symbolDictionaryAdaptiveTemplateX[0] = this.readATValue();
                this.symbolDictionaryAdaptiveTemplateY[0] = this.readATValue();
            }
        }
        int n4 = this.symbolDictionaryFlags.getFlagValue(SymbolDictionaryFlags.SD_REF_AGG);
        int n5 = this.symbolDictionaryFlags.getFlagValue(SymbolDictionaryFlags.SD_R_TEMPLATE);
        if (n4 != 0 && n5 == 0) {
            this.symbolDictionaryRAdaptiveTemplateX[0] = this.readATValue();
            this.symbolDictionaryRAdaptiveTemplateY[0] = this.readATValue();
            this.symbolDictionaryRAdaptiveTemplateX[1] = this.readATValue();
            this.symbolDictionaryRAdaptiveTemplateY[1] = this.readATValue();
        }
        short[] sArray2 = new short[4];
        this.decoder.readByte(sArray2);
        int n6 = BinaryOperation.getInt32(sArray2);
        this.setNoOfExportedSymbols(n6);
        if (JBIG2StreamDecoder.debug) {
            System.out.println("noOfExportedSymbols = " + n6);
        }
        short[] sArray3 = new short[4];
        this.decoder.readByte(sArray3);
        int n7 = BinaryOperation.getInt32(sArray3);
        this.setNoOfNewSymbols(n7);
        if (JBIG2StreamDecoder.debug) {
            System.out.println("noOfNewSymbols = " + n7);
        }
    }

    public int getNoOfExportedSymbols() {
        return this.noOfExportedSymbols;
    }

    public void setNoOfExportedSymbols(int n) {
        this.noOfExportedSymbols = n;
    }

    public int getNoOfNewSymbols() {
        return this.noOfNewSymbols;
    }

    public void setNoOfNewSymbols(int n) {
        this.noOfNewSymbols = n;
    }

    public JBIG2Bitmap[] getBitmaps() {
        return this.bitmaps;
    }

    public SymbolDictionaryFlags getSymbolDictionaryFlags() {
        return this.symbolDictionaryFlags;
    }

    public void setSymbolDictionaryFlags(SymbolDictionaryFlags symbolDictionaryFlags) {
        this.symbolDictionaryFlags = symbolDictionaryFlags;
    }

    private ArithmeticDecoderStats getGenericRegionStats() {
        return this.genericRegionStats;
    }

    private void setGenericRegionStats(ArithmeticDecoderStats arithmeticDecoderStats) {
        this.genericRegionStats = arithmeticDecoderStats;
    }

    private void setRefinementRegionStats(ArithmeticDecoderStats arithmeticDecoderStats) {
        this.refinementRegionStats = arithmeticDecoderStats;
    }

    private ArithmeticDecoderStats getRefinementRegionStats() {
        return this.refinementRegionStats;
    }
}

