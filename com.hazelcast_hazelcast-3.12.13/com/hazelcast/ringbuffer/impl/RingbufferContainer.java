/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.ringbuffer.impl;

import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.RingbufferConfig;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.ringbuffer.StaleSequenceException;
import com.hazelcast.ringbuffer.impl.ArrayRingbuffer;
import com.hazelcast.ringbuffer.impl.ReadResultSetImpl;
import com.hazelcast.ringbuffer.impl.Ringbuffer;
import com.hazelcast.ringbuffer.impl.RingbufferDataSerializerHook;
import com.hazelcast.ringbuffer.impl.RingbufferExpirationPolicy;
import com.hazelcast.ringbuffer.impl.RingbufferStoreWrapper;
import com.hazelcast.ringbuffer.impl.RingbufferWaitNotifyKey;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Notifier;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.WaitNotifyKey;
import com.hazelcast.spi.serialization.SerializationService;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class RingbufferContainer<T, E>
implements IdentifiedDataSerializable,
Notifier,
Versioned {
    private static final long TTL_DISABLED = 0L;
    private ObjectNamespace namespace;
    private RingbufferWaitNotifyKey emptyRingWaitNotifyKey;
    private RingbufferExpirationPolicy expirationPolicy;
    private InMemoryFormat inMemoryFormat;
    private RingbufferConfig config;
    private RingbufferStoreWrapper store;
    private SerializationService serializationService;
    private Ringbuffer<E> ringbuffer;

    public RingbufferContainer() {
    }

    public RingbufferContainer(ObjectNamespace namespace, int partitionId) {
        this.namespace = namespace;
        this.emptyRingWaitNotifyKey = new RingbufferWaitNotifyKey(namespace, partitionId);
    }

    public RingbufferContainer(ObjectNamespace namespace, RingbufferConfig config, NodeEngine nodeEngine, int partitionId) {
        this(namespace, partitionId);
        this.inMemoryFormat = config.getInMemoryFormat();
        this.ringbuffer = new ArrayRingbuffer(config.getCapacity());
        long ttlMs = TimeUnit.SECONDS.toMillis(config.getTimeToLiveSeconds());
        if (ttlMs != 0L) {
            this.expirationPolicy = new RingbufferExpirationPolicy(this.ringbuffer.getCapacity(), ttlMs);
        }
        this.init(config, nodeEngine);
    }

    public void init(RingbufferConfig config, NodeEngine nodeEngine) {
        this.config = config;
        this.serializationService = nodeEngine.getSerializationService();
        this.initRingbufferStore(nodeEngine.getConfigClassLoader());
    }

    private void initRingbufferStore(ClassLoader configClassLoader) {
        this.store = RingbufferStoreWrapper.create(this.namespace, this.config.getRingbufferStoreConfig(), this.config.getInMemoryFormat(), this.serializationService, configClassLoader);
        if (this.store.isEnabled()) {
            try {
                long storeSequence = this.store.getLargestSequence();
                if (this.tailSequence() < storeSequence) {
                    this.ringbuffer.setTailSequence(storeSequence);
                    this.ringbuffer.setHeadSequence(storeSequence + 1L);
                }
            }
            catch (Exception e) {
                throw new HazelcastException(e);
            }
        }
    }

    public RingbufferStoreWrapper getStore() {
        return this.store;
    }

    public RingbufferWaitNotifyKey getRingEmptyWaitNotifyKey() {
        return this.emptyRingWaitNotifyKey;
    }

    public RingbufferConfig getConfig() {
        return this.config;
    }

    public long tailSequence() {
        return this.ringbuffer.tailSequence();
    }

    public long headSequence() {
        return this.ringbuffer.headSequence();
    }

    public void setHeadSequence(long sequence) {
        this.ringbuffer.setHeadSequence(sequence);
    }

    public void setTailSequence(long sequence) {
        this.ringbuffer.setTailSequence(sequence);
    }

    public long getCapacity() {
        return this.ringbuffer.getCapacity();
    }

    public long size() {
        return this.ringbuffer.size();
    }

    public boolean isEmpty() {
        return this.ringbuffer.isEmpty();
    }

    public boolean shouldWait(long sequence) {
        this.checkBlockableReadSequence(sequence);
        return sequence == this.ringbuffer.tailSequence() + 1L;
    }

    public long remainingCapacity() {
        if (this.expirationPolicy != null) {
            return this.ringbuffer.getCapacity() - this.size();
        }
        return this.ringbuffer.getCapacity();
    }

    public long add(T item) {
        long storedSequence;
        long nextSequence = this.ringbuffer.peekNextTailSequence();
        if (this.store.isEnabled()) {
            try {
                this.store.store(nextSequence, this.convertToData(item));
            }
            catch (Exception e) {
                throw new HazelcastException(e);
            }
        }
        if ((storedSequence = this.addInternal(item)) != nextSequence) {
            throw new IllegalStateException("Sequence we stored the item with and Ringbuffer sequence differs. Was the Ringbuffer mutated from multiple threads?");
        }
        return storedSequence;
    }

    public long addAll(T[] items) {
        long firstSequence = this.ringbuffer.peekNextTailSequence();
        long lastSequence = this.ringbuffer.peekNextTailSequence();
        if (this.store.isEnabled() && items.length != 0) {
            try {
                this.store.storeAll(firstSequence, this.convertToData(items));
            }
            catch (Exception e) {
                throw new HazelcastException(e);
            }
        }
        for (int i = 0; i < items.length; ++i) {
            lastSequence = this.addInternal(items[i]);
        }
        return lastSequence;
    }

    public void set(long sequenceId, T item) {
        E rbItem = this.convertToRingbufferFormat(item);
        this.ringbuffer.set(sequenceId, rbItem);
        if (sequenceId > this.tailSequence()) {
            this.ringbuffer.setTailSequence(sequenceId);
            if (this.ringbuffer.size() > this.ringbuffer.getCapacity()) {
                this.ringbuffer.setHeadSequence(this.ringbuffer.tailSequence() - this.ringbuffer.getCapacity() + 1L);
            }
        }
        if (sequenceId < this.headSequence()) {
            this.ringbuffer.setHeadSequence(sequenceId);
        }
        if (this.expirationPolicy != null) {
            this.expirationPolicy.setExpirationAt(sequenceId);
        }
    }

    public Data readAsData(long sequence) {
        this.checkReadSequence(sequence);
        Object rbItem = this.readOrLoadItem(sequence);
        return this.serializationService.toData(rbItem);
    }

    public long readMany(long beginSequence, ReadResultSetImpl result) {
        long seq;
        this.checkReadSequence(beginSequence);
        for (seq = beginSequence; seq <= this.ringbuffer.tailSequence(); ++seq) {
            result.addItem(seq, this.readOrLoadItem(seq));
            if (!result.isMaxSizeReached()) continue;
            break;
        }
        return seq;
    }

    public void cleanup() {
        if (this.expirationPolicy != null) {
            this.expirationPolicy.cleanup(this.ringbuffer);
        }
    }

    public boolean isStaleSequence(long sequence) {
        return sequence < this.headSequence() && !this.store.isEnabled();
    }

    public boolean isTooLargeSequence(long sequence) {
        return sequence > this.tailSequence() + 1L;
    }

    public void checkBlockableReadSequence(long readSequence) {
        if (this.isTooLargeSequence(readSequence)) {
            throw new IllegalArgumentException("sequence:" + readSequence + " is too large. The current tailSequence is:" + this.tailSequence());
        }
        if (this.isStaleSequence(readSequence)) {
            throw new StaleSequenceException("sequence:" + readSequence + " is too small and data store is disabled. The current headSequence is:" + this.headSequence() + " tailSequence is:" + this.tailSequence(), this.headSequence());
        }
    }

    private void checkReadSequence(long sequence) {
        long tailSequence = this.ringbuffer.tailSequence();
        if (sequence > tailSequence) {
            throw new IllegalArgumentException("sequence:" + sequence + " is too large. The current tailSequence is:" + tailSequence);
        }
        if (this.isStaleSequence(sequence)) {
            throw new StaleSequenceException("sequence:" + sequence + " is too small and data store is disabled. The current headSequence is:" + this.headSequence() + " tailSequence is:" + tailSequence, this.headSequence());
        }
    }

    private Object readOrLoadItem(long sequence) {
        Object item = sequence < this.ringbuffer.headSequence() && this.store.isEnabled() ? this.store.load(sequence) : this.ringbuffer.read(sequence);
        return item;
    }

    private long addInternal(T item) {
        E rbItem = this.convertToRingbufferFormat(item);
        long tailSequence = this.ringbuffer.add(rbItem);
        if (this.expirationPolicy != null) {
            this.expirationPolicy.setExpirationAt(tailSequence);
        }
        return tailSequence;
    }

    private E convertToRingbufferFormat(Object item) {
        return (E)(this.inMemoryFormat == InMemoryFormat.OBJECT ? this.serializationService.toObject(item) : this.serializationService.toData(item));
    }

    private Data convertToData(Object item) {
        return this.serializationService.toData(item);
    }

    private Data[] convertToData(T[] items) {
        if (items == null || items.length == 0) {
            return new Data[0];
        }
        if (items[0] instanceof Data) {
            return (Data[])items;
        }
        Data[] ret = new Data[items.length];
        for (int i = 0; i < items.length; ++i) {
            ret[i] = this.convertToData(items[i]);
        }
        return ret;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        boolean ttlEnabled = this.expirationPolicy != null;
        out.writeLong(this.ringbuffer.tailSequence());
        out.writeLong(this.ringbuffer.headSequence());
        out.writeInt((int)this.ringbuffer.getCapacity());
        out.writeLong(ttlEnabled ? this.expirationPolicy.getTtlMs() : 0L);
        out.writeInt(this.inMemoryFormat.ordinal());
        long now = System.currentTimeMillis();
        for (long seq = this.ringbuffer.headSequence(); seq <= this.ringbuffer.tailSequence(); ++seq) {
            if (this.inMemoryFormat == InMemoryFormat.BINARY) {
                out.writeData((Data)this.ringbuffer.read(seq));
            } else {
                out.writeObject(this.ringbuffer.read(seq));
            }
            if (!ttlEnabled) continue;
            long deltaMs = this.expirationPolicy.getExpirationAt(seq) - now;
            out.writeLong(deltaMs);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        boolean ttlEnabled;
        long tailSequence = in.readLong();
        long headSequence = in.readLong();
        int capacity = in.readInt();
        long ttlMs = in.readLong();
        this.inMemoryFormat = InMemoryFormat.values()[in.readInt()];
        this.ringbuffer = new ArrayRingbuffer(capacity);
        this.ringbuffer.setTailSequence(tailSequence);
        this.ringbuffer.setHeadSequence(headSequence);
        boolean bl = ttlEnabled = ttlMs != 0L;
        if (ttlEnabled) {
            this.expirationPolicy = new RingbufferExpirationPolicy(capacity, ttlMs);
        }
        long now = System.currentTimeMillis();
        for (long seq = headSequence; seq <= tailSequence; ++seq) {
            if (this.inMemoryFormat == InMemoryFormat.BINARY) {
                this.ringbuffer.set(seq, in.readData());
            } else {
                this.ringbuffer.set(seq, in.readObject());
            }
            if (!ttlEnabled) continue;
            long delta = in.readLong();
            this.expirationPolicy.setExpirationAt(seq, delta + now);
        }
    }

    public Ringbuffer<E> getRingbuffer() {
        return this.ringbuffer;
    }

    RingbufferExpirationPolicy getExpirationPolicy() {
        return this.expirationPolicy;
    }

    public ObjectNamespace getNamespace() {
        return this.namespace;
    }

    @Override
    public int getFactoryId() {
        return RingbufferDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 10;
    }

    @Override
    public boolean shouldNotify() {
        return true;
    }

    @Override
    public WaitNotifyKey getNotifiedKey() {
        return this.emptyRingWaitNotifyKey;
    }

    public void clear() {
        this.ringbuffer.clear();
        if (this.expirationPolicy != null) {
            this.expirationPolicy.clear();
        }
    }
}

