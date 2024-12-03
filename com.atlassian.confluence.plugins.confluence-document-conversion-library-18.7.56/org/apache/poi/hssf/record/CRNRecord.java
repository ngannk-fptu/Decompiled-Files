/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.ss.formula.constant.ConstantValueParser;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianOutput;

public final class CRNRecord
extends StandardRecord {
    public static final short sid = 90;
    private int field_1_last_column_index;
    private int field_2_first_column_index;
    private int field_3_row_index;
    private Object[] field_4_constant_values;

    public CRNRecord(CRNRecord other) {
        super(other);
        this.field_1_last_column_index = other.field_1_last_column_index;
        this.field_2_first_column_index = other.field_2_first_column_index;
        this.field_3_row_index = other.field_3_row_index;
        this.field_4_constant_values = other.field_4_constant_values == null ? null : (Object[])other.field_4_constant_values.clone();
    }

    public CRNRecord(RecordInputStream in) {
        this.field_1_last_column_index = in.readUByte();
        this.field_2_first_column_index = in.readUByte();
        this.field_3_row_index = in.readShort();
        int nValues = this.field_1_last_column_index - this.field_2_first_column_index + 1;
        this.field_4_constant_values = ConstantValueParser.parse(in, nValues);
    }

    public int getNumberOfCRNs() {
        return this.field_1_last_column_index;
    }

    @Override
    protected int getDataSize() {
        return 4 + ConstantValueParser.getEncodedSize(this.field_4_constant_values);
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeByte(this.field_1_last_column_index);
        out.writeByte(this.field_2_first_column_index);
        out.writeShort(this.field_3_row_index);
        ConstantValueParser.encode(out, this.field_4_constant_values);
    }

    @Override
    public short getSid() {
        return 90;
    }

    @Override
    public CRNRecord copy() {
        return new CRNRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.CRN;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("row", () -> this.field_3_row_index, "firstColumn", () -> this.field_2_first_column_index, "lastColumn", () -> this.field_1_last_column_index, "constantValues", () -> this.field_4_constant_values);
    }
}

