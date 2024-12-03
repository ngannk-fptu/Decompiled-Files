/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.pool2.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.time.Duration;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import org.apache.tomcat.dbcp.pool2.impl.InterruptibleReentrantLock;
import org.apache.tomcat.dbcp.pool2.impl.PoolImplUtils;

class LinkedBlockingDeque<E>
extends AbstractQueue<E>
implements Deque<E>,
Serializable {
    private static final long serialVersionUID = -387911632671998426L;
    private transient Node<E> first;
    private transient Node<E> last;
    private transient int count;
    private final int capacity;
    private final InterruptibleReentrantLock lock;
    private final Condition notEmpty;
    private final Condition notFull;

    LinkedBlockingDeque() {
        this(Integer.MAX_VALUE);
    }

    LinkedBlockingDeque(boolean fairness) {
        this(Integer.MAX_VALUE, fairness);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    LinkedBlockingDeque(Collection<? extends E> c) {
        this(Integer.MAX_VALUE);
        this.lock.lock();
        try {
            for (E e : c) {
                Objects.requireNonNull(e);
                if (super.linkLast(e)) continue;
                throw new IllegalStateException("Deque full");
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    LinkedBlockingDeque(int capacity) {
        this(capacity, false);
    }

    LinkedBlockingDeque(int capacity, boolean fairness) {
        if (capacity <= 0) {
            throw new IllegalArgumentException();
        }
        this.capacity = capacity;
        this.lock = new InterruptibleReentrantLock(fairness);
        this.notEmpty = this.lock.newCondition();
        this.notFull = this.lock.newCondition();
    }

    @Override
    public boolean add(E e) {
        this.addLast(e);
        return true;
    }

    @Override
    public void addFirst(E e) {
        if (!this.offerFirst(e)) {
            throw new IllegalStateException("Deque full");
        }
    }

    @Override
    public void addLast(E e) {
        if (!this.offerLast(e)) {
            throw new IllegalStateException("Deque full");
        }
    }

    @Override
    public void clear() {
        this.lock.lock();
        try {
            Node<E> f = this.first;
            while (f != null) {
                f.item = null;
                Node n = f.next;
                f.prev = null;
                f.next = null;
                f = n;
            }
            this.last = null;
            this.first = null;
            this.count = 0;
            this.notFull.signalAll();
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean contains(Object o) {
        if (o == null) {
            return false;
        }
        this.lock.lock();
        try {
            Node<E> p = this.first;
            while (p != null) {
                if (o.equals(p.item)) {
                    boolean bl = true;
                    return bl;
                }
                p = p.next;
            }
            boolean bl = false;
            return bl;
        }
        finally {
            this.lock.unlock();
        }
    }

    @Override
    public Iterator<E> descendingIterator() {
        return new DescendingItr();
    }

    public int drainTo(Collection<? super E> c) {
        return this.drainTo(c, Integer.MAX_VALUE);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int drainTo(Collection<? super E> c, int maxElements) {
        Objects.requireNonNull(c, "c");
        if (c == this) {
            throw new IllegalArgumentException();
        }
        this.lock.lock();
        try {
            int n = Math.min(maxElements, this.count);
            for (int i = 0; i < n; ++i) {
                c.add(this.first.item);
                this.unlinkFirst();
            }
            int n2 = n;
            return n2;
        }
        finally {
            this.lock.unlock();
        }
    }

    @Override
    public E element() {
        return this.getFirst();
    }

    @Override
    public E getFirst() {
        E x = this.peekFirst();
        if (x == null) {
            throw new NoSuchElementException();
        }
        return x;
    }

    @Override
    public E getLast() {
        E x = this.peekLast();
        if (x == null) {
            throw new NoSuchElementException();
        }
        return x;
    }

    public int getTakeQueueLength() {
        this.lock.lock();
        try {
            int n = this.lock.getWaitQueueLength(this.notEmpty);
            return n;
        }
        finally {
            this.lock.unlock();
        }
    }

    public boolean hasTakeWaiters() {
        this.lock.lock();
        try {
            boolean bl = this.lock.hasWaiters(this.notEmpty);
            return bl;
        }
        finally {
            this.lock.unlock();
        }
    }

    public void interuptTakeWaiters() {
        this.lock.lock();
        try {
            this.lock.interruptWaiters(this.notEmpty);
        }
        finally {
            this.lock.unlock();
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }

    private boolean linkFirst(E e) {
        if (this.count >= this.capacity) {
            return false;
        }
        Node<E> f = this.first;
        Node<E> x = new Node<E>(e, null, f);
        this.first = x;
        if (this.last == null) {
            this.last = x;
        } else {
            f.prev = x;
        }
        ++this.count;
        this.notEmpty.signal();
        return true;
    }

    private boolean linkLast(E e) {
        if (this.count >= this.capacity) {
            return false;
        }
        Node<E> l = this.last;
        Node<E> x = new Node<E>(e, l, null);
        this.last = x;
        if (this.first == null) {
            this.first = x;
        } else {
            l.next = x;
        }
        ++this.count;
        this.notEmpty.signal();
        return true;
    }

    @Override
    public boolean offer(E e) {
        return this.offerLast(e);
    }

    boolean offer(E e, Duration timeout) throws InterruptedException {
        return this.offerLast(e, timeout);
    }

    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        return this.offerLast(e, timeout, unit);
    }

    @Override
    public boolean offerFirst(E e) {
        Objects.requireNonNull(e, "e");
        this.lock.lock();
        try {
            boolean bl = this.linkFirst(e);
            return bl;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean offerFirst(E e, Duration timeout) throws InterruptedException {
        Objects.requireNonNull(e, "e");
        long nanos = timeout.toNanos();
        this.lock.lockInterruptibly();
        try {
            while (!this.linkFirst(e)) {
                if (nanos <= 0L) {
                    boolean bl = false;
                    return bl;
                }
                nanos = this.notFull.awaitNanos(nanos);
            }
            boolean bl = true;
            return bl;
        }
        finally {
            this.lock.unlock();
        }
    }

    public boolean offerFirst(E e, long timeout, TimeUnit unit) throws InterruptedException {
        return this.offerFirst(e, PoolImplUtils.toDuration(timeout, unit));
    }

    @Override
    public boolean offerLast(E e) {
        Objects.requireNonNull(e, "e");
        this.lock.lock();
        try {
            boolean bl = this.linkLast(e);
            return bl;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    boolean offerLast(E e, Duration timeout) throws InterruptedException {
        Objects.requireNonNull(e, "e");
        long nanos = timeout.toNanos();
        this.lock.lockInterruptibly();
        try {
            while (!this.linkLast(e)) {
                if (nanos <= 0L) {
                    boolean bl = false;
                    return bl;
                }
                nanos = this.notFull.awaitNanos(nanos);
            }
            boolean bl = true;
            return bl;
        }
        finally {
            this.lock.unlock();
        }
    }

    public boolean offerLast(E e, long timeout, TimeUnit unit) throws InterruptedException {
        return this.offerLast(e, PoolImplUtils.toDuration(timeout, unit));
    }

    @Override
    public E peek() {
        return this.peekFirst();
    }

    @Override
    public E peekFirst() {
        this.lock.lock();
        try {
            E e = this.first == null ? null : (E)this.first.item;
            return e;
        }
        finally {
            this.lock.unlock();
        }
    }

    @Override
    public E peekLast() {
        this.lock.lock();
        try {
            E e = this.last == null ? null : (E)this.last.item;
            return e;
        }
        finally {
            this.lock.unlock();
        }
    }

    @Override
    public E poll() {
        return this.pollFirst();
    }

    E poll(Duration timeout) throws InterruptedException {
        return this.pollFirst(timeout);
    }

    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        return this.pollFirst(timeout, unit);
    }

    @Override
    public E pollFirst() {
        this.lock.lock();
        try {
            E e = this.unlinkFirst();
            return e;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    E pollFirst(Duration timeout) throws InterruptedException {
        long nanos = timeout.toNanos();
        this.lock.lockInterruptibly();
        try {
            E x;
            while ((x = this.unlinkFirst()) == null) {
                if (nanos <= 0L) {
                    E e = null;
                    return e;
                }
                nanos = this.notEmpty.awaitNanos(nanos);
            }
            E e = x;
            return e;
        }
        finally {
            this.lock.unlock();
        }
    }

    public E pollFirst(long timeout, TimeUnit unit) throws InterruptedException {
        return this.pollFirst(PoolImplUtils.toDuration(timeout, unit));
    }

    @Override
    public E pollLast() {
        this.lock.lock();
        try {
            E e = this.unlinkLast();
            return e;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public E pollLast(Duration timeout) throws InterruptedException {
        long nanos = timeout.toNanos();
        this.lock.lockInterruptibly();
        try {
            E x;
            while ((x = this.unlinkLast()) == null) {
                if (nanos <= 0L) {
                    E e = null;
                    return e;
                }
                nanos = this.notEmpty.awaitNanos(nanos);
            }
            E e = x;
            return e;
        }
        finally {
            this.lock.unlock();
        }
    }

    public E pollLast(long timeout, TimeUnit unit) throws InterruptedException {
        return this.pollLast(PoolImplUtils.toDuration(timeout, unit));
    }

    @Override
    public E pop() {
        return this.removeFirst();
    }

    @Override
    public void push(E e) {
        this.addFirst(e);
    }

    public void put(E e) throws InterruptedException {
        this.putLast(e);
    }

    public void putFirst(E e) throws InterruptedException {
        Objects.requireNonNull(e, "e");
        this.lock.lock();
        try {
            while (!this.linkFirst(e)) {
                this.notFull.await();
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    public void putLast(E e) throws InterruptedException {
        Objects.requireNonNull(e, "e");
        this.lock.lock();
        try {
            while (!this.linkLast(e)) {
                this.notFull.await();
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        Object item;
        s.defaultReadObject();
        this.count = 0;
        this.first = null;
        this.last = null;
        while ((item = s.readObject()) != null) {
            this.add(item);
        }
    }

    public int remainingCapacity() {
        this.lock.lock();
        try {
            int n = this.capacity - this.count;
            return n;
        }
        finally {
            this.lock.unlock();
        }
    }

    @Override
    public E remove() {
        return this.removeFirst();
    }

    @Override
    public boolean remove(Object o) {
        return this.removeFirstOccurrence(o);
    }

    @Override
    public E removeFirst() {
        E x = this.pollFirst();
        if (x == null) {
            throw new NoSuchElementException();
        }
        return x;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean removeFirstOccurrence(Object o) {
        if (o == null) {
            return false;
        }
        this.lock.lock();
        try {
            Node<E> p = this.first;
            while (p != null) {
                if (o.equals(p.item)) {
                    this.unlink(p);
                    boolean bl = true;
                    return bl;
                }
                p = p.next;
            }
            boolean bl = false;
            return bl;
        }
        finally {
            this.lock.unlock();
        }
    }

    @Override
    public E removeLast() {
        E x = this.pollLast();
        if (x == null) {
            throw new NoSuchElementException();
        }
        return x;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean removeLastOccurrence(Object o) {
        if (o == null) {
            return false;
        }
        this.lock.lock();
        try {
            Node<E> p = this.last;
            while (p != null) {
                if (o.equals(p.item)) {
                    this.unlink(p);
                    boolean bl = true;
                    return bl;
                }
                p = p.prev;
            }
            boolean bl = false;
            return bl;
        }
        finally {
            this.lock.unlock();
        }
    }

    @Override
    public int size() {
        this.lock.lock();
        try {
            int n = this.count;
            return n;
        }
        finally {
            this.lock.unlock();
        }
    }

    public E take() throws InterruptedException {
        return this.takeFirst();
    }

    public E takeFirst() throws InterruptedException {
        this.lock.lock();
        try {
            E x;
            while ((x = this.unlinkFirst()) == null) {
                this.notEmpty.await();
            }
            E e = x;
            return e;
        }
        finally {
            this.lock.unlock();
        }
    }

    public E takeLast() throws InterruptedException {
        this.lock.lock();
        try {
            E x;
            while ((x = this.unlinkLast()) == null) {
                this.notEmpty.await();
            }
            E e = x;
            return e;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object[] toArray() {
        this.lock.lock();
        try {
            Object[] a = new Object[this.count];
            int k = 0;
            Node<E> p = this.first;
            while (p != null) {
                a[k++] = p.item;
                p = p.next;
            }
            Object[] objectArray = a;
            return objectArray;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public <T> T[] toArray(T[] a) {
        this.lock.lock();
        try {
            if (a.length < this.count) {
                a = (Object[])Array.newInstance(a.getClass().getComponentType(), this.count);
            }
            int k = 0;
            Node<E> p = this.first;
            while (p != null) {
                a[k++] = p.item;
                p = p.next;
            }
            if (a.length > k) {
                a[k] = null;
            }
            Object[] objectArray = a;
            return objectArray;
        }
        finally {
            this.lock.unlock();
        }
    }

    @Override
    public String toString() {
        this.lock.lock();
        try {
            String string = super.toString();
            return string;
        }
        finally {
            this.lock.unlock();
        }
    }

    private void unlink(Node<E> x) {
        Node p = x.prev;
        Node n = x.next;
        if (p == null) {
            this.unlinkFirst();
        } else if (n == null) {
            this.unlinkLast();
        } else {
            p.next = n;
            n.prev = p;
            x.item = null;
            --this.count;
            this.notFull.signal();
        }
    }

    private E unlinkFirst() {
        Node<E> f = this.first;
        if (f == null) {
            return null;
        }
        Node n = f.next;
        Object item = f.item;
        f.item = null;
        f.next = f;
        this.first = n;
        if (n == null) {
            this.last = null;
        } else {
            n.prev = null;
        }
        --this.count;
        this.notFull.signal();
        return item;
    }

    private E unlinkLast() {
        Node<E> l = this.last;
        if (l == null) {
            return null;
        }
        Node p = l.prev;
        Object item = l.item;
        l.item = null;
        l.prev = l;
        this.last = p;
        if (p == null) {
            this.first = null;
        } else {
            p.next = null;
        }
        --this.count;
        this.notFull.signal();
        return item;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        this.lock.lock();
        try {
            s.defaultWriteObject();
            Node<E> p = this.first;
            while (p != null) {
                s.writeObject(p.item);
                p = p.next;
            }
            s.writeObject(null);
        }
        finally {
            this.lock.unlock();
        }
    }

    private static final class Node<E> {
        E item;
        Node<E> prev;
        Node<E> next;

        Node(E x, Node<E> p, Node<E> n) {
            this.item = x;
            this.prev = p;
            this.next = n;
        }
    }

    private class DescendingItr
    extends AbstractItr {
        private DescendingItr() {
        }

        @Override
        Node<E> firstNode() {
            return LinkedBlockingDeque.this.last;
        }

        @Override
        Node<E> nextNode(Node<E> n) {
            return n.prev;
        }
    }

    private class Itr
    extends AbstractItr {
        private Itr() {
        }

        @Override
        Node<E> firstNode() {
            return LinkedBlockingDeque.this.first;
        }

        @Override
        Node<E> nextNode(Node<E> n) {
            return n.next;
        }
    }

    private abstract class AbstractItr
    implements Iterator<E> {
        Node<E> next;
        E nextItem;
        private Node<E> lastRet;

        AbstractItr() {
            LinkedBlockingDeque.this.lock.lock();
            try {
                this.next = this.firstNode();
                this.nextItem = this.next == null ? null : this.next.item;
            }
            finally {
                LinkedBlockingDeque.this.lock.unlock();
            }
        }

        void advance() {
            LinkedBlockingDeque.this.lock.lock();
            try {
                this.next = this.succ(this.next);
                this.nextItem = this.next == null ? null : this.next.item;
            }
            finally {
                LinkedBlockingDeque.this.lock.unlock();
            }
        }

        abstract Node<E> firstNode();

        @Override
        public boolean hasNext() {
            return this.next != null;
        }

        @Override
        public E next() {
            if (this.next == null) {
                throw new NoSuchElementException();
            }
            this.lastRet = this.next;
            Object x = this.nextItem;
            this.advance();
            return x;
        }

        abstract Node<E> nextNode(Node<E> var1);

        @Override
        public void remove() {
            Node n = this.lastRet;
            if (n == null) {
                throw new IllegalStateException();
            }
            this.lastRet = null;
            LinkedBlockingDeque.this.lock.lock();
            try {
                if (n.item != null) {
                    LinkedBlockingDeque.this.unlink(n);
                }
            }
            finally {
                LinkedBlockingDeque.this.lock.unlock();
            }
        }

        private Node<E> succ(Node<E> n) {
            Node s;
            while ((s = this.nextNode(n)) != null) {
                if (s.item != null) {
                    return s;
                }
                if (s == n) {
                    return this.firstNode();
                }
                n = s;
            }
            return null;
        }
    }
}

