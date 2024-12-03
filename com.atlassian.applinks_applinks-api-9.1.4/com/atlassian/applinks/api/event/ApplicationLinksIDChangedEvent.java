/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.applinks.api.event;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.event.ApplicationLinkEvent;

public class ApplicationLinksIDChangedEvent
extends ApplicationLinkEvent {
    private final ApplicationId oldApplicationId;

    public ApplicationLinksIDChangedEvent(ApplicationLink applicationLink, ApplicationId oldApplicationId) {
        super(applicationLink);
        this.oldApplicationId = oldApplicationId;
    }

    public ApplicationId getOldApplicationId() {
        return this.oldApplicationId;
    }
}

