/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.taginfos;

import java.nio.ByteOrder;
import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryType;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;

public class TagInfoSByte
extends TagInfo {
    public TagInfoSByte(String name, int tag, TiffDirectoryType directoryType) {
        super(name, tag, FieldType.SBYTE, 1, directoryType);
    }

    public byte[] encodeValue(ByteOrder byteOrder, byte value) {
        return new byte[]{value};
    }
}

