/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.parsepasses;

import com.google.template.soy.basetree.SyntaxVersion;
import com.google.template.soy.basetree.SyntaxVersionBound;
import com.google.template.soy.exprtree.AbstractExprNodeVisitor;
import com.google.template.soy.exprtree.BooleanNode;
import com.google.template.soy.exprtree.ExprNode;
import com.google.template.soy.exprtree.FunctionNode;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.ExprUnion;
import com.google.template.soy.soytree.SoyNode;

public class ReplaceHasDataFunctionVisitor
extends AbstractSoyNodeVisitor<Void> {
    private final SyntaxVersion declaredSyntaxVersion;

    public ReplaceHasDataFunctionVisitor(SyntaxVersion declaredSyntaxVersion) {
        this.declaredSyntaxVersion = declaredSyntaxVersion;
    }

    @Override
    protected void visitSoyNode(SoyNode node) {
        if (node instanceof SoyNode.ExprHolderNode) {
            for (ExprUnion exprUnion : ((SoyNode.ExprHolderNode)node).getAllExprUnions()) {
                if (exprUnion.getExpr() == null) continue;
                new ReplaceHasDataFunctionInExprVisitor((SoyNode.ExprHolderNode)node).exec(exprUnion.getExpr());
            }
        }
        if (node instanceof SoyNode.ParentSoyNode) {
            this.visitChildren((SoyNode.ParentSoyNode)node);
        }
    }

    private class ReplaceHasDataFunctionInExprVisitor
    extends AbstractExprNodeVisitor<Void> {
        private final SoyNode.ExprHolderNode holder;

        public ReplaceHasDataFunctionInExprVisitor(SoyNode.ExprHolderNode holder) {
            this.holder = holder;
        }

        @Override
        protected void visitFunctionNode(FunctionNode node) {
            if (node.getFunctionName().equals("hasData")) {
                if (((ReplaceHasDataFunctionVisitor)ReplaceHasDataFunctionVisitor.this).declaredSyntaxVersion.num < SyntaxVersion.V2_2.num) {
                    node.getParent().replaceChild(node, new BooleanNode(true));
                }
                this.holder.maybeSetSyntaxVersionBound(new SyntaxVersionBound(SyntaxVersion.V2_2, "Function hasData() is unnecessary and no longer allowed."));
            }
        }

        @Override
        protected void visitExprNode(ExprNode node) {
            if (node instanceof ExprNode.ParentExprNode) {
                this.visitChildrenAllowingConcurrentModification((ExprNode.ParentExprNode)node);
            }
        }
    }
}

