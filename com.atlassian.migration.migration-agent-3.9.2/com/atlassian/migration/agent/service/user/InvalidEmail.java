/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.service.user;

import lombok.Generated;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class InvalidEmail {
    private final int errorCode;
    private final String email;

    @JsonCreator
    public InvalidEmail(@JsonProperty(value="errorCode") int errorCode, @JsonProperty(value="email") String email) {
        this.errorCode = errorCode;
        this.email = email;
    }

    @Generated
    public int getErrorCode() {
        return this.errorCode;
    }

    @Generated
    public String getEmail() {
        return this.email;
    }
}

