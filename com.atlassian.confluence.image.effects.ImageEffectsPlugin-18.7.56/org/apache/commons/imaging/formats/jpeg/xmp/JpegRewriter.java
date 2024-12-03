/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.jpeg.xmp;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.common.BinaryFileParser;
import org.apache.commons.imaging.common.BinaryFunctions;
import org.apache.commons.imaging.common.ByteConversions;
import org.apache.commons.imaging.common.bytesource.ByteSource;
import org.apache.commons.imaging.formats.jpeg.JpegConstants;
import org.apache.commons.imaging.formats.jpeg.JpegUtils;
import org.apache.commons.imaging.formats.jpeg.iptc.IptcParser;

public class JpegRewriter
extends BinaryFileParser {
    private static final ByteOrder JPEG_BYTE_ORDER = ByteOrder.BIG_ENDIAN;
    private static final SegmentFilter EXIF_SEGMENT_FILTER = segment -> segment.isExifSegment();
    private static final SegmentFilter XMP_SEGMENT_FILTER = segment -> segment.isXmpSegment();
    private static final SegmentFilter PHOTOSHOP_APP13_SEGMENT_FILTER = segment -> segment.isPhotoshopApp13Segment();

    public JpegRewriter() {
        this.setByteOrder(JPEG_BYTE_ORDER);
    }

    protected JFIFPieces analyzeJFIF(ByteSource byteSource) throws ImageReadException, IOException {
        final ArrayList<JFIFPiece> pieces = new ArrayList<JFIFPiece>();
        final ArrayList<JFIFPiece> segmentPieces = new ArrayList<JFIFPiece>();
        JpegUtils.Visitor visitor = new JpegUtils.Visitor(){

            @Override
            public boolean beginSOS() {
                return true;
            }

            @Override
            public void visitSOS(int marker, byte[] markerBytes, byte[] imageData) {
                pieces.add(new JFIFPieceImageData(markerBytes, imageData));
            }

            @Override
            public boolean visitSegment(int marker, byte[] markerBytes, int segmentLength, byte[] segmentLengthBytes, byte[] segmentData) throws ImageReadException, IOException {
                JFIFPieceSegment piece = new JFIFPieceSegment(marker, markerBytes, segmentLengthBytes, segmentData);
                pieces.add(piece);
                segmentPieces.add(piece);
                return true;
            }
        };
        new JpegUtils().traverseJFIF(byteSource, visitor);
        return new JFIFPieces(pieces, segmentPieces);
    }

    protected <T extends JFIFPiece> List<T> removeXmpSegments(List<T> segments) {
        return this.filterSegments(segments, XMP_SEGMENT_FILTER);
    }

    protected <T extends JFIFPiece> List<T> removePhotoshopApp13Segments(List<T> segments) {
        return this.filterSegments(segments, PHOTOSHOP_APP13_SEGMENT_FILTER);
    }

    protected <T extends JFIFPiece> List<T> findPhotoshopApp13Segments(List<T> segments) {
        return this.filterSegments(segments, PHOTOSHOP_APP13_SEGMENT_FILTER, true);
    }

    protected <T extends JFIFPiece> List<T> removeExifSegments(List<T> segments) {
        return this.filterSegments(segments, EXIF_SEGMENT_FILTER);
    }

    protected <T extends JFIFPiece> List<T> filterSegments(List<T> segments, SegmentFilter filter) {
        return this.filterSegments(segments, filter, false);
    }

    protected <T extends JFIFPiece> List<T> filterSegments(List<T> segments, SegmentFilter filter, boolean reverse) {
        ArrayList<JFIFPiece> result = new ArrayList<JFIFPiece>();
        for (JFIFPiece piece : segments) {
            if (piece instanceof JFIFPieceSegment) {
                if (!(filter.filter((JFIFPieceSegment)piece) ^ !reverse)) continue;
                result.add(piece);
                continue;
            }
            if (reverse) continue;
            result.add(piece);
        }
        return result;
    }

    protected <T extends JFIFPiece, U extends JFIFPiece> List<JFIFPiece> insertBeforeFirstAppSegments(List<T> segments, List<U> newSegments) throws ImageWriteException {
        int firstAppIndex = -1;
        for (int i = 0; i < segments.size(); ++i) {
            JFIFPieceSegment segment;
            JFIFPiece piece = (JFIFPiece)segments.get(i);
            if (!(piece instanceof JFIFPieceSegment) || !(segment = (JFIFPieceSegment)piece).isAppSegment() || firstAppIndex != -1) continue;
            firstAppIndex = i;
        }
        ArrayList<JFIFPiece> result = new ArrayList<JFIFPiece>(segments);
        if (firstAppIndex == -1) {
            throw new ImageWriteException("JPEG file has no APP segments.");
        }
        result.addAll(firstAppIndex, newSegments);
        return result;
    }

    protected <T extends JFIFPiece, U extends JFIFPiece> List<JFIFPiece> insertAfterLastAppSegments(List<T> segments, List<U> newSegments) throws ImageWriteException {
        int lastAppIndex = -1;
        for (int i = 0; i < segments.size(); ++i) {
            JFIFPieceSegment segment;
            JFIFPiece piece = (JFIFPiece)segments.get(i);
            if (!(piece instanceof JFIFPieceSegment) || !(segment = (JFIFPieceSegment)piece).isAppSegment()) continue;
            lastAppIndex = i;
        }
        ArrayList<JFIFPiece> result = new ArrayList<JFIFPiece>(segments);
        if (lastAppIndex == -1) {
            if (segments.isEmpty()) {
                throw new ImageWriteException("JPEG file has no APP segments.");
            }
            result.addAll(1, newSegments);
        } else {
            result.addAll(lastAppIndex + 1, newSegments);
        }
        return result;
    }

    protected void writeSegments(OutputStream outputStream, List<? extends JFIFPiece> segments) throws IOException {
        try (DataOutputStream os = new DataOutputStream(outputStream);){
            JpegConstants.SOI.writeTo(os);
            for (JFIFPiece jFIFPiece : segments) {
                jFIFPiece.write(os);
            }
        }
    }

    public static class JpegSegmentOverflowException
    extends ImageWriteException {
        private static final long serialVersionUID = -1062145751550646846L;

        public JpegSegmentOverflowException(String message) {
            super(message);
        }
    }

    private static interface SegmentFilter {
        public boolean filter(JFIFPieceSegment var1);
    }

    static class JFIFPieceImageData
    extends JFIFPiece {
        private final byte[] markerBytes;
        private final byte[] imageData;

        JFIFPieceImageData(byte[] markerBytes, byte[] imageData) {
            this.markerBytes = markerBytes;
            this.imageData = imageData;
        }

        @Override
        protected void write(OutputStream os) throws IOException {
            os.write(this.markerBytes);
            os.write(this.imageData);
        }
    }

    protected static class JFIFPieceSegment
    extends JFIFPiece {
        public final int marker;
        private final byte[] markerBytes;
        private final byte[] segmentLengthBytes;
        private final byte[] segmentData;

        public JFIFPieceSegment(int marker, byte[] segmentData) {
            this(marker, ByteConversions.toBytes((short)marker, JPEG_BYTE_ORDER), ByteConversions.toBytes((short)(segmentData.length + 2), JPEG_BYTE_ORDER), segmentData);
        }

        JFIFPieceSegment(int marker, byte[] markerBytes, byte[] segmentLengthBytes, byte[] segmentData) {
            this.marker = marker;
            this.markerBytes = markerBytes;
            this.segmentLengthBytes = segmentLengthBytes;
            this.segmentData = (byte[])segmentData.clone();
        }

        @Override
        public String toString() {
            return "[" + this.getClass().getName() + " (0x" + Integer.toHexString(this.marker) + ")]";
        }

        @Override
        protected void write(OutputStream os) throws IOException {
            os.write(this.markerBytes);
            os.write(this.segmentLengthBytes);
            os.write(this.segmentData);
        }

        public boolean isApp1Segment() {
            return this.marker == 65505;
        }

        public boolean isAppSegment() {
            return this.marker >= 65504 && this.marker <= 65519;
        }

        public boolean isExifSegment() {
            if (this.marker != 65505) {
                return false;
            }
            return BinaryFunctions.startsWith(this.segmentData, JpegConstants.EXIF_IDENTIFIER_CODE);
        }

        public boolean isPhotoshopApp13Segment() {
            if (this.marker != 65517) {
                return false;
            }
            return new IptcParser().isPhotoshopJpegSegment(this.segmentData);
        }

        public boolean isXmpSegment() {
            if (this.marker != 65505) {
                return false;
            }
            return BinaryFunctions.startsWith(this.segmentData, JpegConstants.XMP_IDENTIFIER);
        }

        public byte[] getSegmentData() {
            return (byte[])this.segmentData.clone();
        }
    }

    protected static abstract class JFIFPiece {
        protected JFIFPiece() {
        }

        protected abstract void write(OutputStream var1) throws IOException;

        public String toString() {
            return "[" + this.getClass().getName() + "]";
        }
    }

    protected static class JFIFPieces {
        public final List<JFIFPiece> pieces;
        public final List<JFIFPiece> segmentPieces;

        public JFIFPieces(List<JFIFPiece> pieces, List<JFIFPiece> segmentPieces) {
            this.pieces = pieces;
            this.segmentPieces = segmentPieces;
        }
    }
}

