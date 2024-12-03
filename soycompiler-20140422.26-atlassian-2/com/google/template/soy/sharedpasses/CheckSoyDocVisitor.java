/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 */
package com.google.template.soy.sharedpasses;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.basetree.SyntaxVersion;
import com.google.template.soy.exprtree.AbstractExprNodeVisitor;
import com.google.template.soy.exprtree.ExprNode;
import com.google.template.soy.exprtree.VarRefNode;
import com.google.template.soy.sharedpasses.FindIndirectParamsVisitor;
import com.google.template.soy.sharedpasses.ReportSyntaxVersionErrorsVisitor;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.CallNode;
import com.google.template.soy.soytree.ExprUnion;
import com.google.template.soy.soytree.SoyFileNode;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.SoySyntaxExceptionUtils;
import com.google.template.soy.soytree.TemplateDelegateNode;
import com.google.template.soy.soytree.TemplateNode;
import com.google.template.soy.soytree.TemplateRegistry;
import com.google.template.soy.soytree.defn.TemplateParam;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CheckSoyDocVisitor
extends AbstractSoyNodeVisitor<Void> {
    private final SyntaxVersion declaredSyntaxVersion;
    private TemplateRegistry templateRegistry;
    private GetDataKeysInExprVisitor getDataKeysInExprVisitor;

    public CheckSoyDocVisitor(SyntaxVersion declaredSyntaxVersion) {
        this.declaredSyntaxVersion = declaredSyntaxVersion;
    }

    @Override
    protected void visitSoyFileSetNode(SoyFileSetNode node) {
        this.templateRegistry = new TemplateRegistry(node);
        for (SoyFileNode soyFile : node.getChildren()) {
            boolean doCheckSoyDocInFile;
            if (this.declaredSyntaxVersion.num >= SyntaxVersion.V2_0.num) {
                doCheckSoyDocInFile = true;
            } else {
                try {
                    new ReportSyntaxVersionErrorsVisitor(SyntaxVersion.V2_0, true).exec(soyFile);
                    doCheckSoyDocInFile = true;
                }
                catch (SoySyntaxException sse) {
                    doCheckSoyDocInFile = false;
                }
            }
            if (!doCheckSoyDocInFile) continue;
            this.visit(soyFile);
        }
    }

    @Override
    protected void visitTemplateNode(TemplateNode node) {
        HashSet dataKeys = Sets.newHashSet();
        this.getDataKeysInExprVisitor = new GetDataKeysInExprVisitor(dataKeys);
        this.visitChildren(node);
        FindIndirectParamsVisitor.IndirectParamsInfo ipi = new FindIndirectParamsVisitor(this.templateRegistry).exec(node);
        ArrayList unusedParams = Lists.newArrayList();
        for (TemplateParam param : node.getParams()) {
            if (dataKeys.contains(param.name())) {
                dataKeys.remove(param.name());
                continue;
            }
            if (ipi.paramKeyToCalleesMultimap.containsKey((Object)param.name()) || ipi.mayHaveIndirectParamsInExternalCalls || ipi.mayHaveIndirectParamsInExternalDelCalls) continue;
            unusedParams.add(param.name());
        }
        ArrayList undeclaredDataKeys = Lists.newArrayList();
        if (dataKeys.size() > 0) {
            undeclaredDataKeys.addAll(dataKeys);
            Collections.sort(undeclaredDataKeys);
        }
        if (undeclaredDataKeys.size() > 0) {
            throw SoySyntaxExceptionUtils.createWithNode("Found references to data keys that are not declared in SoyDoc: " + undeclaredDataKeys, node);
        }
        if (unusedParams.size() > 0 && !(node instanceof TemplateDelegateNode)) {
            throw SoySyntaxExceptionUtils.createWithNode("Found params declared in SoyDoc but not used in template: " + unusedParams, node);
        }
    }

    @Override
    protected void visitCallNode(CallNode node) {
        if (!node.isPassingAllData()) {
            this.visitExprHolderHelper(node);
        }
        this.visitChildren(node);
    }

    @Override
    protected void visitSoyNode(SoyNode node) {
        if (node instanceof SoyNode.ExprHolderNode) {
            this.visitExprHolderHelper((SoyNode.ExprHolderNode)node);
        }
        if (node instanceof SoyNode.ParentSoyNode) {
            this.visitChildren((SoyNode.ParentSoyNode)node);
        }
    }

    private void visitExprHolderHelper(SoyNode.ExprHolderNode exprHolder) {
        for (ExprUnion exprUnion : exprHolder.getAllExprUnions()) {
            this.getDataKeysInExprVisitor.exec(exprUnion.getExpr());
        }
    }

    private static class GetDataKeysInExprVisitor
    extends AbstractExprNodeVisitor<Void> {
        private final Set<String> dataKeys;

        public GetDataKeysInExprVisitor(Set<String> dataKeys) {
            this.dataKeys = dataKeys;
        }

        @Override
        protected void visitVarRefNode(VarRefNode node) {
            if (node.isPossibleParam().booleanValue()) {
                this.dataKeys.add(node.getName());
            }
        }

        @Override
        protected void visitExprNode(ExprNode node) {
            if (node instanceof ExprNode.ParentExprNode) {
                this.visitChildren((ExprNode.ParentExprNode)node);
            }
        }
    }
}

