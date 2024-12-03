/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.streams.api.Html
 *  com.atlassian.streams.api.StreamsEntry
 *  com.atlassian.streams.api.StreamsEntry$ActivityObject
 *  com.atlassian.streams.api.UserProfile
 *  com.atlassian.streams.api.common.Functions
 *  com.atlassian.streams.api.common.Option
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  javax.annotation.Nullable
 */
package com.atlassian.streams.common.renderer;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.streams.api.Html;
import com.atlassian.streams.api.StreamsEntry;
import com.atlassian.streams.api.UserProfile;
import com.atlassian.streams.api.common.Functions;
import com.atlassian.streams.api.common.Option;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.io.Serializable;
import javax.annotation.Nullable;

final class TitleRenderer
implements Function<StreamsEntry, Html> {
    private final I18nResolver i18nResolver;
    private final String key;
    private final java.util.function.Function<Iterable<UserProfile>, Html> authorsRenderer;
    private final java.util.function.Function<Iterable<StreamsEntry.ActivityObject>, Option<Html>> activityObjectRenderer;
    private final java.util.function.Function<StreamsEntry.ActivityObject, Option<Html>> targetRenderer;

    @Deprecated
    TitleRenderer(I18nResolver i18nResolver, String key, Function<Iterable<UserProfile>, Html> authorsRenderer, Option<Function<Iterable<StreamsEntry.ActivityObject>, Option<Html>>> activityObjectRenderer, Option<Function<StreamsEntry.ActivityObject, Option<Html>>> targetRenderer) {
        this.i18nResolver = i18nResolver;
        this.key = key;
        this.authorsRenderer = authorsRenderer;
        this.activityObjectRenderer = activityObjectRenderer.isDefined() ? (java.util.function.Function)activityObjectRenderer.get() : null;
        this.targetRenderer = targetRenderer.isDefined() ? (java.util.function.Function)targetRenderer.get() : null;
    }

    TitleRenderer(I18nResolver i18nResolver, String key, java.util.function.Function<Iterable<UserProfile>, Html> authorsRenderer, @Nullable java.util.function.Function<Iterable<StreamsEntry.ActivityObject>, Option<Html>> activityObjectRenderer, @Nullable java.util.function.Function<StreamsEntry.ActivityObject, Option<Html>> targetRenderer) {
        this.i18nResolver = i18nResolver;
        this.key = key;
        this.authorsRenderer = authorsRenderer;
        this.activityObjectRenderer = activityObjectRenderer;
        this.targetRenderer = targetRenderer;
    }

    public Html apply(StreamsEntry entry) {
        Option<Html> objectHtml = this.activityObjectRenderer != null ? this.activityObjectRenderer.apply(entry.getActivityObjects()) : Option.none();
        Option targetHtml = this.targetRenderer != null ? entry.getTarget().flatMap(this.targetRenderer) : Option.none();
        return new Html(this.getText(this.key, Iterables.concat((Iterable)ImmutableList.of((Object)this.authorsRenderer.apply((Iterable<UserProfile>)entry.getAuthors())), (Iterable)((Iterable)objectHtml.map(Functions.singletonList(Html.class)).getOrElse((Object)ImmutableList.of())), (Iterable)((Iterable)targetHtml.map(Functions.singletonList(Html.class)).getOrElse((Object)ImmutableList.of())))));
    }

    private String getText(String key, Iterable<Html> args) {
        return this.i18nResolver.getText(key, (Serializable[])Iterables.toArray(args, Serializable.class));
    }
}

