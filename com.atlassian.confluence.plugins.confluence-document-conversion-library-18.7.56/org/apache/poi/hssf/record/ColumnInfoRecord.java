/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianOutput;

public final class ColumnInfoRecord
extends StandardRecord {
    public static final short sid = 125;
    private static final BitField hidden = BitFieldFactory.getInstance(1);
    private static final BitField outlevel = BitFieldFactory.getInstance(1792);
    private static final BitField collapsed = BitFieldFactory.getInstance(4096);
    private int _firstCol;
    private int _lastCol;
    private int _colWidth;
    private int _xfIndex;
    private int _options;
    private int field_6_reserved;

    public ColumnInfoRecord() {
        this.setColumnWidth(2275);
        this._options = 2;
        this._xfIndex = 15;
        this.field_6_reserved = 2;
    }

    public ColumnInfoRecord(ColumnInfoRecord other) {
        super(other);
        this._firstCol = other._firstCol;
        this._lastCol = other._lastCol;
        this._colWidth = other._colWidth;
        this._xfIndex = other._xfIndex;
        this._options = other._options;
        this.field_6_reserved = other.field_6_reserved;
    }

    public ColumnInfoRecord(RecordInputStream in) {
        this._firstCol = in.readUShort();
        this._lastCol = in.readUShort();
        this._colWidth = in.readUShort();
        this._xfIndex = in.readUShort();
        this._options = in.readUShort();
        switch (in.remaining()) {
            case 2: {
                this.field_6_reserved = in.readUShort();
                break;
            }
            case 1: {
                this.field_6_reserved = in.readByte();
                break;
            }
            case 0: {
                this.field_6_reserved = 0;
                break;
            }
            default: {
                throw new IllegalArgumentException("Unusual record size remaining=(" + in.remaining() + ")");
            }
        }
    }

    public void setFirstColumn(int fc) {
        this._firstCol = fc;
    }

    public void setLastColumn(int lc) {
        this._lastCol = lc;
    }

    public void setColumnWidth(int cw) {
        this._colWidth = cw;
    }

    public void setXFIndex(int xfi) {
        this._xfIndex = xfi;
    }

    public void setHidden(boolean ishidden) {
        this._options = hidden.setBoolean(this._options, ishidden);
    }

    public void setOutlineLevel(int olevel) {
        this._options = outlevel.setValue(this._options, olevel);
    }

    public void setCollapsed(boolean isCollapsed) {
        this._options = collapsed.setBoolean(this._options, isCollapsed);
    }

    public int getFirstColumn() {
        return this._firstCol;
    }

    public int getLastColumn() {
        return this._lastCol;
    }

    public int getColumnWidth() {
        return this._colWidth;
    }

    public int getXFIndex() {
        return this._xfIndex;
    }

    public boolean getHidden() {
        return hidden.isSet(this._options);
    }

    public int getOutlineLevel() {
        return outlevel.getValue(this._options);
    }

    public boolean getCollapsed() {
        return collapsed.isSet(this._options);
    }

    public boolean containsColumn(int columnIndex) {
        return this._firstCol <= columnIndex && columnIndex <= this._lastCol;
    }

    public boolean isAdjacentBefore(ColumnInfoRecord other) {
        return this._lastCol == other._firstCol - 1;
    }

    public boolean formatMatches(ColumnInfoRecord other) {
        if (this._xfIndex != other._xfIndex) {
            return false;
        }
        if (this._options != other._options) {
            return false;
        }
        return this._colWidth == other._colWidth;
    }

    @Override
    public short getSid() {
        return 125;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.getFirstColumn());
        out.writeShort(this.getLastColumn());
        out.writeShort(this.getColumnWidth());
        out.writeShort(this.getXFIndex());
        out.writeShort(this._options);
        out.writeShort(this.field_6_reserved);
    }

    @Override
    protected int getDataSize() {
        return 12;
    }

    @Override
    public ColumnInfoRecord copy() {
        return new ColumnInfoRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.COLUMN_INFO;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("firstColumn", this::getFirstColumn, "lastColumn", this::getLastColumn, "columnWidth", this::getColumnWidth, "xfIndex", this::getXFIndex, "options", () -> this._options, "hidden", this::getHidden, "outlineLevel", this::getOutlineLevel, "collapsed", this::getCollapsed);
    }
}

