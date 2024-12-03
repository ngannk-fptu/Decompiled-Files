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

public final class PrintGridlinesRecord
extends StandardRecord {
    public static final short sid = 43;
    private short field_1_print_gridlines;

    public PrintGridlinesRecord() {
    }

    public PrintGridlinesRecord(PrintGridlinesRecord other) {
        super(other);
        this.field_1_print_gridlines = other.field_1_print_gridlines;
    }

    public PrintGridlinesRecord(RecordInputStream in) {
        this.field_1_print_gridlines = in.readShort();
    }

    public void setPrintGridlines(boolean pg) {
        this.field_1_print_gridlines = (short)(pg ? 1 : 0);
    }

    public boolean getPrintGridlines() {
        return this.field_1_print_gridlines == 1;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_print_gridlines);
    }

    @Override
    protected int getDataSize() {
        return 2;
    }

    @Override
    public short getSid() {
        return 43;
    }

    @Override
    public PrintGridlinesRecord copy() {
        return new PrintGridlinesRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.PRINT_GRIDLINES;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("printGridlines", this::getPrintGridlines);
    }
}

