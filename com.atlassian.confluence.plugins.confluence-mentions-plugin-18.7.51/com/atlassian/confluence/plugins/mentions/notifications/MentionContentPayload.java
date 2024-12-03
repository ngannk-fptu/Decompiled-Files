/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.notifications.NotificationPayload
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.plugins.mentions.notifications;

import com.atlassian.confluence.notifications.NotificationPayload;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.sal.api.user.UserKey;

public interface MentionContentPayload
extends NotificationPayload {
    public long getContentId();

    public ContentTypeEnum getContentType();

    public UserKey getMentionedUserKey();

    public Option<String> getMentionHtml();

    public Maybe<UserKey> getAuthorUserKey();
}

