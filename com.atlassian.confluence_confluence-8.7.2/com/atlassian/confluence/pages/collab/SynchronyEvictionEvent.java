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
import com.atlassian.confluence.pages.collab.impl.tracking.SynchronyEvictionType;
import com.atlassian.event.api.AsynchronousPreferred;
import org.codehaus.jackson.annotate.JsonProperty;

@AsynchronousPreferred
@EventName(value="confluence.synchrony.eviction")
public class SynchronyEvictionEvent {
    private final SynchronyEvictionType type;
    private final Integer thresholdHours;
    private final Integer limit;
    private final Boolean successful;
    private final Long time;
    private final Integer contentsRemoved;
    private final Integer rowsRemoved;

    public static SynchronyEvictionEvent successful(SynchronyEvictionProgress progress, int contentsRemoved, int rowsRemoved) {
        return new SynchronyEvictionEvent(progress.getEvictionType(), progress.getThresholdHours(), progress.getEvictionLimit(), true, progress.millisPassedFromEvictionStart(), contentsRemoved, rowsRemoved);
    }

    public static SynchronyEvictionEvent failed(SynchronyEvictionProgress progress) {
        return new SynchronyEvictionEvent(progress.getEvictionType(), progress.getThresholdHours(), progress.getEvictionLimit(), true, progress.millisPassedFromEvictionStart(), null, null);
    }

    private SynchronyEvictionEvent(SynchronyEvictionType type, Integer thresholdHours, Integer limit, Boolean successful, Long time, Integer contentsRemoved, Integer rowsRemoved) {
        this.type = type;
        this.thresholdHours = thresholdHours;
        this.limit = limit;
        this.successful = successful;
        this.time = time;
        this.contentsRemoved = contentsRemoved;
        this.rowsRemoved = rowsRemoved;
    }

    @JsonProperty(value="type")
    public SynchronyEvictionType getType() {
        return this.type;
    }

    @JsonProperty(value="thresholdHours")
    public Integer getThresholdHours() {
        return this.thresholdHours;
    }

    @JsonProperty(value="limit")
    public Integer getLimit() {
        return this.limit;
    }

    @JsonProperty(value="time")
    public long getTime() {
        return this.time;
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
}

