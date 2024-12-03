/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.confluence.api.model.messages.Message
 *  com.atlassian.confluence.api.model.messages.SimpleMessage
 *  com.atlassian.confluence.api.model.validation.FieldValidationError
 *  com.atlassian.confluence.api.model.validation.SimpleFieldValidationError
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.rest.api.model.validation;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.messages.Message;
import com.atlassian.confluence.api.model.messages.SimpleMessage;
import com.atlassian.confluence.api.model.validation.FieldValidationError;
import com.atlassian.confluence.api.model.validation.SimpleFieldValidationError;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
@ExperimentalApi
public class RestFieldValidationError
implements FieldValidationError {
    @JsonProperty
    private final SimpleMessage message;
    @JsonProperty
    private final String fieldName;

    @JsonCreator
    private RestFieldValidationError() {
        this.message = null;
        this.fieldName = null;
    }

    public RestFieldValidationError(SimpleFieldValidationError that) {
        this.message = SimpleMessage.copyOf((Message)that.getMessage());
        this.fieldName = that.getFieldName();
    }

    public Message getMessage() {
        return this.message;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public String toString() {
        return "RestFieldValidationError{message=" + this.message + ", fieldName='" + this.fieldName + '\'' + '}';
    }
}

