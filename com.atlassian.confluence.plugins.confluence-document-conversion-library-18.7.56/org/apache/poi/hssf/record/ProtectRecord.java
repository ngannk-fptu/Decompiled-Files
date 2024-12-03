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

public final class ProtectRecord
extends StandardRecord {
    public static final short sid = 18;
    private static final BitField protectFlag = BitFieldFactory.getInstance(1);
    private int _options;

    private ProtectRecord(int options) {
        this._options = options;
    }

    private ProtectRecord(ProtectRecord other) {
        super(other);
        this._options = other._options;
    }

    public ProtectRecord(boolean isProtected) {
        this(0);
        this.setProtect(isProtected);
    }

    public ProtectRecord(RecordInputStream in) {
        this(in.readShort());
    }

    public void setProtect(boolean protect) {
        this._options = protectFlag.setBoolean(this._options, protect);
    }

    public boolean getProtect() {
        return protectFlag.isSet(this._options);
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
        return 18;
    }

    @Override
    public ProtectRecord copy() {
        return new ProtectRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.PROTECT;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("options", () -> this._options, "protect", this::getProtect);
    }
}

