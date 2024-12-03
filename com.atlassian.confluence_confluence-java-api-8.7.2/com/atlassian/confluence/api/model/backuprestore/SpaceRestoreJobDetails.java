/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.api.model.backuprestore;

import com.atlassian.confluence.api.model.backuprestore.JobDetails;
import com.atlassian.confluence.api.model.backuprestore.JobStatistics;
import com.atlassian.confluence.api.model.backuprestore.SpaceRestoreSettings;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class SpaceRestoreJobDetails
extends JobDetails {
    @JsonProperty
    private SpaceRestoreSettings jobSettings;
    @JsonProperty
    private JobStatistics statistics;

    public SpaceRestoreSettings getJobSettings() {
        return this.jobSettings;
    }

    public JobStatistics getJobStatistics() {
        return this.statistics;
    }

    public void setJobSettings(SpaceRestoreSettings jobSettings) {
        this.jobSettings = jobSettings;
    }

    public void setJobStatistics(JobStatistics jobStatistics) {
        this.statistics = jobStatistics;
    }
}

