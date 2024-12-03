/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.sharedpasses;

import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.TemplateNode;

public class ClearSoyDocStringsVisitor
extends AbstractSoyNodeVisitor<Void> {
    @Override
    protected void visitTemplateNode(TemplateNode node) {
        node.clearSoyDocStrings();
    }

    @Override
    protected void visitSoyNode(SoyNode node) {
        if (node instanceof SoyNode.ParentSoyNode) {
            this.visitChildren((SoyNode.ParentSoyNode)node);
        }
    }
}

