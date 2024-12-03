/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codec;

import com.sun.media.jai.codec.StreamSegment;
import com.sun.media.jai.codec.StreamSegmentMapper;

class SectorStreamSegmentMapper
implements StreamSegmentMapper {
    long[] segmentPositions;
    int segmentLength;
    int totalLength;
    int lastSegmentLength;

    public SectorStreamSegmentMapper(long[] segmentPositions, int segmentLength, int totalLength) {
        this.segmentPositions = (long[])segmentPositions.clone();
        this.segmentLength = segmentLength;
        this.totalLength = totalLength;
        this.lastSegmentLength = totalLength - (segmentPositions.length - 1) * segmentLength;
    }

    public StreamSegment getStreamSegment(long position, int length) {
        int index = (int)(position / (long)this.segmentLength);
        int len = index == this.segmentPositions.length - 1 ? this.lastSegmentLength : this.segmentLength;
        if ((len = (int)((long)len - (position -= (long)(index * this.segmentLength)))) > length) {
            len = length;
        }
        return new StreamSegment(this.segmentPositions[index] + position, len);
    }

    public void getStreamSegment(long position, int length, StreamSegment seg) {
        int index = (int)(position / (long)this.segmentLength);
        int len = index == this.segmentPositions.length - 1 ? this.lastSegmentLength : this.segmentLength;
        if ((len = (int)((long)len - (position -= (long)(index * this.segmentLength)))) > length) {
            len = length;
        }
        seg.setStartPos(this.segmentPositions[index] + position);
        seg.setSegmentLength(len);
    }
}

