/*
 * Decompiled with CFR 0.152.
 */
package org.jpedal.jbig2.segment.region;

import java.io.IOException;
import org.jpedal.jbig2.JBIG2Exception;
import org.jpedal.jbig2.decoders.JBIG2StreamDecoder;
import org.jpedal.jbig2.segment.Segment;
import org.jpedal.jbig2.segment.region.RegionFlags;
import org.jpedal.jbig2.util.BinaryOperation;

public abstract class RegionSegment
extends Segment {
    protected int regionBitmapWidth;
    protected int regionBitmapHeight;
    protected int regionBitmapXLocation;
    protected int regionBitmapYLocation;
    protected RegionFlags regionFlags = new RegionFlags();

    public RegionSegment(JBIG2StreamDecoder jBIG2StreamDecoder) {
        super(jBIG2StreamDecoder);
    }

    public void readSegment() throws IOException, JBIG2Exception {
        short[] sArray = new short[4];
        this.decoder.readByte(sArray);
        this.regionBitmapWidth = BinaryOperation.getInt32(sArray);
        sArray = new short[4];
        this.decoder.readByte(sArray);
        this.regionBitmapHeight = BinaryOperation.getInt32(sArray);
        if (JBIG2StreamDecoder.debug) {
            System.out.println("Bitmap size = " + this.regionBitmapWidth + "x" + this.regionBitmapHeight);
        }
        sArray = new short[4];
        this.decoder.readByte(sArray);
        this.regionBitmapXLocation = BinaryOperation.getInt32(sArray);
        sArray = new short[4];
        this.decoder.readByte(sArray);
        this.regionBitmapYLocation = BinaryOperation.getInt32(sArray);
        if (JBIG2StreamDecoder.debug) {
            System.out.println("Bitmap location = " + this.regionBitmapXLocation + "," + this.regionBitmapYLocation);
        }
        short s = this.decoder.readByte();
        this.regionFlags.setFlags(s);
        if (JBIG2StreamDecoder.debug) {
            System.out.println("region Segment flags = " + s);
        }
    }
}

