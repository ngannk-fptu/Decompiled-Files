/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.annotate.JsonPropertyOrder
 */
package com.atlassian.webresource.plugin.rest.two.zero.model;

import javax.annotation.Nonnull;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonPropertyOrder(alphabetic=true)
public class ErrorResponseJson {
    private final String message;

    @JsonCreator
    public ErrorResponseJson(@Nonnull @JsonProperty(value="message") String message) {
        this.message = message;
    }

    @JsonProperty(value="message")
    public String getMessage() {
        return this.message;
    }
}

