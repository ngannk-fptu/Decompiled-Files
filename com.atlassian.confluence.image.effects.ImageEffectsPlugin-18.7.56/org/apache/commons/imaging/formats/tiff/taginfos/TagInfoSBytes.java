/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.taginfos;

import java.nio.ByteOrder;
import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryType;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;

public class TagInfoSBytes
extends TagInfo {
    public TagInfoSBytes(String name, int tag, int length, TiffDirectoryType directoryType) {
        super(name, tag, FieldType.SBYTE, length, directoryType);
    }

    public byte[] encodeValue(ByteOrder byteOrder, byte ... values) {
        return values;
    }
}

