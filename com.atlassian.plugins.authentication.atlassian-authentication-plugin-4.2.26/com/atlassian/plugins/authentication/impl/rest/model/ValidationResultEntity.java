/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Multimap
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.plugins.authentication.impl.rest.model;

import com.atlassian.plugins.authentication.api.config.ValidationError;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonProperty;

public class ValidationResultEntity {
    @JsonProperty
    private Map<String, Collection<ValidationError.Entity>> errors;

    public ValidationResultEntity() {
    }

    public ValidationResultEntity(Multimap<String, ValidationError.Entity> errors) {
        this.errors = new HashMap<String, Collection<ValidationError.Entity>>();
        errors.asMap().forEach((key, value) -> this.errors.put((String)key, (Collection<ValidationError.Entity>)value));
    }

    public Map<String, Collection<ValidationError.Entity>> getErrors() {
        return this.errors;
    }

    public void setErrors(Map<String, Collection<ValidationError.Entity>> errors) {
        this.errors = errors;
    }
}

