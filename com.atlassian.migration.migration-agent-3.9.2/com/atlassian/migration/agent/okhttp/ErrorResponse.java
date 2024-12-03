/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.okhttp;

import lombok.Generated;
import org.codehaus.jackson.annotate.JsonProperty;

public class ErrorResponse {
    @JsonProperty
    public final int code;
    @JsonProperty
    public final String message;

    public ErrorResponse(@JsonProperty(value="code") int code, @JsonProperty(value="message") String message) {
        this.code = code;
        this.message = message;
    }

    @Generated
    public String toString() {
        return "ErrorResponse(code=" + this.code + ", message=" + this.message + ")";
    }
}

