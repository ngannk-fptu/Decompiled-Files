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

public final class PrintHeadersRecord
extends StandardRecord {
    public static final short sid = 42;
    private short field_1_print_headers;

    public PrintHeadersRecord() {
    }

    public PrintHeadersRecord(PrintHeadersRecord other) {
        super(other);
        this.field_1_print_headers = other.field_1_print_headers;
    }

    public PrintHeadersRecord(RecordInputStream in) {
        this.field_1_print_headers = in.readShort();
    }

    public void setPrintHeaders(boolean p) {
        this.field_1_print_headers = p ? (short)1 : 0;
    }

    public boolean getPrintHeaders() {
        return this.field_1_print_headers == 1;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_print_headers);
    }

    @Override
    protected int getDataSize() {
        return 2;
    }

    @Override
    public short getSid() {
        return 42;
    }

    @Override
    public PrintHeadersRecord copy() {
        return new PrintHeadersRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.PRINT_HEADERS;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("printHeaders", this::getPrintHeaders);
    }
}

