/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.sharedpasses;

import com.google.template.soy.exprtree.AbstractExprNodeVisitor;
import com.google.template.soy.exprtree.ExprNode;
import com.google.template.soy.exprtree.VarRefNode;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.SoytreeUtils;

public class UnmarkLocalVarDataRefsVisitor
extends AbstractSoyNodeVisitor<Void> {
    @Override
    public Void exec(SoyNode node) {
        SoytreeUtils.execOnAllV2Exprs(node, new UnmarkLocalVarDataRefsInExprVisitor());
        return null;
    }

    private static class UnmarkLocalVarDataRefsInExprVisitor
    extends AbstractExprNodeVisitor<Void> {
        private UnmarkLocalVarDataRefsInExprVisitor() {
        }

        @Override
        protected void visitVarRefNode(VarRefNode node) {
            node.setIsLocalVar(null);
        }

        @Override
        protected void visitExprNode(ExprNode node) {
            if (node instanceof ExprNode.ParentExprNode) {
                this.visitChildren((ExprNode.ParentExprNode)node);
            }
        }
    }
}

