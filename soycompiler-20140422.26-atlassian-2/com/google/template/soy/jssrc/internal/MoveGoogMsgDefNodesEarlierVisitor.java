/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package com.google.template.soy.jssrc.internal;

import com.google.common.collect.Lists;
import com.google.template.soy.sharedpasses.BuildAllDependeesMapVisitor;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.jssrc.GoogMsgDefNode;
import java.util.List;
import java.util.Map;

class MoveGoogMsgDefNodesEarlierVisitor
extends AbstractSoyNodeVisitor<Void> {
    private List<GoogMsgDefNode> googMsgDefNodes;

    MoveGoogMsgDefNodesEarlierVisitor() {
    }

    @Override
    public Void exec(SoyNode node) {
        this.googMsgDefNodes = Lists.newArrayList();
        this.visit(node);
        return null;
    }

    @Override
    protected void visitSoyFileSetNode(SoyFileSetNode node) {
        this.visitChildren(node);
        Map<SoyNode, List<SoyNode>> allDependeesMap = new BuildAllDependeesMapVisitor().exec(node);
        for (GoogMsgDefNode googMsgDefNode : this.googMsgDefNodes) {
            this.moveGoogMsgDefNodeEarlierHelper(googMsgDefNode, allDependeesMap.get(googMsgDefNode));
        }
    }

    @Override
    protected void visitGoogMsgDefNode(GoogMsgDefNode node) {
        this.googMsgDefNodes.add(node);
    }

    @Override
    protected void visitSoyNode(SoyNode node) {
        if (node instanceof SoyNode.ParentSoyNode) {
            this.visitChildren((SoyNode.ParentSoyNode)node);
        }
    }

    private void moveGoogMsgDefNodeEarlierHelper(GoogMsgDefNode googMsgDefNode, List<SoyNode> allDependees) {
        int indexUnderNewParent;
        SoyNode.BlockNode newParent;
        SoyNode nearestDependee = allDependees.get(0);
        if (nearestDependee instanceof SoyNode.LocalVarInlineNode) {
            newParent = (SoyNode.BlockNode)nearestDependee.getParent();
            indexUnderNewParent = newParent.getChildIndex((SoyNode.LocalVarInlineNode)nearestDependee) + 1;
        } else if (nearestDependee instanceof SoyNode.BlockNode) {
            newParent = (SoyNode.BlockNode)nearestDependee;
            indexUnderNewParent = 0;
        } else {
            throw new AssertionError();
        }
        List siblings = newParent.getChildren();
        while (indexUnderNewParent < siblings.size() && siblings.get(indexUnderNewParent) instanceof GoogMsgDefNode) {
            if (googMsgDefNode == siblings.get(indexUnderNewParent)) {
                return;
            }
            ++indexUnderNewParent;
        }
        googMsgDefNode.getParent().removeChild(googMsgDefNode);
        newParent.addChild(indexUnderNewParent, googMsgDefNode);
    }
}

