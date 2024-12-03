/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.google.common.collect.ImmutableSortedSet
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package com.google.template.soy.sharedpasses;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.CallBasicNode;
import com.google.template.soy.soytree.CallDelegateNode;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.SoytreeUtils;
import com.google.template.soy.soytree.TemplateBasicNode;
import com.google.template.soy.soytree.TemplateDelegateNode;
import com.google.template.soy.soytree.TemplateNode;
import com.google.template.soy.soytree.TemplateRegistry;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public class FindTransitiveDepTemplatesVisitor
extends AbstractSoyNodeVisitor<TransitiveDepTemplatesInfo> {
    private TemplateRegistry templateRegistry;
    @VisibleForTesting
    Map<TemplateNode, TransitiveDepTemplatesInfo> templateToFinishedInfoMap;
    private TemplateVisitInfo currTemplateVisitInfo;
    private Deque<TemplateVisitInfo> activeTemplateVisitInfoStack;
    private Set<TemplateNode> activeTemplateSet;
    private Map<TemplateNode, TemplateVisitInfo> visitedTemplateToInfoMap;

    public FindTransitiveDepTemplatesVisitor(@Nullable TemplateRegistry templateRegistry) {
        this.templateRegistry = templateRegistry;
        this.templateToFinishedInfoMap = Maps.newHashMap();
    }

    @Override
    public TransitiveDepTemplatesInfo exec(SoyNode rootTemplate) {
        Preconditions.checkArgument((boolean)(rootTemplate instanceof TemplateNode));
        TemplateNode rootTemplateCast = (TemplateNode)rootTemplate;
        if (this.templateRegistry == null) {
            SoyFileSetNode soyTree = rootTemplateCast.getParent().getParent();
            this.templateRegistry = new TemplateRegistry(soyTree);
        }
        if (this.templateToFinishedInfoMap.containsKey(rootTemplateCast)) {
            return this.templateToFinishedInfoMap.get(rootTemplateCast);
        }
        this.currTemplateVisitInfo = null;
        this.activeTemplateVisitInfoStack = new ArrayDeque<TemplateVisitInfo>();
        this.activeTemplateSet = Sets.newHashSet();
        this.visitedTemplateToInfoMap = Maps.newHashMap();
        this.visit(rootTemplateCast);
        if (this.activeTemplateVisitInfoStack.size() != 0 || this.activeTemplateSet.size() != 0) {
            throw new AssertionError();
        }
        for (TemplateVisitInfo templateVisitInfo : this.visitedTemplateToInfoMap.values()) {
            this.templateToFinishedInfoMap.put(templateVisitInfo.rootTemplate, templateVisitInfo.toFinishedInfo());
        }
        return this.templateToFinishedInfoMap.get(rootTemplateCast);
    }

    public ImmutableMap<TemplateNode, TransitiveDepTemplatesInfo> execOnMultipleTemplates(Iterable<TemplateNode> rootTemplates) {
        ImmutableMap.Builder resultBuilder = ImmutableMap.builder();
        for (TemplateNode rootTemplate : rootTemplates) {
            resultBuilder.put((Object)rootTemplate, (Object)this.exec(rootTemplate));
        }
        return resultBuilder.build();
    }

    public ImmutableMap<TemplateNode, TransitiveDepTemplatesInfo> execOnAllTemplates(SoyFileSetNode soyTree) {
        List<TemplateNode> allTemplates = SoytreeUtils.getAllNodesOfType(soyTree, TemplateNode.class, false);
        return this.execOnMultipleTemplates(allTemplates);
    }

    @Override
    protected void visitTemplateNode(TemplateNode node) {
        if (this.templateToFinishedInfoMap.containsKey(node)) {
            throw new AssertionError();
        }
        if (this.visitedTemplateToInfoMap.containsKey(node)) {
            throw new AssertionError();
        }
        this.currTemplateVisitInfo = new TemplateVisitInfo(node, this.visitedTemplateToInfoMap.size());
        this.visitedTemplateToInfoMap.put(node, this.currTemplateVisitInfo);
        this.currTemplateVisitInfo.depTemplateSet.add(node);
        this.visitChildren(node);
        this.currTemplateVisitInfo = null;
    }

    @Override
    protected void visitCallBasicNode(CallBasicNode node) {
        this.visitChildren(node);
        TemplateBasicNode callee = this.templateRegistry.getBasicTemplate(node.getCalleeName());
        if (callee == null) {
            this.currTemplateVisitInfo.hasExternalCalls = true;
            return;
        }
        this.processCalleeHelper(callee);
    }

    @Override
    protected void visitCallDelegateNode(CallDelegateNode node) {
        this.visitChildren(node);
        this.currTemplateVisitInfo.hasDelCalls = true;
        Set<TemplateRegistry.DelegateTemplateDivision> delTemplateDivisions = this.templateRegistry.getDelTemplateDivisionsForAllVariants(node.getDelCalleeName());
        if (delTemplateDivisions != null) {
            for (TemplateRegistry.DelegateTemplateDivision division : delTemplateDivisions) {
                for (TemplateDelegateNode delCallee : division.delPackageNameToDelTemplateMap.values()) {
                    this.processCalleeHelper(delCallee);
                }
            }
        }
    }

    private void processCalleeHelper(TemplateNode callee) {
        if (this.templateToFinishedInfoMap.containsKey(callee)) {
            this.currTemplateVisitInfo.incorporateCalleeFinishedInfo(this.templateToFinishedInfoMap.get(callee));
        } else if (callee != this.currTemplateVisitInfo.rootTemplate) {
            if (this.activeTemplateSet.contains(callee)) {
                this.currTemplateVisitInfo.maybeUpdateEarliestEquivalent(this.visitedTemplateToInfoMap.get(callee));
            } else if (this.visitedTemplateToInfoMap.containsKey(callee)) {
                this.currTemplateVisitInfo.incorporateCalleeVisitInfo(this.visitedTemplateToInfoMap.get(callee), this.activeTemplateSet);
            } else {
                this.activeTemplateVisitInfoStack.push(this.currTemplateVisitInfo);
                this.activeTemplateSet.add(this.currTemplateVisitInfo.rootTemplate);
                this.visit(callee);
                this.currTemplateVisitInfo = this.activeTemplateVisitInfoStack.pop();
                this.activeTemplateSet.remove(this.currTemplateVisitInfo.rootTemplate);
                this.currTemplateVisitInfo.incorporateCalleeVisitInfo(this.visitedTemplateToInfoMap.get(callee), this.activeTemplateSet);
            }
        }
    }

    @Override
    protected void visitSoyNode(SoyNode node) {
        if (node instanceof SoyNode.ParentSoyNode) {
            this.visitChildren((SoyNode.ParentSoyNode)node);
        }
    }

    private static class TemplateVisitInfo {
        public final TemplateNode rootTemplate;
        public final int visitOrdinal;
        public TemplateVisitInfo visitInfoOfEarliestEquivalent;
        public Set<TemplateNode> depTemplateSet;
        public boolean hasExternalCalls;
        public boolean hasDelCalls;
        private TransitiveDepTemplatesInfo finishedInfo;

        public TemplateVisitInfo(TemplateNode template, int visitOrdinal) {
            this.rootTemplate = template;
            this.visitOrdinal = visitOrdinal;
            this.visitInfoOfEarliestEquivalent = null;
            this.depTemplateSet = Sets.newHashSet();
            this.hasExternalCalls = false;
            this.hasDelCalls = false;
            this.finishedInfo = null;
        }

        public void maybeUpdateEarliestEquivalent(TemplateVisitInfo visitInfoOfNewEquivalent) {
            Preconditions.checkArgument((visitInfoOfNewEquivalent != this ? 1 : 0) != 0);
            if (this.visitInfoOfEarliestEquivalent == null || visitInfoOfNewEquivalent.visitOrdinal < this.visitInfoOfEarliestEquivalent.visitOrdinal) {
                this.visitInfoOfEarliestEquivalent = visitInfoOfNewEquivalent;
            }
        }

        public void incorporateCalleeFinishedInfo(TransitiveDepTemplatesInfo calleeFinishedInfo) {
            this.depTemplateSet.addAll((Collection<TemplateNode>)calleeFinishedInfo.depTemplateSet);
            this.hasExternalCalls |= calleeFinishedInfo.hasExternalCalls;
            this.hasDelCalls |= calleeFinishedInfo.hasDelCalls;
        }

        public void incorporateCalleeVisitInfo(TemplateVisitInfo calleeVisitInfo, Set<TemplateNode> activeTemplateSet) {
            if (calleeVisitInfo.visitInfoOfEarliestEquivalent == null || calleeVisitInfo.visitInfoOfEarliestEquivalent == this) {
                this.incorporateCalleeVisitInfoHelper(calleeVisitInfo);
            } else if (activeTemplateSet.contains(calleeVisitInfo.visitInfoOfEarliestEquivalent.rootTemplate)) {
                this.maybeUpdateEarliestEquivalent(calleeVisitInfo.visitInfoOfEarliestEquivalent);
                this.incorporateCalleeVisitInfoHelper(calleeVisitInfo);
            } else {
                this.incorporateCalleeVisitInfo(calleeVisitInfo.visitInfoOfEarliestEquivalent, activeTemplateSet);
            }
        }

        private void incorporateCalleeVisitInfoHelper(TemplateVisitInfo calleeVisitInfo) {
            this.depTemplateSet.addAll(calleeVisitInfo.depTemplateSet);
            this.hasExternalCalls |= calleeVisitInfo.hasExternalCalls;
            this.hasDelCalls |= calleeVisitInfo.hasDelCalls;
        }

        public TransitiveDepTemplatesInfo toFinishedInfo() {
            if (this.finishedInfo == null) {
                this.finishedInfo = this.visitInfoOfEarliestEquivalent != null ? this.visitInfoOfEarliestEquivalent.toFinishedInfo() : new TransitiveDepTemplatesInfo(this.depTemplateSet, this.hasExternalCalls, this.hasDelCalls);
            }
            return this.finishedInfo;
        }
    }

    public static class TransitiveDepTemplatesInfo {
        public final ImmutableSortedSet<TemplateNode> depTemplateSet;
        public final boolean hasExternalCalls;
        public final boolean hasDelCalls;

        public TransitiveDepTemplatesInfo(Set<TemplateNode> depTemplateSet, boolean hasExternalCalls, boolean hasDelCalls) {
            this.depTemplateSet = ImmutableSortedSet.copyOf((Comparator)new Comparator<TemplateNode>(){

                @Override
                public int compare(TemplateNode o1, TemplateNode o2) {
                    return o1.getTemplateName().compareTo(o2.getTemplateName());
                }
            }, depTemplateSet);
            this.hasExternalCalls = hasExternalCalls;
            this.hasDelCalls = hasDelCalls;
        }

        public static TransitiveDepTemplatesInfo merge(Iterable<? extends TransitiveDepTemplatesInfo> infosToMerge) {
            ImmutableSet.Builder depTemplateSetBuilder = ImmutableSet.builder();
            boolean hasExternalCalls = false;
            boolean hasDelCalls = false;
            for (TransitiveDepTemplatesInfo transitiveDepTemplatesInfo : infosToMerge) {
                depTemplateSetBuilder.addAll(transitiveDepTemplatesInfo.depTemplateSet);
                hasExternalCalls |= transitiveDepTemplatesInfo.hasExternalCalls;
                hasDelCalls |= transitiveDepTemplatesInfo.hasDelCalls;
            }
            return new TransitiveDepTemplatesInfo((Set<TemplateNode>)depTemplateSetBuilder.build(), hasExternalCalls, hasDelCalls);
        }
    }
}

