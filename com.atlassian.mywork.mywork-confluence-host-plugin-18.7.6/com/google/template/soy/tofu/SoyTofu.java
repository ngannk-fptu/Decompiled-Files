/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSortedSet
 *  javax.annotation.Nullable
 */
package com.google.template.soy.tofu;

import com.google.common.collect.ImmutableSortedSet;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.data.SoyRecord;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.parseinfo.SoyTemplateInfo;
import com.google.template.soy.shared.SoyCssRenamingMap;
import com.google.template.soy.shared.SoyIdRenamingMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public interface SoyTofu {
    public String getNamespace();

    public SoyTofu forNamespace(@Nullable String var1);

    public boolean isCaching();

    public void addToCache(@Nullable SoyMsgBundle var1, @Nullable SoyCssRenamingMap var2);

    public Renderer newRenderer(SoyTemplateInfo var1);

    public Renderer newRenderer(String var1);

    public ImmutableSortedSet<String> getUsedIjParamsForTemplate(SoyTemplateInfo var1);

    public ImmutableSortedSet<String> getUsedIjParamsForTemplate(String var1);

    @Deprecated
    public String render(SoyTemplateInfo var1, @Nullable Map<String, ?> var2, @Nullable SoyMsgBundle var3);

    @Deprecated
    public String render(SoyTemplateInfo var1, @Nullable SoyRecord var2, @Nullable SoyMsgBundle var3);

    @Deprecated
    public String render(String var1, @Nullable Map<String, ?> var2, @Nullable SoyMsgBundle var3);

    @Deprecated
    public String render(String var1, @Nullable SoyRecord var2, @Nullable SoyMsgBundle var3);

    public static interface Renderer {
        public Renderer setData(Map<String, ?> var1);

        public Renderer setData(SoyRecord var1);

        public Renderer setIjData(Map<String, ?> var1);

        public Renderer setIjData(SoyRecord var1);

        public Renderer setActiveDelegatePackageNames(Set<String> var1);

        public Renderer setMsgBundle(SoyMsgBundle var1);

        public Renderer setIdRenamingMap(SoyIdRenamingMap var1);

        public Renderer setCssRenamingMap(SoyCssRenamingMap var1);

        public Renderer setDontAddToCache(boolean var1);

        public Renderer setContentKind(SanitizedContent.ContentKind var1);

        public String render();

        public SanitizedContent renderStrict();

        public void render(Appendable var1);
    }
}

