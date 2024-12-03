/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.streams.thirdparty.rest.representations;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class ErrorRepresentation {
    @JsonProperty
    private final String errorMessage;
    @JsonProperty
    private final String subCode;

    @JsonCreator
    public ErrorRepresentation(@JsonProperty(value="errorMessage") String errorMessage, @JsonProperty(value="subCode") String subCode) {
        this.errorMessage = errorMessage;
        this.subCode = subCode;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public String getSubCode() {
        return this.subCode;
    }
}

