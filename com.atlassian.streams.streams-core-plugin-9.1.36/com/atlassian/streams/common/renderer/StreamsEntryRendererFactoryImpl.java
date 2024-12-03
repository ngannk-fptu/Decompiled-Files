/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.streams.api.Html
 *  com.atlassian.streams.api.StreamsEntry
 *  com.atlassian.streams.api.StreamsEntry$ActivityObject
 *  com.atlassian.streams.api.StreamsEntry$Renderer
 *  com.atlassian.streams.api.UserProfile
 *  com.atlassian.streams.api.common.Option
 *  com.atlassian.streams.api.renderer.StreamsEntryRendererFactory
 *  com.atlassian.streams.spi.StreamsI18nResolver
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.atlassian.streams.common.renderer;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.streams.api.Html;
import com.atlassian.streams.api.StreamsEntry;
import com.atlassian.streams.api.UserProfile;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.api.renderer.StreamsEntryRendererFactory;
import com.atlassian.streams.common.renderer.ActivityObjectRenderer;
import com.atlassian.streams.common.renderer.AuthorsRenderer;
import com.atlassian.streams.common.renderer.CommentRenderer;
import com.atlassian.streams.common.renderer.CompoundStatementRenderer;
import com.atlassian.streams.common.renderer.TitleRenderer;
import com.atlassian.streams.spi.StreamsI18nResolver;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import java.net.URI;
import javax.annotation.Nullable;

public class StreamsEntryRendererFactoryImpl
implements StreamsEntryRendererFactory {
    private final I18nResolver i18nResolver;
    private final TemplateRenderer templateRenderer;

    public StreamsEntryRendererFactoryImpl(StreamsI18nResolver i18nResolver, TemplateRenderer templateRenderer) {
        this.i18nResolver = i18nResolver;
        this.templateRenderer = (TemplateRenderer)Preconditions.checkNotNull((Object)templateRenderer, (Object)"templateRenderer");
    }

    public StreamsEntry.Renderer newCommentRenderer(String comment) {
        return this.newCommentRenderer(this.newCommentTitleRenderer(), comment);
    }

    public StreamsEntry.Renderer newCommentRenderer(Html comment) {
        return this.newCommentRenderer(this.newCommentTitleRenderer(), comment);
    }

    public StreamsEntry.Renderer newCommentRenderer(Function<StreamsEntry, Html> titleRenderer, String comment) {
        return new CommentRenderer(this.templateRenderer, titleRenderer, comment);
    }

    public StreamsEntry.Renderer newCommentRenderer(java.util.function.Function<StreamsEntry, Html> titleRenderer, String message) {
        return new CommentRenderer(this.templateRenderer, titleRenderer, message);
    }

    public StreamsEntry.Renderer newCommentRenderer(Function<StreamsEntry, Html> titleRenderer, Html comment) {
        return this.newCommentRenderer(titleRenderer, comment, (Option<URI>)Option.none(URI.class));
    }

    public StreamsEntry.Renderer newCommentRenderer(java.util.function.Function<StreamsEntry, Html> titleRenderer, Html message) {
        return this.newCommentRenderer(titleRenderer, message, null);
    }

    public StreamsEntry.Renderer newCommentRenderer(Function<StreamsEntry, Html> titleRenderer, Html comment, Option<URI> styleLink) {
        return new CommentRenderer(this.templateRenderer, titleRenderer, comment, styleLink);
    }

    public StreamsEntry.Renderer newCommentRenderer(java.util.function.Function<StreamsEntry, Html> titleRenderer, Html apply, @Nullable URI styleLink) {
        return new CommentRenderer(this.templateRenderer, titleRenderer, apply, (Option<URI>)Option.option((Object)styleLink));
    }

    public Function<StreamsEntry, Html> newTitleRenderer(String key) {
        return this.newTitleRenderer(key, this.newAuthorsRenderer(), (Option<Function<Iterable<StreamsEntry.ActivityObject>, Option<Html>>>)Option.some(this.newActivityObjectsRenderer()), (Option<Function<StreamsEntry.ActivityObject, Option<Html>>>)Option.some(this.newActivityObjectRendererWithSummary()));
    }

    public java.util.function.Function<StreamsEntry, Html> newTitleRendererFunc(String key) {
        return this.newTitleRenderer(key, this.newAuthorsRenderer(), (Option<Function<Iterable<StreamsEntry.ActivityObject>, Option<Html>>>)Option.some(this.newActivityObjectsRenderer()), (Option<Function<StreamsEntry.ActivityObject, Option<Html>>>)Option.some(this.newActivityObjectRendererWithSummary()));
    }

    public Function<StreamsEntry, Html> newTitleRenderer(String key, Function<Iterable<UserProfile>, Html> authorsRenderer, Option<Function<Iterable<StreamsEntry.ActivityObject>, Option<Html>>> activityObjectRenderer, Option<Function<StreamsEntry.ActivityObject, Option<Html>>> targetRenderer) {
        return new TitleRenderer(this.i18nResolver, key, authorsRenderer, activityObjectRenderer, targetRenderer);
    }

    public java.util.function.Function<StreamsEntry, Html> newTitleRendererFunc(String key, java.util.function.Function<Iterable<UserProfile>, Html> authorsRenderer, @Nullable java.util.function.Function<Iterable<StreamsEntry.ActivityObject>, Option<Html>> activityObjectRenderer, @Nullable java.util.function.Function<StreamsEntry.ActivityObject, Option<Html>> targetRenderer) {
        return new TitleRenderer(this.i18nResolver, key, authorsRenderer, activityObjectRenderer, targetRenderer);
    }

    public Function<Iterable<StreamsEntry.ActivityObject>, Option<Html>> newActivityObjectsRenderer() {
        return this.newActivityObjectsRenderer(this.newActivityObjectRendererWithSummary());
    }

    public java.util.function.Function<Iterable<StreamsEntry.ActivityObject>, Option<Html>> newActivityObjectsRendererFunc() {
        return this.newActivityObjectsRenderer(this.newActivityObjectRendererWithSummary());
    }

    public Function<Iterable<StreamsEntry.ActivityObject>, Option<Html>> newActivityObjectsRenderer(Function<StreamsEntry.ActivityObject, Option<Html>> objectRenderer) {
        return new CompoundStatementRenderer<StreamsEntry.ActivityObject>(this.i18nResolver, objectRenderer);
    }

    public java.util.function.Function<Iterable<StreamsEntry.ActivityObject>, Option<Html>> newActivityObjectsRendererFunc(java.util.function.Function<StreamsEntry.ActivityObject, Option<Html>> objectRenderer) {
        return new CompoundStatementRenderer<StreamsEntry.ActivityObject>(this.i18nResolver, objectRenderer);
    }

    public Function<Iterable<UserProfile>, Html> newAuthorsRenderer() {
        return new AuthorsRenderer(this.i18nResolver, this.templateRenderer, true);
    }

    public java.util.function.Function<Iterable<UserProfile>, Html> newAuthorsRendererFunc() {
        return new AuthorsRenderer(this.i18nResolver, this.templateRenderer, true);
    }

    public Function<Iterable<UserProfile>, Html> newUserProfileRenderer() {
        return new AuthorsRenderer(this.i18nResolver, this.templateRenderer, false);
    }

    public java.util.function.Function<Iterable<UserProfile>, Html> newUserProfileRendererFunc() {
        return new AuthorsRenderer(this.i18nResolver, this.templateRenderer, false);
    }

    public Function<StreamsEntry.ActivityObject, Option<Html>> newActivityObjectRendererWithSummary() {
        return new ActivityObjectRenderer(this.templateRenderer, true);
    }

    public java.util.function.Function<StreamsEntry.ActivityObject, Option<Html>> newActivityObjectRendererWithSummaryFunc() {
        return new ActivityObjectRenderer(this.templateRenderer, true);
    }

    public Function<StreamsEntry.ActivityObject, Option<Html>> newActivityObjectRendererWithoutSummary() {
        return new ActivityObjectRenderer(this.templateRenderer, false);
    }

    public java.util.function.Function<StreamsEntry.ActivityObject, Option<Html>> newActivityObjectRendererWithoutSummaryFunc() {
        return new ActivityObjectRenderer(this.templateRenderer, false);
    }

    public <T> Function<Iterable<T>, Option<Html>> newCompoundStatementRenderer(Function<T, Option<Html>> render) {
        return new CompoundStatementRenderer<T>(this.i18nResolver, render);
    }

    public <T> java.util.function.Function<Iterable<T>, Option<Html>> newCompoundStatementRendererFunc(java.util.function.Function<T, Option<Html>> render) {
        return new CompoundStatementRenderer<T>(this.i18nResolver, render);
    }

    public Function<StreamsEntry, Html> newCommentTitleRenderer() {
        return CommentRenderer.standardTitleRenderer(this);
    }

    public java.util.function.Function<StreamsEntry, Html> newCommentTitleRendererFunc() {
        return CommentRenderer.standardTitleRenderer(this);
    }
}

