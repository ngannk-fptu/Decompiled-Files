/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.buffer;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;
import org.apache.commons.collections.BoundedCollection;
import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.BufferOverflowException;
import org.apache.commons.collections.BufferUnderflowException;
import org.apache.commons.collections.buffer.SynchronizedBuffer;
import org.apache.commons.collections.iterators.AbstractIteratorDecorator;

public class BoundedBuffer
extends SynchronizedBuffer
implements BoundedCollection {
    private static final long serialVersionUID = 1536432911093974264L;
    private final int maximumSize;
    private final long timeout;

    public static BoundedBuffer decorate(Buffer buffer, int maximumSize) {
        return new BoundedBuffer(buffer, maximumSize, 0L);
    }

    public static BoundedBuffer decorate(Buffer buffer, int maximumSize, long timeout) {
        return new BoundedBuffer(buffer, maximumSize, timeout);
    }

    protected BoundedBuffer(Buffer buffer, int maximumSize, long timeout) {
        super(buffer);
        if (maximumSize < 1) {
            throw new IllegalArgumentException();
        }
        this.maximumSize = maximumSize;
        this.timeout = timeout;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object remove() {
        Object object = this.lock;
        synchronized (object) {
            Object returnValue = this.getBuffer().remove();
            this.lock.notifyAll();
            return returnValue;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean add(Object o) {
        Object object = this.lock;
        synchronized (object) {
            this.timeoutWait(1);
            return this.getBuffer().add(o);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean addAll(Collection c) {
        Object object = this.lock;
        synchronized (object) {
            this.timeoutWait(c.size());
            return this.getBuffer().addAll(c);
        }
    }

    public Iterator iterator() {
        return new NotifyingIterator(this.collection.iterator());
    }

    private void timeoutWait(int nAdditions) {
        if (nAdditions > this.maximumSize) {
            throw new BufferOverflowException("Buffer size cannot exceed " + this.maximumSize);
        }
        if (this.timeout <= 0L) {
            if (this.getBuffer().size() + nAdditions > this.maximumSize) {
                throw new BufferOverflowException("Buffer size cannot exceed " + this.maximumSize);
            }
            return;
        }
        long expiration = System.currentTimeMillis() + this.timeout;
        long timeLeft = expiration - System.currentTimeMillis();
        while (timeLeft > 0L && this.getBuffer().size() + nAdditions > this.maximumSize) {
            try {
                this.lock.wait(timeLeft);
                timeLeft = expiration - System.currentTimeMillis();
            }
            catch (InterruptedException ex) {
                PrintWriter out = new PrintWriter(new StringWriter());
                ex.printStackTrace(out);
                throw new BufferUnderflowException("Caused by InterruptedException: " + out.toString());
            }
        }
        if (this.getBuffer().size() + nAdditions > this.maximumSize) {
            throw new BufferOverflowException("Timeout expired");
        }
    }

    public boolean isFull() {
        return this.size() == this.maxSize();
    }

    public int maxSize() {
        return this.maximumSize;
    }

    private class NotifyingIterator
    extends AbstractIteratorDecorator {
        public NotifyingIterator(Iterator it) {
            super(it);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void remove() {
            Object object = BoundedBuffer.this.lock;
            synchronized (object) {
                this.iterator.remove();
                BoundedBuffer.this.lock.notifyAll();
            }
        }
    }
}

