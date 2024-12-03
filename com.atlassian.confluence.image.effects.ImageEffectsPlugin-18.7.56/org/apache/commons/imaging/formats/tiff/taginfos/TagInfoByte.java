/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.taginfos;

import java.nio.ByteOrder;
import java.util.List;
import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryType;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;

public class TagInfoByte
extends TagInfo {
    public TagInfoByte(String name, int tag, TiffDirectoryType directoryType) {
        super(name, tag, FieldType.BYTE, 1, directoryType);
    }

    public TagInfoByte(String name, int tag, List<FieldType> fieldTypes, TiffDirectoryType directoryType) {
        super(name, tag, fieldTypes, 1, directoryType);
    }

    public TagInfoByte(String name, int tag, FieldType fieldType, TiffDirectoryType directoryType) {
        super(name, tag, fieldType, 1, directoryType);
    }

    public byte[] encodeValue(ByteOrder byteOrder, byte value) {
        return new byte[]{value};
    }
}

