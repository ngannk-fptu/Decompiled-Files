/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.streams.api.renderer;

import com.atlassian.streams.api.Html;
import com.atlassian.streams.api.StreamsEntry;
import com.atlassian.streams.api.UserProfile;
import com.atlassian.streams.api.common.Option;
import com.google.common.base.Function;
import java.net.URI;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface StreamsEntryRendererFactory {
    public StreamsEntry.Renderer newCommentRenderer(String var1);

    public StreamsEntry.Renderer newCommentRenderer(Html var1);

    @Deprecated
    public StreamsEntry.Renderer newCommentRenderer(Function<StreamsEntry, Html> var1, String var2);

    default public StreamsEntry.Renderer newCommentRenderer(java.util.function.Function<StreamsEntry, Html> titleRenderer, String message) {
        throw new UnsupportedOperationException("Please override this method");
    }

    @Deprecated
    public StreamsEntry.Renderer newCommentRenderer(Function<StreamsEntry, Html> var1, Html var2);

    default public StreamsEntry.Renderer newCommentRenderer(java.util.function.Function<StreamsEntry, Html> titleRenderer, Html message) {
        throw new UnsupportedOperationException("Please override this method");
    }

    @Deprecated
    public StreamsEntry.Renderer newCommentRenderer(Function<StreamsEntry, Html> var1, Html var2, Option<URI> var3);

    default public StreamsEntry.Renderer newCommentRenderer(java.util.function.Function<StreamsEntry, Html> titleRenderer, Html apply, @Nonnull URI styleLink) {
        throw new UnsupportedOperationException("Please override this method");
    }

    @Deprecated
    public Function<StreamsEntry, Html> newCommentTitleRenderer();

    default public java.util.function.Function<StreamsEntry, Html> newCommentTitleRendererFunc() {
        throw new UnsupportedOperationException("Please override this method");
    }

    @Deprecated
    public Function<StreamsEntry, Html> newTitleRenderer(String var1);

    default public java.util.function.Function<StreamsEntry, Html> newTitleRendererFunc(String key) {
        throw new UnsupportedOperationException("Please override this method");
    }

    @Deprecated
    public Function<StreamsEntry, Html> newTitleRenderer(String var1, Function<Iterable<UserProfile>, Html> var2, Option<Function<Iterable<StreamsEntry.ActivityObject>, Option<Html>>> var3, Option<Function<StreamsEntry.ActivityObject, Option<Html>>> var4);

    default public java.util.function.Function<StreamsEntry, Html> newTitleRendererFunc(String key, java.util.function.Function<Iterable<UserProfile>, Html> authorsRenderer, @Nullable java.util.function.Function<Iterable<StreamsEntry.ActivityObject>, Option<Html>> activityObjectRenderer, @Nullable java.util.function.Function<StreamsEntry.ActivityObject, Option<Html>> targetRenderer) {
        throw new UnsupportedOperationException("Please override this method");
    }

    @Deprecated
    public Function<Iterable<UserProfile>, Html> newAuthorsRenderer();

    default public java.util.function.Function<Iterable<UserProfile>, Html> newAuthorsRendererFunc() {
        throw new UnsupportedOperationException("Please override this method");
    }

    @Deprecated
    public Function<Iterable<UserProfile>, Html> newUserProfileRenderer();

    default public java.util.function.Function<Iterable<UserProfile>, Html> newUserProfileRendererFunc() {
        throw new UnsupportedOperationException("Please override this method");
    }

    @Deprecated
    public Function<Iterable<StreamsEntry.ActivityObject>, Option<Html>> newActivityObjectsRenderer();

    default public java.util.function.Function<Iterable<StreamsEntry.ActivityObject>, Option<Html>> newActivityObjectsRendererFunc() {
        throw new UnsupportedOperationException("Please override this method");
    }

    @Deprecated
    public Function<Iterable<StreamsEntry.ActivityObject>, Option<Html>> newActivityObjectsRenderer(Function<StreamsEntry.ActivityObject, Option<Html>> var1);

    default public java.util.function.Function<Iterable<StreamsEntry.ActivityObject>, Option<Html>> newActivityObjectsRendererFunc(java.util.function.Function<StreamsEntry.ActivityObject, Option<Html>> objectRenderer) {
        throw new UnsupportedOperationException("Please override this method");
    }

    @Deprecated
    public Function<StreamsEntry.ActivityObject, Option<Html>> newActivityObjectRendererWithSummary();

    default public java.util.function.Function<StreamsEntry.ActivityObject, Option<Html>> newActivityObjectRendererWithSummaryFunc() {
        throw new UnsupportedOperationException("Please override this method");
    }

    @Deprecated
    public Function<StreamsEntry.ActivityObject, Option<Html>> newActivityObjectRendererWithoutSummary();

    default public java.util.function.Function<StreamsEntry.ActivityObject, Option<Html>> newActivityObjectRendererWithoutSummaryFunc() {
        throw new UnsupportedOperationException("Please override this method");
    }

    @Deprecated
    public <T> Function<Iterable<T>, Option<Html>> newCompoundStatementRenderer(Function<T, Option<Html>> var1);

    default public <T> java.util.function.Function<Iterable<T>, Option<Html>> newCompoundStatementRendererFunc(java.util.function.Function<T, Option<Html>> render) {
        throw new UnsupportedOperationException("Please override this method");
    }
}

