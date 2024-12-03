/*
 * Decompiled with CFR 0.152.
 */
package org.jpedal.jbig2.segment.region.text;

import java.io.IOException;
import java.util.ArrayList;
import org.jpedal.jbig2.JBIG2Exception;
import org.jpedal.jbig2.decoders.HuffmanDecoder;
import org.jpedal.jbig2.decoders.JBIG2StreamDecoder;
import org.jpedal.jbig2.image.JBIG2Bitmap;
import org.jpedal.jbig2.segment.Segment;
import org.jpedal.jbig2.segment.pageinformation.PageInformationSegment;
import org.jpedal.jbig2.segment.region.RegionFlags;
import org.jpedal.jbig2.segment.region.RegionSegment;
import org.jpedal.jbig2.segment.region.text.TextRegionFlags;
import org.jpedal.jbig2.segment.region.text.TextRegionHuffmanFlags;
import org.jpedal.jbig2.segment.symboldictionary.SymbolDictionarySegment;
import org.jpedal.jbig2.util.BinaryOperation;

public class TextRegionSegment
extends RegionSegment {
    private TextRegionFlags textRegionFlags = new TextRegionFlags();
    private TextRegionHuffmanFlags textRegionHuffmanFlags = new TextRegionHuffmanFlags();
    private int noOfSymbolInstances;
    private boolean inlineImage;
    private short[] symbolRegionAdaptiveTemplateX = new short[2];
    private short[] symbolRegionAdaptiveTemplateY = new short[2];

    public TextRegionSegment(JBIG2StreamDecoder jBIG2StreamDecoder, boolean bl) {
        super(jBIG2StreamDecoder);
        this.inlineImage = bl;
    }

    public void readSegment() throws IOException, JBIG2Exception {
        int n;
        int n2;
        int n3;
        int n4;
        int n5;
        Object object;
        Object object2;
        int n6;
        int n7;
        if (JBIG2StreamDecoder.debug) {
            System.out.println("==== Reading Text Region ====");
        }
        super.readSegment();
        this.readTextRegionFlags();
        short[] sArray = new short[4];
        this.decoder.readByte(sArray);
        this.noOfSymbolInstances = BinaryOperation.getInt32(sArray);
        if (JBIG2StreamDecoder.debug) {
            System.out.println("noOfSymbolInstances = " + this.noOfSymbolInstances);
        }
        int n8 = this.segmentHeader.getReferredToSegmentCount();
        int[] nArray = this.segmentHeader.getReferredToSegments();
        ArrayList<Segment> arrayList = new ArrayList<Segment>();
        ArrayList<Segment> arrayList2 = new ArrayList<Segment>();
        int n9 = 0;
        if (JBIG2StreamDecoder.debug) {
            System.out.println("noOfReferredToSegments = " + n8);
        }
        for (n7 = 0; n7 < n8; ++n7) {
            Segment segment = this.decoder.findSegment(nArray[n7]);
            n6 = segment.getSegmentHeader().getSegmentType();
            if (n6 == 0) {
                arrayList2.add(segment);
                n9 += ((SymbolDictionarySegment)segment).getNoOfExportedSymbols();
                continue;
            }
            if (n6 != 53) continue;
            arrayList.add(segment);
        }
        n7 = 0;
        for (int i = 1; i < n9; i <<= 1) {
            ++n7;
        }
        n6 = 0;
        JBIG2Bitmap[] jBIG2BitmapArray = new JBIG2Bitmap[n9];
        Object object3 = arrayList2.iterator();
        while (object3.hasNext()) {
            object2 = (Segment)object3.next();
            if (((Segment)object2).getSegmentHeader().getSegmentType() != 0) continue;
            object = ((SymbolDictionarySegment)object2).getBitmaps();
            for (int i = 0; i < ((int[][])object).length; ++i) {
                jBIG2BitmapArray[n6] = object[i];
                ++n6;
            }
        }
        object3 = null;
        object2 = null;
        object = null;
        int[][] nArray2 = null;
        int[][] nArray3 = null;
        int[][] nArray4 = null;
        int[][] nArray5 = null;
        int[][] nArray6 = null;
        boolean bl2 = this.textRegionFlags.getFlagValue(TextRegionFlags.SB_HUFF) != 0;
        int n10 = 0;
        if (bl2) {
            int n11 = this.textRegionHuffmanFlags.getFlagValue(TextRegionHuffmanFlags.SB_HUFF_FS);
            if (n11 == 0) {
                object3 = HuffmanDecoder.huffmanTableF;
            } else if (n11 == 1) {
                object3 = HuffmanDecoder.huffmanTableG;
            }
            int n12 = this.textRegionHuffmanFlags.getFlagValue(TextRegionHuffmanFlags.SB_HUFF_DS);
            if (n12 == 0) {
                object2 = HuffmanDecoder.huffmanTableH;
            } else if (n12 == 1) {
                object2 = HuffmanDecoder.huffmanTableI;
            } else if (n12 == 2) {
                object2 = HuffmanDecoder.huffmanTableJ;
            }
            n5 = this.textRegionHuffmanFlags.getFlagValue(TextRegionHuffmanFlags.SB_HUFF_DT);
            if (n5 == 0) {
                object = HuffmanDecoder.huffmanTableK;
            } else if (n5 == 1) {
                object = HuffmanDecoder.huffmanTableL;
            } else if (n5 == 2) {
                object = HuffmanDecoder.huffmanTableM;
            }
            n4 = this.textRegionHuffmanFlags.getFlagValue(TextRegionHuffmanFlags.SB_HUFF_RDW);
            if (n4 == 0) {
                nArray2 = HuffmanDecoder.huffmanTableN;
            } else if (n4 == 1) {
                nArray2 = HuffmanDecoder.huffmanTableO;
            }
            n3 = this.textRegionHuffmanFlags.getFlagValue(TextRegionHuffmanFlags.SB_HUFF_RDH);
            if (n3 == 0) {
                nArray3 = HuffmanDecoder.huffmanTableN;
            } else if (n3 == 1) {
                nArray3 = HuffmanDecoder.huffmanTableO;
            }
            n2 = this.textRegionHuffmanFlags.getFlagValue(TextRegionHuffmanFlags.SB_HUFF_RDX);
            if (n2 == 0) {
                nArray4 = HuffmanDecoder.huffmanTableN;
            } else if (n2 == 1) {
                nArray4 = HuffmanDecoder.huffmanTableO;
            }
            int bl = this.textRegionHuffmanFlags.getFlagValue(TextRegionHuffmanFlags.SB_HUFF_RDY);
            if (bl == 0) {
                nArray5 = HuffmanDecoder.huffmanTableN;
            } else if (bl == 1) {
                nArray5 = HuffmanDecoder.huffmanTableO;
            }
            n = this.textRegionHuffmanFlags.getFlagValue(TextRegionHuffmanFlags.SB_HUFF_RSIZE);
            if (n == 0) {
                nArray6 = HuffmanDecoder.huffmanTableA;
            }
        }
        int[][] nArray7 = new int[36][4];
        int[][] nArray8 = new int[n9 + 1][4];
        if (bl2) {
            this.decoder.consumeRemainingBits();
            for (n10 = 0; n10 < 32; ++n10) {
                nArray7[n10] = new int[]{n10, this.decoder.readBits(4), 0, 0};
            }
            nArray7[32] = new int[]{259, this.decoder.readBits(4), 2, 0};
            nArray7[33] = new int[]{515, this.decoder.readBits(4), 3, 0};
            nArray7[34] = new int[]{523, this.decoder.readBits(4), 7, 0};
            nArray7[35] = new int[]{0, 0, HuffmanDecoder.jbig2HuffmanEOT};
            nArray7 = this.huffDecoder.buildTable(nArray7, 35);
            for (n10 = 0; n10 < n9; ++n10) {
                nArray8[n10] = new int[]{n10, 0, 0, 0};
            }
            n10 = 0;
            while (n10 < n9) {
                n5 = this.huffDecoder.decodeInt(nArray7).intResult();
                if (n5 > 512) {
                    n5 -= 512;
                    while (n5 != 0 && n10 < n9) {
                        nArray8[n10++][1] = 0;
                        --n5;
                    }
                    continue;
                }
                if (n5 > 256) {
                    n5 -= 256;
                    while (n5 != 0 && n10 < n9) {
                        nArray8[n10][1] = nArray8[n10 - 1][1];
                        ++n10;
                        --n5;
                    }
                    continue;
                }
                nArray8[n10++][1] = n5;
            }
            nArray8[n9][1] = 0;
            nArray8[n9][2] = HuffmanDecoder.jbig2HuffmanEOT;
            nArray8 = this.huffDecoder.buildTable(nArray8, n9);
            this.decoder.consumeRemainingBits();
        } else {
            nArray8 = null;
            this.arithmeticDecoder.resetIntStats(n7);
            this.arithmeticDecoder.start();
        }
        n5 = this.textRegionFlags.getFlagValue(TextRegionFlags.SB_REFINE) != 0 ? 1 : 0;
        n4 = this.textRegionFlags.getFlagValue(TextRegionFlags.LOG_SB_STRIPES);
        n3 = this.textRegionFlags.getFlagValue(TextRegionFlags.SB_DEF_PIXEL);
        n2 = this.textRegionFlags.getFlagValue(TextRegionFlags.SB_COMB_OP);
        boolean bl = this.textRegionFlags.getFlagValue(TextRegionFlags.TRANSPOSED) != 0;
        n = this.textRegionFlags.getFlagValue(TextRegionFlags.REF_CORNER);
        int n11 = this.textRegionFlags.getFlagValue(TextRegionFlags.SB_DS_OFFSET);
        int n12 = this.textRegionFlags.getFlagValue(TextRegionFlags.SB_R_TEMPLATE);
        if (n5 != 0) {
            this.arithmeticDecoder.resetRefinementStats(n12, null);
        }
        JBIG2Bitmap jBIG2Bitmap = new JBIG2Bitmap(this.regionBitmapWidth, this.regionBitmapHeight);
        jBIG2Bitmap.readTextRegion(bl2, n5 != 0, this.noOfSymbolInstances, n4, n9, nArray8, n7, jBIG2BitmapArray, n3, n2, bl, n, n11, (int[][])object3, (int[][])object2, (int[][])object, nArray2, nArray3, nArray4, nArray5, nArray6, n12, this.symbolRegionAdaptiveTemplateX, this.symbolRegionAdaptiveTemplateY, this.decoder);
        if (this.inlineImage) {
            PageInformationSegment pageInformationSegment = this.decoder.findPageSegement(this.segmentHeader.getPageAssociation());
            JBIG2Bitmap jBIG2Bitmap2 = pageInformationSegment.getPageBitmap();
            if (JBIG2StreamDecoder.debug) {
                System.out.println(jBIG2Bitmap2 + " " + jBIG2Bitmap);
            }
            int n13 = this.regionFlags.getFlagValue(RegionFlags.EXTERNAL_COMBINATION_OPERATOR);
            jBIG2Bitmap2.combine(jBIG2Bitmap, this.regionBitmapXLocation, this.regionBitmapYLocation, n13);
        } else {
            jBIG2Bitmap.setBitmapNumber(this.getSegmentHeader().getSegmentNumber());
            this.decoder.appendBitmap(jBIG2Bitmap);
        }
        this.decoder.consumeRemainingBits();
    }

    private void readTextRegionFlags() throws IOException {
        boolean bl;
        short[] sArray = new short[2];
        this.decoder.readByte(sArray);
        int n = BinaryOperation.getInt16(sArray);
        this.textRegionFlags.setFlags(n);
        if (JBIG2StreamDecoder.debug) {
            System.out.println("text region Segment flags = " + n);
        }
        boolean bl2 = bl = this.textRegionFlags.getFlagValue(TextRegionFlags.SB_HUFF) != 0;
        if (bl) {
            short[] sArray2 = new short[2];
            this.decoder.readByte(sArray2);
            n = BinaryOperation.getInt16(sArray2);
            this.textRegionHuffmanFlags.setFlags(n);
            if (JBIG2StreamDecoder.debug) {
                System.out.println("text region segment Huffman flags = " + n);
            }
        }
        boolean bl3 = this.textRegionFlags.getFlagValue(TextRegionFlags.SB_REFINE) != 0;
        int n2 = this.textRegionFlags.getFlagValue(TextRegionFlags.SB_R_TEMPLATE);
        if (bl3 && n2 == 0) {
            this.symbolRegionAdaptiveTemplateX[0] = this.readATValue();
            this.symbolRegionAdaptiveTemplateY[0] = this.readATValue();
            this.symbolRegionAdaptiveTemplateX[1] = this.readATValue();
            this.symbolRegionAdaptiveTemplateY[1] = this.readATValue();
        }
    }

    public TextRegionFlags getTextRegionFlags() {
        return this.textRegionFlags;
    }

    public TextRegionHuffmanFlags getTextRegionHuffmanFlags() {
        return this.textRegionHuffmanFlags;
    }
}

