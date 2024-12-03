/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.api.model.permissions;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.permissions.Operation;
import com.atlassian.confluence.api.model.permissions.OperationKey;
import com.atlassian.confluence.api.model.permissions.TargetType;
import com.atlassian.confluence.api.serialization.RestEnrichable;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@ExperimentalApi
@JsonIgnoreProperties(ignoreUnknown=true)
@RestEnrichable
public class OperationDescription
implements Operation {
    @JsonProperty
    private final TargetType targetType;
    @JsonProperty
    private final OperationKey operationKey;

    private OperationDescription(OperationDescriptionBuilder builder) {
        Objects.requireNonNull(builder.targetType, "targetType must not be null");
        Objects.requireNonNull(builder.operationKey, "operationKey must not be null");
        this.targetType = builder.targetType;
        this.operationKey = builder.operationKey;
    }

    public static OperationDescriptionBuilder builder() {
        return new OperationDescriptionBuilder();
    }

    @Override
    public @NonNull OperationKey getOperationKey() {
        return this.operationKey;
    }

    public @NonNull TargetType getTargetType() {
        return this.targetType;
    }

    public String toString() {
        return "OperationDescription{targetType=" + this.targetType + ", operationKey=" + this.operationKey + '}';
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        OperationDescription that = (OperationDescription)o;
        return this.targetType.equals(that.targetType) && this.operationKey.equals(that.operationKey);
    }

    public int hashCode() {
        return Objects.hash(this.targetType, this.operationKey);
    }

    public static class OperationDescriptionBuilder {
        private TargetType targetType;
        private OperationKey operationKey;

        public OperationDescription build() {
            return new OperationDescription(this);
        }

        public OperationDescriptionBuilder targetType(@NonNull TargetType targetType) {
            this.targetType = targetType;
            return this;
        }

        public OperationDescriptionBuilder operationKey(@NonNull OperationKey operationKey) {
            this.operationKey = operationKey;
            return this;
        }
    }
}

