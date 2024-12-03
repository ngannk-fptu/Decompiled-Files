/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar;

import com.ctc.wstx.shaded.msv_core.grammar.ChoiceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ConcurExp;
import com.ctc.wstx.shaded.msv_core.grammar.DataExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression;
import com.ctc.wstx.shaded.msv_core.grammar.InterleaveExp;
import com.ctc.wstx.shaded.msv_core.grammar.ListExp;
import com.ctc.wstx.shaded.msv_core.grammar.MixedExp;
import com.ctc.wstx.shaded.msv_core.grammar.OneOrMoreExp;
import com.ctc.wstx.shaded.msv_core.grammar.SequenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ValueExp;

public abstract class ExpressionCloner
implements ExpressionVisitorExpression {
    protected final ExpressionPool pool;

    protected ExpressionCloner(ExpressionPool pool) {
        this.pool = pool;
    }

    public Expression onChoice(ChoiceExp exp) {
        Expression np1 = exp.exp1.visit(this);
        Expression np2 = exp.exp2.visit(this);
        if (exp.exp1 == np1 && exp.exp2 == np2) {
            return exp;
        }
        return this.pool.createChoice(np1, np2);
    }

    public Expression onOneOrMore(OneOrMoreExp exp) {
        Expression np = exp.exp.visit(this);
        if (exp.exp == np) {
            return exp;
        }
        return this.pool.createOneOrMore(np);
    }

    public Expression onMixed(MixedExp exp) {
        Expression body = exp.exp.visit(this);
        if (exp.exp == body) {
            return exp;
        }
        return this.pool.createMixed(body);
    }

    public Expression onList(ListExp exp) {
        Expression body = exp.exp.visit(this);
        if (exp.exp == body) {
            return exp;
        }
        return this.pool.createList(body);
    }

    public Expression onSequence(SequenceExp exp) {
        Expression np1 = exp.exp1.visit(this);
        Expression np2 = exp.exp2.visit(this);
        if (exp.exp1 == np1 && exp.exp2 == np2) {
            return exp;
        }
        return this.pool.createSequence(np1, np2);
    }

    public Expression onConcur(ConcurExp exp) {
        return this.pool.createConcur(exp.exp1.visit(this), exp.exp2.visit(this));
    }

    public Expression onInterleave(InterleaveExp exp) {
        return this.pool.createInterleave(exp.exp1.visit(this), exp.exp2.visit(this));
    }

    public Expression onEpsilon() {
        return Expression.epsilon;
    }

    public Expression onNullSet() {
        return Expression.nullSet;
    }

    public Expression onAnyString() {
        return Expression.anyString;
    }

    public Expression onData(DataExp exp) {
        return exp;
    }

    public Expression onValue(ValueExp exp) {
        return exp;
    }
}

