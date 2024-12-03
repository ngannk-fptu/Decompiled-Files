/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.google.template.soy.exprtree;

import com.google.common.base.Preconditions;
import com.google.template.soy.exprtree.AbstractParentExprNode;
import com.google.template.soy.exprtree.ExprNode;

public class ExprRootNode<N extends ExprNode>
extends AbstractParentExprNode {
    public ExprRootNode(N child) {
        this.addChild((ExprNode)child);
    }

    protected ExprRootNode(ExprRootNode<N> orig) {
        super(orig);
    }

    @Override
    public ExprNode.Kind getKind() {
        return ExprNode.Kind.EXPR_ROOT_NODE;
    }

    @Override
    public N getChild(int index) {
        Preconditions.checkArgument((index == 0 ? 1 : 0) != 0);
        ExprNode child = super.getChild(0);
        return (N)child;
    }

    @Override
    public String toSourceString() {
        return this.getChild(0).toSourceString();
    }

    @Override
    public ExprRootNode<N> clone() {
        return new ExprRootNode<N>(this);
    }
}

