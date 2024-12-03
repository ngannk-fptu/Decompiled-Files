/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.taginfos;

import java.nio.ByteOrder;
import org.apache.commons.imaging.common.ByteConversions;
import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryType;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;

public class TagInfoDoubles
extends TagInfo {
    public TagInfoDoubles(String name, int tag, int length, TiffDirectoryType directoryType) {
        super(name, tag, FieldType.DOUBLE, length, directoryType);
    }

    public double[] getValue(ByteOrder byteOrder, byte[] bytes) {
        return ByteConversions.toDoubles(bytes, byteOrder);
    }

    public byte[] encodeValue(ByteOrder byteOrder, double ... values) {
        return ByteConversions.toBytes(values, byteOrder);
    }
}

