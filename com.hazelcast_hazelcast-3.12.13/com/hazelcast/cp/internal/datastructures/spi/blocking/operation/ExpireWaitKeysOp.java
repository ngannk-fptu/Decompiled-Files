/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.spi.blocking.operation;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.RaftOp;
import com.hazelcast.cp.internal.datastructures.RaftDataServiceDataSerializerHook;
import com.hazelcast.cp.internal.datastructures.spi.blocking.AbstractBlockingService;
import com.hazelcast.cp.internal.util.Tuple2;
import com.hazelcast.cp.internal.util.UUIDSerializationUtil;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class ExpireWaitKeysOp
extends RaftOp
implements IdentifiedDataSerializable {
    private String serviceName;
    private Collection<Tuple2<String, UUID>> keys;

    public ExpireWaitKeysOp() {
    }

    public ExpireWaitKeysOp(String serviceName, Collection<Tuple2<String, UUID>> keys) {
        this.serviceName = serviceName;
        this.keys = keys;
    }

    @Override
    public Object run(CPGroupId groupId, long commitIndex) {
        AbstractBlockingService service = (AbstractBlockingService)this.getService();
        service.expireWaitKeys(groupId, this.keys);
        return null;
    }

    @Override
    public String getServiceName() {
        return this.serviceName;
    }

    @Override
    public int getFactoryId() {
        return RaftDataServiceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 2;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.serviceName);
        out.writeInt(this.keys.size());
        for (Tuple2<String, UUID> key : this.keys) {
            out.writeUTF((String)key.element1);
            UUIDSerializationUtil.writeUUID(out, (UUID)key.element2);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.serviceName = in.readUTF();
        int size = in.readInt();
        this.keys = new ArrayList<Tuple2<String, UUID>>(size);
        for (int i = 0; i < size; ++i) {
            String name = in.readUTF();
            UUID invocationUid = UUIDSerializationUtil.readUUID(in);
            this.keys.add(Tuple2.of(name, invocationUid));
        }
    }

    @Override
    protected void toString(StringBuilder sb) {
        sb.append(", keys=").append(this.keys);
    }
}

