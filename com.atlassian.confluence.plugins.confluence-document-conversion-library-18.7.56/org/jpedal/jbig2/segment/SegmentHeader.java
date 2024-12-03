/*
 * Decompiled with CFR 0.152.
 */
package org.jpedal.jbig2.segment;

import org.jpedal.jbig2.decoders.JBIG2StreamDecoder;

public class SegmentHeader {
    private int segmentNumber;
    private int segmentType;
    private boolean pageAssociationSizeSet;
    private boolean deferredNonRetainSet;
    private int referredToSegmentCount;
    private short[] rententionFlags;
    private int[] referredToSegments;
    private int pageAssociation;
    private int dataLength;

    public void setSegmentNumber(int n) {
        this.segmentNumber = n;
    }

    public void setSegmentHeaderFlags(short s) {
        this.segmentType = s & 0x3F;
        this.pageAssociationSizeSet = (s & 0x40) == 64;
        boolean bl = this.deferredNonRetainSet = (s & 0x50) == 80;
        if (JBIG2StreamDecoder.debug) {
            System.out.println("SegmentType = " + this.segmentType);
            System.out.println("pageAssociationSizeSet = " + this.pageAssociationSizeSet);
            System.out.println("deferredNonRetainSet = " + this.deferredNonRetainSet);
        }
    }

    public void setReferredToSegmentCount(int n) {
        this.referredToSegmentCount = n;
    }

    public void setRententionFlags(short[] sArray) {
        this.rententionFlags = sArray;
    }

    public void setReferredToSegments(int[] nArray) {
        this.referredToSegments = nArray;
    }

    public int[] getReferredToSegments() {
        return this.referredToSegments;
    }

    public int getSegmentType() {
        return this.segmentType;
    }

    public int getSegmentNumber() {
        return this.segmentNumber;
    }

    public boolean isPageAssociationSizeSet() {
        return this.pageAssociationSizeSet;
    }

    public boolean isDeferredNonRetainSet() {
        return this.deferredNonRetainSet;
    }

    public int getReferredToSegmentCount() {
        return this.referredToSegmentCount;
    }

    public short[] getRententionFlags() {
        return this.rententionFlags;
    }

    public int getPageAssociation() {
        return this.pageAssociation;
    }

    public void setPageAssociation(int n) {
        this.pageAssociation = n;
    }

    public void setDataLength(int n) {
        this.dataLength = n;
    }

    public void setSegmentType(int n) {
        this.segmentType = n;
    }

    public int getSegmentDataLength() {
        return this.dataLength;
    }
}

