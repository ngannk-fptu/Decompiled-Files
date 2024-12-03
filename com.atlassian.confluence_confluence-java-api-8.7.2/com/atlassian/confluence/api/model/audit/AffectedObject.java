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
public final class AffectedObject {
    @JsonProperty
    private final String name;
    @JsonProperty
    private final String objectType;

    @JsonCreator
    private AffectedObject(@JsonProperty(value="name") String name, @JsonProperty(value="objectType") String objectType) {
        this.name = name;
        this.objectType = objectType;
    }

    public static AffectedObject none() {
        return new AffectedObject("", "");
    }

    public String getName() {
        return this.name;
    }

    public String getObjectType() {
        return this.objectType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String toString() {
        return "[" + this.name + "] [" + this.objectType + "]";
    }

    public boolean equals(Object other) {
        if (!(other instanceof AffectedObject)) {
            return false;
        }
        AffectedObject affectedObject = (AffectedObject)other;
        return Objects.equals(affectedObject.name, this.name) && Objects.equals(affectedObject.objectType, this.objectType);
    }

    public int hashCode() {
        return Objects.hash(this.name, this.objectType);
    }

    public static class Builder {
        String name = "";
        String objectType = "";

        public Builder name(@NonNull String name) {
            this.name = name;
            return this;
        }

        public Builder objectType(@NonNull String objectType) {
            this.objectType = objectType;
            return this;
        }

        public AffectedObject build() {
            return new AffectedObject(this.name, this.objectType);
        }
    }
}

