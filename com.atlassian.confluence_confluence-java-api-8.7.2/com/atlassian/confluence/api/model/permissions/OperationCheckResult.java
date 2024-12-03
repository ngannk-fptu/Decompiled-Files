/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnore
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
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@ExperimentalApi
@JsonIgnoreProperties(ignoreUnknown=true)
@RestEnrichable
public class OperationCheckResult
implements Operation {
    @JsonProperty
    private final OperationKey operation;
    @JsonProperty
    private final TargetType targetType;

    @JsonCreator
    private OperationCheckResult() {
        this(OperationCheckResult.builder());
    }

    private OperationCheckResult(OperationCheckResultBuilder builder) {
        this.operation = builder.operationKey;
        this.targetType = builder.targetType;
    }

    public static OperationCheckResultBuilder builder() {
        return new OperationCheckResultBuilder();
    }

    @Override
    @JsonIgnore
    public @NonNull OperationKey getOperationKey() {
        return this.operation;
    }

    @JsonIgnore
    public @NonNull TargetType getTargetType() {
        return this.targetType;
    }

    public String toString() {
        return "OperationCheckResult{operation=" + this.operation + ", targetType=" + this.targetType + '}';
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        OperationCheckResult that = (OperationCheckResult)o;
        return Objects.equals(this.operation, that.operation) && Objects.equals(this.targetType, that.targetType);
    }

    public int hashCode() {
        return Objects.hash(this.operation, this.targetType);
    }

    public static class OperationCheckResultBuilder {
        private OperationKey operationKey;
        private TargetType targetType;

        public OperationCheckResult build() {
            Objects.requireNonNull(this.operationKey, "operationKey must not be null");
            Objects.requireNonNull(this.targetType, "targetType must not be null");
            return new OperationCheckResult(this);
        }

        public OperationCheckResultBuilder operationKey(@NonNull OperationKey operationKey) {
            this.operationKey = operationKey;
            return this;
        }

        public OperationCheckResultBuilder targetType(@NonNull TargetType targetType) {
            this.targetType = targetType;
            return this;
        }
    }
}

