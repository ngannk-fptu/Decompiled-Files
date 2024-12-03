/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webresource.api.UrlMode
 *  com.atlassian.webresource.api.assembler.WebResource
 *  com.atlassian.webresource.api.assembler.WebResourceSet
 *  com.atlassian.webresource.api.assembler.resource.ResourcePhase
 *  com.atlassian.webresource.api.data.PluginDataResource
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Iterables
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.webresource.assembler;

import com.atlassian.plugin.webresource.ResourceUrl;
import com.atlassian.plugin.webresource.assembler.ResourceUrls;
import com.atlassian.plugin.webresource.assembler.WebResourceInformation;
import com.atlassian.plugin.webresource.assembler.html.HtmlTagWriter;
import com.atlassian.plugin.webresource.assembler.html.HtmlWriterFactory;
import com.atlassian.plugin.webresource.assembler.html.PrefetchHtmlTagWriter;
import com.atlassian.plugin.webresource.impl.RequestState;
import com.atlassian.plugin.webresource.impl.config.Config;
import com.atlassian.webresource.api.UrlMode;
import com.atlassian.webresource.api.assembler.WebResource;
import com.atlassian.webresource.api.assembler.WebResourceSet;
import com.atlassian.webresource.api.assembler.resource.ResourcePhase;
import com.atlassian.webresource.api.data.PluginDataResource;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public class DefaultWebResourceSet
implements WebResourceSet {
    private final boolean complete;
    private final Config config;
    private final HtmlWriterFactory htmlWriterFactory;
    private final RequestState requestState;
    private final Collection<WebResourceInformation> webResourceInformation;

    public DefaultWebResourceSet(boolean complete, @Nonnull Config config, @Nonnull RequestState requestState, @Nonnull Collection<WebResourceInformation> webResourceInformation) {
        this.complete = complete;
        this.config = config;
        this.requestState = requestState;
        this.webResourceInformation = webResourceInformation;
        this.htmlWriterFactory = new HtmlWriterFactory(config, requestState);
    }

    public DefaultWebResourceSet(@Nonnull RequestState requestState, @Nonnull List<PluginDataResource> data, @Nonnull List<ResourceUrl> resourceUrls, boolean complete, @Nonnull Config config) {
        this(complete, config, requestState, Collections.singletonList(new WebResourceInformation(data, ResourcePhase.defaultPhase(), resourceUrls)));
    }

    @Nonnull
    public Iterable<WebResource> getResources() {
        Collection webResources = this.webResourceInformation.stream().map(WebResourceInformation::getData).flatMap(Collection::stream).collect(Collectors.toCollection(LinkedList::new));
        webResources.addAll(this.webResourceInformation.stream().map(WebResourceInformation::getResourceUrls).flatMap(Collection::stream).map(ResourceUrls::getPluginUrlResource).collect(Collectors.toCollection(LinkedList::new)));
        return webResources;
    }

    @Nonnull
    public <T extends WebResource> Iterable<T> getResources(@Nonnull Class<T> clazz) {
        return Iterables.filter(this.getResources(), (Predicate)Predicates.instanceOf(clazz));
    }

    @Nonnull
    public List<ResourceUrl> getResourceUrls() {
        return this.webResourceInformation.stream().map(WebResourceInformation::getUrls).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public boolean isComplete() {
        return this.complete;
    }

    public void writeHtmlTags(@Nonnull Writer writer, @Nonnull UrlMode urlMode) {
        this.writeHtmlTags(writer, urlMode, (Predicate<WebResource>)Predicates.alwaysTrue());
    }

    public void writeHtmlTags(@Nonnull Writer writer, @Nonnull UrlMode urlMode, @Nonnull Predicate<WebResource> predicate) {
        this.writeHtmlTags(writer, urlMode, predicate, (Predicate<ResourceUrl>)Predicates.alwaysTrue());
    }

    public void writePrefetchLinks(@Nonnull Writer writer, @Nonnull UrlMode urlMode) {
        PrefetchHtmlTagWriter htmlTagWriter = new PrefetchHtmlTagWriter(this.config, this.requestState, writer, urlMode);
        this.webResourceInformation.stream().map(WebResourceInformation::getResourceUrls).forEach(htmlTagWriter::writeHtmlTag);
    }

    public void writeHtmlTags(@Nonnull Writer writer, @Nonnull UrlMode urlMode, @Nonnull Predicate<WebResource> predicate, @Nonnull Predicate<ResourceUrl> legacyPredicate) {
        this.webResourceInformation.forEach(information -> {
            HtmlTagWriter htmlTagWriter = this.htmlWriterFactory.get(information.getResourcePhase(), writer, urlMode);
            htmlTagWriter.writeHtmlTag((WebResourceInformation)information, predicate, legacyPredicate);
        });
    }
}

