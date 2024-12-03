/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.parsepasses.contextautoesc;

import com.google.template.soy.parsepasses.contextautoesc.EscapingMode;
import com.google.template.soy.parsepasses.contextautoesc.SoyAutoescapeException;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.AutoescapeMode;
import com.google.template.soy.soytree.CallParamContentNode;
import com.google.template.soy.soytree.LetContentNode;
import com.google.template.soy.soytree.PrintDirectiveNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.TemplateNode;

public final class CheckEscapingSanityVisitor
extends AbstractSoyNodeVisitor<Void> {
    private AutoescapeMode autoescapeMode;

    private boolean isCurrTemplateContextuallyAutoescaped() {
        return this.autoescapeMode == AutoescapeMode.CONTEXTUAL || this.autoescapeMode == AutoescapeMode.STRICT;
    }

    @Override
    protected void visitTemplateNode(TemplateNode node) {
        this.autoescapeMode = node.getAutoescapeMode();
        this.visitChildren(node);
    }

    @Override
    protected void visitPrintDirectiveNode(PrintDirectiveNode node) {
        EscapingMode escapingMode = EscapingMode.fromDirective(node.getName());
        if (escapingMode != null && escapingMode.isInternalOnly) {
            throw SoyAutoescapeException.createWithNode("Print directive " + node.getName() + " is only for internal use by the Soy compiler.", node);
        }
    }

    @Override
    protected void visitLetContentNode(LetContentNode node) {
        this.visitRenderUnitNode(node, "let", "{let $x: $y /}");
    }

    @Override
    protected void visitCallParamContentNode(CallParamContentNode node) {
        this.visitRenderUnitNode(node, "param", "{param x: $y /}");
    }

    private void visitRenderUnitNode(SoyNode.RenderUnitNode node, String nodeName, String selfClosingExample) {
        AutoescapeMode oldMode = this.autoescapeMode;
        if (node.getContentKind() != null) {
            if (!this.isCurrTemplateContextuallyAutoescaped()) {
                throw SoyAutoescapeException.createWithNode("{" + nodeName + "} node with 'kind' attribute is only permitted in contextually autoescaped templates: " + node.toSourceString(), node);
            }
            this.autoescapeMode = AutoescapeMode.STRICT;
        } else if (this.autoescapeMode == AutoescapeMode.STRICT) {
            throw SoyAutoescapeException.createWithNode("In strict templates, {" + nodeName + "}...{/" + nodeName + "} blocks require an explicit kind=\"<type>\". This restriction will be lifted soon once a reasonable default is chosen. (Note that " + selfClosingExample + " is NOT subject to this restriction). Cause: " + node.getTagString(), node);
        }
        this.visitChildren(node);
        this.autoescapeMode = oldMode;
    }

    @Override
    protected void visitSoyNode(SoyNode node) {
        if (node instanceof SoyNode.ParentSoyNode) {
            this.visitChildren((SoyNode.ParentSoyNode)node);
        }
    }
}

