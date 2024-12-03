/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.core.rest.async;

import com.atlassian.upm.core.rest.BaseUriBuilder;
import com.atlassian.upm.core.rest.async.LegacyAsyncTaskRepresentation;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.net.URI;
import java.util.Collection;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class LegacyAsyncTaskCollectionRepresentation {
    @JsonProperty
    private final Map<String, URI> links;
    @JsonProperty
    private final Collection<LegacyAsyncTaskRepresentation> tasks;

    @JsonCreator
    public LegacyAsyncTaskCollectionRepresentation(@JsonProperty(value="links") Map<String, URI> links, @JsonProperty(value="tasks") Collection<LegacyAsyncTaskRepresentation> tasks) {
        this.links = ImmutableMap.copyOf(links);
        this.tasks = ImmutableList.copyOf(tasks);
    }

    LegacyAsyncTaskCollectionRepresentation(Iterable<LegacyAsyncTaskRepresentation> tasks, BaseUriBuilder uriBuilder) {
        this.links = ImmutableMap.of((Object)"self", (Object)uriBuilder.buildLegacyPendingTasksUri());
        this.tasks = ImmutableList.copyOf(tasks);
    }

    public URI getSelf() {
        return this.links.get("self");
    }

    public Collection<LegacyAsyncTaskRepresentation> getTasks() {
        return this.tasks;
    }
}

