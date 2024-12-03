/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.relax;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionOwner;
import com.ctc.wstx.shaded.msv_core.reader.SimpleState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

public abstract class HedgeRuleBaseState
extends SimpleState
implements ExpressionOwner {
    private Expression contentModel = null;

    public void onEndChild(Expression exp) {
        if (this.contentModel != null) {
            this.reader.reportError("GrammarReader.Abstract.MoreThanOneChildExpression");
        }
        this.contentModel = exp;
    }

    protected final void endSelf() {
        super.endSelf();
        if (this.contentModel == null) {
            this.reader.reportError("GrammarReader.Abstract.MissingChildExpression");
            return;
        }
        this.endSelf(this.contentModel);
    }

    protected abstract void endSelf(Expression var1);

    protected State createChildState(StartTagInfo tag) {
        return this.reader.createExpressionChildState(this, tag);
    }
}

