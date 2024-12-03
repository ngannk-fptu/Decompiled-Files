/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 */
package com.atlassian.applinks.core.event;

import com.atlassian.applinks.api.ApplicationLink;

public class BeforeApplicationLinkDeletedEvent {
    private final ApplicationLink applicationLink;

    public BeforeApplicationLinkDeletedEvent(ApplicationLink applicationLink) {
        this.applicationLink = applicationLink;
    }

    public ApplicationLink getApplicationLink() {
        return this.applicationLink;
    }
}

