/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.jpeg.iptc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.common.BinaryFileParser;
import org.apache.commons.imaging.common.BinaryFunctions;
import org.apache.commons.imaging.common.BinaryOutputStream;
import org.apache.commons.imaging.common.ByteConversions;
import org.apache.commons.imaging.formats.jpeg.JpegConstants;
import org.apache.commons.imaging.formats.jpeg.iptc.IptcBlock;
import org.apache.commons.imaging.formats.jpeg.iptc.IptcRecord;
import org.apache.commons.imaging.formats.jpeg.iptc.IptcType;
import org.apache.commons.imaging.formats.jpeg.iptc.IptcTypeLookup;
import org.apache.commons.imaging.formats.jpeg.iptc.IptcTypes;
import org.apache.commons.imaging.formats.jpeg.iptc.PhotoshopApp13Data;
import org.apache.commons.imaging.internal.Debug;

public class IptcParser
extends BinaryFileParser {
    private static final Logger LOGGER = Logger.getLogger(IptcParser.class.getName());
    private static final ByteOrder APP13_BYTE_ORDER = ByteOrder.BIG_ENDIAN;
    private static final List<Integer> PHOTOSHOP_IGNORED_BLOCK_TYPE = Arrays.asList(1084, 1085, 1086, 1087);

    public IptcParser() {
        this.setByteOrder(ByteOrder.BIG_ENDIAN);
    }

    public boolean isPhotoshopJpegSegment(byte[] segmentData) {
        if (!BinaryFunctions.startsWith(segmentData, JpegConstants.PHOTOSHOP_IDENTIFICATION_STRING)) {
            return false;
        }
        int index = JpegConstants.PHOTOSHOP_IDENTIFICATION_STRING.size();
        return index + 4 <= segmentData.length && ByteConversions.toInt(segmentData, index, APP13_BYTE_ORDER) == JpegConstants.CONST_8BIM;
    }

    public PhotoshopApp13Data parsePhotoshopSegment(byte[] bytes, Map<String, Object> params) throws ImageReadException, IOException {
        boolean strict = params != null && Boolean.TRUE.equals(params.get("STRICT"));
        return this.parsePhotoshopSegment(bytes, strict);
    }

    public PhotoshopApp13Data parsePhotoshopSegment(byte[] bytes, boolean strict) throws ImageReadException, IOException {
        ArrayList<IptcRecord> records = new ArrayList<IptcRecord>();
        List<IptcBlock> blocks = this.parseAllBlocks(bytes, strict);
        for (IptcBlock block : blocks) {
            if (!block.isIPTCBlock()) continue;
            records.addAll(this.parseIPTCBlock(block.getBlockData()));
        }
        return new PhotoshopApp13Data(records, blocks);
    }

    protected List<IptcRecord> parseIPTCBlock(byte[] bytes) throws IOException {
        ArrayList<IptcRecord> elements = new ArrayList<IptcRecord>();
        int index = 0;
        while (index + 1 < bytes.length) {
            int tagMarker = 0xFF & bytes[index++];
            Debug.debug("tagMarker: " + tagMarker + " (0x" + Integer.toHexString(tagMarker) + ")");
            if (tagMarker != 28) {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.fine("Unexpected record tag marker in IPTC data.");
                }
                return elements;
            }
            int recordNumber = 0xFF & bytes[index++];
            Debug.debug("recordNumber: " + recordNumber + " (0x" + Integer.toHexString(recordNumber) + ")");
            int recordType = 0xFF & bytes[index];
            Debug.debug("recordType: " + recordType + " (0x" + Integer.toHexString(recordType) + ")");
            int recordSize = ByteConversions.toUInt16(bytes, ++index, this.getByteOrder());
            index += 2;
            boolean extendedDataset = recordSize > Short.MAX_VALUE;
            int dataFieldCountLength = recordSize & Short.MAX_VALUE;
            if (extendedDataset) {
                Debug.debug("extendedDataset. dataFieldCountLength: " + dataFieldCountLength);
            }
            if (extendedDataset) {
                return elements;
            }
            byte[] recordData = BinaryFunctions.slice(bytes, index, recordSize);
            index += recordSize;
            if (recordNumber != 2) continue;
            if (recordType == 0) {
                if (!LOGGER.isLoggable(Level.FINE)) continue;
                LOGGER.fine("ignore record version record! " + elements.size());
                continue;
            }
            String value = new String(recordData, StandardCharsets.ISO_8859_1);
            IptcType iptcType = IptcTypeLookup.getIptcType(recordType);
            IptcRecord element = new IptcRecord(iptcType, value);
            elements.add(element);
        }
        return elements;
    }

    protected List<IptcBlock> parseAllBlocks(byte[] bytes, boolean strict) throws ImageReadException, IOException {
        ArrayList<IptcBlock> blocks = new ArrayList<IptcBlock>();
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);){
            byte[] idString = BinaryFunctions.readBytes("", bis, JpegConstants.PHOTOSHOP_IDENTIFICATION_STRING.size(), "App13 Segment missing identification string");
            if (!JpegConstants.PHOTOSHOP_IDENTIFICATION_STRING.equals(idString)) {
                throw new ImageReadException("Not a Photoshop App13 Segment");
            }
            while (true) {
                byte[] blockData;
                byte[] blockNameBytes;
                int imageResourceBlockSignature;
                try {
                    imageResourceBlockSignature = BinaryFunctions.read4Bytes("", bis, "Image Resource Block missing identification string", APP13_BYTE_ORDER);
                }
                catch (IOException ioEx) {
                    break;
                }
                if (imageResourceBlockSignature != JpegConstants.CONST_8BIM) {
                    throw new ImageReadException("Invalid Image Resource Block Signature");
                }
                int blockType = BinaryFunctions.read2Bytes("", bis, "Image Resource Block missing type", APP13_BYTE_ORDER);
                Debug.debug("blockType: " + blockType + " (0x" + Integer.toHexString(blockType) + ")");
                if (PHOTOSHOP_IGNORED_BLOCK_TYPE.contains(blockType)) {
                    Debug.debug("Skipping blockType: " + blockType + " (0x" + Integer.toHexString(blockType) + ")");
                    BinaryFunctions.searchQuad(JpegConstants.CONST_8BIM, bis);
                    continue;
                }
                byte blockNameLength = BinaryFunctions.readByte("Name length", bis, "Image Resource Block missing name length");
                if (blockNameLength > 0) {
                    Debug.debug("blockNameLength: " + blockNameLength + " (0x" + Integer.toHexString(blockNameLength) + ")");
                }
                if (blockNameLength == 0) {
                    BinaryFunctions.readByte("Block name bytes", bis, "Image Resource Block has invalid name");
                    blockNameBytes = new byte[]{};
                } else {
                    try {
                        blockNameBytes = BinaryFunctions.readBytes("", bis, blockNameLength, "Invalid Image Resource Block name");
                    }
                    catch (IOException ioEx) {
                        if (!strict) break;
                        throw ioEx;
                    }
                    if (blockNameLength % 2 == 0) {
                        BinaryFunctions.readByte("Padding byte", bis, "Image Resource Block missing padding byte");
                    }
                }
                int blockSize = BinaryFunctions.read4Bytes("", bis, "Image Resource Block missing size", APP13_BYTE_ORDER);
                Debug.debug("blockSize: " + blockSize + " (0x" + Integer.toHexString(blockSize) + ")");
                if (blockSize > bytes.length) {
                    throw new ImageReadException("Invalid Block Size : " + blockSize + " > " + bytes.length);
                }
                try {
                    blockData = BinaryFunctions.readBytes("", bis, blockSize, "Invalid Image Resource Block data");
                }
                catch (IOException ioEx) {
                    if (!strict) break;
                    throw ioEx;
                }
                blocks.add(new IptcBlock(blockType, blockNameBytes, blockData));
                if (blockSize % 2 == 0) continue;
                BinaryFunctions.readByte("Padding byte", bis, "Image Resource Block missing padding byte");
            }
            ArrayList<IptcBlock> arrayList = blocks;
            return arrayList;
        }
    }

    public byte[] writePhotoshopApp13Segment(PhotoshopApp13Data data) throws IOException, ImageWriteException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        BinaryOutputStream bos = new BinaryOutputStream(os);
        JpegConstants.PHOTOSHOP_IDENTIFICATION_STRING.writeTo(bos);
        List<IptcBlock> blocks = data.getRawBlocks();
        for (IptcBlock block : blocks) {
            byte[] blockData;
            bos.write4Bytes(JpegConstants.CONST_8BIM);
            if (block.getBlockType() < 0 || block.getBlockType() > 65535) {
                throw new ImageWriteException("Invalid IPTC block type.");
            }
            bos.write2Bytes(block.getBlockType());
            byte[] blockNameBytes = block.getBlockNameBytes();
            if (blockNameBytes.length > 255) {
                throw new ImageWriteException("IPTC block name is too long: " + blockNameBytes.length);
            }
            bos.write(blockNameBytes.length);
            bos.write(blockNameBytes);
            if (blockNameBytes.length % 2 == 0) {
                bos.write(0);
            }
            if ((blockData = block.getBlockData()).length > Short.MAX_VALUE) {
                throw new ImageWriteException("IPTC block data is too long: " + blockData.length);
            }
            bos.write4Bytes(blockData.length);
            bos.write(blockData);
            if (blockData.length % 2 != 1) continue;
            bos.write(0);
        }
        bos.flush();
        return os.toByteArray();
    }

    public byte[] writeIPTCBlock(List<IptcRecord> elements) throws ImageWriteException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (BinaryOutputStream bos = new BinaryOutputStream(baos, this.getByteOrder());){
            bos.write(28);
            bos.write(2);
            bos.write(IptcTypes.RECORD_VERSION.type);
            bos.write2Bytes(2);
            bos.write2Bytes(2);
            elements = new ArrayList<IptcRecord>(elements);
            Comparator comparator = (e1, e2) -> e2.iptcType.getType() - e1.iptcType.getType();
            Collections.sort(elements, comparator);
            for (IptcRecord element : elements) {
                if (element.iptcType == IptcTypes.RECORD_VERSION) continue;
                bos.write(28);
                bos.write(2);
                if (element.iptcType.getType() < 0 || element.iptcType.getType() > 255) {
                    throw new ImageWriteException("Invalid record type: " + element.iptcType.getType());
                }
                bos.write(element.iptcType.getType());
                byte[] recordData = element.getValue().getBytes(StandardCharsets.ISO_8859_1);
                if (!new String(recordData, StandardCharsets.ISO_8859_1).equals(element.getValue())) {
                    throw new ImageWriteException("Invalid record value, not ISO-8859-1");
                }
                bos.write2Bytes(recordData.length);
                bos.write(recordData);
            }
        }
        byte[] blockData = baos.toByteArray();
        return blockData;
    }
}

