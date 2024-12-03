/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionOwner;
import com.ctc.wstx.shaded.msv_core.reader.SimpleState;

public abstract class ExpressionState
extends SimpleState {
    protected void endSelf() {
        Expression exp = this.reader.interceptExpression(this, this.makeExpression());
        if (this.parentState != null) {
            ((ExpressionOwner)((Object)this.parentState)).onEndChild(exp);
        }
        super.endSelf();
    }

    protected abstract Expression makeExpression();
}

