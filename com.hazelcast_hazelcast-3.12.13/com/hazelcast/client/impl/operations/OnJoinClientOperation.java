/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.operations;

import com.hazelcast.client.impl.ClientEngineImpl;
import com.hazelcast.client.impl.operations.AbstractClientOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.util.MapUtil;
import java.io.IOException;
import java.util.Map;

public class OnJoinClientOperation
extends AbstractClientOperation {
    private Map<String, String> mappings;

    public OnJoinClientOperation() {
    }

    public OnJoinClientOperation(Map<String, String> mappings) {
        this.mappings = mappings;
    }

    @Override
    public void run() throws Exception {
        if (this.mappings == null) {
            return;
        }
        ClientEngineImpl engine = (ClientEngineImpl)this.getService();
        for (Map.Entry<String, String> entry : this.mappings.entrySet()) {
            engine.addOwnershipMapping(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public String getServiceName() {
        return "hz:core:clientEngine";
    }

    @Override
    public boolean returnsResponse() {
        return false;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        if (this.mappings == null) {
            out.writeInt(0);
            return;
        }
        int len = this.mappings.size();
        out.writeInt(len);
        for (Map.Entry<String, String> entry : this.mappings.entrySet()) {
            out.writeUTF(entry.getKey());
            out.writeUTF(entry.getValue());
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        int len = in.readInt();
        this.mappings = MapUtil.createHashMap(len);
        for (int i = 0; i < len; ++i) {
            String clientUuid = in.readUTF();
            String ownerUuid = in.readUTF();
            this.mappings.put(clientUuid, ownerUuid);
        }
    }

    @Override
    public int getId() {
        return 3;
    }
}

