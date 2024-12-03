/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import java.util.Iterator;
import org.mozilla.javascript.EmbeddedSlotMap;
import org.mozilla.javascript.HashSlotMap;
import org.mozilla.javascript.Slot;
import org.mozilla.javascript.SlotMap;

class SlotMapContainer
implements SlotMap {
    private static final int LARGE_HASH_SIZE = 2000;
    private static final int DEFAULT_SIZE = 10;
    protected SlotMap map;

    SlotMapContainer() {
        this(10);
    }

    SlotMapContainer(int initialSize) {
        this.map = initialSize > 2000 ? new HashSlotMap() : new EmbeddedSlotMap();
    }

    @Override
    public int size() {
        return this.map.size();
    }

    public int dirtySize() {
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public Slot modify(Object key, int index, int attributes) {
        this.checkMapSize();
        return this.map.modify(key, index, attributes);
    }

    @Override
    public void replace(Slot oldSlot, Slot newSlot) {
        this.map.replace(oldSlot, newSlot);
    }

    @Override
    public Slot query(Object key, int index) {
        return this.map.query(key, index);
    }

    @Override
    public void add(Slot newSlot) {
        this.checkMapSize();
        this.map.add(newSlot);
    }

    @Override
    public void remove(Object key, int index) {
        this.map.remove(key, index);
    }

    @Override
    public Iterator<Slot> iterator() {
        return this.map.iterator();
    }

    public long readLock() {
        return 0L;
    }

    public void unlockRead(long stamp) {
    }

    protected void checkMapSize() {
        if (this.map instanceof EmbeddedSlotMap && this.map.size() >= 2000) {
            HashSlotMap newMap = new HashSlotMap();
            for (Slot s : this.map) {
                newMap.add(s);
            }
            this.map = newMap;
        }
    }
}

