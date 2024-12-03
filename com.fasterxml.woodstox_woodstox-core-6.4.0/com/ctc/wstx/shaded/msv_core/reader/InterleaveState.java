/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState;

public class InterleaveState
extends ExpressionWithChildState {
    protected Expression castExpression(Expression exp, Expression child) {
        if (exp == null) {
            return child;
        }
        return this.reader.pool.createInterleave(exp, child);
    }
}

