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

public class TagInfoRationals
extends TagInfo {
    public TagInfoRationals(String name, int tag, int length, TiffDirectoryType directoryType) {
        super(name, tag, FieldType.RATIONAL, length, directoryType);
    }

    public RationalNumber[] getValue(ByteOrder byteOrder, byte[] bytes) {
        return ByteConversions.toRationals(bytes, byteOrder);
    }

    public byte[] encodeValue(ByteOrder byteOrder, RationalNumber ... values) {
        return ByteConversions.toBytes(values, byteOrder);
    }
}

