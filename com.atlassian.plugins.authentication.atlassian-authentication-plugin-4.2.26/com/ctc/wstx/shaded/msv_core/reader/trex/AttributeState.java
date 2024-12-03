/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.trex;

import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.trex.NameClassAndExpressionState;
import com.ctc.wstx.shaded.msv_core.reader.trex.TREXBaseReader;

public class AttributeState
extends NameClassAndExpressionState {
    protected boolean firstChild = true;

    protected Expression initialExpression() {
        return Expression.anyString;
    }

    protected String getNamespace() {
        String ns = this.startTag.getAttribute("ns");
        boolean global = "true".equals(this.startTag.getAttribute("global"));
        if (ns != null) {
            return ns;
        }
        if (global) {
            return ((TREXBaseReader)this.reader).targetNamespace;
        }
        return "";
    }

    protected Expression castExpression(Expression initialExpression, Expression newChild) {
        if (!this.firstChild) {
            this.reader.reportError("GrammarReader.Abstract.MoreThanOneChildExpression");
        }
        this.firstChild = false;
        return newChild;
    }

    protected Expression annealExpression(Expression contentModel) {
        Expression e = this.reader.pool.createAttribute(this.nameClass, contentModel);
        if (e instanceof AttributeExp) {
            this.reader.setDeclaredLocationOf(e);
        }
        return e;
    }
}

