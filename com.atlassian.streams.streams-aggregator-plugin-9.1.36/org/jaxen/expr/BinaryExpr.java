/*
 * Decompiled with CFR 0.152.
 */
package org.jaxen.expr;

import org.jaxen.expr.Expr;

public interface BinaryExpr
extends Expr {
    public Expr getLHS();

    public Expr getRHS();

    public String getOperator();
}

