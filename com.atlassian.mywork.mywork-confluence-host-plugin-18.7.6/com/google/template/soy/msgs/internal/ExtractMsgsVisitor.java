/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Lists
 */
package com.google.template.soy.msgs.internal;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.msgs.internal.MsgUtils;
import com.google.template.soy.msgs.restricted.SoyMsg;
import com.google.template.soy.msgs.restricted.SoyMsgBundleImpl;
import com.google.template.soy.msgs.restricted.SoyMsgPart;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.MsgNode;
import com.google.template.soy.soytree.SoyFileNode;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.soytree.SoyNode;
import java.util.List;

public class ExtractMsgsVisitor
extends AbstractSoyNodeVisitor<SoyMsgBundle> {
    List<SoyMsg> msgs;
    String currentSource;

    @Override
    public SoyMsgBundle exec(SoyNode node) {
        Preconditions.checkArgument((node instanceof SoyFileSetNode || node instanceof SoyFileNode ? 1 : 0) != 0);
        this.msgs = Lists.newArrayList();
        this.currentSource = null;
        this.visit(node);
        this.currentSource = null;
        return new SoyMsgBundleImpl(null, this.msgs);
    }

    public SoyMsgBundle execOnMultipleNodes(Iterable<? extends SoyNode> nodes) {
        this.msgs = Lists.newArrayList();
        for (SoyNode soyNode : nodes) {
            this.currentSource = soyNode instanceof SoyFileSetNode || soyNode instanceof SoyFileNode ? null : soyNode.getNearestAncestor(SoyFileNode.class).getFilePath();
            this.visit(soyNode);
            this.currentSource = null;
        }
        return new SoyMsgBundleImpl(null, this.msgs);
    }

    @Override
    protected void visitSoyFileNode(SoyFileNode node) {
        this.currentSource = node.getFilePath();
        this.visitChildren(node);
        this.currentSource = null;
    }

    @Override
    protected void visitMsgNode(MsgNode node) {
        MsgUtils.MsgPartsAndIds msgPartsAndIds = MsgUtils.buildMsgPartsAndComputeMsgIdForDualFormat(node);
        this.msgs.add(new SoyMsg(msgPartsAndIds.id, -1L, null, node.getMeaning(), node.getDesc(), node.isHidden(), node.getContentType(), this.currentSource, node.isPlrselMsg(), (List<SoyMsgPart>)msgPartsAndIds.parts));
    }

    @Override
    protected void visitSoyNode(SoyNode node) {
        if (node instanceof SoyNode.ParentSoyNode) {
            this.visitChildren((SoyNode.ParentSoyNode)node);
        }
    }
}

