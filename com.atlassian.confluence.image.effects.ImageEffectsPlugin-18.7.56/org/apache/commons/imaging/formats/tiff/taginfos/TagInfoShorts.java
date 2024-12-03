/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.taginfos;

import java.nio.ByteOrder;
import org.apache.commons.imaging.common.ByteConversions;
import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryType;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;

public class TagInfoShorts
extends TagInfo {
    public TagInfoShorts(String name, int tag, int length, TiffDirectoryType directoryType) {
        super(name, tag, FieldType.SHORT, length, directoryType);
    }

    public short[] getValue(ByteOrder byteOrder, byte[] bytes) {
        return ByteConversions.toShorts(bytes, byteOrder);
    }

    public byte[] encodeValue(ByteOrder byteOrder, short ... values) {
        return ByteConversions.toBytes(values, byteOrder);
    }
}

