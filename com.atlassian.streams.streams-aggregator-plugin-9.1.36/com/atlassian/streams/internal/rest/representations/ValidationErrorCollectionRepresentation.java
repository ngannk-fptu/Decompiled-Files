/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.streams.internal.rest.representations;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class ValidationErrorCollectionRepresentation {
    @JsonProperty
    private final Collection<ValidationErrorEntry> errors;

    @JsonCreator
    public ValidationErrorCollectionRepresentation(@JsonProperty(value="errors") Collection<ValidationErrorEntry> errors) {
        this.errors = ImmutableList.copyOf(errors);
    }

    public Collection<ValidationErrorEntry> getErrors() {
        return this.errors;
    }

    public static class ValidationErrorEntry {
        @JsonProperty
        private final String field;
        @JsonProperty
        private final String error;
        @JsonProperty
        private final Collection<String> params;

        @JsonCreator
        public ValidationErrorEntry(@JsonProperty(value="field") String field, @JsonProperty(value="error") String error, @JsonProperty(value="params") Collection<String> params) {
            this.field = field;
            this.error = error;
            this.params = ImmutableList.copyOf(params);
        }

        public ValidationErrorEntry(String field, String error) {
            this.field = field;
            this.error = error;
            this.params = ImmutableList.of();
        }

        public String getField() {
            return this.field;
        }

        public String getError() {
            return this.error;
        }

        public Collection<String> getParams() {
            return this.params;
        }
    }
}

