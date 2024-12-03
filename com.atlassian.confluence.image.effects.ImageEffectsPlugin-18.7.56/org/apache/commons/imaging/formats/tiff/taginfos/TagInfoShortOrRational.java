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

public class TagInfoShortOrRational
extends TagInfo {
    public TagInfoShortOrRational(String name, int tag, int length, TiffDirectoryType directoryType) {
        super(name, tag, FieldType.SHORT_OR_RATIONAL, length, directoryType, false);
    }

    public byte[] encodeValue(ByteOrder byteOrder, short ... values) {
        return ByteConversions.toBytes(values, byteOrder);
    }

    public byte[] encodeValue(ByteOrder byteOrder, RationalNumber ... values) {
        return ByteConversions.toBytes(values, byteOrder);
    }
}

