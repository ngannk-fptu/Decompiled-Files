/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Maps
 */
package com.google.template.soy.parsepasses;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.SoyFileNode;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.SoySyntaxExceptionUtils;
import com.google.template.soy.soytree.TemplateBasicNode;
import com.google.template.soy.soytree.TemplateDelegateNode;
import com.google.template.soy.soytree.TemplateNode;
import java.util.Map;

public class CheckOverridesVisitor
extends AbstractSoyNodeVisitor<Void> {
    private Map<String, TemplateBasicNode> basicTemplatesMap;

    @Override
    public Void exec(SoyNode node) {
        this.basicTemplatesMap = Maps.newHashMap();
        this.visit(node);
        return null;
    }

    @Override
    protected void visitSoyFileSetNode(SoyFileSetNode node) {
        this.visitChildren(node);
    }

    @Override
    protected void visitSoyFileNode(SoyFileNode node) {
        this.visitChildren(node);
    }

    @Override
    protected void visitTemplateBasicNode(TemplateBasicNode node) {
        String templateName = node.getTemplateName();
        Preconditions.checkArgument((templateName.charAt(0) != '.' ? 1 : 0) != 0);
        if (this.basicTemplatesMap.containsKey(templateName)) {
            TemplateNode prevTemplate = this.basicTemplatesMap.get(templateName);
            if (!node.isOverride()) {
                SoyFileNode prevTemplateFile = prevTemplate.getNearestAncestor(SoyFileNode.class);
                SoyFileNode currTemplateFile = node.getNearestAncestor(SoyFileNode.class);
                if (currTemplateFile == prevTemplateFile) {
                    throw SoySyntaxExceptionUtils.createWithNode("Found two definitions for template name '" + templateName + "', both in the file " + currTemplateFile.getFilePath() + ".", node);
                }
                String prevTemplateFilePath = prevTemplateFile.getFilePath();
                String currTemplateFilePath = currTemplateFile.getFilePath();
                if (currTemplateFilePath != null && currTemplateFilePath.equals(prevTemplateFilePath)) {
                    throw SoySyntaxExceptionUtils.createWithNode("Found two definitions for template name '" + templateName + "' in two different files with the same name " + currTemplateFilePath + " (perhaps the file was accidentally included twice).", node);
                }
                throw SoySyntaxExceptionUtils.createWithNode("Found two definitions for template name '" + templateName + "' in two different files " + prevTemplateFilePath + " and " + currTemplateFilePath + ".", node);
            }
        } else {
            this.basicTemplatesMap.put(templateName, node);
        }
    }

    @Override
    protected void visitTemplateDelegateNode(TemplateDelegateNode node) {
    }
}

