/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.taginfos;

import java.nio.ByteOrder;
import org.apache.commons.imaging.common.ByteConversions;
import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryType;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;

public class TagInfoShortOrLong
extends TagInfo {
    public TagInfoShortOrLong(String name, int tag, int length, TiffDirectoryType directoryType) {
        super(name, tag, FieldType.SHORT_OR_LONG, length, directoryType, false);
    }

    public TagInfoShortOrLong(String name, int tag, int length, TiffDirectoryType directoryType, boolean isOffset) {
        super(name, tag, FieldType.SHORT_OR_LONG, length, directoryType, isOffset);
    }

    public byte[] encodeValue(ByteOrder byteOrder, short ... values) {
        return ByteConversions.toBytes(values, byteOrder);
    }

    public byte[] encodeValue(ByteOrder byteOrder, int ... values) {
        return ByteConversions.toBytes(values, byteOrder);
    }
}

