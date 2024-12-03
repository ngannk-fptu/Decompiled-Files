/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.pool2.impl;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.apache.tomcat.dbcp.pool2.BaseObjectPool;
import org.apache.tomcat.dbcp.pool2.PoolUtils;
import org.apache.tomcat.dbcp.pool2.PooledObject;
import org.apache.tomcat.dbcp.pool2.PooledObjectFactory;
import org.apache.tomcat.dbcp.pool2.impl.LinkedBlockingDeque;
import org.apache.tomcat.dbcp.pool2.impl.PooledSoftReference;

public class SoftReferenceObjectPool<T>
extends BaseObjectPool<T> {
    private final PooledObjectFactory<T> factory;
    private final ReferenceQueue<T> refQueue = new ReferenceQueue();
    private int numActive;
    private long destroyCount;
    private long createCount;
    private final LinkedBlockingDeque<PooledSoftReference<T>> idleReferences = new LinkedBlockingDeque();
    private final ArrayList<PooledSoftReference<T>> allReferences = new ArrayList();

    public SoftReferenceObjectPool(PooledObjectFactory<T> factory) {
        this.factory = factory;
    }

    @Override
    public synchronized void addObject() throws Exception {
        boolean shouldDestroy;
        this.assertOpen();
        if (this.factory == null) {
            throw new IllegalStateException("Cannot add objects without a factory.");
        }
        T obj = this.factory.makeObject().getObject();
        ++this.createCount;
        PooledSoftReference<T> ref = new PooledSoftReference<T>(new SoftReference<T>(obj, this.refQueue));
        this.allReferences.add(ref);
        boolean success = true;
        if (!this.factory.validateObject(ref)) {
            success = false;
        } else {
            this.factory.passivateObject(ref);
        }
        boolean bl = shouldDestroy = !success;
        if (success) {
            this.idleReferences.add(ref);
            this.notifyAll();
        }
        if (shouldDestroy) {
            try {
                this.destroy(ref);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized T borrowObject() throws Exception {
        this.assertOpen();
        Object obj = null;
        boolean newlyCreated = false;
        PooledSoftReference<Object> ref = null;
        while (null == obj) {
            if (this.idleReferences.isEmpty()) {
                if (null == this.factory) {
                    throw new NoSuchElementException();
                }
                newlyCreated = true;
                obj = this.factory.makeObject().getObject();
                ++this.createCount;
                ref = new PooledSoftReference<Object>(new SoftReference<Object>(obj));
                this.allReferences.add(ref);
            } else {
                ref = this.idleReferences.pollFirst();
                obj = ref.getObject();
                ref.getReference().clear();
                ref.setReference(new SoftReference<Object>(obj));
            }
            if (null == this.factory || null == obj) continue;
            try {
                this.factory.activateObject(ref);
                if (this.factory.validateObject(ref)) continue;
                throw new Exception("ValidateObject failed");
            }
            catch (Throwable t) {
                PoolUtils.checkRethrow(t);
                try {
                    this.destroy(ref);
                }
                catch (Throwable t2) {
                    PoolUtils.checkRethrow(t2);
                }
                finally {
                    obj = null;
                }
                if (!newlyCreated) continue;
                throw new NoSuchElementException("Could not create a validated object, cause: " + t);
            }
        }
        ++this.numActive;
        ref.allocate();
        return obj;
    }

    @Override
    public synchronized void clear() {
        if (null != this.factory) {
            this.idleReferences.forEach(ref -> {
                try {
                    if (null != ref.getObject()) {
                        this.factory.destroyObject((PooledObject<T>)ref);
                    }
                }
                catch (Exception exception) {
                    // empty catch block
                }
            });
        }
        this.idleReferences.clear();
        this.pruneClearedReferences();
    }

    @Override
    public void close() {
        super.close();
        this.clear();
    }

    private void destroy(PooledSoftReference<T> toDestroy) throws Exception {
        toDestroy.invalidate();
        this.idleReferences.remove(toDestroy);
        this.allReferences.remove(toDestroy);
        try {
            this.factory.destroyObject(toDestroy);
        }
        finally {
            ++this.destroyCount;
            toDestroy.getReference().clear();
        }
    }

    private PooledSoftReference<T> findReference(T obj) {
        Optional<PooledSoftReference> first = this.allReferences.stream().filter(reference -> reference.getObject() != null && reference.getObject().equals(obj)).findFirst();
        return first.orElse(null);
    }

    public synchronized PooledObjectFactory<T> getFactory() {
        return this.factory;
    }

    @Override
    public synchronized int getNumActive() {
        return this.numActive;
    }

    @Override
    public synchronized int getNumIdle() {
        this.pruneClearedReferences();
        return this.idleReferences.size();
    }

    @Override
    public synchronized void invalidateObject(T obj) throws Exception {
        PooledSoftReference<T> ref = this.findReference(obj);
        if (ref == null) {
            throw new IllegalStateException("Object to invalidate is not currently part of this pool");
        }
        if (this.factory != null) {
            this.destroy(ref);
        }
        --this.numActive;
        this.notifyAll();
    }

    private void pruneClearedReferences() {
        this.removeClearedReferences(this.idleReferences.iterator());
        this.removeClearedReferences(this.allReferences.iterator());
        while (this.refQueue.poll() != null) {
        }
    }

    private void removeClearedReferences(Iterator<PooledSoftReference<T>> iterator) {
        while (iterator.hasNext()) {
            PooledSoftReference<T> ref = iterator.next();
            if (ref.getReference() != null && !ref.getReference().isEnqueued()) continue;
            iterator.remove();
        }
    }

    @Override
    public synchronized void returnObject(T obj) throws Exception {
        boolean success = !this.isClosed();
        PooledSoftReference<T> ref = this.findReference(obj);
        if (ref == null) {
            throw new IllegalStateException("Returned object not currently part of this pool");
        }
        if (this.factory != null) {
            if (!this.factory.validateObject(ref)) {
                success = false;
            } else {
                try {
                    this.factory.passivateObject(ref);
                }
                catch (Exception e) {
                    success = false;
                }
            }
        }
        boolean shouldDestroy = !success;
        --this.numActive;
        if (success) {
            ref.deallocate();
            this.idleReferences.add(ref);
        }
        this.notifyAll();
        if (shouldDestroy && this.factory != null) {
            try {
                this.destroy(ref);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    @Override
    protected void toStringAppendFields(StringBuilder builder) {
        super.toStringAppendFields(builder);
        builder.append(", factory=");
        builder.append(this.factory);
        builder.append(", refQueue=");
        builder.append(this.refQueue);
        builder.append(", numActive=");
        builder.append(this.numActive);
        builder.append(", destroyCount=");
        builder.append(this.destroyCount);
        builder.append(", createCount=");
        builder.append(this.createCount);
        builder.append(", idleReferences=");
        builder.append(this.idleReferences);
        builder.append(", allReferences=");
        builder.append(this.allReferences);
    }
}

