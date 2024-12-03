/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.jpeg.segments;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.BinaryFunctions;
import org.apache.commons.imaging.formats.jpeg.segments.Segment;

public class DqtSegment
extends Segment {
    public final List<QuantizationTable> quantizationTables = new ArrayList<QuantizationTable>();

    public DqtSegment(int marker, byte[] segmentData) throws ImageReadException, IOException {
        this(marker, segmentData.length, new ByteArrayInputStream(segmentData));
    }

    public DqtSegment(int marker, int length, InputStream is) throws ImageReadException, IOException {
        super(marker, length);
        while (length > 0) {
            byte precisionAndDestination = BinaryFunctions.readByte("QuantizationTablePrecisionAndDestination", is, "Not a Valid JPEG File");
            --length;
            int precision = precisionAndDestination >> 4 & 0xF;
            int destinationIdentifier = precisionAndDestination & 0xF;
            int[] elements = new int[64];
            for (int i = 0; i < 64; ++i) {
                if (precision == 0) {
                    elements[i] = 0xFF & BinaryFunctions.readByte("QuantizationTableElement", is, "Not a Valid JPEG File");
                    --length;
                    continue;
                }
                if (precision == 1) {
                    elements[i] = BinaryFunctions.read2Bytes("QuantizationTableElement", is, "Not a Valid JPEG File", this.getByteOrder());
                    length -= 2;
                    continue;
                }
                throw new ImageReadException("Quantization table precision '" + precision + "' is invalid");
            }
            this.quantizationTables.add(new QuantizationTable(precision, destinationIdentifier, elements));
        }
    }

    @Override
    public String getDescription() {
        return "DQT (" + this.getSegmentType() + ")";
    }

    public static class QuantizationTable {
        public final int precision;
        public final int destinationIdentifier;
        private final int[] elements;

        public QuantizationTable(int precision, int destinationIdentifier, int[] elements) {
            this.precision = precision;
            this.destinationIdentifier = destinationIdentifier;
            this.elements = elements;
        }

        public int[] getElements() {
            return this.elements;
        }
    }
}

