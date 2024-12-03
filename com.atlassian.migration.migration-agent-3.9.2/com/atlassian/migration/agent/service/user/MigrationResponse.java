/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.service.user;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class MigrationResponse {
    final String taskId;

    @JsonCreator
    public MigrationResponse(@JsonProperty(value="taskId") String taskId) {
        this.taskId = taskId;
    }
}

