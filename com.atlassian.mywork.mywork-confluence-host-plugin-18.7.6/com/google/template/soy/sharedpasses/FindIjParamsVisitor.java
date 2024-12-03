/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ImmutableMultimap
 *  com.google.common.collect.ImmutableMultimap$Builder
 *  com.google.common.collect.ImmutableSortedSet
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 */
package com.google.template.soy.sharedpasses;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Maps;
import com.google.template.soy.sharedpasses.FindIjParamsInExprHelperVisitor;
import com.google.template.soy.sharedpasses.FindTransitiveDepTemplatesVisitor;
import com.google.template.soy.soytree.SoyFileNode;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.soytree.SoytreeUtils;
import com.google.template.soy.soytree.TemplateNode;
import com.google.template.soy.soytree.TemplateRegistry;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public class FindIjParamsVisitor {
    private final FindTransitiveDepTemplatesVisitor findTransitiveDepTemplatesVisitor;
    private final Map<FindTransitiveDepTemplatesVisitor.TransitiveDepTemplatesInfo, IjParamsInfo> depsInfoToIjParamsInfoMap;
    private final Map<TemplateNode, Set<String>> templateToLocalIjParamsMap;

    public FindIjParamsVisitor(@Nullable TemplateRegistry templateRegistry) {
        this.findTransitiveDepTemplatesVisitor = new FindTransitiveDepTemplatesVisitor(templateRegistry);
        this.depsInfoToIjParamsInfoMap = Maps.newHashMap();
        this.templateToLocalIjParamsMap = Maps.newHashMap();
    }

    public IjParamsInfo exec(TemplateNode rootTemplate) {
        FindTransitiveDepTemplatesVisitor.TransitiveDepTemplatesInfo depsInfo = this.findTransitiveDepTemplatesVisitor.exec(rootTemplate);
        if (!this.depsInfoToIjParamsInfoMap.containsKey(depsInfo)) {
            ImmutableMultimap.Builder ijParamToCalleesMultimapBuilder = ImmutableMultimap.builder();
            for (TemplateNode template : depsInfo.depTemplateSet) {
                if (!this.templateToLocalIjParamsMap.containsKey(template)) {
                    FindIjParamsInExprHelperVisitor helperVisitor = new FindIjParamsInExprHelperVisitor();
                    SoytreeUtils.execOnAllV2Exprs(template, helperVisitor);
                    Set<String> localIjParams = helperVisitor.getResult();
                    this.templateToLocalIjParamsMap.put(template, localIjParams);
                }
                for (String localIjParam : this.templateToLocalIjParamsMap.get(template)) {
                    ijParamToCalleesMultimapBuilder.put((Object)localIjParam, (Object)template);
                }
            }
            IjParamsInfo ijParamsInfo = new IjParamsInfo((ImmutableMultimap<String, TemplateNode>)ijParamToCalleesMultimapBuilder.build(), depsInfo.hasExternalCalls, depsInfo.hasDelCalls);
            this.depsInfoToIjParamsInfoMap.put(depsInfo, ijParamsInfo);
        }
        return this.depsInfoToIjParamsInfoMap.get(depsInfo);
    }

    public ImmutableMap<TemplateNode, IjParamsInfo> execOnAllTemplates(SoyFileSetNode soyTree) {
        ImmutableMap.Builder resultMapBuilder = ImmutableMap.builder();
        for (SoyFileNode soyFile : soyTree.getChildren()) {
            for (TemplateNode template : soyFile.getChildren()) {
                resultMapBuilder.put((Object)template, (Object)this.exec(template));
            }
        }
        return resultMapBuilder.build();
    }

    public static class IjParamsInfo {
        public final ImmutableSortedSet<String> ijParamSet;
        public final ImmutableMultimap<String, TemplateNode> ijParamToCalleesMultimap;
        public final boolean mayHaveIjParamsInExternalCalls;
        public final boolean mayHaveIjParamsInExternalDelCalls;

        public IjParamsInfo(ImmutableMultimap<String, TemplateNode> ijParamToCalleesMultimap, boolean mayHaveIjParamsInExternalCalls, boolean mayHaveIjParamsInExternalDelCalls) {
            this.ijParamToCalleesMultimap = ijParamToCalleesMultimap;
            this.ijParamSet = ImmutableSortedSet.copyOf((Collection)ijParamToCalleesMultimap.keySet());
            this.mayHaveIjParamsInExternalCalls = mayHaveIjParamsInExternalCalls;
            this.mayHaveIjParamsInExternalDelCalls = mayHaveIjParamsInExternalDelCalls;
        }
    }
}

