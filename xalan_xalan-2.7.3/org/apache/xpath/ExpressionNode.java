/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath;

import javax.xml.transform.SourceLocator;

public interface ExpressionNode
extends SourceLocator {
    public void exprSetParent(ExpressionNode var1);

    public ExpressionNode exprGetParent();

    public void exprAddChild(ExpressionNode var1, int var2);

    public ExpressionNode exprGetChild(int var1);

    public int exprGetNumChildren();
}

