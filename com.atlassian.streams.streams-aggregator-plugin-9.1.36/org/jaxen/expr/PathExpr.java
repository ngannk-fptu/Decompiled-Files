/*
 * Decompiled with CFR 0.152.
 */
package org.jaxen.expr;

import org.jaxen.expr.Expr;
import org.jaxen.expr.LocationPath;

public interface PathExpr
extends Expr {
    public Expr getFilterExpr();

    public void setFilterExpr(Expr var1);

    public LocationPath getLocationPath();
}

