/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.fieldtypes;

import java.nio.ByteOrder;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.common.ByteConversions;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;

public class FieldTypeLong
extends FieldType {
    public FieldTypeLong(int type, String name) {
        super(type, name, 4);
    }

    @Override
    public Object getValue(TiffField entry) {
        byte[] bytes = entry.getByteArrayValue();
        if (entry.getCount() == 1L) {
            return ByteConversions.toInt(bytes, entry.getByteOrder());
        }
        return ByteConversions.toInts(bytes, entry.getByteOrder());
    }

    @Override
    public byte[] writeData(Object o, ByteOrder byteOrder) throws ImageWriteException {
        if (o instanceof Integer) {
            return ByteConversions.toBytes((Integer)o, byteOrder);
        }
        if (o instanceof int[]) {
            int[] numbers = (int[])o;
            return ByteConversions.toBytes(numbers, byteOrder);
        }
        if (o instanceof Integer[]) {
            Integer[] numbers = (Integer[])o;
            int[] values = new int[numbers.length];
            for (int i = 0; i < values.length; ++i) {
                values[i] = numbers[i];
            }
            return ByteConversions.toBytes(values, byteOrder);
        }
        throw new ImageWriteException("Invalid data", o);
    }
}

