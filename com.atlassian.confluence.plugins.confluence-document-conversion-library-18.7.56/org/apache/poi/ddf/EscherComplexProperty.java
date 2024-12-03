/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ddf;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.ddf.EscherProperty;
import org.apache.poi.ddf.EscherPropertyTypes;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;

public class EscherComplexProperty
extends EscherProperty {
    private static final int DEFAULT_MAX_RECORD_LENGTH = 100000000;
    private static int MAX_RECORD_LENGTH = 100000000;
    private byte[] complexData;

    public static void setMaxRecordLength(int length) {
        MAX_RECORD_LENGTH = length;
    }

    public static int getMaxRecordLength() {
        return MAX_RECORD_LENGTH;
    }

    public EscherComplexProperty(short id, int complexSize) {
        super((short)(id | 0x8000));
        this.complexData = IOUtils.safelyAllocate(complexSize, MAX_RECORD_LENGTH);
    }

    public EscherComplexProperty(short propertyNumber, boolean isBlipId, int complexSize) {
        this((short)(propertyNumber | (isBlipId ? 16384 : 0)), complexSize);
    }

    public EscherComplexProperty(EscherPropertyTypes type, boolean isBlipId, int complexSize) {
        this((short)(type.propNumber | (isBlipId ? 16384 : 0)), complexSize);
    }

    @Override
    public int serializeSimplePart(byte[] data, int pos) {
        LittleEndian.putShort(data, pos, this.getId());
        LittleEndian.putInt(data, pos + 2, this.complexData.length);
        return 6;
    }

    @Override
    public int serializeComplexPart(byte[] data, int pos) {
        System.arraycopy(this.complexData, 0, data, pos, this.complexData.length);
        return this.complexData.length;
    }

    public byte[] getComplexData() {
        return this.complexData;
    }

    public int setComplexData(byte[] complexData) {
        return this.setComplexData(complexData, 0);
    }

    public int setComplexData(byte[] complexData, int offset) {
        if (complexData == null) {
            return 0;
        }
        int copySize = Math.max(0, Math.min(this.complexData.length, complexData.length - offset));
        System.arraycopy(complexData, offset, this.complexData, 0, copySize);
        return copySize;
    }

    protected void resizeComplexData(int newSize) {
        this.resizeComplexData(newSize, Integer.MAX_VALUE);
    }

    protected void resizeComplexData(int newSize, int copyLen) {
        if (newSize == this.complexData.length) {
            return;
        }
        byte[] newArray = IOUtils.safelyAllocate(newSize, MAX_RECORD_LENGTH);
        System.arraycopy(this.complexData, 0, newArray, 0, Math.min(Math.min(this.complexData.length, copyLen), newSize));
        this.complexData = newArray;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EscherComplexProperty)) {
            return false;
        }
        EscherComplexProperty escherComplexProperty = (EscherComplexProperty)o;
        return Arrays.equals(this.complexData, escherComplexProperty.complexData);
    }

    @Override
    public int getPropertySize() {
        return 6 + this.complexData.length;
    }

    public int hashCode() {
        return Arrays.deepHashCode(new Object[]{this.complexData, this.getId()});
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("base", () -> super.getGenericProperties(), "data", this::getComplexData);
    }
}

