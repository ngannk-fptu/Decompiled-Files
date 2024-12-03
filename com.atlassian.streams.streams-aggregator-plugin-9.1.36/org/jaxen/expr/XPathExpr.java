/*
 * Decompiled with CFR 0.152.
 */
package org.jaxen.expr;

import java.io.Serializable;
import java.util.List;
import org.jaxen.Context;
import org.jaxen.JaxenException;
import org.jaxen.expr.Expr;

public interface XPathExpr
extends Serializable {
    public Expr getRootExpr();

    public void setRootExpr(Expr var1);

    public String getText();

    public void simplify();

    public List asList(Context var1) throws JaxenException;
}

