/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.taginfos;

import java.nio.ByteOrder;
import org.apache.commons.imaging.common.ByteConversions;
import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryType;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;

public class TagInfoLongs
extends TagInfo {
    public TagInfoLongs(String name, int tag, int length, TiffDirectoryType directoryType) {
        super(name, tag, FieldType.LONG, length, directoryType);
    }

    public TagInfoLongs(String name, int tag, int length, TiffDirectoryType directoryType, boolean isOffset) {
        super(name, tag, FieldType.LONG, length, directoryType, isOffset);
    }

    public int[] getValue(ByteOrder byteOrder, byte[] bytes) {
        return ByteConversions.toInts(bytes, byteOrder);
    }

    public byte[] encodeValue(ByteOrder byteOrder, int ... values) {
        return ByteConversions.toBytes(values, byteOrder);
    }
}

