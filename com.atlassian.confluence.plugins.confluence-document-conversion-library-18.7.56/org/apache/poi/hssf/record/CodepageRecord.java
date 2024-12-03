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

public final class CodepageRecord
extends StandardRecord {
    public static final short sid = 66;
    public static final short CODEPAGE = 1200;
    private short field_1_codepage;

    public CodepageRecord() {
    }

    public CodepageRecord(CodepageRecord other) {
        super(other);
        this.field_1_codepage = other.field_1_codepage;
    }

    public CodepageRecord(RecordInputStream in) {
        this.field_1_codepage = in.readShort();
    }

    public void setCodepage(short cp) {
        this.field_1_codepage = cp;
    }

    public short getCodepage() {
        return this.field_1_codepage;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.getCodepage());
    }

    @Override
    protected int getDataSize() {
        return 2;
    }

    @Override
    public short getSid() {
        return 66;
    }

    @Override
    public CodepageRecord copy() {
        return new CodepageRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.CODEPAGE;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("codepage", this::getCodepage);
    }
}

