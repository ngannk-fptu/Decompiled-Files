/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.Html
 *  com.atlassian.streams.api.StreamsEntry
 *  com.atlassian.streams.api.StreamsEntry$Renderer
 *  com.atlassian.streams.api.common.Function2
 *  com.atlassian.streams.api.common.Functions
 *  com.atlassian.streams.api.common.Option
 *  com.atlassian.streams.api.renderer.StreamsEntryRendererFactory
 *  com.atlassian.streams.spi.renderer.Renderers
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.streams.common.renderer;

import com.atlassian.streams.api.Html;
import com.atlassian.streams.api.StreamsEntry;
import com.atlassian.streams.api.common.Function2;
import com.atlassian.streams.api.common.Functions;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.api.renderer.StreamsEntryRendererFactory;
import com.atlassian.streams.spi.renderer.Renderers;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.net.URI;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

final class CommentRenderer
implements StreamsEntry.Renderer {
    private final TemplateRenderer templateRenderer;
    private final java.util.function.Function<StreamsEntry, Html> titleRenderer;
    private final Function2<StreamsEntry, Boolean, Option<Html>> commentRenderer;
    private final Option<URI> styleLink;
    private final Function<Html, Function2<StreamsEntry, Boolean, Option<Html>>> renderHtml = new Function<Html, Function2<StreamsEntry, Boolean, Option<Html>>>(){

        public Function2<StreamsEntry, Boolean, Option<Html>> apply(final Html h) {
            return new Function2<StreamsEntry, Boolean, Option<Html>>(){

                public Option<Html> apply(StreamsEntry entry, Boolean truncate) {
                    Html comment;
                    Html html = comment = truncate != false ? Renderers.truncate((int)250, (Html)h) : h;
                    if (truncate.booleanValue() && h.equals((Object)comment) || StringUtils.isBlank((CharSequence)h.toString())) {
                        return Option.none();
                    }
                    ImmutableMap context = ImmutableMap.builder().put((Object)"commentHtml", (Object)comment).put((Object)"truncated", (Object)truncate).put((Object)"commentUri", (Object)entry.getAlternateLink()).put((Object)"styleLink", (Object)CommentRenderer.this.styleLink).build();
                    return Option.some((Object)new Html(Renderers.render((TemplateRenderer)CommentRenderer.this.templateRenderer, (String)"comment-block.vm", (Map)context)));
                }
            };
        }
    };

    @Deprecated
    public CommentRenderer(TemplateRenderer templateRenderer, Function<StreamsEntry, Html> titleRenderer, String comment) {
        this(templateRenderer, titleRenderer, (Option<String>)Option.option((Object)comment), (Option<Html>)Option.none(Html.class), (Option<URI>)Option.none(URI.class));
    }

    public CommentRenderer(TemplateRenderer templateRenderer, java.util.function.Function<StreamsEntry, Html> titleRenderer, String comment) {
        this(templateRenderer, titleRenderer, (Option<String>)Option.option((Object)comment), (Option<Html>)Option.none(Html.class), (Option<URI>)Option.none(URI.class));
    }

    @Deprecated
    public CommentRenderer(TemplateRenderer templateRenderer, Function<StreamsEntry, Html> titleRenderer, Html comment, Option<URI> styleLink) {
        this(templateRenderer, titleRenderer, (Option<String>)Option.none(String.class), (Option<Html>)Option.some((Object)comment), styleLink);
    }

    public CommentRenderer(TemplateRenderer templateRenderer, java.util.function.Function<StreamsEntry, Html> titleRenderer, Html comment, Option<URI> styleLink) {
        this(templateRenderer, titleRenderer, (Option<String>)Option.none(String.class), (Option<Html>)Option.some((Object)comment), styleLink);
    }

    @Deprecated
    public CommentRenderer(TemplateRenderer templateRenderer, Function<StreamsEntry, Html> titleRenderer, Option<String> wikiComment, Option<Html> htmlComment, Option<URI> styleLink) {
        this.templateRenderer = (TemplateRenderer)Preconditions.checkNotNull((Object)templateRenderer, (Object)"templateRenderer");
        this.titleRenderer = (java.util.function.Function)Preconditions.checkNotNull(titleRenderer, (Object)"titleRenderer");
        this.commentRenderer = (Function2)htmlComment.map(this.renderHtml).getOrElse(this.renderWiki(wikiComment));
        this.styleLink = (Option)Preconditions.checkNotNull(styleLink, (Object)"styleLink");
    }

    public CommentRenderer(TemplateRenderer templateRenderer, java.util.function.Function<StreamsEntry, Html> titleRenderer, Option<String> wikiComment, Option<Html> htmlComment, Option<URI> styleLink) {
        this.templateRenderer = (TemplateRenderer)Preconditions.checkNotNull((Object)templateRenderer, (Object)"templateRenderer");
        this.titleRenderer = (java.util.function.Function)Preconditions.checkNotNull(titleRenderer, (Object)"titleRenderer");
        this.commentRenderer = (Function2)htmlComment.map(this.renderHtml).getOrElse(this.renderWiki(wikiComment));
        this.styleLink = (Option)Preconditions.checkNotNull(styleLink, (Object)"styleLink");
    }

    public Option<Html> renderContentAsHtml(StreamsEntry entry) {
        return (Option)this.commentRenderer.apply((Object)entry, (Object)false);
    }

    public Option<Html> renderSummaryAsHtml(StreamsEntry entry) {
        return (Option)this.commentRenderer.apply((Object)entry, (Object)true);
    }

    private Function2<StreamsEntry, Boolean, Option<Html>> renderWiki(Option<String> comment) {
        final Option strippedComment = comment.map(Renderers.stripBasicMarkup()).flatMap(Functions.trimToNone());
        return new Function2<StreamsEntry, Boolean, Option<Html>>(){

            public Option<Html> apply(StreamsEntry entry, Boolean truncate) {
                return strippedComment.flatMap(this.renderF(entry, truncate));
            }

            private Function<String, Option<Html>> renderF(final StreamsEntry entry, final Boolean truncate) {
                return new Function<String, Option<Html>>(){

                    public Option<Html> apply(String s) {
                        String comment;
                        String string = comment = truncate != false ? Renderers.getExcerptUsingLimit((String)s, (int)250) : s;
                        if (truncate.booleanValue() && s.equals(comment)) {
                            return Option.none();
                        }
                        ImmutableMap context = ImmutableMap.builder().put((Object)"comment", (Object)Renderers.replaceNbsp((String)comment)).put((Object)"truncated", (Object)truncate).put((Object)"commentUri", (Object)entry.getAlternateLink()).build();
                        return Option.some((Object)new Html(Renderers.render((TemplateRenderer)CommentRenderer.this.templateRenderer, (String)"comment-block.vm", (Map)context)));
                    }
                };
            }
        };
    }

    public Html renderTitleAsHtml(StreamsEntry entry) {
        return this.titleRenderer.apply(entry);
    }

    static Function<StreamsEntry, Html> standardTitleRenderer(StreamsEntryRendererFactory rendererFactory) {
        return new StandardTitleRenderer(rendererFactory);
    }

    static final class StandardTitleRenderer
    implements Function<StreamsEntry, Html> {
        private final StreamsEntryRendererFactory rendererFactory;

        public StandardTitleRenderer(StreamsEntryRendererFactory rendererFactory) {
            this.rendererFactory = rendererFactory;
        }

        public Html apply(StreamsEntry entry) {
            String key = entry.getTarget().isDefined() ? "streams.title.commented.on" : "streams.title.commented";
            return (Html)this.rendererFactory.newTitleRenderer(key).apply((Object)entry);
        }
    }
}

