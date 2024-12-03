/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections.BoundedMap;
import org.apache.commons.collections.collection.UnmodifiableCollection;
import org.apache.commons.collections.map.AbstractMapDecorator;
import org.apache.commons.collections.set.UnmodifiableSet;

public class FixedSizeMap
extends AbstractMapDecorator
implements Map,
BoundedMap,
Serializable {
    private static final long serialVersionUID = 7450927208116179316L;

    public static Map decorate(Map map) {
        return new FixedSizeMap(map);
    }

    protected FixedSizeMap(Map map) {
        super(map);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.map);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.map = (Map)in.readObject();
    }

    public Object put(Object key, Object value) {
        if (!this.map.containsKey(key)) {
            throw new IllegalArgumentException("Cannot put new key/value pair - Map is fixed size");
        }
        return this.map.put(key, value);
    }

    public void putAll(Map mapToCopy) {
        Iterator it = mapToCopy.keySet().iterator();
        while (it.hasNext()) {
            if (mapToCopy.containsKey(it.next())) continue;
            throw new IllegalArgumentException("Cannot put new key/value pair - Map is fixed size");
        }
        this.map.putAll(mapToCopy);
    }

    public void clear() {
        throw new UnsupportedOperationException("Map is fixed size");
    }

    public Object remove(Object key) {
        throw new UnsupportedOperationException("Map is fixed size");
    }

    public Set entrySet() {
        Set set = this.map.entrySet();
        return UnmodifiableSet.decorate(set);
    }

    public Set keySet() {
        Set set = this.map.keySet();
        return UnmodifiableSet.decorate(set);
    }

    public Collection values() {
        Collection coll = this.map.values();
        return UnmodifiableCollection.decorate(coll);
    }

    public boolean isFull() {
        return true;
    }

    public int maxSize() {
        return this.size();
    }
}

