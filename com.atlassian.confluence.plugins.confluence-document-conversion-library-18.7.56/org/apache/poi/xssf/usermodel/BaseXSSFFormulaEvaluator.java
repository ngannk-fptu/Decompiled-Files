/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xssf.usermodel;

import org.apache.poi.ss.formula.BaseFormulaEvaluator;
import org.apache.poi.ss.formula.EvaluationCell;
import org.apache.poi.ss.formula.EvaluationWorkbook;
import org.apache.poi.ss.formula.WorkbookEvaluator;
import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.ptg.Area3DPxg;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.model.ExternalLinksTable;
import org.apache.poi.xssf.usermodel.BaseXSSFEvaluationWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFEvaluationCell;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public abstract class BaseXSSFFormulaEvaluator
extends BaseFormulaEvaluator {
    protected BaseXSSFFormulaEvaluator(WorkbookEvaluator bookEvaluator) {
        super(bookEvaluator);
    }

    @Override
    protected RichTextString createRichTextString(String str) {
        return new XSSFRichTextString(str);
    }

    protected abstract EvaluationCell toEvaluationCell(Cell var1);

    @Override
    protected CellValue evaluateFormulaCellValue(Cell cell) {
        EvaluationCell evalCell = this.toEvaluationCell(cell);
        ValueEval eval = this._bookEvaluator.evaluate(evalCell);
        this.cacheExternalWorkbookCells(evalCell);
        if (eval instanceof NumberEval) {
            NumberEval ne = (NumberEval)eval;
            return new CellValue(ne.getNumberValue());
        }
        if (eval instanceof BoolEval) {
            BoolEval be = (BoolEval)eval;
            return CellValue.valueOf(be.getBooleanValue());
        }
        if (eval instanceof StringEval) {
            StringEval ne = (StringEval)eval;
            return new CellValue(ne.getStringValue());
        }
        if (eval instanceof ErrorEval) {
            return CellValue.getError(((ErrorEval)eval).getErrorCode());
        }
        throw new RuntimeException("Unexpected eval class (" + eval.getClass().getName() + ")");
    }

    private void cacheExternalWorkbookCells(EvaluationCell evalCell) {
        Ptg[] formulaTokens;
        for (Ptg ptg : formulaTokens = this.getEvaluationWorkbook().getFormulaTokens(evalCell)) {
            int firstSheet;
            Area3DPxg area3DPxg;
            if (!(ptg instanceof Area3DPxg) || (area3DPxg = (Area3DPxg)ptg).getExternalWorkbookNumber() <= 0) continue;
            EvaluationWorkbook.ExternalSheet externalSheet = this.getEvaluationWorkbook().getExternalSheet(area3DPxg.getSheetName(), area3DPxg.getLastSheetName(), area3DPxg.getExternalWorkbookNumber());
            XSSFCell xssfCell = ((XSSFEvaluationCell)evalCell).getXSSFCell();
            XSSFWorkbook externalWorkbook = (XSSFWorkbook)xssfCell.getSheet().getWorkbook().getCreationHelper().getReferencedWorkbooks().get(externalSheet.getWorkbookName());
            ExternalLinksTable externalLinksTable = xssfCell.getSheet().getWorkbook().getExternalLinksTable().get(area3DPxg.getExternalWorkbookNumber() - 1);
            int lastSheet = firstSheet = externalWorkbook.getSheetIndex(area3DPxg.getSheetName());
            if (area3DPxg.getLastSheetName() != null) {
                lastSheet = externalWorkbook.getSheetIndex(area3DPxg.getLastSheetName());
            }
            for (int sheetIndex = firstSheet; sheetIndex <= lastSheet; ++sheetIndex) {
                XSSFSheet sheet = externalWorkbook.getSheetAt(sheetIndex);
                int firstRow = area3DPxg.getFirstRow();
                int lastRow = area3DPxg.getLastRow();
                for (int rowIndex = firstRow; rowIndex <= lastRow; ++rowIndex) {
                    XSSFRow row = sheet.getRow(rowIndex);
                    int firstColumn = area3DPxg.getFirstColumn();
                    int lastColumn = area3DPxg.getLastColumn();
                    for (int cellIndex = firstColumn; cellIndex <= lastColumn; ++cellIndex) {
                        XSSFCell cell = row.getCell(cellIndex);
                        String cellValue = cell.getRawValue();
                        String cellR = new CellReference(cell).formatAsString(false);
                        externalLinksTable.cacheData(sheet.getSheetName(), (long)rowIndex + 1L, cellR, cellValue);
                    }
                }
            }
        }
    }

    @Override
    protected void setCellType(Cell cell, CellType cellType) {
        if (cell instanceof XSSFCell) {
            EvaluationWorkbook evaluationWorkbook = this.getEvaluationWorkbook();
            BaseXSSFEvaluationWorkbook xewb = BaseXSSFEvaluationWorkbook.class.isAssignableFrom(evaluationWorkbook.getClass()) ? (BaseXSSFEvaluationWorkbook)evaluationWorkbook : null;
            ((XSSFCell)cell).setCellType(cellType, xewb);
        } else {
            cell.setCellType(cellType);
        }
    }
}

