/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.admin;

import com.atlassian.confluence.event.events.admin.ReindexEvent;
import com.atlassian.confluence.search.ReIndexOption;
import java.util.UUID;

public class ReindexStageFinishedEvent
extends ReindexEvent {
    private static final long serialVersionUID = -1527469524390940442L;
    private final String option;

    public ReindexStageFinishedEvent(Object src, String option, UUID reindexId) {
        super(src, reindexId);
        this.option = option;
    }

    @Deprecated
    public ReindexStageFinishedEvent(Object src, String option) {
        this(src, option, null);
    }

    @Deprecated
    public ReindexStageFinishedEvent(Object src, ReIndexOption reIndexOption) {
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

