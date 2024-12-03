/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSortedSet
 *  javax.annotation.Nullable
 */
package com.google.template.soy.tofu.internal;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSortedSet;
import com.google.template.soy.data.SoyRecord;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.parseinfo.SoyTemplateInfo;
import com.google.template.soy.shared.SoyCssRenamingMap;
import com.google.template.soy.tofu.SoyTofu;
import com.google.template.soy.tofu.internal.BaseTofu;
import java.util.Map;
import javax.annotation.Nullable;

class NamespacedTofu
implements SoyTofu {
    private final BaseTofu baseTofu;
    private final String namespace;

    NamespacedTofu(BaseTofu baseTofu, String namespace) {
        Preconditions.checkNotNull((Object)baseTofu);
        this.baseTofu = baseTofu;
        Preconditions.checkArgument((namespace != null && namespace.length() > 0 ? 1 : 0) != 0);
        this.namespace = namespace;
    }

    @Override
    public String getNamespace() {
        return this.namespace;
    }

    @Override
    public SoyTofu forNamespace(@Nullable String namespace) {
        if (namespace == null) {
            return this.baseTofu;
        }
        Preconditions.checkArgument((namespace.charAt(0) != '.' && namespace.charAt(namespace.length() - 1) != '.' ? 1 : 0) != 0, (Object)("Invalid namespace '" + namespace + "' (must not begin or end with a dot)."));
        return new NamespacedTofu(this.baseTofu, namespace);
    }

    private String getFullTemplateName(String templateName) {
        return templateName.charAt(0) == '.' ? this.namespace + templateName : templateName;
    }

    @Override
    public boolean isCaching() {
        return this.baseTofu.isCaching();
    }

    @Override
    public void addToCache(@Nullable SoyMsgBundle msgBundle, @Nullable SoyCssRenamingMap cssRenamingMap) {
        this.baseTofu.addToCache(msgBundle, cssRenamingMap);
    }

    @Override
    public SoyTofu.Renderer newRenderer(SoyTemplateInfo templateInfo) {
        return this.baseTofu.newRenderer(templateInfo);
    }

    @Override
    public SoyTofu.Renderer newRenderer(String templateName) {
        return this.baseTofu.newRenderer(this.getFullTemplateName(templateName));
    }

    @Override
    public ImmutableSortedSet<String> getUsedIjParamsForTemplate(SoyTemplateInfo templateInfo) {
        return this.baseTofu.getUsedIjParamsForTemplate(templateInfo);
    }

    @Override
    public ImmutableSortedSet<String> getUsedIjParamsForTemplate(String templateName) {
        return this.baseTofu.getUsedIjParamsForTemplate(this.getFullTemplateName(templateName));
    }

    @Override
    @Deprecated
    public String render(SoyTemplateInfo templateInfo, @Nullable Map<String, ?> data, @Nullable SoyMsgBundle msgBundle) {
        return this.render(templateInfo.getPartialName(), data, msgBundle);
    }

    @Override
    @Deprecated
    public String render(SoyTemplateInfo templateInfo, @Nullable SoyRecord data, @Nullable SoyMsgBundle msgBundle) {
        return this.render(templateInfo.getPartialName(), data, msgBundle);
    }

    @Override
    @Deprecated
    public String render(String templateName, @Nullable Map<String, ?> data, @Nullable SoyMsgBundle msgBundle) {
        if (templateName.charAt(0) == '.') {
            return this.baseTofu.render(this.namespace + templateName, data, msgBundle);
        }
        return this.baseTofu.render(templateName, data, msgBundle);
    }

    @Override
    @Deprecated
    public String render(String templateName, @Nullable SoyRecord data, @Nullable SoyMsgBundle msgBundle) {
        if (templateName.charAt(0) == '.') {
            return this.baseTofu.render(this.namespace + templateName, data, msgBundle);
        }
        return this.baseTofu.render(templateName, data, msgBundle);
    }
}

