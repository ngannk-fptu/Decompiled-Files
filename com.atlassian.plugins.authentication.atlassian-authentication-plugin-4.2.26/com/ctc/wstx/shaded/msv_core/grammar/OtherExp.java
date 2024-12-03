/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorBoolean;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid;

public class OtherExp
extends Expression {
    public Expression exp;
    private static final long serialVersionUID = 1L;

    public String printName() {
        String className = this.getClass().getName();
        int idx = className.lastIndexOf(46);
        if (idx >= 0) {
            className = className.substring(idx + 1);
        }
        return className;
    }

    public OtherExp() {
    }

    protected final int calcHashCode() {
        return System.identityHashCode(this);
    }

    public OtherExp(Expression exp) {
        this();
        this.exp = exp;
    }

    public boolean equals(Object o) {
        return this == o;
    }

    protected boolean calcEpsilonReducibility() {
        if (this.exp == null) {
            return false;
        }
        return this.exp.isEpsilonReducible();
    }

    public final Object visit(ExpressionVisitor visitor) {
        return visitor.onOther(this);
    }

    public final Expression visit(ExpressionVisitorExpression visitor) {
        return visitor.onOther(this);
    }

    public final boolean visit(ExpressionVisitorBoolean visitor) {
        return visitor.onOther(this);
    }

    public final void visit(ExpressionVisitorVoid visitor) {
        visitor.onOther(this);
    }
}

