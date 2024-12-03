/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.eventusermodel.dummyrecord;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hssf.eventusermodel.dummyrecord.DummyRecordBase;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.util.GenericRecordUtil;

public final class LastCellOfRowDummyRecord
extends DummyRecordBase {
    private final int row;
    private final int lastColumnNumber;

    public LastCellOfRowDummyRecord(int row, int lastColumnNumber) {
        this.row = row;
        this.lastColumnNumber = lastColumnNumber;
    }

    public int getRow() {
        return this.row;
    }

    public int getLastColumnNumber() {
        return this.lastColumnNumber;
    }

    @Override
    public LastCellOfRowDummyRecord copy() {
        return this;
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return null;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("row", this::getRow, "lastColumnNumber", this::getLastColumnNumber);
    }
}

