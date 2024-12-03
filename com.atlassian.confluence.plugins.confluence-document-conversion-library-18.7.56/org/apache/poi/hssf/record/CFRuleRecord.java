/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import org.apache.poi.hssf.record.CFRuleBase;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.formula.Formula;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.util.LittleEndianOutput;

public final class CFRuleRecord
extends CFRuleBase {
    public static final short sid = 433;

    public CFRuleRecord(CFRuleRecord other) {
        super(other);
    }

    private CFRuleRecord(byte conditionType, byte comparisonOperation, Ptg[] formula1, Ptg[] formula2) {
        super(conditionType, comparisonOperation, formula1, formula2);
        this.setDefaults();
    }

    private void setDefaults() {
        this.formatting_options = modificationBits.setValue(this.formatting_options, -1);
        this.formatting_options = fmtBlockBits.setValue(this.formatting_options, 0);
        this.formatting_options = undocumented.clear(this.formatting_options);
        this.formatting_not_used = (short)-32766;
        this._fontFormatting = null;
        this._borderFormatting = null;
        this._patternFormatting = null;
    }

    public static CFRuleRecord create(HSSFSheet sheet, String formulaText) {
        Ptg[] formula1 = CFRuleRecord.parseFormula(formulaText, sheet);
        return new CFRuleRecord(2, 0, formula1, null);
    }

    public static CFRuleRecord create(HSSFSheet sheet, byte comparisonOperation, String formulaText1, String formulaText2) {
        Ptg[] formula1 = CFRuleRecord.parseFormula(formulaText1, sheet);
        Ptg[] formula2 = CFRuleRecord.parseFormula(formulaText2, sheet);
        return new CFRuleRecord(1, comparisonOperation, formula1, formula2);
    }

    public CFRuleRecord(RecordInputStream in) {
        this.setConditionType(in.readByte());
        this.setComparisonOperation(in.readByte());
        int field_3_formula1_len = in.readUShort();
        int field_4_formula2_len = in.readUShort();
        this.readFormatOptions(in);
        this.setFormula1(Formula.read(field_3_formula1_len, in));
        this.setFormula2(Formula.read(field_4_formula2_len, in));
    }

    @Override
    public short getSid() {
        return 433;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        int formula1Len = CFRuleRecord.getFormulaSize(this.getFormula1());
        int formula2Len = CFRuleRecord.getFormulaSize(this.getFormula2());
        out.writeByte(this.getConditionType());
        out.writeByte(this.getComparisonOperation());
        out.writeShort(formula1Len);
        out.writeShort(formula2Len);
        this.serializeFormattingBlock(out);
        this.getFormula1().serializeTokens(out);
        this.getFormula2().serializeTokens(out);
    }

    @Override
    protected int getDataSize() {
        return 6 + this.getFormattingBlockSize() + CFRuleRecord.getFormulaSize(this.getFormula1()) + CFRuleRecord.getFormulaSize(this.getFormula2());
    }

    @Override
    public CFRuleRecord copy() {
        return new CFRuleRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.CF_RULE;
    }
}

