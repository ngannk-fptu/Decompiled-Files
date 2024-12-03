/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.fieldtypes;

import java.nio.ByteOrder;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.common.ByteConversions;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;

public class FieldTypeDouble
extends FieldType {
    public FieldTypeDouble(int type, String name) {
        super(type, name, 8);
    }

    @Override
    public Object getValue(TiffField entry) {
        byte[] bytes = entry.getByteArrayValue();
        if (entry.getCount() == 1L) {
            return ByteConversions.toDouble(bytes, entry.getByteOrder());
        }
        return ByteConversions.toDoubles(bytes, entry.getByteOrder());
    }

    @Override
    public byte[] writeData(Object o, ByteOrder byteOrder) throws ImageWriteException {
        if (o instanceof Double) {
            return ByteConversions.toBytes((Double)o, byteOrder);
        }
        if (o instanceof double[]) {
            double[] numbers = (double[])o;
            return ByteConversions.toBytes(numbers, byteOrder);
        }
        if (o instanceof Double[]) {
            Double[] numbers = (Double[])o;
            double[] values = new double[numbers.length];
            for (int i = 0; i < values.length; ++i) {
                values[i] = numbers[i];
            }
            return ByteConversions.toBytes(values, byteOrder);
        }
        throw new ImageWriteException("Invalid data", o);
    }
}

