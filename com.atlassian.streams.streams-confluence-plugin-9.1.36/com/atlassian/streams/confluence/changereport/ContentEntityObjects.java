/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.Page
 */
package com.atlassian.streams.confluence.changereport;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Page;

public class ContentEntityObjects {
    private static final String BLOGPOST_TYPE = "blogpost";
    private static final String MAIL_TYPE = "mail";

    public static boolean isMail(ContentEntityObject entity) {
        return entity.getType().startsWith(MAIL_TYPE);
    }

    public static boolean isBlogPost(ContentEntityObject entity) {
        return entity.getType().startsWith(BLOGPOST_TYPE);
    }

    public static boolean isPage(ContentEntityObject entity) {
        return entity instanceof Page;
    }

    public static boolean isComment(ContentEntityObject entity) {
        return entity instanceof Comment;
    }
}

