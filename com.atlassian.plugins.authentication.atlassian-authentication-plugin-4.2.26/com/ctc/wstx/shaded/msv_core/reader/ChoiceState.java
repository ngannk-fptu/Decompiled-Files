/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState;

public class ChoiceState
extends ExpressionWithChildState {
    protected boolean allowEmptyChoice;

    public ChoiceState() {
        this(false);
    }

    public ChoiceState(boolean allowEmptyChoice) {
        this.allowEmptyChoice = allowEmptyChoice;
    }

    protected Expression initialExpression() {
        return this.allowEmptyChoice ? Expression.nullSet : null;
    }

    protected Expression castExpression(Expression exp, Expression child) {
        if (exp == null) {
            return child;
        }
        return this.reader.pool.createChoice(exp, child);
    }
}

