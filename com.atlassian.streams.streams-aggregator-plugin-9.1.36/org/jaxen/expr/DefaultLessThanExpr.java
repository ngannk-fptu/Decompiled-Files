/*
 * Decompiled with CFR 0.152.
 */
package org.jaxen.expr;

import org.jaxen.expr.DefaultRelationalExpr;
import org.jaxen.expr.Expr;

class DefaultLessThanExpr
extends DefaultRelationalExpr {
    private static final long serialVersionUID = 8423816025305001283L;

    DefaultLessThanExpr(Expr lhs, Expr rhs) {
        super(lhs, rhs);
    }

    public String getOperator() {
        return "<";
    }

    protected boolean evaluateDoubleDouble(Double lhs, Double rhs) {
        return lhs.compareTo(rhs) < 0;
    }
}

