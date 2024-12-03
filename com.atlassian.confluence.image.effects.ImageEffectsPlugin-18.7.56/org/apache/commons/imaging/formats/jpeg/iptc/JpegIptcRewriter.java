/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.jpeg.iptc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.common.bytesource.ByteSource;
import org.apache.commons.imaging.common.bytesource.ByteSourceArray;
import org.apache.commons.imaging.common.bytesource.ByteSourceFile;
import org.apache.commons.imaging.common.bytesource.ByteSourceInputStream;
import org.apache.commons.imaging.formats.jpeg.iptc.IptcBlock;
import org.apache.commons.imaging.formats.jpeg.iptc.IptcParser;
import org.apache.commons.imaging.formats.jpeg.iptc.IptcRecord;
import org.apache.commons.imaging.formats.jpeg.iptc.PhotoshopApp13Data;
import org.apache.commons.imaging.formats.jpeg.xmp.JpegRewriter;

public class JpegIptcRewriter
extends JpegRewriter {
    public void removeIPTC(File src, OutputStream os) throws ImageReadException, IOException, ImageWriteException {
        this.removeIPTC(src, os, false);
    }

    public void removeIPTC(File src, OutputStream os, boolean removeSegment) throws ImageReadException, IOException, ImageWriteException {
        ByteSourceFile byteSource = new ByteSourceFile(src);
        this.removeIPTC(byteSource, os, removeSegment);
    }

    public void removeIPTC(byte[] src, OutputStream os) throws ImageReadException, IOException, ImageWriteException {
        this.removeIPTC(src, os, false);
    }

    public void removeIPTC(byte[] src, OutputStream os, boolean removeSegment) throws ImageReadException, IOException, ImageWriteException {
        ByteSourceArray byteSource = new ByteSourceArray(src);
        this.removeIPTC(byteSource, os, removeSegment);
    }

    public void removeIPTC(InputStream src, OutputStream os) throws ImageReadException, IOException, ImageWriteException {
        this.removeIPTC(src, os, false);
    }

    public void removeIPTC(InputStream src, OutputStream os, boolean removeSegment) throws ImageReadException, IOException, ImageWriteException {
        ByteSourceInputStream byteSource = new ByteSourceInputStream(src, null);
        this.removeIPTC(byteSource, os, removeSegment);
    }

    public void removeIPTC(ByteSource byteSource, OutputStream os) throws ImageReadException, IOException, ImageWriteException {
        this.removeIPTC(byteSource, os, false);
    }

    public void removeIPTC(ByteSource byteSource, OutputStream os, boolean removeSegment) throws ImageReadException, IOException, ImageWriteException {
        JpegRewriter.JFIFPieces jfifPieces = this.analyzeJFIF(byteSource);
        List<JpegRewriter.JFIFPiece> oldPieces = jfifPieces.pieces;
        List<JpegRewriter.JFIFPiece> photoshopApp13Segments = this.findPhotoshopApp13Segments(oldPieces);
        if (photoshopApp13Segments.size() > 1) {
            throw new ImageReadException("Image contains more than one Photoshop App13 segment.");
        }
        List<JpegRewriter.JFIFPiece> newPieces = this.removePhotoshopApp13Segments(oldPieces);
        if (!removeSegment && photoshopApp13Segments.size() == 1) {
            JpegRewriter.JFIFPieceSegment oldSegment = (JpegRewriter.JFIFPieceSegment)photoshopApp13Segments.get(0);
            HashMap<String, Object> params = new HashMap<String, Object>();
            PhotoshopApp13Data oldData = new IptcParser().parsePhotoshopSegment(oldSegment.getSegmentData(), params);
            List<IptcBlock> newBlocks = oldData.getNonIptcBlocks();
            ArrayList<IptcRecord> newRecords = new ArrayList<IptcRecord>();
            PhotoshopApp13Data newData = new PhotoshopApp13Data(newRecords, newBlocks);
            byte[] segmentBytes = new IptcParser().writePhotoshopApp13Segment(newData);
            JpegRewriter.JFIFPieceSegment newSegment = new JpegRewriter.JFIFPieceSegment(oldSegment.marker, segmentBytes);
            newPieces.add(oldPieces.indexOf(oldSegment), newSegment);
        }
        this.writeSegments(os, newPieces);
    }

    public void writeIPTC(byte[] src, OutputStream os, PhotoshopApp13Data newData) throws ImageReadException, IOException, ImageWriteException {
        ByteSourceArray byteSource = new ByteSourceArray(src);
        this.writeIPTC(byteSource, os, newData);
    }

    public void writeIPTC(InputStream src, OutputStream os, PhotoshopApp13Data newData) throws ImageReadException, IOException, ImageWriteException {
        ByteSourceInputStream byteSource = new ByteSourceInputStream(src, null);
        this.writeIPTC(byteSource, os, newData);
    }

    public void writeIPTC(File src, OutputStream os, PhotoshopApp13Data newData) throws ImageReadException, IOException, ImageWriteException {
        ByteSourceFile byteSource = new ByteSourceFile(src);
        this.writeIPTC(byteSource, os, newData);
    }

    public void writeIPTC(ByteSource byteSource, OutputStream os, PhotoshopApp13Data newData) throws ImageReadException, IOException, ImageWriteException {
        JpegRewriter.JFIFPieces jfifPieces = this.analyzeJFIF(byteSource);
        List<JpegRewriter.JFIFPiece> oldPieces = jfifPieces.pieces;
        List<JpegRewriter.JFIFPiece> photoshopApp13Segments = this.findPhotoshopApp13Segments(oldPieces);
        if (photoshopApp13Segments.size() > 1) {
            throw new ImageReadException("Image contains more than one Photoshop App13 segment.");
        }
        List<JpegRewriter.JFIFPiece> newPieces = this.removePhotoshopApp13Segments(oldPieces);
        List<IptcBlock> newBlocks = newData.getNonIptcBlocks();
        byte[] newBlockBytes = new IptcParser().writeIPTCBlock(newData.getRecords());
        int blockType = 1028;
        byte[] blockNameBytes = new byte[]{};
        IptcBlock newBlock = new IptcBlock(1028, blockNameBytes, newBlockBytes);
        newBlocks.add(newBlock);
        newData = new PhotoshopApp13Data(newData.getRecords(), newBlocks);
        byte[] segmentBytes = new IptcParser().writePhotoshopApp13Segment(newData);
        JpegRewriter.JFIFPieceSegment newSegment = new JpegRewriter.JFIFPieceSegment(65517, segmentBytes);
        newPieces = this.insertAfterLastAppSegments(newPieces, Arrays.asList(newSegment));
        this.writeSegments(os, newPieces);
    }
}

