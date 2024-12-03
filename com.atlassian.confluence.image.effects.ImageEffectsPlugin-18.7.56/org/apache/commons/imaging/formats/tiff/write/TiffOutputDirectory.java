/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.write;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.common.BinaryOutputStream;
import org.apache.commons.imaging.common.RationalNumber;
import org.apache.commons.imaging.formats.tiff.JpegImageData;
import org.apache.commons.imaging.formats.tiff.TiffDirectory;
import org.apache.commons.imaging.formats.tiff.TiffElement;
import org.apache.commons.imaging.formats.tiff.TiffImageData;
import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryType;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoAscii;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoAsciiOrByte;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoAsciiOrRational;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoByte;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoByteOrShort;
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
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoShortOrLongOrRational;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoShortOrRational;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoShorts;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoXpString;
import org.apache.commons.imaging.formats.tiff.write.ImageDataOffsets;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputField;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputItem;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSummary;

public final class TiffOutputDirectory
extends TiffOutputItem {
    public final int type;
    private final List<TiffOutputField> fields = new ArrayList<TiffOutputField>();
    private final ByteOrder byteOrder;
    private TiffOutputDirectory nextDirectory;
    public static final Comparator<TiffOutputDirectory> COMPARATOR = (o1, o2) -> {
        if (o1.type < o2.type) {
            return -1;
        }
        if (o1.type > o2.type) {
            return 1;
        }
        return 0;
    };
    private JpegImageData jpegImageData;
    private TiffImageData tiffImageData;

    public void setNextDirectory(TiffOutputDirectory nextDirectory) {
        this.nextDirectory = nextDirectory;
    }

    public TiffOutputDirectory(int type, ByteOrder byteOrder) {
        this.type = type;
        this.byteOrder = byteOrder;
    }

    public void add(TagInfoByte tagInfo, byte value) throws ImageWriteException {
        if (tagInfo.length != 1) {
            throw new ImageWriteException("Tag expects " + tagInfo.length + " value(s), not 1");
        }
        byte[] bytes = tagInfo.encodeValue(this.byteOrder, value);
        TiffOutputField tiffOutputField = new TiffOutputField(tagInfo.tag, tagInfo, FieldType.BYTE, bytes.length, bytes);
        this.add(tiffOutputField);
    }

    public void add(TagInfoBytes tagInfo, byte ... values) throws ImageWriteException {
        if (tagInfo.length > 0 && tagInfo.length != values.length) {
            throw new ImageWriteException("Tag expects " + tagInfo.length + " value(s), not " + values.length);
        }
        byte[] bytes = tagInfo.encodeValue(this.byteOrder, values);
        TiffOutputField tiffOutputField = new TiffOutputField(tagInfo.tag, tagInfo, FieldType.BYTE, values.length, bytes);
        this.add(tiffOutputField);
    }

    public void add(TagInfoAscii tagInfo, String ... values) throws ImageWriteException {
        byte[] bytes = tagInfo.encodeValue(this.byteOrder, values);
        if (tagInfo.length > 0 && tagInfo.length != bytes.length) {
            throw new ImageWriteException("Tag expects " + tagInfo.length + " byte(s), not " + values.length);
        }
        TiffOutputField tiffOutputField = new TiffOutputField(tagInfo.tag, tagInfo, FieldType.ASCII, bytes.length, bytes);
        this.add(tiffOutputField);
    }

    public void add(TagInfoShort tagInfo, short value) throws ImageWriteException {
        if (tagInfo.length != 1) {
            throw new ImageWriteException("Tag expects " + tagInfo.length + " value(s), not 1");
        }
        byte[] bytes = tagInfo.encodeValue(this.byteOrder, value);
        TiffOutputField tiffOutputField = new TiffOutputField(tagInfo.tag, tagInfo, FieldType.SHORT, 1, bytes);
        this.add(tiffOutputField);
    }

    public void add(TagInfoShorts tagInfo, short ... values) throws ImageWriteException {
        if (tagInfo.length > 0 && tagInfo.length != values.length) {
            throw new ImageWriteException("Tag expects " + tagInfo.length + " value(s), not " + values.length);
        }
        byte[] bytes = tagInfo.encodeValue(this.byteOrder, values);
        TiffOutputField tiffOutputField = new TiffOutputField(tagInfo.tag, tagInfo, FieldType.SHORT, values.length, bytes);
        this.add(tiffOutputField);
    }

    public void add(TagInfoLong tagInfo, int value) throws ImageWriteException {
        if (tagInfo.length != 1) {
            throw new ImageWriteException("Tag expects " + tagInfo.length + " value(s), not 1");
        }
        byte[] bytes = tagInfo.encodeValue(this.byteOrder, value);
        TiffOutputField tiffOutputField = new TiffOutputField(tagInfo.tag, tagInfo, FieldType.LONG, 1, bytes);
        this.add(tiffOutputField);
    }

    public void add(TagInfoLongs tagInfo, int ... values) throws ImageWriteException {
        if (tagInfo.length > 0 && tagInfo.length != values.length) {
            throw new ImageWriteException("Tag expects " + tagInfo.length + " value(s), not " + values.length);
        }
        byte[] bytes = tagInfo.encodeValue(this.byteOrder, values);
        TiffOutputField tiffOutputField = new TiffOutputField(tagInfo.tag, tagInfo, FieldType.LONG, values.length, bytes);
        this.add(tiffOutputField);
    }

    public void add(TagInfoRational tagInfo, RationalNumber value) throws ImageWriteException {
        if (tagInfo.length != 1) {
            throw new ImageWriteException("Tag expects " + tagInfo.length + " value(s), not 1");
        }
        byte[] bytes = tagInfo.encodeValue(this.byteOrder, value);
        TiffOutputField tiffOutputField = new TiffOutputField(tagInfo.tag, tagInfo, FieldType.RATIONAL, 1, bytes);
        this.add(tiffOutputField);
    }

    public void add(TagInfoRationals tagInfo, RationalNumber ... values) throws ImageWriteException {
        if (tagInfo.length > 0 && tagInfo.length != values.length) {
            throw new ImageWriteException("Tag expects " + tagInfo.length + " value(s), not " + values.length);
        }
        byte[] bytes = tagInfo.encodeValue(this.byteOrder, values);
        TiffOutputField tiffOutputField = new TiffOutputField(tagInfo.tag, tagInfo, FieldType.RATIONAL, values.length, bytes);
        this.add(tiffOutputField);
    }

    public void add(TagInfoSByte tagInfo, byte value) throws ImageWriteException {
        if (tagInfo.length != 1) {
            throw new ImageWriteException("Tag expects " + tagInfo.length + " value(s), not 1");
        }
        byte[] bytes = tagInfo.encodeValue(this.byteOrder, value);
        TiffOutputField tiffOutputField = new TiffOutputField(tagInfo.tag, tagInfo, FieldType.SBYTE, 1, bytes);
        this.add(tiffOutputField);
    }

    public void add(TagInfoSBytes tagInfo, byte ... values) throws ImageWriteException {
        if (tagInfo.length > 0 && tagInfo.length != values.length) {
            throw new ImageWriteException("Tag expects " + tagInfo.length + " value(s), not " + values.length);
        }
        byte[] bytes = tagInfo.encodeValue(this.byteOrder, values);
        TiffOutputField tiffOutputField = new TiffOutputField(tagInfo.tag, tagInfo, FieldType.SBYTE, values.length, bytes);
        this.add(tiffOutputField);
    }

    public void add(TagInfoSShort tagInfo, short value) throws ImageWriteException {
        if (tagInfo.length != 1) {
            throw new ImageWriteException("Tag expects " + tagInfo.length + " value(s), not 1");
        }
        byte[] bytes = tagInfo.encodeValue(this.byteOrder, value);
        TiffOutputField tiffOutputField = new TiffOutputField(tagInfo.tag, tagInfo, FieldType.SSHORT, 1, bytes);
        this.add(tiffOutputField);
    }

    public void add(TagInfoSShorts tagInfo, short ... values) throws ImageWriteException {
        if (tagInfo.length > 0 && tagInfo.length != values.length) {
            throw new ImageWriteException("Tag expects " + tagInfo.length + " value(s), not " + values.length);
        }
        byte[] bytes = tagInfo.encodeValue(this.byteOrder, values);
        TiffOutputField tiffOutputField = new TiffOutputField(tagInfo.tag, tagInfo, FieldType.SSHORT, values.length, bytes);
        this.add(tiffOutputField);
    }

    public void add(TagInfoSLong tagInfo, int value) throws ImageWriteException {
        if (tagInfo.length != 1) {
            throw new ImageWriteException("Tag expects " + tagInfo.length + " value(s), not 1");
        }
        byte[] bytes = tagInfo.encodeValue(this.byteOrder, value);
        TiffOutputField tiffOutputField = new TiffOutputField(tagInfo.tag, tagInfo, FieldType.SLONG, 1, bytes);
        this.add(tiffOutputField);
    }

    public void add(TagInfoSLongs tagInfo, int ... values) throws ImageWriteException {
        if (tagInfo.length > 0 && tagInfo.length != values.length) {
            throw new ImageWriteException("Tag expects " + tagInfo.length + " value(s), not " + values.length);
        }
        byte[] bytes = tagInfo.encodeValue(this.byteOrder, values);
        TiffOutputField tiffOutputField = new TiffOutputField(tagInfo.tag, tagInfo, FieldType.SLONG, values.length, bytes);
        this.add(tiffOutputField);
    }

    public void add(TagInfoSRational tagInfo, RationalNumber value) throws ImageWriteException {
        if (tagInfo.length != 1) {
            throw new ImageWriteException("Tag expects " + tagInfo.length + " value(s), not 1");
        }
        byte[] bytes = tagInfo.encodeValue(this.byteOrder, value);
        TiffOutputField tiffOutputField = new TiffOutputField(tagInfo.tag, tagInfo, FieldType.SRATIONAL, 1, bytes);
        this.add(tiffOutputField);
    }

    public void add(TagInfoSRationals tagInfo, RationalNumber ... values) throws ImageWriteException {
        if (tagInfo.length > 0 && tagInfo.length != values.length) {
            throw new ImageWriteException("Tag expects " + tagInfo.length + " value(s), not " + values.length);
        }
        byte[] bytes = tagInfo.encodeValue(this.byteOrder, values);
        TiffOutputField tiffOutputField = new TiffOutputField(tagInfo.tag, tagInfo, FieldType.SRATIONAL, values.length, bytes);
        this.add(tiffOutputField);
    }

    public void add(TagInfoFloat tagInfo, float value) throws ImageWriteException {
        if (tagInfo.length != 1) {
            throw new ImageWriteException("Tag expects " + tagInfo.length + " value(s), not 1");
        }
        byte[] bytes = tagInfo.encodeValue(this.byteOrder, value);
        TiffOutputField tiffOutputField = new TiffOutputField(tagInfo.tag, tagInfo, FieldType.FLOAT, 1, bytes);
        this.add(tiffOutputField);
    }

    public void add(TagInfoFloats tagInfo, float ... values) throws ImageWriteException {
        if (tagInfo.length > 0 && tagInfo.length != values.length) {
            throw new ImageWriteException("Tag expects " + tagInfo.length + " value(s), not " + values.length);
        }
        byte[] bytes = tagInfo.encodeValue(this.byteOrder, values);
        TiffOutputField tiffOutputField = new TiffOutputField(tagInfo.tag, tagInfo, FieldType.FLOAT, values.length, bytes);
        this.add(tiffOutputField);
    }

    public void add(TagInfoDouble tagInfo, double value) throws ImageWriteException {
        if (tagInfo.length != 1) {
            throw new ImageWriteException("Tag expects " + tagInfo.length + " value(s), not 1");
        }
        byte[] bytes = tagInfo.encodeValue(this.byteOrder, value);
        TiffOutputField tiffOutputField = new TiffOutputField(tagInfo.tag, tagInfo, FieldType.DOUBLE, 1, bytes);
        this.add(tiffOutputField);
    }

    public void add(TagInfoDoubles tagInfo, double ... values) throws ImageWriteException {
        if (tagInfo.length > 0 && tagInfo.length != values.length) {
            throw new ImageWriteException("Tag expects " + tagInfo.length + " value(s), not " + values.length);
        }
        byte[] bytes = tagInfo.encodeValue(this.byteOrder, values);
        TiffOutputField tiffOutputField = new TiffOutputField(tagInfo.tag, tagInfo, FieldType.DOUBLE, values.length, bytes);
        this.add(tiffOutputField);
    }

    public void add(TagInfoByteOrShort tagInfo, byte ... values) throws ImageWriteException {
        if (tagInfo.length > 0 && tagInfo.length != values.length) {
            throw new ImageWriteException("Tag expects " + tagInfo.length + " value(s), not " + values.length);
        }
        byte[] bytes = tagInfo.encodeValue(this.byteOrder, values);
        TiffOutputField tiffOutputField = new TiffOutputField(tagInfo.tag, tagInfo, FieldType.BYTE, values.length, bytes);
        this.add(tiffOutputField);
    }

    public void add(TagInfoByteOrShort tagInfo, short ... values) throws ImageWriteException {
        if (tagInfo.length > 0 && tagInfo.length != values.length) {
            throw new ImageWriteException("Tag expects " + tagInfo.length + " value(s), not " + values.length);
        }
        byte[] bytes = tagInfo.encodeValue(this.byteOrder, values);
        TiffOutputField tiffOutputField = new TiffOutputField(tagInfo.tag, tagInfo, FieldType.SHORT, values.length, bytes);
        this.add(tiffOutputField);
    }

    public void add(TagInfoShortOrLong tagInfo, short ... values) throws ImageWriteException {
        if (tagInfo.length > 0 && tagInfo.length != values.length) {
            throw new ImageWriteException("Tag expects " + tagInfo.length + " value(s), not " + values.length);
        }
        byte[] bytes = tagInfo.encodeValue(this.byteOrder, values);
        TiffOutputField tiffOutputField = new TiffOutputField(tagInfo.tag, tagInfo, FieldType.SHORT, values.length, bytes);
        this.add(tiffOutputField);
    }

    public void add(TagInfoShortOrLong tagInfo, int ... values) throws ImageWriteException {
        if (tagInfo.length > 0 && tagInfo.length != values.length) {
            throw new ImageWriteException("Tag expects " + tagInfo.length + " value(s), not " + values.length);
        }
        byte[] bytes = tagInfo.encodeValue(this.byteOrder, values);
        TiffOutputField tiffOutputField = new TiffOutputField(tagInfo.tag, tagInfo, FieldType.LONG, values.length, bytes);
        this.add(tiffOutputField);
    }

    public void add(TagInfoShortOrLongOrRational tagInfo, short ... values) throws ImageWriteException {
        if (tagInfo.length > 0 && tagInfo.length != values.length) {
            throw new ImageWriteException("Tag expects " + tagInfo.length + " value(s), not " + values.length);
        }
        byte[] bytes = tagInfo.encodeValue(this.byteOrder, values);
        TiffOutputField tiffOutputField = new TiffOutputField(tagInfo.tag, tagInfo, FieldType.SHORT, values.length, bytes);
        this.add(tiffOutputField);
    }

    public void add(TagInfoShortOrLongOrRational tagInfo, int ... values) throws ImageWriteException {
        if (tagInfo.length > 0 && tagInfo.length != values.length) {
            throw new ImageWriteException("Tag expects " + tagInfo.length + " value(s), not " + values.length);
        }
        byte[] bytes = tagInfo.encodeValue(this.byteOrder, values);
        TiffOutputField tiffOutputField = new TiffOutputField(tagInfo.tag, tagInfo, FieldType.LONG, values.length, bytes);
        this.add(tiffOutputField);
    }

    public void add(TagInfoShortOrLongOrRational tagInfo, RationalNumber ... values) throws ImageWriteException {
        if (tagInfo.length > 0 && tagInfo.length != values.length) {
            throw new ImageWriteException("Tag expects " + tagInfo.length + " value(s), not " + values.length);
        }
        byte[] bytes = tagInfo.encodeValue(this.byteOrder, values);
        TiffOutputField tiffOutputField = new TiffOutputField(tagInfo.tag, tagInfo, FieldType.RATIONAL, values.length, bytes);
        this.add(tiffOutputField);
    }

    public void add(TagInfoShortOrRational tagInfo, short ... values) throws ImageWriteException {
        if (tagInfo.length > 0 && tagInfo.length != values.length) {
            throw new ImageWriteException("Tag expects " + tagInfo.length + " value(s), not " + values.length);
        }
        byte[] bytes = tagInfo.encodeValue(this.byteOrder, values);
        TiffOutputField tiffOutputField = new TiffOutputField(tagInfo.tag, tagInfo, FieldType.SHORT, values.length, bytes);
        this.add(tiffOutputField);
    }

    public void add(TagInfoShortOrRational tagInfo, RationalNumber ... values) throws ImageWriteException {
        if (tagInfo.length > 0 && tagInfo.length != values.length) {
            throw new ImageWriteException("Tag expects " + tagInfo.length + " value(s), not " + values.length);
        }
        byte[] bytes = tagInfo.encodeValue(this.byteOrder, values);
        TiffOutputField tiffOutputField = new TiffOutputField(tagInfo.tag, tagInfo, FieldType.RATIONAL, values.length, bytes);
        this.add(tiffOutputField);
    }

    public void add(TagInfoGpsText tagInfo, String value) throws ImageWriteException {
        byte[] bytes = tagInfo.encodeValue(FieldType.UNDEFINED, value, this.byteOrder);
        TiffOutputField tiffOutputField = new TiffOutputField(tagInfo.tag, tagInfo, (FieldType)tagInfo.dataTypes.get(0), bytes.length, bytes);
        this.add(tiffOutputField);
    }

    public void add(TagInfoXpString tagInfo, String value) throws ImageWriteException {
        byte[] bytes = tagInfo.encodeValue(FieldType.BYTE, value, this.byteOrder);
        TiffOutputField tiffOutputField = new TiffOutputField(tagInfo.tag, tagInfo, FieldType.BYTE, bytes.length, bytes);
        this.add(tiffOutputField);
    }

    public void add(TagInfoAsciiOrByte tagInfo, String ... values) throws ImageWriteException {
        byte[] bytes = tagInfo.encodeValue(FieldType.ASCII, values, this.byteOrder);
        if (tagInfo.length > 0 && tagInfo.length != bytes.length) {
            throw new ImageWriteException("Tag expects " + tagInfo.length + " byte(s), not " + values.length);
        }
        TiffOutputField tiffOutputField = new TiffOutputField(tagInfo.tag, tagInfo, FieldType.ASCII, bytes.length, bytes);
        this.add(tiffOutputField);
    }

    public void add(TagInfoAsciiOrRational tagInfo, String ... values) throws ImageWriteException {
        byte[] bytes = tagInfo.encodeValue(FieldType.ASCII, values, this.byteOrder);
        if (tagInfo.length > 0 && tagInfo.length != bytes.length) {
            throw new ImageWriteException("Tag expects " + tagInfo.length + " byte(s), not " + values.length);
        }
        TiffOutputField tiffOutputField = new TiffOutputField(tagInfo.tag, tagInfo, FieldType.ASCII, bytes.length, bytes);
        this.add(tiffOutputField);
    }

    public void add(TagInfoAsciiOrRational tagInfo, RationalNumber ... values) throws ImageWriteException {
        if (tagInfo.length > 0 && tagInfo.length != values.length) {
            throw new ImageWriteException("Tag expects " + tagInfo.length + " value(s), not " + values.length);
        }
        byte[] bytes = tagInfo.encodeValue(FieldType.RATIONAL, values, this.byteOrder);
        TiffOutputField tiffOutputField = new TiffOutputField(tagInfo.tag, tagInfo, FieldType.RATIONAL, bytes.length, bytes);
        this.add(tiffOutputField);
    }

    public void add(TiffOutputField field) {
        this.fields.add(field);
    }

    public List<TiffOutputField> getFields() {
        return new ArrayList<TiffOutputField>(this.fields);
    }

    public void removeField(TagInfo tagInfo) {
        this.removeField(tagInfo.tag);
    }

    public void removeField(int tag) {
        ArrayList<TiffOutputField> matches = new ArrayList<TiffOutputField>();
        for (TiffOutputField field : this.fields) {
            if (field.tag != tag) continue;
            matches.add(field);
        }
        this.fields.removeAll(matches);
    }

    public TiffOutputField findField(TagInfo tagInfo) {
        return this.findField(tagInfo.tag);
    }

    public TiffOutputField findField(int tag) {
        for (TiffOutputField field : this.fields) {
            if (field.tag != tag) continue;
            return field;
        }
        return null;
    }

    public void sortFields() {
        Comparator comparator = (e1, e2) -> {
            if (e1.tag != e2.tag) {
                return e1.tag - e2.tag;
            }
            return e1.getSortHint() - e2.getSortHint();
        };
        Collections.sort(this.fields, comparator);
    }

    public String description() {
        return TiffDirectory.description(this.type);
    }

    @Override
    public void writeItem(BinaryOutputStream bos) throws IOException, ImageWriteException {
        bos.write2Bytes(this.fields.size());
        for (TiffOutputField field : this.fields) {
            field.writeField(bos);
        }
        long nextDirectoryOffset = 0L;
        if (this.nextDirectory != null) {
            nextDirectoryOffset = this.nextDirectory.getOffset();
        }
        if (nextDirectoryOffset == -1L) {
            bos.write4Bytes(0);
        } else {
            bos.write4Bytes((int)nextDirectoryOffset);
        }
    }

    public void setJpegImageData(JpegImageData rawJpegImageData) {
        this.jpegImageData = rawJpegImageData;
    }

    public JpegImageData getRawJpegImageData() {
        return this.jpegImageData;
    }

    public void setTiffImageData(TiffImageData rawTiffImageData) {
        this.tiffImageData = rawTiffImageData;
    }

    public TiffImageData getRawTiffImageData() {
        return this.tiffImageData;
    }

    @Override
    public int getItemLength() {
        return 12 * this.fields.size() + 2 + 4;
    }

    @Override
    public String getItemDescription() {
        TiffDirectoryType dirType = TiffDirectoryType.getExifDirectoryType(this.type);
        return "Directory: " + dirType.name + " (" + this.type + ")";
    }

    private void removeFieldIfPresent(TagInfo tagInfo) {
        TiffOutputField field = this.findField(tagInfo);
        if (null != field) {
            this.fields.remove(field);
        }
    }

    protected List<TiffOutputItem> getOutputItems(TiffOutputSummary outputSummary) throws ImageWriteException {
        this.removeFieldIfPresent(TiffTagConstants.TIFF_TAG_JPEG_INTERCHANGE_FORMAT);
        this.removeFieldIfPresent(TiffTagConstants.TIFF_TAG_JPEG_INTERCHANGE_FORMAT_LENGTH);
        TiffOutputField jpegOffsetField = null;
        if (null != this.jpegImageData) {
            jpegOffsetField = new TiffOutputField(TiffTagConstants.TIFF_TAG_JPEG_INTERCHANGE_FORMAT, FieldType.LONG, 1, new byte[4]);
            this.add(jpegOffsetField);
            byte[] lengthValue = FieldType.LONG.writeData(this.jpegImageData.length, outputSummary.byteOrder);
            TiffOutputField jpegLengthField = new TiffOutputField(TiffTagConstants.TIFF_TAG_JPEG_INTERCHANGE_FORMAT_LENGTH, FieldType.LONG, 1, lengthValue);
            this.add(jpegLengthField);
        }
        this.removeFieldIfPresent(TiffTagConstants.TIFF_TAG_STRIP_OFFSETS);
        this.removeFieldIfPresent(TiffTagConstants.TIFF_TAG_STRIP_BYTE_COUNTS);
        this.removeFieldIfPresent(TiffTagConstants.TIFF_TAG_TILE_OFFSETS);
        this.removeFieldIfPresent(TiffTagConstants.TIFF_TAG_TILE_BYTE_COUNTS);
        ImageDataOffsets imageDataInfo = null;
        if (null != this.tiffImageData) {
            TagInfoShortOrLong byteCountsTag;
            Object offsetTag;
            boolean stripsNotTiles = this.tiffImageData.stripsNotTiles();
            if (stripsNotTiles) {
                offsetTag = TiffTagConstants.TIFF_TAG_STRIP_OFFSETS;
                byteCountsTag = TiffTagConstants.TIFF_TAG_STRIP_BYTE_COUNTS;
            } else {
                offsetTag = TiffTagConstants.TIFF_TAG_TILE_OFFSETS;
                byteCountsTag = TiffTagConstants.TIFF_TAG_TILE_BYTE_COUNTS;
            }
            TiffElement.DataElement[] imageData = this.tiffImageData.getImageData();
            int[] imageDataOffsets = new int[imageData.length];
            int[] imageDataByteCounts = new int[imageData.length];
            for (int i = 0; i < imageData.length; ++i) {
                imageDataByteCounts[i] = imageData[i].length;
            }
            TiffOutputField imageDataOffsetField = new TiffOutputField((TagInfo)offsetTag, FieldType.LONG, imageDataOffsets.length, FieldType.LONG.writeData(imageDataOffsets, outputSummary.byteOrder));
            this.add(imageDataOffsetField);
            byte[] data = FieldType.LONG.writeData(imageDataByteCounts, outputSummary.byteOrder);
            TiffOutputField byteCountsField = new TiffOutputField(byteCountsTag, FieldType.LONG, imageDataByteCounts.length, data);
            this.add(byteCountsField);
            imageDataInfo = new ImageDataOffsets(imageData, imageDataOffsets, imageDataOffsetField);
        }
        ArrayList<TiffOutputItem> result = new ArrayList<TiffOutputItem>();
        result.add(this);
        this.sortFields();
        for (TiffOutputField field : this.fields) {
            if (field.isLocalValue()) continue;
            TiffOutputItem item = field.getSeperateValue();
            result.add(item);
        }
        if (null != imageDataInfo) {
            Collections.addAll(result, imageDataInfo.outputItems);
            outputSummary.addTiffImageData(imageDataInfo);
        }
        if (null != this.jpegImageData) {
            TiffOutputItem.Value item = new TiffOutputItem.Value("JPEG image data", this.jpegImageData.getData());
            result.add(item);
            outputSummary.add(item, jpegOffsetField);
        }
        return result;
    }
}

