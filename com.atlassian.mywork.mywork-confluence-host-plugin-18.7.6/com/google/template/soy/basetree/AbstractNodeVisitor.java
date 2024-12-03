/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package com.google.template.soy.basetree;

import com.google.common.collect.Lists;
import com.google.template.soy.basetree.Node;
import com.google.template.soy.basetree.NodeVisitor;
import com.google.template.soy.basetree.ParentNode;

public abstract class AbstractNodeVisitor<N extends Node, R>
implements NodeVisitor<N, R> {
    @Override
    public R exec(N node) {
        this.visit(node);
        return null;
    }

    protected abstract void visit(N var1);

    protected void visitChildren(ParentNode<? extends N> node) {
        for (Node child : node.getChildren()) {
            this.visit(child);
        }
    }

    protected void visitChildrenAllowingConcurrentModification(ParentNode<? extends N> node) {
        for (Node child : Lists.newArrayList(node.getChildren())) {
            this.visit(child);
        }
    }
}

