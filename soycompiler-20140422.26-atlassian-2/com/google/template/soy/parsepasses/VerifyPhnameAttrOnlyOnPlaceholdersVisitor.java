/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.parsepasses;

import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.CallNode;
import com.google.template.soy.soytree.MsgPlaceholderNode;
import com.google.template.soy.soytree.PrintNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.SoySyntaxExceptionUtils;

public class VerifyPhnameAttrOnlyOnPlaceholdersVisitor
extends AbstractSoyNodeVisitor<Void> {
    @Override
    protected void visitPrintNode(PrintNode node) {
        this.visitMsgPlaceholderInitialContentNodeHelper(node);
    }

    @Override
    protected void visitCallNode(CallNode node) {
        this.visitMsgPlaceholderInitialContentNodeHelper(node);
        this.visitChildren(node);
    }

    private void visitMsgPlaceholderInitialContentNodeHelper(SoyNode.MsgPlaceholderInitialNode node) {
        if (node.getUserSuppliedPhName() != null && !(node.getParent() instanceof MsgPlaceholderNode)) {
            throw SoySyntaxExceptionUtils.createWithNode("Found 'phname' attribute not on a msg placeholder (tag " + node.toSourceString() + ").", node);
        }
    }

    @Override
    protected void visitSoyNode(SoyNode node) {
        if (node instanceof SoyNode.ParentSoyNode) {
            this.visitChildren((SoyNode.ParentSoyNode)node);
        }
    }
}

