/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package com.google.template.soy.msgs.internal;

import com.google.common.collect.Lists;
import com.google.template.soy.base.internal.IdGenerator;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.msgs.internal.MsgUtils;
import com.google.template.soy.msgs.restricted.SoyMsg;
import com.google.template.soy.msgs.restricted.SoyMsgPart;
import com.google.template.soy.msgs.restricted.SoyMsgPlaceholderPart;
import com.google.template.soy.msgs.restricted.SoyMsgRawTextPart;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.MsgFallbackGroupNode;
import com.google.template.soy.soytree.MsgHtmlTagNode;
import com.google.template.soy.soytree.MsgNode;
import com.google.template.soy.soytree.MsgPlaceholderNode;
import com.google.template.soy.soytree.MsgPluralNode;
import com.google.template.soy.soytree.MsgSelectNode;
import com.google.template.soy.soytree.RawTextNode;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.soytree.SoyNode;
import java.util.List;
import javax.annotation.Nullable;

public class InsertMsgsVisitor
extends AbstractSoyNodeVisitor<Void> {
    private final SoyMsgBundle msgBundle;
    private final boolean dontErrorOnPlrselMsgs;
    private IdGenerator nodeIdGen;
    private List<SoyNode.StandaloneNode> currReplacementNodes;

    public InsertMsgsVisitor(@Nullable SoyMsgBundle msgBundle, boolean dontErrorOnPlrselMsgs) {
        this.msgBundle = msgBundle;
        this.dontErrorOnPlrselMsgs = dontErrorOnPlrselMsgs;
    }

    @Override
    public Void exec(SoyNode node) {
        this.nodeIdGen = node.getNearestAncestor(SoyFileSetNode.class).getNodeIdGenerator();
        super.exec(node);
        return null;
    }

    @Override
    protected void visitMsgFallbackGroupNode(MsgFallbackGroupNode node) {
        for (MsgNode msg : node.getChildren()) {
            if (msg.numChildren() != 1 || !(msg.getChild(0) instanceof MsgSelectNode) && !(msg.getChild(0) instanceof MsgPluralNode)) continue;
            if (this.dontErrorOnPlrselMsgs) {
                return;
            }
            throw new EncounteredPlrselMsgException(msg);
        }
        this.currReplacementNodes = null;
        if (this.msgBundle != null) {
            for (MsgNode msg : node.getChildren()) {
                SoyMsg translation = this.msgBundle.getMsg(MsgUtils.computeMsgIdForDualFormat(msg));
                if (translation == null) continue;
                this.buildReplacementNodesFromTranslation(msg, translation);
                break;
            }
        }
        if (this.currReplacementNodes == null) {
            this.buildReplacementNodesFromSource((MsgNode)node.getChild(0));
        }
        SoyNode.BlockNode parent = node.getParent();
        int indexInParent = parent.getChildIndex(node);
        parent.removeChild(indexInParent);
        parent.addChildren(indexInParent, this.currReplacementNodes);
        this.currReplacementNodes = null;
    }

    private void buildReplacementNodesFromTranslation(MsgNode msg, SoyMsg translation) {
        this.currReplacementNodes = Lists.newArrayList();
        for (SoyMsgPart msgPart : translation.getParts()) {
            if (msgPart instanceof SoyMsgRawTextPart) {
                String rawText = ((SoyMsgRawTextPart)msgPart).getRawText();
                this.currReplacementNodes.add(new RawTextNode(this.nodeIdGen.genId(), rawText));
                continue;
            }
            if (msgPart instanceof SoyMsgPlaceholderPart) {
                String placeholderName = ((SoyMsgPlaceholderPart)msgPart).getPlaceholderName();
                MsgPlaceholderNode placeholderNode = msg.getRepPlaceholderNode(placeholderName);
                for (SoyNode.StandaloneNode contentNode : placeholderNode.getChildren()) {
                    if (contentNode instanceof MsgHtmlTagNode) {
                        this.visit(contentNode);
                        continue;
                    }
                    this.currReplacementNodes.add(contentNode);
                }
                continue;
            }
            throw new AssertionError();
        }
    }

    private void buildReplacementNodesFromSource(MsgNode msg) {
        this.currReplacementNodes = Lists.newArrayList();
        for (SoyNode.StandaloneNode child : msg.getChildren()) {
            if (child instanceof RawTextNode) {
                this.currReplacementNodes.add(child);
                continue;
            }
            if (child instanceof MsgPlaceholderNode) {
                for (SoyNode.StandaloneNode contentNode : ((MsgPlaceholderNode)child).getChildren()) {
                    if (contentNode instanceof MsgHtmlTagNode) {
                        this.visit(contentNode);
                        continue;
                    }
                    this.currReplacementNodes.add(contentNode);
                }
                continue;
            }
            throw new AssertionError();
        }
    }

    @Override
    protected void visitMsgHtmlTagNode(MsgHtmlTagNode node) {
        for (SoyNode.StandaloneNode child : node.getChildren()) {
            this.currReplacementNodes.add(child);
        }
    }

    @Override
    protected void visitSoyNode(SoyNode node) {
        if (node instanceof SoyNode.ParentSoyNode) {
            this.visitChildrenAllowingConcurrentModification((SoyNode.ParentSoyNode)node);
        }
    }

    public static class EncounteredPlrselMsgException
    extends RuntimeException {
        public final MsgNode msgNode;

        public EncounteredPlrselMsgException(MsgNode msgNode) {
            this.msgNode = msgNode;
        }
    }
}

