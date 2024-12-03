/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.service.blogpost;

import com.atlassian.confluence.core.service.SingleEntityLocator;
import com.atlassian.confluence.pages.BlogPost;

public interface BlogPostLocator
extends SingleEntityLocator {
    public BlogPost getBlogPost();
}

