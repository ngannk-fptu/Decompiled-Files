/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.verifier.regexp;

import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ConcurExp;
import com.ctc.wstx.shaded.msv_core.grammar.DataExp;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression;
import com.ctc.wstx.shaded.msv_core.grammar.InterleaveExp;
import com.ctc.wstx.shaded.msv_core.grammar.ListExp;
import com.ctc.wstx.shaded.msv_core.grammar.MixedExp;
import com.ctc.wstx.shaded.msv_core.grammar.OneOrMoreExp;
import com.ctc.wstx.shaded.msv_core.grammar.OtherExp;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.SequenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ValueExp;

public class AttributePicker
implements ExpressionVisitorExpression {
    private final ExpressionPool pool;

    public AttributePicker(ExpressionPool pool) {
        this.pool = pool;
    }

    public Expression onElement(ElementExp exp) {
        return AttributeExp.epsilon;
    }

    public Expression onMixed(MixedExp exp) {
        return exp.exp.visit(this);
    }

    public Expression onAnyString() {
        return Expression.epsilon;
    }

    public Expression onEpsilon() {
        return Expression.epsilon;
    }

    public Expression onNullSet() {
        return Expression.nullSet;
    }

    public Expression onRef(ReferenceExp exp) {
        return exp.exp.visit(this);
    }

    public Expression onOther(OtherExp exp) {
        return exp.exp.visit(this);
    }

    public Expression onData(DataExp exp) {
        return Expression.epsilon;
    }

    public Expression onValue(ValueExp exp) {
        return Expression.epsilon;
    }

    public Expression onList(ListExp exp) {
        return Expression.epsilon;
    }

    public Expression onAttribute(AttributeExp exp) {
        return exp;
    }

    public Expression onOneOrMore(OneOrMoreExp exp) {
        return exp.exp.visit(this);
    }

    public Expression onSequence(SequenceExp exp) {
        Expression ex1 = exp.exp1.visit(this);
        Expression ex2 = exp.exp2.visit(this);
        if (ex1.isEpsilonReducible()) {
            if (ex2.isEpsilonReducible()) {
                return Expression.epsilon;
            }
            return ex2;
        }
        return ex1;
    }

    public Expression onInterleave(InterleaveExp exp) {
        Expression ex1 = exp.exp1.visit(this);
        Expression ex2 = exp.exp2.visit(this);
        if (ex1.isEpsilonReducible()) {
            if (ex2.isEpsilonReducible()) {
                return Expression.epsilon;
            }
            return ex2;
        }
        return ex1;
    }

    public Expression onConcur(ConcurExp exp) {
        return Expression.epsilon;
    }

    public Expression onChoice(ChoiceExp exp) {
        Expression ex1 = exp.exp1.visit(this);
        Expression ex2 = exp.exp2.visit(this);
        if (ex1.isEpsilonReducible() || ex2.isEpsilonReducible()) {
            return Expression.epsilon;
        }
        return this.pool.createChoice(ex1, ex2);
    }
}

