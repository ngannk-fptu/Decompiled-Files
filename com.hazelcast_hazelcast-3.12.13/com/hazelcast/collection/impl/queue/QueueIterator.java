/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.queue;

import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;
import java.util.Iterator;

public class QueueIterator<E>
implements Iterator<E> {
    private final Iterator<Data> iterator;
    private final SerializationService serializationService;
    private final boolean binary;

    public QueueIterator(Iterator<Data> iterator, SerializationService serializationService, boolean binary) {
        this.iterator = iterator;
        this.serializationService = serializationService;
        this.binary = binary;
    }

    @Override
    public boolean hasNext() {
        return this.iterator.hasNext();
    }

    @Override
    public E next() {
        Data item = this.iterator.next();
        if (this.binary) {
            return (E)item;
        }
        return (E)this.serializationService.toObject(item);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove() is not supported!");
    }
}

