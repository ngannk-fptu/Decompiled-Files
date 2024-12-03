/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.jpeg.segments;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.imaging.formats.jpeg.segments.GenericSegment;

public class UnknownSegment
extends GenericSegment {
    public UnknownSegment(int marker, int markerLength, InputStream is) throws IOException {
        super(marker, markerLength, is);
    }

    public UnknownSegment(int marker, byte[] bytes) {
        super(marker, bytes);
    }

    @Override
    public String getDescription() {
        return "Unknown (" + this.getSegmentType() + ")";
    }
}

