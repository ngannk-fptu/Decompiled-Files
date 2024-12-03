/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.core.async;

import com.atlassian.upm.core.async.AsyncTaskStatus;
import com.atlassian.upm.core.async.AsyncTaskType;
import java.util.Date;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class AsyncTaskInfo {
    @JsonProperty
    private final AsyncTaskType type;
    @JsonProperty
    private final String id;
    @JsonProperty
    private final Date timestamp;
    @JsonProperty
    private final String userKey;
    @JsonProperty
    private final AsyncTaskStatus status;

    @JsonCreator
    public AsyncTaskInfo(@JsonProperty(value="id") String id, @JsonProperty(value="type") AsyncTaskType type, @JsonProperty(value="userKey") String userKey, @JsonProperty(value="timestamp") Date timestamp, @JsonProperty(value="status") AsyncTaskStatus status) {
        this.id = id;
        this.type = type;
        this.userKey = userKey;
        this.timestamp = timestamp;
        this.status = status;
    }

    public String getId() {
        return this.id;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public AsyncTaskType getType() {
        return this.type;
    }

    public String getUserKey() {
        return this.userKey;
    }

    public AsyncTaskStatus getStatus() {
        return this.status;
    }

    public AsyncTaskInfo withStatus(AsyncTaskStatus newStatus) {
        return new AsyncTaskInfo(this.id, this.type, this.userKey, this.timestamp, newStatus);
    }

    public boolean isCancellable() {
        return this.type == AsyncTaskType.CANCELLABLE;
    }
}

