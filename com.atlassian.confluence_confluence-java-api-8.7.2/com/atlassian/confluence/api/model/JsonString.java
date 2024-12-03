/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.api.model;

import java.util.Objects;
import org.codehaus.jackson.annotate.JsonProperty;

public class JsonString {
    private final String value;

    public JsonString(@JsonProperty(value="value") String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        JsonString that = (JsonString)o;
        return Objects.equals(this.value, that.value);
    }

    public int hashCode() {
        return this.value != null ? this.value.hashCode() : 0;
    }

    public String toString() {
        return "JsonString{value='" + this.value + '\'' + '}';
    }
}

