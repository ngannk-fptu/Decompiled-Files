/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.buffer;

import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.collection.SynchronizedCollection;

public class SynchronizedBuffer
extends SynchronizedCollection
implements Buffer {
    private static final long serialVersionUID = -6859936183953626253L;

    public static Buffer decorate(Buffer buffer) {
        return new SynchronizedBuffer(buffer);
    }

    protected SynchronizedBuffer(Buffer buffer) {
        super(buffer);
    }

    protected SynchronizedBuffer(Buffer buffer, Object lock) {
        super(buffer, lock);
    }

    protected Buffer getBuffer() {
        return (Buffer)this.collection;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object get() {
        Object object = this.lock;
        synchronized (object) {
            return this.getBuffer().get();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object remove() {
        Object object = this.lock;
        synchronized (object) {
            return this.getBuffer().remove();
        }
    }
}

