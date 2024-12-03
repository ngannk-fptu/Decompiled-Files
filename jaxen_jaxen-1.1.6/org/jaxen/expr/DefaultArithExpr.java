/*
 * Decompiled with CFR 0.152.
 */
package org.jaxen.expr;

import org.jaxen.expr.DefaultBinaryExpr;
import org.jaxen.expr.Expr;

abstract class DefaultArithExpr
extends DefaultBinaryExpr {
    DefaultArithExpr(Expr lhs, Expr rhs) {
        super(lhs, rhs);
    }

    public String toString() {
        return "[(DefaultArithExpr): " + this.getLHS() + ", " + this.getRHS() + "]";
    }
}

