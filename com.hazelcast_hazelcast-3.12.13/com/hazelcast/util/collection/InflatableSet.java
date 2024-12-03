/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.util.collection;

import com.hazelcast.nio.serialization.SerializableByConvention;
import com.hazelcast.util.Preconditions;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@SerializableByConvention
public final class InflatableSet<T>
extends AbstractSet<T>
implements Set<T>,
Serializable,
Cloneable {
    private static final long serialVersionUID = 0L;
    private final List<T> compactList;
    private Set<T> inflatedSet;
    private State state;

    private InflatableSet(List<T> compactList) {
        this.state = State.COMPACT;
        this.compactList = compactList;
    }

    private InflatableSet(InflatableSet<T> other) {
        this.compactList = new ArrayList<T>(other.compactList.size());
        this.compactList.addAll(other.compactList);
        if (other.inflatedSet != null) {
            this.inflatedSet = new HashSet<T>(other.inflatedSet);
        }
        this.state = other.state;
    }

    public static <T> Builder<T> newBuilder(int initialCapacity) {
        return new Builder(initialCapacity);
    }

    public static <T> Builder<T> newBuilder(List<T> list) {
        return new Builder(list);
    }

    @Override
    public int size() {
        if (this.state == State.INFLATED) {
            return this.inflatedSet.size();
        }
        return this.compactList.size();
    }

    @Override
    public boolean isEmpty() {
        if (this.state == State.INFLATED) {
            return this.inflatedSet.isEmpty();
        }
        return this.compactList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        if (this.state == State.COMPACT) {
            this.toHybridState();
        }
        return this.inflatedSet.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        if (this.state == State.INFLATED) {
            return this.inflatedSet.iterator();
        }
        return new HybridIterator();
    }

    @Override
    public boolean add(T t) {
        this.toInflatedState();
        return this.inflatedSet.add(t);
    }

    @Override
    public boolean remove(Object o) {
        switch (this.state) {
            case HYBRID: {
                this.compactList.remove(o);
                return this.inflatedSet.remove(o);
            }
            case INFLATED: {
                return this.inflatedSet.remove(o);
            }
        }
        return this.compactList.remove(o);
    }

    @Override
    public void clear() {
        switch (this.state) {
            case HYBRID: {
                this.inflatedSet.clear();
                this.compactList.clear();
                break;
            }
            case INFLATED: {
                this.inflatedSet.clear();
                break;
            }
            default: {
                this.compactList.clear();
            }
        }
    }

    @SuppressFBWarnings(value={"CN_IDIOM"}, justification="Deliberate, documented contract violation")
    protected Object clone() {
        return new InflatableSet<T>(this);
    }

    private void inflateIfNeeded() {
        if (this.inflatedSet == null) {
            this.inflatedSet = new HashSet<T>(this.compactList);
        }
    }

    private void toHybridState() {
        if (this.state == State.HYBRID) {
            return;
        }
        this.state = State.HYBRID;
        this.inflateIfNeeded();
    }

    private void toInflatedState() {
        if (this.state == State.INFLATED) {
            return;
        }
        this.state = State.INFLATED;
        this.inflateIfNeeded();
        this.invalidateIterators();
    }

    private void invalidateIterators() {
        if (this.compactList.size() == 0) {
            this.compactList.clear();
        } else {
            this.compactList.remove(0);
        }
    }

    public static final class Builder<T> {
        private List<T> list;

        private Builder(int initialCapacity) {
            this.list = new ArrayList<T>(initialCapacity);
        }

        private Builder(List<T> list) {
            this.list = Preconditions.checkNotNull(list, "list cannot be null");
        }

        public int size() {
            return this.list.size();
        }

        public Builder add(T item) {
            this.list.add(item);
            return this;
        }

        public InflatableSet<T> build() {
            InflatableSet set = new InflatableSet(this.list);
            this.list = Collections.emptyList();
            return set;
        }
    }

    private class HybridIterator
    implements Iterator<T> {
        private Iterator<T> innerIterator;
        private T currentValue;

        HybridIterator() {
            this.innerIterator = InflatableSet.this.compactList.iterator();
        }

        @Override
        public boolean hasNext() {
            return this.innerIterator.hasNext();
        }

        @Override
        public T next() {
            this.currentValue = this.innerIterator.next();
            return this.currentValue;
        }

        @Override
        public void remove() {
            this.innerIterator.remove();
            if (InflatableSet.this.inflatedSet != null) {
                InflatableSet.this.inflatedSet.remove(this.currentValue);
            }
        }
    }

    private static enum State {
        COMPACT,
        HYBRID,
        INFLATED;

    }
}

