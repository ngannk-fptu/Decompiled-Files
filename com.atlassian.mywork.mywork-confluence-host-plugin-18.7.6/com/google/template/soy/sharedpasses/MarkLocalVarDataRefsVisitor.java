/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Sets
 */
package com.google.template.soy.sharedpasses;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.template.soy.exprtree.AbstractExprNodeVisitor;
import com.google.template.soy.exprtree.ExprNode;
import com.google.template.soy.exprtree.VarRefNode;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.ExprUnion;
import com.google.template.soy.soytree.SoyFileNode;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.TemplateNode;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public class MarkLocalVarDataRefsVisitor
extends AbstractSoyNodeVisitor<Void> {
    private Deque<Set<String>> localVarFrames;
    private MarkLocalVarDataRefsInExprVisitor markLocalVarDataRefsInExprVisitor;

    @Override
    public Void exec(SoyNode node) {
        Preconditions.checkArgument((node instanceof SoyFileSetNode || node instanceof SoyFileNode || node instanceof TemplateNode ? 1 : 0) != 0);
        return (Void)super.exec(node);
    }

    @Override
    protected void visitTemplateNode(TemplateNode node) {
        this.localVarFrames = new ArrayDeque<Set<String>>();
        this.markLocalVarDataRefsInExprVisitor = new MarkLocalVarDataRefsInExprVisitor(this.localVarFrames);
        this.visitSoyNode(node);
    }

    @Override
    protected void visitSoyNode(SoyNode node) {
        if (node instanceof SoyNode.ExprHolderNode) {
            this.visitExprHolderHelper((SoyNode.ExprHolderNode)node);
        }
        if (node instanceof SoyNode.LocalVarInlineNode) {
            this.localVarFrames.peek().add(((SoyNode.LocalVarInlineNode)node).getVarName());
        }
        if (node instanceof SoyNode.ParentSoyNode) {
            if (node instanceof SoyNode.LocalVarBlockNode) {
                SoyNode.LocalVarBlockNode nodeAsLocalVarBlock = (SoyNode.LocalVarBlockNode)node;
                HashSet newLocalVarFrame = Sets.newHashSet();
                newLocalVarFrame.add(nodeAsLocalVarBlock.getVarName());
                this.localVarFrames.push(newLocalVarFrame);
                this.visitChildren(nodeAsLocalVarBlock);
                this.localVarFrames.pop();
            } else if (node instanceof SoyNode.BlockNode) {
                this.localVarFrames.push(Sets.newHashSet());
                this.visitChildren((SoyNode.BlockNode)node);
                this.localVarFrames.pop();
            } else {
                this.visitChildren((SoyNode.ParentSoyNode)node);
            }
        }
    }

    private void visitExprHolderHelper(SoyNode.ExprHolderNode exprHolder) {
        for (ExprUnion exprUnion : exprHolder.getAllExprUnions()) {
            if (exprUnion.getExpr() == null) continue;
            this.markLocalVarDataRefsInExprVisitor.exec(exprUnion.getExpr());
        }
    }

    private static class MarkLocalVarDataRefsInExprVisitor
    extends AbstractExprNodeVisitor<Void> {
        private final Deque<Set<String>> localVarFrames;

        public MarkLocalVarDataRefsInExprVisitor(Deque<Set<String>> localVarFrames) {
            this.localVarFrames = localVarFrames;
        }

        @Override
        protected void visitVarRefNode(VarRefNode node) {
            boolean isLocalVar;
            if (node.isInjected()) {
                isLocalVar = false;
            } else {
                String name = node.getName();
                isLocalVar = false;
                for (Set<String> localVarFrame : this.localVarFrames) {
                    if (!localVarFrame.contains(name)) continue;
                    isLocalVar = true;
                    break;
                }
            }
            node.setIsLocalVar(isLocalVar);
        }

        @Override
        protected void visitExprNode(ExprNode node) {
            if (node instanceof ExprNode.ParentExprNode) {
                this.visitChildren((ExprNode.ParentExprNode)node);
            }
        }
    }
}

