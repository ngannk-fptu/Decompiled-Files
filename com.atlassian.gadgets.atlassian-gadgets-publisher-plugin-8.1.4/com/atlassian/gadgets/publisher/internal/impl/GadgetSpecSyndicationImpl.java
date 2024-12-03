/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.GadgetParsingException
 *  com.atlassian.gadgets.plugins.PluginGadgetSpec
 *  com.atlassian.gadgets.plugins.PluginGadgetSpecEventListener
 *  com.atlassian.gadgets.util.GadgetSpecUrlBuilder
 *  com.atlassian.gadgets.util.Uri
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.component.ClasspathComponent
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.ImmutableList
 *  com.rometools.rome.feed.atom.Entry
 *  com.rometools.rome.feed.atom.Feed
 *  com.rometools.rome.feed.atom.Link
 *  com.rometools.rome.feed.atom.Person
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.gadgets.publisher.internal.impl;

import com.atlassian.gadgets.GadgetParsingException;
import com.atlassian.gadgets.plugins.PluginGadgetSpec;
import com.atlassian.gadgets.plugins.PluginGadgetSpecEventListener;
import com.atlassian.gadgets.publisher.internal.GadgetSpecSyndication;
import com.atlassian.gadgets.publisher.internal.impl.PublishedGadgetSpecStore;
import com.atlassian.gadgets.util.GadgetSpecUrlBuilder;
import com.atlassian.gadgets.util.Uri;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.component.ClasspathComponent;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.sal.api.ApplicationProperties;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.rometools.rome.feed.atom.Entry;
import com.rometools.rome.feed.atom.Feed;
import com.rometools.rome.feed.atom.Link;
import com.rometools.rome.feed.atom.Person;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={PluginGadgetSpecEventListener.class})
public class GadgetSpecSyndicationImpl
implements GadgetSpecSyndication,
PluginGadgetSpecEventListener {
    private static final String MODULE_KEY = "com.atlassian.gadgets.publisher:ajs-gadgets";
    private final PublishedGadgetSpecStore store;
    private final GadgetSpecUrlBuilder urlBuilder;
    private final ApplicationProperties applicationProperties;
    private final PluginAccessor pluginAccessor;
    private volatile Date lastModified = new Date();
    private final WebResourceUrlProvider webResourceUrlProvider;
    private final Function<PluginGadgetSpec, Entry> pluginGadgetSpecToEntryFunction = new Function<PluginGadgetSpec, Entry>(){

        public Entry apply(PluginGadgetSpec spec) {
            String specUrl = Uri.resolveUriAgainstBase((String)GadgetSpecSyndicationImpl.this.applicationProperties.getBaseUrl(), (String)GadgetSpecSyndicationImpl.this.urlBuilder.buildGadgetSpecUrl(spec)).toASCIIString();
            Entry entry = new Entry();
            entry.setId(specUrl);
            entry.setUpdated(GadgetSpecSyndicationImpl.this.pluginAccessor.getPlugin(spec.getPluginKey()).getDateLoaded());
            entry.setTitle("Gadget spec at " + entry.getId());
            this.addAlternateLink(entry, specUrl);
            return entry;
        }

        private void addAlternateLink(Entry entry, String specUrl) {
            Link link = new Link();
            link.setHref(specUrl);
            link.setRel("alternate");
            entry.getAlternateLinks().add(link);
        }
    };

    @Autowired
    public GadgetSpecSyndicationImpl(PublishedGadgetSpecStore store, @ClasspathComponent GadgetSpecUrlBuilder urlBuilder, @ComponentImport ApplicationProperties applicationProperties, @ComponentImport PluginAccessor pluginAccessor, @ComponentImport WebResourceUrlProvider webResourceUrlProvider) {
        this.store = (PublishedGadgetSpecStore)Preconditions.checkNotNull((Object)store, (Object)"store");
        this.urlBuilder = (GadgetSpecUrlBuilder)Preconditions.checkNotNull((Object)urlBuilder, (Object)"urlBuilder");
        this.applicationProperties = (ApplicationProperties)Preconditions.checkNotNull((Object)applicationProperties, (Object)"applicationProperties");
        this.pluginAccessor = (PluginAccessor)Preconditions.checkNotNull((Object)pluginAccessor, (Object)"pluginAccessor");
        this.webResourceUrlProvider = (WebResourceUrlProvider)Preconditions.checkNotNull((Object)webResourceUrlProvider, (Object)"webResourceUrlProvider");
    }

    @Override
    public Feed getFeed() {
        Feed feed = new Feed();
        feed.setId(this.urlBuilder.buildGadgetSpecFeedUrl());
        feed.setTitle("Gadget specs published from " + this.applicationProperties.getBaseUrl());
        feed.setUpdated(this.lastModified);
        Person person = new Person();
        person.setName(this.applicationProperties.getDisplayName());
        feed.setAuthors((List)ImmutableList.of((Object)person));
        feed.setIcon(this.webResourceUrlProvider.getStaticPluginResourceUrl(MODULE_KEY, "images/icons/" + this.applicationProperties.getDisplayName().toLowerCase() + ".png", UrlMode.ABSOLUTE));
        this.addLink(feed, this.applicationProperties.getBaseUrl(), "base");
        this.addGadgetSpecEntries(feed);
        return feed;
    }

    private void addLink(Feed feed, String baseUrl, String rel) {
        Link link = new Link();
        link.setHref(baseUrl);
        link.setRel(rel);
        feed.getOtherLinks().add(link);
    }

    private boolean addGadgetSpecEntries(Feed feed) {
        return feed.getEntries().addAll(Collections2.transform(this.store.getAll(), this.toEntries()));
    }

    private Function<PluginGadgetSpec, Entry> toEntries() {
        return this.pluginGadgetSpecToEntryFunction;
    }

    public void pluginGadgetSpecEnabled(PluginGadgetSpec pluginGadgetSpec) throws GadgetParsingException {
        Preconditions.checkNotNull((Object)pluginGadgetSpec, (Object)"pluginGadgetSpec");
        if (pluginGadgetSpec.isHostedExternally()) {
            return;
        }
        this.lastModified = new Date();
    }

    public void pluginGadgetSpecDisabled(PluginGadgetSpec pluginGadgetSpec) {
        Preconditions.checkNotNull((Object)pluginGadgetSpec, (Object)"pluginGadgetSpec");
        if (pluginGadgetSpec.isHostedExternally()) {
            return;
        }
        this.lastModified = new Date();
    }
}

