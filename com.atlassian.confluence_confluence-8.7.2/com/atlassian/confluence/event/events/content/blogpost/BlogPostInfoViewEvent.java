/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.content.blogpost;

import com.atlassian.confluence.event.events.content.blogpost.BlogPostViewEvent;
import com.atlassian.confluence.languages.LocaleInfo;
import com.atlassian.confluence.pages.BlogPost;
import org.checkerframework.checker.nullness.qual.Nullable;

public class BlogPostInfoViewEvent
extends BlogPostViewEvent {
    private static final long serialVersionUID = 5238639491970438874L;

    @Deprecated
    public BlogPostInfoViewEvent(Object source, BlogPost blogPost) {
        this(source, blogPost, (LocaleInfo)null);
    }

    public BlogPostInfoViewEvent(Object source, BlogPost blogPost, @Nullable LocaleInfo localeInfo) {
        super(source, blogPost, localeInfo);
    }
}

