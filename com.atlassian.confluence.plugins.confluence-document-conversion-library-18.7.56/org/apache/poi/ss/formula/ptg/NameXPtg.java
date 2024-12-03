/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.ptg;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.ss.formula.FormulaRenderingWorkbook;
import org.apache.poi.ss.formula.WorkbookDependentFormula;
import org.apache.poi.ss.formula.ptg.OperandPtg;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;

public final class NameXPtg
extends OperandPtg
implements WorkbookDependentFormula {
    public static final short sid = 57;
    private static final int SIZE = 7;
    private final int _sheetRefIndex;
    private final int _nameNumber;
    private final int _reserved;

    private NameXPtg(int sheetRefIndex, int nameNumber, int reserved) {
        this._sheetRefIndex = sheetRefIndex;
        this._nameNumber = nameNumber;
        this._reserved = reserved;
    }

    public NameXPtg(int sheetRefIndex, int nameIndex) {
        this(sheetRefIndex, nameIndex + 1, 0);
    }

    public NameXPtg(LittleEndianInput in) {
        this(in.readUShort(), in.readUShort(), in.readUShort());
    }

    @Override
    public void write(LittleEndianOutput out) {
        out.writeByte(57 + this.getPtgClass());
        out.writeShort(this._sheetRefIndex);
        out.writeShort(this._nameNumber);
        out.writeShort(this._reserved);
    }

    @Override
    public byte getSid() {
        return 57;
    }

    @Override
    public int getSize() {
        return 7;
    }

    @Override
    public String toFormulaString(FormulaRenderingWorkbook book) {
        return book.resolveNameXText(this);
    }

    @Override
    public String toFormulaString() {
        throw new RuntimeException("3D references need a workbook to determine formula text");
    }

    @Override
    public byte getDefaultOperandClass() {
        return 32;
    }

    public int getSheetRefIndex() {
        return this._sheetRefIndex;
    }

    public int getNameIndex() {
        return this._nameNumber - 1;
    }

    @Override
    public NameXPtg copy() {
        return this;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("sheetRefIndex", this::getSheetRefIndex, "nameIndex", this::getNameIndex);
    }
}

