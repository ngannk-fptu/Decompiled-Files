/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.mapi.entity;

import lombok.Generated;
import org.codehaus.jackson.annotate.JsonProperty;

public class AttachCloudError {
    @JsonProperty
    private String message;

    @Generated
    public AttachCloudError(String message) {
        this.message = message;
    }
}

