/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 */
package com.google.template.soy.soytree;

import com.google.common.collect.ImmutableList;
import com.google.template.soy.basetree.SyntaxVersionBound;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.soytree.AutoescapeMode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.TemplateNode;
import com.google.template.soy.soytree.defn.TemplateParam;
import javax.annotation.Nullable;

public class TemplateBasicNode
extends TemplateNode {
    private final boolean isOverride;

    TemplateBasicNode(int id, @Nullable SyntaxVersionBound syntaxVersionBound, String cmdText, TemplateNode.SoyFileHeaderInfo soyFileHeaderInfo, String templateName, @Nullable String partialTemplateName, String templateNameForUserMsgs, boolean isOverride, boolean isPrivate, AutoescapeMode autoescapeMode, SanitizedContent.ContentKind contentKind, ImmutableList<String> requiredCssNamespaces, String soyDoc, String soyDocDesc, ImmutableList<TemplateParam> params) {
        super(id, syntaxVersionBound, "template", cmdText, soyFileHeaderInfo, templateName, partialTemplateName, templateNameForUserMsgs, isPrivate, autoescapeMode, contentKind, requiredCssNamespaces, soyDoc, soyDocDesc, params);
        this.isOverride = isOverride;
    }

    protected TemplateBasicNode(TemplateBasicNode orig) {
        super(orig);
        this.isOverride = orig.isOverride;
    }

    @Override
    public SoyNode.Kind getKind() {
        return SoyNode.Kind.TEMPLATE_BASIC_NODE;
    }

    public boolean isOverride() {
        return this.isOverride;
    }

    @Override
    public TemplateBasicNode clone() {
        return new TemplateBasicNode(this);
    }
}

