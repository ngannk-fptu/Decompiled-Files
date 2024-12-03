/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.api.model.backuprestore;

import com.atlassian.confluence.api.model.backuprestore.JobScope;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class FileInfo {
    @JsonProperty
    public final String name;
    @JsonProperty
    public final String creationTime;
    @JsonProperty
    public final long size;
    @JsonProperty
    public final JobScope jobScope;

    public FileInfo(String name, String creationTime, long size, JobScope jobScope) {
        this.name = name;
        this.creationTime = creationTime;
        this.size = size;
        this.jobScope = jobScope;
    }

    public String getName() {
        return this.name;
    }

    public String getCreationTime() {
        return this.creationTime;
    }

    public long getSize() {
        return this.size;
    }

    public JobScope getJobScope() {
        return this.jobScope;
    }
}

