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

public final class DSFRecord
extends StandardRecord {
    public static final short sid = 353;
    private static final BitField biff5BookStreamFlag = BitFieldFactory.getInstance(1);
    private int _options;

    private DSFRecord(DSFRecord other) {
        super(other);
        this._options = other._options;
    }

    private DSFRecord(int options) {
        this._options = options;
    }

    public DSFRecord(boolean isBiff5BookStreamPresent) {
        this(0);
        this._options = biff5BookStreamFlag.setBoolean(0, isBiff5BookStreamPresent);
    }

    public DSFRecord(RecordInputStream in) {
        this(in.readShort());
    }

    public boolean isBiff5BookStreamPresent() {
        return biff5BookStreamFlag.isSet(this._options);
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
        return 353;
    }

    @Override
    public DSFRecord copy() {
        return new DSFRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.DSF;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("options", () -> this._options, "biff5BookStreamPresent", this::isBiff5BookStreamPresent);
    }
}

