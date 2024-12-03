/*
 * Decompiled with CFR 0.152.
 */
package javax.el;

import javax.el.ValueExpression;

public abstract class VariableMapper {
    public abstract ValueExpression resolveVariable(String var1);

    public abstract ValueExpression setVariable(String var1, ValueExpression var2);
}

