/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 */
package com.google.template.soy.parsepasses.contextautoesc;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.data.SanitizedContentOperator;
import com.google.template.soy.parsepasses.contextautoesc.Context;
import com.google.template.soy.parsepasses.contextautoesc.InferenceEngine;
import com.google.template.soy.parsepasses.contextautoesc.Inferences;
import com.google.template.soy.parsepasses.contextautoesc.Rewriter;
import com.google.template.soy.parsepasses.contextautoesc.SlicedRawTextNode;
import com.google.template.soy.parsepasses.contextautoesc.SoyAutoescapeException;
import com.google.template.soy.parsepasses.contextautoesc.TemplateCallGraph;
import com.google.template.soy.shared.restricted.SoyPrintDirective;
import com.google.template.soy.soytree.AutoescapeMode;
import com.google.template.soy.soytree.SoyFileNode;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.soytree.TemplateBasicNode;
import com.google.template.soy.soytree.TemplateDelegateNode;
import com.google.template.soy.soytree.TemplateNode;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ContextualAutoescaper {
    private final ImmutableSet<String> autoescapeCancellingDirectives;
    private final Map<String, SanitizedContent.ContentKind> sanitizedContentOperators;
    private Inferences inferences;
    private List<SlicedRawTextNode> slicedRawTextNodes;
    private static final Predicate<TemplateNode> IS_CONTEXTUAL = new Predicate<TemplateNode>(){

        public boolean apply(TemplateNode templateNode) {
            return templateNode.getAutoescapeMode() == AutoescapeMode.CONTEXTUAL || templateNode.getAutoescapeMode() == AutoescapeMode.STRICT;
        }
    };
    private static final Predicate<TemplateNode> REQUIRES_INFERENCE = new Predicate<TemplateNode>(){

        public boolean apply(TemplateNode templateNode) {
            return templateNode.getAutoescapeMode() == AutoescapeMode.STRICT || templateNode.getAutoescapeMode() == AutoescapeMode.CONTEXTUAL && !templateNode.isPrivate();
        }
    };

    @Inject
    ContextualAutoescaper(final Map<String, SoyPrintDirective> soyDirectivesMap) {
        this((Iterable<? extends String>)ImmutableSet.copyOf((Collection)Collections2.filter(soyDirectivesMap.keySet(), (Predicate)new Predicate<String>(){

            public boolean apply(String directiveName) {
                return ((SoyPrintDirective)soyDirectivesMap.get(directiveName)).shouldCancelAutoescape();
            }
        })), ContextualAutoescaper.makeOperatorKindMap(soyDirectivesMap));
    }

    public ContextualAutoescaper(Iterable<? extends String> autoescapeCancellingDirectives, Map<? extends String, ? extends SanitizedContent.ContentKind> sanitizedContentOperators) {
        this.autoescapeCancellingDirectives = ImmutableSet.copyOf(autoescapeCancellingDirectives);
        this.sanitizedContentOperators = ImmutableMap.copyOf(sanitizedContentOperators);
    }

    public List<TemplateNode> rewrite(SoyFileSetNode fileSet) throws SoyAutoescapeException {
        ImmutableList files = ImmutableList.copyOf(fileSet.getChildren());
        Map<String, ImmutableList<TemplateNode>> templatesByName = ContextualAutoescaper.findTemplates((Iterable<? extends SoyFileNode>)files);
        Inferences inferences = new Inferences((Set<String>)this.autoescapeCancellingDirectives, fileSet.getNodeIdGenerator(), templatesByName);
        ImmutableList.Builder slicedRawTextNodesBuilder = ImmutableList.builder();
        List<TemplateNode> allTemplates = inferences.getAllTemplates();
        TemplateCallGraph callGraph = new TemplateCallGraph(templatesByName);
        Set<TemplateNode> templateNodesToType = callGraph.callersOf(Collections2.filter(allTemplates, IS_CONTEXTUAL));
        templateNodesToType.addAll(Collections2.filter(allTemplates, REQUIRES_INFERENCE));
        for (TemplateNode templateNode : templateNodesToType) {
            Context startContext = templateNode.getContentKind() != null ? Context.getStartContextForContentKind(templateNode.getContentKind()) : Context.HTML_PCDATA;
            InferenceEngine.inferTemplateEndContext(templateNode, startContext, inferences, this.autoescapeCancellingDirectives, (ImmutableList.Builder<SlicedRawTextNode>)slicedRawTextNodesBuilder);
        }
        this.inferences = inferences;
        this.slicedRawTextNodes = slicedRawTextNodesBuilder.build();
        return new Rewriter(inferences, this.sanitizedContentOperators).rewrite(fileSet);
    }

    public Context getTemplateEndContext(String templateName) {
        return this.inferences.getTemplateEndContext(templateName);
    }

    public Map<Integer, Context> getPrintNodeStartContexts() {
        return this.inferences.getPrintNodeStartContexts();
    }

    public List<SlicedRawTextNode> getSlicedRawTextNodes() {
        return this.slicedRawTextNodes;
    }

    private static Map<String, ImmutableList<TemplateNode>> findTemplates(Iterable<? extends SoyFileNode> files) {
        LinkedHashMap templatesByName = Maps.newLinkedHashMap();
        for (SoyFileNode soyFileNode : files) {
            for (TemplateNode template : soyFileNode.getChildren()) {
                String templateName = template instanceof TemplateBasicNode ? template.getTemplateName() : ((TemplateDelegateNode)template).getDelTemplateName();
                if (!templatesByName.containsKey(templateName)) {
                    templatesByName.put(templateName, ImmutableList.builder());
                }
                ((ImmutableList.Builder)templatesByName.get(templateName)).add((Object)template);
            }
        }
        ImmutableMap.Builder templatesByNameBuilder = ImmutableMap.builder();
        for (Map.Entry e : templatesByName.entrySet()) {
            templatesByNameBuilder.put(e.getKey(), (Object)((ImmutableList.Builder)e.getValue()).build());
        }
        return templatesByNameBuilder.build();
    }

    private static Map<String, SanitizedContent.ContentKind> makeOperatorKindMap(Map<String, SoyPrintDirective> soyDirectivesMap) {
        ImmutableMap.Builder operatorKindMapBuilder = ImmutableMap.builder();
        for (SoyPrintDirective directive : soyDirectivesMap.values()) {
            if (!(directive instanceof SanitizedContentOperator)) continue;
            operatorKindMapBuilder.put((Object)directive.getName(), (Object)((SanitizedContentOperator)((Object)directive)).getContentKind());
        }
        return operatorKindMapBuilder.build();
    }
}

