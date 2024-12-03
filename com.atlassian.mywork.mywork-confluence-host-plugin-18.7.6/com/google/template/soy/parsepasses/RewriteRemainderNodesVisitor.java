/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.parsepasses;

import com.google.template.soy.exprtree.ExprRootNode;
import com.google.template.soy.exprtree.FunctionNode;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.MsgPluralNode;
import com.google.template.soy.soytree.PrintNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.SoySyntaxExceptionUtils;

public class RewriteRemainderNodesVisitor
extends AbstractSoyNodeVisitor<Void> {
    private MsgPluralNode currPluralNode;

    @Override
    protected void visitPrintNode(PrintNode node) {
        FunctionNode functionNode;
        ExprRootNode<?> exprRootNode = node.getExprUnion().getExpr();
        if (exprRootNode == null) {
            return;
        }
        if (exprRootNode.getChild(0) instanceof FunctionNode && (functionNode = (FunctionNode)exprRootNode.getChild(0)).getFunctionName().equals("remainder")) {
            if (this.currPluralNode == null) {
                throw SoySyntaxExceptionUtils.createWithNode("The special function 'remainder' is for use in plural messages (tag " + node.toSourceString() + ").", node);
            }
            if (functionNode.numChildren() != 1) {
                throw SoySyntaxExceptionUtils.createWithNode("The function 'remainder' has to have exactly one argument (tag " + node.toSourceString() + ").", node);
            }
            if (!functionNode.getChild(0).toSourceString().equals(this.currPluralNode.getExpr().toSourceString())) {
                throw SoySyntaxExceptionUtils.createWithNode("The parameter to 'remainder' has to be the same as the 'plural' variable (tag " + node.toSourceString() + ").", node);
            }
            if (this.currPluralNode.getOffset() == 0) {
                throw SoySyntaxExceptionUtils.createWithNode("In 'plural' block, use of 'remainder' function is unnecessary since offset = 0 (tag " + node.toSourceString() + ").", node);
            }
            if (node.getUserSuppliedPhName() != null) {
                throw SoySyntaxExceptionUtils.createWithNode("Cannot use special function 'remainder' and attribute 'phname' together (tag " + node.toSourceString() + ").", node);
            }
            String newExprText = "(" + this.currPluralNode.getExpr().toSourceString() + ") - " + this.currPluralNode.getOffset();
            PrintNode newPrintNode = new PrintNode(node.getId(), node.isImplicit(), newExprText, null);
            newPrintNode.addChildren(node.getChildren());
            node.getParent().replaceChild(node, newPrintNode);
        }
    }

    @Override
    protected void visitMsgPluralNode(MsgPluralNode node) {
        this.currPluralNode = node;
        this.visitChildren(node);
        this.currPluralNode = null;
    }

    @Override
    protected void visitSoyNode(SoyNode node) {
        if (node instanceof SoyNode.ParentSoyNode) {
            this.visitChildrenAllowingConcurrentModification((SoyNode.ParentSoyNode)node);
        }
    }
}

