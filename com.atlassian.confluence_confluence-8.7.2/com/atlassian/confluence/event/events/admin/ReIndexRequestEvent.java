/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.admin;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.search.ReIndexOption;
import java.util.EnumSet;
import java.util.List;

public class ReIndexRequestEvent
extends ConfluenceEvent {
    private static final long serialVersionUID = 5202221995967227476L;
    private final String jobId;
    private final String sourceNodeId;
    private final EnumSet<ReIndexOption> options;
    private final List<String> spaceKeys;

    public ReIndexRequestEvent(Object src, String jobId, String sourceNodeId, EnumSet<ReIndexOption> options, List<String> spaceKeys) {
        super(src);
        this.jobId = jobId;
        this.sourceNodeId = sourceNodeId;
        this.options = options;
        this.spaceKeys = spaceKeys;
    }

    public String getJobId() {
        return this.jobId;
    }

    public String getSourceNodeId() {
        return this.sourceNodeId;
    }

    public EnumSet<ReIndexOption> getOptions() {
        return this.options;
    }

    public List<String> getSpaceKeys() {
        return this.spaceKeys;
    }
}

