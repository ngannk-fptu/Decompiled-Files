/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.relax.core;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionOwner;
import com.ctc.wstx.shaded.msv_core.reader.SimpleState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.RELAXCoreReader;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

abstract class ClauseState
extends SimpleState
implements ExpressionOwner {
    protected Expression exp = this.initialExpression();

    ClauseState() {
    }

    protected State createChildState(StartTagInfo tag) {
        if (tag.localName.equals("ref")) {
            return this.getReader().getStateFactory().refRole(this, tag);
        }
        if (tag.localName.equals("attribute")) {
            return this.getReader().getStateFactory().attribute(this, tag);
        }
        return null;
    }

    protected Expression initialExpression() {
        return Expression.epsilon;
    }

    protected Expression castExpression(Expression exp, Expression child) {
        return this.reader.pool.createSequence(exp, child);
    }

    protected RELAXCoreReader getReader() {
        return (RELAXCoreReader)this.reader;
    }

    public final void onEndChild(Expression childExpression) {
        this.exp = this.castExpression(this.exp, childExpression);
    }
}

