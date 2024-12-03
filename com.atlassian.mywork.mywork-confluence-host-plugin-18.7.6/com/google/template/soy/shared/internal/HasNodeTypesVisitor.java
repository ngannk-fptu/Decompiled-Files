/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.google.template.soy.shared.internal;

import com.google.common.base.Preconditions;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.SoyFileNode;
import com.google.template.soy.soytree.SoyNode;

public class HasNodeTypesVisitor
extends AbstractSoyNodeVisitor<Boolean> {
    private boolean found;
    private final Class<? extends SoyNode>[] nodeTypes;

    public HasNodeTypesVisitor(Class<? extends SoyNode>[] nodeTypes) {
        this.nodeTypes = nodeTypes;
    }

    @Override
    public Boolean exec(SoyNode soyNode) {
        Preconditions.checkArgument((boolean)(soyNode instanceof SoyFileNode));
        this.found = false;
        this.visit(soyNode);
        return this.found;
    }

    @Override
    protected void visitSoyNode(SoyNode node) {
        for (Class<? extends SoyNode> cls : this.nodeTypes) {
            if (!cls.isInstance(node)) continue;
            this.found = true;
            return;
        }
        if (node instanceof SoyNode.ParentSoyNode) {
            for (SoyNode child : ((SoyNode.ParentSoyNode)node).getChildren()) {
                if (this.found) {
                    return;
                }
                this.visit(child);
            }
        }
    }
}

