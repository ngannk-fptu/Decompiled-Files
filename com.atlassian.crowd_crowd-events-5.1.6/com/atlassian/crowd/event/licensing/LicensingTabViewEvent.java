/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.crowd.event.licensing;

import com.atlassian.crowd.event.EnumBasedEvent;
import com.google.common.collect.ImmutableMap;

public class LicensingTabViewEvent
extends EnumBasedEvent<Status> {
    public static final ImmutableMap<Status, LicensingTabViewEvent> BY_STATUS = LicensingTabViewEvent.createMapByEnum((Enum[])Status.values(), LicensingTabViewEvent::new);

    public static LicensingTabViewEvent from(Status status) {
        return (LicensingTabViewEvent)BY_STATUS.get((Object)status);
    }

    private LicensingTabViewEvent(Status status) {
        super(status);
    }

    public Status getStatus() {
        return (Status)this.data;
    }

    public static enum Status {
        NOT_CONFIGURED,
        LOADING,
        EMPTY_SEARCH_RESULTS,
        NON_EMPTY_SEARCH_RESULTS;

    }
}

