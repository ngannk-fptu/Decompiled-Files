/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool2.impl;

import java.lang.ref.SoftReference;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class PooledSoftReference<T>
extends DefaultPooledObject<T> {
    private volatile SoftReference<T> reference;

    public PooledSoftReference(SoftReference<T> reference) {
        super(null);
        this.reference = reference;
    }

    @Override
    public T getObject() {
        return this.reference.get();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Referenced Object: ");
        result.append(this.getObject().toString());
        result.append(", State: ");
        PooledSoftReference pooledSoftReference = this;
        synchronized (pooledSoftReference) {
            result.append(this.getState().toString());
        }
        return result.toString();
    }

    public synchronized SoftReference<T> getReference() {
        return this.reference;
    }

    public synchronized void setReference(SoftReference<T> reference) {
        this.reference = reference;
    }
}

