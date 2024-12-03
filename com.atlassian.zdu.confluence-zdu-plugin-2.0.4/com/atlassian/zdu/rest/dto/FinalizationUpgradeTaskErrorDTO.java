/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.zdu.rest.dto;

import com.atlassian.zdu.internal.api.UpgradeTaskError;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.codehaus.jackson.annotate.JsonProperty;

@Schema(description="Represents a Finalization Upgrade Task error.")
public class FinalizationUpgradeTaskErrorDTO
implements UpgradeTaskError {
    @JsonProperty
    @Schema(description="The name of the task from which the error originated, or null if it occurred before performing finalization upgrade tasks.")
    private final String taskName;
    @JsonProperty
    @Schema(description="The exception message of the error.")
    private final String exceptionMessage;
    @JsonProperty
    @Schema(description="True if finalization upgrade task was cluster wide and false if local to the node.")
    private final boolean clusterUpgradeTask;
    @JsonProperty
    @Schema(description="A list of errors resulting from the failed finalization upgrade task, or empty if none returned.")
    private final List<String> errors;

    public FinalizationUpgradeTaskErrorDTO(@Nullable String taskName, @Nonnull String exceptionMessage, boolean isClusterUpgradeTask, @Nonnull List<String> errors) {
        this.taskName = taskName;
        this.exceptionMessage = Objects.requireNonNull(exceptionMessage);
        this.clusterUpgradeTask = isClusterUpgradeTask;
        this.errors = Objects.requireNonNull(errors);
    }

    @Override
    public String getTaskName() {
        return this.taskName;
    }

    @Override
    public String getExceptionMessage() {
        return this.exceptionMessage;
    }

    @Override
    public boolean isClusterUpgradeTask() {
        return this.clusterUpgradeTask;
    }

    @Override
    public List<String> getErrors() {
        return this.errors;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        FinalizationUpgradeTaskErrorDTO upgradeTaskErrorDTO = (FinalizationUpgradeTaskErrorDTO)o;
        return com.google.common.base.Objects.equal((Object)this.taskName, (Object)upgradeTaskErrorDTO.taskName) && com.google.common.base.Objects.equal((Object)this.exceptionMessage, (Object)upgradeTaskErrorDTO.exceptionMessage) && com.google.common.base.Objects.equal((Object)this.clusterUpgradeTask, (Object)upgradeTaskErrorDTO.clusterUpgradeTask) && com.google.common.base.Objects.equal(this.errors, upgradeTaskErrorDTO.errors);
    }

    public int hashCode() {
        return com.google.common.base.Objects.hashCode((Object[])new Object[]{this.taskName, this.exceptionMessage, this.clusterUpgradeTask, this.errors});
    }
}

