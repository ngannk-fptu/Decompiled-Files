/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.usermodel.helpers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.helpers.HSSFRowColShifter;
import org.apache.poi.ss.formula.FormulaShifter;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.apache.poi.ss.usermodel.helpers.RowShifter;
import org.apache.poi.util.Internal;
import org.apache.poi.util.NotImplemented;

public final class HSSFRowShifter
extends RowShifter {
    private static final Logger LOG = LogManager.getLogger(HSSFRowShifter.class);

    public HSSFRowShifter(HSSFSheet sh) {
        super(sh);
    }

    @Override
    @NotImplemented
    public void updateNamedRanges(FormulaShifter formulaShifter) {
        throw new NotImplementedException("HSSFRowShifter.updateNamedRanges");
    }

    @Override
    @NotImplemented
    public void updateFormulas(FormulaShifter formulaShifter) {
        throw new NotImplementedException("updateFormulas");
    }

    @Override
    @NotImplemented
    public void updateConditionalFormatting(FormulaShifter formulaShifter) {
        throw new NotImplementedException("updateConditionalFormatting");
    }

    @Override
    @NotImplemented
    public void updateHyperlinks(FormulaShifter formulaShifter) {
        throw new NotImplementedException("updateHyperlinks");
    }

    @Internal(since="5.1.0")
    public void updateRowFormulas(HSSFRow row, FormulaShifter formulaShifter) {
        HSSFRowColShifter.updateRowFormulas(row, formulaShifter);
    }
}

