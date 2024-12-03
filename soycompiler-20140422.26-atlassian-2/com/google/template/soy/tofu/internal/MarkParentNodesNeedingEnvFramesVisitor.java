/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.tofu.internal;

import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.SoyNode;

class MarkParentNodesNeedingEnvFramesVisitor
extends AbstractSoyNodeVisitor<Void> {
    MarkParentNodesNeedingEnvFramesVisitor() {
    }

    @Override
    protected void visitSoyNode(SoyNode node) {
        if (!(node instanceof SoyNode.ParentSoyNode)) {
            return;
        }
        SoyNode.ParentSoyNode nodeAsParent = (SoyNode.ParentSoyNode)node;
        this.visitChildren(nodeAsParent);
        if (nodeAsParent instanceof SoyNode.LocalVarBlockNode) {
            nodeAsParent.setNeedsEnvFrameDuringInterp(true);
        } else if (nodeAsParent instanceof SoyNode.BlockNode) {
            boolean needsEnvFrameDuringInterp = false;
            for (SoyNode child : nodeAsParent.getChildren()) {
                if (!(child instanceof SoyNode.LocalVarInlineNode)) continue;
                needsEnvFrameDuringInterp = true;
                break;
            }
            nodeAsParent.setNeedsEnvFrameDuringInterp(needsEnvFrameDuringInterp);
        } else {
            nodeAsParent.setNeedsEnvFrameDuringInterp(false);
        }
    }
}

