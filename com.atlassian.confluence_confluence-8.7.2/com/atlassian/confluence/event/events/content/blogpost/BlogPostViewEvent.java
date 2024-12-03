/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.content.blogpost;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostEvent;
import com.atlassian.confluence.event.events.types.Viewed;
import com.atlassian.confluence.languages.LocaleInfo;
import com.atlassian.confluence.pages.BlogPost;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@EventName(value="confluence.blogpost.view")
public class BlogPostViewEvent
extends BlogPostEvent
implements Viewed {
    private static final long serialVersionUID = 1169387105285880926L;
    private final LocaleInfo localeInfo;

    @Deprecated
    public BlogPostViewEvent(Object source, BlogPost blogPost) {
        this(source, blogPost, (LocaleInfo)null);
    }

    public BlogPostViewEvent(Object source, BlogPost blogPost, @Nullable LocaleInfo localeInfo) {
        super(source, blogPost, false);
        this.localeInfo = localeInfo;
    }

    @Override
    public @Nullable LocaleInfo getLocaleInfo() {
        return this.localeInfo;
    }

    @Override
    public @NonNull Map<String, Object> getProperties() {
        return Viewed.super.getProperties();
    }
}

