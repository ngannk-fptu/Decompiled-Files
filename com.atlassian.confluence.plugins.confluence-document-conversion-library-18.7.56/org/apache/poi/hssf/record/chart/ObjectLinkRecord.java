/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record.chart;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianOutput;

public final class ObjectLinkRecord
extends StandardRecord {
    public static final short sid = 4135;
    public static final short ANCHOR_ID_CHART_TITLE = 1;
    public static final short ANCHOR_ID_Y_AXIS = 2;
    public static final short ANCHOR_ID_X_AXIS = 3;
    public static final short ANCHOR_ID_SERIES_OR_POINT = 4;
    public static final short ANCHOR_ID_Z_AXIS = 7;
    private short field_1_anchorId;
    private short field_2_link1;
    private short field_3_link2;

    public ObjectLinkRecord() {
    }

    public ObjectLinkRecord(ObjectLinkRecord other) {
        super(other);
        this.field_1_anchorId = other.field_1_anchorId;
        this.field_2_link1 = other.field_2_link1;
        this.field_3_link2 = other.field_3_link2;
    }

    public ObjectLinkRecord(RecordInputStream in) {
        this.field_1_anchorId = in.readShort();
        this.field_2_link1 = in.readShort();
        this.field_3_link2 = in.readShort();
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_anchorId);
        out.writeShort(this.field_2_link1);
        out.writeShort(this.field_3_link2);
    }

    @Override
    protected int getDataSize() {
        return 6;
    }

    @Override
    public short getSid() {
        return 4135;
    }

    @Override
    public ObjectLinkRecord copy() {
        return new ObjectLinkRecord(this);
    }

    public short getAnchorId() {
        return this.field_1_anchorId;
    }

    public void setAnchorId(short field_1_anchorId) {
        this.field_1_anchorId = field_1_anchorId;
    }

    public short getLink1() {
        return this.field_2_link1;
    }

    public void setLink1(short field_2_link1) {
        this.field_2_link1 = field_2_link1;
    }

    public short getLink2() {
        return this.field_3_link2;
    }

    public void setLink2(short field_3_link2) {
        this.field_3_link2 = field_3_link2;
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.OBJECT_LINK;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("anchorId", GenericRecordUtil.getEnumBitsAsString(this::getAnchorId, new int[]{1, 2, 3, 4, 7}, new String[]{"CHART_TITLE", "Y_AXIS", "X_AXIS", "SERIES_OR_POINT", "Z_AXIS"}), "link1", this::getLink1, "link2", this::getLink2);
    }
}

