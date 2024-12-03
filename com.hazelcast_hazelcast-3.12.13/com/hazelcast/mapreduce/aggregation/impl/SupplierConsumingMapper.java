/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.mapreduce.aggregation.impl;

import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;
import com.hazelcast.mapreduce.aggregation.Supplier;
import com.hazelcast.mapreduce.aggregation.impl.AggregationsDataSerializerHook;
import com.hazelcast.mapreduce.aggregation.impl.SimpleEntry;
import com.hazelcast.mapreduce.impl.task.DefaultContext;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.query.impl.getters.Extractors;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;

@BinaryInterface
@SuppressFBWarnings(value={"SE_NO_SERIALVERSIONID"})
class SupplierConsumingMapper<Key, ValueIn, ValueOut>
implements Mapper<Key, ValueIn, Key, ValueOut>,
IdentifiedDataSerializable {
    private transient SimpleEntry<Key, ValueIn> entry = new SimpleEntry();
    private Supplier<Key, ValueIn, ValueOut> supplier;

    SupplierConsumingMapper() {
    }

    SupplierConsumingMapper(Supplier<Key, ValueIn, ValueOut> supplier) {
        this.supplier = supplier;
    }

    @Override
    public void map(Key key, ValueIn value, Context<Key, ValueOut> context) {
        this.entry.setKey(key);
        this.entry.setValue(value);
        this.entry.setSerializationService(((DefaultContext)context).getSerializationService());
        this.entry.setExtractors(Extractors.newBuilder(((DefaultContext)context).getSerializationService()).build());
        ValueOut valueOut = this.supplier.apply(this.entry);
        if (valueOut != null) {
            context.emit(key, valueOut);
        }
    }

    @Override
    public int getFactoryId() {
        return AggregationsDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject(this.supplier);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.supplier = (Supplier)in.readObject();
    }
}

