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

public final class SaveRecalcRecord
extends StandardRecord {
    public static final short sid = 95;
    private short field_1_recalc;

    public SaveRecalcRecord() {
    }

    public SaveRecalcRecord(SaveRecalcRecord other) {
        super(other);
        this.field_1_recalc = other.field_1_recalc;
    }

    public SaveRecalcRecord(RecordInputStream in) {
        this.field_1_recalc = in.readShort();
    }

    public void setRecalc(boolean recalc) {
        this.field_1_recalc = (short)(recalc ? 1 : 0);
    }

    public boolean getRecalc() {
        return this.field_1_recalc == 1;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_recalc);
    }

    @Override
    protected int getDataSize() {
        return 2;
    }

    @Override
    public short getSid() {
        return 95;
    }

    @Override
    public SaveRecalcRecord copy() {
        return new SaveRecalcRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.SAVE_RECALC;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("recalc", this::getRecalc);
    }
}

