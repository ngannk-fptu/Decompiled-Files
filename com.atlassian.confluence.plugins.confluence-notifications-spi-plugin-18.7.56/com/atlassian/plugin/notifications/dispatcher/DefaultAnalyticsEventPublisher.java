/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.notifications.spi.AnalyticsEventPublisher
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.plugin.notifications.dispatcher;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.notifications.spi.AnalyticsEventPublisher;
import com.atlassian.sal.api.user.UserKey;

public class DefaultAnalyticsEventPublisher
implements AnalyticsEventPublisher {
    private final EventPublisher eventPublisher;

    public DefaultAnalyticsEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publishEvent(Object event, Option<UserKey> user) {
        this.eventPublisher.publish(event);
    }
}

