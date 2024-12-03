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
import com.atlassian.confluence.pages.collab.impl.tracking.SynchronyEvictionSearchType;
import com.atlassian.event.api.AsynchronousPreferred;
import org.codehaus.jackson.annotate.JsonProperty;

@AsynchronousPreferred
@EventName(value="confluence.synchrony.eviction.search")
public class SynchronyEvictionSearchEvent {
    private final SynchronyEvictionSearchType type;
    private final Integer limit;
    private final Boolean successful;
    private final Integer contentsFound;
    private final Long time;

    public static SynchronyEvictionSearchEvent successful(SynchronyEvictionProgress progress, int contentsFound) {
        return new SynchronyEvictionSearchEvent(progress.getSearchType(), progress.getSearchLimit(), true, contentsFound, progress.millisPassedFromSearchStart());
    }

    public static SynchronyEvictionSearchEvent failed(SynchronyEvictionProgress progress) {
        return new SynchronyEvictionSearchEvent(progress.getSearchType(), progress.getSearchLimit(), false, null, progress.millisPassedFromSearchStart());
    }

    private SynchronyEvictionSearchEvent(SynchronyEvictionSearchType type, Integer limit, Boolean successful, Integer contentsFound, Long time) {
        this.type = type;
        this.limit = limit;
        this.successful = successful;
        this.contentsFound = contentsFound;
        this.time = time;
    }

    @JsonProperty(value="type")
    public SynchronyEvictionSearchType getType() {
        return this.type;
    }

    @JsonProperty(value="limit")
    public Integer getLimit() {
        return this.limit;
    }

    @JsonProperty(value="successful")
    public Boolean getSuccessful() {
        return this.successful;
    }

    @JsonProperty(value="contentsFound")
    public Integer getContentsFound() {
        return this.contentsFound;
    }

    @JsonProperty(value="time")
    public Long getTime() {
        return this.time;
    }
}

