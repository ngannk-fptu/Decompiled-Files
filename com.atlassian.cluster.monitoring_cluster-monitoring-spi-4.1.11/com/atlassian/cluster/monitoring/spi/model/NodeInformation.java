/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Maybe
 *  io.atlassian.fugue.Option
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.cluster.monitoring.spi.model;

import com.atlassian.cluster.monitoring.spi.model.NodeIdentifier;
import io.atlassian.fugue.Maybe;
import io.atlassian.fugue.Option;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class NodeInformation
implements Serializable,
Comparable<NodeInformation> {
    private final NodeIdentifier nodeId;
    private final String hostAddress;
    private final String hostName;
    private final Maybe<String> displayableNodeName;
    private final List<CustomNodeAttribute> customNodeAttributes;

    public NodeInformation(NodeIdentifier nodeId, String hostAddress, String hostName) {
        this(nodeId, hostAddress, hostName, (Maybe<String>)Option.none(String.class), Collections.emptyList());
    }

    public NodeInformation(NodeIdentifier nodeId, String hostAddress, String hostName, Maybe<String> displayableNodeName) {
        this(nodeId, hostAddress, hostName, displayableNodeName, Collections.emptyList());
    }

    public NodeInformation(NodeIdentifier nodeId, String hostAddress, String hostName, Maybe<String> displayableNodeName, List<CustomNodeAttribute> customNodeAttributes) {
        this.nodeId = Objects.requireNonNull(nodeId, "nodeId cannot be null");
        this.hostAddress = Objects.requireNonNull(hostAddress, "hostAddress cannot be null");
        this.hostName = Objects.requireNonNull(hostName, "hostName cannot be null");
        this.displayableNodeName = Objects.requireNonNull(displayableNodeName, "displayableNodeName cannot be null");
        this.customNodeAttributes = Objects.requireNonNull(customNodeAttributes, "customNodeAttributes cannot be null");
    }

    @Nonnull
    @JsonProperty
    public String getNodeId() {
        return this.nodeId.getNodeId();
    }

    @Nonnull
    @JsonProperty
    public String getHostAddress() {
        return this.hostAddress;
    }

    @Nonnull
    @JsonProperty
    public String getHostName() {
        return this.hostName;
    }

    @Nullable
    @JsonProperty
    public String getDisplayableNodeName() {
        return (String)this.displayableNodeName.getOrNull();
    }

    @Nonnull
    @JsonProperty
    public List<CustomNodeAttribute> getCustomNodeAttributes() {
        return this.customNodeAttributes;
    }

    @Override
    public int compareTo(NodeInformation nodeInformation) {
        return this.nodeId.compareTo(nodeInformation.nodeId);
    }

    public static class CustomNodeAttribute {
        private final Style style;
        private final String title;
        private final String value;

        public CustomNodeAttribute(Style style, String title, String value) {
            this.style = Objects.requireNonNull(style);
            this.title = Objects.requireNonNull(title);
            this.value = Objects.requireNonNull(value);
        }

        @Nonnull
        @JsonProperty
        public Style getStyle() {
            return this.style;
        }

        @Nonnull
        @JsonProperty
        public String getTitle() {
            return this.title;
        }

        @Nonnull
        @JsonProperty
        public String getValue() {
            return this.value;
        }

        public static enum Style {
            NONE,
            DEFAULT,
            SUCCESS,
            REMOVED,
            INPROGRESS,
            NEW,
            MOVED;

        }
    }
}

