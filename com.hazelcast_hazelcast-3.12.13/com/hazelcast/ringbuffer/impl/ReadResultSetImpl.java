/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.ringbuffer.impl;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.IFunction;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.projection.Projection;
import com.hazelcast.ringbuffer.ReadResultSet;
import com.hazelcast.ringbuffer.impl.RingbufferDataSerializerHook;
import com.hazelcast.spi.impl.SerializationServiceSupport;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.function.Predicate;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.util.AbstractList;

public class ReadResultSetImpl<O, E>
extends AbstractList<E>
implements IdentifiedDataSerializable,
HazelcastInstanceAware,
ReadResultSet<E>,
Versioned {
    protected transient SerializationService serializationService;
    private transient int minSize;
    private transient int maxSize;
    private transient IFunction<O, Boolean> filter;
    private transient Predicate<? super O> predicate;
    private transient Projection<? super O, E> projection;
    private Data[] items;
    private long[] seqs;
    private int size;
    private int readCount;
    private long nextSeq;

    public ReadResultSetImpl() {
    }

    public ReadResultSetImpl(int minSize, int maxSize, SerializationService serializationService, IFunction<O, Boolean> filter) {
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.items = new Data[maxSize];
        this.seqs = new long[maxSize];
        this.serializationService = serializationService;
        this.filter = filter;
    }

    public ReadResultSetImpl(int minSize, int maxSize, SerializationService serializationService, Predicate<? super O> predicate, Projection<? super O, E> projection) {
        this(minSize, maxSize, serializationService, null);
        this.predicate = predicate;
        this.projection = projection;
    }

    public boolean isMaxSizeReached() {
        return this.size == this.maxSize;
    }

    public boolean isMinSizeReached() {
        return this.size >= this.minSize;
    }

    @SuppressFBWarnings(value={"EI_EXPOSE_REP"})
    public Data[] getDataItems() {
        return this.items;
    }

    @Override
    public int readCount() {
        return this.readCount;
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hz) {
        this.setSerializationService(((SerializationServiceSupport)((Object)hz)).getSerializationService());
    }

    public void setSerializationService(SerializationService serializationService) {
        this.serializationService = serializationService;
    }

    @Override
    public E get(int index) {
        this.rangeCheck(index);
        Data item = this.items[index];
        return (E)this.serializationService.toObject(item);
    }

    @Override
    public long getSequence(int index) {
        this.rangeCheck(index);
        return this.seqs.length > index ? this.seqs[index] : -1L;
    }

    private void rangeCheck(int index) {
        if (index < 0 || index >= this.size) {
            throw new IllegalArgumentException("index=" + index + ", size=" + this.size);
        }
    }

    public void addItem(long seq, Object item) {
        Object resultItem;
        assert (this.size < this.maxSize);
        ++this.readCount;
        if (this.filter != null || this.predicate != null || this.projection != null) {
            boolean passesPredicate;
            Object objectItem = this.serializationService.toObject(item);
            boolean passesFilter = this.filter == null || this.filter.apply(objectItem) != false;
            boolean bl = passesPredicate = this.predicate == null || this.predicate.test(objectItem);
            if (!passesFilter || !passesPredicate) {
                return;
            }
            resultItem = this.projection != null ? this.serializationService.toData(this.projection.transform(objectItem)) : this.serializationService.toData(item);
        } else {
            resultItem = this.serializationService.toData(item);
        }
        this.items[this.size] = resultItem;
        this.seqs[this.size] = seq;
        ++this.size;
    }

    @Override
    public boolean add(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public int getFactoryId() {
        return RingbufferDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 9;
    }

    @Override
    public long getNextSequenceToReadFrom() {
        return this.nextSeq;
    }

    public void setNextSequenceToReadFrom(long nextSeq) {
        this.nextSeq = nextSeq;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.readCount);
        out.writeInt(this.size);
        for (int k = 0; k < this.size; ++k) {
            out.writeData(this.items[k]);
        }
        out.writeLongArray(this.seqs);
        out.writeLong(this.nextSeq);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.readCount = in.readInt();
        this.size = in.readInt();
        this.items = new Data[this.size];
        for (int k = 0; k < this.size; ++k) {
            this.items[k] = in.readData();
        }
        this.seqs = in.readLongArray();
        this.nextSeq = in.readLong();
    }
}

