/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.core.util;

import java.io.Serializable;
import java.util.Deque;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicReference;

public interface RecyclerPool<P extends WithPool<P>>
extends Serializable {
    default public P acquireAndLinkPooled() {
        return this.acquirePooled().withPool(this);
    }

    public P acquirePooled();

    public void releasePooled(P var1);

    public static abstract class BoundedPoolBase<P extends WithPool<P>>
    extends StatefulImplBase<P> {
        private static final long serialVersionUID = 1L;
        public static final int DEFAULT_CAPACITY = 100;
        private final transient ArrayBlockingQueue<P> pool;
        private final transient int capacity;

        protected BoundedPoolBase(int capacityAsId) {
            super(capacityAsId);
            this.capacity = capacityAsId <= 0 ? 100 : capacityAsId;
            this.pool = new ArrayBlockingQueue(this.capacity);
        }

        @Override
        public P acquirePooled() {
            WithPool pooled = (WithPool)this.pool.poll();
            if (pooled == null) {
                pooled = this.createPooled();
            }
            return (P)pooled;
        }

        @Override
        public void releasePooled(P pooled) {
            this.pool.offer(pooled);
        }

        public int capacity() {
            return this.capacity;
        }
    }

    public static abstract class LockFreePoolBase<P extends WithPool<P>>
    extends StatefulImplBase<P> {
        private static final long serialVersionUID = 1L;
        private final transient AtomicReference<Node<P>> head = new AtomicReference();

        protected LockFreePoolBase(int serialization) {
            super(serialization);
        }

        @Override
        public P acquirePooled() {
            for (int i = 0; i < 3; ++i) {
                Node<P> currentHead = this.head.get();
                if (currentHead == null) {
                    return this.createPooled();
                }
                if (!this.head.compareAndSet(currentHead, currentHead.next)) continue;
                currentHead.next = null;
                return (P)((WithPool)currentHead.value);
            }
            return this.createPooled();
        }

        @Override
        public void releasePooled(P pooled) {
            Node<P> newHead = new Node<P>(pooled);
            for (int i = 0; i < 3; ++i) {
                newHead.next = this.head.get();
                if (!this.head.compareAndSet(newHead.next, newHead)) continue;
                return;
            }
        }

        protected static class Node<P> {
            final P value;
            Node<P> next;

            Node(P value) {
                this.value = value;
            }
        }
    }

    public static abstract class ConcurrentDequePoolBase<P extends WithPool<P>>
    extends StatefulImplBase<P> {
        private static final long serialVersionUID = 1L;
        protected final transient Deque<P> pool = new ConcurrentLinkedDeque<P>();

        protected ConcurrentDequePoolBase(int serialization) {
            super(serialization);
        }

        @Override
        public P acquirePooled() {
            WithPool pooled = (WithPool)this.pool.pollFirst();
            if (pooled == null) {
                pooled = this.createPooled();
            }
            return (P)pooled;
        }

        @Override
        public void releasePooled(P pooled) {
            this.pool.offerLast(pooled);
        }
    }

    public static abstract class StatefulImplBase<P extends WithPool<P>>
    implements RecyclerPool<P> {
        private static final long serialVersionUID = 1L;
        public static final int SERIALIZATION_SHARED = -1;
        public static final int SERIALIZATION_NON_SHARED = 1;
        protected final int _serialization;

        protected StatefulImplBase(int serialization) {
            this._serialization = serialization;
        }

        protected Optional<StatefulImplBase<P>> _resolveToShared(StatefulImplBase<P> shared) {
            if (this._serialization == -1) {
                return Optional.of(shared);
            }
            return Optional.empty();
        }

        public abstract P createPooled();
    }

    public static abstract class NonRecyclingPoolBase<P extends WithPool<P>>
    implements RecyclerPool<P> {
        private static final long serialVersionUID = 1L;

        @Override
        public P acquireAndLinkPooled() {
            return this.acquirePooled();
        }

        @Override
        public abstract P acquirePooled();

        @Override
        public void releasePooled(P pooled) {
        }
    }

    public static abstract class ThreadLocalPoolBase<P extends WithPool<P>>
    implements RecyclerPool<P> {
        private static final long serialVersionUID = 1L;

        protected ThreadLocalPoolBase() {
        }

        @Override
        public P acquireAndLinkPooled() {
            return this.acquirePooled();
        }

        @Override
        public abstract P acquirePooled();

        @Override
        public void releasePooled(P pooled) {
        }
    }

    public static interface WithPool<P extends WithPool<P>> {
        public P withPool(RecyclerPool<P> var1);

        public void releaseToPool();
    }
}

