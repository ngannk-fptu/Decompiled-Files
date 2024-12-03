/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorBoolean;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid;
import com.ctc.wstx.shaded.msv_core.grammar.UnaryExp;

public final class OneOrMoreExp
extends UnaryExp {
    private static final long serialVersionUID = 1L;

    OneOrMoreExp(Expression exp) {
        super(exp);
    }

    public Object visit(ExpressionVisitor visitor) {
        return visitor.onOneOrMore(this);
    }

    public Expression visit(ExpressionVisitorExpression visitor) {
        return visitor.onOneOrMore(this);
    }

    public boolean visit(ExpressionVisitorBoolean visitor) {
        return visitor.onOneOrMore(this);
    }

    public void visit(ExpressionVisitorVoid visitor) {
        visitor.onOneOrMore(this);
    }

    protected boolean calcEpsilonReducibility() {
        return this.exp.isEpsilonReducible();
    }
}

