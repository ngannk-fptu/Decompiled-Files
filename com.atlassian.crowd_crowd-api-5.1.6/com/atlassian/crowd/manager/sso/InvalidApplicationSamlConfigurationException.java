/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.crowd.manager.sso;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Objects;

public class InvalidApplicationSamlConfigurationException
extends Exception {
    private final Map<Field, ErrorCode> errors;

    public InvalidApplicationSamlConfigurationException(Map<Field, ErrorCode> errors) {
        this.errors = ImmutableMap.copyOf(errors);
    }

    public Map<Field, ErrorCode> getErrors() {
        return this.errors;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        InvalidApplicationSamlConfigurationException that = (InvalidApplicationSamlConfigurationException)o;
        return Objects.equals(this.getErrors(), that.getErrors());
    }

    public int hashCode() {
        return Objects.hash(this.getErrors());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("errors", this.getErrors()).toString();
    }

    public static enum ErrorCode {
        NOT_UNIQUE,
        INVALID_URL,
        EMPTY;

    }

    public static enum Field {
        ENTITY_ID,
        ASSERTION_CONSUMER_URL;

    }
}

