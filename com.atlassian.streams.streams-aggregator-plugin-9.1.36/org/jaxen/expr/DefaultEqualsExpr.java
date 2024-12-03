/*
 * Decompiled with CFR 0.152.
 */
package org.jaxen.expr;

import org.jaxen.expr.DefaultEqualityExpr;
import org.jaxen.expr.Expr;
import org.jaxen.function.NumberFunction;

class DefaultEqualsExpr
extends DefaultEqualityExpr {
    private static final long serialVersionUID = -8327599812627931648L;

    DefaultEqualsExpr(Expr lhs, Expr rhs) {
        super(lhs, rhs);
    }

    public String getOperator() {
        return "=";
    }

    public String toString() {
        return "[(DefaultEqualsExpr): " + this.getLHS() + ", " + this.getRHS() + "]";
    }

    protected boolean evaluateObjectObject(Object lhs, Object rhs) {
        if (this.eitherIsNumber(lhs, rhs) && (NumberFunction.isNaN((Double)lhs) || NumberFunction.isNaN((Double)rhs))) {
            return false;
        }
        return lhs.equals(rhs);
    }
}

