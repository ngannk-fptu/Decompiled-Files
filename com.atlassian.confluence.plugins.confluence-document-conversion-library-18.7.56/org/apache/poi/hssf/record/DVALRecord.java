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

public final class DVALRecord
extends StandardRecord {
    public static final short sid = 434;
    private short field_1_options;
    private int field_2_horiz_pos;
    private int field_3_vert_pos;
    private int field_cbo_id;
    private int field_5_dv_no;

    public DVALRecord() {
        this.field_cbo_id = -1;
        this.field_5_dv_no = 0;
    }

    public DVALRecord(DVALRecord other) {
        super(other);
        this.field_1_options = other.field_1_options;
        this.field_2_horiz_pos = other.field_2_horiz_pos;
        this.field_3_vert_pos = other.field_3_vert_pos;
        this.field_cbo_id = other.field_cbo_id;
        this.field_5_dv_no = other.field_5_dv_no;
    }

    public DVALRecord(RecordInputStream in) {
        this.field_1_options = in.readShort();
        this.field_2_horiz_pos = in.readInt();
        this.field_3_vert_pos = in.readInt();
        this.field_cbo_id = in.readInt();
        this.field_5_dv_no = in.readInt();
    }

    public void setOptions(short options) {
        this.field_1_options = options;
    }

    public void setHorizontalPos(int horiz_pos) {
        this.field_2_horiz_pos = horiz_pos;
    }

    public void setVerticalPos(int vert_pos) {
        this.field_3_vert_pos = vert_pos;
    }

    public void setObjectID(int cboID) {
        this.field_cbo_id = cboID;
    }

    public void setDVRecNo(int dvNo) {
        this.field_5_dv_no = dvNo;
    }

    public short getOptions() {
        return this.field_1_options;
    }

    public int getHorizontalPos() {
        return this.field_2_horiz_pos;
    }

    public int getVerticalPos() {
        return this.field_3_vert_pos;
    }

    public int getObjectID() {
        return this.field_cbo_id;
    }

    public int getDVRecNo() {
        return this.field_5_dv_no;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.getOptions());
        out.writeInt(this.getHorizontalPos());
        out.writeInt(this.getVerticalPos());
        out.writeInt(this.getObjectID());
        out.writeInt(this.getDVRecNo());
    }

    @Override
    protected int getDataSize() {
        return 18;
    }

    @Override
    public short getSid() {
        return 434;
    }

    @Override
    public DVALRecord copy() {
        return new DVALRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.DVAL;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("options", this::getOptions, "horizPos", this::getHorizontalPos, "vertPos", this::getVerticalPos, "comboObjectID", this::getObjectID, "dvRecordsNumber", this::getDVRecNo);
    }
}

