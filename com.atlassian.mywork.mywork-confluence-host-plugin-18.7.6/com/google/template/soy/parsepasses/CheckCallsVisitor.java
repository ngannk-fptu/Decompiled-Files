/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 */
package com.google.template.soy.parsepasses;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.CallBasicNode;
import com.google.template.soy.soytree.CallDelegateNode;
import com.google.template.soy.soytree.CallNode;
import com.google.template.soy.soytree.CallParamNode;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.SoySyntaxExceptionUtils;
import com.google.template.soy.soytree.TemplateNode;
import com.google.template.soy.soytree.TemplateRegistry;
import com.google.template.soy.soytree.defn.TemplateParam;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CheckCallsVisitor
extends AbstractSoyNodeVisitor<List<String>> {
    private TemplateRegistry templateRegistry;

    @Override
    public List<String> exec(SoyNode soyNode) {
        Preconditions.checkArgument((boolean)(soyNode instanceof SoyFileSetNode));
        this.templateRegistry = new TemplateRegistry((SoyFileSetNode)soyNode);
        super.exec(soyNode);
        return null;
    }

    @Override
    protected void visitCallNode(CallNode node) {
        Set<TemplateRegistry.DelegateTemplateDivision> divisions;
        TemplateNode callee;
        this.visitChildren(node);
        if (!node.isPassingData() && (callee = node instanceof CallBasicNode ? this.templateRegistry.getBasicTemplate(((CallBasicNode)node).getCalleeName()) : ((divisions = this.templateRegistry.getDelTemplateDivisionsForAllVariants(((CallDelegateNode)node).getDelCalleeName())) != null ? (TemplateNode)Iterables.get(((TemplateRegistry.DelegateTemplateDivision)Iterables.getFirst(divisions, null)).delPackageNameToDelTemplateMap.values(), (int)0) : null)) != null && callee.getParams() != null) {
            HashSet callerParamKeys = Sets.newHashSet();
            for (Object callerParam : node.getChildren()) {
                callerParamKeys.add(((CallParamNode)callerParam).getKey());
            }
            ArrayList missingParamKeys = Lists.newArrayListWithCapacity((int)2);
            for (TemplateParam calleeParam : callee.getParams()) {
                if (!calleeParam.isRequired() || callerParamKeys.contains(calleeParam.name())) continue;
                missingParamKeys.add(calleeParam.name());
            }
            if (missingParamKeys.size() > 0) {
                String errorMsgEnd = missingParamKeys.size() == 1 ? "param '" + (String)missingParamKeys.get(0) + "'" : "params " + missingParamKeys;
                throw SoySyntaxExceptionUtils.createWithNode(String.format("Call to '%s' is missing required %s.", callee.getTemplateNameForUserMsgs(), errorMsgEnd), node);
            }
        }
    }

    @Override
    protected void visitSoyNode(SoyNode node) {
        if (node instanceof SoyNode.ParentSoyNode) {
            this.visitChildren((SoyNode.ParentSoyNode)node);
        }
    }
}

