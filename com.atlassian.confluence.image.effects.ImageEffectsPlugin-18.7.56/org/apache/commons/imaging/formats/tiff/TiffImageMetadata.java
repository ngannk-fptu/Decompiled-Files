/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.common.GenericImageMetadata;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.common.RationalNumber;
import org.apache.commons.imaging.formats.tiff.JpegImageData;
import org.apache.commons.imaging.formats.tiff.TiffContents;
import org.apache.commons.imaging.formats.tiff.TiffDirectory;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.TiffImageData;
import org.apache.commons.imaging.formats.tiff.TiffTags;
import org.apache.commons.imaging.formats.tiff.constants.GpsTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryType;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoAscii;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoByte;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoDoubles;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoFloats;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoGpsText;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoLongs;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoRationals;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoSBytes;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoSLongs;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoSRationals;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoSShorts;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoShorts;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoXpString;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputField;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

public class TiffImageMetadata
extends GenericImageMetadata {
    public final TiffContents contents;

    public TiffImageMetadata(TiffContents contents) {
        this.contents = contents;
    }

    public List<? extends ImageMetadata.ImageMetadataItem> getDirectories() {
        return super.getItems();
    }

    @Override
    public List<? extends ImageMetadata.ImageMetadataItem> getItems() {
        ArrayList<? extends ImageMetadata.ImageMetadataItem> result = new ArrayList<ImageMetadata.ImageMetadataItem>();
        List<? extends ImageMetadata.ImageMetadataItem> items = super.getItems();
        for (ImageMetadata.ImageMetadataItem imageMetadataItem : items) {
            Directory dir = (Directory)imageMetadataItem;
            result.addAll(dir.getItems());
        }
        return result;
    }

    public TiffOutputSet getOutputSet() throws ImageWriteException {
        ByteOrder byteOrder = this.contents.header.byteOrder;
        TiffOutputSet result = new TiffOutputSet(byteOrder);
        List<? extends ImageMetadata.ImageMetadataItem> srcDirs = this.getDirectories();
        for (ImageMetadata.ImageMetadataItem imageMetadataItem : srcDirs) {
            Directory srcDir = (Directory)imageMetadataItem;
            if (null != result.findDirectory(srcDir.type)) continue;
            TiffOutputDirectory outputDirectory = srcDir.getOutputDirectory(byteOrder);
            result.addDirectory(outputDirectory);
        }
        return result;
    }

    public TiffField findField(TagInfo tagInfo) throws ImageReadException {
        return this.findField(tagInfo, false);
    }

    public TiffField findField(TagInfo tagInfo, boolean exactDirectoryMatch) throws ImageReadException {
        TiffField field;
        Directory directory;
        Integer tagCount = TiffTags.getTagCount(tagInfo.tag);
        int tagsMatching = tagCount == null ? 0 : tagCount;
        List<? extends ImageMetadata.ImageMetadataItem> directories = this.getDirectories();
        if (exactDirectoryMatch || tagInfo.directoryType != TiffDirectoryType.EXIF_DIRECTORY_UNKNOWN) {
            for (ImageMetadata.ImageMetadataItem imageMetadataItem : directories) {
                directory = (Directory)imageMetadataItem;
                if (directory.type != tagInfo.directoryType.directoryType || (field = directory.findField(tagInfo)) == null) continue;
                return field;
            }
            if (exactDirectoryMatch || tagsMatching > 1) {
                return null;
            }
            for (ImageMetadata.ImageMetadataItem imageMetadataItem : directories) {
                directory = (Directory)imageMetadataItem;
                if (!(tagInfo.directoryType.isImageDirectory() && directory.type >= 0 ? (field = directory.findField(tagInfo)) != null : !tagInfo.directoryType.isImageDirectory() && directory.type < 0 && (field = directory.findField(tagInfo)) != null)) continue;
                return field;
            }
        }
        for (ImageMetadata.ImageMetadataItem imageMetadataItem : directories) {
            directory = (Directory)imageMetadataItem;
            field = directory.findField(tagInfo);
            if (field == null) continue;
            return field;
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

    public byte[] getFieldValue(TagInfoByte tag) throws ImageReadException {
        TiffField field = this.findField(tag);
        if (field == null) {
            return null;
        }
        if (!tag.dataTypes.contains(field.getFieldType())) {
            return null;
        }
        return field.getByteArrayValue();
    }

    public String[] getFieldValue(TagInfoAscii tag) throws ImageReadException {
        TiffField field = this.findField(tag);
        if (field == null) {
            return null;
        }
        if (!tag.dataTypes.contains(field.getFieldType())) {
            return null;
        }
        byte[] bytes = field.getByteArrayValue();
        return tag.getValue(field.getByteOrder(), bytes);
    }

    public short[] getFieldValue(TagInfoShorts tag) throws ImageReadException {
        TiffField field = this.findField(tag);
        if (field == null) {
            return null;
        }
        if (!tag.dataTypes.contains(field.getFieldType())) {
            return null;
        }
        byte[] bytes = field.getByteArrayValue();
        return tag.getValue(field.getByteOrder(), bytes);
    }

    public int[] getFieldValue(TagInfoLongs tag) throws ImageReadException {
        TiffField field = this.findField(tag);
        if (field == null) {
            return null;
        }
        if (!tag.dataTypes.contains(field.getFieldType())) {
            return null;
        }
        byte[] bytes = field.getByteArrayValue();
        return tag.getValue(field.getByteOrder(), bytes);
    }

    public RationalNumber[] getFieldValue(TagInfoRationals tag) throws ImageReadException {
        TiffField field = this.findField(tag);
        if (field == null) {
            return null;
        }
        if (!tag.dataTypes.contains(field.getFieldType())) {
            return null;
        }
        byte[] bytes = field.getByteArrayValue();
        return tag.getValue(field.getByteOrder(), bytes);
    }

    public byte[] getFieldValue(TagInfoSBytes tag) throws ImageReadException {
        TiffField field = this.findField(tag);
        if (field == null) {
            return null;
        }
        if (!tag.dataTypes.contains(field.getFieldType())) {
            return null;
        }
        return field.getByteArrayValue();
    }

    public short[] getFieldValue(TagInfoSShorts tag) throws ImageReadException {
        TiffField field = this.findField(tag);
        if (field == null) {
            return null;
        }
        if (!tag.dataTypes.contains(field.getFieldType())) {
            return null;
        }
        byte[] bytes = field.getByteArrayValue();
        return tag.getValue(field.getByteOrder(), bytes);
    }

    public int[] getFieldValue(TagInfoSLongs tag) throws ImageReadException {
        TiffField field = this.findField(tag);
        if (field == null) {
            return null;
        }
        if (!tag.dataTypes.contains(field.getFieldType())) {
            return null;
        }
        byte[] bytes = field.getByteArrayValue();
        return tag.getValue(field.getByteOrder(), bytes);
    }

    public RationalNumber[] getFieldValue(TagInfoSRationals tag) throws ImageReadException {
        TiffField field = this.findField(tag);
        if (field == null) {
            return null;
        }
        if (!tag.dataTypes.contains(field.getFieldType())) {
            return null;
        }
        byte[] bytes = field.getByteArrayValue();
        return tag.getValue(field.getByteOrder(), bytes);
    }

    public float[] getFieldValue(TagInfoFloats tag) throws ImageReadException {
        TiffField field = this.findField(tag);
        if (field == null) {
            return null;
        }
        if (!tag.dataTypes.contains(field.getFieldType())) {
            return null;
        }
        byte[] bytes = field.getByteArrayValue();
        return tag.getValue(field.getByteOrder(), bytes);
    }

    public double[] getFieldValue(TagInfoDoubles tag) throws ImageReadException {
        TiffField field = this.findField(tag);
        if (field == null) {
            return null;
        }
        if (!tag.dataTypes.contains(field.getFieldType())) {
            return null;
        }
        byte[] bytes = field.getByteArrayValue();
        return tag.getValue(field.getByteOrder(), bytes);
    }

    public String getFieldValue(TagInfoGpsText tag) throws ImageReadException {
        TiffField field = this.findField(tag);
        if (field == null) {
            return null;
        }
        return tag.getValue(field);
    }

    public String getFieldValue(TagInfoXpString tag) throws ImageReadException {
        TiffField field = this.findField(tag);
        if (field == null) {
            return null;
        }
        return tag.getValue(field);
    }

    public TiffDirectory findDirectory(int directoryType) {
        List<? extends ImageMetadata.ImageMetadataItem> directories = this.getDirectories();
        for (ImageMetadata.ImageMetadataItem imageMetadataItem : directories) {
            Directory directory = (Directory)imageMetadataItem;
            if (directory.type != directoryType) continue;
            return directory.directory;
        }
        return null;
    }

    public List<TiffField> getAllFields() {
        ArrayList<TiffField> result = new ArrayList<TiffField>();
        List<? extends ImageMetadata.ImageMetadataItem> directories = this.getDirectories();
        for (ImageMetadata.ImageMetadataItem imageMetadataItem : directories) {
            Directory directory = (Directory)imageMetadataItem;
            result.addAll(directory.getAllFields());
        }
        return result;
    }

    public GPSInfo getGPS() throws ImageReadException {
        TiffDirectory gpsDirectory = this.findDirectory(-3);
        if (null == gpsDirectory) {
            return null;
        }
        TiffField latitudeRefField = gpsDirectory.findField(GpsTagConstants.GPS_TAG_GPS_LATITUDE_REF);
        TiffField latitudeField = gpsDirectory.findField(GpsTagConstants.GPS_TAG_GPS_LATITUDE);
        TiffField longitudeRefField = gpsDirectory.findField(GpsTagConstants.GPS_TAG_GPS_LONGITUDE_REF);
        TiffField longitudeField = gpsDirectory.findField(GpsTagConstants.GPS_TAG_GPS_LONGITUDE);
        if (latitudeRefField == null || latitudeField == null || longitudeRefField == null || longitudeField == null) {
            return null;
        }
        String latitudeRef = latitudeRefField.getStringValue();
        RationalNumber[] latitude = (RationalNumber[])latitudeField.getValue();
        String longitudeRef = longitudeRefField.getStringValue();
        RationalNumber[] longitude = (RationalNumber[])longitudeField.getValue();
        if (latitude.length != 3 || longitude.length != 3) {
            throw new ImageReadException("Expected three values for latitude and longitude.");
        }
        RationalNumber latitudeDegrees = latitude[0];
        RationalNumber latitudeMinutes = latitude[1];
        RationalNumber latitudeSeconds = latitude[2];
        RationalNumber longitudeDegrees = longitude[0];
        RationalNumber longitudeMinutes = longitude[1];
        RationalNumber longitudeSeconds = longitude[2];
        return new GPSInfo(latitudeRef, longitudeRef, latitudeDegrees, latitudeMinutes, latitudeSeconds, longitudeDegrees, longitudeMinutes, longitudeSeconds);
    }

    public static class GPSInfo {
        public final String latitudeRef;
        public final String longitudeRef;
        public final RationalNumber latitudeDegrees;
        public final RationalNumber latitudeMinutes;
        public final RationalNumber latitudeSeconds;
        public final RationalNumber longitudeDegrees;
        public final RationalNumber longitudeMinutes;
        public final RationalNumber longitudeSeconds;

        public GPSInfo(String latitudeRef, String longitudeRef, RationalNumber latitudeDegrees, RationalNumber latitudeMinutes, RationalNumber latitudeSeconds, RationalNumber longitudeDegrees, RationalNumber longitudeMinutes, RationalNumber longitudeSeconds) {
            this.latitudeRef = latitudeRef;
            this.longitudeRef = longitudeRef;
            this.latitudeDegrees = latitudeDegrees;
            this.latitudeMinutes = latitudeMinutes;
            this.latitudeSeconds = latitudeSeconds;
            this.longitudeDegrees = longitudeDegrees;
            this.longitudeMinutes = longitudeMinutes;
            this.longitudeSeconds = longitudeSeconds;
        }

        public String toString() {
            return "[GPS. Latitude: " + this.latitudeDegrees.toDisplayString() + " degrees, " + this.latitudeMinutes.toDisplayString() + " minutes, " + this.latitudeSeconds.toDisplayString() + " seconds " + this.latitudeRef + ", Longitude: " + this.longitudeDegrees.toDisplayString() + " degrees, " + this.longitudeMinutes.toDisplayString() + " minutes, " + this.longitudeSeconds.toDisplayString() + " seconds " + this.longitudeRef + ']';
        }

        public double getLongitudeAsDegreesEast() throws ImageReadException {
            double result = this.longitudeDegrees.doubleValue() + this.longitudeMinutes.doubleValue() / 60.0 + this.longitudeSeconds.doubleValue() / 3600.0;
            if (this.longitudeRef.trim().equalsIgnoreCase("e")) {
                return result;
            }
            if (this.longitudeRef.trim().equalsIgnoreCase("w")) {
                return -result;
            }
            throw new ImageReadException("Unknown longitude ref: \"" + this.longitudeRef + "\"");
        }

        public double getLatitudeAsDegreesNorth() throws ImageReadException {
            double result = this.latitudeDegrees.doubleValue() + this.latitudeMinutes.doubleValue() / 60.0 + this.latitudeSeconds.doubleValue() / 3600.0;
            if (this.latitudeRef.trim().equalsIgnoreCase("n")) {
                return result;
            }
            if (this.latitudeRef.trim().equalsIgnoreCase("s")) {
                return -result;
            }
            throw new ImageReadException("Unknown latitude ref: \"" + this.latitudeRef + "\"");
        }
    }

    public static class TiffMetadataItem
    extends GenericImageMetadata.GenericImageMetadataItem {
        private final TiffField entry;

        public TiffMetadataItem(TiffField entry) {
            super(entry.getTagName(), entry.getValueDescription());
            this.entry = entry;
        }

        public TiffField getTiffField() {
            return this.entry;
        }
    }

    public static class Directory
    extends GenericImageMetadata
    implements ImageMetadata.ImageMetadataItem {
        public final int type;
        private final TiffDirectory directory;
        private final ByteOrder byteOrder;

        public Directory(ByteOrder byteOrder, TiffDirectory directory) {
            this.type = directory.type;
            this.directory = directory;
            this.byteOrder = byteOrder;
        }

        public void add(TiffField entry) {
            this.add(new TiffMetadataItem(entry));
        }

        public BufferedImage getThumbnail() throws ImageReadException, IOException {
            return this.directory.getTiffImage(this.byteOrder);
        }

        public TiffImageData getTiffImageData() {
            return this.directory.getTiffImageData();
        }

        public TiffField findField(TagInfo tagInfo) throws ImageReadException {
            return this.directory.findField(tagInfo);
        }

        public List<TiffField> getAllFields() {
            return this.directory.getDirectoryEntries();
        }

        public JpegImageData getJpegImageData() {
            return this.directory.getJpegImageData();
        }

        @Override
        public String toString(String prefix) {
            return (prefix != null ? prefix : "") + this.directory.description() + ": " + (this.getTiffImageData() != null ? " (tiffImageData)" : "") + (this.getJpegImageData() != null ? " (jpegImageData)" : "") + "\n" + super.toString(prefix) + "\n";
        }

        public TiffOutputDirectory getOutputDirectory(ByteOrder byteOrder) throws ImageWriteException {
            try {
                TiffOutputDirectory dstDir = new TiffOutputDirectory(this.type, byteOrder);
                List<? extends ImageMetadata.ImageMetadataItem> entries = this.getItems();
                for (ImageMetadata.ImageMetadataItem imageMetadataItem : entries) {
                    TiffMetadataItem item = (TiffMetadataItem)imageMetadataItem;
                    TiffField srcField = item.getTiffField();
                    if (null != dstDir.findField(srcField.getTag()) || srcField.getTagInfo().isOffset()) continue;
                    TagInfo tagInfo = srcField.getTagInfo();
                    FieldType fieldType = srcField.getFieldType();
                    Object value = srcField.getValue();
                    byte[] bytes = tagInfo.encodeValue(fieldType, value, byteOrder);
                    int count = bytes.length / fieldType.getSize();
                    TiffOutputField dstField = new TiffOutputField(srcField.getTag(), tagInfo, fieldType, count, bytes);
                    dstField.setSortHint(srcField.getSortHint());
                    dstDir.add(dstField);
                }
                dstDir.setTiffImageData(this.getTiffImageData());
                dstDir.setJpegImageData(this.getJpegImageData());
                return dstDir;
            }
            catch (ImageReadException e) {
                throw new ImageWriteException(e.getMessage(), e);
            }
        }
    }
}

