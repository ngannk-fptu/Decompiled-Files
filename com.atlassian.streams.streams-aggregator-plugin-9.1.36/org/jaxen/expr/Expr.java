/*
 * Decompiled with CFR 0.152.
 */
package org.jaxen.expr;

import java.io.Serializable;
import org.jaxen.Context;
import org.jaxen.JaxenException;

public interface Expr
extends Serializable {
    public String getText();

    public Expr simplify();

    public Object evaluate(Context var1) throws JaxenException;
}

