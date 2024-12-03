/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 */
package com.google.template.soy.sharedpasses;

import com.google.common.collect.Sets;
import com.google.template.soy.exprtree.AbstractExprNodeVisitor;
import com.google.template.soy.exprtree.ExprNode;
import com.google.template.soy.exprtree.VarRefNode;
import java.util.Set;

class FindIjParamsInExprHelperVisitor
extends AbstractExprNodeVisitor<Set<String>> {
    private final Set<String> usedIjParamsInExpr = Sets.newHashSet();

    @Override
    public Set<String> exec(ExprNode node) {
        this.visit(node);
        return this.getResult();
    }

    public Set<String> getResult() {
        return this.usedIjParamsInExpr;
    }

    @Override
    protected void visitVarRefNode(VarRefNode node) {
        if (node.isInjected()) {
            this.usedIjParamsInExpr.add(node.getName());
        }
    }

    @Override
    protected void visitExprNode(ExprNode node) {
        if (node instanceof ExprNode.ParentExprNode) {
            this.visitChildren((ExprNode.ParentExprNode)node);
        }
    }
}

