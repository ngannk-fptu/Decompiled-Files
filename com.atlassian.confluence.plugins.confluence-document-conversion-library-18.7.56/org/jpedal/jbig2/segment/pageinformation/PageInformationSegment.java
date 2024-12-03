/*
 * Decompiled with CFR 0.152.
 */
package org.jpedal.jbig2.segment.pageinformation;

import java.io.IOException;
import org.jpedal.jbig2.JBIG2Exception;
import org.jpedal.jbig2.decoders.JBIG2StreamDecoder;
import org.jpedal.jbig2.image.JBIG2Bitmap;
import org.jpedal.jbig2.segment.Segment;
import org.jpedal.jbig2.segment.pageinformation.PageInformationFlags;
import org.jpedal.jbig2.util.BinaryOperation;

public class PageInformationSegment
extends Segment {
    private int pageBitmapHeight;
    private int pageBitmapWidth;
    private int yResolution;
    private int xResolution;
    PageInformationFlags pageInformationFlags = new PageInformationFlags();
    private int pageStriping;
    private JBIG2Bitmap pageBitmap;

    public PageInformationSegment(JBIG2StreamDecoder jBIG2StreamDecoder) {
        super(jBIG2StreamDecoder);
    }

    public PageInformationFlags getPageInformationFlags() {
        return this.pageInformationFlags;
    }

    public JBIG2Bitmap getPageBitmap() {
        return this.pageBitmap;
    }

    public void readSegment() throws IOException, JBIG2Exception {
        if (JBIG2StreamDecoder.debug) {
            System.out.println("==== Reading Page Information Dictionary ====");
        }
        short[] sArray = new short[4];
        this.decoder.readByte(sArray);
        this.pageBitmapWidth = BinaryOperation.getInt32(sArray);
        sArray = new short[4];
        this.decoder.readByte(sArray);
        this.pageBitmapHeight = BinaryOperation.getInt32(sArray);
        if (JBIG2StreamDecoder.debug) {
            System.out.println("Bitmap size = " + this.pageBitmapWidth + "x" + this.pageBitmapHeight);
        }
        sArray = new short[4];
        this.decoder.readByte(sArray);
        this.xResolution = BinaryOperation.getInt32(sArray);
        sArray = new short[4];
        this.decoder.readByte(sArray);
        this.yResolution = BinaryOperation.getInt32(sArray);
        if (JBIG2StreamDecoder.debug) {
            System.out.println("Resolution = " + this.xResolution + "x" + this.yResolution);
        }
        short s = this.decoder.readByte();
        this.pageInformationFlags.setFlags(s);
        if (JBIG2StreamDecoder.debug) {
            System.out.println("symbolDictionaryFlags = " + s);
        }
        sArray = new short[2];
        this.decoder.readByte(sArray);
        this.pageStriping = BinaryOperation.getInt16(sArray);
        if (JBIG2StreamDecoder.debug) {
            System.out.println("Page Striping = " + this.pageStriping);
        }
        int n = this.pageInformationFlags.getFlagValue(PageInformationFlags.DEFAULT_PIXEL_VALUE);
        int n2 = this.pageBitmapHeight == -1 ? this.pageStriping & Short.MAX_VALUE : this.pageBitmapHeight;
        this.pageBitmap = new JBIG2Bitmap(this.pageBitmapWidth, n2);
        this.pageBitmap.clear(n);
    }

    public int getPageBitmapHeight() {
        return this.pageBitmapHeight;
    }
}

