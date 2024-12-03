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

public class TagInfoSRational
extends TagInfo {
    public TagInfoSRational(String name, int tag, TiffDirectoryType directoryType) {
        super(name, tag, FieldType.SRATIONAL, 1, directoryType);
    }

    public RationalNumber getValue(ByteOrder byteOrder, byte[] bytes) {
        return ByteConversions.toRational(bytes, byteOrder);
    }

    public byte[] encodeValue(ByteOrder byteOrder, RationalNumber value) {
        return ByteConversions.toBytes(value, byteOrder);
    }
}

