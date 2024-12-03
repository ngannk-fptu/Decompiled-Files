/*
 * Decompiled with CFR 0.152.
 */
package org.jaxen.expr;

import org.jaxen.expr.Expr;

public interface VariableReferenceExpr
extends Expr {
    public String getPrefix();

    public String getVariableName();
}

