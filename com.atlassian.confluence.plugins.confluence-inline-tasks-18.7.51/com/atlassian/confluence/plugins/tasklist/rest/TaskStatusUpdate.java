/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.tasklist.rest;

import javax.annotation.Nullable;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class TaskStatusUpdate {
    @JsonProperty
    private final String status;
    @JsonProperty
    private final String trigger;

    public TaskStatusUpdate(@JsonProperty(value="status") String status, @JsonProperty(value="trigger") String trigger) {
        this.status = status;
        this.trigger = trigger;
    }

    @Nullable
    public String getTrigger() {
        return this.trigger;
    }

    @Nullable
    public String getStatus() {
        return this.status;
    }
}

