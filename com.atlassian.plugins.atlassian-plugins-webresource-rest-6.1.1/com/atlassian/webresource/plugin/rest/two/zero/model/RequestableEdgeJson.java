/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.annotate.JsonPropertyOrder
 *  org.codehaus.jackson.map.annotate.JsonSerialize
 *  org.codehaus.jackson.map.annotate.JsonSerialize$Inclusion
 */
package com.atlassian.webresource.plugin.rest.two.zero.model;

import com.atlassian.webresource.plugin.rest.two.zero.graph.Requestable;
import com.atlassian.webresource.plugin.rest.two.zero.model.ResourcePhase;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonPropertyOrder(alphabetic=true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public final class RequestableEdgeJson {
    private final String source;
    private final String target;
    private final String phase;

    public RequestableEdgeJson(Requestable source, Requestable target, ResourcePhase phase) {
        this(source.getKey(), target.getKey(), phase != null ? phase.getName() : null);
    }

    @JsonCreator
    public RequestableEdgeJson(@Nonnull @JsonProperty(value="source") String source, @Nonnull @JsonProperty(value="target") String target, @Nullable @JsonProperty(value="phase") String phase) {
        this.source = source;
        this.target = target;
        this.phase = phase;
    }

    @JsonProperty(value="source")
    @Nonnull
    public String getSource() {
        return this.source;
    }

    @JsonProperty(value="target")
    @Nonnull
    public String getTarget() {
        return this.target;
    }

    @JsonProperty(value="phase")
    @Nonnull
    public String getPhase() {
        return StringUtils.isNotBlank((CharSequence)this.phase) ? this.phase : "unknown";
    }

    public String toString() {
        return String.format("RequestableEdge(%s): %s -> %s", this.phase, this.source, this.target);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RequestableEdgeJson that = (RequestableEdgeJson)o;
        return Objects.equals(this.source, that.source) && Objects.equals(this.target, that.target) && Objects.equals(this.phase, that.phase);
    }

    public int hashCode() {
        return Objects.hash(this.source, this.target, this.phase);
    }
}

