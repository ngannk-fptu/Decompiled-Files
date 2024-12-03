/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;
import org.apache.commons.collections.SequencedHashMap;

public class LRUMap
extends SequencedHashMap
implements Externalizable {
    private int maximumSize = 0;
    private static final long serialVersionUID = 2197433140769957051L;

    public LRUMap() {
        this(100);
    }

    public LRUMap(int i) {
        super(i);
        this.maximumSize = i;
    }

    public Object get(Object key) {
        if (!this.containsKey(key)) {
            return null;
        }
        Object value = this.remove(key);
        super.put(key, value);
        return value;
    }

    public Object put(Object key, Object value) {
        int mapSize = this.size();
        Object retval = null;
        if (mapSize >= this.maximumSize && !this.containsKey(key)) {
            this.removeLRU();
        }
        retval = super.put(key, value);
        return retval;
    }

    protected void removeLRU() {
        Object key = this.getFirstKey();
        Object value = super.get(key);
        this.remove(key);
        this.processRemovedLRU(key, value);
    }

    protected void processRemovedLRU(Object key, Object value) {
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.maximumSize = in.readInt();
        int size = in.readInt();
        for (int i = 0; i < size; ++i) {
            Object key = in.readObject();
            Object value = in.readObject();
            this.put(key, value);
        }
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(this.maximumSize);
        out.writeInt(this.size());
        Iterator iterator = this.keySet().iterator();
        while (iterator.hasNext()) {
            Object key = iterator.next();
            out.writeObject(key);
            Object value = super.get(key);
            out.writeObject(value);
        }
    }

    public int getMaximumSize() {
        return this.maximumSize;
    }

    public void setMaximumSize(int maximumSize) {
        this.maximumSize = maximumSize;
        while (this.size() > maximumSize) {
            this.removeLRU();
        }
    }
}

