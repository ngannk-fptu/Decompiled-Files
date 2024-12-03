/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Iterables
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$ResponseBuilder
 *  javax.ws.rs.core.Response$Status
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.core.rest.async;

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.async.AsyncTaskErrorInfo;
import com.atlassian.upm.core.async.AsyncTaskInfo;
import com.atlassian.upm.core.async.AsyncTaskStage;
import com.atlassian.upm.core.async.AsyncTaskStatus;
import com.atlassian.upm.core.async.AsyncTaskType;
import com.atlassian.upm.core.async.TaskSubitemFailure;
import com.atlassian.upm.core.async.TaskSubitemSuccess;
import com.atlassian.upm.core.rest.BaseUriBuilder;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public final class LegacyAsyncTaskRepresentation {
    @JsonProperty
    private final AsyncTaskType type;
    @JsonProperty
    private final Integer pingAfter;
    @JsonProperty
    private final LegacyStatusRep status;
    @JsonProperty
    private final Map<String, URI> links;
    @JsonProperty
    private final String userKey;
    @JsonProperty
    private final Date timestamp;
    @JsonProperty
    private final String id;

    @JsonCreator
    public LegacyAsyncTaskRepresentation(@JsonProperty(value="type") AsyncTaskType type, @JsonProperty(value="pingAfter") Integer pingAfter, @JsonProperty(value="status") LegacyStatusRep status, @JsonProperty(value="links") Map<String, URI> links, @JsonProperty(value="timestamp") Date timestamp, @JsonProperty(value="userKey") String userKey, @JsonProperty(value="id") String id) {
        this.type = type;
        this.pingAfter = pingAfter;
        this.status = status;
        this.links = links == null ? ImmutableMap.of() : links;
        this.timestamp = timestamp;
        this.userKey = userKey;
        this.id = id;
    }

    public LegacyAsyncTaskRepresentation(AsyncTaskInfo task, BaseUriBuilder uriBuilder) {
        this.type = task.getType();
        this.status = this.makeLegacyStatusRep(task, uriBuilder);
        this.userKey = task.getUserKey();
        this.timestamp = task.getTimestamp();
        this.pingAfter = task.getStatus().getPollDelay();
        this.links = this.buildLinks(task, uriBuilder);
        this.id = task.getId();
    }

    public AsyncTaskType getType() {
        return this.type;
    }

    public Integer getPingAfter() {
        return this.pingAfter;
    }

    public LegacyStatusRep getStatus() {
        return this.status;
    }

    public Map<String, URI> getLinks() {
        return this.links;
    }

    public URI getSelf() {
        return this.links.get("self");
    }

    public String getUserKey() {
        return this.userKey;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public String getId() {
        return this.id;
    }

    public Response toResponse() {
        return Response.status((int)(this.status == null ? Response.Status.OK.getStatusCode() : this.status.statusCode)).entity((Object)this).type(this.status == null || this.status.contentType == null ? "application/vnd.atl.plugins.pending-task+json" : this.status.contentType).build();
    }

    public Response toNewlyCreatedResponse(BaseUriBuilder uriBuilder) {
        Response.ResponseBuilder ret = Response.status((Response.Status)Response.Status.ACCEPTED).entity((Object)this).type(this.status == null || this.status.contentType == null ? "application/vnd.atl.plugins.pending-task+json" : this.status.contentType);
        if (this.links.get("self") != null) {
            ret.location(uriBuilder.makeAbsolute(this.links.get("self")));
        }
        return ret.build();
    }

    private Map<String, URI> buildLinks(AsyncTaskInfo task, BaseUriBuilder uriBuilder) {
        ImmutableMap.Builder links = ImmutableMap.builder();
        links.put((Object)"self", (Object)uriBuilder.buildLegacyPendingTaskUri(task.getId()));
        links.put((Object)"alternate", (Object)uriBuilder.buildPendingTaskUri(task.getId()));
        for (TaskSubitemSuccess s : task.getStatus().getSuccesses()) {
            if (s.getLinks() == null || !s.getLinks().containsKey("change-requiring-restart")) continue;
            links.put((Object)"changes-requiring-restart", (Object)uriBuilder.buildChangesRequiringRestartUri());
            break;
        }
        return links.build();
    }

    private LegacyStatusRep makeLegacyStatusRep(AsyncTaskInfo task, BaseUriBuilder uriBuilder) {
        AsyncTaskStatus status = task.getStatus();
        String subCode = null;
        String errorMessage = null;
        Long amountDownloaded = null;
        Long totalSize = null;
        ImmutableList successes = null;
        ImmutableList failures = null;
        for (AsyncTaskErrorInfo error : status.getError()) {
            subCode = error.getCode().getOrElse((String)null);
            errorMessage = error.getMessage().getOrElse((String)null);
        }
        for (Float progress : status.getProgress()) {
            amountDownloaded = (long)(progress.floatValue() * 100.0f);
            totalSize = 100L;
        }
        successes = status.getSuccesses().isEmpty() ? null : ImmutableList.copyOf((Iterable)Iterables.transform(status.getSuccesses(), this.makeSuccessItemRep(uriBuilder)));
        failures = status.getFailures().isEmpty() ? null : ImmutableList.copyOf((Iterable)Iterables.transform(status.getFailures(), this.makeFailureItemRep()));
        return new LegacyStatusRep(status.isDone(), this.getLegacyStatusCode(task).getStatusCode(), this.getLegacyContentType(task), subCode, errorMessage, status.getResourceName().getOrElse((String)null), status.getNextStepPostUri().getOrElse((URI)null), amountDownloaded, totalSize, status.getResourceName().getOrElse((String)null), status.getResourceVersion().getOrElse((String)null), status.getItemsDone().getOrElse((Integer)null), status.getItemsTotal().getOrElse((Integer)null), (Collection<SuccessItemRep>)successes, (Collection<FailureItemRep>)failures);
    }

    private Function<TaskSubitemSuccess, SuccessItemRep> makeSuccessItemRep(BaseUriBuilder uriBuilder) {
        return s -> new SuccessItemRep(s.getName(), s.getKey(), s.getVersion(), s.getLinks());
    }

    private Function<TaskSubitemFailure, FailureItemRep> makeFailureItemRep() {
        return f -> new FailureItemRep(f.getType() == null ? null : f.getType().name(), f.getName(), f.getKey(), f.getVersion(), f.getErrorCode(), f.getMessage(), f.getSource());
    }

    private Response.Status getLegacyStatusCode(AsyncTaskInfo taskInfo) {
        for (AsyncTaskStage stage : taskInfo.getStatus().getStage()) {
            if (stage != AsyncTaskStage.POST_INSTALL_TASK) continue;
            return Response.Status.ACCEPTED;
        }
        return Response.Status.OK;
    }

    private String getLegacyContentType(AsyncTaskInfo taskInfo) {
        AsyncTaskStatus status = taskInfo.getStatus();
        Option<AsyncTaskStage> stage = status.getStage();
        switch (taskInfo.getType()) {
            case CANCELLABLE: {
                return "application/vnd.atl.plugins.cancellable.blocking+json";
            }
            case DISABLE_ALL_INCOMPATIBLE: {
                for (AsyncTaskStage s : stage) {
                    switch (s) {
                        case APPLYING_ALL: {
                            return "application/vnd.atl.plugins.disableall.disabling+json";
                        }
                        case FINDING: {
                            return "application/vnd.atl.plugins.disableall.finding+json";
                        }
                    }
                }
                return status.getError().isDefined() ? "application/vnd.atl.plugins.disableall.err+json" : "application/vnd.atl.plugins.disableall.complete+json";
            }
            case EMBEDDED_HOST_LICENSE_CHANGE: {
                if (status.isDone()) {
                    return status.getError().isDefined() ? "application/vnd.atl.plugins.task.embeddedlicense.err+json" : "application/vnd.atl.plugins.embeddedlicense.complete+json";
                }
                return "application/vnd.atl.plugins.embeddedlicense.installing+json";
            }
            case INSTALL: {
                for (AsyncTaskStage s : stage) {
                    switch (s) {
                        case DOWNLOADING: {
                            return "application/vnd.atl.plugins.install.downloading+json";
                        }
                        case INSTALLING: {
                            return "application/vnd.atl.plugins.install.installing+json";
                        }
                        case POST_INSTALL_TASK: {
                            return "application/vnd.atl.plugins.install.next-task+json";
                        }
                    }
                }
                return status.getError().isDefined() ? "application/vnd.atl.plugins.task.install.err+json" : "application/vnd.atl.plugins.install.complete+json";
            }
            case PLUGIN_SCAN_DIRECTORY_REFRESH: {
                return "application/json";
            }
            case UPDATE_ALL: {
                for (AsyncTaskStage s : stage) {
                    switch (s) {
                        case FINDING: {
                            return "application/vnd.atl.plugins.updateall.finding+json";
                        }
                        case DOWNLOADING: {
                            return "application/vnd.atl.plugins.updateall.downloading+json";
                        }
                        case APPLYING_ALL: {
                            return "application/vnd.atl.plugins.updateall.updating+json";
                        }
                    }
                }
                return status.getError().isDefined() ? "application/vnd.atl.plugins.updateall.err+json" : "application/vnd.atl.plugins.updateall.complete+json";
            }
        }
        return status.getError().isDefined() ? "application/vnd.atl.plugins.task.error+json" : "application/vnd.atl.plugins.pending-task+json";
    }

    public static class FailureItemRep {
        @JsonProperty
        private final String name;
        @JsonProperty
        private final String key;
        @JsonProperty
        private final String version;
        @JsonProperty
        private final String subCode;
        @JsonProperty
        private final String message;
        @JsonProperty
        private final String source;
        @JsonProperty
        private final String type;

        @JsonCreator
        public FailureItemRep(@JsonProperty(value="type") String type, @JsonProperty(value="name") String name, @JsonProperty(value="key") String key, @JsonProperty(value="version") String version, @JsonProperty(value="subCode") String subCode, @JsonProperty(value="message") String message, @JsonProperty(value="source") String source) {
            this.type = type;
            this.name = name;
            this.key = key;
            this.version = version;
            this.subCode = subCode;
            this.message = message;
            this.source = source;
        }

        public String getName() {
            return this.name;
        }

        public String getKey() {
            return this.key;
        }

        public String getVersion() {
            return this.version;
        }

        public String getSubCode() {
            return this.subCode;
        }

        public String getMessage() {
            return this.message;
        }

        public String getSource() {
            return this.source;
        }

        public String getType() {
            return this.type;
        }
    }

    public static class SuccessItemRep {
        @JsonProperty
        public final String name;
        @JsonProperty
        public final String key;
        @JsonProperty
        public final String version;
        @JsonProperty
        public final Map<String, URI> links;

        @JsonCreator
        public SuccessItemRep(@JsonProperty(value="name") String name, @JsonProperty(value="key") String key, @JsonProperty(value="version") String version, @JsonProperty(value="links") Map<String, URI> links) {
            this.name = name;
            this.key = key;
            this.version = version;
            this.links = links;
        }

        public String getName() {
            return this.name;
        }

        public String getKey() {
            return this.key;
        }

        public String getVersion() {
            return this.version;
        }

        public Map<String, URI> getLinks() {
            return this.links;
        }
    }

    public static class LegacyStatusRep {
        @JsonProperty
        public final boolean done;
        @JsonProperty
        public final int statusCode;
        @JsonProperty
        public final String contentType;
        @JsonProperty
        public final String subCode;
        @JsonProperty
        public final String errorMessage;
        @JsonProperty
        public final String source;
        @JsonProperty
        public final URI nextTaskPostUri;
        @JsonProperty
        public final Long amountDownloaded;
        @JsonProperty
        public final Long totalSize;
        @JsonProperty
        public final String name;
        @JsonProperty
        public final String version;
        @JsonProperty
        public final Integer numberComplete;
        @JsonProperty
        public final Integer totalItems;
        @JsonProperty
        public final Collection<SuccessItemRep> successes;
        @JsonProperty
        public final Collection<FailureItemRep> failures;

        @JsonCreator
        public LegacyStatusRep(@JsonProperty(value="done") boolean done, @JsonProperty(value="statusCode") int statusCode, @JsonProperty(value="contentType") String contentType, @JsonProperty(value="subCode") String subCode, @JsonProperty(value="errorMessage") String errorMessage, @JsonProperty(value="source") String source, @JsonProperty(value="nextTaskPostUri") URI nextTaskPostUri, @JsonProperty(value="amountDownloaded") Long amountDownloaded, @JsonProperty(value="totalSize") Long totalSize, @JsonProperty(value="name") String name, @JsonProperty(value="version") String version, @JsonProperty(value="numberComplete") Integer numberComplete, @JsonProperty(value="totalItems") Integer totalItems, @JsonProperty(value="successes") Collection<SuccessItemRep> successes, @JsonProperty(value="failures") Collection<FailureItemRep> failures) {
            this.done = done;
            this.statusCode = statusCode;
            this.contentType = contentType;
            this.subCode = subCode;
            this.errorMessage = errorMessage;
            this.source = source;
            this.nextTaskPostUri = nextTaskPostUri;
            this.amountDownloaded = amountDownloaded;
            this.totalSize = totalSize;
            this.name = name;
            this.version = version;
            this.numberComplete = numberComplete;
            this.totalItems = totalItems;
            this.successes = successes;
            this.failures = failures;
        }

        public boolean isDone() {
            return this.done;
        }

        public int getStatusCode() {
            return this.statusCode;
        }

        public String getContentType() {
            return this.contentType;
        }

        public String getSubCode() {
            return this.subCode;
        }

        public String getErrorMessage() {
            return this.errorMessage;
        }

        public String getSource() {
            return this.source;
        }

        public URI getNextTaskPostUri() {
            return this.nextTaskPostUri;
        }

        public Long getAmountDownloaded() {
            return this.amountDownloaded;
        }

        public Long getTotalSize() {
            return this.totalSize;
        }

        public String getName() {
            return this.name;
        }

        public String getVersion() {
            return this.version;
        }

        public Integer getNumberComplete() {
            return this.numberComplete;
        }

        public Integer getTotalItems() {
            return this.totalItems;
        }

        public Collection<SuccessItemRep> getSuccesses() {
            return this.successes;
        }

        public Collection<FailureItemRep> getFailures() {
            return this.failures;
        }
    }
}

