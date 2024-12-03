/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.trex.ng;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.SequenceState;

public class ListState
extends SequenceState {
    protected Expression annealExpression(Expression exp) {
        return this.reader.pool.createList(exp);
    }
}

