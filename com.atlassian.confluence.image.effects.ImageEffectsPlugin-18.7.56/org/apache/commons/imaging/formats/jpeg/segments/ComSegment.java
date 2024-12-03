/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.jpeg.segments;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import org.apache.commons.imaging.formats.jpeg.segments.GenericSegment;

public class ComSegment
extends GenericSegment {
    public ComSegment(int marker, byte[] segmentData) {
        super(marker, segmentData);
    }

    public ComSegment(int marker, int markerLength, InputStream is) throws IOException {
        super(marker, markerLength, is);
    }

    public byte[] getComment() {
        return this.getSegmentData();
    }

    @Override
    public String getDescription() {
        String commentString = "";
        try {
            commentString = this.getSegmentDataAsString("UTF-8");
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            // empty catch block
        }
        return "COM (" + commentString + ")";
    }
}

