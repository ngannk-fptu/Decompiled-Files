/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.api.model.audit;

import com.atlassian.annotations.ExperimentalApi;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@ExperimentalApi
@JsonIgnoreProperties(ignoreUnknown=true)
@Deprecated
public final class ChangedValue {
    @JsonProperty
    private final String name;
    @JsonProperty
    private final String oldValue;
    @JsonProperty
    private final String newValue;

    @JsonCreator
    private ChangedValue(@JsonProperty(value="name") String name, @JsonProperty(value="oldValue") String oldValue, @JsonProperty(value="newValue") String newValue) {
        this.name = name;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public String toString() {
        String fromClause = this.oldValue == null || this.oldValue.isEmpty() ? "" : " from [" + this.oldValue + "]";
        String toClause = this.newValue == null || this.newValue.isEmpty() ? "" : " to [" + this.newValue + "]";
        return "[" + this.name + "] was changed" + fromClause + toClause;
    }

    public String getName() {
        return this.name;
    }

    public String getOldValue() {
        return this.oldValue;
    }

    public String getNewValue() {
        return this.newValue;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean equals(Object other) {
        if (!(other instanceof ChangedValue)) {
            return false;
        }
        ChangedValue changedValue = (ChangedValue)other;
        return Objects.equals(changedValue.name, this.name) && Objects.equals(changedValue.oldValue, this.oldValue) && Objects.equals(changedValue.newValue, this.newValue);
    }

    public int hashCode() {
        return Objects.hash(this.name, this.oldValue, this.newValue);
    }

    public static class Builder {
        String name = "";
        String oldValue = "";
        String newValue = "";

        public Builder name(@NonNull String name) {
            this.name = name;
            return this;
        }

        public Builder oldValue(@NonNull String oldValue) {
            this.oldValue = oldValue;
            return this;
        }

        public Builder newValue(@NonNull String newValue) {
            this.newValue = newValue;
            return this;
        }

        public ChangedValue build() {
            return new ChangedValue(this.name, this.oldValue, this.newValue);
        }
    }
}

