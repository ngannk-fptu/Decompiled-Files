/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.follow.FollowEvent
 *  com.atlassian.confluence.notifications.PayloadTransformerTemplate
 *  com.atlassian.confluence.security.access.ConfluenceAccessManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.core.user.preferences.UserPreferences
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.notifications.content.transformer;

import com.atlassian.confluence.event.events.follow.FollowEvent;
import com.atlassian.confluence.notifications.PayloadTransformerTemplate;
import com.atlassian.confluence.notifications.content.FollowerPayload;
import com.atlassian.confluence.notifications.content.SimpleFollowerNotificationPayload;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.core.user.preferences.UserPreferences;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.user.User;

public class FollowerPayloadTransformer
extends PayloadTransformerTemplate<FollowEvent, FollowerPayload> {
    private final UserAccessor userAccessor;
    private final ConfluenceAccessManager confluenceAccessManager;

    public FollowerPayloadTransformer(UserAccessor userAccessor, ConfluenceAccessManager confluenceAccessManager) {
        this.userAccessor = userAccessor;
        this.confluenceAccessManager = confluenceAccessManager;
    }

    protected Maybe<FollowerPayload> checkedCreate(FollowEvent followEvent) {
        if (this.isNotificationRequired(followEvent.getFolloweeUser())) {
            SimpleFollowerNotificationPayload payload = new SimpleFollowerNotificationPayload(followEvent.getFolloweeUser().getKey().getStringValue(), followEvent.getFollowerUser().getKey().getStringValue());
            return Option.some((Object)payload);
        }
        return Option.none();
    }

    private boolean isNotificationRequired(ConfluenceUser subject) {
        UserPreferences userPreferences = new UserPreferences(this.userAccessor.getPropertySet(subject));
        return userPreferences.getBoolean("confluence.prefs.notify.on.new.followers") && this.confluenceAccessManager.getUserAccessStatus((User)subject).hasLicensedAccess();
    }
}

