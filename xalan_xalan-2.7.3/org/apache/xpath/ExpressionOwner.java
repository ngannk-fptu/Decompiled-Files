/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath;

import org.apache.xpath.Expression;

public interface ExpressionOwner {
    public Expression getExpression();

    public void setExpression(Expression var1);
}

