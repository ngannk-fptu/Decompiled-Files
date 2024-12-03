/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.trex;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionOwner;
import com.ctc.wstx.shaded.msv_core.reader.SimpleState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.trex.TREXBaseReader;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

public abstract class DefineState
extends SimpleState
implements ExpressionOwner {
    protected Expression exp = null;
    private ReferenceExp ref;

    public final ReferenceExp getRef() {
        return this.ref;
    }

    public final void onEndChild(Expression child) {
        this.exp = this.exp == null ? child : this.reader.pool.createSequence(this.exp, child);
    }

    protected void startSelf() {
        super.startSelf();
        this.ref = this.getReference();
    }

    protected void endSelf() {
        if (this.exp == null) {
            this.reader.reportError("GrammarReader.Abstract.MissingChildExpression");
            this.exp = Expression.nullSet;
        }
        if (this.ref == null) {
            return;
        }
        TREXBaseReader reader = (TREXBaseReader)this.reader;
        String combine = this.startTag.getCollapsedAttribute("combine");
        this.exp = this.callInterceptExpression(this.exp);
        Expression newexp = this.doCombine(this.ref, this.exp, combine);
        if (newexp == null) {
            reader.reportError("TREXGrammarReader.BadCombine", (Object)combine);
        } else {
            this.ref.exp = newexp;
        }
        reader.setDeclaredLocationOf(this.ref);
        ((ExpressionOwner)((Object)this.parentState)).onEndChild(this.ref);
    }

    protected State createChildState(StartTagInfo tag) {
        return this.reader.createExpressionChildState(this, tag);
    }

    protected ReferenceExp getReference() {
        String name = this.startTag.getCollapsedAttribute("name");
        if (name == null) {
            this.reader.reportError("GrammarReader.MissingAttribute", (Object)"ref", (Object)"name");
            return null;
        }
        TREXBaseReader reader = (TREXBaseReader)this.reader;
        return reader.grammar.namedPatterns.getOrCreate(name);
    }

    protected abstract Expression doCombine(ReferenceExp var1, Expression var2, String var3);
}

