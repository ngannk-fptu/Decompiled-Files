/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula;

import org.apache.poi.ss.formula.EvaluationSheet;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.Internal;

@Internal
public interface EvaluationCell {
    public Object getIdentityKey();

    public EvaluationSheet getSheet();

    public int getRowIndex();

    public int getColumnIndex();

    public CellType getCellType();

    public double getNumericCellValue();

    public String getStringCellValue();

    public boolean getBooleanCellValue();

    public int getErrorCellValue();

    public CellRangeAddress getArrayFormulaRange();

    public boolean isPartOfArrayFormulaGroup();

    public CellType getCachedFormulaResultType();
}

