/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Sets
 */
package com.google.template.soy.parsepasses.contextautoesc;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.parsepasses.contextautoesc.EscapingMode;
import com.google.template.soy.parsepasses.contextautoesc.Inferences;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.CallBasicNode;
import com.google.template.soy.soytree.CallDelegateNode;
import com.google.template.soy.soytree.CallNode;
import com.google.template.soy.soytree.PrintDirectiveNode;
import com.google.template.soy.soytree.PrintNode;
import com.google.template.soy.soytree.RawTextNode;
import com.google.template.soy.soytree.SoyFileNode;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.TemplateNode;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class Rewriter {
    private final Inferences inferences;
    private final Set<String> visitedTemplateNames = Sets.newHashSet();
    private final Map<String, SanitizedContent.ContentKind> sanitizedContentOperators;

    public Rewriter(Inferences inferences, Map<String, SanitizedContent.ContentKind> sanitizedContentOperators) {
        this.inferences = inferences;
        this.sanitizedContentOperators = sanitizedContentOperators;
    }

    public List<TemplateNode> rewrite(SoyFileSetNode files) {
        RewriterVisitor mutator = new RewriterVisitor();
        for (SoyFileNode file : files.getChildren()) {
            mutator.exec(file);
        }
        ImmutableList.Builder extraTemplates = ImmutableList.builder();
        for (TemplateNode template : this.inferences.getAllTemplates()) {
            String name = template.getTemplateName();
            if (this.visitedTemplateNames.contains(name)) continue;
            extraTemplates.add((Object)template);
            mutator.exec(template);
        }
        return extraTemplates.build();
    }

    private static void replaceChild(SoyNode.StandaloneNode oldChild, SoyNode.StandaloneNode newChild) {
        oldChild.getParent().replaceChild(oldChild, newChild);
    }

    private static <T extends SoyNode> void moveChildrenTo(SoyNode.ParentSoyNode<T> oldParent, SoyNode.ParentSoyNode<T> newParent) {
        ImmutableList children = ImmutableList.copyOf(oldParent.getChildren());
        oldParent.clearChildren();
        newParent.addChildren((List<T>)children);
    }

    final class RewriterVisitor
    extends AbstractSoyNodeVisitor<Void> {
        RewriterVisitor() {
        }

        @Override
        protected void visitTemplateNode(TemplateNode templateNode) {
            Preconditions.checkState((!Rewriter.this.visitedTemplateNames.contains(templateNode.getTemplateName()) ? 1 : 0) != 0);
            Rewriter.this.visitedTemplateNames.add(templateNode.getTemplateName());
            this.visitChildrenAllowingConcurrentModification(templateNode);
        }

        @Override
        protected void visitPrintNode(PrintNode printNode) {
            int id = printNode.getId();
            ImmutableList<EscapingMode> escapingModes = Rewriter.this.inferences.getEscapingModesForId(id);
            for (EscapingMode escapingMode : escapingModes) {
                int newPrintDirectiveIndex;
                PrintDirectiveNode newPrintDirective = new PrintDirectiveNode(Rewriter.this.inferences.getIdGenerator().genId(), escapingMode.directiveName, "");
                newPrintDirective.setSourceLocation(printNode.getSourceLocation());
                for (newPrintDirectiveIndex = printNode.numChildren(); newPrintDirectiveIndex > 0; --newPrintDirectiveIndex) {
                    String printDirectiveName = ((PrintDirectiveNode)printNode.getChild(newPrintDirectiveIndex - 1)).getName();
                    SanitizedContent.ContentKind contentKind = (SanitizedContent.ContentKind)((Object)Rewriter.this.sanitizedContentOperators.get(printDirectiveName));
                    if (contentKind == null || contentKind != escapingMode.contentKind) break;
                }
                printNode.addChild(newPrintDirectiveIndex, newPrintDirective);
            }
        }

        @Override
        protected void visitRawTextNode(RawTextNode rawTextNode) {
        }

        @Override
        protected void visitCallNode(CallNode callNode) {
            String derivedCalleeName = Rewriter.this.inferences.getDerivedCalleeNameForCallId(callNode.getId());
            if (derivedCalleeName != null) {
                CallNode newCallNode;
                if (callNode instanceof CallBasicNode) {
                    newCallNode = new CallBasicNode(callNode.getId(), derivedCalleeName, derivedCalleeName, false, false, callNode.isPassingData(), callNode.isPassingAllData(), callNode.getDataExpr(), callNode.getUserSuppliedPhName(), callNode.getSyntaxVersionBound(), callNode.getEscapingDirectiveNames());
                } else {
                    CallDelegateNode callNodeCast = (CallDelegateNode)callNode;
                    newCallNode = new CallDelegateNode(callNode.getId(), derivedCalleeName, callNodeCast.getDelCalleeVariantExpr(), false, callNodeCast.allowsEmptyDefault(), callNode.isPassingData(), callNode.isPassingAllData(), callNode.getDataExpr(), callNode.getUserSuppliedPhName(), callNode.getEscapingDirectiveNames());
                }
                if (!callNode.getCommandText().equals(newCallNode.getCommandText())) {
                    newCallNode.setSourceLocation(callNode.getSourceLocation());
                    Rewriter.moveChildrenTo(callNode, newCallNode);
                    Rewriter.replaceChild(callNode, newCallNode);
                }
                callNode = newCallNode;
            }
            ImmutableList.Builder escapingDirectiveNames = new ImmutableList.Builder();
            for (EscapingMode escapingMode : Rewriter.this.inferences.getEscapingModesForId(callNode.getId())) {
                escapingDirectiveNames.add((Object)escapingMode.directiveName);
            }
            callNode.setEscapingDirectiveNames((ImmutableList<String>)escapingDirectiveNames.build());
            this.visitChildrenAllowingConcurrentModification(callNode);
        }

        @Override
        protected void visitSoyNode(SoyNode node) {
            if (node instanceof SoyNode.ParentSoyNode) {
                this.visitChildrenAllowingConcurrentModification((SoyNode.ParentSoyNode)node);
            }
        }
    }
}

