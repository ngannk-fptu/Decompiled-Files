/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianOutput;

public final class RecalcIdRecord
extends StandardRecord {
    public static final short sid = 449;
    private final int _reserved0;
    private int _engineId;

    public RecalcIdRecord() {
        this._reserved0 = 0;
        this._engineId = 0;
    }

    public RecalcIdRecord(RecalcIdRecord other) {
        this._reserved0 = other._reserved0;
        this._engineId = other._engineId;
    }

    public RecalcIdRecord(RecordInputStream in) {
        in.readUShort();
        this._reserved0 = in.readUShort();
        this._engineId = in.readInt();
    }

    public boolean isNeeded() {
        return true;
    }

    public void setEngineId(int val) {
        this._engineId = val;
    }

    public int getEngineId() {
        return this._engineId;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(449);
        out.writeShort(this._reserved0);
        out.writeInt(this._engineId);
    }

    @Override
    protected int getDataSize() {
        return 8;
    }

    @Override
    public short getSid() {
        return 449;
    }

    @Override
    public RecalcIdRecord copy() {
        return new RecalcIdRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.RECALC_ID;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("reserved0", () -> this._reserved0, "engineId", this::getEngineId);
    }
}

