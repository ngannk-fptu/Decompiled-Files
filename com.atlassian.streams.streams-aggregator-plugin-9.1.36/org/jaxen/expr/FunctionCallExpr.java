/*
 * Decompiled with CFR 0.152.
 */
package org.jaxen.expr;

import java.util.List;
import org.jaxen.expr.Expr;

public interface FunctionCallExpr
extends Expr {
    public String getPrefix();

    public String getFunctionName();

    public void addParameter(Expr var1);

    public List getParameters();
}

