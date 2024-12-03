/*
 * Decompiled with CFR 0.152.
 */
package org.jpedal.jbig2.segment.region.halftone;

import java.io.IOException;
import org.jpedal.jbig2.JBIG2Exception;
import org.jpedal.jbig2.decoders.JBIG2StreamDecoder;
import org.jpedal.jbig2.image.JBIG2Bitmap;
import org.jpedal.jbig2.segment.Segment;
import org.jpedal.jbig2.segment.pageinformation.PageInformationSegment;
import org.jpedal.jbig2.segment.pattern.PatternDictionarySegment;
import org.jpedal.jbig2.segment.region.RegionFlags;
import org.jpedal.jbig2.segment.region.RegionSegment;
import org.jpedal.jbig2.segment.region.halftone.HalftoneRegionFlags;
import org.jpedal.jbig2.util.BinaryOperation;

public class HalftoneRegionSegment
extends RegionSegment {
    private HalftoneRegionFlags halftoneRegionFlags = new HalftoneRegionFlags();
    private boolean inlineImage;

    public HalftoneRegionSegment(JBIG2StreamDecoder jBIG2StreamDecoder, boolean bl) {
        super(jBIG2StreamDecoder);
        this.inlineImage = bl;
    }

    public void readSegment() throws IOException, JBIG2Exception {
        int n;
        int n2;
        int n3;
        int n4;
        int n5;
        Segment segment;
        int[] nArray;
        super.readSegment();
        this.readHalftoneRegionFlags();
        short[] sArray = new short[4];
        this.decoder.readByte(sArray);
        int n6 = BinaryOperation.getInt32(sArray);
        sArray = new short[4];
        this.decoder.readByte(sArray);
        int n7 = BinaryOperation.getInt32(sArray);
        sArray = new short[4];
        this.decoder.readByte(sArray);
        int n8 = BinaryOperation.getInt32(sArray);
        sArray = new short[4];
        this.decoder.readByte(sArray);
        int n9 = BinaryOperation.getInt32(sArray);
        if (JBIG2StreamDecoder.debug) {
            System.out.println("grid pos and size = " + n8 + "," + n9 + " " + n6 + "," + n7);
        }
        sArray = new short[2];
        this.decoder.readByte(sArray);
        int n10 = BinaryOperation.getInt16(sArray);
        sArray = new short[2];
        this.decoder.readByte(sArray);
        int n11 = BinaryOperation.getInt16(sArray);
        if (JBIG2StreamDecoder.debug) {
            System.out.println("step size = " + n10 + "," + n11);
        }
        if ((nArray = this.segmentHeader.getReferredToSegments()).length != 1) {
            System.out.println("Error in halftone Segment. refSegs should == 1");
        }
        if ((segment = this.decoder.findSegment(nArray[0])).getSegmentHeader().getSegmentType() != 16 && JBIG2StreamDecoder.debug) {
            System.out.println("Error in halftone Segment. bad symbol dictionary reference");
        }
        PatternDictionarySegment patternDictionarySegment = (PatternDictionarySegment)segment;
        int n12 = 0;
        for (n5 = 1; n5 < patternDictionarySegment.getSize(); n5 <<= 1) {
            ++n12;
        }
        JBIG2Bitmap jBIG2Bitmap = patternDictionarySegment.getBitmaps()[0];
        int n13 = jBIG2Bitmap.getWidth();
        int n14 = jBIG2Bitmap.getHeight();
        if (JBIG2StreamDecoder.debug) {
            System.out.println("pattern size = " + n13 + "," + n14);
        }
        boolean bl = this.halftoneRegionFlags.getFlagValue(HalftoneRegionFlags.H_MMR) != 0;
        int n15 = this.halftoneRegionFlags.getFlagValue(HalftoneRegionFlags.H_TEMPLATE);
        if (!bl) {
            this.arithmeticDecoder.resetGenericStats(n15, null);
            this.arithmeticDecoder.start();
        }
        int n16 = this.halftoneRegionFlags.getFlagValue(HalftoneRegionFlags.H_DEF_PIXEL);
        jBIG2Bitmap = new JBIG2Bitmap(this.regionBitmapWidth, this.regionBitmapHeight);
        jBIG2Bitmap.clear(n16);
        boolean bl2 = this.halftoneRegionFlags.getFlagValue(HalftoneRegionFlags.H_ENABLE_SKIP) != 0;
        JBIG2Bitmap jBIG2Bitmap2 = null;
        if (bl2) {
            jBIG2Bitmap2 = new JBIG2Bitmap(n6, n7);
            jBIG2Bitmap2.clear(0);
            for (int i = 0; i < n7; ++i) {
                for (int j = 0; j < n6; ++j) {
                    int n17 = n8 + i * n11 + j * n10;
                    n4 = n9 + i * n10 - j * n11;
                    if (n17 + n13 >> 8 > 0 && n17 >> 8 < this.regionBitmapWidth && n4 + n14 >> 8 > 0 && n4 >> 8 < this.regionBitmapHeight) continue;
                    jBIG2Bitmap2.setPixel(i, j, 1);
                }
            }
        }
        int[] nArray2 = new int[n6 * n7];
        short[] sArray2 = new short[4];
        short[] sArray3 = new short[4];
        sArray2[0] = (short)(n15 <= 1 ? 3 : 2);
        sArray3[0] = -1;
        sArray2[1] = -3;
        sArray3[1] = -1;
        sArray2[2] = 2;
        sArray3[2] = -2;
        sArray2[3] = -2;
        sArray3[3] = -2;
        for (n4 = n12 - 1; n4 >= 0; --n4) {
            JBIG2Bitmap jBIG2Bitmap3 = new JBIG2Bitmap(n6, n7);
            jBIG2Bitmap3.readBitmap(bl, n15, false, bl2, jBIG2Bitmap2, sArray2, sArray3, -1);
            n5 = 0;
            for (n3 = 0; n3 < n7; ++n3) {
                for (n2 = 0; n2 < n6; ++n2) {
                    n = jBIG2Bitmap3.getPixel(n2, n3) ^ nArray2[n5] & 1;
                    nArray2[n5] = nArray2[n5] << 1 | n;
                    ++n5;
                }
            }
        }
        n4 = this.halftoneRegionFlags.getFlagValue(HalftoneRegionFlags.H_COMB_OP);
        n5 = 0;
        for (int i = 0; i < n7; ++i) {
            n3 = n8 + i * n11;
            n2 = n9 + i * n10;
            for (n = 0; n < n6; ++n) {
                if (!bl2 || jBIG2Bitmap2.getPixel(i, n) != 1) {
                    JBIG2Bitmap jBIG2Bitmap4 = patternDictionarySegment.getBitmaps()[nArray2[n5]];
                    jBIG2Bitmap.combine(jBIG2Bitmap4, n3 >> 8, n2 >> 8, n4);
                }
                n3 += n10;
                n2 -= n11;
                ++n5;
            }
        }
        if (this.inlineImage) {
            PageInformationSegment pageInformationSegment = this.decoder.findPageSegement(this.segmentHeader.getPageAssociation());
            JBIG2Bitmap jBIG2Bitmap5 = pageInformationSegment.getPageBitmap();
            n2 = this.regionFlags.getFlagValue(RegionFlags.EXTERNAL_COMBINATION_OPERATOR);
            jBIG2Bitmap5.combine(jBIG2Bitmap, this.regionBitmapXLocation, this.regionBitmapYLocation, n2);
        } else {
            jBIG2Bitmap.setBitmapNumber(this.getSegmentHeader().getSegmentNumber());
            this.decoder.appendBitmap(jBIG2Bitmap);
        }
    }

    private void readHalftoneRegionFlags() throws IOException {
        short s = this.decoder.readByte();
        this.halftoneRegionFlags.setFlags(s);
        if (JBIG2StreamDecoder.debug) {
            System.out.println("generic region Segment flags = " + s);
        }
    }

    public HalftoneRegionFlags getHalftoneRegionFlags() {
        return this.halftoneRegionFlags;
    }
}

