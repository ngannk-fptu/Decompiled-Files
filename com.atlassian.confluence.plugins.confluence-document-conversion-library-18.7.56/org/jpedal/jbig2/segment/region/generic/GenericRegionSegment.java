/*
 * Decompiled with CFR 0.152.
 */
package org.jpedal.jbig2.segment.region.generic;

import java.io.IOException;
import org.jpedal.jbig2.JBIG2Exception;
import org.jpedal.jbig2.decoders.JBIG2StreamDecoder;
import org.jpedal.jbig2.image.JBIG2Bitmap;
import org.jpedal.jbig2.segment.pageinformation.PageInformationFlags;
import org.jpedal.jbig2.segment.pageinformation.PageInformationSegment;
import org.jpedal.jbig2.segment.region.RegionFlags;
import org.jpedal.jbig2.segment.region.RegionSegment;
import org.jpedal.jbig2.segment.region.generic.GenericRegionFlags;

public class GenericRegionSegment
extends RegionSegment {
    private GenericRegionFlags genericRegionFlags = new GenericRegionFlags();
    private boolean inlineImage;

    public GenericRegionSegment(JBIG2StreamDecoder jBIG2StreamDecoder, boolean bl) {
        super(jBIG2StreamDecoder);
        this.inlineImage = bl;
    }

    public void readSegment() throws IOException, JBIG2Exception {
        if (JBIG2StreamDecoder.debug) {
            System.out.println("==== Reading Immediate Generic Region ====");
        }
        super.readSegment();
        this.readGenericRegionFlags();
        boolean bl = this.genericRegionFlags.getFlagValue(GenericRegionFlags.MMR) != 0;
        int n = this.genericRegionFlags.getFlagValue(GenericRegionFlags.GB_TEMPLATE);
        short[] sArray = new short[4];
        short[] sArray2 = new short[4];
        if (!bl) {
            if (n == 0) {
                sArray[0] = this.readATValue();
                sArray2[0] = this.readATValue();
                sArray[1] = this.readATValue();
                sArray2[1] = this.readATValue();
                sArray[2] = this.readATValue();
                sArray2[2] = this.readATValue();
                sArray[3] = this.readATValue();
                sArray2[3] = this.readATValue();
            } else {
                sArray[0] = this.readATValue();
                sArray2[0] = this.readATValue();
            }
            this.arithmeticDecoder.resetGenericStats(n, null);
            this.arithmeticDecoder.start();
        }
        boolean bl2 = this.genericRegionFlags.getFlagValue(GenericRegionFlags.TPGDON) != 0;
        int n2 = this.segmentHeader.getSegmentDataLength() - 18;
        JBIG2Bitmap jBIG2Bitmap = new JBIG2Bitmap(this.regionBitmapWidth, this.regionBitmapHeight);
        jBIG2Bitmap.clear(0);
        jBIG2Bitmap.readBitmap(bl, n, bl2, false, null, sArray, sArray2, bl ? 0 : n2 - 18);
        if (this.inlineImage) {
            PageInformationSegment pageInformationSegment = this.decoder.findPageSegement(this.segmentHeader.getPageAssociation());
            JBIG2Bitmap jBIG2Bitmap2 = pageInformationSegment.getPageBitmap();
            int n3 = this.regionFlags.getFlagValue(RegionFlags.EXTERNAL_COMBINATION_OPERATOR);
            if (pageInformationSegment.getPageBitmapHeight() == -1 && this.regionBitmapYLocation + this.regionBitmapHeight > jBIG2Bitmap2.getHeight()) {
                jBIG2Bitmap2.expand(this.regionBitmapYLocation + this.regionBitmapHeight, pageInformationSegment.getPageInformationFlags().getFlagValue(PageInformationFlags.DEFAULT_PIXEL_VALUE));
            }
            jBIG2Bitmap2.combine(jBIG2Bitmap, this.regionBitmapXLocation, this.regionBitmapYLocation, n3);
        } else {
            jBIG2Bitmap.setBitmapNumber(this.getSegmentHeader().getSegmentNumber());
            this.decoder.appendBitmap(jBIG2Bitmap);
        }
    }

    private void readGenericRegionFlags() throws IOException {
        short s = this.decoder.readByte();
        this.genericRegionFlags.setFlags(s);
        if (JBIG2StreamDecoder.debug) {
            System.out.println("generic region Segment flags = " + s);
        }
    }

    public GenericRegionFlags getGenericRegionFlags() {
        return this.genericRegionFlags;
    }
}

