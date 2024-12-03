/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.webresource.ResourceType
 *  com.atlassian.fugue.Option
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.plugin.webresource;

import com.atlassian.confluence.api.model.content.webresource.ResourceType;
import com.atlassian.fugue.Option;
import java.io.Writer;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface ConfluenceWebResourceService {
    public void writeConfluenceResourceTags(@NonNull Writer var1, @Nullable Style var2, @Nullable String var3);

    @Deprecated
    default public Map<ResourceType, Iterable<String>> computeConfluenceResourceUris(Option<Style> style, Option<String> spaceKey) {
        return this.calculateConfluenceResourceUris((Style)((Object)style.getOrNull()), (String)spaceKey.getOrNull());
    }

    public Map<ResourceType, Iterable<String>> calculateConfluenceResourceUris(@Nullable Style var1, @Nullable String var2);

    public static enum Style {
        ADMIN;

    }
}

