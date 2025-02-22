/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.map.impl.MapEntries;
import com.hazelcast.map.impl.operation.PutAllOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.operationservice.impl.operations.PartitionAwareOperationFactory;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.util.Arrays;

public class PutAllPartitionAwareOperationFactory
extends PartitionAwareOperationFactory {
    protected String name;
    protected MapEntries[] mapEntries;

    public PutAllPartitionAwareOperationFactory() {
    }

    @SuppressFBWarnings(value={"EI_EXPOSE_REP2"})
    public PutAllPartitionAwareOperationFactory(String name, int[] partitions, MapEntries[] mapEntries) {
        this.name = name;
        this.partitions = partitions;
        this.mapEntries = mapEntries;
    }

    @Override
    public Operation createPartitionOperation(int partitionId) {
        for (int i = 0; i < this.partitions.length; ++i) {
            if (this.partitions[i] != partitionId) continue;
            return new PutAllOperation(this.name, this.mapEntries[i]);
        }
        throw new IllegalArgumentException("Unknown partitionId " + partitionId + " (" + Arrays.toString(this.partitions) + ")");
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeIntArray(this.partitions);
        for (MapEntries entry : this.mapEntries) {
            entry.writeData(out);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.partitions = in.readIntArray();
        this.mapEntries = new MapEntries[this.partitions.length];
        for (int i = 0; i < this.partitions.length; ++i) {
            MapEntries entry = new MapEntries();
            entry.readData(in);
            this.mapEntries[i] = entry;
        }
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 87;
    }
}

