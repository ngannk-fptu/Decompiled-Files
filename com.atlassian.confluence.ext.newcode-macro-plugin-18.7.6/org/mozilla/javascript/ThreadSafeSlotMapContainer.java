/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import java.util.Iterator;
import java.util.concurrent.locks.StampedLock;
import org.mozilla.javascript.Slot;
import org.mozilla.javascript.SlotMapContainer;

class ThreadSafeSlotMapContainer
extends SlotMapContainer {
    private final StampedLock lock = new StampedLock();

    ThreadSafeSlotMapContainer() {
    }

    ThreadSafeSlotMapContainer(int initialSize) {
        super(initialSize);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int size() {
        long stamp = this.lock.tryOptimisticRead();
        int s = this.map.size();
        if (this.lock.validate(stamp)) {
            return s;
        }
        stamp = this.lock.readLock();
        try {
            int n = this.map.size();
            return n;
        }
        finally {
            this.lock.unlockRead(stamp);
        }
    }

    @Override
    public int dirtySize() {
        assert (this.lock.isReadLocked());
        return this.map.size();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isEmpty() {
        long stamp = this.lock.tryOptimisticRead();
        boolean e = this.map.isEmpty();
        if (this.lock.validate(stamp)) {
            return e;
        }
        stamp = this.lock.readLock();
        try {
            boolean bl = this.map.isEmpty();
            return bl;
        }
        finally {
            this.lock.unlockRead(stamp);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Slot modify(Object key, int index, int attributes) {
        long stamp = this.lock.writeLock();
        try {
            this.checkMapSize();
            Slot slot = this.map.modify(key, index, attributes);
            return slot;
        }
        finally {
            this.lock.unlockWrite(stamp);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void replace(Slot oldSlot, Slot newSlot) {
        long stamp = this.lock.writeLock();
        try {
            this.map.replace(oldSlot, newSlot);
        }
        finally {
            this.lock.unlockWrite(stamp);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Slot query(Object key, int index) {
        long stamp = this.lock.tryOptimisticRead();
        Slot s = this.map.query(key, index);
        if (this.lock.validate(stamp)) {
            return s;
        }
        stamp = this.lock.readLock();
        try {
            Slot slot = this.map.query(key, index);
            return slot;
        }
        finally {
            this.lock.unlockRead(stamp);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void add(Slot newSlot) {
        long stamp = this.lock.writeLock();
        try {
            this.checkMapSize();
            this.map.add(newSlot);
        }
        finally {
            this.lock.unlockWrite(stamp);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void remove(Object key, int index) {
        long stamp = this.lock.writeLock();
        try {
            this.map.remove(key, index);
        }
        finally {
            this.lock.unlockWrite(stamp);
        }
    }

    @Override
    public long readLock() {
        return this.lock.readLock();
    }

    @Override
    public void unlockRead(long stamp) {
        this.lock.unlockRead(stamp);
    }

    @Override
    public Iterator<Slot> iterator() {
        assert (this.lock.isReadLocked());
        return this.map.iterator();
    }

    @Override
    protected void checkMapSize() {
        assert (this.lock.isWriteLocked());
        super.checkMapSize();
    }
}

