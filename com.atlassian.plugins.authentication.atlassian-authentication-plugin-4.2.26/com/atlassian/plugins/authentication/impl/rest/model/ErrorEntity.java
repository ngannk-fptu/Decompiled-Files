/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.plugins.authentication.impl.rest.model;

import org.codehaus.jackson.annotate.JsonProperty;

public class ErrorEntity {
    @JsonProperty
    private final String message;

    public ErrorEntity(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}

