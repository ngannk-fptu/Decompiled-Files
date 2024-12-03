/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.ImmutableMultimap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSortedMap
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package com.google.template.soy.sharedpasses;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.CallBasicNode;
import com.google.template.soy.soytree.CallDelegateNode;
import com.google.template.soy.soytree.CallNode;
import com.google.template.soy.soytree.CallParamNode;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.TemplateBasicNode;
import com.google.template.soy.soytree.TemplateDelegateNode;
import com.google.template.soy.soytree.TemplateNode;
import com.google.template.soy.soytree.TemplateRegistry;
import com.google.template.soy.soytree.defn.TemplateParam;
import com.google.template.soy.types.SoyType;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import javax.annotation.Nullable;

public class FindIndirectParamsVisitor
extends AbstractSoyNodeVisitor<IndirectParamsInfo> {
    private TemplateRegistry templateRegistry;
    private boolean isStartOfPass;
    private Set<CallSituation> visitedCallSituations;
    private TemplateNode currTemplate;
    private Set<TemplateNode> currNewAllCallers;
    private Deque<CallerFrame> callerStack;
    private Map<String, TemplateParam> indirectParams;
    private Multimap<String, TemplateNode> paramKeyToCalleesMultimap;
    public Multimap<String, SoyType> indirectParamTypes;
    private boolean mayHaveIndirectParamsInExternalCalls;
    private boolean mayHaveIndirectParamsInExternalDelCalls;

    public FindIndirectParamsVisitor(@Nullable TemplateRegistry templateRegistry) {
        this.templateRegistry = templateRegistry;
    }

    @Override
    public IndirectParamsInfo exec(SoyNode node) {
        Preconditions.checkArgument((boolean)(node instanceof TemplateNode));
        this.isStartOfPass = true;
        this.visitedCallSituations = Sets.newHashSet();
        this.currTemplate = null;
        this.callerStack = new ArrayDeque<CallerFrame>();
        this.callerStack.add(new CallerFrame(null, (Set<TemplateNode>)ImmutableSet.of(), (Set<String>)ImmutableSet.of()));
        this.indirectParams = Maps.newHashMap();
        this.paramKeyToCalleesMultimap = HashMultimap.create();
        this.indirectParamTypes = HashMultimap.create();
        this.mayHaveIndirectParamsInExternalCalls = false;
        this.mayHaveIndirectParamsInExternalDelCalls = false;
        this.visit(node);
        return new IndirectParamsInfo((SortedMap<String, TemplateParam>)ImmutableSortedMap.copyOf(this.indirectParams), this.paramKeyToCalleesMultimap, (Multimap<String, SoyType>)ImmutableMultimap.copyOf(this.indirectParamTypes), this.mayHaveIndirectParamsInExternalCalls, this.mayHaveIndirectParamsInExternalDelCalls);
    }

    @Override
    protected void visitTemplateNode(TemplateNode node) {
        if (this.templateRegistry == null) {
            SoyFileSetNode soyTree = node.getParent().getParent();
            this.templateRegistry = new TemplateRegistry(soyTree);
        }
        if (this.isStartOfPass) {
            this.isStartOfPass = false;
        } else {
            List<TemplateParam> params = node.getParams();
            if (params == null) {
                this.mayHaveIndirectParamsInExternalCalls = true;
            } else {
                for (TemplateParam param : params) {
                    if (this.callerStack.peek().allCallParamKeys.contains(param.name())) continue;
                    if (!this.indirectParams.containsKey(param.name())) {
                        this.indirectParams.put(param.name(), param);
                    }
                    this.paramKeyToCalleesMultimap.put((Object)param.name(), (Object)node);
                    Preconditions.checkNotNull((Object)param.type());
                    this.indirectParamTypes.put((Object)param.name(), (Object)param.type());
                }
            }
        }
        this.currTemplate = node;
        this.currNewAllCallers = null;
        this.visitChildren(node);
    }

    @Override
    protected void visitCallBasicNode(CallBasicNode node) {
        this.visitChildren(node);
        if (!node.isPassingAllData()) {
            return;
        }
        TemplateBasicNode callee = this.templateRegistry.getBasicTemplate(node.getCalleeName());
        if (callee == null) {
            this.mayHaveIndirectParamsInExternalCalls = true;
            return;
        }
        this.visitCalleeHelper(node, callee);
    }

    @Override
    protected void visitCallDelegateNode(CallDelegateNode node) {
        this.visitChildren(node);
        if (!node.isPassingAllData()) {
            return;
        }
        this.mayHaveIndirectParamsInExternalDelCalls = true;
        Set<TemplateRegistry.DelegateTemplateDivision> delTemplateDivisions = this.templateRegistry.getDelTemplateDivisionsForAllVariants(node.getDelCalleeName());
        if (delTemplateDivisions != null) {
            for (TemplateRegistry.DelegateTemplateDivision division : delTemplateDivisions) {
                for (TemplateDelegateNode delCallee : division.delPackageNameToDelTemplateMap.values()) {
                    this.visitCalleeHelper(node, delCallee);
                }
            }
        }
    }

    private void visitCalleeHelper(CallNode caller, TemplateNode callee) {
        HashSet newAllCallParamKeys;
        if (callee == this.currTemplate || this.callerStack.peek().allCallers.contains(callee)) {
            return;
        }
        HashSet prevAllCallParamKeys = this.callerStack.peek().allCallParamKeys;
        HashSet additionalCallParamKeys = Sets.newHashSet();
        for (CallParamNode callParamNode : caller.getChildren()) {
            String callParamKey = callParamNode.getKey();
            if (prevAllCallParamKeys.contains(callParamKey)) continue;
            additionalCallParamKeys.add(callParamKey);
        }
        if (additionalCallParamKeys.size() > 0) {
            newAllCallParamKeys = Sets.newHashSet(prevAllCallParamKeys);
            newAllCallParamKeys.addAll(additionalCallParamKeys);
        } else {
            newAllCallParamKeys = prevAllCallParamKeys;
        }
        CallSituation currCallSituation = new CallSituation(callee, newAllCallParamKeys);
        if (this.visitedCallSituations.contains(currCallSituation)) {
            return;
        }
        this.visitedCallSituations.add(currCallSituation);
        if (this.currNewAllCallers == null) {
            this.currNewAllCallers = Sets.newHashSet(this.callerStack.peek().allCallers);
            this.currNewAllCallers.add(this.currTemplate);
        }
        CallerFrame callerFrame = new CallerFrame(this.currTemplate, this.currNewAllCallers, newAllCallParamKeys);
        this.callerStack.push(callerFrame);
        this.visit(callee);
        CallerFrame poppedCallerFrame = this.callerStack.pop();
        if (poppedCallerFrame != callerFrame) {
            throw new AssertionError();
        }
        this.currTemplate = callerFrame.caller;
        this.currNewAllCallers = callerFrame.allCallers;
    }

    @Override
    protected void visitSoyNode(SoyNode node) {
        if (node instanceof SoyNode.ParentSoyNode) {
            this.visitChildren((SoyNode.ParentSoyNode)node);
        }
    }

    private static class CallSituation {
        private final TemplateNode callee;
        private final Set<String> allCallParamKeys;

        public CallSituation(TemplateNode callee, Set<String> allCallParamKeys) {
            this.callee = callee;
            this.allCallParamKeys = allCallParamKeys;
        }

        public boolean equals(Object other) {
            if (other == null || other.getClass() != this.getClass()) {
                return false;
            }
            CallSituation otherCallSit = (CallSituation)other;
            return otherCallSit.callee == this.callee && otherCallSit.allCallParamKeys.equals(this.allCallParamKeys);
        }

        public int hashCode() {
            return this.callee.hashCode() * 31 + this.allCallParamKeys.hashCode();
        }
    }

    private static class CallerFrame {
        public final TemplateNode caller;
        public final Set<TemplateNode> allCallers;
        public final Set<String> allCallParamKeys;

        public CallerFrame(TemplateNode caller, Set<TemplateNode> allCallers, Set<String> allCallParamKeys) {
            this.caller = caller;
            this.allCallers = allCallers;
            this.allCallParamKeys = allCallParamKeys;
        }
    }

    public static class IndirectParamsInfo {
        public final SortedMap<String, TemplateParam> indirectParams;
        public final Multimap<String, TemplateNode> paramKeyToCalleesMultimap;
        public final Multimap<String, SoyType> indirectParamTypes;
        public final boolean mayHaveIndirectParamsInExternalCalls;
        public final boolean mayHaveIndirectParamsInExternalDelCalls;

        public IndirectParamsInfo(SortedMap<String, TemplateParam> indirectParams, Multimap<String, TemplateNode> paramKeyToCalleesMultimap, Multimap<String, SoyType> indirectParamTypes, boolean mayHaveIndirectParamsInExternalCalls, boolean mayHaveIndirectParamsInExternalDelCalls) {
            this.indirectParams = indirectParams;
            this.paramKeyToCalleesMultimap = paramKeyToCalleesMultimap;
            this.indirectParamTypes = indirectParamTypes;
            this.mayHaveIndirectParamsInExternalCalls = mayHaveIndirectParamsInExternalCalls;
            this.mayHaveIndirectParamsInExternalDelCalls = mayHaveIndirectParamsInExternalDelCalls;
        }
    }
}

