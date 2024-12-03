/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.taginfos;

import java.nio.ByteOrder;
import org.apache.commons.imaging.common.ByteConversions;
import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryType;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;

public class TagInfoByteOrShort
extends TagInfo {
    public TagInfoByteOrShort(String name, int tag, int length, TiffDirectoryType directoryType) {
        super(name, tag, FieldType.BYTE_OR_SHORT, length, directoryType);
    }

    public byte[] encodeValue(ByteOrder byteOrder, byte ... values) {
        return values;
    }

    public byte[] encodeValue(ByteOrder byteOrder, short ... values) {
        return ByteConversions.toBytes(values, byteOrder);
    }
}

