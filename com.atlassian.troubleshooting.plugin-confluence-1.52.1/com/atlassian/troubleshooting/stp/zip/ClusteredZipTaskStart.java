/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nonnull
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.troubleshooting.stp.zip;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class ClusteredZipTaskStart {
    @JsonProperty
    private final String clusterTaskId;
    @JsonProperty
    private final Set<String> nodeIds;

    @JsonCreator
    public ClusteredZipTaskStart(@JsonProperty(value="clusterTaskId") String clusterTaskId, @JsonProperty(value="nodeIds") Collection<String> nodeIds) {
        this.clusterTaskId = Objects.requireNonNull(clusterTaskId);
        this.nodeIds = ImmutableSet.copyOf(nodeIds);
    }

    @Nonnull
    public String getClusterTaskId() {
        return this.clusterTaskId;
    }

    @Nonnull
    public Set<String> getNodeIds() {
        return this.nodeIds;
    }
}

