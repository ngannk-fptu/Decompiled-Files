/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.exprtree;

import com.google.template.soy.basetree.AbstractNode;
import com.google.template.soy.exprtree.ExprNode;

public abstract class AbstractExprNode
extends AbstractNode
implements ExprNode {
    @Override
    public ExprNode.ParentExprNode getParent() {
        return (ExprNode.ParentExprNode)super.getParent();
    }

    protected AbstractExprNode() {
    }

    protected AbstractExprNode(AbstractExprNode orig) {
        super(orig);
    }

    @Override
    public abstract ExprNode clone();
}

