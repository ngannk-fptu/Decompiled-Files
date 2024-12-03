/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.event;

import com.atlassian.crowd.event.Event;

public class LicenseResourceLimitEvent
extends Event {
    private final Integer currentUserCount;

    public LicenseResourceLimitEvent(Object source, Integer currentUserCount) {
        super(source);
        this.currentUserCount = currentUserCount;
    }

    public Integer getCurrentUserCount() {
        return this.currentUserCount;
    }
}

