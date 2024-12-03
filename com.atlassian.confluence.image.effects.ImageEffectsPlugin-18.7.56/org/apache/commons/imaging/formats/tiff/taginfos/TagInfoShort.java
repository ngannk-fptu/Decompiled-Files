/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.taginfos;

import java.nio.ByteOrder;
import org.apache.commons.imaging.common.ByteConversions;
import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryType;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;

public class TagInfoShort
extends TagInfo {
    public TagInfoShort(String name, int tag, TiffDirectoryType directoryType) {
        super(name, tag, FieldType.SHORT, 1, directoryType);
    }

    public short getValue(ByteOrder byteOrder, byte[] bytes) {
        return ByteConversions.toShort(bytes, byteOrder);
    }

    public byte[] encodeValue(ByteOrder byteOrder, short value) {
        return ByteConversions.toBytes(value, byteOrder);
    }
}

