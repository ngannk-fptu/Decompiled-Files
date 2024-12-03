/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.AsynchronousPreferred
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.pages.collab;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.pages.collab.impl.tracking.SynchronyEvictionProgress;
import com.atlassian.event.api.AsynchronousPreferred;
import org.codehaus.jackson.annotate.JsonProperty;

@EventName(value="confluence.synchrony.eviction.removal")
@AsynchronousPreferred
public class SynchronyEvictionRemovalEvent {
    private Boolean successful;
    private Integer contentsRemoved;
    private Integer rowsRemoved;
    private Long time;

    public static SynchronyEvictionRemovalEvent successful(SynchronyEvictionProgress progress, int rowsRemoved) {
        return new SynchronyEvictionRemovalEvent(true, progress.getRemovalNumberOfContent(), rowsRemoved, progress.millisPassedFromRemovalStart());
    }

    public static SynchronyEvictionRemovalEvent failed(SynchronyEvictionProgress progress) {
        return new SynchronyEvictionRemovalEvent(false, null, null, progress.millisPassedFromRemovalStart());
    }

    private SynchronyEvictionRemovalEvent(Boolean successful, Integer contentsRemoved, Integer rowsRemoved, Long time) {
        this.successful = successful;
        this.contentsRemoved = contentsRemoved;
        this.rowsRemoved = rowsRemoved;
        this.time = time;
    }

    @JsonProperty(value="successful")
    public Boolean getSuccessful() {
        return this.successful;
    }

    @JsonProperty(value="contentsRemoved")
    public Integer getContentsRemoved() {
        return this.contentsRemoved;
    }

    @JsonProperty(value="rowsRemoved")
    public Integer getRowsRemoved() {
        return this.rowsRemoved;
    }

    @JsonProperty(value="time")
    public Long getTime() {
        return this.time;
    }
}

