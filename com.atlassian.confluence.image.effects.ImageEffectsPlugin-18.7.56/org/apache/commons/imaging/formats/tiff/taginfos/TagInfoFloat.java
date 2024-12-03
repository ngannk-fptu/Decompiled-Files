/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.taginfos;

import java.nio.ByteOrder;
import org.apache.commons.imaging.common.ByteConversions;
import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryType;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;

public class TagInfoFloat
extends TagInfo {
    public TagInfoFloat(String name, int tag, TiffDirectoryType directoryType) {
        super(name, tag, FieldType.FLOAT, 1, directoryType);
    }

    public float getValue(ByteOrder byteOrder, byte[] bytes) {
        return ByteConversions.toFloat(bytes, byteOrder);
    }

    public byte[] encodeValue(ByteOrder byteOrder, float value) {
        return ByteConversions.toBytes(value, byteOrder);
    }
}

