/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.taginfos;

import java.nio.ByteOrder;
import org.apache.commons.imaging.common.ByteConversions;
import org.apache.commons.imaging.common.RationalNumber;
import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryType;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;

public class TagInfoShortOrLongOrRational
extends TagInfo {
    public TagInfoShortOrLongOrRational(String name, int tag, int length, TiffDirectoryType directoryType) {
        super(name, tag, FieldType.SHORT_OR_LONG_OR_RATIONAL, length, directoryType);
    }

    public byte[] encodeValue(ByteOrder byteOrder, short ... values) {
        return ByteConversions.toBytes(values, byteOrder);
    }

    public byte[] encodeValue(ByteOrder byteOrder, int ... values) {
        return ByteConversions.toBytes(values, byteOrder);
    }

    public byte[] encodeValue(ByteOrder byteOrder, RationalNumber ... values) {
        return ByteConversions.toBytes(values, byteOrder);
    }
}

