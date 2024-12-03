/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.jpeg.exif;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
import org.apache.commons.imaging.common.bytesource.ByteSourceArray;
import org.apache.commons.imaging.common.bytesource.ByteSourceFile;
import org.apache.commons.imaging.common.bytesource.ByteSourceInputStream;
import org.apache.commons.imaging.formats.jpeg.JpegConstants;
import org.apache.commons.imaging.formats.jpeg.JpegUtils;
import org.apache.commons.imaging.formats.tiff.write.TiffImageWriterBase;
import org.apache.commons.imaging.formats.tiff.write.TiffImageWriterLossless;
import org.apache.commons.imaging.formats.tiff.write.TiffImageWriterLossy;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

public class ExifRewriter
extends BinaryFileParser {
    public ExifRewriter() {
        this(ByteOrder.BIG_ENDIAN);
    }

    public ExifRewriter(ByteOrder byteOrder) {
        this.setByteOrder(byteOrder);
    }

    private JFIFPieces analyzeJFIF(ByteSource byteSource) throws ImageReadException, IOException {
        final ArrayList<JFIFPiece> pieces = new ArrayList<JFIFPiece>();
        final ArrayList<JFIFPiece> exifPieces = new ArrayList<JFIFPiece>();
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
            public boolean visitSegment(int marker, byte[] markerBytes, int markerLength, byte[] markerLengthBytes, byte[] segmentData) throws ImageReadException, IOException {
                if (marker != 65505) {
                    pieces.add(new JFIFPieceSegment(marker, markerBytes, markerLengthBytes, segmentData));
                } else if (!BinaryFunctions.startsWith(segmentData, JpegConstants.EXIF_IDENTIFIER_CODE)) {
                    pieces.add(new JFIFPieceSegment(marker, markerBytes, markerLengthBytes, segmentData));
                } else {
                    JFIFPieceSegmentExif piece = new JFIFPieceSegmentExif(marker, markerBytes, markerLengthBytes, segmentData);
                    pieces.add(piece);
                    exifPieces.add(piece);
                }
                return true;
            }
        };
        new JpegUtils().traverseJFIF(byteSource, visitor);
        return new JFIFPieces(pieces, exifPieces);
    }

    public void removeExifMetadata(File src, OutputStream os) throws ImageReadException, IOException, ImageWriteException {
        ByteSourceFile byteSource = new ByteSourceFile(src);
        this.removeExifMetadata(byteSource, os);
    }

    public void removeExifMetadata(byte[] src, OutputStream os) throws ImageReadException, IOException, ImageWriteException {
        ByteSourceArray byteSource = new ByteSourceArray(src);
        this.removeExifMetadata(byteSource, os);
    }

    public void removeExifMetadata(InputStream src, OutputStream os) throws ImageReadException, IOException, ImageWriteException {
        ByteSourceInputStream byteSource = new ByteSourceInputStream(src, null);
        this.removeExifMetadata(byteSource, os);
    }

    public void removeExifMetadata(ByteSource byteSource, OutputStream os) throws ImageReadException, IOException, ImageWriteException {
        JFIFPieces jfifPieces = this.analyzeJFIF(byteSource);
        List<JFIFPiece> pieces = jfifPieces.pieces;
        this.writeSegmentsReplacingExif(os, pieces, null);
    }

    public void updateExifMetadataLossless(File src, OutputStream os, TiffOutputSet outputSet) throws ImageReadException, IOException, ImageWriteException {
        ByteSourceFile byteSource = new ByteSourceFile(src);
        this.updateExifMetadataLossless(byteSource, os, outputSet);
    }

    public void updateExifMetadataLossless(byte[] src, OutputStream os, TiffOutputSet outputSet) throws ImageReadException, IOException, ImageWriteException {
        ByteSourceArray byteSource = new ByteSourceArray(src);
        this.updateExifMetadataLossless(byteSource, os, outputSet);
    }

    public void updateExifMetadataLossless(InputStream src, OutputStream os, TiffOutputSet outputSet) throws ImageReadException, IOException, ImageWriteException {
        ByteSourceInputStream byteSource = new ByteSourceInputStream(src, null);
        this.updateExifMetadataLossless(byteSource, os, outputSet);
    }

    public void updateExifMetadataLossless(ByteSource byteSource, OutputStream os, TiffOutputSet outputSet) throws ImageReadException, IOException, ImageWriteException {
        TiffImageWriterBase writer;
        JFIFPieces jfifPieces = this.analyzeJFIF(byteSource);
        List<JFIFPiece> pieces = jfifPieces.pieces;
        if (!jfifPieces.exifPieces.isEmpty()) {
            JFIFPieceSegment exifPiece = null;
            exifPiece = (JFIFPieceSegment)jfifPieces.exifPieces.get(0);
            byte[] exifBytes = exifPiece.segmentData;
            exifBytes = BinaryFunctions.remainingBytes("trimmed exif bytes", exifBytes, 6);
            writer = new TiffImageWriterLossless(outputSet.byteOrder, exifBytes);
        } else {
            writer = new TiffImageWriterLossy(outputSet.byteOrder);
        }
        boolean includeEXIFPrefix = true;
        byte[] newBytes = this.writeExifSegment(writer, outputSet, true);
        this.writeSegmentsReplacingExif(os, pieces, newBytes);
    }

    public void updateExifMetadataLossy(byte[] src, OutputStream os, TiffOutputSet outputSet) throws ImageReadException, IOException, ImageWriteException {
        ByteSourceArray byteSource = new ByteSourceArray(src);
        this.updateExifMetadataLossy(byteSource, os, outputSet);
    }

    public void updateExifMetadataLossy(InputStream src, OutputStream os, TiffOutputSet outputSet) throws ImageReadException, IOException, ImageWriteException {
        ByteSourceInputStream byteSource = new ByteSourceInputStream(src, null);
        this.updateExifMetadataLossy(byteSource, os, outputSet);
    }

    public void updateExifMetadataLossy(File src, OutputStream os, TiffOutputSet outputSet) throws ImageReadException, IOException, ImageWriteException {
        ByteSourceFile byteSource = new ByteSourceFile(src);
        this.updateExifMetadataLossy(byteSource, os, outputSet);
    }

    public void updateExifMetadataLossy(ByteSource byteSource, OutputStream os, TiffOutputSet outputSet) throws ImageReadException, IOException, ImageWriteException {
        JFIFPieces jfifPieces = this.analyzeJFIF(byteSource);
        List<JFIFPiece> pieces = jfifPieces.pieces;
        TiffImageWriterLossy writer = new TiffImageWriterLossy(outputSet.byteOrder);
        boolean includeEXIFPrefix = true;
        byte[] newBytes = this.writeExifSegment(writer, outputSet, true);
        this.writeSegmentsReplacingExif(os, pieces, newBytes);
    }

    private void writeSegmentsReplacingExif(OutputStream outputStream, List<JFIFPiece> segments, byte[] newBytes) throws ImageWriteException, IOException {
        try (DataOutputStream os = new DataOutputStream(outputStream);){
            JpegConstants.SOI.writeTo(os);
            boolean hasExif = false;
            for (JFIFPiece piece : segments) {
                if (!(piece instanceof JFIFPieceSegmentExif)) continue;
                hasExif = true;
                break;
            }
            if (!hasExif && newBytes != null) {
                byte[] markerBytes = ByteConversions.toBytes((short)-31, this.getByteOrder());
                if (newBytes.length > 65535) {
                    throw new ExifOverflowException("APP1 Segment is too long: " + newBytes.length);
                }
                int markerLength = newBytes.length + 2;
                byte[] markerLengthBytes = ByteConversions.toBytes((short)markerLength, this.getByteOrder());
                int index = 0;
                JFIFPieceSegment firstSegment = (JFIFPieceSegment)segments.get(index);
                if (firstSegment.marker == 65504) {
                    index = 1;
                }
                segments.add(index, new JFIFPieceSegmentExif(65505, markerBytes, markerLengthBytes, newBytes));
            }
            boolean APP1Written = false;
            for (JFIFPiece piece : segments) {
                if (piece instanceof JFIFPieceSegmentExif) {
                    if (APP1Written) continue;
                    APP1Written = true;
                    if (newBytes == null) continue;
                    byte[] markerBytes = ByteConversions.toBytes((short)-31, this.getByteOrder());
                    if (newBytes.length > 65535) {
                        throw new ExifOverflowException("APP1 Segment is too long: " + newBytes.length);
                    }
                    int markerLength = newBytes.length + 2;
                    byte[] markerLengthBytes = ByteConversions.toBytes((short)markerLength, this.getByteOrder());
                    os.write(markerBytes);
                    os.write(markerLengthBytes);
                    os.write(newBytes);
                    continue;
                }
                piece.write(os);
            }
        }
    }

    private byte[] writeExifSegment(TiffImageWriterBase writer, TiffOutputSet outputSet, boolean includeEXIFPrefix) throws IOException, ImageWriteException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        if (includeEXIFPrefix) {
            JpegConstants.EXIF_IDENTIFIER_CODE.writeTo(os);
            os.write(0);
            os.write(0);
        }
        writer.write(os, outputSet);
        return os.toByteArray();
    }

    public static class ExifOverflowException
    extends ImageWriteException {
        private static final long serialVersionUID = 1401484357224931218L;

        public ExifOverflowException(String message) {
            super(message);
        }
    }

    private static class JFIFPieceImageData
    extends JFIFPiece {
        public final byte[] markerBytes;
        public final byte[] imageData;

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

    private static class JFIFPieceSegmentExif
    extends JFIFPieceSegment {
        JFIFPieceSegmentExif(int marker, byte[] markerBytes, byte[] markerLengthBytes, byte[] segmentData) {
            super(marker, markerBytes, markerLengthBytes, segmentData);
        }
    }

    private static class JFIFPieceSegment
    extends JFIFPiece {
        public final int marker;
        public final byte[] markerBytes;
        public final byte[] markerLengthBytes;
        public final byte[] segmentData;

        JFIFPieceSegment(int marker, byte[] markerBytes, byte[] markerLengthBytes, byte[] segmentData) {
            this.marker = marker;
            this.markerBytes = markerBytes;
            this.markerLengthBytes = markerLengthBytes;
            this.segmentData = segmentData;
        }

        @Override
        protected void write(OutputStream os) throws IOException {
            os.write(this.markerBytes);
            os.write(this.markerLengthBytes);
            os.write(this.segmentData);
        }
    }

    private static abstract class JFIFPiece {
        private JFIFPiece() {
        }

        protected abstract void write(OutputStream var1) throws IOException;
    }

    private static class JFIFPieces {
        public final List<JFIFPiece> pieces;
        public final List<JFIFPiece> exifPieces;

        JFIFPieces(List<JFIFPiece> pieces, List<JFIFPiece> exifPieces) {
            this.pieces = pieces;
            this.exifPieces = exifPieces;
        }
    }
}

