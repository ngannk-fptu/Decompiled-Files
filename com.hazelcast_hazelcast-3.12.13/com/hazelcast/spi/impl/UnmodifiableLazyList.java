/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.impl.SpiDataSerializerHook;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.EmptyStatement;
import com.hazelcast.util.UnmodifiableListIterator;
import java.io.IOException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class UnmodifiableLazyList<E>
extends AbstractList<E>
implements IdentifiedDataSerializable {
    private final transient SerializationService serializationService;
    private List list;

    public UnmodifiableLazyList() {
        this.serializationService = null;
    }

    public UnmodifiableLazyList(List list, SerializationService serializationService) {
        this.list = list;
        this.serializationService = serializationService;
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public E get(int index) {
        Object o = this.list.get(index);
        if (o instanceof Data) {
            Object item = this.serializationService.toObject(o);
            try {
                this.list.set(index, item);
            }
            catch (Exception e) {
                EmptyStatement.ignore(e);
            }
            return (E)item;
        }
        return o;
    }

    @Override
    public Iterator<E> iterator() {
        return this.listIterator(0);
    }

    @Override
    public ListIterator<E> listIterator() {
        return this.listIterator(0);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return new UnmodifiableLazyListIterator(this.list.listIterator(index));
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return new UnmodifiableLazyList<E>(this.list.subList(fromIndex, toIndex), this.serializationService);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.list.size());
        for (E o : this) {
            out.writeObject(o);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        int size = in.readInt();
        this.list = new ArrayList(size);
        for (int i = 0; i < size; ++i) {
            this.list.add(in.readObject());
        }
    }

    @Override
    public int getFactoryId() {
        return SpiDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 18;
    }

    private class UnmodifiableLazyListIterator
    extends UnmodifiableListIterator<E> {
        ListIterator listIterator;

        public UnmodifiableLazyListIterator(ListIterator listIterator) {
            this.listIterator = listIterator;
        }

        @Override
        public boolean hasNext() {
            return this.listIterator.hasNext();
        }

        @Override
        public E next() {
            return this.deserializeAndSet(this.listIterator.next());
        }

        @Override
        public boolean hasPrevious() {
            return this.listIterator.hasPrevious();
        }

        @Override
        public E previous() {
            return this.deserializeAndSet(this.listIterator.previous());
        }

        @Override
        public int nextIndex() {
            return this.listIterator.nextIndex();
        }

        @Override
        public int previousIndex() {
            return this.listIterator.previousIndex();
        }

        private E deserializeAndSet(Object o) {
            if (o instanceof Data) {
                Object item = UnmodifiableLazyList.this.serializationService.toObject(o);
                try {
                    this.listIterator.set(item);
                }
                catch (Exception e) {
                    EmptyStatement.ignore(e);
                }
                return item;
            }
            return o;
        }
    }
}

