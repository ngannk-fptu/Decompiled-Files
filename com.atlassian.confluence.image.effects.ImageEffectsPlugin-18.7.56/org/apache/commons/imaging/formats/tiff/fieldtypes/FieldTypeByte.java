/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.fieldtypes;

import java.nio.ByteOrder;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;

public class FieldTypeByte
extends FieldType {
    public FieldTypeByte(int type, String name) {
        super(type, name, 1);
    }

    @Override
    public Object getValue(TiffField entry) {
        byte[] bytes = entry.getByteArrayValue();
        if (entry.getCount() == 1L) {
            return bytes[0];
        }
        return bytes;
    }

    @Override
    public byte[] writeData(Object o, ByteOrder byteOrder) throws ImageWriteException {
        if (o instanceof Byte) {
            return new byte[]{(Byte)o};
        }
        if (o instanceof byte[]) {
            return (byte[])o;
        }
        throw new ImageWriteException("Invalid data", o);
    }
}

