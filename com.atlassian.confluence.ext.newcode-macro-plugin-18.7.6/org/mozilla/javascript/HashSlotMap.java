/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import java.util.Iterator;
import java.util.LinkedHashMap;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Slot;
import org.mozilla.javascript.SlotMap;

public class HashSlotMap
implements SlotMap {
    private final LinkedHashMap<Object, Slot> map = new LinkedHashMap();

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public Slot query(Object key, int index) {
        Object name = this.makeKey(key, index);
        return this.map.get(name);
    }

    @Override
    public Slot modify(Object key, int index, int attributes) {
        Object name = this.makeKey(key, index);
        Slot slot = this.map.get(name);
        if (slot != null) {
            return slot;
        }
        return this.createSlot(key, index, attributes);
    }

    @Override
    public void replace(Slot oldSlot, Slot newSlot) {
        Object name = this.makeKey(oldSlot);
        this.map.put(name, newSlot);
    }

    private Slot createSlot(Object key, int index, int attributes) {
        Slot newSlot = new Slot(key, index, attributes);
        this.add(newSlot);
        return newSlot;
    }

    @Override
    public void add(Slot newSlot) {
        Object name = this.makeKey(newSlot);
        this.map.put(name, newSlot);
    }

    @Override
    public void remove(Object key, int index) {
        Object name = this.makeKey(key, index);
        Slot slot = this.map.get(name);
        if (slot != null) {
            if ((slot.getAttributes() & 4) != 0) {
                Context cx = Context.getContext();
                if (cx.isStrictMode()) {
                    throw ScriptRuntime.typeErrorById("msg.delete.property.with.configurable.false", key);
                }
                return;
            }
            this.map.remove(name);
        }
    }

    @Override
    public Iterator<Slot> iterator() {
        return this.map.values().iterator();
    }

    private Object makeKey(Object name, int index) {
        return name == null ? String.valueOf(index) : name;
    }

    private Object makeKey(Slot slot) {
        return slot.name == null ? String.valueOf(slot.indexOrHash) : slot.name;
    }
}

