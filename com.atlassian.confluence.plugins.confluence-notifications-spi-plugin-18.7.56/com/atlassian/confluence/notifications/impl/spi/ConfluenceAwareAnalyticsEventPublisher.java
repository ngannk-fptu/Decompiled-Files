/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.user.AuthenticatedUserImpersonator
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.notifications.spi.AnalyticsEventPublisher
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.notifications.impl.spi;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.user.AuthenticatedUserImpersonator;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.notifications.spi.AnalyticsEventPublisher;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import java.util.concurrent.Callable;

@Internal
public class ConfluenceAwareAnalyticsEventPublisher
implements AnalyticsEventPublisher {
    private final UserAccessor userAccessor;
    private final EventPublisher eventPublisher;

    public ConfluenceAwareAnalyticsEventPublisher(UserAccessor userAccessor, EventPublisher eventPublisher) {
        this.userAccessor = userAccessor;
        this.eventPublisher = eventPublisher;
    }

    public void publishEvent(Object event, Option<UserKey> userKey) {
        if (userKey.isDefined()) {
            ConfluenceUser user = this.userAccessor.getUserByKey((UserKey)userKey.get());
            AuthenticatedUserImpersonator.REQUEST_AGNOSTIC.asUser(this.publishEvent(event), (User)user);
        } else {
            AuthenticatedUserImpersonator.REQUEST_AGNOSTIC.asAnonymousUser(this.publishEvent(event));
        }
    }

    private Callable<Void> publishEvent(Object event) {
        return () -> {
            this.eventPublisher.publish(event);
            return null;
        };
    }
}

