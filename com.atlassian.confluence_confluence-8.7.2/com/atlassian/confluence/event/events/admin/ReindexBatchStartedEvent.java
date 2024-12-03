/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.admin;

import com.atlassian.confluence.event.events.admin.ReindexEvent;
import java.util.UUID;

public class ReindexBatchStartedEvent
extends ReindexEvent {
    private static final long serialVersionUID = -5115353481742351356L;

    public ReindexBatchStartedEvent(Object src, UUID reindexId) {
        super(src, reindexId);
    }
}

