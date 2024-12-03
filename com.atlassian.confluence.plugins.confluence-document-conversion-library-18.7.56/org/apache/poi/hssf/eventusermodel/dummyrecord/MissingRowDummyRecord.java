/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.eventusermodel.dummyrecord;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hssf.eventusermodel.dummyrecord.DummyRecordBase;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.util.GenericRecordUtil;

public final class MissingRowDummyRecord
extends DummyRecordBase {
    private final int rowNumber;

    public MissingRowDummyRecord(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public int getRowNumber() {
        return this.rowNumber;
    }

    @Override
    public MissingRowDummyRecord copy() {
        return this;
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return null;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("rowNumber", this::getRowNumber);
    }
}

