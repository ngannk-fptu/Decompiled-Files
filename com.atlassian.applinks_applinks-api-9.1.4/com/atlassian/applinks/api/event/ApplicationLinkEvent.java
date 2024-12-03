/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.applinks.api.event;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.api.event.LinkEvent;

public abstract class ApplicationLinkEvent
implements LinkEvent {
    protected final ApplicationLink applicationLink;

    protected ApplicationLinkEvent(ApplicationLink applicationLink) {
        this.applicationLink = applicationLink;
    }

    public ApplicationId getApplicationId() {
        return this.applicationLink.getId();
    }

    public ApplicationLink getApplicationLink() {
        return this.applicationLink;
    }

    public ApplicationType getApplicationType() {
        return this.applicationLink.getType();
    }
}

