/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.usermodel.helpers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFEvaluationWorkbook;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaRenderer;
import org.apache.poi.ss.formula.FormulaShifter;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.Internal;

@Internal
final class HSSFRowColShifter {
    private static final Logger LOG = LogManager.getLogger(HSSFRowColShifter.class);

    private HSSFRowColShifter() {
    }

    static void updateFormulas(Sheet sheet, FormulaShifter formulaShifter) {
        HSSFRowColShifter.updateSheetFormulas(sheet, formulaShifter);
        Workbook wb = sheet.getWorkbook();
        for (Sheet sh : wb) {
            if (sheet == sh) continue;
            HSSFRowColShifter.updateSheetFormulas(sh, formulaShifter);
        }
    }

    static void updateSheetFormulas(Sheet sh, FormulaShifter formulashifter) {
        for (Row r : sh) {
            HSSFRow row = (HSSFRow)r;
            HSSFRowColShifter.updateRowFormulas(row, formulashifter);
        }
    }

    static void updateRowFormulas(HSSFRow row, FormulaShifter formulaShifter) {
        HSSFSheet sheet = row.getSheet();
        for (Cell c : row) {
            HSSFCell cell = (HSSFCell)c;
            String formula = cell.getCellFormula();
            if (formula.length() <= 0) continue;
            String shiftedFormula = HSSFRowColShifter.shiftFormula(row, formula, formulaShifter);
            cell.setCellFormula(shiftedFormula);
        }
    }

    static String shiftFormula(Row row, String formula, FormulaShifter formulaShifter) {
        Sheet sheet = row.getSheet();
        Workbook wb = sheet.getWorkbook();
        int sheetIndex = wb.getSheetIndex(sheet);
        int rowIndex = row.getRowNum();
        HSSFEvaluationWorkbook fpb = HSSFEvaluationWorkbook.create((HSSFWorkbook)wb);
        try {
            Ptg[] ptgs = FormulaParser.parse(formula, fpb, FormulaType.CELL, sheetIndex, rowIndex);
            String shiftedFmla = formulaShifter.adjustFormula(ptgs, sheetIndex) ? FormulaRenderer.toFormulaString(fpb, ptgs) : formula;
            return shiftedFmla;
        }
        catch (FormulaParseException fpe) {
            LOG.atWarn().withThrowable(fpe).log("Error shifting formula on row {}", (Object)Unbox.box(row.getRowNum()));
            return formula;
        }
    }
}

