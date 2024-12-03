/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.event.events.admin;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import java.util.UUID;
import javax.annotation.Nullable;

public abstract class ReindexEvent
extends ConfluenceEvent {
    private static final long serialVersionUID = -8667639020643719428L;
    private final UUID reindexId;

    @Deprecated
    public ReindexEvent(Object src) {
        this(src, null);
    }

    public ReindexEvent(Object src, @Nullable UUID reindexId) {
        super(src);
        this.reindexId = reindexId;
    }

    @Nullable
    public UUID getReindexId() {
        return this.reindexId;
    }
}

