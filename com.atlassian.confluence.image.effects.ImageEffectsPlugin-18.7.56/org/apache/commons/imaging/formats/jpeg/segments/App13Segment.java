/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.jpeg.segments;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.formats.jpeg.JpegImageParser;
import org.apache.commons.imaging.formats.jpeg.iptc.IptcParser;
import org.apache.commons.imaging.formats.jpeg.iptc.PhotoshopApp13Data;
import org.apache.commons.imaging.formats.jpeg.segments.AppnSegment;

public class App13Segment
extends AppnSegment {
    public App13Segment(JpegImageParser parser, int marker, byte[] segmentData) throws IOException {
        this(marker, segmentData.length, new ByteArrayInputStream(segmentData));
    }

    public App13Segment(int marker, int markerLength, InputStream is) throws IOException {
        super(marker, markerLength, is);
    }

    public boolean isPhotoshopJpegSegment() {
        return new IptcParser().isPhotoshopJpegSegment(this.getSegmentData());
    }

    public PhotoshopApp13Data parsePhotoshopSegment(Map<String, Object> params) throws ImageReadException, IOException {
        if (!this.isPhotoshopJpegSegment()) {
            return null;
        }
        return new IptcParser().parsePhotoshopSegment(this.getSegmentData(), params);
    }
}

