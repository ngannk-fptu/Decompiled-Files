/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class Na {
    public static ValueEval evaluate(ValueEval[] args, int srcCellRow, int srcCellCol) {
        return args.length != 0 ? ErrorEval.VALUE_INVALID : ErrorEval.NA;
    }
}

