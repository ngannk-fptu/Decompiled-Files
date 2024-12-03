/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.eval;

import org.apache.poi.ss.formula.EvaluationName;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class ExternalNameEval
implements ValueEval {
    private final EvaluationName _name;

    public ExternalNameEval(EvaluationName name) {
        this._name = name;
    }

    public EvaluationName getName() {
        return this._name;
    }

    public String toString() {
        return this.getClass().getName() + " [" + this._name.getNameText() + "]";
    }
}

