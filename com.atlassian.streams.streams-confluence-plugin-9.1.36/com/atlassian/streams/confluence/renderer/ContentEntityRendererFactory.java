/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.Renderer
 *  com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory
 *  com.atlassian.confluence.content.render.xhtml.XmlOutputFactory
 *  com.atlassian.confluence.core.BodyContent
 *  com.atlassian.confluence.core.BodyType
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.streams.api.ActivityObjectType
 *  com.atlassian.streams.api.Html
 *  com.atlassian.streams.api.StreamsEntry
 *  com.atlassian.streams.api.StreamsEntry$ActivityObject
 *  com.atlassian.streams.api.StreamsEntry$Renderer
 *  com.atlassian.streams.api.common.Function2
 *  com.atlassian.streams.api.common.Option
 *  com.atlassian.streams.api.renderer.StreamsEntryRendererFactory
 *  com.atlassian.streams.spi.renderer.Renderers
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.google.common.base.Function
 *  com.google.common.base.Functions
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Iterables
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.streams.confluence.renderer;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactory;
import com.atlassian.confluence.core.BodyContent;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.renderer.RenderContext;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.streams.api.ActivityObjectType;
import com.atlassian.streams.api.Html;
import com.atlassian.streams.api.StreamsEntry;
import com.atlassian.streams.api.common.Function2;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.api.renderer.StreamsEntryRendererFactory;
import com.atlassian.streams.confluence.UriProvider;
import com.atlassian.streams.confluence.changereport.ContentEntityObjects;
import com.atlassian.streams.confluence.renderer.GadgetMacroStripper;
import com.atlassian.streams.spi.renderer.Renderers;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import java.io.Serializable;
import java.net.URI;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ContentEntityRendererFactory {
    private static final Logger log = LoggerFactory.getLogger(ContentEntityRendererFactory.class);
    private final PageManager pageManager;
    private final I18nResolver i18nResolver;
    private final UriProvider uriProvider;
    private final StreamsEntryRendererFactory rendererFactory;
    private final TemplateRenderer templateRenderer;
    private final Renderer xhtmlRenderer;
    private XmlEventReaderFactory xmlEventReaderFactory;
    private XmlOutputFactory xmlOutputFactory;

    public ContentEntityRendererFactory(PageManager pageManager, I18nResolver i18nResolver, UriProvider uriProvider, StreamsEntryRendererFactory rendererFactory, TemplateRenderer templateRenderer, Renderer xhtmlRenderer, XmlEventReaderFactory xmlEventReaderFactory, XmlOutputFactory xmlOutputFactory) {
        this.pageManager = (PageManager)Preconditions.checkNotNull((Object)pageManager, (Object)"pageManager");
        this.i18nResolver = (I18nResolver)Preconditions.checkNotNull((Object)i18nResolver, (Object)"i18nResolver");
        this.uriProvider = (UriProvider)Preconditions.checkNotNull((Object)uriProvider, (Object)"uriProvider");
        this.rendererFactory = (StreamsEntryRendererFactory)Preconditions.checkNotNull((Object)rendererFactory, (Object)"rendererFactory");
        this.templateRenderer = (TemplateRenderer)Preconditions.checkNotNull((Object)templateRenderer, (Object)"templateRenderer");
        this.xhtmlRenderer = (Renderer)Preconditions.checkNotNull((Object)xhtmlRenderer, (Object)"xhtmlRenderer");
        this.xmlEventReaderFactory = (XmlEventReaderFactory)Preconditions.checkNotNull((Object)xmlEventReaderFactory, (Object)"xmlEventReaderFactory");
        this.xmlOutputFactory = (XmlOutputFactory)Preconditions.checkNotNull((Object)xmlOutputFactory, (Object)"xmlOutputFactory");
    }

    public StreamsEntry.Renderer newInstance(URI baseUri, ContentEntityObject entity) {
        return new ContentEntityRenderer(baseUri, entity);
    }

    private final class ContentEntityRenderer
    implements StreamsEntry.Renderer {
        private final Function<StreamsEntry, Html> titleRenderer;
        private final Function2<StreamsEntry, Boolean, Option<Html>> contentRenderer;
        private final URI baseUri;

        ContentEntityRenderer(URI baseUri, ContentEntityObject entity) {
            this.baseUri = baseUri;
            BodyContent bodyContent = entity.getBodyContent();
            this.contentRenderer = this.renderContent(entity, bodyContent);
            this.titleRenderer = this.renderTitle(entity);
        }

        public Option<Html> renderContentAsHtml(StreamsEntry entry) {
            return (Option)this.contentRenderer.apply((Object)entry, (Object)false);
        }

        public Option<Html> renderSummaryAsHtml(StreamsEntry entry) {
            return (Option)this.contentRenderer.apply((Object)entry, (Object)true);
        }

        public Html renderTitleAsHtml(StreamsEntry entry) {
            return (Html)this.titleRenderer.apply((Object)entry);
        }

        private Function<StreamsEntry, Html> renderTitle(ContentEntityObject entity) {
            if (ContentEntityObjects.isComment(entity)) {
                return ContentEntityRendererFactory.this.rendererFactory.newCommentTitleRenderer();
            }
            return entry -> {
                Option objectType = ((StreamsEntry.ActivityObject)Iterables.get((Iterable)entry.getActivityObjects(), (int)0)).getActivityObjectType();
                String key = String.format("streams.item.confluence.%s.%s", objectType.isDefined() ? ((ActivityObjectType)objectType.get()).key() : "unknown", entry.getVerb().key());
                return (Html)ContentEntityRendererFactory.this.rendererFactory.newTitleRenderer(key).apply(entry);
            };
        }

        private Option<String> getPageDiffUri(URI baseUri, ContentEntityObject entity, ContentEntityObject lastVersion) {
            if (lastVersion != null) {
                int revisedVersion = lastVersion.getVersion();
                return Option.some((Object)ContentEntityRendererFactory.this.uriProvider.getPageDiffUri(baseUri, entity, revisedVersion, revisedVersion + 1).toASCIIString());
            }
            return Option.none();
        }

        private Function2<StreamsEntry, Boolean, Option<Html>> renderContent(final ContentEntityObject entity, BodyContent bodyContent) {
            final Supplier<Option<Html>> content = this.contentAsHtml(entity, bodyContent);
            return new Function2<StreamsEntry, Boolean, Option<Html>>(){

                public Option<Html> apply(StreamsEntry entry, Boolean truncate) {
                    return ((Option)content.get()).flatMap(this.renderContent(entry, truncate));
                }

                private Function<Html, Option<Html>> renderContent(StreamsEntry entry, boolean truncate) {
                    return c -> {
                        Html description;
                        Html html = description = truncate ? Renderers.truncate((int)250, (Html)c) : c;
                        if (truncate && c.equals((Object)description)) {
                            return Option.none();
                        }
                        ImmutableMap context = ImmutableMap.builder().put((Object)"contentHtml", (Object)description).put((Object)"truncated", (Object)truncate).put((Object)"isComment", (Object)ContentEntityObjects.isComment(entity)).put((Object)"contentUri", (Object)entry.getAlternateLink()).build();
                        return Option.some((Object)new Html(Renderers.render((TemplateRenderer)ContentEntityRendererFactory.this.templateRenderer, (String)"confluence-content-block.vm", (Map)context)));
                    };
                }
            };
        }

        private Supplier<Option<Html>> contentAsHtml(ContentEntityObject entity, BodyContent bodyContent) {
            return () -> this.content(entity, ContentEntityRendererFactory.this.pageManager.getPreviousVersion(entity), bodyContent).map(Functions.compose((Function)Html.html(), (Function)Renderers.unescapeLineBreaks())).flatMap(Html.trimHtmlToNone());
        }

        private Option<String> content(ContentEntityObject entity, ContentEntityObject lastVersion, BodyContent bodyContent) {
            if (entity.isVersionCommentAvailable()) {
                return Option.some((Object)this.blockquote(StringEscapeUtils.escapeHtml4((String)entity.getVersionComment())));
            }
            if (lastVersion != null && entity instanceof AbstractPage) {
                boolean renamed = !entity.getTitle().equals(lastVersion.getTitle());
                return this.getEditedPageContent(entity, lastVersion, renamed);
            }
            PageContext renderContext = entity.toPageContext();
            renderContext.setOutputType("feed");
            if (BodyType.XHTML.equals((Object)bodyContent.getBodyType())) {
                String xml = this.stripGadgetMacros(entity, entity.getBodyAsString());
                return Option.some((Object)Renderers.replaceNbsp((String)ContentEntityRendererFactory.this.xhtmlRenderer.render(xml, (ConversionContext)new DefaultConversionContext((RenderContext)renderContext))));
            }
            return Option.none();
        }

        private String blockquote(String content) {
            return content != null ? "<blockquote>" + content + "</blockquote>" : null;
        }

        private String stripGadgetMacros(ContentEntityObject entity, String content) {
            if (StringUtils.isBlank((CharSequence)content)) {
                return content;
            }
            try {
                GadgetMacroStripper stripper = new GadgetMacroStripper(ContentEntityRendererFactory.this.xmlEventReaderFactory, ContentEntityRendererFactory.this.xmlOutputFactory);
                return stripper.stripGadgetMacros(content);
            }
            catch (Exception e) {
                log.warn("Unable to parse content for entity '" + entity.getDisplayTitle() + "' at " + entity.getUrlPath(), (Throwable)e);
                return content;
            }
        }

        private Option<String> getEditedPageContent(ContentEntityObject entity, ContentEntityObject lastVersion, boolean renamed) {
            Option renamedText = renamed ? Option.some((Object)ContentEntityRendererFactory.this.i18nResolver.getText("stream.item.confluence.action.page.renamed", new Serializable[]{lastVersion.getTitle(), entity.getTitle()})) : Option.none(String.class);
            return Option.some((Object)Renderers.render((TemplateRenderer)ContentEntityRendererFactory.this.templateRenderer, (String)"confluence-updated-page.vm", (Map)ImmutableMap.of((Object)"pageDiffUri", this.getPageDiffUri(this.baseUri, entity, lastVersion), (Object)"renamedText", (Object)renamedText)));
        }
    }
}

