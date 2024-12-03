/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianOutput;

public final class RefreshAllRecord
extends StandardRecord {
    public static final short sid = 439;
    private static final BitField refreshFlag = BitFieldFactory.getInstance(1);
    private int _options;

    private RefreshAllRecord(int options) {
        this._options = options;
    }

    private RefreshAllRecord(RefreshAllRecord other) {
        super(other);
        this._options = other._options;
    }

    public RefreshAllRecord(RecordInputStream in) {
        this(in.readUShort());
    }

    public RefreshAllRecord(boolean refreshAll) {
        this(0);
        this.setRefreshAll(refreshAll);
    }

    public void setRefreshAll(boolean refreshAll) {
        this._options = refreshFlag.setBoolean(this._options, refreshAll);
    }

    public boolean getRefreshAll() {
        return refreshFlag.isSet(this._options);
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this._options);
    }

    @Override
    protected int getDataSize() {
        return 2;
    }

    @Override
    public short getSid() {
        return 439;
    }

    @Override
    public RefreshAllRecord copy() {
        return new RefreshAllRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.REFRESH_ALL;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("options", () -> this._options, "refreshAll", this::getRefreshAll);
    }
}

