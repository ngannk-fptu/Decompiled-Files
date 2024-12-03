/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.people.KnownUser
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.confluence.api.model.people.User
 */
package com.atlassian.confluence.plugins.inlinecomments.utils;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.people.KnownUser;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.api.model.people.User;

public class InlineCommentUtils {
    private static final String EMPTY_VALUE = "";

    public static ContentId buildContentId(long contentId) {
        return ContentId.deserialise((String)String.valueOf(contentId));
    }

    public static ContentId buildContentId(ContentType contentType, long contentId) {
        return ContentId.of((ContentType)contentType, (long)contentId);
    }

    public static Content buildContentProxy(long contentId) {
        return Content.builder().id(ContentId.deserialise((String)String.valueOf(contentId))).build();
    }

    public static Content buildContentProxy(ContentType contentType, long contentId) {
        return Content.builder().id(ContentId.of((ContentType)contentType, (long)contentId)).build();
    }

    public static String getDisplayName(Person person) {
        return person instanceof KnownUser ? ((KnownUser)person).getDisplayName() : EMPTY_VALUE;
    }

    public static String getUserName(Person person) {
        if (person instanceof User) {
            if (person.getUserKey().isEmpty()) {
                return EMPTY_VALUE;
            }
            return ((User)person).getUsername();
        }
        return EMPTY_VALUE;
    }

    public static String getUserAvatarUrl(Person person) {
        return person.getProfilePicture().getPath();
    }

    public static String getCommentDateUrl(String pageUrl, Long commentId) {
        return pageUrl + "focusedCommentId=" + commentId + "#comment-" + commentId;
    }
}

