/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.ptg;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.ss.formula.ExternSheetReferenceToken;
import org.apache.poi.ss.formula.FormulaRenderingWorkbook;
import org.apache.poi.ss.formula.WorkbookDependentFormula;
import org.apache.poi.ss.formula.ptg.ExternSheetNameResolver;
import org.apache.poi.ss.formula.ptg.RefPtgBase;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;

public final class Ref3DPtg
extends RefPtgBase
implements WorkbookDependentFormula,
ExternSheetReferenceToken {
    public static final byte sid = 58;
    private static final int SIZE = 7;
    private int field_1_index_extern_sheet;

    public Ref3DPtg(Ref3DPtg other) {
        super(other);
        this.field_1_index_extern_sheet = other.field_1_index_extern_sheet;
    }

    public Ref3DPtg(LittleEndianInput in) {
        this.field_1_index_extern_sheet = in.readShort();
        this.readCoordinates(in);
    }

    public Ref3DPtg(String cellref, int externIdx) {
        this(new CellReference(cellref), externIdx);
    }

    public Ref3DPtg(CellReference c, int externIdx) {
        super(c);
        this.setExternSheetIndex(externIdx);
    }

    @Override
    public void write(LittleEndianOutput out) {
        out.writeByte(58 + this.getPtgClass());
        out.writeShort(this.getExternSheetIndex());
        this.writeCoordinates(out);
    }

    @Override
    public byte getSid() {
        return 58;
    }

    @Override
    public int getSize() {
        return 7;
    }

    @Override
    public int getExternSheetIndex() {
        return this.field_1_index_extern_sheet;
    }

    public void setExternSheetIndex(int index) {
        this.field_1_index_extern_sheet = index;
    }

    @Override
    public String format2DRefAsString() {
        return this.formatReferenceAsString();
    }

    @Override
    public String toFormulaString(FormulaRenderingWorkbook book) {
        return ExternSheetNameResolver.prependSheetName(book, this.field_1_index_extern_sheet, this.formatReferenceAsString());
    }

    @Override
    public String toFormulaString() {
        throw new RuntimeException("3D references need a workbook to determine formula text");
    }

    @Override
    public Ref3DPtg copy() {
        return new Ref3DPtg(this);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("base", () -> super.getGenericProperties(), "externSheetIndex", this::getExternSheetIndex);
    }
}

