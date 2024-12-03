/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.AsynchronousPreferred
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.event.events.admin;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.event.events.admin.ReindexEvent;
import com.atlassian.confluence.index.status.ReIndexJob;
import com.atlassian.event.api.AsynchronousPreferred;
import java.util.Objects;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;

@AsynchronousPreferred
@EventName(value="reindex_job_finished_event")
public class ReIndexJobFinishedEvent
extends ReindexEvent {
    private static final long serialVersionUID = -431004785005056650L;
    private final ReIndexJob reIndexJob;

    public ReIndexJobFinishedEvent(Object src, ReIndexJob reIndexJob) {
        super(src, ReIndexJobFinishedEvent.getUuid(reIndexJob));
        this.reIndexJob = reIndexJob;
    }

    public ReIndexJob getReIndexJob() {
        return this.reIndexJob;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ReIndexJobFinishedEvent that = (ReIndexJobFinishedEvent)o;
        return this.reIndexJob.getId().equals(that.getReIndexJob().getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.reIndexJob);
    }

    private static UUID getUuid(ReIndexJob reIndexJob) {
        Objects.requireNonNull(reIndexJob);
        return StringUtils.isBlank((CharSequence)reIndexJob.getId()) ? null : UUID.fromString(reIndexJob.getId());
    }
}

