/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.ptg;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.ss.formula.constant.ConstantValueParser;
import org.apache.poi.ss.formula.constant.ErrorConstant;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianOutput;

public final class ArrayPtg
extends Ptg {
    public static final byte sid = 32;
    private static final int RESERVED_FIELD_LEN = 7;
    public static final int PLAIN_TOKEN_SIZE = 8;
    private final int _reserved0Int;
    private final int _reserved1Short;
    private final int _reserved2Byte;
    private final int _nColumns;
    private final int _nRows;
    private final Object[] _arrayValues;

    ArrayPtg(int reserved0, int reserved1, int reserved2, int nColumns, int nRows, Object[] arrayValues) {
        this._reserved0Int = reserved0;
        this._reserved1Short = reserved1;
        this._reserved2Byte = reserved2;
        this._nColumns = nColumns;
        this._nRows = nRows;
        this._arrayValues = (Object[])arrayValues.clone();
    }

    public ArrayPtg(ArrayPtg other) {
        this._reserved0Int = other._reserved0Int;
        this._reserved1Short = other._reserved1Short;
        this._reserved2Byte = other._reserved2Byte;
        this._nColumns = other._nColumns;
        this._nRows = other._nRows;
        this._arrayValues = other._arrayValues == null ? null : (Object[])other._arrayValues.clone();
    }

    public ArrayPtg(Object[][] values2d) {
        int nColumns = values2d[0].length;
        int nRows = values2d.length;
        this._nColumns = (short)nColumns;
        this._nRows = (short)nRows;
        Object[] vv = new Object[this._nColumns * this._nRows];
        for (int r = 0; r < nRows; ++r) {
            Object[] rowData = values2d[r];
            for (int c = 0; c < nColumns; ++c) {
                vv[this.getValueIndex((int)c, (int)r)] = rowData[c];
            }
        }
        this._arrayValues = vv;
        this._reserved0Int = 0;
        this._reserved1Short = 0;
        this._reserved2Byte = 0;
    }

    public Object[][] getTokenArrayValues() {
        if (this._arrayValues == null) {
            throw new IllegalStateException("array values not read yet");
        }
        Object[][] result = new Object[this._nRows][this._nColumns];
        for (int r = 0; r < this._nRows; ++r) {
            Object[] rowData = result[r];
            for (int c = 0; c < this._nColumns; ++c) {
                rowData[c] = this._arrayValues[this.getValueIndex(c, r)];
            }
        }
        return result;
    }

    @Override
    public boolean isBaseToken() {
        return false;
    }

    int getValueIndex(int colIx, int rowIx) {
        if (colIx < 0 || colIx >= this._nColumns) {
            throw new IllegalArgumentException("Specified colIx (" + colIx + ") is outside the allowed range (0.." + (this._nColumns - 1) + ")");
        }
        if (rowIx < 0 || rowIx >= this._nRows) {
            throw new IllegalArgumentException("Specified rowIx (" + rowIx + ") is outside the allowed range (0.." + (this._nRows - 1) + ")");
        }
        return rowIx * this._nColumns + colIx;
    }

    @Override
    public void write(LittleEndianOutput out) {
        out.writeByte(32 + this.getPtgClass());
        out.writeInt(this._reserved0Int);
        out.writeShort(this._reserved1Short);
        out.writeByte(this._reserved2Byte);
    }

    public int writeTokenValueBytes(LittleEndianOutput out) {
        out.writeByte(this._nColumns - 1);
        out.writeShort(this._nRows - 1);
        ConstantValueParser.encode(out, this._arrayValues);
        return 3 + ConstantValueParser.getEncodedSize(this._arrayValues);
    }

    public int getRowCount() {
        return this._nRows;
    }

    public int getColumnCount() {
        return this._nColumns;
    }

    @Override
    public int getSize() {
        return 11 + ConstantValueParser.getEncodedSize(this._arrayValues);
    }

    @Override
    public byte getSid() {
        return 32;
    }

    @Override
    public String toFormulaString() {
        StringBuilder b = new StringBuilder();
        b.append("{");
        for (int y = 0; y < this._nRows; ++y) {
            if (y > 0) {
                b.append(";");
            }
            for (int x = 0; x < this._nColumns; ++x) {
                if (x > 0) {
                    b.append(",");
                }
                Object o = this._arrayValues[this.getValueIndex(x, y)];
                b.append(ArrayPtg.getConstantText(o));
            }
        }
        b.append("}");
        return b.toString();
    }

    private static String getConstantText(Object o) {
        if (o == null) {
            throw new RuntimeException("Array item cannot be null");
        }
        if (o instanceof String) {
            return "\"" + o + "\"";
        }
        if (o instanceof Double) {
            return NumberToTextConverter.toText((Double)o);
        }
        if (o instanceof Boolean) {
            return (Boolean)o != false ? "TRUE" : "FALSE";
        }
        if (o instanceof ErrorConstant) {
            return ((ErrorConstant)o).getText();
        }
        throw new IllegalArgumentException("Unexpected constant class (" + o.getClass().getName() + ")");
    }

    @Override
    public byte getDefaultOperandClass() {
        return 64;
    }

    @Override
    public ArrayPtg copy() {
        return new ArrayPtg(this);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("reserved0", () -> this._reserved0Int, "reserved1", () -> this._reserved1Short, "reserved2", () -> this._reserved2Byte, "columnCount", this::getColumnCount, "rowCount", this::getRowCount, "arrayValues", () -> this._arrayValues == null ? "#values#uninitialised#" : this.toFormulaString());
    }
}

