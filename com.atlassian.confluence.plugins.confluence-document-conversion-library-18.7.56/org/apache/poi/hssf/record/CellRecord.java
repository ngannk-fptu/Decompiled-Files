/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hssf.record.CellValueRecordInterface;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianOutput;

public abstract class CellRecord
extends StandardRecord
implements CellValueRecordInterface {
    private int _rowIndex;
    private int _columnIndex;
    private int _formatIndex;

    protected CellRecord() {
    }

    protected CellRecord(CellRecord other) {
        super(other);
        this._rowIndex = other.getRow();
        this._columnIndex = other.getColumn();
        this._formatIndex = other.getXFIndex();
    }

    protected CellRecord(RecordInputStream in) {
        this._rowIndex = in.readUShort();
        this._columnIndex = in.readUShort();
        this._formatIndex = in.readUShort();
    }

    @Override
    public final void setRow(int row) {
        this._rowIndex = row;
    }

    @Override
    public final void setColumn(short col) {
        this._columnIndex = col;
    }

    @Override
    public final void setXFIndex(short xf) {
        this._formatIndex = xf;
    }

    @Override
    public final int getRow() {
        return this._rowIndex;
    }

    @Override
    public final short getColumn() {
        return (short)this._columnIndex;
    }

    @Override
    public final short getXFIndex() {
        return (short)this._formatIndex;
    }

    protected abstract String getRecordName();

    protected abstract void serializeValue(LittleEndianOutput var1);

    protected abstract int getValueDataSize();

    @Override
    public final void serialize(LittleEndianOutput out) {
        out.writeShort(this.getRow());
        out.writeShort(this.getColumn());
        out.writeShort(this.getXFIndex());
        this.serializeValue(out);
    }

    @Override
    protected final int getDataSize() {
        return 6 + this.getValueDataSize();
    }

    @Override
    public abstract CellRecord copy();

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("row", this::getRow, "col", this::getColumn, "xfIndex", this::getXFIndex);
    }
}

