/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.Function0Arg;
import org.apache.poi.util.Removal;

@Deprecated
@Removal(version="6.0.0")
public abstract class Fixed0ArgFunction
implements Function0Arg {
    @Override
    public final ValueEval evaluate(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
        if (args.length != 0) {
            return ErrorEval.VALUE_INVALID;
        }
        return this.evaluate(srcRowIndex, srcColumnIndex);
    }
}

