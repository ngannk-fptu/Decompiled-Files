/*
 * Decompiled with CFR 0.152.
 */
package org.jaxen.expr;

import org.jaxen.expr.AdditiveExpr;
import org.jaxen.expr.DefaultArithExpr;
import org.jaxen.expr.Expr;

abstract class DefaultAdditiveExpr
extends DefaultArithExpr
implements AdditiveExpr {
    DefaultAdditiveExpr(Expr lhs, Expr rhs) {
        super(lhs, rhs);
    }

    public String toString() {
        return "[(" + this.getClass().getName() + "): " + this.getLHS() + ", " + this.getRHS() + "]";
    }
}

