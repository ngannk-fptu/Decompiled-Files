/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.eval;

import org.apache.poi.ss.formula.eval.ValueEval;

public final class MissingArgEval
implements ValueEval {
    public static final MissingArgEval instance = new MissingArgEval();

    private MissingArgEval() {
    }
}

