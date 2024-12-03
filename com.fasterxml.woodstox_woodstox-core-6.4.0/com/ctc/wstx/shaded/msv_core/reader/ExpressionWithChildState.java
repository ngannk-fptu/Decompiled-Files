/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionOwner;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

public abstract class ExpressionWithChildState
extends ExpressionState
implements ExpressionOwner {
    protected Expression exp;

    protected void startSelf() {
        super.startSelf();
        this.exp = this.initialExpression();
    }

    protected Expression initialExpression() {
        return null;
    }

    protected Expression defaultExpression() {
        return null;
    }

    public final void onEndChild(Expression childExpression) {
        this.exp = this.castExpression(this.exp, childExpression);
    }

    protected final Expression makeExpression() {
        if (this.exp == null) {
            this.exp = this.defaultExpression();
        }
        if (this.exp == null) {
            this.reader.reportError("GrammarReader.Abstract.MissingChildExpression");
            this.exp = Expression.nullSet;
        }
        return this.annealExpression(this.exp);
    }

    protected State createChildState(StartTagInfo tag) {
        return this.reader.createExpressionChildState(this, tag);
    }

    protected abstract Expression castExpression(Expression var1, Expression var2);

    protected Expression annealExpression(Expression exp) {
        return exp;
    }
}

