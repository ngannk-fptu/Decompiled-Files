/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package com.google.template.soy.jssrc.internal;

import com.google.common.collect.Lists;
import com.google.template.soy.base.internal.IdGenerator;
import com.google.template.soy.msgs.internal.MsgUtils;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.MsgFallbackGroupNode;
import com.google.template.soy.soytree.MsgNode;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.jssrc.GoogMsgDefNode;
import com.google.template.soy.soytree.jssrc.GoogMsgRefNode;
import java.util.ArrayList;
import java.util.List;

class ReplaceMsgsWithGoogMsgsVisitor
extends AbstractSoyNodeVisitor<Void> {
    private List<MsgFallbackGroupNode> msgFbGrpNodes;

    ReplaceMsgsWithGoogMsgsVisitor() {
    }

    @Override
    public Void exec(SoyNode node) {
        this.msgFbGrpNodes = Lists.newArrayList();
        this.visit(node);
        return null;
    }

    @Override
    protected void visitSoyFileSetNode(SoyFileSetNode node) {
        this.visitChildren(node);
        IdGenerator nodeIdGen = node.getNearestAncestor(SoyFileSetNode.class).getNodeIdGenerator();
        for (MsgFallbackGroupNode msgFbGrpNode : this.msgFbGrpNodes) {
            this.replaceMsgFallbackGroupNodeHelper(msgFbGrpNode, nodeIdGen);
        }
    }

    @Override
    protected void visitMsgFallbackGroupNode(MsgFallbackGroupNode node) {
        this.msgFbGrpNodes.add(node);
        this.visitChildren(node);
    }

    @Override
    protected void visitSoyNode(SoyNode node) {
        if (node instanceof SoyNode.ParentSoyNode) {
            this.visitChildren((SoyNode.ParentSoyNode)node);
        }
    }

    private void replaceMsgFallbackGroupNodeHelper(MsgFallbackGroupNode msgFbGrpNode, IdGenerator nodeIdGen) {
        ArrayList childMsgIds = Lists.newArrayListWithCapacity((int)msgFbGrpNode.numChildren());
        for (MsgNode msgNode : msgFbGrpNode.getChildren()) {
            childMsgIds.add(MsgUtils.computeMsgIdForDualFormat(msgNode));
        }
        GoogMsgDefNode googMsgDefNode = new GoogMsgDefNode(nodeIdGen.genId(), msgFbGrpNode, childMsgIds);
        GoogMsgRefNode googMsgRefNode = new GoogMsgRefNode(nodeIdGen.genId(), googMsgDefNode.getRenderedGoogMsgVarName());
        SoyNode.BlockNode parent = msgFbGrpNode.getParent();
        int index = parent.getChildIndex(msgFbGrpNode);
        parent.replaceChild(index, googMsgDefNode);
        parent.addChild(index + 1, googMsgRefNode);
    }
}

