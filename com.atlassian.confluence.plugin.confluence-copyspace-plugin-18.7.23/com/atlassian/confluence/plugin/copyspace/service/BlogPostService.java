/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.copyspace.service;

import com.atlassian.confluence.plugin.copyspace.context.CopySpaceContext;

public interface BlogPostService {
    public void copyBlogPosts(CopySpaceContext var1);

    public void copyWholeBlogWatchers(CopySpaceContext var1);
}

