/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.confluence.api.model.messages.Message
 *  com.atlassian.confluence.api.model.messages.SimpleMessage
 *  com.atlassian.confluence.api.model.validation.ValidationError
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.rest.api.model.validation;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.messages.Message;
import com.atlassian.confluence.api.model.messages.SimpleMessage;
import com.atlassian.confluence.api.model.validation.ValidationError;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
@ExperimentalApi
public class RestValidationError
implements ValidationError {
    @JsonProperty
    private final SimpleMessage message;

    @JsonCreator
    private RestValidationError() {
        this.message = null;
    }

    public RestValidationError(ValidationError that) {
        this.message = SimpleMessage.copyOf((Message)that.getMessage());
    }

    public Message getMessage() {
        return this.message;
    }

    public String toString() {
        return "RestValidationError{message=" + this.message + '}';
    }
}

