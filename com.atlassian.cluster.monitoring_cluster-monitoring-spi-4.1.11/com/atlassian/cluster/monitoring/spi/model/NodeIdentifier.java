/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.cluster.monitoring.spi.model;

import java.io.Serializable;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class NodeIdentifier
implements Serializable,
Comparable<NodeIdentifier> {
    @JsonProperty
    private final String nodeId;

    @JsonCreator
    public NodeIdentifier(@JsonProperty String nodeId) {
        this.nodeId = Objects.requireNonNull(nodeId);
    }

    public String getNodeId() {
        return this.nodeId;
    }

    public boolean equals(Object obj) {
        if (obj instanceof NodeIdentifier) {
            NodeIdentifier that = (NodeIdentifier)obj;
            return this.nodeId.equals(that.getNodeId());
        }
        return false;
    }

    public int hashCode() {
        return this.nodeId.hashCode();
    }

    public String toString() {
        return this.nodeId;
    }

    @Override
    public int compareTo(NodeIdentifier that) {
        return this.nodeId.compareTo(that.nodeId);
    }
}

