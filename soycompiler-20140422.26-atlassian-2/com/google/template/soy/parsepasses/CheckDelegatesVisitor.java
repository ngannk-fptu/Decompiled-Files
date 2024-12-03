/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Sets
 */
package com.google.template.soy.parsepasses;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.CallBasicNode;
import com.google.template.soy.soytree.CallDelegateNode;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.SoySyntaxExceptionUtils;
import com.google.template.soy.soytree.TemplateBasicNode;
import com.google.template.soy.soytree.TemplateDelegateNode;
import com.google.template.soy.soytree.TemplateNode;
import com.google.template.soy.soytree.TemplateRegistry;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CheckDelegatesVisitor
extends AbstractSoyNodeVisitor<Void> {
    private TemplateRegistry templateRegistry;
    private String currTemplateNameForUserMsgs;
    private String currDelPackageName;

    @Override
    public Void exec(SoyNode soyNode) {
        Preconditions.checkArgument((boolean)(soyNode instanceof SoyFileSetNode));
        this.templateRegistry = new TemplateRegistry((SoyFileSetNode)soyNode);
        this.checkTemplates();
        super.exec(soyNode);
        return null;
    }

    private void checkTemplates() {
        Map<String, TemplateBasicNode> basicTemplatesMap = this.templateRegistry.getBasicTemplatesMap();
        Map<TemplateDelegateNode.DelTemplateKey, List<TemplateRegistry.DelegateTemplateDivision>> delTemplatesMap = this.templateRegistry.getDelTemplatesMap();
        Map<String, Set<TemplateDelegateNode.DelTemplateKey>> delTemplateNameToKeysMap = this.templateRegistry.getDelTemplateNameToKeysMap();
        LinkedHashSet reusedTemplateNames = Sets.newLinkedHashSet();
        for (TemplateDelegateNode.DelTemplateKey delTemplateKey : delTemplatesMap.keySet()) {
            if (!basicTemplatesMap.containsKey(delTemplateKey.name)) continue;
            reusedTemplateNames.add(delTemplateKey.name);
        }
        if (reusedTemplateNames.size() > 0) {
            throw SoySyntaxException.createWithoutMetaInfo("Found template name " + reusedTemplateNames + " being reused for both basic and delegate templates.");
        }
        for (Set set : delTemplateNameToKeysMap.values()) {
            TemplateDelegateNode firstDelTemplate = null;
            HashSet firstParamSet = null;
            SanitizedContent.ContentKind firstContentKind = null;
            for (TemplateDelegateNode.DelTemplateKey delTemplateKey : set) {
                for (TemplateRegistry.DelegateTemplateDivision division : delTemplatesMap.get(delTemplateKey)) {
                    for (TemplateDelegateNode delTemplate : division.delPackageNameToDelTemplateMap.values()) {
                        String currDelPackageName;
                        String string = currDelPackageName = delTemplate.getDelPackageName() != null ? delTemplate.getDelPackageName() : "<default>";
                        if (firstDelTemplate == null) {
                            firstDelTemplate = delTemplate;
                            firstParamSet = Sets.newHashSet(delTemplate.getParams());
                            firstContentKind = delTemplate.getContentKind();
                            continue;
                        }
                        HashSet currParamSet = Sets.newHashSet(delTemplate.getParams());
                        if (!currParamSet.equals(firstParamSet)) {
                            throw SoySyntaxExceptionUtils.createWithNode(String.format("Found delegate template with same name '%s' but different param declarations compared to the definition at %s.", firstDelTemplate.getDelTemplateName(), firstDelTemplate.getSourceLocation().toString()), delTemplate);
                        }
                        if (delTemplate.getContentKind() == firstContentKind) continue;
                        throw SoySyntaxExceptionUtils.createWithNode(String.format("If one deltemplate has strict autoescaping, all its peers must also be strictly autoescaped with the same content kind: %s != %s. Conflicting definition at %s.", new Object[]{firstContentKind, delTemplate.getContentKind(), firstDelTemplate.getSourceLocation().toString()}), delTemplate);
                    }
                }
            }
        }
    }

    @Override
    protected void visitTemplateNode(TemplateNode node) {
        this.currTemplateNameForUserMsgs = node.getTemplateNameForUserMsgs();
        this.currDelPackageName = node.getDelPackageName();
        this.visitChildren(node);
    }

    @Override
    protected void visitCallBasicNode(CallBasicNode node) {
        String calleeDelPackageName;
        String calleeName = node.getCalleeName();
        if (this.templateRegistry.getDelTemplateKeysForAllVariants(calleeName) != null) {
            throw SoySyntaxExceptionUtils.createWithNode(String.format("In template '%s', found a 'call' referencing a delegate template '%s' (expected 'delcall').", this.currTemplateNameForUserMsgs, calleeName), node);
        }
        TemplateBasicNode callee = this.templateRegistry.getBasicTemplate(calleeName);
        if (callee != null && (calleeDelPackageName = callee.getDelPackageName()) != null && !calleeDelPackageName.equals(this.currDelPackageName)) {
            throw SoySyntaxExceptionUtils.createWithNode(String.format("Found illegal call from '%s' to '%s', which is in a different delegate package.", this.currTemplateNameForUserMsgs, callee.getTemplateName()), node);
        }
    }

    @Override
    protected void visitCallDelegateNode(CallDelegateNode node) {
        String delCalleeName = node.getDelCalleeName();
        if (this.templateRegistry.getBasicTemplate(delCalleeName) != null) {
            throw SoySyntaxExceptionUtils.createWithNode(String.format("In template '%s', found a 'delcall' referencing a basic template '%s' (expected 'call').", this.currTemplateNameForUserMsgs, delCalleeName), node);
        }
    }

    @Override
    protected void visitSoyNode(SoyNode node) {
        if (node instanceof SoyNode.ParentSoyNode) {
            this.visitChildren((SoyNode.ParentSoyNode)node);
        }
    }
}

