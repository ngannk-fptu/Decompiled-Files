/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.event.user.UserCredentialUpdatedEvent
 *  com.atlassian.event.api.EventListener
 */
package com.atlassian.confluence.user.listeners;

import com.atlassian.confluence.user.rememberme.ConfluenceRememberMeTokenDao;
import com.atlassian.crowd.event.user.UserCredentialUpdatedEvent;
import com.atlassian.event.api.EventListener;

public class UserCredentialUpdateListener {
    private final ConfluenceRememberMeTokenDao rememberMeTokenDao;

    public UserCredentialUpdateListener(ConfluenceRememberMeTokenDao rememberMeTokenDao) {
        this.rememberMeTokenDao = rememberMeTokenDao;
    }

    @EventListener
    public void onUserRemovalEvent(UserCredentialUpdatedEvent event) {
        this.rememberMeTokenDao.removeAllForUser(event.getUsername());
    }
}

