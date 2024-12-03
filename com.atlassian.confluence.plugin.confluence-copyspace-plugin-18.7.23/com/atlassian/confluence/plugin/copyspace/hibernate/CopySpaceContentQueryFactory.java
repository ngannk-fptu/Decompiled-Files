/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.ContentQuery
 */
package com.atlassian.confluence.plugin.copyspace.hibernate;

import com.atlassian.confluence.content.ContentQuery;

public class CopySpaceContentQueryFactory {
    public static ContentQuery<Integer> findTotalPagesCountBySpace(String spaceKey) {
        return new ContentQuery("copyspace.space.findPagesCount", new Object[]{spaceKey});
    }

    public static ContentQuery<Integer> findTotalBlogPostsCountBySpace(String spaceKey) {
        return new ContentQuery("copyspace.space.findBlogPostsCount", new Object[]{spaceKey});
    }

    public static ContentQuery<Integer> findTotalCommentsCountBySpace(String spaceKey) {
        return new ContentQuery("copyspace.space.findCommentsCount", new Object[]{spaceKey});
    }

    public static ContentQuery<Integer> findTotalAttachmentsCountBySpace(String spaceKey) {
        return new ContentQuery("copyspace.space.findAttachmentsCount", new Object[]{spaceKey});
    }
}

