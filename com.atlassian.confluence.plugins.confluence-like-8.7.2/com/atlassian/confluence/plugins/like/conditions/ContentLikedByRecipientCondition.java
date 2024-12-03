/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.like.LikeManager
 *  com.atlassian.confluence.plugin.descriptor.mail.NotificationContext
 *  com.atlassian.confluence.plugin.descriptor.mail.conditions.AbstractNotificationCondition
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.like.conditions;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.like.LikeManager;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.plugin.descriptor.mail.conditions.AbstractNotificationCondition;
import com.atlassian.user.User;

public class ContentLikedByRecipientCondition
extends AbstractNotificationCondition {
    private final LikeManager likeManager;

    public ContentLikedByRecipientCondition(LikeManager likeManager) {
        this.likeManager = likeManager;
    }

    protected boolean shouldDisplay(NotificationContext context) {
        return this.likeManager.hasLike((ContentEntityObject)context.getContent(), (User)context.get("user"));
    }
}

