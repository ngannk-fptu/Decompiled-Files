/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.taginfos;

import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryType;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;

public class TagInfoXpString
extends TagInfo {
    public TagInfoXpString(String name, int tag, TiffDirectoryType directoryType) {
        super(name, tag, FieldType.BYTE, -1, directoryType);
    }

    @Override
    public byte[] encodeValue(FieldType fieldType, Object value, ByteOrder byteOrder) throws ImageWriteException {
        if (!(value instanceof String)) {
            throw new ImageWriteException("Text value not String", value);
        }
        String s = (String)value;
        byte[] bytes = s.getBytes(StandardCharsets.UTF_16LE);
        byte[] paddedBytes = new byte[bytes.length + 2];
        System.arraycopy(bytes, 0, paddedBytes, 0, bytes.length);
        return paddedBytes;
    }

    @Override
    public String getValue(TiffField entry) throws ImageReadException {
        if (entry.getFieldType() != FieldType.BYTE) {
            throw new ImageReadException("Text field not encoded as bytes.");
        }
        byte[] bytes = entry.getByteArrayValue();
        int length = bytes.length >= 2 && bytes[bytes.length - 1] == 0 && bytes[bytes.length - 2] == 0 ? bytes.length - 2 : bytes.length;
        return new String(bytes, 0, length, StandardCharsets.UTF_16LE);
    }
}

