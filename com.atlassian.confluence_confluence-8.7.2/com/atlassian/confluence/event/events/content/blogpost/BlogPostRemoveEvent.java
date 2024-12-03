/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.content.blogpost;

import com.atlassian.confluence.event.events.content.blogpost.BlogPostEvent;
import com.atlassian.confluence.event.events.types.Removed;
import com.atlassian.confluence.pages.BlogPost;

public class BlogPostRemoveEvent
extends BlogPostEvent
implements Removed {
    private static final long serialVersionUID = -6350767999503459746L;

    public BlogPostRemoveEvent(Object src, BlogPost blogPost) {
        super(src, blogPost, false);
    }
}

