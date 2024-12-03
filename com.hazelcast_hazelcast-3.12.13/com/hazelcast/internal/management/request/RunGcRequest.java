/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.internal.management.request;

import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.ManagementCenterService;
import com.hazelcast.internal.management.request.ConsoleRequest;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class RunGcRequest
implements ConsoleRequest {
    @Override
    public int getType() {
        return 15;
    }

    @Override
    @SuppressFBWarnings(value={"DM_GC"}, justification="Explicit GC is the point of this class")
    public void writeResponse(ManagementCenterService mcs, JsonObject root) throws Exception {
        System.gc();
        root.add("result", new JsonObject());
    }

    @Override
    public void fromJson(JsonObject json) {
    }
}

