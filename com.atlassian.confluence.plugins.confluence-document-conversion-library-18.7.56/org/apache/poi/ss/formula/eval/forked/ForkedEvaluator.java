/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.eval.forked;

import java.util.stream.Stream;
import org.apache.poi.ss.formula.CollaboratingWorkbooksEnvironment;
import org.apache.poi.ss.formula.EvaluationCell;
import org.apache.poi.ss.formula.EvaluationWorkbook;
import org.apache.poi.ss.formula.IStabilityClassifier;
import org.apache.poi.ss.formula.WorkbookEvaluator;
import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.eval.forked.ForkedEvaluationCell;
import org.apache.poi.ss.formula.eval.forked.ForkedEvaluationWorkbook;
import org.apache.poi.ss.formula.udf.UDFFinder;
import org.apache.poi.ss.usermodel.Workbook;

public final class ForkedEvaluator {
    private final WorkbookEvaluator _evaluator;
    private final ForkedEvaluationWorkbook _sewb;

    private ForkedEvaluator(EvaluationWorkbook masterWorkbook, IStabilityClassifier stabilityClassifier, UDFFinder udfFinder) {
        this._sewb = new ForkedEvaluationWorkbook(masterWorkbook);
        this._evaluator = new WorkbookEvaluator(this._sewb, stabilityClassifier, udfFinder);
    }

    public static ForkedEvaluator create(Workbook wb, IStabilityClassifier stabilityClassifier, UDFFinder udfFinder) {
        return new ForkedEvaluator(wb.createEvaluationWorkbook(), stabilityClassifier, udfFinder);
    }

    public void updateCell(String sheetName, int rowIndex, int columnIndex, ValueEval value) {
        ForkedEvaluationCell cell = this._sewb.getOrCreateUpdatableCell(sheetName, rowIndex, columnIndex);
        cell.setValue(value);
        this._evaluator.notifyUpdateCell(cell);
    }

    public void copyUpdatedCells(Workbook workbook) {
        this._sewb.copyUpdatedCells(workbook);
    }

    public ValueEval evaluate(String sheetName, int rowIndex, int columnIndex) {
        EvaluationCell cell = this._sewb.getEvaluationCell(sheetName, rowIndex, columnIndex);
        switch (cell.getCellType()) {
            case BOOLEAN: {
                return BoolEval.valueOf(cell.getBooleanCellValue());
            }
            case ERROR: {
                return ErrorEval.valueOf(cell.getErrorCellValue());
            }
            case FORMULA: {
                return this._evaluator.evaluate(cell);
            }
            case NUMERIC: {
                return new NumberEval(cell.getNumericCellValue());
            }
            case STRING: {
                return new StringEval(cell.getStringCellValue());
            }
            case BLANK: {
                return null;
            }
        }
        throw new IllegalStateException("Bad cell type (" + (Object)((Object)cell.getCellType()) + ")");
    }

    public static void setupEnvironment(String[] workbookNames, ForkedEvaluator[] evaluators) {
        WorkbookEvaluator[] wbEvals = (WorkbookEvaluator[])Stream.of(evaluators).map(e -> e._evaluator).toArray(WorkbookEvaluator[]::new);
        CollaboratingWorkbooksEnvironment.setup(workbookNames, wbEvals);
    }
}

