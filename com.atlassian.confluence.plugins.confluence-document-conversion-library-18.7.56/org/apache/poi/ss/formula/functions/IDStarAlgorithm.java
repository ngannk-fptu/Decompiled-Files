/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.ValueEval;

public interface IDStarAlgorithm {
    public boolean processMatch(ValueEval var1);

    public ValueEval getResult();

    default public boolean allowEmptyMatchField() {
        return false;
    }
}

