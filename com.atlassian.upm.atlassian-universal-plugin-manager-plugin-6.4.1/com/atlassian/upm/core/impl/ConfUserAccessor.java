/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.user.UserEvent
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.event.api.EventListener
 *  io.atlassian.util.concurrent.ResettableLazyReference
 */
package com.atlassian.upm.core.impl;

import com.atlassian.confluence.event.events.user.UserEvent;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.event.api.EventListener;
import com.atlassian.upm.LazyReferences;
import io.atlassian.util.concurrent.ResettableLazyReference;

public class ConfUserAccessor {
    private final ResettableLazyReference<Integer> cachedUserCount;

    public ConfUserAccessor(final UserAccessor userAccessor) {
        this.cachedUserCount = new ResettableLazyReference<Integer>(){

            protected Integer create() {
                return userAccessor.countLicenseConsumingUsers();
            }
        };
    }

    public int getActiveUserCount() {
        return LazyReferences.safeGet(this.cachedUserCount);
    }

    @EventListener
    public void onUserEvent(UserEvent event) {
        this.cachedUserCount.reset();
    }
}

