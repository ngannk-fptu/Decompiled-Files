/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.application.ImmutableApplication
 *  com.atlassian.crowd.model.application.RemoteAddress
 */
package com.atlassian.crowd.event.application;

import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.application.ImmutableApplication;
import com.atlassian.crowd.model.application.RemoteAddress;

public class ApplicationRemoteAddressAddedEvent {
    private final ImmutableApplication application;
    private final RemoteAddress remoteAddress;

    public ApplicationRemoteAddressAddedEvent(Application application, RemoteAddress remoteAddress) {
        this.application = ImmutableApplication.from((Application)application);
        this.remoteAddress = remoteAddress;
    }

    public Application getApplication() {
        return this.application;
    }

    public RemoteAddress getRemoteAddress() {
        return this.remoteAddress;
    }

    public Long getApplicationId() {
        return this.application.getId();
    }
}

