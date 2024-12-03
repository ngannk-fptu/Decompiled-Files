/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.exprtree;

import com.google.template.soy.exprtree.AbstractExprNode;
import com.google.template.soy.exprtree.ExprNode;

public abstract class AbstractPrimitiveNode
extends AbstractExprNode
implements ExprNode.PrimitiveNode {
    public AbstractPrimitiveNode() {
    }

    protected AbstractPrimitiveNode(AbstractPrimitiveNode orig) {
        super(orig);
    }
}

