/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionWithoutChildState;

public class TerminalState
extends ExpressionWithoutChildState {
    private final Expression exp;

    public TerminalState(Expression exp) {
        this.exp = exp;
    }

    protected Expression makeExpression() {
        return this.exp;
    }
}

