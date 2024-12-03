/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.confluence.spaces.Space
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.plugins.createcontent.api.services;

import com.atlassian.annotations.PublicApi;
import com.atlassian.confluence.spaces.Space;
import javax.annotation.Nullable;

@PublicApi
public interface CreateButtonService {
    public String renderBlueprintButton(Space var1, String var2, String var3, @Nullable String var4, @Nullable String var5);

    public String renderTemplateButton(Space var1, long var2, @Nullable String var4, @Nullable String var5);
}

