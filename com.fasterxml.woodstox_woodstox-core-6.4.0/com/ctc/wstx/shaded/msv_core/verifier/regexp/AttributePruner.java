/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.verifier.regexp;

import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionCloner;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.OtherExp;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.OptimizationTag;

public class AttributePruner
extends ExpressionCloner {
    public AttributePruner(ExpressionPool pool) {
        super(pool);
    }

    public Expression onAttribute(AttributeExp exp) {
        return Expression.nullSet;
    }

    public Expression onRef(ReferenceExp exp) {
        return exp.exp.visit(this);
    }

    public Expression onOther(OtherExp exp) {
        return exp.exp.visit(this);
    }

    public Expression onElement(ElementExp exp) {
        return exp;
    }

    public final Expression prune(Expression exp) {
        Expression r;
        OptimizationTag ot = (OptimizationTag)exp.verifierTag;
        if (ot == null) {
            ot = new OptimizationTag();
            exp.verifierTag = ot;
        } else if (ot.attributePrunedExpression != null) {
            return ot.attributePrunedExpression;
        }
        ot.attributePrunedExpression = r = exp.visit(this);
        return r;
    }
}

