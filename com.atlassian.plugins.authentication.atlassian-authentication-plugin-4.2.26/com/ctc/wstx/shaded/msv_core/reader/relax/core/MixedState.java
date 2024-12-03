/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.relax.core;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState;

public class MixedState
extends ExpressionWithChildState {
    protected Expression castExpression(Expression current, Expression child) {
        if (current != null) {
            this.reader.reportError("GrammarReader.Abstract.MoreThanOneChildExpression");
        }
        return child;
    }

    protected Expression annealExpression(Expression exp) {
        return this.reader.pool.createMixed(exp);
    }
}

