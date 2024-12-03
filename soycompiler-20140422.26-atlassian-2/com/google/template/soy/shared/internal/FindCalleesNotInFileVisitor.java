/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Sets
 */
package com.google.template.soy.shared.internal;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.CallBasicNode;
import com.google.template.soy.soytree.SoyFileNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.TemplateNode;
import java.util.Set;
import java.util.SortedSet;

public class FindCalleesNotInFileVisitor
extends AbstractSoyNodeVisitor<SortedSet<String>> {
    private Set<String> templatesInFile;
    private SortedSet<String> calleesNotInFile;

    @Override
    public SortedSet<String> exec(SoyNode node) {
        Preconditions.checkArgument((boolean)(node instanceof SoyFileNode));
        this.visit(node);
        return this.calleesNotInFile;
    }

    @Override
    protected void visitSoyFileNode(SoyFileNode node) {
        this.templatesInFile = Sets.newHashSet();
        for (TemplateNode template : node.getChildren()) {
            this.templatesInFile.add(template.getTemplateName());
        }
        this.calleesNotInFile = Sets.newTreeSet();
        this.visitChildren(node);
    }

    @Override
    protected void visitCallBasicNode(CallBasicNode node) {
        String calleeName = node.getCalleeName();
        if (!this.templatesInFile.contains(calleeName)) {
            this.calleesNotInFile.add(calleeName);
        }
        this.visitChildren(node);
    }

    @Override
    protected void visitSoyNode(SoyNode node) {
        if (node instanceof SoyNode.ParentSoyNode) {
            this.visitChildren((SoyNode.ParentSoyNode)node);
        }
    }
}

