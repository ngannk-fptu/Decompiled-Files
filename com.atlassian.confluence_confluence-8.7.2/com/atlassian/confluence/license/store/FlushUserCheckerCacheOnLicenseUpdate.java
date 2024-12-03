/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.license.store;

import com.atlassian.confluence.event.events.admin.LicenceUpdatedEvent;
import com.atlassian.confluence.util.UserChecker;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class FlushUserCheckerCacheOnLicenseUpdate
implements UserChecker,
InitializingBean,
DisposableBean {
    private UserChecker delegate;
    private EventPublisher eventPublisher;

    public FlushUserCheckerCacheOnLicenseUpdate(UserChecker delegate, EventPublisher eventPublisher) {
        this.delegate = delegate;
        this.eventPublisher = eventPublisher;
    }

    @EventListener
    public void listenForLicenseUpdate(LicenceUpdatedEvent event) {
        this.delegate.resetResult();
    }

    @Override
    public int getNumberOfRegisteredUsers() {
        return this.delegate.getNumberOfRegisteredUsers();
    }

    @Override
    public boolean hasTooManyUsers() {
        return this.delegate.hasTooManyUsers();
    }

    @Override
    public boolean isLicensedToAddMoreUsers() {
        return this.delegate.isLicensedToAddMoreUsers();
    }

    @Override
    public void incrementRegisteredUserCount() {
        this.delegate.incrementRegisteredUserCount();
    }

    @Override
    public void decrementRegisteredUserCount() {
        this.delegate.decrementRegisteredUserCount();
    }

    @Override
    public void resetResult() {
        this.delegate.resetResult();
    }

    @Override
    public boolean isUnlimitedUserLicense() {
        return this.delegate.isUnlimitedUserLicense();
    }

    public void afterPropertiesSet() throws Exception {
        this.eventPublisher.register((Object)this);
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
    }
}

