/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.jpeg.segments;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.imaging.common.BinaryFunctions;
import org.apache.commons.imaging.formats.jpeg.segments.Segment;

public class SofnSegment
extends Segment {
    private static final Logger LOGGER = Logger.getLogger(SofnSegment.class.getName());
    public final int width;
    public final int height;
    public final int numberOfComponents;
    public final int precision;
    private final Component[] components;

    public SofnSegment(int marker, byte[] segmentData) throws IOException {
        this(marker, segmentData.length, new ByteArrayInputStream(segmentData));
    }

    public SofnSegment(int marker, int markerLength, InputStream is) throws IOException {
        super(marker, markerLength);
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("SOF0Segment marker_length: " + markerLength);
        }
        this.precision = BinaryFunctions.readByte("Data_precision", is, "Not a Valid JPEG File");
        this.height = BinaryFunctions.read2Bytes("Image_height", is, "Not a Valid JPEG File", this.getByteOrder());
        this.width = BinaryFunctions.read2Bytes("Image_Width", is, "Not a Valid JPEG File", this.getByteOrder());
        this.numberOfComponents = BinaryFunctions.readByte("Number_of_components", is, "Not a Valid JPEG File");
        this.components = new Component[this.numberOfComponents];
        for (int i = 0; i < this.numberOfComponents; ++i) {
            byte componentIdentifier = BinaryFunctions.readByte("ComponentIdentifier", is, "Not a Valid JPEG File");
            byte hvSamplingFactors = BinaryFunctions.readByte("SamplingFactors", is, "Not a Valid JPEG File");
            int horizontalSamplingFactor = hvSamplingFactors >> 4 & 0xF;
            int verticalSamplingFactor = hvSamplingFactors & 0xF;
            byte quantTabDestSelector = BinaryFunctions.readByte("QuantTabDestSel", is, "Not a Valid JPEG File");
            this.components[i] = new Component(componentIdentifier, horizontalSamplingFactor, verticalSamplingFactor, quantTabDestSelector);
        }
    }

    public Component[] getComponents() {
        return (Component[])this.components.clone();
    }

    public Component getComponents(int index) {
        return this.components[index];
    }

    @Override
    public String getDescription() {
        return "SOFN (SOF" + (this.marker - 65472) + ") (" + this.getSegmentType() + ")";
    }

    public static class Component {
        public final int componentIdentifier;
        public final int horizontalSamplingFactor;
        public final int verticalSamplingFactor;
        public final int quantTabDestSelector;

        public Component(int componentIdentifier, int horizontalSamplingFactor, int veritcalSamplingFactor, int quantTabDestSelector) {
            this.componentIdentifier = componentIdentifier;
            this.horizontalSamplingFactor = horizontalSamplingFactor;
            this.verticalSamplingFactor = veritcalSamplingFactor;
            this.quantTabDestSelector = quantTabDestSelector;
        }
    }
}

