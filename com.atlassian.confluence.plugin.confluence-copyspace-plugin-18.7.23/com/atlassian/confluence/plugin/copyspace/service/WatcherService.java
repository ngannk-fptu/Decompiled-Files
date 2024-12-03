/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.spaces.Space
 */
package com.atlassian.confluence.plugin.copyspace.service;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.spaces.Space;

public interface WatcherService {
    public void copyPageWatchers(ContentEntityObject var1, Page var2);

    public void copyBlogPostWatchers(BlogPost var1, BlogPost var2);

    public void copyWholeBlogWatchers(Space var1, Space var2);

    public void copySpaceWatchers(Space var1, String var2);
}

