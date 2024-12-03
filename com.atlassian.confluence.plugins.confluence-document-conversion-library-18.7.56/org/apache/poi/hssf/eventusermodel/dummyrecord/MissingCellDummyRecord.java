/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.eventusermodel.dummyrecord;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hssf.eventusermodel.dummyrecord.DummyRecordBase;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.util.GenericRecordUtil;

public final class MissingCellDummyRecord
extends DummyRecordBase {
    private final int row;
    private final int column;

    public MissingCellDummyRecord(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return this.row;
    }

    public int getColumn() {
        return this.column;
    }

    @Override
    public MissingCellDummyRecord copy() {
        return this;
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return null;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("row", this::getRow, "column", this::getColumn);
    }
}

