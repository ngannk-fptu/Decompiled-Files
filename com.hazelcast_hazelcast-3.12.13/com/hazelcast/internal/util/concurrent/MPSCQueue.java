/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.concurrent;

import com.hazelcast.util.Preconditions;
import com.hazelcast.util.QuickMath;
import com.hazelcast.util.concurrent.IdleStrategy;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

public final class MPSCQueue<E>
extends AbstractQueue<E>
implements BlockingQueue<E> {
    static final int INITIAL_ARRAY_SIZE = 512;
    static final Node BLOCKED = new Node();
    final AtomicReference<Node> putStack = new AtomicReference();
    private final AtomicInteger takeStackSize = new AtomicInteger();
    private final IdleStrategy idleStrategy;
    private Thread consumerThread;
    private Object[] takeStack = new Object[512];
    private int takeStackIndex = -1;

    public MPSCQueue(Thread consumerThread, IdleStrategy idleStrategy) {
        this.consumerThread = Preconditions.checkNotNull(consumerThread, "consumerThread can't be null");
        this.idleStrategy = idleStrategy;
    }

    public MPSCQueue(IdleStrategy idleStrategy) {
        this.idleStrategy = idleStrategy;
    }

    public void setConsumerThread(Thread consumerThread) {
        this.consumerThread = Preconditions.checkNotNull(consumerThread, "consumerThread can't be null");
    }

    @Override
    public void clear() {
        this.putStack.set(BLOCKED);
    }

    @Override
    public boolean offer(E item) {
        Node oldHead;
        Preconditions.checkNotNull(item, "item can't be null");
        AtomicReference<Node> putStack = this.putStack;
        Node newHead = new Node();
        newHead.item = item;
        do {
            if ((oldHead = putStack.get()) == null || oldHead == BLOCKED) {
                newHead.next = null;
                newHead.size = 1;
                continue;
            }
            newHead.next = oldHead;
            newHead.size = oldHead.size + 1;
        } while (!putStack.compareAndSet(oldHead, newHead));
        if (oldHead == BLOCKED) {
            LockSupport.unpark(this.consumerThread);
        }
        return true;
    }

    @Override
    public E peek() {
        E item = this.peekNext();
        if (item != null) {
            return item;
        }
        if (!this.drainPutStack()) {
            return null;
        }
        return this.peekNext();
    }

    @Override
    public E take() throws InterruptedException {
        E item = this.next();
        if (item != null) {
            return item;
        }
        this.takeAll();
        assert (this.takeStackIndex == 0);
        assert (this.takeStack[this.takeStackIndex] != null);
        return this.next();
    }

    @Override
    public E poll() {
        E item = this.next();
        if (item != null) {
            return item;
        }
        if (!this.drainPutStack()) {
            return null;
        }
        return this.next();
    }

    private E next() {
        E item = this.peekNext();
        if (item != null) {
            this.dequeue();
        }
        return item;
    }

    private E peekNext() {
        if (this.takeStackIndex == -1) {
            return null;
        }
        if (this.takeStackIndex == this.takeStack.length) {
            this.takeStackIndex = -1;
            return null;
        }
        Object item = this.takeStack[this.takeStackIndex];
        if (item == null) {
            this.takeStackIndex = -1;
            return null;
        }
        return (E)item;
    }

    private void dequeue() {
        this.takeStack[this.takeStackIndex] = null;
        ++this.takeStackIndex;
        this.takeStackSize.lazySet(this.takeStackSize.get() - 1);
    }

    private void takeAll() throws InterruptedException {
        Node currentPutStackHead;
        long iteration = 0L;
        AtomicReference<Node> putStack = this.putStack;
        while (true) {
            if (this.consumerThread.isInterrupted()) {
                putStack.compareAndSet(BLOCKED, null);
                throw new InterruptedException();
            }
            currentPutStackHead = putStack.get();
            if (currentPutStackHead == null) {
                if (this.idleStrategy != null) {
                    this.idleStrategy.idle(iteration);
                    continue;
                }
                if (!putStack.compareAndSet(null, BLOCKED)) continue;
                LockSupport.park();
            } else if (currentPutStackHead == BLOCKED) {
                LockSupport.park();
            } else {
                if (!putStack.compareAndSet(currentPutStackHead, null)) continue;
                break;
            }
            ++iteration;
        }
        this.copyIntoTakeStack(currentPutStackHead);
    }

    private boolean drainPutStack() {
        Node head;
        do {
            if ((head = this.putStack.get()) != null) continue;
            return false;
        } while (!this.putStack.compareAndSet(head, null));
        this.copyIntoTakeStack(head);
        return true;
    }

    private void copyIntoTakeStack(Node putStackHead) {
        int putStackSize = putStackHead.size;
        this.takeStackSize.lazySet(putStackSize);
        if (putStackSize > this.takeStack.length) {
            this.takeStack = new Object[QuickMath.nextPowerOfTwo(putStackHead.size)];
        }
        for (int i = putStackSize - 1; i >= 0; --i) {
            this.takeStack[i] = putStackHead.item;
            putStackHead = putStackHead.next;
        }
        this.takeStackIndex = 0;
        assert (this.takeStack[0] != null);
    }

    @Override
    public int size() {
        Node h = this.putStack.get();
        int putStackSize = h == null ? 0 : h.size;
        return putStackSize + this.takeStackSize.get();
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public void put(E e) throws InterruptedException {
        this.offer(e);
    }

    @Override
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        this.add(e);
        return true;
    }

    @Override
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int remainingCapacity() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int drainTo(Collection<? super E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int drainTo(Collection<? super E> c, int maxElements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }

    private static final class Node<E> {
        Node next;
        E item;
        int size;

        private Node() {
        }
    }
}

