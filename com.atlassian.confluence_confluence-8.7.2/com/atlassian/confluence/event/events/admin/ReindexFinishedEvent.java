/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.event.events.admin;

import com.atlassian.confluence.event.events.admin.ReindexEvent;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

public class ReindexFinishedEvent
extends ReindexEvent {
    private static final long serialVersionUID = 6761091884705447650L;
    private final List<String> spaceKeys;

    @Deprecated
    public ReindexFinishedEvent(Object src) {
        this(src, null, Collections.emptyList());
    }

    public ReindexFinishedEvent(Object src, @Nullable UUID reindexId) {
        this(src, reindexId, Collections.emptyList());
    }

    public ReindexFinishedEvent(Object src, @Nullable UUID reindexId, List<String> spaceKeys) {
        super(src, reindexId);
        this.spaceKeys = spaceKeys;
    }

    public List<String> getSpaceKeys() {
        return this.spaceKeys;
    }
}

