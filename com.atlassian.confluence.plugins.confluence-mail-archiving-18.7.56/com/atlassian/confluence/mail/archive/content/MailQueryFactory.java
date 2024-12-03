/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.ContentQuery
 *  com.atlassian.confluence.content.CustomContentEntityObject
 */
package com.atlassian.confluence.mail.archive.content;

import com.atlassian.confluence.content.ContentQuery;
import com.atlassian.confluence.content.CustomContentEntityObject;

public class MailQueryFactory {
    public static ContentQuery<CustomContentEntityObject> findNextInSpaceById(long spaceId, long contentId) {
        return new ContentQuery("mail.findNextInSpace", new Object[]{spaceId, contentId});
    }

    public static ContentQuery<CustomContentEntityObject> findPreviousInSpaceById(long spaceId, long contentId) {
        return new ContentQuery("mail.findPreviousInSpace", new Object[]{spaceId, contentId});
    }

    public static ContentQuery<CustomContentEntityObject> findInSpaceByMessageId(long spaceId, String messageId) {
        return new ContentQuery("mail.findInSpaceByMessageId", new Object[]{spaceId, messageId});
    }
}

