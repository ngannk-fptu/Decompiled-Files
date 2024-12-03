/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.taginfos;

import java.nio.ByteOrder;
import org.apache.commons.imaging.common.ByteConversions;
import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryType;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;

public class TagInfoFloats
extends TagInfo {
    public TagInfoFloats(String name, int tag, int length, TiffDirectoryType directoryType) {
        super(name, tag, FieldType.FLOAT, length, directoryType);
    }

    public float[] getValue(ByteOrder byteOrder, byte[] bytes) {
        return ByteConversions.toFloats(bytes, byteOrder);
    }

    public byte[] encodeValue(ByteOrder byteOrder, float ... values) {
        return ByteConversions.toBytes(values, byteOrder);
    }
}

