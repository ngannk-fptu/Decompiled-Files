/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula;

import org.apache.poi.ss.formula.FormulaRenderingWorkbook;
import org.apache.poi.util.Internal;

@Internal
public interface WorkbookDependentFormula {
    public String toFormulaString(FormulaRenderingWorkbook var1);
}

