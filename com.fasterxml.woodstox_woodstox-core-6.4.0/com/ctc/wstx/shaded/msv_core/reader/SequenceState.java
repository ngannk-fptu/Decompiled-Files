/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState;

public class SequenceState
extends ExpressionWithChildState {
    protected boolean allowEmptySequence;

    public SequenceState() {
        this(false);
    }

    public SequenceState(boolean allowEmptySequence) {
        this.allowEmptySequence = allowEmptySequence;
    }

    protected Expression initialExpression() {
        return this.allowEmptySequence ? Expression.epsilon : null;
    }

    protected Expression castExpression(Expression exp, Expression child) {
        if (exp == null) {
            return child;
        }
        return this.reader.pool.createSequence(exp, child);
    }
}

