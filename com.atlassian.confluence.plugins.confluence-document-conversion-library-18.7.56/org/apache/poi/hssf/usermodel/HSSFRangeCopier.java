/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.record.aggregates.FormulaRecordAggregate;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RangeCopier;
import org.apache.poi.ss.usermodel.Sheet;

public class HSSFRangeCopier
extends RangeCopier {
    public HSSFRangeCopier(Sheet sourceSheet, Sheet destSheet) {
        super(sourceSheet, destSheet);
    }

    public HSSFRangeCopier(Sheet sheet) {
        super(sheet);
    }

    @Override
    protected void adjustCellReferencesInsideFormula(Cell cell, Sheet destSheet, int deltaX, int deltaY) {
        FormulaRecordAggregate fra = (FormulaRecordAggregate)((HSSFCell)cell).getCellValueRecord();
        int destSheetIndex = destSheet.getWorkbook().getSheetIndex(destSheet);
        Ptg[] ptgs = fra.getFormulaTokens();
        if (this.adjustInBothDirections(ptgs, destSheetIndex, deltaX, deltaY)) {
            fra.setParsedExpression(ptgs);
        }
    }
}

