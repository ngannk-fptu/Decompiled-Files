/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.jpeg.segments;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.imaging.formats.jpeg.segments.GenericSegment;

public class AppnSegment
extends GenericSegment {
    public AppnSegment(int marker, int markerLength, InputStream is) throws IOException {
        super(marker, markerLength, is);
    }

    @Override
    public String getDescription() {
        return "APPN (APP" + (this.marker - 65504) + ") (" + this.getSegmentType() + ")";
    }
}

