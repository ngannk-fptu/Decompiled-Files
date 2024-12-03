/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.plugins.authentication.api.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonProperty;

public class ValidationError {
    private final Reason reason;
    private final Map<String, Object> metadata;

    public static ValidationError required() {
        return new ValidationError(Reason.REQUIRED);
    }

    public static ValidationError incorrect() {
        return new ValidationError(Reason.INCORRECT);
    }

    public static ValidationError insecure() {
        return new ValidationError(Reason.INSECURE);
    }

    public static ValidationError notSupported() {
        return new ValidationError(Reason.NOT_SUPPORTED);
    }

    public static ValidationError tooLong() {
        return new ValidationError(Reason.TOO_LONG);
    }

    public static ValidationError nonUnique() {
        return new ValidationError(Reason.NON_UNIQUE);
    }

    private ValidationError(Reason reason) {
        this.reason = reason;
        this.metadata = new HashMap<String, Object>();
    }

    public Reason getReason() {
        return this.reason;
    }

    public Map<String, Object> getMetadata() {
        return this.metadata;
    }

    public Entity toEntity(String text) {
        return new Entity(text, this.metadata);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ValidationError error = (ValidationError)o;
        return this.reason == error.reason && Objects.equals(this.metadata, error.metadata);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.reason, this.metadata});
    }

    public static class Entity {
        @JsonProperty
        private final String text;
        @JsonProperty
        private final Map<String, Object> metadata;

        public Entity(String text, Map<String, Object> metadata) {
            this.text = text;
            this.metadata = metadata;
        }

        public String getText() {
            return this.text;
        }

        public Map<String, Object> getMetadata() {
            return this.metadata;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Entity entity = (Entity)o;
            return Objects.equals(this.text, entity.text) && Objects.equals(this.metadata, entity.metadata);
        }

        public int hashCode() {
            return Objects.hash(this.text, this.metadata);
        }
    }

    public static enum Reason {
        REQUIRED,
        INCORRECT,
        INSECURE,
        NOT_SUPPORTED,
        TOO_LONG,
        NON_UNIQUE;

    }
}

