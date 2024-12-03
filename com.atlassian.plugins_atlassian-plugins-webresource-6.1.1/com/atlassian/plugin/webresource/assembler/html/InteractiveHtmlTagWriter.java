/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webresource.api.UrlMode
 *  com.atlassian.webresource.api.assembler.resource.PluginUrlResource$BatchType
 *  com.google.common.collect.Maps
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.webresource.assembler.html;

import com.atlassian.plugin.webresource.ResourceUrl;
import com.atlassian.plugin.webresource.assembler.ResourceUrls;
import com.atlassian.plugin.webresource.assembler.html.HtmlTagFormatter;
import com.atlassian.plugin.webresource.assembler.html.HtmlTagWriter;
import com.atlassian.plugin.webresource.impl.RequestState;
import com.atlassian.webresource.api.UrlMode;
import com.atlassian.webresource.api.assembler.resource.PluginUrlResource;
import com.google.common.collect.Maps;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

final class InteractiveHtmlTagWriter
extends HtmlTagWriter {
    InteractiveHtmlTagWriter(@Nonnull RequestState requestState, @Nonnull Writer writer, @Nonnull UrlMode urlMode) {
        super(requestState, writer, Collections.emptyList());
    }

    @Override
    public void writeHtmlTag(@Nonnull Collection<ResourceUrls> resources) {
        LinkedHashMap reducedResources = Maps.newLinkedHashMap();
        resources.forEach(resource -> reducedResources.put(resource.getResourceUrl().getKey(), resource));
        this.writeHtmlTag(this.generateRequireLazyScriptTag(reducedResources.values()));
    }

    @Override
    @Nonnull
    String generateHtmlTag(@Nonnull ResourceUrls resourceUrls, @Nonnull HtmlTagFormatter formatter) {
        Objects.requireNonNull(resourceUrls, "The resource urls are mandatory for the creation of the script tag.");
        Objects.requireNonNull(formatter, "The formatter is mandory for generating the tags.");
        return formatter.format(resourceUrls);
    }

    private String generateRequireLazyScriptTag(Collection<ResourceUrls> resources) {
        List formattedKeys = resources.stream().map(ResourceUrls::getResourceUrl).map(this::generateRequireLazyArguments).flatMap(Collection::stream).collect(Collectors.toList());
        return formattedKeys.size() > 0 ? String.format("<script type=\"module\">WRM.requireLazily([%s])</script>", String.join((CharSequence)",", formattedKeys)) : "";
    }

    private List<String> generateRequireLazyArguments(ResourceUrl batchedResources) {
        String prefix = batchedResources.getBatchType().equals((Object)PluginUrlResource.BatchType.CONTEXT) ? "wrc!" : "wr!";
        String[] split = batchedResources.getKey().split(",");
        return Arrays.stream(split).filter(key -> !key.startsWith("-")).map(key -> "\"" + prefix + key + "\"").collect(Collectors.toList());
    }
}

