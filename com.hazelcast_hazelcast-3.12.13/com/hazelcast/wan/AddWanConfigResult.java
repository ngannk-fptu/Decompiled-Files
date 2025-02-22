/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.wan;

import com.hazelcast.util.Preconditions;
import java.util.Collection;

public class AddWanConfigResult {
    private final Collection<String> addedPublisherIds;
    private final Collection<String> ignoredPublisherIds;

    public AddWanConfigResult(Collection<String> addedPublisherIds, Collection<String> ignoredPublisherIds) {
        Preconditions.checkNotNull(addedPublisherIds, "Added publisher IDs must not be null");
        Preconditions.checkNotNull(ignoredPublisherIds, "Ignored publisher IDs must not be null");
        this.addedPublisherIds = addedPublisherIds;
        this.ignoredPublisherIds = ignoredPublisherIds;
    }

    public Collection<String> getAddedPublisherIds() {
        return this.addedPublisherIds;
    }

    public Collection<String> getIgnoredPublisherIds() {
        return this.ignoredPublisherIds;
    }
}

