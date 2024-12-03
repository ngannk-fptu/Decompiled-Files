/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.fieldtypes;

import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldTypeAscii;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldTypeByte;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldTypeDouble;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldTypeFloat;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldTypeLong;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldTypeRational;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldTypeShort;

public abstract class FieldType {
    public static final FieldTypeByte BYTE = new FieldTypeByte(1, "Byte");
    public static final FieldTypeAscii ASCII = new FieldTypeAscii(2, "ASCII");
    public static final FieldTypeShort SHORT = new FieldTypeShort(3, "Short");
    public static final FieldTypeLong LONG = new FieldTypeLong(4, "Long");
    public static final FieldTypeRational RATIONAL = new FieldTypeRational(5, "Rational");
    public static final FieldTypeByte SBYTE = new FieldTypeByte(6, "SByte");
    public static final FieldTypeByte UNDEFINED = new FieldTypeByte(7, "Undefined");
    public static final FieldTypeShort SSHORT = new FieldTypeShort(8, "SShort");
    public static final FieldTypeLong SLONG = new FieldTypeLong(9, "SLong");
    public static final FieldTypeRational SRATIONAL = new FieldTypeRational(10, "SRational");
    public static final FieldTypeFloat FLOAT = new FieldTypeFloat(11, "Float");
    public static final FieldTypeDouble DOUBLE = new FieldTypeDouble(12, "Double");
    public static final FieldTypeLong IFD = new FieldTypeLong(13, "IFD");
    private final int type;
    private final String name;
    private final int elementSize;
    public static final List<FieldType> ANY = Collections.unmodifiableList(Arrays.asList(BYTE, ASCII, SHORT, LONG, RATIONAL, SBYTE, UNDEFINED, SSHORT, SLONG, SRATIONAL, FLOAT, DOUBLE, IFD));
    public static final List<FieldType> SHORT_OR_LONG = Collections.unmodifiableList(Arrays.asList(SHORT, LONG));
    public static final List<FieldType> SHORT_OR_RATIONAL = Collections.unmodifiableList(Arrays.asList(SHORT, RATIONAL));
    public static final List<FieldType> SHORT_OR_LONG_OR_RATIONAL = Collections.unmodifiableList(Arrays.asList(SHORT, LONG, RATIONAL));
    public static final List<FieldType> LONG_OR_SHORT = Collections.unmodifiableList(Arrays.asList(SHORT, LONG));
    public static final List<FieldType> BYTE_OR_SHORT = Collections.unmodifiableList(Arrays.asList(SHORT, BYTE));
    public static final List<FieldType> LONG_OR_IFD = Collections.unmodifiableList(Arrays.asList(LONG, IFD));
    public static final List<FieldType> ASCII_OR_RATIONAL = Collections.unmodifiableList(Arrays.asList(ASCII, RATIONAL));
    public static final List<FieldType> ASCII_OR_BYTE = Collections.unmodifiableList(Arrays.asList(ASCII, BYTE));

    protected FieldType(int type, String name, int elementSize) {
        this.type = type;
        this.name = name;
        this.elementSize = elementSize;
    }

    public int getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public int getSize() {
        return this.elementSize;
    }

    public static FieldType getFieldType(int type) throws ImageReadException {
        for (FieldType fieldType : ANY) {
            if (fieldType.getType() != type) continue;
            return fieldType;
        }
        throw new ImageReadException("Field type " + type + " is unsupported");
    }

    public abstract Object getValue(TiffField var1);

    public abstract byte[] writeData(Object var1, ByteOrder var2) throws ImageWriteException;
}

