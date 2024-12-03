/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.streams.api.Html
 *  com.atlassian.streams.api.StreamsEntry
 *  com.atlassian.streams.api.StreamsEntry$ActivityObject
 *  com.atlassian.streams.api.StreamsEntry$Renderer
 *  com.atlassian.streams.api.UserProfile
 *  com.atlassian.streams.api.common.Option
 *  com.atlassian.streams.api.renderer.StreamsEntryRendererFactory
 *  com.atlassian.streams.spi.renderer.Renderers
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.base.Supplier
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Iterables
 */
package com.atlassian.streams.confluence.renderer;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.streams.api.Html;
import com.atlassian.streams.api.StreamsEntry;
import com.atlassian.streams.api.UserProfile;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.api.renderer.StreamsEntryRendererFactory;
import com.atlassian.streams.confluence.changereport.AttachmentActivityItem;
import com.atlassian.streams.spi.renderer.Renderers;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import java.io.Serializable;
import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class AttachmentRendererFactory {
    private final StreamsEntryRendererFactory rendererFactory;
    private final I18nResolver i18nResolver;
    private final TemplateRenderer templateRenderer;
    private final ApplicationProperties applicationProperties;

    public AttachmentRendererFactory(StreamsEntryRendererFactory rendererFactory, I18nResolver i18nResolver, TemplateRenderer templateRenderer, ApplicationProperties applicationProperties) {
        this.rendererFactory = (StreamsEntryRendererFactory)Preconditions.checkNotNull((Object)rendererFactory, (Object)"rendererFactory");
        this.i18nResolver = (I18nResolver)Preconditions.checkNotNull((Object)i18nResolver, (Object)"i18nResolver");
        this.templateRenderer = (TemplateRenderer)Preconditions.checkNotNull((Object)templateRenderer, (Object)"templateRenderer");
        this.applicationProperties = (ApplicationProperties)Preconditions.checkNotNull((Object)applicationProperties, (Object)"applicationProperties");
    }

    public StreamsEntry.Renderer newInstance(Iterable<AttachmentActivityItem.Entry> entries) {
        return new AttachmentRenderer(entries);
    }

    private final Predicate<AttachmentActivityItem.Entry> previewable() {
        return Previewable.INSTANCE;
    }

    private static enum Previewable implements Predicate<AttachmentActivityItem.Entry>
    {
        INSTANCE;


        public boolean apply(AttachmentActivityItem.Entry attachment) {
            return attachment.getPreview().isDefined();
        }
    }

    private final class AttachmentRenderer
    implements StreamsEntry.Renderer {
        private final Function<Iterable<UserProfile>, Html> authorsRenderer;
        private final Function<StreamsEntry.ActivityObject, Option<Html>> targetRenderer;
        private final Iterable<AttachmentActivityItem.Entry> entries;

        public AttachmentRenderer(Iterable<AttachmentActivityItem.Entry> entries) {
            this.authorsRenderer = AttachmentRendererFactory.this.rendererFactory.newAuthorsRenderer();
            this.targetRenderer = AttachmentRendererFactory.this.rendererFactory.newActivityObjectRendererWithSummary();
            this.entries = entries;
        }

        public Html renderTitleAsHtml(StreamsEntry entry) {
            return (Html)entry.getTarget().flatMap(this.targetRenderer).map(this.renderAttachedTo(entry)).getOrElse(this.renderAttached(entry));
        }

        private Supplier<Html> renderAttached(StreamsEntry entry) {
            return () -> new Html(AttachmentRendererFactory.this.i18nResolver.getText("streams.confluence.attached", new Serializable[]{(Serializable)this.authorsRenderer.apply((Object)entry.getAuthors()), Integer.valueOf(Iterables.size((Iterable)entry.getActivityObjects()))}));
        }

        private Function<Html, Html> renderAttachedTo(StreamsEntry entry) {
            return target -> new Html(AttachmentRendererFactory.this.i18nResolver.getText("streams.confluence.attached.to", new Serializable[]{(Serializable)this.authorsRenderer.apply((Object)entry.getAuthors()), Integer.valueOf(Iterables.size((Iterable)entry.getActivityObjects())), target}));
        }

        public Option<Html> renderSummaryAsHtml(StreamsEntry entry) {
            return Option.none();
        }

        public Option<Html> renderContentAsHtml(StreamsEntry entry) {
            String baseUrl = AttachmentRendererFactory.this.applicationProperties.getBaseUrl(UrlMode.ABSOLUTE);
            Collection previewableEntries = StreamSupport.stream(Iterables.filter(this.entries, (Predicate)AttachmentRendererFactory.this.previewable()).spliterator(), false).map(entryItem -> new EntryWrapper((AttachmentActivityItem.Entry)entryItem, this.getAttachmentDownloadPath(baseUrl, entryItem.getDownloadPath()))).collect(Collectors.toList());
            Collection nonPreviewableEntries = StreamSupport.stream(Iterables.filter(this.entries, (Predicate)Predicates.not((Predicate)AttachmentRendererFactory.this.previewable())).spliterator(), false).map(entryItem -> new EntryWrapper((AttachmentActivityItem.Entry)entryItem, this.getAttachmentDownloadPath(baseUrl, entryItem.getDownloadPath()))).collect(Collectors.toList());
            ImmutableMap context = ImmutableMap.of((Object)"previewable", (Object)ImmutableList.copyOf((Collection)previewableEntries), (Object)"nonpreviewable", (Object)ImmutableList.copyOf((Collection)nonPreviewableEntries), (Object)"applicationProperties", (Object)AttachmentRendererFactory.this.applicationProperties);
            return Option.some((Object)new Html(Renderers.render((TemplateRenderer)AttachmentRendererFactory.this.templateRenderer, (String)"attachment-content.vm", (Map)context)));
        }

        private String getAttachmentDownloadPath(String baseUrl, String attachmentPath) {
            URI uri = URI.create(baseUrl);
            String newPath = uri.getPath() + attachmentPath;
            return uri.resolve(newPath).toString();
        }
    }

    public final class EntryWrapper {
        private AttachmentActivityItem.Entry entry;
        private String absoluteDownloadPath;

        public EntryWrapper(AttachmentActivityItem.Entry entry, String absoluteDownloadPath) {
            this.entry = entry;
            this.absoluteDownloadPath = absoluteDownloadPath;
        }

        public AttachmentActivityItem.Entry getEntry() {
            return this.entry;
        }

        public String getAbsoluteDownloadPath() {
            return this.absoluteDownloadPath;
        }
    }
}

