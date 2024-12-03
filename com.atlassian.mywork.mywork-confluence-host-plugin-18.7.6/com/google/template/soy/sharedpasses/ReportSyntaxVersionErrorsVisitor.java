/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Lists
 */
package com.google.template.soy.sharedpasses;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.basetree.SyntaxVersion;
import com.google.template.soy.basetree.SyntaxVersionBound;
import com.google.template.soy.exprtree.AbstractExprNodeVisitor;
import com.google.template.soy.exprtree.ExprNode;
import com.google.template.soy.exprtree.ExprRootNode;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.ExprUnion;
import com.google.template.soy.soytree.PrintDirectiveNode;
import com.google.template.soy.soytree.SoyFileNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.SoySyntaxExceptionUtils;
import java.util.List;

public class ReportSyntaxVersionErrorsVisitor
extends AbstractSoyNodeVisitor<Void> {
    private final SyntaxVersion requiredSyntaxVersion;
    private final boolean isDeclared;
    private List<SoySyntaxException> syntaxExceptions;

    public ReportSyntaxVersionErrorsVisitor(SyntaxVersion requiredSyntaxVersion, boolean isDeclared) {
        this.requiredSyntaxVersion = requiredSyntaxVersion;
        this.isDeclared = isDeclared;
        this.syntaxExceptions = null;
    }

    @Override
    public Void exec(SoyNode node) {
        this.syntaxExceptions = Lists.newArrayList();
        this.visitSoyNode(node);
        int numErrors = this.syntaxExceptions.size();
        if (numErrors != 0) {
            StringBuilder errorMsgBuilder = new StringBuilder();
            Object[] objectArray = new Object[2];
            Object object = objectArray[0] = numErrors == 1 ? "error" : numErrors + " errors";
            objectArray[1] = this.requiredSyntaxVersion == SyntaxVersion.V1_0 ? "syntax is incorrect" : (this.isDeclared ? "declared" : "inferred") + " syntax version " + (Object)((Object)this.requiredSyntaxVersion) + " is not satisfied";
            errorMsgBuilder.append(String.format("Found %s where %s:", objectArray));
            if (numErrors == 1) {
                errorMsgBuilder.append(' ').append(this.syntaxExceptions.get(0).getMessage());
            } else {
                for (int i = 0; i < numErrors; ++i) {
                    errorMsgBuilder.append(String.format("\n%s. %s", i + 1, this.syntaxExceptions.get(i).getMessage()));
                }
            }
            throw SoySyntaxException.createWithoutMetaInfo(errorMsgBuilder.toString());
        }
        this.syntaxExceptions = null;
        return null;
    }

    @Override
    protected void visitSoyNode(SoyNode node) {
        if (!node.couldHaveSyntaxVersionAtLeast(this.requiredSyntaxVersion)) {
            String nodeStringForErrorMsg = node instanceof SoyNode.CommandNode ? "Tag " + ((SoyNode.CommandNode)node).getTagString() : (node instanceof SoyFileNode ? "File " + ((SoyFileNode)node).getFileName() : (node instanceof PrintDirectiveNode ? "Print directive \"" + node.toSourceString() + "\"" : "Node " + node.toSourceString()));
            SyntaxVersionBound syntaxVersionBound = node.getSyntaxVersionBound();
            assert (syntaxVersionBound != null);
            this.addError(nodeStringForErrorMsg + ": " + syntaxVersionBound.reasonStr, node);
        }
        if (node instanceof SoyNode.ExprHolderNode) {
            ReportSyntaxVersionErrorsExprVisitor exprVisitor = new ReportSyntaxVersionErrorsExprVisitor((SoyNode.ExprHolderNode)node);
            for (ExprUnion exprUnion : ((SoyNode.ExprHolderNode)node).getAllExprUnions()) {
                if (exprUnion.getExpr() != null) {
                    exprVisitor.exec(exprUnion.getExpr());
                    continue;
                }
                if (this.requiredSyntaxVersion.num < SyntaxVersion.V2_0.num) continue;
                String exprText = exprUnion.getExprText();
                String errorMsgPrefix = "Invalid expression \"" + exprText + "\"";
                int numSingleQuotes = 0;
                int numDoubleQuotes = 0;
                block5: for (int i = 0; i < exprText.length(); ++i) {
                    switch (exprText.charAt(i)) {
                        case '\'': {
                            ++numSingleQuotes;
                            continue block5;
                        }
                        case '\"': {
                            ++numDoubleQuotes;
                        }
                    }
                }
                if (numDoubleQuotes >= 2 && numSingleQuotes <= 1) {
                    this.addError(errorMsgPrefix + ", possibly due to using double quotes instead of single quotes for string literal.", node);
                    continue;
                }
                if (exprText.contains("&&") || exprText.contains("||") || exprText.contains("!")) {
                    this.addError(errorMsgPrefix + ", possibly due to using &&/||/! instead of and/or/not operators.", node);
                    continue;
                }
                this.addError(errorMsgPrefix + ".", node);
            }
        }
        if (node instanceof SoyNode.ParentSoyNode) {
            this.visitChildren((SoyNode.ParentSoyNode)node);
        }
    }

    private void addError(String errorMsg, SoyNode node) {
        this.syntaxExceptions.add(SoySyntaxExceptionUtils.createWithNode(errorMsg, node));
    }

    private class ReportSyntaxVersionErrorsExprVisitor
    extends AbstractExprNodeVisitor<Void> {
        private final SoyNode.ExprHolderNode exprHolder;
        private ExprRootNode<?> exprRoot;

        public ReportSyntaxVersionErrorsExprVisitor(SoyNode.ExprHolderNode exprHolder) {
            this.exprHolder = exprHolder;
        }

        @Override
        public Void exec(ExprNode node) {
            Preconditions.checkArgument((boolean)(node instanceof ExprRootNode));
            this.exprRoot = (ExprRootNode)node;
            this.visit(node);
            this.exprRoot = null;
            return null;
        }

        @Override
        public void visitExprNode(ExprNode node) {
            if (!node.couldHaveSyntaxVersionAtLeast(ReportSyntaxVersionErrorsVisitor.this.requiredSyntaxVersion)) {
                SyntaxVersionBound syntaxVersionBound = node.getSyntaxVersionBound();
                assert (syntaxVersionBound != null);
                ReportSyntaxVersionErrorsVisitor.this.addError(String.format("Invalid expression \"%s\": %s", this.exprRoot.toSourceString(), syntaxVersionBound.reasonStr), this.exprHolder);
            }
            if (node instanceof ExprNode.ParentExprNode) {
                this.visitChildren((ExprNode.ParentExprNode)node);
            }
        }
    }
}

