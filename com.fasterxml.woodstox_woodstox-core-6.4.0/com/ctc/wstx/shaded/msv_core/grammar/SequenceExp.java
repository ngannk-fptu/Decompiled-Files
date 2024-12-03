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

public final class SequenceExp
extends BinaryExp {
    private static final long serialVersionUID = 1L;

    SequenceExp(Expression left, Expression right) {
        super(left, right);
    }

    public Object visit(ExpressionVisitor visitor) {
        return visitor.onSequence(this);
    }

    public Expression visit(ExpressionVisitorExpression visitor) {
        return visitor.onSequence(this);
    }

    public boolean visit(ExpressionVisitorBoolean visitor) {
        return visitor.onSequence(this);
    }

    public void visit(ExpressionVisitorVoid visitor) {
        visitor.onSequence(this);
    }

    protected boolean calcEpsilonReducibility() {
        return this.exp1.isEpsilonReducible() && this.exp2.isEpsilonReducible();
    }
}

