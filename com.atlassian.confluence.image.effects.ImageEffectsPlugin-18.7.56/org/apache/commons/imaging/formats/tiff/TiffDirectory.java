/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.ByteConversions;
import org.apache.commons.imaging.common.RationalNumber;
import org.apache.commons.imaging.formats.tiff.JpegImageData;
import org.apache.commons.imaging.formats.tiff.TiffElement;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.TiffImageData;
import org.apache.commons.imaging.formats.tiff.TiffImageParser;
import org.apache.commons.imaging.formats.tiff.TiffRasterData;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoAscii;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoByte;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoBytes;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoDouble;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoDoubles;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoFloat;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoFloats;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoGpsText;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoLong;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoLongs;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoRational;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoRationals;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoSByte;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoSBytes;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoSLong;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoSLongs;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoSRational;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoSRationals;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoSShort;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoSShorts;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoShort;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoShortOrLong;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoShorts;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoXpString;

public class TiffDirectory
extends TiffElement {
    public final int type;
    public final List<TiffField> entries;
    public final long nextDirectoryOffset;
    private TiffImageData tiffImageData;
    private JpegImageData jpegImageData;
    private final ByteOrder headerByteOrder;

    public TiffDirectory(int type, List<TiffField> entries, long offset, long nextDirectoryOffset, ByteOrder byteOrder) {
        super(offset, 2 + entries.size() * 12 + 4);
        this.type = type;
        this.entries = Collections.unmodifiableList(entries);
        this.nextDirectoryOffset = nextDirectoryOffset;
        this.headerByteOrder = byteOrder;
    }

    public String description() {
        return TiffDirectory.description(this.type);
    }

    @Override
    public String getElementDescription() {
        long entryOffset = this.offset + 2L;
        StringBuilder result = new StringBuilder();
        for (TiffField entry : this.entries) {
            result.append(String.format("\t[%d]: %s (%d, 0x%x), %s, %d: %s%n", entryOffset, entry.getTagInfo().name, entry.getTag(), entry.getTag(), entry.getFieldType().getName(), entry.getBytesLength(), entry.getValueDescription()));
            entryOffset += 12L;
        }
        return result.toString();
    }

    public static String description(int type) {
        switch (type) {
            case -1: {
                return "Unknown";
            }
            case 0: {
                return "Root";
            }
            case 1: {
                return "Sub";
            }
            case 2: {
                return "Thumbnail";
            }
            case -2: {
                return "Exif";
            }
            case -3: {
                return "Gps";
            }
            case -4: {
                return "Interoperability";
            }
        }
        return "Bad Type";
    }

    public List<TiffField> getDirectoryEntries() {
        return new ArrayList<TiffField>(this.entries);
    }

    public void dump() {
        for (TiffField entry : this.entries) {
            entry.dump();
        }
    }

    public boolean hasJpegImageData() throws ImageReadException {
        return null != this.findField(TiffTagConstants.TIFF_TAG_JPEG_INTERCHANGE_FORMAT);
    }

    public boolean hasTiffImageData() throws ImageReadException {
        if (null != this.findField(TiffTagConstants.TIFF_TAG_TILE_OFFSETS)) {
            return true;
        }
        return null != this.findField(TiffTagConstants.TIFF_TAG_STRIP_OFFSETS);
    }

    public BufferedImage getTiffImage() throws ImageReadException, IOException {
        if (null == this.tiffImageData) {
            return null;
        }
        return new TiffImageParser().getBufferedImage(this, this.headerByteOrder, null);
    }

    public BufferedImage getTiffImage(Map<String, Object> params) throws ImageReadException, IOException {
        if (null == this.tiffImageData) {
            return null;
        }
        return new TiffImageParser().getBufferedImage(this, this.headerByteOrder, params);
    }

    public BufferedImage getTiffImage(ByteOrder byteOrder) throws ImageReadException, IOException {
        Map<String, Object> params = null;
        return this.getTiffImage(byteOrder, params);
    }

    public BufferedImage getTiffImage(ByteOrder byteOrder, Map<String, Object> params) throws ImageReadException, IOException {
        if (null == this.tiffImageData) {
            return null;
        }
        return new TiffImageParser().getBufferedImage(this, byteOrder, params);
    }

    public TiffField findField(TagInfo tag) throws ImageReadException {
        boolean failIfMissing = false;
        return this.findField(tag, false);
    }

    public TiffField findField(TagInfo tag, boolean failIfMissing) throws ImageReadException {
        if (this.entries == null) {
            return null;
        }
        for (TiffField field : this.entries) {
            if (field.getTag() != tag.tag) continue;
            return field;
        }
        if (failIfMissing) {
            throw new ImageReadException("Missing expected field: " + tag.getDescription());
        }
        return null;
    }

    public Object getFieldValue(TagInfo tag) throws ImageReadException {
        TiffField field = this.findField(tag);
        if (field == null) {
            return null;
        }
        return field.getValue();
    }

    public String getSingleFieldValue(TagInfoAscii tag) throws ImageReadException {
        String[] result = this.getFieldValue(tag, true);
        if (result.length != 1) {
            throw new ImageReadException("Field \"" + tag.name + "\" has incorrect length " + result.length);
        }
        return result[0];
    }

    public int getSingleFieldValue(TagInfoShortOrLong tag) throws ImageReadException {
        int[] result = this.getFieldValue(tag, true);
        if (result.length != 1) {
            throw new ImageReadException("Field \"" + tag.name + "\" has incorrect length " + result.length);
        }
        return result[0];
    }

    public byte getFieldValue(TagInfoByte tag) throws ImageReadException {
        TiffField field = this.findField(tag);
        if (field == null) {
            throw new ImageReadException("Required field \"" + tag.name + "\" is missing");
        }
        if (!tag.dataTypes.contains(field.getFieldType())) {
            throw new ImageReadException("Required field \"" + tag.name + "\" has incorrect type " + field.getFieldType().getName());
        }
        if (field.getCount() != 1L) {
            throw new ImageReadException("Field \"" + tag.name + "\" has wrong count " + field.getCount());
        }
        return field.getByteArrayValue()[0];
    }

    public byte[] getFieldValue(TagInfoBytes tag, boolean mustExist) throws ImageReadException {
        TiffField field = this.findField(tag);
        if (field == null) {
            if (mustExist) {
                throw new ImageReadException("Required field \"" + tag.name + "\" is missing");
            }
            return null;
        }
        if (!tag.dataTypes.contains(field.getFieldType())) {
            if (mustExist) {
                throw new ImageReadException("Required field \"" + tag.name + "\" has incorrect type " + field.getFieldType().getName());
            }
            return null;
        }
        return field.getByteArrayValue();
    }

    public String[] getFieldValue(TagInfoAscii tag, boolean mustExist) throws ImageReadException {
        TiffField field = this.findField(tag);
        if (field == null) {
            if (mustExist) {
                throw new ImageReadException("Required field \"" + tag.name + "\" is missing");
            }
            return null;
        }
        if (!tag.dataTypes.contains(field.getFieldType())) {
            if (mustExist) {
                throw new ImageReadException("Required field \"" + tag.name + "\" has incorrect type " + field.getFieldType().getName());
            }
            return null;
        }
        byte[] bytes = field.getByteArrayValue();
        return tag.getValue(field.getByteOrder(), bytes);
    }

    public short getFieldValue(TagInfoShort tag) throws ImageReadException {
        TiffField field = this.findField(tag);
        if (field == null) {
            throw new ImageReadException("Required field \"" + tag.name + "\" is missing");
        }
        if (!tag.dataTypes.contains(field.getFieldType())) {
            throw new ImageReadException("Required field \"" + tag.name + "\" has incorrect type " + field.getFieldType().getName());
        }
        if (field.getCount() != 1L) {
            throw new ImageReadException("Field \"" + tag.name + "\" has wrong count " + field.getCount());
        }
        byte[] bytes = field.getByteArrayValue();
        return tag.getValue(field.getByteOrder(), bytes);
    }

    public short[] getFieldValue(TagInfoShorts tag, boolean mustExist) throws ImageReadException {
        TiffField field = this.findField(tag);
        if (field == null) {
            if (mustExist) {
                throw new ImageReadException("Required field \"" + tag.name + "\" is missing");
            }
            return null;
        }
        if (!tag.dataTypes.contains(field.getFieldType())) {
            if (mustExist) {
                throw new ImageReadException("Required field \"" + tag.name + "\" has incorrect type " + field.getFieldType().getName());
            }
            return null;
        }
        byte[] bytes = field.getByteArrayValue();
        return tag.getValue(field.getByteOrder(), bytes);
    }

    public int getFieldValue(TagInfoLong tag) throws ImageReadException {
        TiffField field = this.findField(tag);
        if (field == null) {
            throw new ImageReadException("Required field \"" + tag.name + "\" is missing");
        }
        if (!tag.dataTypes.contains(field.getFieldType())) {
            throw new ImageReadException("Required field \"" + tag.name + "\" has incorrect type " + field.getFieldType().getName());
        }
        if (field.getCount() != 1L) {
            throw new ImageReadException("Field \"" + tag.name + "\" has wrong count " + field.getCount());
        }
        byte[] bytes = field.getByteArrayValue();
        return tag.getValue(field.getByteOrder(), bytes);
    }

    public int[] getFieldValue(TagInfoLongs tag, boolean mustExist) throws ImageReadException {
        TiffField field = this.findField(tag);
        if (field == null) {
            if (mustExist) {
                throw new ImageReadException("Required field \"" + tag.name + "\" is missing");
            }
            return null;
        }
        if (!tag.dataTypes.contains(field.getFieldType())) {
            if (mustExist) {
                throw new ImageReadException("Required field \"" + tag.name + "\" has incorrect type " + field.getFieldType().getName());
            }
            return null;
        }
        byte[] bytes = field.getByteArrayValue();
        return tag.getValue(field.getByteOrder(), bytes);
    }

    public int[] getFieldValue(TagInfoShortOrLong tag, boolean mustExist) throws ImageReadException {
        TiffField field = this.findField(tag);
        if (field == null) {
            if (mustExist) {
                throw new ImageReadException("Required field \"" + tag.name + "\" is missing");
            }
            return null;
        }
        if (!tag.dataTypes.contains(field.getFieldType())) {
            if (mustExist) {
                throw new ImageReadException("Required field \"" + tag.name + "\" has incorrect type " + field.getFieldType().getName());
            }
            return null;
        }
        byte[] bytes = field.getByteArrayValue();
        if (field.getFieldType() == FieldType.SHORT) {
            return ByteConversions.toUInt16s(bytes, field.getByteOrder());
        }
        return ByteConversions.toInts(bytes, field.getByteOrder());
    }

    public RationalNumber getFieldValue(TagInfoRational tag) throws ImageReadException {
        TiffField field = this.findField(tag);
        if (field == null) {
            throw new ImageReadException("Required field \"" + tag.name + "\" is missing");
        }
        if (!tag.dataTypes.contains(field.getFieldType())) {
            throw new ImageReadException("Required field \"" + tag.name + "\" has incorrect type " + field.getFieldType().getName());
        }
        if (field.getCount() != 1L) {
            throw new ImageReadException("Field \"" + tag.name + "\" has wrong count " + field.getCount());
        }
        byte[] bytes = field.getByteArrayValue();
        return tag.getValue(field.getByteOrder(), bytes);
    }

    public RationalNumber[] getFieldValue(TagInfoRationals tag, boolean mustExist) throws ImageReadException {
        TiffField field = this.findField(tag);
        if (field == null) {
            if (mustExist) {
                throw new ImageReadException("Required field \"" + tag.name + "\" is missing");
            }
            return null;
        }
        if (!tag.dataTypes.contains(field.getFieldType())) {
            if (mustExist) {
                throw new ImageReadException("Required field \"" + tag.name + "\" has incorrect type " + field.getFieldType().getName());
            }
            return null;
        }
        byte[] bytes = field.getByteArrayValue();
        return tag.getValue(field.getByteOrder(), bytes);
    }

    public byte getFieldValue(TagInfoSByte tag) throws ImageReadException {
        TiffField field = this.findField(tag);
        if (field == null) {
            throw new ImageReadException("Required field \"" + tag.name + "\" is missing");
        }
        if (!tag.dataTypes.contains(field.getFieldType())) {
            throw new ImageReadException("Required field \"" + tag.name + "\" has incorrect type " + field.getFieldType().getName());
        }
        if (field.getCount() != 1L) {
            throw new ImageReadException("Field \"" + tag.name + "\" has wrong count " + field.getCount());
        }
        return field.getByteArrayValue()[0];
    }

    public byte[] getFieldValue(TagInfoSBytes tag, boolean mustExist) throws ImageReadException {
        TiffField field = this.findField(tag);
        if (field == null) {
            if (mustExist) {
                throw new ImageReadException("Required field \"" + tag.name + "\" is missing");
            }
            return null;
        }
        if (!tag.dataTypes.contains(field.getFieldType())) {
            if (mustExist) {
                throw new ImageReadException("Required field \"" + tag.name + "\" has incorrect type " + field.getFieldType().getName());
            }
            return null;
        }
        return field.getByteArrayValue();
    }

    public short getFieldValue(TagInfoSShort tag) throws ImageReadException {
        TiffField field = this.findField(tag);
        if (field == null) {
            throw new ImageReadException("Required field \"" + tag.name + "\" is missing");
        }
        if (!tag.dataTypes.contains(field.getFieldType())) {
            throw new ImageReadException("Required field \"" + tag.name + "\" has incorrect type " + field.getFieldType().getName());
        }
        if (field.getCount() != 1L) {
            throw new ImageReadException("Field \"" + tag.name + "\" has wrong count " + field.getCount());
        }
        byte[] bytes = field.getByteArrayValue();
        return tag.getValue(field.getByteOrder(), bytes);
    }

    public short[] getFieldValue(TagInfoSShorts tag, boolean mustExist) throws ImageReadException {
        TiffField field = this.findField(tag);
        if (field == null) {
            if (mustExist) {
                throw new ImageReadException("Required field \"" + tag.name + "\" is missing");
            }
            return null;
        }
        if (!tag.dataTypes.contains(field.getFieldType())) {
            if (mustExist) {
                throw new ImageReadException("Required field \"" + tag.name + "\" has incorrect type " + field.getFieldType().getName());
            }
            return null;
        }
        byte[] bytes = field.getByteArrayValue();
        return tag.getValue(field.getByteOrder(), bytes);
    }

    public int getFieldValue(TagInfoSLong tag) throws ImageReadException {
        TiffField field = this.findField(tag);
        if (field == null) {
            throw new ImageReadException("Required field \"" + tag.name + "\" is missing");
        }
        if (!tag.dataTypes.contains(field.getFieldType())) {
            throw new ImageReadException("Required field \"" + tag.name + "\" has incorrect type " + field.getFieldType().getName());
        }
        if (field.getCount() != 1L) {
            throw new ImageReadException("Field \"" + tag.name + "\" has wrong count " + field.getCount());
        }
        byte[] bytes = field.getByteArrayValue();
        return tag.getValue(field.getByteOrder(), bytes);
    }

    public int[] getFieldValue(TagInfoSLongs tag, boolean mustExist) throws ImageReadException {
        TiffField field = this.findField(tag);
        if (field == null) {
            if (mustExist) {
                throw new ImageReadException("Required field \"" + tag.name + "\" is missing");
            }
            return null;
        }
        if (!tag.dataTypes.contains(field.getFieldType())) {
            if (mustExist) {
                throw new ImageReadException("Required field \"" + tag.name + "\" has incorrect type " + field.getFieldType().getName());
            }
            return null;
        }
        byte[] bytes = field.getByteArrayValue();
        return tag.getValue(field.getByteOrder(), bytes);
    }

    public RationalNumber getFieldValue(TagInfoSRational tag) throws ImageReadException {
        TiffField field = this.findField(tag);
        if (field == null) {
            throw new ImageReadException("Required field \"" + tag.name + "\" is missing");
        }
        if (!tag.dataTypes.contains(field.getFieldType())) {
            throw new ImageReadException("Required field \"" + tag.name + "\" has incorrect type " + field.getFieldType().getName());
        }
        if (field.getCount() != 1L) {
            throw new ImageReadException("Field \"" + tag.name + "\" has wrong count " + field.getCount());
        }
        byte[] bytes = field.getByteArrayValue();
        return tag.getValue(field.getByteOrder(), bytes);
    }

    public RationalNumber[] getFieldValue(TagInfoSRationals tag, boolean mustExist) throws ImageReadException {
        TiffField field = this.findField(tag);
        if (field == null) {
            if (mustExist) {
                throw new ImageReadException("Required field \"" + tag.name + "\" is missing");
            }
            return null;
        }
        if (!tag.dataTypes.contains(field.getFieldType())) {
            if (mustExist) {
                throw new ImageReadException("Required field \"" + tag.name + "\" has incorrect type " + field.getFieldType().getName());
            }
            return null;
        }
        byte[] bytes = field.getByteArrayValue();
        return tag.getValue(field.getByteOrder(), bytes);
    }

    public float getFieldValue(TagInfoFloat tag) throws ImageReadException {
        TiffField field = this.findField(tag);
        if (field == null) {
            throw new ImageReadException("Required field \"" + tag.name + "\" is missing");
        }
        if (!tag.dataTypes.contains(field.getFieldType())) {
            throw new ImageReadException("Required field \"" + tag.name + "\" has incorrect type " + field.getFieldType().getName());
        }
        if (field.getCount() != 1L) {
            throw new ImageReadException("Field \"" + tag.name + "\" has wrong count " + field.getCount());
        }
        byte[] bytes = field.getByteArrayValue();
        return tag.getValue(field.getByteOrder(), bytes);
    }

    public float[] getFieldValue(TagInfoFloats tag, boolean mustExist) throws ImageReadException {
        TiffField field = this.findField(tag);
        if (field == null) {
            if (mustExist) {
                throw new ImageReadException("Required field \"" + tag.name + "\" is missing");
            }
            return null;
        }
        if (!tag.dataTypes.contains(field.getFieldType())) {
            if (mustExist) {
                throw new ImageReadException("Required field \"" + tag.name + "\" has incorrect type " + field.getFieldType().getName());
            }
            return null;
        }
        byte[] bytes = field.getByteArrayValue();
        return tag.getValue(field.getByteOrder(), bytes);
    }

    public double getFieldValue(TagInfoDouble tag) throws ImageReadException {
        TiffField field = this.findField(tag);
        if (field == null) {
            throw new ImageReadException("Required field \"" + tag.name + "\" is missing");
        }
        if (!tag.dataTypes.contains(field.getFieldType())) {
            throw new ImageReadException("Required field \"" + tag.name + "\" has incorrect type " + field.getFieldType().getName());
        }
        if (field.getCount() != 1L) {
            throw new ImageReadException("Field \"" + tag.name + "\" has wrong count " + field.getCount());
        }
        byte[] bytes = field.getByteArrayValue();
        return tag.getValue(field.getByteOrder(), bytes);
    }

    public double[] getFieldValue(TagInfoDoubles tag, boolean mustExist) throws ImageReadException {
        TiffField field = this.findField(tag);
        if (field == null) {
            if (mustExist) {
                throw new ImageReadException("Required field \"" + tag.name + "\" is missing");
            }
            return null;
        }
        if (!tag.dataTypes.contains(field.getFieldType())) {
            if (mustExist) {
                throw new ImageReadException("Required field \"" + tag.name + "\" has incorrect type " + field.getFieldType().getName());
            }
            return null;
        }
        byte[] bytes = field.getByteArrayValue();
        return tag.getValue(field.getByteOrder(), bytes);
    }

    public String getFieldValue(TagInfoGpsText tag, boolean mustExist) throws ImageReadException {
        TiffField field = this.findField(tag);
        if (field == null) {
            if (mustExist) {
                throw new ImageReadException("Required field \"" + tag.name + "\" is missing");
            }
            return null;
        }
        return tag.getValue(field);
    }

    public String getFieldValue(TagInfoXpString tag, boolean mustExist) throws ImageReadException {
        TiffField field = this.findField(tag);
        if (field == null) {
            if (mustExist) {
                throw new ImageReadException("Required field \"" + tag.name + "\" is missing");
            }
            return null;
        }
        return tag.getValue(field);
    }

    private List<ImageDataElement> getRawImageDataElements(TiffField offsetsField, TiffField byteCountsField) throws ImageReadException {
        int[] byteCounts;
        int[] offsets = offsetsField.getIntArrayValue();
        if (offsets.length != (byteCounts = byteCountsField.getIntArrayValue()).length) {
            throw new ImageReadException("offsets.length(" + offsets.length + ") != byteCounts.length(" + byteCounts.length + ")");
        }
        ArrayList<ImageDataElement> result = new ArrayList<ImageDataElement>(offsets.length);
        for (int i = 0; i < offsets.length; ++i) {
            result.add(new ImageDataElement(offsets[i], byteCounts[i]));
        }
        return result;
    }

    public List<ImageDataElement> getTiffRawImageDataElements() throws ImageReadException {
        TiffField tileOffsets = this.findField(TiffTagConstants.TIFF_TAG_TILE_OFFSETS);
        TiffField tileByteCounts = this.findField(TiffTagConstants.TIFF_TAG_TILE_BYTE_COUNTS);
        TiffField stripOffsets = this.findField(TiffTagConstants.TIFF_TAG_STRIP_OFFSETS);
        TiffField stripByteCounts = this.findField(TiffTagConstants.TIFF_TAG_STRIP_BYTE_COUNTS);
        if (tileOffsets != null && tileByteCounts != null) {
            return this.getRawImageDataElements(tileOffsets, tileByteCounts);
        }
        if (stripOffsets != null && stripByteCounts != null) {
            return this.getRawImageDataElements(stripOffsets, stripByteCounts);
        }
        throw new ImageReadException("Couldn't find image data.");
    }

    public boolean imageDataInStrips() throws ImageReadException {
        TiffField tileOffsets = this.findField(TiffTagConstants.TIFF_TAG_TILE_OFFSETS);
        TiffField tileByteCounts = this.findField(TiffTagConstants.TIFF_TAG_TILE_BYTE_COUNTS);
        TiffField stripOffsets = this.findField(TiffTagConstants.TIFF_TAG_STRIP_OFFSETS);
        TiffField stripByteCounts = this.findField(TiffTagConstants.TIFF_TAG_STRIP_BYTE_COUNTS);
        if (tileOffsets != null && tileByteCounts != null) {
            return false;
        }
        if (stripOffsets != null && stripByteCounts != null) {
            return true;
        }
        throw new ImageReadException("Couldn't find image data.");
    }

    public ImageDataElement getJpegRawImageDataElement() throws ImageReadException {
        TiffField jpegInterchangeFormat = this.findField(TiffTagConstants.TIFF_TAG_JPEG_INTERCHANGE_FORMAT);
        TiffField jpegInterchangeFormatLength = this.findField(TiffTagConstants.TIFF_TAG_JPEG_INTERCHANGE_FORMAT_LENGTH);
        if (jpegInterchangeFormat != null && jpegInterchangeFormatLength != null) {
            int offSet = jpegInterchangeFormat.getIntArrayValue()[0];
            int byteCount = jpegInterchangeFormatLength.getIntArrayValue()[0];
            return new ImageDataElement(offSet, byteCount);
        }
        throw new ImageReadException("Couldn't find image data.");
    }

    public void setTiffImageData(TiffImageData rawImageData) {
        this.tiffImageData = rawImageData;
    }

    public TiffImageData getTiffImageData() {
        return this.tiffImageData;
    }

    public void setJpegImageData(JpegImageData value) {
        this.jpegImageData = value;
    }

    public JpegImageData getJpegImageData() {
        return this.jpegImageData;
    }

    public TiffRasterData getFloatingPointRasterData(Map<String, Object> params) throws ImageReadException, IOException {
        TiffImageParser parser = new TiffImageParser();
        return parser.getFloatingPointRasterData(this, this.headerByteOrder, params);
    }

    public boolean hasTiffFloatingPointRasterData() throws ImageReadException {
        if (this.hasTiffImageData()) {
            short[] sSampleFmt = this.getFieldValue(TiffTagConstants.TIFF_TAG_SAMPLE_FORMAT, false);
            return sSampleFmt != null && sSampleFmt.length > 0 && sSampleFmt[0] == 3;
        }
        return false;
    }

    public static final class ImageDataElement
    extends TiffElement {
        public ImageDataElement(long offset, int length) {
            super(offset, length);
        }

        @Override
        public String getElementDescription() {
            return "ImageDataElement";
        }
    }
}

