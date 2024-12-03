/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  com.google.inject.Inject
 */
package com.google.template.soy.jssrc.internal;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.template.soy.jssrc.restricted.SoyLibraryAssistedJsSrcPrintDirective;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.PrintDirectiveNode;
import com.google.template.soy.soytree.SoyNode;
import java.util.Collection;
import java.util.Map;
import java.util.SortedSet;

class GenDirectivePluginRequiresVisitor
extends AbstractSoyNodeVisitor<SortedSet<String>> {
    private final Map<String, SoyLibraryAssistedJsSrcPrintDirective> soyLibraryAssistedJsSrcDirectivesMap;
    private SortedSet<String> requiredJsLibNames;

    @Inject
    public GenDirectivePluginRequiresVisitor(Map<String, SoyLibraryAssistedJsSrcPrintDirective> soyLibraryAssistedJsSrcDirectivesMap) {
        this.soyLibraryAssistedJsSrcDirectivesMap = soyLibraryAssistedJsSrcDirectivesMap;
    }

    @Override
    public SortedSet<String> exec(SoyNode soyNode) {
        this.requiredJsLibNames = Sets.newTreeSet();
        this.visit(soyNode);
        return this.requiredJsLibNames;
    }

    @Override
    protected void visitPrintDirectiveNode(PrintDirectiveNode node) {
        String directiveName = node.getName();
        if (this.soyLibraryAssistedJsSrcDirectivesMap.containsKey(directiveName)) {
            this.requiredJsLibNames.addAll((Collection<String>)this.soyLibraryAssistedJsSrcDirectivesMap.get(directiveName).getRequiredJsLibNames());
        }
    }

    @Override
    protected void visitSoyNode(SoyNode node) {
        if (node instanceof SoyNode.ParentSoyNode) {
            this.visitChildren((SoyNode.ParentSoyNode)node);
        }
    }
}

