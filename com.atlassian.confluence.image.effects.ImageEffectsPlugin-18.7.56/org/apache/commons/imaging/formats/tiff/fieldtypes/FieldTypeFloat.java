/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.fieldtypes;

import java.nio.ByteOrder;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.common.ByteConversions;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;

public class FieldTypeFloat
extends FieldType {
    public FieldTypeFloat(int type, String name) {
        super(type, name, 4);
    }

    @Override
    public Object getValue(TiffField entry) {
        byte[] bytes = entry.getByteArrayValue();
        if (entry.getCount() == 1L) {
            return Float.valueOf(ByteConversions.toFloat(bytes, entry.getByteOrder()));
        }
        return ByteConversions.toFloats(bytes, entry.getByteOrder());
    }

    @Override
    public byte[] writeData(Object o, ByteOrder byteOrder) throws ImageWriteException {
        if (o instanceof Float) {
            return ByteConversions.toBytes(((Float)o).floatValue(), byteOrder);
        }
        if (o instanceof float[]) {
            float[] numbers = (float[])o;
            return ByteConversions.toBytes(numbers, byteOrder);
        }
        if (o instanceof Float[]) {
            Float[] numbers = (Float[])o;
            float[] values = new float[numbers.length];
            for (int i = 0; i < values.length; ++i) {
                values[i] = numbers[i].floatValue();
            }
            return ByteConversions.toBytes(values, byteOrder);
        }
        throw new ImageWriteException("Invalid data", o);
    }
}

