/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.event.user.UserEditedEvent
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.user.User
 *  com.atlassian.user.impl.DefaultUser
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user.crowd;

import com.atlassian.confluence.event.events.user.UserDeactivateEvent;
import com.atlassian.confluence.event.events.user.UserReactivateEvent;
import com.atlassian.crowd.event.user.UserEditedEvent;
import com.atlassian.crowd.model.user.User;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.user.impl.DefaultUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceCrowdUserEventAdaptorListener {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceCrowdUserEventAdaptorListener.class);
    private final EventPublisher eventPublisher;

    public ConfluenceCrowdUserEventAdaptorListener(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @EventListener
    public void handleUserEditedEvent(UserEditedEvent userEditedEvent) {
        User originalUser = userEditedEvent.getOriginalUser();
        User user = userEditedEvent.getUser();
        if (user.isActive() != originalUser.isActive()) {
            log.debug("Active state for user {} has changed to {}: publishing event.", (Object)user.getName(), (Object)user.isActive());
            if (user.isActive()) {
                this.eventPublisher.publish((Object)new UserReactivateEvent(this, (com.atlassian.user.User)new DefaultUser(user.getName())));
            } else {
                this.eventPublisher.publish((Object)new UserDeactivateEvent(this, (com.atlassian.user.User)new DefaultUser(user.getName())));
            }
        }
    }
}

