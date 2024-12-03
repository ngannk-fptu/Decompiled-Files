/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.inject.Inject
 */
package com.google.template.soy.parsepasses;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.template.soy.base.internal.IdGenerator;
import com.google.template.soy.coredirectives.NoAutoescapeDirective;
import com.google.template.soy.shared.restricted.SoyPrintDirective;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.AutoescapeMode;
import com.google.template.soy.soytree.PrintDirectiveNode;
import com.google.template.soy.soytree.PrintNode;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.SoySyntaxExceptionUtils;
import com.google.template.soy.soytree.TemplateNode;
import java.util.Map;

public class PerformAutoescapeVisitor
extends AbstractSoyNodeVisitor<Void> {
    private final Map<String, SoyPrintDirective> soyDirectivesMap;
    private IdGenerator nodeIdGen;
    private boolean currTemplateShouldAutoescape;

    @Inject
    public PerformAutoescapeVisitor(Map<String, SoyPrintDirective> soyDirectivesMap) {
        this.soyDirectivesMap = soyDirectivesMap;
    }

    @Override
    protected void visitSoyFileSetNode(SoyFileSetNode node) {
        this.nodeIdGen = node.getNodeIdGenerator();
        this.visitChildren(node);
    }

    @Override
    protected void visitTemplateNode(TemplateNode node) {
        this.currTemplateShouldAutoescape = node.getAutoescapeMode() != AutoescapeMode.FALSE;
        this.visitChildren(node);
    }

    @Override
    protected void visitPrintNode(PrintNode node) {
        boolean shouldCancelAutoescape = false;
        for (PrintDirectiveNode directiveNode : Lists.newArrayList(node.getChildren())) {
            SoyPrintDirective directive = this.soyDirectivesMap.get(directiveNode.getName());
            if (directive == null) {
                throw SoySyntaxExceptionUtils.createWithNode("Failed to find SoyPrintDirective with name '" + directiveNode.getName() + "' (tag " + node.toSourceString() + ")", directiveNode);
            }
            if (!directive.shouldCancelAutoescape()) continue;
            shouldCancelAutoescape = true;
            if (this.currTemplateShouldAutoescape || !(directive instanceof NoAutoescapeDirective)) continue;
            node.removeChild(directiveNode);
        }
        if (this.currTemplateShouldAutoescape && !shouldCancelAutoescape) {
            PrintDirectiveNode newEscapeHtmlDirectiveNode = new PrintDirectiveNode(this.nodeIdGen.genId(), "|escapeHtml", "");
            node.addChild(0, newEscapeHtmlDirectiveNode);
        }
    }

    @Override
    protected void visitSoyNode(SoyNode node) {
        if (node instanceof SoyNode.ParentSoyNode) {
            this.visitChildren((SoyNode.ParentSoyNode)node);
        }
    }
}

