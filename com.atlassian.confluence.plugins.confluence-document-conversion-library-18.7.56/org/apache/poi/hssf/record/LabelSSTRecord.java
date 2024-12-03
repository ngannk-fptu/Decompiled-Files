/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hssf.record.CellRecord;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianOutput;

public final class LabelSSTRecord
extends CellRecord {
    public static final short sid = 253;
    private int field_4_sst_index;

    public LabelSSTRecord() {
    }

    public LabelSSTRecord(LabelSSTRecord other) {
        super(other);
        this.field_4_sst_index = other.field_4_sst_index;
    }

    public LabelSSTRecord(RecordInputStream in) {
        super(in);
        this.field_4_sst_index = in.readInt();
    }

    public void setSSTIndex(int index) {
        this.field_4_sst_index = index;
    }

    public int getSSTIndex() {
        return this.field_4_sst_index;
    }

    @Override
    protected String getRecordName() {
        return "LABELSST";
    }

    @Override
    protected void serializeValue(LittleEndianOutput out) {
        out.writeInt(this.getSSTIndex());
    }

    @Override
    protected int getValueDataSize() {
        return 4;
    }

    @Override
    public short getSid() {
        return 253;
    }

    @Override
    public LabelSSTRecord copy() {
        return new LabelSSTRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.LABEL_SST;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("base", () -> super.getGenericProperties(), "sstIndex", this::getSSTIndex);
    }
}

