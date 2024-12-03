/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.service.blogpost;

import com.atlassian.confluence.core.service.ServiceCommand;
import com.atlassian.confluence.pages.BlogPost;

public interface CreateBlogPostCommand
extends ServiceCommand {
    public BlogPost getCreatedBlogPost();
}

