/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.fieldtypes;

import java.nio.ByteOrder;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.common.ByteConversions;
import org.apache.commons.imaging.common.RationalNumber;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;

public class FieldTypeRational
extends FieldType {
    public FieldTypeRational(int type, String name) {
        super(type, name, 8);
    }

    @Override
    public Object getValue(TiffField entry) {
        byte[] bytes = entry.getByteArrayValue();
        if (entry.getCount() == 1L) {
            return ByteConversions.toRational(bytes, entry.getByteOrder());
        }
        return ByteConversions.toRationals(bytes, entry.getByteOrder());
    }

    @Override
    public byte[] writeData(Object o, ByteOrder byteOrder) throws ImageWriteException {
        if (o instanceof RationalNumber) {
            return ByteConversions.toBytes((RationalNumber)o, byteOrder);
        }
        if (o instanceof RationalNumber[]) {
            return ByteConversions.toBytes((RationalNumber[])o, byteOrder);
        }
        if (o instanceof Number) {
            Number number = (Number)o;
            RationalNumber rationalNumber = RationalNumber.valueOf(number.doubleValue());
            return ByteConversions.toBytes(rationalNumber, byteOrder);
        }
        if (o instanceof Number[]) {
            Number[] numbers = (Number[])o;
            RationalNumber[] rationalNumbers = new RationalNumber[numbers.length];
            for (int i = 0; i < numbers.length; ++i) {
                Number number = numbers[i];
                rationalNumbers[i] = RationalNumber.valueOf(number.doubleValue());
            }
            return ByteConversions.toBytes(rationalNumbers, byteOrder);
        }
        if (o instanceof double[]) {
            double[] numbers = (double[])o;
            RationalNumber[] rationalNumbers = new RationalNumber[numbers.length];
            for (int i = 0; i < numbers.length; ++i) {
                double number = numbers[i];
                rationalNumbers[i] = RationalNumber.valueOf(number);
            }
            return ByteConversions.toBytes(rationalNumbers, byteOrder);
        }
        throw new ImageWriteException("Invalid data", o);
    }
}

