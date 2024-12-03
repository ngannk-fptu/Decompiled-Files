/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cardinality.impl.operations;

import com.hazelcast.cardinality.impl.CardinalityEstimatorContainer;
import com.hazelcast.cardinality.impl.CardinalityEstimatorDataSerializerHook;
import com.hazelcast.cardinality.impl.CardinalityEstimatorService;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.Operation;
import com.hazelcast.util.MapUtil;
import java.io.IOException;
import java.util.Map;

public class ReplicationOperation
extends Operation
implements IdentifiedDataSerializable {
    private Map<String, CardinalityEstimatorContainer> migrationData;

    public ReplicationOperation() {
    }

    public ReplicationOperation(Map<String, CardinalityEstimatorContainer> migrationData) {
        this.migrationData = migrationData;
    }

    @Override
    public void run() throws Exception {
        CardinalityEstimatorService service = (CardinalityEstimatorService)this.getService();
        for (Map.Entry<String, CardinalityEstimatorContainer> entry : this.migrationData.entrySet()) {
            String name = entry.getKey();
            service.addCardinalityEstimator(name, entry.getValue());
        }
    }

    @Override
    public String getServiceName() {
        return "hz:impl:cardinalityEstimatorService";
    }

    @Override
    public int getFactoryId() {
        return CardinalityEstimatorDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 3;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeInt(this.migrationData.size());
        for (Map.Entry<String, CardinalityEstimatorContainer> entry : this.migrationData.entrySet()) {
            out.writeUTF(entry.getKey());
            out.writeObject(entry.getValue());
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        int mapSize = in.readInt();
        this.migrationData = MapUtil.createHashMap(mapSize);
        for (int i = 0; i < mapSize; ++i) {
            String name = in.readUTF();
            CardinalityEstimatorContainer newCont = (CardinalityEstimatorContainer)in.readObject();
            this.migrationData.put(name, newCont);
        }
    }
}

