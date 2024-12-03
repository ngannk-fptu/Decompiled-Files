/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.taginfos;

import java.nio.ByteOrder;
import org.apache.commons.imaging.common.ByteConversions;
import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryType;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;

public class TagInfoDouble
extends TagInfo {
    public TagInfoDouble(String name, int tag, TiffDirectoryType directoryType) {
        super(name, tag, FieldType.DOUBLE, 1, directoryType);
    }

    public double getValue(ByteOrder byteOrder, byte[] bytes) {
        return ByteConversions.toDouble(bytes, byteOrder);
    }

    public byte[] encodeValue(ByteOrder byteOrder, double value) {
        return ByteConversions.toBytes(value, byteOrder);
    }
}

