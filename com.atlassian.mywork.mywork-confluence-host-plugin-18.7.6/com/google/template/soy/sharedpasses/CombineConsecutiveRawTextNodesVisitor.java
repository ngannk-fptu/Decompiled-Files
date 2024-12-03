/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package com.google.template.soy.sharedpasses;

import com.google.common.collect.Lists;
import com.google.template.soy.base.internal.IdGenerator;
import com.google.template.soy.basetree.Node;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.RawTextNode;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.soytree.SoyNode;
import java.util.ArrayList;
import java.util.List;

public class CombineConsecutiveRawTextNodesVisitor
extends AbstractSoyNodeVisitor<Void> {
    private IdGenerator nodeIdGen;

    @Override
    public Void exec(SoyNode node) {
        this.nodeIdGen = node.getNearestAncestor(SoyFileSetNode.class).getNodeIdGenerator();
        return (Void)super.exec(node);
    }

    @Override
    protected void visitSoyNode(SoyNode node) {
        if (node instanceof SoyNode.ParentSoyNode) {
            this.visitChildren((SoyNode.ParentSoyNode)node);
        }
        if (!(node instanceof SoyNode.BlockNode)) {
            return;
        }
        SoyNode.BlockNode nodeAsBlock = (SoyNode.BlockNode)node;
        boolean hasConsecRawTextNodes = false;
        for (int i = 0; i <= nodeAsBlock.numChildren() - 2; ++i) {
            if (!(nodeAsBlock.getChild(i) instanceof RawTextNode) || !(nodeAsBlock.getChild(i + 1) instanceof RawTextNode)) continue;
            hasConsecRawTextNodes = true;
            break;
        }
        if (!hasConsecRawTextNodes) {
            return;
        }
        ArrayList copyOfOrigChildren = Lists.newArrayList(nodeAsBlock.getChildren());
        nodeAsBlock.clearChildren();
        ArrayList consecutiveRawTextNodes = Lists.newArrayList();
        for (SoyNode.StandaloneNode origChild : copyOfOrigChildren) {
            if (origChild instanceof RawTextNode) {
                consecutiveRawTextNodes.add((RawTextNode)origChild);
                continue;
            }
            this.addConsecutiveRawTextNodesAsOneNodeHelper(nodeAsBlock, consecutiveRawTextNodes);
            consecutiveRawTextNodes.clear();
            nodeAsBlock.addChild(origChild);
        }
        this.addConsecutiveRawTextNodesAsOneNodeHelper(nodeAsBlock, consecutiveRawTextNodes);
        consecutiveRawTextNodes.clear();
    }

    private void addConsecutiveRawTextNodesAsOneNodeHelper(SoyNode.BlockNode parent, List<RawTextNode> consecutiveRawTextNodes) {
        if (consecutiveRawTextNodes.size() == 0) {
            return;
        }
        if (consecutiveRawTextNodes.size() == 1) {
            parent.addChild((Node)consecutiveRawTextNodes.get(0));
        } else {
            StringBuilder rawText = new StringBuilder();
            for (RawTextNode rtn : consecutiveRawTextNodes) {
                rawText.append(rtn.getRawText());
            }
            parent.addChild(new RawTextNode(this.nodeIdGen.genId(), rawText.toString()));
        }
    }
}

