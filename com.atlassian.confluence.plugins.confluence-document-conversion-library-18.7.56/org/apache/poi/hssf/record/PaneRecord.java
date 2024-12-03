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

public final class PaneRecord
extends StandardRecord {
    public static final short sid = 65;
    public static final short ACTIVE_PANE_LOWER_RIGHT = 0;
    public static final short ACTIVE_PANE_UPPER_RIGHT = 1;
    public static final short ACTIVE_PANE_LOWER_LEFT = 2;
    public static final short ACTIVE_PANE_UPPER_LEFT = 3;
    private short field_1_x;
    private short field_2_y;
    private short field_3_topRow;
    private short field_4_leftColumn;
    private short field_5_activePane;

    public PaneRecord() {
    }

    public PaneRecord(PaneRecord other) {
        super(other);
        this.field_1_x = other.field_1_x;
        this.field_2_y = other.field_2_y;
        this.field_3_topRow = other.field_3_topRow;
        this.field_4_leftColumn = other.field_4_leftColumn;
        this.field_5_activePane = other.field_5_activePane;
    }

    public PaneRecord(RecordInputStream in) {
        this.field_1_x = in.readShort();
        this.field_2_y = in.readShort();
        this.field_3_topRow = in.readShort();
        this.field_4_leftColumn = in.readShort();
        this.field_5_activePane = in.readShort();
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_x);
        out.writeShort(this.field_2_y);
        out.writeShort(this.field_3_topRow);
        out.writeShort(this.field_4_leftColumn);
        out.writeShort(this.field_5_activePane);
    }

    @Override
    protected int getDataSize() {
        return 10;
    }

    @Override
    public short getSid() {
        return 65;
    }

    @Override
    public PaneRecord copy() {
        return new PaneRecord(this);
    }

    public short getX() {
        return this.field_1_x;
    }

    public void setX(short field_1_x) {
        this.field_1_x = field_1_x;
    }

    public short getY() {
        return this.field_2_y;
    }

    public void setY(short field_2_y) {
        this.field_2_y = field_2_y;
    }

    public short getTopRow() {
        return this.field_3_topRow;
    }

    public void setTopRow(short field_3_topRow) {
        this.field_3_topRow = field_3_topRow;
    }

    public short getLeftColumn() {
        return this.field_4_leftColumn;
    }

    public void setLeftColumn(short field_4_leftColumn) {
        this.field_4_leftColumn = field_4_leftColumn;
    }

    public short getActivePane() {
        return this.field_5_activePane;
    }

    public void setActivePane(short field_5_activePane) {
        this.field_5_activePane = field_5_activePane;
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.PANE;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("x", this::getX, "y", this::getY, "topRow", this::getTopRow, "leftColumn", this::getLeftColumn, "activePane", GenericRecordUtil.getEnumBitsAsString(this::getActivePane, new int[]{0, 1, 2, 3}, new String[]{"LOWER_RIGHT", "UPPER_RIGHT", "LOWER_LEFT", "UPPER_LEFT"}));
    }
}

