/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.ContentQuery
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.spaces.Space
 */
package com.atlassian.confluence.plugins.mobile.hibernate;

import com.atlassian.confluence.content.ContentQuery;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.spaces.Space;
import java.util.List;

public class MobileContentQueryFactory {
    public static ContentQuery<Comment> findAttachmentCommentByContentId(List<Long> attachmentIds) {
        return new ContentQuery("mobile.attachmentComment.findByContent", new Object[]{attachmentIds});
    }

    public static ContentQuery<Space> findFavouriteSpacesByUserName(String userName) {
        return new ContentQuery("mobile.favouriteSpaces.findByUserName", new Object[]{userName});
    }

    public static ContentQuery<Page> findChildrenPageByParentPageId(long parentPageId) {
        return new ContentQuery("mobile.page.findChildrenPage", new Object[]{parentPageId});
    }

    public static ContentQuery<Long> findPageHasChildren(List<Long> parentPageIds) {
        return new ContentQuery("mobile.page.findPageHasChildren", new Object[]{parentPageIds});
    }
}

