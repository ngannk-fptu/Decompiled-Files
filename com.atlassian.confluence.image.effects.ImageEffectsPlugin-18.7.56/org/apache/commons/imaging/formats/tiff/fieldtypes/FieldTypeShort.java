/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.fieldtypes;

import java.nio.ByteOrder;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.common.ByteConversions;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;

public class FieldTypeShort
extends FieldType {
    public FieldTypeShort(int type, String name) {
        super(type, name, 2);
    }

    @Override
    public Object getValue(TiffField entry) {
        byte[] bytes = entry.getByteArrayValue();
        if (entry.getCount() == 1L) {
            return ByteConversions.toShort(bytes, entry.getByteOrder());
        }
        return ByteConversions.toShorts(bytes, entry.getByteOrder());
    }

    @Override
    public byte[] writeData(Object o, ByteOrder byteOrder) throws ImageWriteException {
        if (o instanceof Short) {
            return ByteConversions.toBytes((Short)o, byteOrder);
        }
        if (o instanceof short[]) {
            short[] numbers = (short[])o;
            return ByteConversions.toBytes(numbers, byteOrder);
        }
        if (o instanceof Short[]) {
            Short[] numbers = (Short[])o;
            short[] values = new short[numbers.length];
            for (int i = 0; i < values.length; ++i) {
                values[i] = numbers[i];
            }
            return ByteConversions.toBytes(values, byteOrder);
        }
        throw new ImageWriteException("Invalid data", o);
    }
}

