/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula;

import org.apache.poi.ss.formula.SheetRange;
import org.apache.poi.ss.formula.SheetRefEvaluator;
import org.apache.poi.ss.formula.eval.ValueEval;

final class SheetRangeEvaluator
implements SheetRange {
    private final int _firstSheetIndex;
    private final int _lastSheetIndex;
    private final SheetRefEvaluator[] _sheetEvaluators;

    public SheetRangeEvaluator(int firstSheetIndex, int lastSheetIndex, SheetRefEvaluator[] sheetEvaluators) {
        if (firstSheetIndex < 0) {
            throw new IllegalArgumentException("Invalid firstSheetIndex: " + firstSheetIndex + ".");
        }
        if (lastSheetIndex < firstSheetIndex) {
            throw new IllegalArgumentException("Invalid lastSheetIndex: " + lastSheetIndex + " for firstSheetIndex: " + firstSheetIndex + ".");
        }
        this._firstSheetIndex = firstSheetIndex;
        this._lastSheetIndex = lastSheetIndex;
        this._sheetEvaluators = (SheetRefEvaluator[])sheetEvaluators.clone();
    }

    public SheetRangeEvaluator(int onlySheetIndex, SheetRefEvaluator sheetEvaluator) {
        this(onlySheetIndex, onlySheetIndex, new SheetRefEvaluator[]{sheetEvaluator});
    }

    public SheetRefEvaluator getSheetEvaluator(int sheetIndex) {
        if (sheetIndex < this._firstSheetIndex || sheetIndex > this._lastSheetIndex) {
            throw new IllegalArgumentException("Invalid SheetIndex: " + sheetIndex + " - Outside range " + this._firstSheetIndex + " : " + this._lastSheetIndex);
        }
        return this._sheetEvaluators[sheetIndex - this._firstSheetIndex];
    }

    @Override
    public int getFirstSheetIndex() {
        return this._firstSheetIndex;
    }

    @Override
    public int getLastSheetIndex() {
        return this._lastSheetIndex;
    }

    public String getSheetName(int sheetIndex) {
        return this.getSheetEvaluator(sheetIndex).getSheetName();
    }

    public String getSheetNameRange() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getSheetName(this._firstSheetIndex));
        if (this._firstSheetIndex != this._lastSheetIndex) {
            sb.append(':');
            sb.append(this.getSheetName(this._lastSheetIndex));
        }
        return sb.toString();
    }

    public ValueEval getEvalForCell(int sheetIndex, int rowIndex, int columnIndex) {
        return this.getSheetEvaluator(sheetIndex).getEvalForCell(rowIndex, columnIndex);
    }

    public int adjustRowNumber(int rowIndex) {
        boolean found = false;
        for (int i = this._firstSheetIndex; i <= this._lastSheetIndex; ++i) {
            if (rowIndex < this._sheetEvaluators[i - this._firstSheetIndex].getMaxRowNum()) continue;
            found = true;
        }
        if (!found) {
            return rowIndex;
        }
        int maxRowNum = 0;
        for (int i = this._firstSheetIndex; i <= this._lastSheetIndex; ++i) {
            maxRowNum = Math.max(maxRowNum, this._sheetEvaluators[i - this._firstSheetIndex].getLastRowNum());
        }
        return Math.min(rowIndex, maxRowNum);
    }
}

