/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.request;

import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.ManagementCenterService;
import com.hazelcast.internal.management.operation.ThreadDumpOperation;
import com.hazelcast.internal.management.request.ConsoleRequest;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.JsonUtil;
import java.util.concurrent.ExecutionException;

public class ThreadDumpRequest
implements ConsoleRequest {
    private boolean dumpDeadlocks;

    public ThreadDumpRequest() {
    }

    public ThreadDumpRequest(boolean dumpDeadlocks) {
        this.dumpDeadlocks = dumpDeadlocks;
    }

    @Override
    public int getType() {
        return 2;
    }

    @Override
    public void writeResponse(ManagementCenterService mcs, JsonObject root) {
        JsonObject result = new JsonObject();
        InternalCompletableFuture<Object> future = mcs.callOnThis(new ThreadDumpOperation(this.dumpDeadlocks));
        try {
            String threadDump = (String)future.get();
            if (threadDump != null) {
                result.add("hasDump", true);
                result.add("dump", threadDump);
            } else {
                result.add("hasDump", false);
            }
        }
        catch (ExecutionException e) {
            ThreadDumpRequest.addError(result, e);
        }
        catch (InterruptedException e) {
            ThreadDumpRequest.addError(result, e);
            Thread.currentThread().interrupt();
        }
        root.add("result", result);
    }

    @Override
    public void fromJson(JsonObject json) {
        this.dumpDeadlocks = JsonUtil.getBoolean(json, "dumpDeadlocks", false);
    }

    private static void addError(JsonObject root, Exception e) {
        root.add("hasDump", false);
        root.add("error", ExceptionUtil.toString(e));
    }
}

