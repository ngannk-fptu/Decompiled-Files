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

public class SosSegment
extends Segment {
    private static final Logger LOGGER = Logger.getLogger(SosSegment.class.getName());
    public final int numberOfComponents;
    private final Component[] components;
    public final int startOfSpectralSelection;
    public final int endOfSpectralSelection;
    public final int successiveApproximationBitHigh;
    public final int successiveApproximationBitLow;

    public SosSegment(int marker, byte[] segmentData) throws IOException {
        this(marker, segmentData.length, new ByteArrayInputStream(segmentData));
    }

    public SosSegment(int marker, int markerLength, InputStream is) throws IOException {
        super(marker, markerLength);
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("SosSegment marker_length: " + markerLength);
        }
        this.numberOfComponents = BinaryFunctions.readByte("number_of_components_in_scan", is, "Not a Valid JPEG File");
        this.components = new Component[this.numberOfComponents];
        for (int i = 0; i < this.numberOfComponents; ++i) {
            byte scanComponentSelector = BinaryFunctions.readByte("scanComponentSelector", is, "Not a Valid JPEG File");
            byte acDcEntropoyCodingTableSelector = BinaryFunctions.readByte("acDcEntropoyCodingTableSelector", is, "Not a Valid JPEG File");
            int dcCodingTableSelector = acDcEntropoyCodingTableSelector >> 4 & 0xF;
            int acCodingTableSelector = acDcEntropoyCodingTableSelector & 0xF;
            this.components[i] = new Component(scanComponentSelector, dcCodingTableSelector, acCodingTableSelector);
        }
        this.startOfSpectralSelection = BinaryFunctions.readByte("start_of_spectral_selection", is, "Not a Valid JPEG File");
        this.endOfSpectralSelection = BinaryFunctions.readByte("end_of_spectral_selection", is, "Not a Valid JPEG File");
        byte successiveApproximationBitPosition = BinaryFunctions.readByte("successive_approximation_bit_position", is, "Not a Valid JPEG File");
        this.successiveApproximationBitHigh = successiveApproximationBitPosition >> 4 & 0xF;
        this.successiveApproximationBitLow = successiveApproximationBitPosition & 0xF;
    }

    public Component[] getComponents() {
        return (Component[])this.components.clone();
    }

    public Component getComponents(int index) {
        return this.components[index];
    }

    @Override
    public String getDescription() {
        return "SOS (" + this.getSegmentType() + ")";
    }

    public static class Component {
        public final int scanComponentSelector;
        public final int dcCodingTableSelector;
        public final int acCodingTableSelector;

        public Component(int scanComponentSelector, int dcCodingTableSelector, int acCodingTableSelector) {
            this.scanComponentSelector = scanComponentSelector;
            this.dcCodingTableSelector = dcCodingTableSelector;
            this.acCodingTableSelector = acCodingTableSelector;
        }
    }
}

