/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record.pivottable;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianOutput;

public final class ViewSourceRecord
extends StandardRecord {
    public static final short sid = 227;
    private int vs;

    public ViewSourceRecord(ViewSourceRecord other) {
        super(other);
        this.vs = other.vs;
    }

    public ViewSourceRecord(RecordInputStream in) {
        this.vs = in.readShort();
    }

    @Override
    protected void serialize(LittleEndianOutput out) {
        out.writeShort(this.vs);
    }

    @Override
    protected int getDataSize() {
        return 2;
    }

    @Override
    public short getSid() {
        return 227;
    }

    @Override
    public ViewSourceRecord copy() {
        return new ViewSourceRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.VIEW_SOURCE;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("vs", () -> this.vs);
    }
}

