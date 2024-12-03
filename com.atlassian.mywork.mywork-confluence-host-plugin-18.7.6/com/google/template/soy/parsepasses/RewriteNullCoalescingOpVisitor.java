/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 */
package com.google.template.soy.parsepasses;

import com.google.common.annotations.VisibleForTesting;
import com.google.template.soy.exprtree.AbstractExprNodeVisitor;
import com.google.template.soy.exprtree.ExprNode;
import com.google.template.soy.exprtree.FunctionNode;
import com.google.template.soy.exprtree.OperatorNodes;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.SoytreeUtils;

public class RewriteNullCoalescingOpVisitor {
    public void exec(SoyNode node) {
        SoytreeUtils.execOnAllV2Exprs(node, new RewriteNullCoalescingOpInExprVisitor());
    }

    @VisibleForTesting
    static class RewriteNullCoalescingOpInExprVisitor
    extends AbstractExprNodeVisitor<Void> {
        RewriteNullCoalescingOpInExprVisitor() {
        }

        @Override
        protected void visitNullCoalescingOpNode(OperatorNodes.NullCoalescingOpNode node) {
            this.visitChildrenAllowingConcurrentModification(node);
            ExprNode operand0a = node.getChild(0);
            ExprNode operand0b = operand0a.clone();
            ExprNode operand1 = node.getChild(1);
            FunctionNode isNonnullFnNode = new FunctionNode("isNonnull");
            isNonnullFnNode.addChild(operand0a);
            OperatorNodes.ConditionalOpNode condOpNode = new OperatorNodes.ConditionalOpNode();
            condOpNode.addChild(isNonnullFnNode);
            condOpNode.addChild(operand0b);
            condOpNode.addChild(operand1);
            node.getParent().replaceChild(node, condOpNode);
        }

        @Override
        protected void visitExprNode(ExprNode node) {
            if (node instanceof ExprNode.ParentExprNode) {
                this.visitChildrenAllowingConcurrentModification((ExprNode.ParentExprNode)node);
            }
        }
    }
}

