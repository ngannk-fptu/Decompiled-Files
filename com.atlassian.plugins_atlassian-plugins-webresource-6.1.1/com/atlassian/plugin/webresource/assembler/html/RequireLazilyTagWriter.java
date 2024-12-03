/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webresource.api.UrlMode
 *  com.atlassian.webresource.api.assembler.resource.PluginUrlResource$BatchType
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.plugin.webresource.assembler.html;

import com.atlassian.plugin.webresource.ResourceUrl;
import com.atlassian.plugin.webresource.assembler.ResourceUrls;
import com.atlassian.plugin.webresource.assembler.html.HtmlTagFormatter;
import com.atlassian.webresource.api.UrlMode;
import com.atlassian.webresource.api.assembler.resource.PluginUrlResource;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RequireLazilyTagWriter
implements HtmlTagFormatter {
    @Nullable
    private UrlMode urlMode;

    public RequireLazilyTagWriter(@Nullable UrlMode urlMode) {
        this.urlMode = urlMode;
    }

    @Override
    @Nonnull
    public String format(@Nonnull ResourceUrls resourceUrls) {
        Objects.requireNonNull(resourceUrls, "The resource urls are mandatory for the creation of the require lazily tag");
        ResourceUrl batchedResources = resourceUrls.getResourceUrl();
        String prefix = batchedResources.getBatchType().equals((Object)PluginUrlResource.BatchType.CONTEXT) ? "wrc!" : "wr!";
        List formattedKeys = Arrays.stream(batchedResources.getKey().split(",")).map(key -> "\"" + prefix + key + "\"").collect(Collectors.toList());
        return "<script>" + String.format("WRM.requireLazily([%s])", String.join((CharSequence)",", formattedKeys)) + "</script>";
    }

    @Override
    public boolean matches(@Nonnull String resourceName) {
        return true;
    }
}

