/*
 * Decompiled with CFR 0.152.
 */
package org.jaxen.expr;

import org.jaxen.expr.DefaultEqualityExpr;
import org.jaxen.expr.Expr;
import org.jaxen.function.NumberFunction;

class DefaultNotEqualsExpr
extends DefaultEqualityExpr {
    private static final long serialVersionUID = -8001267398136979152L;

    DefaultNotEqualsExpr(Expr lhs, Expr rhs) {
        super(lhs, rhs);
    }

    public String getOperator() {
        return "!=";
    }

    public String toString() {
        return "[(DefaultNotEqualsExpr): " + this.getLHS() + ", " + this.getRHS() + "]";
    }

    protected boolean evaluateObjectObject(Object lhs, Object rhs) {
        if (this.eitherIsNumber(lhs, rhs) && (NumberFunction.isNaN((Double)lhs) || NumberFunction.isNaN((Double)rhs))) {
            return true;
        }
        return !lhs.equals(rhs);
    }
}

