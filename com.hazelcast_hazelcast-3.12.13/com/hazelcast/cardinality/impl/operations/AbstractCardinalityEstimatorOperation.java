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
import com.hazelcast.spi.NamedOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PartitionAwareOperation;
import java.io.IOException;

public abstract class AbstractCardinalityEstimatorOperation
extends Operation
implements NamedOperation,
PartitionAwareOperation,
IdentifiedDataSerializable {
    protected String name;

    AbstractCardinalityEstimatorOperation() {
    }

    AbstractCardinalityEstimatorOperation(String name) {
        this.name = name;
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
    public String getName() {
        return this.name;
    }

    public CardinalityEstimatorContainer getCardinalityEstimatorContainer() {
        CardinalityEstimatorService service = (CardinalityEstimatorService)this.getService();
        return service.getCardinalityEstimatorContainer(this.name);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", name=").append(this.name);
    }
}

