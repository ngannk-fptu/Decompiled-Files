/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.troubleshooting.jfr.rest.dto;

import org.codehaus.jackson.annotate.JsonProperty;

public class ErrorReasonDto {
    @JsonProperty
    private final String message;

    public ErrorReasonDto(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}

