/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.annotate.JsonPropertyOrder
 */
package com.atlassian.webresource.plugin.rest.two.zero.model;

import com.atlassian.webresource.plugin.rest.two.zero.model.RequestableEdgeJson;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonPropertyOrder(alphabetic=true)
public final class RequestableEdgeListResponseJson {
    Collection<RequestableEdgeJson> items;

    @JsonCreator
    public RequestableEdgeListResponseJson(@Nonnull @JsonProperty(value="items") Collection<RequestableEdgeJson> items) {
        this.items = items;
    }

    @Nonnull
    @JsonProperty(value="items")
    public Collection<RequestableEdgeJson> getItems() {
        return this.items;
    }

    public String toString() {
        return String.format("RequestableEdgeList: %d edges.%n%s", this.items.size(), this.items.stream().map(Objects::toString).collect(Collectors.joining("\n")));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RequestableEdgeListResponseJson that = (RequestableEdgeListResponseJson)o;
        return Objects.equals(this.items, that.items);
    }

    public int hashCode() {
        return Objects.hash(this.items);
    }
}

