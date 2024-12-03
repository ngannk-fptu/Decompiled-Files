/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.jpeg.xmp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.common.bytesource.ByteSource;
import org.apache.commons.imaging.common.bytesource.ByteSourceArray;
import org.apache.commons.imaging.common.bytesource.ByteSourceFile;
import org.apache.commons.imaging.common.bytesource.ByteSourceInputStream;
import org.apache.commons.imaging.formats.jpeg.JpegConstants;
import org.apache.commons.imaging.formats.jpeg.xmp.JpegRewriter;

public class JpegXmpRewriter
extends JpegRewriter {
    public void removeXmpXml(File src, OutputStream os) throws ImageReadException, IOException {
        ByteSourceFile byteSource = new ByteSourceFile(src);
        this.removeXmpXml(byteSource, os);
    }

    public void removeXmpXml(byte[] src, OutputStream os) throws ImageReadException, IOException {
        ByteSourceArray byteSource = new ByteSourceArray(src);
        this.removeXmpXml(byteSource, os);
    }

    public void removeXmpXml(InputStream src, OutputStream os) throws ImageReadException, IOException {
        ByteSourceInputStream byteSource = new ByteSourceInputStream(src, null);
        this.removeXmpXml(byteSource, os);
    }

    public void removeXmpXml(ByteSource byteSource, OutputStream os) throws ImageReadException, IOException {
        JpegRewriter.JFIFPieces jfifPieces = this.analyzeJFIF(byteSource);
        List<JpegRewriter.JFIFPiece> pieces = jfifPieces.pieces;
        pieces = this.removeXmpSegments(pieces);
        this.writeSegments(os, pieces);
    }

    public void updateXmpXml(byte[] src, OutputStream os, String xmpXml) throws ImageReadException, IOException, ImageWriteException {
        ByteSourceArray byteSource = new ByteSourceArray(src);
        this.updateXmpXml(byteSource, os, xmpXml);
    }

    public void updateXmpXml(InputStream src, OutputStream os, String xmpXml) throws ImageReadException, IOException, ImageWriteException {
        ByteSourceInputStream byteSource = new ByteSourceInputStream(src, null);
        this.updateXmpXml(byteSource, os, xmpXml);
    }

    public void updateXmpXml(File src, OutputStream os, String xmpXml) throws ImageReadException, IOException, ImageWriteException {
        ByteSourceFile byteSource = new ByteSourceFile(src);
        this.updateXmpXml(byteSource, os, xmpXml);
    }

    public void updateXmpXml(ByteSource byteSource, OutputStream os, String xmpXml) throws ImageReadException, IOException, ImageWriteException {
        int segmentSize;
        JpegRewriter.JFIFPieces jfifPieces = this.analyzeJFIF(byteSource);
        List<JpegRewriter.JFIFPiece> pieces = jfifPieces.pieces;
        pieces = this.removeXmpSegments(pieces);
        ArrayList<JpegRewriter.JFIFPieceSegment> newPieces = new ArrayList<JpegRewriter.JFIFPieceSegment>();
        byte[] xmpXmlBytes = xmpXml.getBytes(StandardCharsets.UTF_8);
        for (int index = 0; index < xmpXmlBytes.length; index += segmentSize) {
            segmentSize = Math.min(xmpXmlBytes.length, 65535);
            byte[] segmentData = this.writeXmpSegment(xmpXmlBytes, index, segmentSize);
            newPieces.add(new JpegRewriter.JFIFPieceSegment(65505, segmentData));
        }
        pieces = this.insertAfterLastAppSegments(pieces, newPieces);
        this.writeSegments(os, pieces);
    }

    private byte[] writeXmpSegment(byte[] xmpXmlData, int start, int length) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        JpegConstants.XMP_IDENTIFIER.writeTo(os);
        os.write(xmpXmlData, start, length);
        return os.toByteArray();
    }
}

