/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.notifications.PayloadTransformerTemplate
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.plugins.mentions.notifications;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.notifications.PayloadTransformerTemplate;
import com.atlassian.confluence.plugins.mentions.api.ConfluenceMentionEvent;
import com.atlassian.confluence.plugins.mentions.notifications.MentionContentPayload;
import com.atlassian.confluence.plugins.mentions.notifications.SimpleMentionContentPayload;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.sal.api.user.UserKey;

public class NewContentPayloadTransformer
extends PayloadTransformerTemplate<ConfluenceMentionEvent, MentionContentPayload> {
    private final UserAccessor userAccessor;

    public NewContentPayloadTransformer(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    protected Maybe<MentionContentPayload> checkedCreate(ConfluenceMentionEvent event) {
        ContentEntityObject content = event.getContent();
        ConfluenceUser mentionedUser = this.userAccessor.getUserByName(event.getMentionedUserProfile().getUsername());
        UserKey mentionedUserKey = mentionedUser != null ? mentionedUser.getKey() : null;
        ConfluenceUser mentionAuthor = event.getMentionAuthor();
        UserKey mentionAuthorKey = mentionAuthor != null ? mentionAuthor.getKey() : null;
        return Option.some((Object)new SimpleMentionContentPayload(content.getId(), content.getTypeEnum(), mentionAuthorKey, mentionedUserKey, event.getMentionHtml()));
    }
}

