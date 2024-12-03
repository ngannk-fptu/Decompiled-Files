/*
 * Decompiled with CFR 0.152.
 */
package javax.el;

import javax.el.ELContext;
import javax.el.Expression;
import javax.el.MethodInfo;

public abstract class MethodExpression
extends Expression {
    private static final long serialVersionUID = 8163925562047324656L;

    public abstract MethodInfo getMethodInfo(ELContext var1);

    public abstract Object invoke(ELContext var1, Object[] var2);

    public boolean isParametersProvided() {
        return false;
    }

    @Deprecated
    public boolean isParmetersProvided() {
        return false;
    }
}

