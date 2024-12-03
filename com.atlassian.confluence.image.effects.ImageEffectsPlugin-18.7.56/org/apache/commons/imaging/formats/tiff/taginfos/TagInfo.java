/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.taginfos;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryType;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;

public class TagInfo {
    public static final int LENGTH_UNKNOWN = -1;
    public final String name;
    public final int tag;
    public final List<FieldType> dataTypes;
    public final int length;
    public final TiffDirectoryType directoryType;
    private final boolean isOffset;

    public TagInfo(String name, int tag, FieldType dataType, int length, TiffDirectoryType exifDirectory) {
        this(name, tag, Arrays.asList(dataType), length, exifDirectory);
    }

    public TagInfo(String name, int tag, FieldType dataType, int length, TiffDirectoryType exifDirectory, boolean isOffset) {
        this(name, tag, Arrays.asList(dataType), length, exifDirectory, isOffset);
    }

    public TagInfo(String name, int tag, FieldType dataType, int length) {
        this(name, tag, Arrays.asList(dataType), length, TiffDirectoryType.EXIF_DIRECTORY_UNKNOWN);
    }

    public TagInfo(String name, int tag, FieldType dataType) {
        this(name, tag, dataType, -1, TiffDirectoryType.EXIF_DIRECTORY_UNKNOWN);
    }

    public TagInfo(String name, int tag, List<FieldType> dataTypes, int length, TiffDirectoryType exifDirectory) {
        this(name, tag, dataTypes, length, exifDirectory, false);
    }

    public TagInfo(String name, int tag, List<FieldType> dataTypes, int length, TiffDirectoryType exifDirectory, boolean isOffset) {
        this.name = name;
        this.tag = tag;
        this.dataTypes = Collections.unmodifiableList(new ArrayList<FieldType>(dataTypes));
        this.length = length;
        this.directoryType = exifDirectory;
        this.isOffset = isOffset;
    }

    public Object getValue(TiffField entry) throws ImageReadException {
        return entry.getFieldType().getValue(entry);
    }

    public byte[] encodeValue(FieldType fieldType, Object value, ByteOrder byteOrder) throws ImageWriteException {
        return fieldType.writeData(value, byteOrder);
    }

    public String getDescription() {
        return this.tag + " (0x" + Integer.toHexString(this.tag) + ": " + this.name + "): ";
    }

    public String toString() {
        return "[TagInfo. tag: " + this.tag + " (0x" + Integer.toHexString(this.tag) + ", name: " + this.name + "]";
    }

    public boolean isOffset() {
        return this.isOffset;
    }

    public boolean isText() {
        return false;
    }
}

