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

public final class InterfaceHdrRecord
extends StandardRecord {
    public static final short sid = 225;
    public static final int CODEPAGE = 1200;
    private final int _codepage;

    public InterfaceHdrRecord(InterfaceHdrRecord other) {
        super(other);
        this._codepage = other._codepage;
    }

    public InterfaceHdrRecord(int codePage) {
        this._codepage = codePage;
    }

    public InterfaceHdrRecord(RecordInputStream in) {
        this._codepage = in.readShort();
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this._codepage);
    }

    @Override
    protected int getDataSize() {
        return 2;
    }

    @Override
    public short getSid() {
        return 225;
    }

    @Override
    public InterfaceHdrRecord copy() {
        return new InterfaceHdrRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.INTERFACE_HDR;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("codePage", () -> this._codepage);
    }
}

