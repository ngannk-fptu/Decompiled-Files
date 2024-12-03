/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ognl.Ognl
 *  ognl.OgnlException
 */
package org.apache.struts2.ognl;

import ognl.Ognl;
import ognl.OgnlException;

public interface OgnlGuard {
    public static final String EXPR_BLOCKED = "_ognl_guard_blocked";

    default public boolean isBlocked(String expr) throws OgnlException {
        return EXPR_BLOCKED.equals(this.parseExpression(expr));
    }

    default public Object parseExpression(String expr) throws OgnlException {
        if (this.isRawExpressionBlocked(expr)) {
            return EXPR_BLOCKED;
        }
        Object tree = Ognl.parseExpression((String)expr);
        if (this.isParsedTreeBlocked(tree)) {
            return EXPR_BLOCKED;
        }
        return tree;
    }

    public boolean isRawExpressionBlocked(String var1);

    public boolean isParsedTreeBlocked(Object var1);
}

