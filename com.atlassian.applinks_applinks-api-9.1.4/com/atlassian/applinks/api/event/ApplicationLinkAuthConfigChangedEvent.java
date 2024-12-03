/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.applinks.api.event;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.event.ApplicationLinkEvent;

public class ApplicationLinkAuthConfigChangedEvent
extends ApplicationLinkEvent {
    public ApplicationLinkAuthConfigChangedEvent(ApplicationLink applicationLink) {
        super(applicationLink);
    }
}

