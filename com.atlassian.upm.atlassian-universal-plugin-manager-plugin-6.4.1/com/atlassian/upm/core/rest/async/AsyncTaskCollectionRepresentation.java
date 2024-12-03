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
import com.atlassian.upm.core.rest.async.AsyncTaskRepresentation;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.net.URI;
import java.util.Collection;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class AsyncTaskCollectionRepresentation {
    @JsonProperty
    private final Map<String, URI> links;
    @JsonProperty
    private final Collection<AsyncTaskRepresentation> tasks;

    @JsonCreator
    public AsyncTaskCollectionRepresentation(@JsonProperty(value="links") Map<String, URI> links, @JsonProperty(value="tasks") Collection<AsyncTaskRepresentation> tasks) {
        this.links = ImmutableMap.copyOf(links);
        this.tasks = ImmutableList.copyOf(tasks);
    }

    AsyncTaskCollectionRepresentation(Iterable<AsyncTaskRepresentation> tasks, BaseUriBuilder uriBuilder) {
        this.links = ImmutableMap.of((Object)"self", (Object)uriBuilder.buildPendingTasksUri());
        this.tasks = ImmutableList.copyOf(tasks);
    }

    public URI getSelf() {
        return this.links.get("self");
    }

    public Collection<AsyncTaskRepresentation> getTasks() {
        return this.tasks;
    }
}

