/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar.util;

import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionCloner;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.OtherExp;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import java.util.HashSet;
import java.util.Set;

public class RefExpRemover
extends ExpressionCloner {
    private final Set visitedElements = new HashSet();
    private final boolean recursive;

    public RefExpRemover(ExpressionPool pool, boolean _recursive) {
        super(pool);
        this.recursive = _recursive;
    }

    public Expression onElement(ElementExp exp) {
        if (!this.recursive) {
            return exp;
        }
        if (!this.visitedElements.contains(exp)) {
            this.visitedElements.add(exp);
            exp.contentModel = exp.contentModel.visit(this);
        }
        if (exp.contentModel == Expression.nullSet) {
            return Expression.nullSet;
        }
        return exp;
    }

    public Expression onAttribute(AttributeExp exp) {
        Expression content = exp.exp.visit(this);
        if (content == Expression.nullSet) {
            return Expression.nullSet;
        }
        return this.pool.createAttribute(exp.nameClass, content);
    }

    public Expression onRef(ReferenceExp exp) {
        return exp.exp.visit(this);
    }

    public Expression onOther(OtherExp exp) {
        return exp.exp.visit(this);
    }
}

