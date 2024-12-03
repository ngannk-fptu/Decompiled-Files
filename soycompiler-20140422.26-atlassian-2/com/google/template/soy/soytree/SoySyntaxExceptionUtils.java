/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.template.soy.soytree;

import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.TemplateNode;
import javax.annotation.Nullable;

public class SoySyntaxExceptionUtils {
    private SoySyntaxExceptionUtils() {
    }

    public static SoySyntaxException createWithNode(String message, SoyNode node) {
        return SoySyntaxExceptionUtils.associateNode(SoySyntaxException.createWithoutMetaInfo(message), node);
    }

    public static SoySyntaxException createCausedWithNode(@Nullable String message, Throwable cause, SoyNode node) {
        return SoySyntaxExceptionUtils.associateNode(SoySyntaxException.createCausedWithoutMetaInfo(message, cause), node);
    }

    public static SoySyntaxException associateNode(SoySyntaxException sse, SoyNode node) {
        TemplateNode template = node.getNearestAncestor(TemplateNode.class);
        String templateName = template != null ? template.getTemplateNameForUserMsgs() : null;
        return sse.associateMetaInfo(node.getSourceLocation(), null, templateName);
    }
}

