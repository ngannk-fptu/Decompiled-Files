/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.plugins.license.entity;

import org.codehaus.jackson.annotate.JsonProperty;

public class ErrorEntity {
    @JsonProperty(value="message")
    private String message;

    public ErrorEntity(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}

