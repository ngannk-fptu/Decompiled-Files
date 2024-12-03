/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.google.common.base.Preconditions
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.util.profiling;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import org.checkerframework.checker.nullness.qual.NonNull;

@ParametersAreNonnullByDefault
@Internal
public class ActivitySnapshot
implements Serializable {
    private final long startTime;
    private final long threadId;
    private final String threadName;
    private final String userId;
    private final String type;
    private final String summary;

    public ActivitySnapshot(long startTime, long threadId, String threadName, String userId, String type, String summary) {
        this.startTime = startTime;
        this.threadId = threadId;
        this.threadName = (String)Preconditions.checkNotNull((Object)threadName);
        this.userId = (String)Preconditions.checkNotNull((Object)userId);
        this.type = (String)Preconditions.checkNotNull((Object)type);
        this.summary = (String)Preconditions.checkNotNull((Object)summary);
    }

    public long getStartTime() {
        return this.startTime;
    }

    public long getThreadId() {
        return this.threadId;
    }

    public @NonNull String getThreadName() {
        return this.threadName;
    }

    public @NonNull String getUserId() {
        return this.userId;
    }

    public @NonNull String getType() {
        return this.type;
    }

    public @NonNull String getSummary() {
        return this.summary;
    }

    public String toString() {
        return "ActivitySnapshot{startTime=" + this.startTime + ", threadId=" + this.threadId + ", threadName='" + this.threadName + "', userId='" + this.userId + "', type='" + this.type + "', summary='" + this.summary + "'}";
    }
}

