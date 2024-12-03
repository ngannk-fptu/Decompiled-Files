/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.jdbc.pool;

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
import java.util.concurrent.locks.ReentrantLock;

public class FairBlockingQueue<E>
implements BlockingQueue<E> {
    static final boolean isLinux = "Linux".equals(System.getProperty("os.name")) && !Boolean.getBoolean(FairBlockingQueue.class.getName() + ".ignoreOS");
    final ReentrantLock lock = new ReentrantLock(false);
    final LinkedList<E> items = new LinkedList();
    final LinkedList<ExchangeCountDownLatch<E>> waiters = new LinkedList();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean offer(E e) {
        ReentrantLock lock = this.lock;
        lock.lock();
        ExchangeCountDownLatch<E> c = null;
        try {
            if (!this.waiters.isEmpty()) {
                c = this.waiters.poll();
                c.setItem(e);
                if (isLinux) {
                    c.countDown();
                }
            } else {
                this.items.addFirst(e);
            }
        }
        finally {
            lock.unlock();
        }
        if (!isLinux && c != null) {
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
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        E result = null;
        ReentrantLock lock = this.lock;
        lock.lock();
        try {
            result = this.items.poll();
            if (result == null && timeout > 0L) {
                ExchangeCountDownLatch c = new ExchangeCountDownLatch(1);
                this.waiters.addLast(c);
                lock.unlock();
                boolean didtimeout = true;
                InterruptedException interruptedException = null;
                try {
                    didtimeout = !c.await(timeout, unit);
                }
                catch (InterruptedException ix) {
                    interruptedException = ix;
                }
                if (didtimeout) {
                    lock.lock();
                    try {
                        this.waiters.remove(c);
                    }
                    finally {
                        lock.unlock();
                    }
                }
                result = (E)c.getItem();
                if (null == interruptedException) return result;
                if (null == result) throw interruptedException;
                Thread.interrupted();
                return result;
            }
            lock.unlock();
            return result;
        }
        finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Future<E> pollAsync() {
        ItemFuture<Object> result = null;
        ReentrantLock lock = this.lock;
        lock.lock();
        try {
            E item = this.items.poll();
            if (item == null) {
                ExchangeCountDownLatch c = new ExchangeCountDownLatch(1);
                this.waiters.addLast(c);
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
        ReentrantLock lock = this.lock;
        lock.lock();
        try {
            boolean bl = this.items.remove(e);
            return bl;
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public int size() {
        ReentrantLock lock = this.lock;
        lock.lock();
        try {
            int n = this.items.size();
            return n;
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new FairIterator();
    }

    @Override
    public E poll() {
        ReentrantLock lock = this.lock;
        lock.lock();
        try {
            E e = this.items.poll();
            return e;
        }
        finally {
            lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean contains(Object e) {
        ReentrantLock lock = this.lock;
        lock.lock();
        try {
            boolean bl = this.items.contains(e);
            return bl;
        }
        finally {
            lock.unlock();
        }
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

        public FairIterator() {
            ReentrantLock lock = FairBlockingQueue.this.lock;
            lock.lock();
            try {
                this.elements = new Object[FairBlockingQueue.this.items.size()];
                FairBlockingQueue.this.items.toArray(this.elements);
                this.index = 0;
            }
            finally {
                lock.unlock();
            }
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

        @Override
        public void remove() {
            ReentrantLock lock = FairBlockingQueue.this.lock;
            lock.lock();
            try {
                if (this.element != null) {
                    FairBlockingQueue.this.items.remove(this.element);
                }
            }
            finally {
                lock.unlock();
            }
        }
    }
}

