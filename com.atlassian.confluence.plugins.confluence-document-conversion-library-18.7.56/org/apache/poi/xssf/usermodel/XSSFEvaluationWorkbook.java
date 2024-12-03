/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xssf.usermodel;

import java.util.HashMap;
import java.util.Map;
import org.apache.poi.ss.formula.EvaluationCell;
import org.apache.poi.ss.formula.EvaluationSheet;
import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.util.Internal;
import org.apache.poi.xssf.usermodel.BaseXSSFEvaluationWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFEvaluationCell;
import org.apache.poi.xssf.usermodel.XSSFEvaluationSheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Internal
public final class XSSFEvaluationWorkbook
extends BaseXSSFEvaluationWorkbook {
    private final Map<XSSFSheet, XSSFEvaluationSheet> _sheetCache = new HashMap<XSSFSheet, XSSFEvaluationSheet>();

    public static XSSFEvaluationWorkbook create(XSSFWorkbook book) {
        if (book == null) {
            return null;
        }
        return new XSSFEvaluationWorkbook(book);
    }

    private XSSFEvaluationWorkbook(XSSFWorkbook book) {
        super(book);
    }

    @Override
    public void clearAllCachedResultValues() {
        super.clearAllCachedResultValues();
        this._sheetCache.clear();
    }

    @Override
    public int getSheetIndex(EvaluationSheet evalSheet) {
        XSSFSheet sheet = ((XSSFEvaluationSheet)evalSheet).getXSSFSheet();
        return this._uBook.getSheetIndex(sheet);
    }

    @Override
    public EvaluationSheet getSheet(int sheetIndex) {
        if (sheetIndex < 0 || sheetIndex >= this._uBook.getNumberOfSheets()) {
            this._uBook.getSheetAt(sheetIndex);
        }
        XSSFSheet sheet = this._uBook.getSheetAt(sheetIndex);
        return this._sheetCache.computeIfAbsent(sheet, rows -> new XSSFEvaluationSheet(sheet));
    }

    @Override
    public Ptg[] getFormulaTokens(EvaluationCell evalCell) {
        XSSFCell cell = ((XSSFEvaluationCell)evalCell).getXSSFCell();
        int sheetIndex = this._uBook.getSheetIndex(cell.getSheet());
        int rowIndex = cell.getRowIndex();
        return FormulaParser.parse(cell.getCellFormula(this), this, FormulaType.CELL, sheetIndex, rowIndex);
    }
}

