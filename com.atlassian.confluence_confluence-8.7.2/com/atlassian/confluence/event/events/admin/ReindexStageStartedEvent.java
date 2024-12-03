/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.admin;

import com.atlassian.confluence.event.events.admin.ReindexEvent;
import com.atlassian.confluence.search.ReIndexOption;
import java.util.UUID;

public class ReindexStageStartedEvent
extends ReindexEvent {
    private static final long serialVersionUID = -3212998250521893677L;
    private final String option;

    public ReindexStageStartedEvent(Object src, String option, UUID reindexId) {
        super(src, reindexId);
        this.option = option;
    }

    @Deprecated
    public ReindexStageStartedEvent(Object src, String option) {
        this(src, option, null);
    }

    @Deprecated
    public ReindexStageStartedEvent(Object src, ReIndexOption reIndexOption) {
        super(src);
        this.option = reIndexOption.name();
    }

    public String getOption() {
        return this.option;
    }

    @Deprecated
    public ReIndexOption getReIndexOption() {
        return ReIndexOption.valueOf(this.option);
    }
}

