/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.relax.core;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionOwner;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.ElementRuleBaseState;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

public class ElementRuleWithHedgeState
extends ElementRuleBaseState
implements ExpressionOwner {
    protected Expression contentModel = null;

    public void onEndChild(Expression exp) {
        if (this.contentModel != null) {
            this.reader.reportError("GrammarReader.Abstract.MoreThanOneChildExpression");
        }
        this.contentModel = exp;
    }

    protected Expression getContentModel() {
        if (this.contentModel == null) {
            this.reader.reportError("GrammarReader.Abstract.MissingChildExpression");
            return Expression.epsilon;
        }
        return this.contentModel;
    }

    protected State createChildState(StartTagInfo tag) {
        if (!tag.namespaceURI.equals("http://www.xml.gr.jp/xmlns/relaxCore")) {
            return null;
        }
        State next = this.reader.createExpressionChildState(this, tag);
        if (next != null) {
            return next;
        }
        return super.createChildState(tag);
    }
}

