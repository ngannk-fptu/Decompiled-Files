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
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractReturningNodeVisitor<N extends Node, R>
implements NodeVisitor<N, R> {
    @Override
    public R exec(N node) {
        return this.visit(node);
    }

    protected abstract R visit(N var1);

    protected List<R> visitChildren(ParentNode<? extends N> node) {
        ArrayList results = Lists.newArrayListWithCapacity((int)node.numChildren());
        for (Node child : node.getChildren()) {
            results.add(this.visit(child));
        }
        return results;
    }

    protected List<R> visitChildrenAllowingConcurrentModification(ParentNode<? extends N> node) {
        ArrayList results = Lists.newArrayListWithCapacity((int)node.numChildren());
        for (Node child : Lists.newArrayList(node.getChildren())) {
            results.add(this.visit(child));
        }
        return results;
    }
}

