/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 */
package com.atlassian.confluence.user.listeners;

import com.atlassian.confluence.event.events.user.UserRemoveEvent;
import com.atlassian.confluence.security.persistence.dao.UserLoginInfoDao;
import com.atlassian.event.api.EventListener;

public final class UserRemovalListener {
    private final UserLoginInfoDao userLoginInfoDao;

    public UserRemovalListener(UserLoginInfoDao userLoginInfoDao) {
        this.userLoginInfoDao = userLoginInfoDao;
    }

    @EventListener
    public void handleUserRemoveEvent(UserRemoveEvent userRemoveEvent) {
        this.userLoginInfoDao.deleteUserInfoFor(userRemoveEvent.getUser());
    }
}

