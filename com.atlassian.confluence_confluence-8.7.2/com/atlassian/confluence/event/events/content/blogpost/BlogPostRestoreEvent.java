/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.content.blogpost;

import com.atlassian.confluence.event.events.content.blogpost.BlogPostEvent;
import com.atlassian.confluence.event.events.types.Restore;
import com.atlassian.confluence.pages.BlogPost;

public class BlogPostRestoreEvent
extends BlogPostEvent
implements Restore {
    private static final long serialVersionUID = 8365042474782586711L;

    public BlogPostRestoreEvent(Object src, BlogPost blogPost) {
        super(src, blogPost, false);
    }
}

