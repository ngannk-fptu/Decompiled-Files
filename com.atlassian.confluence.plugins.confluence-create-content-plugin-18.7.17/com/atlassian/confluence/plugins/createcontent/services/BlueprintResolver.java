/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.plugins.createcontent.services;

import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
import com.atlassian.confluence.plugins.createcontent.impl.ContentTemplateRef;
import com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface BlueprintResolver {
    public WebItemModuleDescriptor getWebItemMatchingBlueprint(String var1);

    public WebItemModuleDescriptor getWebItemMatchingBlueprint(UUID var1);

    public ContentTemplateRef resolveTemplateRef(ContentTemplateRef var1);

    @Nonnull
    public ContentBlueprint resolveContentBlueprint(@Nonnull String var1, @Nullable String var2) throws IllegalArgumentException;

    @Nonnull
    public ContentBlueprint getContentBlueprint(String var1, String var2, String var3) throws IllegalArgumentException;
}

