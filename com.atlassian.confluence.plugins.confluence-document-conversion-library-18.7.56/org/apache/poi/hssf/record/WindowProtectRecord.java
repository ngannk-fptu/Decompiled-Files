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

public final class WindowProtectRecord
extends StandardRecord {
    public static final short sid = 25;
    private static final BitField settingsProtectedFlag = BitFieldFactory.getInstance(1);
    private int _options;

    public WindowProtectRecord(int options) {
        this._options = options;
    }

    public WindowProtectRecord(WindowProtectRecord other) {
        super(other);
        this._options = other._options;
    }

    public WindowProtectRecord(RecordInputStream in) {
        this(in.readUShort());
    }

    public WindowProtectRecord(boolean protect) {
        this(0);
        this.setProtect(protect);
    }

    public void setProtect(boolean protect) {
        this._options = settingsProtectedFlag.setBoolean(this._options, protect);
    }

    public boolean getProtect() {
        return settingsProtectedFlag.isSet(this._options);
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
        return 25;
    }

    @Override
    public WindowProtectRecord copy() {
        return new WindowProtectRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.WINDOW_PROTECT;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("options", () -> this._options, "protect", this::getProtect);
    }
}

