/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.plugin.notifications.rest.entity;

import org.codehaus.jackson.annotate.JsonProperty;

public class TaskState {
    @JsonProperty
    private String id;
    @JsonProperty
    private String name;

    public TaskState(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
}

