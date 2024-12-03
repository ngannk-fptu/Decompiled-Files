/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xssf.usermodel;

import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaRenderer;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RangeCopier;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XSSFRangeCopier
extends RangeCopier {
    public XSSFRangeCopier(Sheet sourceSheet, Sheet destSheet) {
        super(sourceSheet, destSheet);
    }

    public XSSFRangeCopier(Sheet sheet) {
        super(sheet);
    }

    @Override
    protected void adjustCellReferencesInsideFormula(Cell cell, Sheet destSheet, int deltaX, int deltaY) {
        int destSheetIndex;
        XSSFWorkbook hostWorkbook = (XSSFWorkbook)destSheet.getWorkbook();
        XSSFEvaluationWorkbook fpb = XSSFEvaluationWorkbook.create(hostWorkbook);
        Ptg[] ptgs = FormulaParser.parse(cell.getCellFormula(), fpb, FormulaType.CELL, 0);
        if (this.adjustInBothDirections(ptgs, destSheetIndex = hostWorkbook.getSheetIndex(destSheet), deltaX, deltaY)) {
            cell.setCellFormula(FormulaRenderer.toFormulaString(fpb, ptgs));
        }
    }
}

