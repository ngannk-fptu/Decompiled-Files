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

public final class MulBlankRecord
extends StandardRecord {
    public static final short sid = 190;
    private final int _row;
    private final int _firstCol;
    private final short[] _xfs;
    private final int _lastCol;

    public MulBlankRecord(int row, int firstCol, short[] xfs) {
        this._row = row;
        this._firstCol = firstCol;
        this._xfs = xfs;
        this._lastCol = firstCol + xfs.length - 1;
    }

    public int getRow() {
        return this._row;
    }

    public int getFirstColumn() {
        return this._firstCol;
    }

    public int getLastColumn() {
        return this._lastCol;
    }

    public int getNumColumns() {
        return this._lastCol - this._firstCol + 1;
    }

    public short getXFAt(int coffset) {
        return this._xfs[coffset];
    }

    public MulBlankRecord(RecordInputStream in) {
        this._row = in.readUShort();
        this._firstCol = in.readShort();
        this._xfs = MulBlankRecord.parseXFs(in);
        this._lastCol = in.readShort();
    }

    private static short[] parseXFs(RecordInputStream in) {
        short[] retval = new short[(in.remaining() - 2) / 2];
        for (int idx = 0; idx < retval.length; ++idx) {
            retval[idx] = in.readShort();
        }
        return retval;
    }

    @Override
    public short getSid() {
        return 190;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this._row);
        out.writeShort(this._firstCol);
        for (short xf : this._xfs) {
            out.writeShort(xf);
        }
        out.writeShort(this._lastCol);
    }

    @Override
    protected int getDataSize() {
        return 6 + this._xfs.length * 2;
    }

    @Override
    public MulBlankRecord copy() {
        return this;
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.MUL_BLANK;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("row", this::getRow, "firstColumn", this::getFirstColumn, "lastColumn", this::getLastColumn, "xf", () -> this._xfs);
    }
}

