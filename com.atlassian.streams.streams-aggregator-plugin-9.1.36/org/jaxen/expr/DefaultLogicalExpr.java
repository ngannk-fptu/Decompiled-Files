/*
 * Decompiled with CFR 0.152.
 */
package org.jaxen.expr;

import org.jaxen.expr.DefaultTruthExpr;
import org.jaxen.expr.Expr;
import org.jaxen.expr.LogicalExpr;

abstract class DefaultLogicalExpr
extends DefaultTruthExpr
implements LogicalExpr {
    DefaultLogicalExpr(Expr lhs, Expr rhs) {
        super(lhs, rhs);
    }
}

