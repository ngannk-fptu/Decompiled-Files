/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorBoolean;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid;

public class ReferenceExp
extends Expression {
    public Expression exp = null;
    public final String name;
    private static final long serialVersionUID = 1L;

    public ReferenceExp(String name) {
        this.name = name;
    }

    public ReferenceExp(String name, Expression exp) {
        this(name);
        this.exp = exp;
    }

    protected final int calcHashCode() {
        return System.identityHashCode(this);
    }

    public boolean isDefined() {
        return this.exp != null;
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
        return visitor.onRef(this);
    }

    public final Expression visit(ExpressionVisitorExpression visitor) {
        return visitor.onRef(this);
    }

    public final boolean visit(ExpressionVisitorBoolean visitor) {
        return visitor.onRef(this);
    }

    public final void visit(ExpressionVisitorVoid visitor) {
        visitor.onRef(this);
    }
}

