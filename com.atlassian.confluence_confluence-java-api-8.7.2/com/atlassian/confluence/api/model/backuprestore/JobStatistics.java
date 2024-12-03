/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.api.model.backuprestore;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class JobStatistics {
    @JsonProperty
    private Long totalObjectsCount;
    @JsonProperty
    private Long processedObjectsCount;
    @JsonProperty
    private Long persistedObjectsCount;
    @JsonProperty
    private Long skippedObjectsCount;
    @JsonProperty
    private Long reusedObjectsCount;

    public Long getTotalObjectsCount() {
        return this.totalObjectsCount;
    }

    public Long getProcessedObjectsCount() {
        return this.processedObjectsCount;
    }

    public Long getPersistedObjectsCount() {
        return this.persistedObjectsCount;
    }

    public Long getSkippedObjectsCount() {
        return this.skippedObjectsCount;
    }

    public Long getReusedObjectsCount() {
        return this.reusedObjectsCount;
    }

    public void setTotalObjectsCount(Long totalObjectsCount) {
        this.totalObjectsCount = totalObjectsCount;
    }

    public void setProcessedObjectsCount(Long processedObjectsCount) {
        this.processedObjectsCount = processedObjectsCount;
    }

    public void setPersistedObjectsCount(Long persistedObjectsCount) {
        this.persistedObjectsCount = persistedObjectsCount;
    }

    public void setSkippedObjectsCount(Long skippedObjectsCount) {
        this.skippedObjectsCount = skippedObjectsCount;
    }

    public void setReusedObjectsCount(Long reusedObjectsCount) {
        this.reusedObjectsCount = reusedObjectsCount;
    }
}

