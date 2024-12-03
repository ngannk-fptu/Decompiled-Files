/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.write;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.common.RationalNumber;
import org.apache.commons.imaging.formats.tiff.constants.GpsTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.TiffConstants;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputField;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputItem;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSummary;
import org.apache.commons.imaging.internal.Debug;

public final class TiffOutputSet {
    public final ByteOrder byteOrder;
    private final List<TiffOutputDirectory> directories = new ArrayList<TiffOutputDirectory>();
    private static final String NEWLINE = System.getProperty("line.separator");

    public TiffOutputSet() {
        this(TiffConstants.DEFAULT_TIFF_BYTE_ORDER);
    }

    public TiffOutputSet(ByteOrder byteOrder) {
        this.byteOrder = byteOrder;
    }

    protected List<TiffOutputItem> getOutputItems(TiffOutputSummary outputSummary) throws ImageWriteException {
        ArrayList<TiffOutputItem> result = new ArrayList<TiffOutputItem>();
        for (TiffOutputDirectory directory : this.directories) {
            result.addAll(directory.getOutputItems(outputSummary));
        }
        return result;
    }

    public void addDirectory(TiffOutputDirectory directory) throws ImageWriteException {
        if (null != this.findDirectory(directory.type)) {
            throw new ImageWriteException("Output set already contains a directory of that type.");
        }
        this.directories.add(directory);
    }

    public List<TiffOutputDirectory> getDirectories() {
        return new ArrayList<TiffOutputDirectory>(this.directories);
    }

    public TiffOutputDirectory getRootDirectory() {
        return this.findDirectory(0);
    }

    public TiffOutputDirectory getExifDirectory() {
        return this.findDirectory(-2);
    }

    public TiffOutputDirectory getOrCreateRootDirectory() throws ImageWriteException {
        TiffOutputDirectory result = this.findDirectory(0);
        if (null != result) {
            return result;
        }
        return this.addRootDirectory();
    }

    public TiffOutputDirectory getOrCreateExifDirectory() throws ImageWriteException {
        this.getOrCreateRootDirectory();
        TiffOutputDirectory result = this.findDirectory(-2);
        if (null != result) {
            return result;
        }
        return this.addExifDirectory();
    }

    public TiffOutputDirectory getOrCreateGPSDirectory() throws ImageWriteException {
        this.getOrCreateExifDirectory();
        TiffOutputDirectory result = this.findDirectory(-3);
        if (null != result) {
            return result;
        }
        return this.addGPSDirectory();
    }

    public TiffOutputDirectory getGPSDirectory() {
        return this.findDirectory(-3);
    }

    public TiffOutputDirectory getInteroperabilityDirectory() {
        return this.findDirectory(-4);
    }

    public TiffOutputDirectory findDirectory(int directoryType) {
        for (TiffOutputDirectory directory : this.directories) {
            if (directory.type != directoryType) continue;
            return directory;
        }
        return null;
    }

    public void setGPSInDegrees(double longitude, double latitude) throws ImageWriteException {
        TiffOutputDirectory gpsDirectory = this.getOrCreateGPSDirectory();
        gpsDirectory.removeField(GpsTagConstants.GPS_TAG_GPS_VERSION_ID);
        gpsDirectory.add(GpsTagConstants.GPS_TAG_GPS_VERSION_ID, GpsTagConstants.gpsVersion());
        String longitudeRef = longitude < 0.0 ? "W" : "E";
        longitude = Math.abs(longitude);
        String latitudeRef = latitude < 0.0 ? "S" : "N";
        latitude = Math.abs(latitude);
        gpsDirectory.removeField(GpsTagConstants.GPS_TAG_GPS_LONGITUDE_REF);
        gpsDirectory.add(GpsTagConstants.GPS_TAG_GPS_LONGITUDE_REF, longitudeRef);
        gpsDirectory.removeField(GpsTagConstants.GPS_TAG_GPS_LATITUDE_REF);
        gpsDirectory.add(GpsTagConstants.GPS_TAG_GPS_LATITUDE_REF, latitudeRef);
        double value = longitude;
        double longitudeDegrees = (long)value;
        value %= 1.0;
        double longitudeMinutes = (long)(value *= 60.0);
        value %= 1.0;
        double longitudeSeconds = value *= 60.0;
        gpsDirectory.removeField(GpsTagConstants.GPS_TAG_GPS_LONGITUDE);
        gpsDirectory.add(GpsTagConstants.GPS_TAG_GPS_LONGITUDE, RationalNumber.valueOf(longitudeDegrees), RationalNumber.valueOf(longitudeMinutes), RationalNumber.valueOf(longitudeSeconds));
        value = latitude;
        double latitudeDegrees = (long)value;
        value %= 1.0;
        double latitudeMinutes = (long)(value *= 60.0);
        value %= 1.0;
        double latitudeSeconds = value *= 60.0;
        gpsDirectory.removeField(GpsTagConstants.GPS_TAG_GPS_LATITUDE);
        gpsDirectory.add(GpsTagConstants.GPS_TAG_GPS_LATITUDE, RationalNumber.valueOf(latitudeDegrees), RationalNumber.valueOf(latitudeMinutes), RationalNumber.valueOf(latitudeSeconds));
    }

    public void removeField(TagInfo tagInfo) {
        this.removeField(tagInfo.tag);
    }

    public void removeField(int tag) {
        for (TiffOutputDirectory directory : this.directories) {
            directory.removeField(tag);
        }
    }

    public TiffOutputField findField(TagInfo tagInfo) {
        return this.findField(tagInfo.tag);
    }

    public TiffOutputField findField(int tag) {
        for (TiffOutputDirectory directory : this.directories) {
            TiffOutputField field = directory.findField(tag);
            if (null == field) continue;
            return field;
        }
        return null;
    }

    public TiffOutputDirectory addRootDirectory() throws ImageWriteException {
        TiffOutputDirectory result = new TiffOutputDirectory(0, this.byteOrder);
        this.addDirectory(result);
        return result;
    }

    public TiffOutputDirectory addExifDirectory() throws ImageWriteException {
        TiffOutputDirectory result = new TiffOutputDirectory(-2, this.byteOrder);
        this.addDirectory(result);
        return result;
    }

    public TiffOutputDirectory addGPSDirectory() throws ImageWriteException {
        TiffOutputDirectory result = new TiffOutputDirectory(-3, this.byteOrder);
        this.addDirectory(result);
        return result;
    }

    public TiffOutputDirectory addInteroperabilityDirectory() throws ImageWriteException {
        this.getOrCreateExifDirectory();
        TiffOutputDirectory result = new TiffOutputDirectory(-4, this.byteOrder);
        this.addDirectory(result);
        return result;
    }

    public String toString() {
        return this.toString(null);
    }

    public String toString(String prefix) {
        if (prefix == null) {
            prefix = "";
        }
        StringBuilder result = new StringBuilder(39);
        result.append(prefix);
        result.append("TiffOutputSet {");
        result.append(NEWLINE);
        result.append(prefix);
        result.append("byteOrder: ");
        result.append(this.byteOrder);
        result.append(NEWLINE);
        for (int i = 0; i < this.directories.size(); ++i) {
            TiffOutputDirectory directory = this.directories.get(i);
            result.append(String.format("%s\tdirectory %d: %s (%d)%n", prefix, i, directory.description(), directory.type));
            List<TiffOutputField> fields = directory.getFields();
            for (TiffOutputField field : fields) {
                result.append(prefix);
                result.append("\t\tfield ").append(i).append(": ").append(field.tagInfo);
                result.append(NEWLINE);
            }
        }
        result.append(prefix);
        result.append('}');
        result.append(NEWLINE);
        return result.toString();
    }

    public void dump() {
        Debug.debug(this.toString());
    }
}

