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

public final class CRNCountRecord
extends StandardRecord {
    public static final short sid = 89;
    private static final short DATA_SIZE = 4;
    private int field_1_number_crn_records;
    private int field_2_sheet_table_index;

    public CRNCountRecord(CRNCountRecord other) {
        super(other);
        this.field_1_number_crn_records = other.field_1_number_crn_records;
        this.field_2_sheet_table_index = other.field_2_sheet_table_index;
    }

    public CRNCountRecord(RecordInputStream in) {
        this.field_1_number_crn_records = in.readShort();
        if (this.field_1_number_crn_records < 0) {
            this.field_1_number_crn_records = (short)(-this.field_1_number_crn_records);
        }
        this.field_2_sheet_table_index = in.readShort();
    }

    public int getNumberOfCRNs() {
        return this.field_1_number_crn_records;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort((short)this.field_1_number_crn_records);
        out.writeShort((short)this.field_2_sheet_table_index);
    }

    @Override
    protected int getDataSize() {
        return 4;
    }

    @Override
    public short getSid() {
        return 89;
    }

    @Override
    public CRNCountRecord copy() {
        return new CRNCountRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.CRN_COUNT;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("numberOfCRNs", this::getNumberOfCRNs, "sheetTableIndex", () -> this.field_2_sheet_table_index);
    }
}

