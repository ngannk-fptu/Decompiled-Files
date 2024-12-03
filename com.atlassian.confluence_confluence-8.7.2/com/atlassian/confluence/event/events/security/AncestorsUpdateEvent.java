/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.event.events.security;

import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;

public class AncestorsUpdateEvent {
    private final Long pageId;
    private final List<Long> ancestorsIds;

    public AncestorsUpdateEvent(@NonNull Long pageId, @NonNull List<Long> ancestorsIds) {
        this.pageId = pageId;
        this.ancestorsIds = ancestorsIds;
    }

    public Long getPageId() {
        return this.pageId;
    }

    public List<Long> getAncestorsIds() {
        return this.ancestorsIds;
    }
}

