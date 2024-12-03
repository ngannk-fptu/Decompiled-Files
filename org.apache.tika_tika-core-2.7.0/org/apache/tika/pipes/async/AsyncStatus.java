/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.pipes.async;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.apache.tika.pipes.PipesResult;
import org.apache.tika.pipes.pipesiterator.TotalCountResult;

public class AsyncStatus {
    private final Instant started;
    private Instant lastUpdate;
    private TotalCountResult totalCountResult = new TotalCountResult(0L, TotalCountResult.STATUS.NOT_COMPLETED);
    private Map<PipesResult.STATUS, Long> statusCounts = new HashMap<PipesResult.STATUS, Long>();
    private ASYNC_STATUS asyncStatus = ASYNC_STATUS.STARTED;
    private String crashMessage = "";

    public AsyncStatus() {
        this.lastUpdate = this.started = Instant.now();
    }

    public synchronized void update(Map<PipesResult.STATUS, Long> statusCounts, TotalCountResult totalCountResult, ASYNC_STATUS status) {
        this.lastUpdate = Instant.now();
        this.statusCounts = statusCounts;
        this.totalCountResult = totalCountResult;
        this.asyncStatus = status;
    }

    public void updateCrash(String msg) {
        this.crashMessage = msg;
    }

    public Instant getStarted() {
        return this.started;
    }

    public Instant getLastUpdate() {
        return this.lastUpdate;
    }

    public TotalCountResult getTotalCountResult() {
        return this.totalCountResult;
    }

    public Map<PipesResult.STATUS, Long> getStatusCounts() {
        return this.statusCounts;
    }

    public ASYNC_STATUS getAsyncStatus() {
        return this.asyncStatus;
    }

    public String getCrashMessage() {
        return this.crashMessage;
    }

    public String toString() {
        return "AsyncStatus{started=" + this.started + ", lastUpdate=" + this.lastUpdate + ", totalCountResult=" + this.totalCountResult + ", statusCounts=" + this.statusCounts + ", asyncStatus=" + (Object)((Object)this.asyncStatus) + ", crashMessage='" + this.crashMessage + '\'' + '}';
    }

    public static enum ASYNC_STATUS {
        STARTED,
        COMPLETED,
        CRASHED;

    }
}

