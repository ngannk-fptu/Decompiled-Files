/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.confluence.api.model.validation.SimpleFieldValidationError
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult
 *  com.atlassian.confluence.api.model.validation.ValidationError
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonDeserialize
 */
package com.atlassian.confluence.rest.api.model.validation;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.validation.SimpleFieldValidationError;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.model.validation.ValidationError;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.rest.api.model.validation.RestFieldValidationError;
import com.atlassian.confluence.rest.api.model.validation.RestValidationError;
import com.atlassian.confluence.rest.api.model.validation.ValidationErrorDeserializer;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown=true)
@ExperimentalApi
public class RestValidationResult
implements ValidationResult {
    @JsonProperty
    private final boolean authorized;
    @JsonProperty
    private final boolean valid;
    @JsonProperty
    private final boolean allowedInReadOnlyMode;
    @JsonDeserialize(using=ValidationErrorDeserializer.class)
    @JsonProperty
    private final List<ValidationError> errors;

    @JsonCreator
    private RestValidationResult() {
        this(null);
    }

    public RestValidationResult(ValidationResult that) {
        if (that == null) {
            that = SimpleValidationResult.builder().build();
        }
        this.authorized = that.isAuthorized();
        this.valid = that.isValid();
        this.allowedInReadOnlyMode = that.isAllowedInReadOnlyMode();
        this.errors = StreamSupport.stream(that.getErrors().spliterator(), false).map(input -> {
            if (input instanceof SimpleFieldValidationError) {
                return new RestFieldValidationError((SimpleFieldValidationError)input);
            }
            return new RestValidationError((ValidationError)input);
        }).collect(Collectors.toList());
    }

    public boolean isAuthorized() {
        return this.authorized;
    }

    public boolean isAllowedInReadOnlyMode() {
        return this.allowedInReadOnlyMode;
    }

    public boolean isValid() {
        return this.valid;
    }

    public Iterable<ValidationError> getErrors() {
        return this.errors;
    }

    public String toString() {
        return "RestValidationResult{ valid=" + this.valid + ", authorized=" + this.authorized + ", allowedInReadOnlyMode=" + this.allowedInReadOnlyMode + ", errors=" + this.errors + '}';
    }
}

