/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.ptg;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.ss.formula.FormulaRenderingWorkbook;
import org.apache.poi.ss.formula.WorkbookDependentFormula;
import org.apache.poi.ss.formula.ptg.ExternSheetNameResolver;
import org.apache.poi.ss.formula.ptg.OperandPtg;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;

public final class DeletedArea3DPtg
extends OperandPtg
implements WorkbookDependentFormula {
    public static final byte sid = 61;
    private final int field_1_index_extern_sheet;
    private final int unused1;
    private final int unused2;

    public DeletedArea3DPtg(int externSheetIndex) {
        this.field_1_index_extern_sheet = externSheetIndex;
        this.unused1 = 0;
        this.unused2 = 0;
    }

    public DeletedArea3DPtg(LittleEndianInput in) {
        this.field_1_index_extern_sheet = in.readUShort();
        this.unused1 = in.readInt();
        this.unused2 = in.readInt();
    }

    @Override
    public String toFormulaString(FormulaRenderingWorkbook book) {
        return ExternSheetNameResolver.prependSheetName(book, this.field_1_index_extern_sheet, FormulaError.REF.getString());
    }

    @Override
    public String toFormulaString() {
        throw new RuntimeException("3D references need a workbook to determine formula text");
    }

    @Override
    public byte getDefaultOperandClass() {
        return 0;
    }

    @Override
    public byte getSid() {
        return 61;
    }

    @Override
    public int getSize() {
        return 11;
    }

    public int getExternSheetIndex() {
        return this.field_1_index_extern_sheet;
    }

    @Override
    public void write(LittleEndianOutput out) {
        out.writeByte(61 + this.getPtgClass());
        out.writeShort(this.field_1_index_extern_sheet);
        out.writeInt(this.unused1);
        out.writeInt(this.unused2);
    }

    @Override
    public DeletedArea3DPtg copy() {
        return this;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("externSheetIndex", this::getExternSheetIndex, "unused1", () -> this.unused1, "unused2", () -> this.unused2);
    }
}

