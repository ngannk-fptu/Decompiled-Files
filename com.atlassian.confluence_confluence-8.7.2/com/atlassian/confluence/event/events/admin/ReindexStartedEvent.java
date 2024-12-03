/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.event.events.admin;

import com.atlassian.confluence.event.events.admin.ReindexEvent;
import com.atlassian.confluence.search.ReIndexOption;
import com.atlassian.confluence.util.Progress;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

public class ReindexStartedEvent
extends ReindexEvent {
    private static final long serialVersionUID = -1233111581057169210L;
    private final Progress progress;
    private final EnumSet<ReIndexOption> options;
    private final List<String> spaceKeys;

    @Deprecated
    public ReindexStartedEvent(Object src) {
        this(src, (Progress)null);
    }

    @Deprecated
    public ReindexStartedEvent(Object src, Progress progress) {
        this(src, progress, null, EnumSet.noneOf(ReIndexOption.class), null);
    }

    @Deprecated
    public ReindexStartedEvent(Object src, @Nullable Progress progress, @Nullable UUID reindexId) {
        this(src, progress, reindexId, EnumSet.noneOf(ReIndexOption.class), null);
    }

    public ReindexStartedEvent(Object src, @Nullable Progress progress, @Nullable UUID reindexId, EnumSet<ReIndexOption> options, List<String> spaceKeys) {
        super(src, reindexId);
        this.progress = progress;
        this.options = options;
        this.spaceKeys = spaceKeys;
    }

    @Nullable
    public Progress getProgress() {
        return this.progress;
    }

    public EnumSet<ReIndexOption> getOptions() {
        return this.options;
    }

    public List<String> getSpaceKeys() {
        return this.spaceKeys;
    }
}

