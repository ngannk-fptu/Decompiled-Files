/*
 * Decompiled with CFR 0.152.
 */
package org.jaxen.expr;

import org.jaxen.Context;
import org.jaxen.JaxenException;
import org.jaxen.expr.Expr;
import org.jaxen.expr.Predicated;

public interface FilterExpr
extends Expr,
Predicated {
    public boolean asBoolean(Context var1) throws JaxenException;

    public Expr getExpr();
}

