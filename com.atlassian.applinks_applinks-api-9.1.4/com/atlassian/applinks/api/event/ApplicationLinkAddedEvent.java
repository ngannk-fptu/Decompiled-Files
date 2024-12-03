/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.applinks.api.event;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.event.ApplicationLinkEvent;

public class ApplicationLinkAddedEvent
extends ApplicationLinkEvent {
    public ApplicationLinkAddedEvent(ApplicationLink applicationLink) {
        super(applicationLink);
    }
}

