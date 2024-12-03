/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.event.events.admin;

import com.atlassian.confluence.event.events.admin.ReindexEvent;
import java.util.UUID;
import javax.annotation.Nullable;

public class ReindexSkippedEvent
extends ReindexEvent {
    private static final long serialVersionUID = 6761091884705447650L;

    public ReindexSkippedEvent(Object src, @Nullable UUID reindexId) {
        super(src, reindexId);
    }
}

