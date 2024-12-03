/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.fieldtypes;

import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;

public class FieldTypeAscii
extends FieldType {
    public FieldTypeAscii(int type, String name) {
        super(type, name, 1);
    }

    @Override
    public Object getValue(TiffField entry) {
        byte[] bytes = entry.getByteArrayValue();
        int nullCount = 1;
        for (int i = 0; i < bytes.length - 1; ++i) {
            if (bytes[i] != 0) continue;
            ++nullCount;
        }
        String[] strings = new String[nullCount];
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
        if (strings.length == 1) {
            return strings[0];
        }
        return strings;
    }

    @Override
    public byte[] writeData(Object o, ByteOrder byteOrder) throws ImageWriteException {
        if (o instanceof byte[]) {
            byte[] bytes = (byte[])o;
            byte[] result = new byte[bytes.length + 1];
            System.arraycopy(bytes, 0, result, 0, bytes.length);
            result[result.length - 1] = 0;
            return result;
        }
        if (o instanceof String) {
            byte[] bytes = ((String)o).getBytes(StandardCharsets.UTF_8);
            byte[] result = new byte[bytes.length + 1];
            System.arraycopy(bytes, 0, result, 0, bytes.length);
            result[result.length - 1] = 0;
            return result;
        }
        if (o instanceof String[]) {
            String[] strings = (String[])o;
            int totalLength = 0;
            for (String string : strings) {
                byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
                totalLength += bytes.length + 1;
            }
            byte[] result = new byte[totalLength];
            int position = 0;
            for (String string : strings) {
                byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
                System.arraycopy(bytes, 0, result, position, bytes.length);
                position += bytes.length + 1;
            }
            return result;
        }
        throw new ImageWriteException("Unknown data type: " + o);
    }
}

