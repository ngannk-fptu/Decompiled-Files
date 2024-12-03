/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.admin;

import com.atlassian.confluence.event.events.admin.ReindexEvent;
import java.util.UUID;

public class ReindexBatchFinishedEvent
extends ReindexEvent {
    private static final long serialVersionUID = -2340920666217198692L;

    public ReindexBatchFinishedEvent(Object src, UUID reindexId) {
        super(src, reindexId);
    }
}

