/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.taginfos;

import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryType;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;

public class TagInfoAscii
extends TagInfo {
    public TagInfoAscii(String name, int tag, int length, TiffDirectoryType directoryType) {
        super(name, tag, FieldType.ASCII, length, directoryType);
    }

    public String[] getValue(ByteOrder byteOrder, byte[] bytes) {
        int nullCount = 0;
        for (int i = 0; i < bytes.length - 1; ++i) {
            if (bytes[i] != 0) continue;
            ++nullCount;
        }
        String[] strings = new String[nullCount + 1];
        int stringsAdded = 0;
        strings[0] = "";
        int nextStringPos = 0;
        for (int i = 0; i < bytes.length; ++i) {
            if (bytes[i] != 0) continue;
            String string = new String(bytes, nextStringPos, i - nextStringPos, StandardCharsets.UTF_8);
            strings[stringsAdded++] = string;
            nextStringPos = i + 1;
        }
        if (nextStringPos < bytes.length) {
            String string = new String(bytes, nextStringPos, bytes.length - nextStringPos, StandardCharsets.UTF_8);
            strings[stringsAdded++] = string;
        }
        return strings;
    }

    public byte[] encodeValue(ByteOrder byteOrder, String ... values) throws ImageWriteException {
        return FieldType.ASCII.writeData(values, byteOrder);
    }
}

