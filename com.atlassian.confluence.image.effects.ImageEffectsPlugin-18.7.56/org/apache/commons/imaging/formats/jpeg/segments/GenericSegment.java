/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.jpeg.segments;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import org.apache.commons.imaging.common.BinaryFunctions;
import org.apache.commons.imaging.formats.jpeg.segments.Segment;

public abstract class GenericSegment
extends Segment {
    private final byte[] segmentData;

    public GenericSegment(int marker, int markerLength, InputStream is) throws IOException {
        super(marker, markerLength);
        this.segmentData = BinaryFunctions.readBytes("Segment Data", is, markerLength, "Invalid Segment: insufficient data");
    }

    public GenericSegment(int marker, byte[] bytes) {
        super(marker, bytes.length);
        this.segmentData = (byte[])bytes.clone();
    }

    @Override
    public void dump(PrintWriter pw) {
        this.dump(pw, 0);
    }

    public void dump(PrintWriter pw, int start) {
        for (int i = 0; i < 50 && i + start < this.segmentData.length; ++i) {
            this.debugNumber(pw, "\t" + (i + start), this.segmentData[i + start], 1);
        }
    }

    public byte[] getSegmentData() {
        return (byte[])this.segmentData.clone();
    }

    protected byte getSegmentData(int offset) {
        return this.segmentData[offset];
    }

    public String getSegmentDataAsString(String encoding) throws UnsupportedEncodingException {
        return new String(this.segmentData, encoding);
    }
}

