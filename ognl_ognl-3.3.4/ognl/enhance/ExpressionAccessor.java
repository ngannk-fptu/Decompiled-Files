/*
 * Decompiled with CFR 0.152.
 */
package ognl.enhance;

import ognl.Node;
import ognl.OgnlContext;

public interface ExpressionAccessor {
    public Object get(OgnlContext var1, Object var2);

    public void set(OgnlContext var1, Object var2, Object var3);

    public void setExpression(Node var1);
}

