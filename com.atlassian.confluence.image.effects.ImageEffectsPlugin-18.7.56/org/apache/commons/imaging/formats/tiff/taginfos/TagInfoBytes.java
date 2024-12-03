/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.taginfos;

import java.nio.ByteOrder;
import java.util.List;
import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryType;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;

public class TagInfoBytes
extends TagInfo {
    public TagInfoBytes(String name, int tag, int length, TiffDirectoryType directoryType) {
        super(name, tag, FieldType.BYTE, length, directoryType);
    }

    public TagInfoBytes(String name, int tag, List<FieldType> fieldTypes, int length, TiffDirectoryType directoryType) {
        super(name, tag, fieldTypes, length, directoryType);
    }

    public TagInfoBytes(String name, int tag, FieldType fieldType, int length, TiffDirectoryType directoryType) {
        super(name, tag, fieldType, length, directoryType);
    }

    public byte[] encodeValue(ByteOrder byteOrder, byte ... values) {
        return values;
    }
}

