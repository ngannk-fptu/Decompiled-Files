/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.taginfos;

import java.nio.ByteOrder;
import java.util.List;
import org.apache.commons.imaging.common.ByteConversions;
import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryType;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;

public class TagInfoLong
extends TagInfo {
    public TagInfoLong(String name, int tag, TiffDirectoryType directoryType) {
        super(name, tag, FieldType.LONG, 1, directoryType);
    }

    public TagInfoLong(String name, int tag, TiffDirectoryType directoryType, boolean isOffset) {
        super(name, tag, FieldType.LONG, 1, directoryType, isOffset);
    }

    public TagInfoLong(String name, int tag, List<FieldType> dataTypes, int length, TiffDirectoryType exifDirectory, boolean isOffset) {
        super(name, tag, dataTypes, length, exifDirectory, isOffset);
    }

    public int getValue(ByteOrder byteOrder, byte[] bytes) {
        return ByteConversions.toInt(bytes, byteOrder);
    }

    public byte[] encodeValue(ByteOrder byteOrder, int value) {
        return ByteConversions.toBytes(value, byteOrder);
    }
}

