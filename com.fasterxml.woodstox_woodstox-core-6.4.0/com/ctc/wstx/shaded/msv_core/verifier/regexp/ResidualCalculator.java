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
import com.ctc.wstx.shaded.msv_core.verifier.regexp.ElementToken;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.OptimizationTag;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.Token;

public class ResidualCalculator
implements ExpressionVisitorExpression {
    protected Token token;
    protected final ExpressionPool pool;

    public ResidualCalculator(ExpressionPool pool) {
        this.pool = pool;
    }

    final Expression calcResidual(Expression exp, ElementToken token) {
        if (token.acceptedPatterns != null && token.acceptedPatterns.length == 1) {
            Expression residual;
            OptimizationTag ot;
            if (exp.verifierTag == null) {
                ot = new OptimizationTag();
                exp.verifierTag = ot;
            } else {
                ot = (OptimizationTag)exp.verifierTag;
                residual = (Expression)ot.simpleElementTokenResidual.get(token.acceptedPatterns[0]);
                if (residual != null) {
                    return residual;
                }
            }
            this.token = token;
            residual = exp.visit(this);
            ot.simpleElementTokenResidual.put(token.acceptedPatterns[0], residual);
            return residual;
        }
        this.token = token;
        return exp.visit(this);
    }

    public final Expression calcResidual(Expression exp, Token token) {
        if (token instanceof ElementToken) {
            return this.calcResidual(exp, (ElementToken)token);
        }
        this.token = token;
        Expression r = exp.visit(this);
        if (token.isIgnorable()) {
            r = this.pool.createChoice(r, exp);
        }
        return r;
    }

    public Expression onAttribute(AttributeExp exp) {
        if (this.token.match(exp)) {
            return Expression.epsilon;
        }
        return Expression.nullSet;
    }

    public Expression onChoice(ChoiceExp exp) {
        return this.pool.createChoice(exp.exp1.visit(this), exp.exp2.visit(this));
    }

    public Expression onElement(ElementExp exp) {
        if (this.token.match(exp)) {
            return Expression.epsilon;
        }
        return Expression.nullSet;
    }

    public Expression onOneOrMore(OneOrMoreExp exp) {
        return this.pool.createSequence(exp.exp.visit(this), this.pool.createZeroOrMore(exp.exp));
    }

    public Expression onMixed(MixedExp exp) {
        if (this.token.matchAnyString()) {
            return exp;
        }
        return this.pool.createMixed(exp.exp.visit(this));
    }

    public Expression onEpsilon() {
        return Expression.nullSet;
    }

    public Expression onNullSet() {
        return Expression.nullSet;
    }

    public Expression onAnyString() {
        if (this.token.matchAnyString()) {
            return Expression.anyString;
        }
        return Expression.nullSet;
    }

    public Expression onRef(ReferenceExp exp) {
        return exp.exp.visit(this);
    }

    public Expression onOther(OtherExp exp) {
        return exp.exp.visit(this);
    }

    public Expression onSequence(SequenceExp exp) {
        Expression r = this.pool.createSequence(exp.exp1.visit(this), exp.exp2);
        if (exp.exp1.isEpsilonReducible()) {
            return this.pool.createChoice(r, exp.exp2.visit(this));
        }
        return r;
    }

    public Expression onData(DataExp exp) {
        if (this.token.match(exp)) {
            return Expression.epsilon;
        }
        return Expression.nullSet;
    }

    public Expression onValue(ValueExp exp) {
        if (this.token.match(exp)) {
            return Expression.epsilon;
        }
        return Expression.nullSet;
    }

    public Expression onList(ListExp exp) {
        if (this.token.match(exp)) {
            return Expression.epsilon;
        }
        return Expression.nullSet;
    }

    public Expression onConcur(ConcurExp exp) {
        return this.pool.createConcur(exp.exp1.visit(this), exp.exp2.visit(this));
    }

    public Expression onInterleave(InterleaveExp exp) {
        return this.pool.createChoice(this.pool.createInterleave(exp.exp1.visit(this), exp.exp2), this.pool.createInterleave(exp.exp1, exp.exp2.visit(this)));
    }
}

