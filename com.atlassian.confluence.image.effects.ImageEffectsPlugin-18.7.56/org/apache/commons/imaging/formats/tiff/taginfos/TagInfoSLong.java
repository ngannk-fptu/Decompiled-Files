/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.taginfos;

import java.nio.ByteOrder;
import org.apache.commons.imaging.common.ByteConversions;
import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryType;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;

public class TagInfoSLong
extends TagInfo {
    public TagInfoSLong(String name, int tag, TiffDirectoryType directoryType) {
        super(name, tag, FieldType.SLONG, 1, directoryType);
    }

    public int getValue(ByteOrder byteOrder, byte[] bytes) {
        return ByteConversions.toInt(bytes, byteOrder);
    }

    public byte[] encodeValue(ByteOrder byteOrder, int value) {
        return ByteConversions.toBytes(value, byteOrder);
    }
}

