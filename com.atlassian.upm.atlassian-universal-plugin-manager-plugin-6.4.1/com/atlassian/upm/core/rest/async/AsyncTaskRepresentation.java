/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.Response
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.core.rest.async;

import com.atlassian.upm.core.async.AsyncTaskErrorInfo;
import java.net.URI;
import java.util.Date;
import java.util.Map;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public final class AsyncTaskRepresentation {
    @JsonProperty
    public final Map<String, URI> links;
    @JsonProperty
    public final boolean done;
    @JsonProperty
    public final AsyncTaskErrorInfo error;
    @JsonProperty
    public final String type;
    @JsonProperty
    public final String statusDescription;
    @JsonProperty
    public final Integer itemsDone;
    @JsonProperty
    public final Integer itemsTotal;
    @JsonProperty
    public final Float progress;
    @JsonProperty
    public final Integer pollDelay;
    @JsonProperty
    public final String userKey;
    @JsonProperty
    public final Date timestamp;

    @JsonCreator
    public AsyncTaskRepresentation(@JsonProperty(value="links") Map<String, URI> links, @JsonProperty(value="done") boolean done, @JsonProperty(value="error") AsyncTaskErrorInfo error, @JsonProperty(value="type") String type, @JsonProperty(value="statusDescription") String statusDescription, @JsonProperty(value="itemsDone") Integer itemsDone, @JsonProperty(value="itemsTotal") Integer itemsTotal, @JsonProperty(value="progress") Float progress, @JsonProperty(value="pollDelay") Integer pollDelay, @JsonProperty(value="timestamp") Date timestamp, @JsonProperty(value="userKey") String userKey) {
        this.links = links;
        this.done = done;
        this.error = error;
        this.type = type;
        this.statusDescription = statusDescription;
        this.itemsDone = itemsDone;
        this.itemsTotal = itemsTotal;
        this.progress = progress;
        this.pollDelay = pollDelay;
        this.timestamp = timestamp;
        this.userKey = userKey;
    }

    public Response toResponse() {
        return Response.ok().entity((Object)this).type("application/vnd.atl.plugins+json").build();
    }
}

