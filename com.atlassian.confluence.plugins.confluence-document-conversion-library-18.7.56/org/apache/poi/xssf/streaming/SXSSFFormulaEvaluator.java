/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xssf.streaming;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.ss.formula.EvaluationCell;
import org.apache.poi.ss.formula.IStabilityClassifier;
import org.apache.poi.ss.formula.WorkbookEvaluator;
import org.apache.poi.ss.formula.udf.UDFFinder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFEvaluationCell;
import org.apache.poi.xssf.streaming.SXSSFEvaluationWorkbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.BaseXSSFFormulaEvaluator;

public final class SXSSFFormulaEvaluator
extends BaseXSSFFormulaEvaluator {
    private static final Logger LOG = LogManager.getLogger(SXSSFFormulaEvaluator.class);
    private final SXSSFWorkbook wb;

    public SXSSFFormulaEvaluator(SXSSFWorkbook workbook) {
        this(workbook, null, null);
    }

    private SXSSFFormulaEvaluator(SXSSFWorkbook workbook, IStabilityClassifier stabilityClassifier, UDFFinder udfFinder) {
        this(workbook, new WorkbookEvaluator(SXSSFEvaluationWorkbook.create(workbook), stabilityClassifier, udfFinder));
    }

    private SXSSFFormulaEvaluator(SXSSFWorkbook workbook, WorkbookEvaluator bookEvaluator) {
        super(bookEvaluator);
        this.wb = workbook;
    }

    public static SXSSFFormulaEvaluator create(SXSSFWorkbook workbook, IStabilityClassifier stabilityClassifier, UDFFinder udfFinder) {
        return new SXSSFFormulaEvaluator(workbook, stabilityClassifier, udfFinder);
    }

    @Override
    public void notifySetFormula(Cell cell) {
        this._bookEvaluator.notifyUpdateCell(new SXSSFEvaluationCell((SXSSFCell)cell));
    }

    @Override
    public void notifyDeleteCell(Cell cell) {
        this._bookEvaluator.notifyDeleteCell(new SXSSFEvaluationCell((SXSSFCell)cell));
    }

    @Override
    public void notifyUpdateCell(Cell cell) {
        this._bookEvaluator.notifyUpdateCell(new SXSSFEvaluationCell((SXSSFCell)cell));
    }

    @Override
    protected EvaluationCell toEvaluationCell(Cell cell) {
        if (!(cell instanceof SXSSFCell)) {
            throw new IllegalArgumentException("Unexpected type of cell: " + cell.getClass() + ". Only SXSSFCells can be evaluated.");
        }
        return new SXSSFEvaluationCell((SXSSFCell)cell);
    }

    @Override
    public SXSSFCell evaluateInCell(Cell cell) {
        return (SXSSFCell)super.evaluateInCell(cell);
    }

    public static void evaluateAllFormulaCells(SXSSFWorkbook wb, boolean skipOutOfWindow) {
        SXSSFFormulaEvaluator eval = new SXSSFFormulaEvaluator(wb);
        for (Sheet sheet : wb) {
            if (!((SXSSFSheet)sheet).areAllRowsFlushed()) continue;
            throw new SheetsFlushedException();
        }
        for (Sheet sheet : wb) {
            int lastFlushedRowNum;
            if (sheet instanceof SXSSFSheet && (lastFlushedRowNum = ((SXSSFSheet)sheet).getLastFlushedRowNum()) > -1) {
                if (!skipOutOfWindow) {
                    throw new RowFlushedException(0, lastFlushedRowNum);
                }
                LOG.atInfo().log("Rows up to {} have already been flushed, skipping", (Object)Unbox.box(lastFlushedRowNum));
            }
            for (Row r : sheet) {
                for (Cell c : r) {
                    if (c.getCellType() != CellType.FORMULA) continue;
                    eval.evaluateFormulaCell(c);
                }
            }
        }
    }

    @Override
    public void evaluateAll() {
        SXSSFFormulaEvaluator.evaluateAllFormulaCells(this.wb, false);
    }

    public static class RowFlushedException
    extends IllegalStateException {
        protected RowFlushedException(int rowNum, int lastFlushedRowNum) {
            super("Row " + rowNum + " has been flushed (rows up to " + lastFlushedRowNum + " have been flushed), cannot evaluate all cells");
        }
    }

    public static class SheetsFlushedException
    extends IllegalStateException {
        protected SheetsFlushedException() {
            super("One or more sheets have been flushed, cannot evaluate all cells");
        }
    }
}

