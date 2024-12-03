/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar;

import com.ctc.wstx.shaded.msv_core.grammar.BinaryExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorBoolean;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid;

public final class InterleaveExp
extends BinaryExp {
    private static final long serialVersionUID = 1L;

    InterleaveExp(Expression left, Expression right) {
        super(left, right);
    }

    public Object visit(ExpressionVisitor visitor) {
        return visitor.onInterleave(this);
    }

    public Expression visit(ExpressionVisitorExpression visitor) {
        return visitor.onInterleave(this);
    }

    public boolean visit(ExpressionVisitorBoolean visitor) {
        return visitor.onInterleave(this);
    }

    public void visit(ExpressionVisitorVoid visitor) {
        visitor.onInterleave(this);
    }

    protected boolean calcEpsilonReducibility() {
        return this.exp1.isEpsilonReducible() && this.exp2.isEpsilonReducible();
    }
}

