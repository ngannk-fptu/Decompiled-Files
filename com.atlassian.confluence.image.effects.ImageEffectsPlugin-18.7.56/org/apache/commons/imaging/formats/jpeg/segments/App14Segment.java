/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.jpeg.segments;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.commons.imaging.common.BinaryFunctions;
import org.apache.commons.imaging.formats.jpeg.segments.AppnSegment;

public class App14Segment
extends AppnSegment {
    private static final byte[] ADOBE_PREFIX = "Adobe".getBytes(StandardCharsets.US_ASCII);
    public static final int ADOBE_COLOR_TRANSFORM_UNKNOWN = 0;
    public static final int ADOBE_COLOR_TRANSFORM_YCbCr = 1;
    public static final int ADOBE_COLOR_TRANSFORM_YCCK = 2;

    public App14Segment(int marker, byte[] segmentData) throws IOException {
        this(marker, segmentData.length, new ByteArrayInputStream(segmentData));
    }

    public App14Segment(int marker, int markerLength, InputStream is) throws IOException {
        super(marker, markerLength, is);
    }

    public boolean isAdobeJpegSegment() {
        return BinaryFunctions.startsWith(this.getSegmentData(), ADOBE_PREFIX);
    }

    public int getAdobeColorTransform() {
        return 0xFF & this.getSegmentData(11);
    }
}

