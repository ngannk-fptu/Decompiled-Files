/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.jpeg.segments;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.BinaryFunctions;
import org.apache.commons.imaging.formats.jpeg.JpegConstants;
import org.apache.commons.imaging.formats.jpeg.segments.AppnSegment;

public class App2Segment
extends AppnSegment
implements Comparable<App2Segment> {
    private final byte[] iccBytes;
    public final int curMarker;
    public final int numMarkers;

    public App2Segment(int marker, byte[] segmentData) throws ImageReadException, IOException {
        this(marker, segmentData.length, new ByteArrayInputStream(segmentData));
    }

    public App2Segment(int marker, int markerLength, InputStream is2) throws ImageReadException, IOException {
        super(marker, markerLength, is2);
        if (BinaryFunctions.startsWith(this.getSegmentData(), JpegConstants.ICC_PROFILE_LABEL)) {
            ByteArrayInputStream is = new ByteArrayInputStream(this.getSegmentData());
            BinaryFunctions.readAndVerifyBytes((InputStream)is, JpegConstants.ICC_PROFILE_LABEL, "Not a Valid App2 Segment: missing ICC Profile label");
            this.curMarker = BinaryFunctions.readByte("curMarker", is, "Not a valid App2 Marker");
            this.numMarkers = BinaryFunctions.readByte("numMarkers", is, "Not a valid App2 Marker");
            markerLength -= JpegConstants.ICC_PROFILE_LABEL.size();
            this.iccBytes = BinaryFunctions.readBytes("App2 Data", is, markerLength -= 2, "Invalid App2 Segment: insufficient data");
        } else {
            this.curMarker = -1;
            this.numMarkers = -1;
            this.iccBytes = null;
        }
    }

    public boolean equals(Object obj) {
        if (obj instanceof App2Segment) {
            App2Segment other = (App2Segment)obj;
            return this.curMarker == other.curMarker;
        }
        return false;
    }

    public int hashCode() {
        return this.curMarker;
    }

    @Override
    public int compareTo(App2Segment other) {
        return this.curMarker - other.curMarker;
    }

    public byte[] getIccBytes() {
        return (byte[])this.iccBytes.clone();
    }
}

