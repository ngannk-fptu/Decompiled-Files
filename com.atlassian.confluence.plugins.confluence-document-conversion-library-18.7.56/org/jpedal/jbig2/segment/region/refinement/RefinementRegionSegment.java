/*
 * Decompiled with CFR 0.152.
 */
package org.jpedal.jbig2.segment.region.refinement;

import java.io.IOException;
import org.jpedal.jbig2.JBIG2Exception;
import org.jpedal.jbig2.decoders.JBIG2StreamDecoder;
import org.jpedal.jbig2.image.JBIG2Bitmap;
import org.jpedal.jbig2.segment.pageinformation.PageInformationFlags;
import org.jpedal.jbig2.segment.pageinformation.PageInformationSegment;
import org.jpedal.jbig2.segment.region.RegionFlags;
import org.jpedal.jbig2.segment.region.RegionSegment;
import org.jpedal.jbig2.segment.region.refinement.RefinementRegionFlags;

public class RefinementRegionSegment
extends RegionSegment {
    private RefinementRegionFlags refinementRegionFlags = new RefinementRegionFlags();
    private boolean inlineImage;
    private int noOfReferedToSegments;
    int[] referedToSegments;

    public RefinementRegionSegment(JBIG2StreamDecoder jBIG2StreamDecoder, boolean bl, int[] nArray, int n) {
        super(jBIG2StreamDecoder);
        this.inlineImage = bl;
        this.referedToSegments = nArray;
        this.noOfReferedToSegments = n;
    }

    public void readSegment() throws IOException, JBIG2Exception {
        JBIG2Bitmap jBIG2Bitmap;
        Object object;
        Object object2;
        if (JBIG2StreamDecoder.debug) {
            System.out.println("==== Reading Generic Refinement Region ====");
        }
        super.readSegment();
        this.readGenericRegionFlags();
        short[] sArray = new short[2];
        short[] sArray2 = new short[2];
        int n = this.refinementRegionFlags.getFlagValue(RefinementRegionFlags.GR_TEMPLATE);
        if (n == 0) {
            sArray[0] = this.readATValue();
            sArray2[0] = this.readATValue();
            sArray[1] = this.readATValue();
            sArray2[1] = this.readATValue();
        }
        if (this.noOfReferedToSegments == 0 || this.inlineImage) {
            object2 = this.decoder.findPageSegement(this.segmentHeader.getPageAssociation());
            object = ((PageInformationSegment)object2).getPageBitmap();
            if (((PageInformationSegment)object2).getPageBitmapHeight() == -1 && this.regionBitmapYLocation + this.regionBitmapHeight > ((JBIG2Bitmap)object).getHeight()) {
                ((JBIG2Bitmap)object).expand(this.regionBitmapYLocation + this.regionBitmapHeight, ((PageInformationSegment)object2).getPageInformationFlags().getFlagValue(PageInformationFlags.DEFAULT_PIXEL_VALUE));
            }
        }
        if (this.noOfReferedToSegments > 1) {
            if (JBIG2StreamDecoder.debug) {
                System.out.println("Bad reference in JBIG2 generic refinement Segment");
            }
            return;
        }
        if (this.noOfReferedToSegments == 1) {
            object2 = this.decoder.findBitmap(this.referedToSegments[0]);
        } else {
            object = this.decoder.findPageSegement(this.segmentHeader.getPageAssociation());
            jBIG2Bitmap = ((PageInformationSegment)object).getPageBitmap();
            object2 = jBIG2Bitmap.getSlice(this.regionBitmapXLocation, this.regionBitmapYLocation, this.regionBitmapWidth, this.regionBitmapHeight);
        }
        this.arithmeticDecoder.resetRefinementStats(n, null);
        this.arithmeticDecoder.start();
        boolean bl = this.refinementRegionFlags.getFlagValue(RefinementRegionFlags.TPGDON) != 0;
        jBIG2Bitmap = new JBIG2Bitmap(this.regionBitmapWidth, this.regionBitmapHeight);
        jBIG2Bitmap.readGenericRefinementRegion(n, bl, (JBIG2Bitmap)object2, 0, 0, sArray, sArray2);
        if (this.inlineImage) {
            PageInformationSegment pageInformationSegment = this.decoder.findPageSegement(this.segmentHeader.getPageAssociation());
            JBIG2Bitmap jBIG2Bitmap2 = pageInformationSegment.getPageBitmap();
            int n2 = this.regionFlags.getFlagValue(RegionFlags.EXTERNAL_COMBINATION_OPERATOR);
            jBIG2Bitmap2.combine(jBIG2Bitmap, this.regionBitmapXLocation, this.regionBitmapYLocation, n2);
        } else {
            jBIG2Bitmap.setBitmapNumber(this.getSegmentHeader().getSegmentNumber());
            this.decoder.appendBitmap(jBIG2Bitmap);
        }
    }

    private void readGenericRegionFlags() throws IOException {
        short s = this.decoder.readByte();
        this.refinementRegionFlags.setFlags(s);
        if (JBIG2StreamDecoder.debug) {
            System.out.println("generic region Segment flags = " + s);
        }
    }

    public RefinementRegionFlags getGenericRegionFlags() {
        return this.refinementRegionFlags;
    }
}

