/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.jpeg.xmp;

import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.BinaryFileParser;
import org.apache.commons.imaging.common.BinaryFunctions;
import org.apache.commons.imaging.formats.jpeg.JpegConstants;

public class JpegXmpParser
extends BinaryFileParser {
    public JpegXmpParser() {
        this.setByteOrder(ByteOrder.BIG_ENDIAN);
    }

    public boolean isXmpJpegSegment(byte[] segmentData) {
        return BinaryFunctions.startsWith(segmentData, JpegConstants.XMP_IDENTIFIER);
    }

    public String parseXmpJpegSegment(byte[] segmentData) throws ImageReadException {
        if (!this.isXmpJpegSegment(segmentData)) {
            throw new ImageReadException("Invalid JPEG XMP Segment.");
        }
        int index = JpegConstants.XMP_IDENTIFIER.size();
        return new String(segmentData, index, segmentData.length - index, StandardCharsets.UTF_8);
    }
}

