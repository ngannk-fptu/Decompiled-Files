/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Slot;
import org.mozilla.javascript.SlotMap;

public class EmbeddedSlotMap
implements SlotMap {
    private Slot[] slots;
    private Slot firstAdded;
    private Slot lastAdded;
    private int count;
    private static final int INITIAL_SLOT_SIZE = 4;

    @Override
    public int size() {
        return this.count;
    }

    @Override
    public boolean isEmpty() {
        return this.count == 0;
    }

    @Override
    public Iterator<Slot> iterator() {
        return new Iter(this.firstAdded);
    }

    @Override
    public Slot query(Object key, int index) {
        if (this.slots == null) {
            return null;
        }
        int indexOrHash = key != null ? key.hashCode() : index;
        int slotIndex = EmbeddedSlotMap.getSlotIndex(this.slots.length, indexOrHash);
        Slot slot = this.slots[slotIndex];
        while (slot != null) {
            if (indexOrHash == slot.indexOrHash && Objects.equals(slot.name, key)) {
                return slot;
            }
            slot = slot.next;
        }
        return null;
    }

    @Override
    public Slot modify(Object key, int index, int attributes) {
        int indexOrHash;
        int n = indexOrHash = key != null ? key.hashCode() : index;
        if (this.slots != null) {
            int slotIndex = EmbeddedSlotMap.getSlotIndex(this.slots.length, indexOrHash);
            Slot slot = this.slots[slotIndex];
            while (!(slot == null || indexOrHash == slot.indexOrHash && Objects.equals(slot.name, key))) {
                slot = slot.next;
            }
            if (slot != null) {
                return slot;
            }
        }
        return this.createSlot(key, indexOrHash, attributes);
    }

    private Slot createSlot(Object key, int indexOrHash, int attributes) {
        if (this.count == 0) {
            this.slots = new Slot[4];
        }
        if (4 * (this.count + 1) > 3 * this.slots.length) {
            Slot[] newSlots = new Slot[this.slots.length * 2];
            EmbeddedSlotMap.copyTable(this.slots, newSlots);
            this.slots = newSlots;
        }
        Slot newSlot = new Slot(key, indexOrHash, attributes);
        this.insertNewSlot(newSlot);
        return newSlot;
    }

    @Override
    public void replace(Slot oldSlot, Slot newSlot) {
        Slot prev;
        int insertPos = EmbeddedSlotMap.getSlotIndex(this.slots.length, oldSlot.indexOrHash);
        Slot tmpSlot = prev = this.slots[insertPos];
        while (tmpSlot != null && tmpSlot != oldSlot) {
            prev = tmpSlot;
            tmpSlot = tmpSlot.next;
        }
        assert (tmpSlot == oldSlot);
        if (prev == oldSlot) {
            this.slots[insertPos] = newSlot;
        } else {
            prev.next = newSlot;
        }
        newSlot.next = oldSlot.next;
        if (oldSlot == this.firstAdded) {
            this.firstAdded = newSlot;
        } else {
            Slot ps = this.firstAdded;
            while (ps != null && ps.orderedNext != oldSlot) {
                ps = ps.orderedNext;
            }
            if (ps != null) {
                ps.orderedNext = newSlot;
            }
        }
        newSlot.orderedNext = oldSlot.orderedNext;
        if (oldSlot == this.lastAdded) {
            this.lastAdded = newSlot;
        }
    }

    @Override
    public void add(Slot newSlot) {
        if (this.slots == null) {
            this.slots = new Slot[4];
        }
        this.insertNewSlot(newSlot);
    }

    private void insertNewSlot(Slot newSlot) {
        ++this.count;
        if (this.lastAdded != null) {
            this.lastAdded.orderedNext = newSlot;
        }
        if (this.firstAdded == null) {
            this.firstAdded = newSlot;
        }
        this.lastAdded = newSlot;
        EmbeddedSlotMap.addKnownAbsentSlot(this.slots, newSlot);
    }

    @Override
    public void remove(Object key, int index) {
        int indexOrHash;
        int n = indexOrHash = key != null ? key.hashCode() : index;
        if (this.count != 0) {
            Slot prev;
            int slotIndex = EmbeddedSlotMap.getSlotIndex(this.slots.length, indexOrHash);
            Slot slot = prev = this.slots[slotIndex];
            while (!(slot == null || slot.indexOrHash == indexOrHash && Objects.equals(slot.name, key))) {
                prev = slot;
                slot = slot.next;
            }
            if (slot != null) {
                if ((slot.getAttributes() & 4) != 0) {
                    Context cx = Context.getContext();
                    if (cx.isStrictMode()) {
                        throw ScriptRuntime.typeErrorById("msg.delete.property.with.configurable.false", key);
                    }
                    return;
                }
                --this.count;
                if (prev == slot) {
                    this.slots[slotIndex] = slot.next;
                } else {
                    prev.next = slot.next;
                }
                if (slot == this.firstAdded) {
                    prev = null;
                    this.firstAdded = slot.orderedNext;
                } else {
                    prev = this.firstAdded;
                    while (prev.orderedNext != slot) {
                        prev = prev.orderedNext;
                    }
                    prev.orderedNext = slot.orderedNext;
                }
                if (slot == this.lastAdded) {
                    this.lastAdded = prev;
                }
            }
        }
    }

    private static void copyTable(Slot[] oldSlots, Slot[] newSlots) {
        for (Slot slot : oldSlots) {
            while (slot != null) {
                Slot nextSlot = slot.next;
                slot.next = null;
                EmbeddedSlotMap.addKnownAbsentSlot(newSlots, slot);
                slot = nextSlot;
            }
        }
    }

    private static void addKnownAbsentSlot(Slot[] addSlots, Slot slot) {
        int insertPos = EmbeddedSlotMap.getSlotIndex(addSlots.length, slot.indexOrHash);
        Slot old = addSlots[insertPos];
        addSlots[insertPos] = slot;
        slot.next = old;
    }

    private static int getSlotIndex(int tableSize, int indexOrHash) {
        return indexOrHash & tableSize - 1;
    }

    private static final class Iter
    implements Iterator<Slot> {
        private Slot next;

        Iter(Slot slot) {
            this.next = slot;
        }

        @Override
        public boolean hasNext() {
            return this.next != null;
        }

        @Override
        public Slot next() {
            Slot ret = this.next;
            if (ret == null) {
                throw new NoSuchElementException();
            }
            this.next = this.next.orderedNext;
            return ret;
        }
    }
}

