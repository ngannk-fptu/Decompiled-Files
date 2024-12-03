/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.streams.internal.rest.representations;

import com.atlassian.streams.internal.rest.representations.ProviderFilterRepresentation;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class StreamsConfigRepresentation {
    @JsonProperty
    final Collection<ProviderFilterRepresentation> filters;

    @JsonCreator
    public StreamsConfigRepresentation(@JsonProperty(value="filters") Collection<ProviderFilterRepresentation> filters) {
        this.filters = ImmutableList.copyOf(filters);
    }

    public Collection<ProviderFilterRepresentation> getFilters() {
        return this.filters;
    }
}

