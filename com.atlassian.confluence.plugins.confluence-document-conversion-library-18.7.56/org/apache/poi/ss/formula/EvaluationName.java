/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula;

import org.apache.poi.ss.formula.ptg.NamePtg;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.util.Internal;

@Internal
public interface EvaluationName {
    public String getNameText();

    public boolean isFunctionName();

    public boolean hasFormula();

    public Ptg[] getNameDefinition();

    public boolean isRange();

    public NamePtg createPtg();
}

