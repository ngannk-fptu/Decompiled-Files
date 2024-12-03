/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.buffer;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.BufferUnderflowException;
import org.apache.commons.collections.buffer.SynchronizedBuffer;

public class BlockingBuffer
extends SynchronizedBuffer {
    private static final long serialVersionUID = 1719328905017860541L;
    private final long timeout;

    public static Buffer decorate(Buffer buffer) {
        return new BlockingBuffer(buffer);
    }

    public static Buffer decorate(Buffer buffer, long timeoutMillis) {
        return new BlockingBuffer(buffer, timeoutMillis);
    }

    protected BlockingBuffer(Buffer buffer) {
        super(buffer);
        this.timeout = 0L;
    }

    protected BlockingBuffer(Buffer buffer, long timeoutMillis) {
        super(buffer);
        this.timeout = timeoutMillis < 0L ? 0L : timeoutMillis;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean add(Object o) {
        Object object = this.lock;
        synchronized (object) {
            boolean result = this.collection.add(o);
            this.lock.notifyAll();
            return result;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean addAll(Collection c) {
        Object object = this.lock;
        synchronized (object) {
            boolean result = this.collection.addAll(c);
            this.lock.notifyAll();
            return result;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object get() {
        Object object = this.lock;
        synchronized (object) {
            while (this.collection.isEmpty()) {
                try {
                    if (this.timeout <= 0L) {
                        this.lock.wait();
                        continue;
                    }
                    return this.get(this.timeout);
                }
                catch (InterruptedException e) {
                    PrintWriter out = new PrintWriter(new StringWriter());
                    e.printStackTrace(out);
                    throw new BufferUnderflowException("Caused by InterruptedException: " + out.toString());
                }
            }
            return this.getBuffer().get();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object get(long timeout) {
        Object object = this.lock;
        synchronized (object) {
            long expiration = System.currentTimeMillis() + timeout;
            long timeLeft = expiration - System.currentTimeMillis();
            while (timeLeft > 0L && this.collection.isEmpty()) {
                try {
                    this.lock.wait(timeLeft);
                    timeLeft = expiration - System.currentTimeMillis();
                }
                catch (InterruptedException e) {
                    PrintWriter out = new PrintWriter(new StringWriter());
                    e.printStackTrace(out);
                    throw new BufferUnderflowException("Caused by InterruptedException: " + out.toString());
                }
            }
            if (this.collection.isEmpty()) {
                throw new BufferUnderflowException("Timeout expired");
            }
            return this.getBuffer().get();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object remove() {
        Object object = this.lock;
        synchronized (object) {
            while (this.collection.isEmpty()) {
                try {
                    if (this.timeout <= 0L) {
                        this.lock.wait();
                        continue;
                    }
                    return this.remove(this.timeout);
                }
                catch (InterruptedException e) {
                    PrintWriter out = new PrintWriter(new StringWriter());
                    e.printStackTrace(out);
                    throw new BufferUnderflowException("Caused by InterruptedException: " + out.toString());
                }
            }
            return this.getBuffer().remove();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object remove(long timeout) {
        Object object = this.lock;
        synchronized (object) {
            long expiration = System.currentTimeMillis() + timeout;
            long timeLeft = expiration - System.currentTimeMillis();
            while (timeLeft > 0L && this.collection.isEmpty()) {
                try {
                    this.lock.wait(timeLeft);
                    timeLeft = expiration - System.currentTimeMillis();
                }
                catch (InterruptedException e) {
                    PrintWriter out = new PrintWriter(new StringWriter());
                    e.printStackTrace(out);
                    throw new BufferUnderflowException("Caused by InterruptedException: " + out.toString());
                }
            }
            if (this.collection.isEmpty()) {
                throw new BufferUnderflowException("Timeout expired");
            }
            return this.getBuffer().remove();
        }
    }
}

