/*
 * Decompiled with CFR 0.152.
 */
package org.jaxen.expr;

import java.util.List;
import org.jaxen.expr.Expr;
import org.jaxen.expr.Step;

public interface LocationPath
extends Expr {
    public void addStep(Step var1);

    public List getSteps();

    public boolean isAbsolute();
}

