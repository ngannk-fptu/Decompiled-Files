/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.jdbc.pool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class MultiLockFairBlockingQueue<E>
implements BlockingQueue<E> {
    final int LOCK_COUNT = Runtime.getRuntime().availableProcessors();
    final AtomicInteger putQueue = new AtomicInteger(0);
    final AtomicInteger pollQueue = new AtomicInteger(0);
    private final ReentrantLock[] locks = new ReentrantLock[this.LOCK_COUNT];
    final LinkedList<E>[] items = new LinkedList[this.LOCK_COUNT];
    final LinkedList<ExchangeCountDownLatch<E>>[] waiters = new LinkedList[this.LOCK_COUNT];

    public int getNextPut() {
        int idx = Math.abs(this.putQueue.incrementAndGet()) % this.LOCK_COUNT;
        return idx;
    }

    public int getNextPoll() {
        int idx = Math.abs(this.pollQueue.incrementAndGet()) % this.LOCK_COUNT;
        return idx;
    }

    public MultiLockFairBlockingQueue() {
        for (int i = 0; i < this.LOCK_COUNT; ++i) {
            this.items[i] = new LinkedList();
            this.waiters[i] = new LinkedList();
            this.locks[i] = new ReentrantLock(false);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean offer(E e) {
        int idx = this.getNextPut();
        ReentrantLock lock = this.locks[idx];
        lock.lock();
        ExchangeCountDownLatch<E> c = null;
        try {
            if (!this.waiters[idx].isEmpty()) {
                c = this.waiters[idx].poll();
                c.setItem(e);
            } else {
                this.items[idx].addFirst(e);
            }
        }
        finally {
            lock.unlock();
        }
        if (c != null) {
            c.countDown();
        }
        return true;
    }

    @Override
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        return this.offer(e);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        int idx = this.getNextPoll();
        E result = null;
        ReentrantLock lock = this.locks[idx];
        try {
            lock.lock();
            result = this.items[idx].poll();
            if (result == null && timeout > 0L) {
                ExchangeCountDownLatch c = new ExchangeCountDownLatch(1);
                this.waiters[idx].addLast(c);
                lock.unlock();
                if (!c.await(timeout, unit)) {
                    lock.lock();
                    this.waiters[idx].remove(c);
                    lock.unlock();
                }
                result = (E)c.getItem();
            } else {
                lock.unlock();
            }
        }
        finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Future<E> pollAsync() {
        int idx = this.getNextPoll();
        ItemFuture<Object> result = null;
        ReentrantLock lock = this.locks[idx];
        try {
            lock.lock();
            E item = this.items[idx].poll();
            if (item == null) {
                ExchangeCountDownLatch c = new ExchangeCountDownLatch(1);
                this.waiters[idx].addLast(c);
                result = new ItemFuture(c);
            } else {
                result = new ItemFuture<E>(item);
            }
        }
        finally {
            lock.unlock();
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean remove(Object e) {
        for (int idx = 0; idx < this.LOCK_COUNT; ++idx) {
            ReentrantLock lock = this.locks[idx];
            lock.lock();
            try {
                boolean result = this.items[idx].remove(e);
                if (!result) continue;
                boolean bl = result;
                return bl;
            }
            finally {
                lock.unlock();
            }
        }
        return false;
    }

    @Override
    public int size() {
        int size = 0;
        for (int idx = 0; idx < this.LOCK_COUNT; ++idx) {
            size += this.items[idx].size();
        }
        return size;
    }

    @Override
    public Iterator<E> iterator() {
        return new FairIterator();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public E poll() {
        int idx = this.getNextPoll();
        ReentrantLock lock = this.locks[idx];
        lock.lock();
        try {
            E e = this.items[idx].poll();
            return e;
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public boolean contains(Object e) {
        for (int idx = 0; idx < this.LOCK_COUNT; ++idx) {
            boolean result = this.items[idx].contains(e);
            if (!result) continue;
            return result;
        }
        return false;
    }

    @Override
    public boolean add(E e) {
        return this.offer(e);
    }

    @Override
    public int drainTo(Collection<? super E> c, int maxElements) {
        throw new UnsupportedOperationException("int drainTo(Collection<? super E> c, int maxElements)");
    }

    @Override
    public int drainTo(Collection<? super E> c) {
        return this.drainTo(c, Integer.MAX_VALUE);
    }

    @Override
    public void put(E e) throws InterruptedException {
        this.offer(e);
    }

    @Override
    public int remainingCapacity() {
        return Integer.MAX_VALUE - this.size();
    }

    @Override
    public E take() throws InterruptedException {
        return this.poll(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        for (E e : c) {
            this.offer(e);
        }
        return true;
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("void clear()");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException("boolean containsAll(Collection<?> c)");
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("boolean removeAll(Collection<?> c)");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("boolean retainAll(Collection<?> c)");
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException("Object[] toArray()");
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("<T> T[] toArray(T[] a)");
    }

    @Override
    public E element() {
        throw new UnsupportedOperationException("E element()");
    }

    @Override
    public E peek() {
        throw new UnsupportedOperationException("E peek()");
    }

    @Override
    public E remove() {
        throw new UnsupportedOperationException("E remove()");
    }

    protected class ExchangeCountDownLatch<T>
    extends CountDownLatch {
        protected volatile T item;

        public ExchangeCountDownLatch(int i) {
            super(i);
        }

        public T getItem() {
            return this.item;
        }

        public void setItem(T item) {
            this.item = item;
        }
    }

    protected class ItemFuture<T>
    implements Future<T> {
        protected volatile T item = null;
        protected volatile ExchangeCountDownLatch<T> latch = null;
        protected volatile boolean canceled = false;

        public ItemFuture(T item) {
            this.item = item;
        }

        public ItemFuture(ExchangeCountDownLatch<T> latch) {
            this.latch = latch;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }

        @Override
        public T get() throws InterruptedException, ExecutionException {
            if (this.item != null) {
                return this.item;
            }
            if (this.latch != null) {
                this.latch.await();
                return this.latch.getItem();
            }
            throw new ExecutionException("ItemFuture incorrectly instantiated. Bug in the code?", new Exception());
        }

        @Override
        public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            if (this.item != null) {
                return this.item;
            }
            if (this.latch != null) {
                boolean timedout;
                boolean bl = timedout = !this.latch.await(timeout, unit);
                if (timedout) {
                    throw new TimeoutException();
                }
                return this.latch.getItem();
            }
            throw new ExecutionException("ItemFuture incorrectly instantiated. Bug in the code?", new Exception());
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return this.item != null || this.latch.getItem() != null;
        }
    }

    protected class FairIterator
    implements Iterator<E> {
        E[] elements = null;
        int index;
        E element = null;

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public FairIterator() {
            ArrayList list = new ArrayList(MultiLockFairBlockingQueue.this.size());
            for (int idx = 0; idx < MultiLockFairBlockingQueue.this.LOCK_COUNT; ++idx) {
                ReentrantLock lock = MultiLockFairBlockingQueue.this.locks[idx];
                lock.lock();
                try {
                    this.elements = new Object[MultiLockFairBlockingQueue.this.items[idx].size()];
                    MultiLockFairBlockingQueue.this.items[idx].toArray(this.elements);
                    continue;
                }
                finally {
                    lock.unlock();
                }
            }
            this.index = 0;
            this.elements = new Object[list.size()];
            list.toArray(this.elements);
        }

        @Override
        public boolean hasNext() {
            return this.index < this.elements.length;
        }

        @Override
        public E next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.element = this.elements[this.index++];
            return this.element;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void remove() {
            for (int idx = 0; idx < MultiLockFairBlockingQueue.this.LOCK_COUNT; ++idx) {
                ReentrantLock lock = MultiLockFairBlockingQueue.this.locks[idx];
                lock.lock();
                try {
                    boolean result = MultiLockFairBlockingQueue.this.items[idx].remove(this.elements[this.index]);
                    if (!result) continue;
                    break;
                }
                finally {
                    lock.unlock();
                }
            }
        }
    }
}

