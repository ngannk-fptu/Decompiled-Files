/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.bidimap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.OrderedBidiMap;
import org.apache.commons.collections.OrderedMap;
import org.apache.commons.collections.OrderedMapIterator;
import org.apache.commons.collections.ResettableIterator;
import org.apache.commons.collections.SortedBidiMap;
import org.apache.commons.collections.bidimap.AbstractDualBidiMap;
import org.apache.commons.collections.map.AbstractSortedMapDecorator;

public class DualTreeBidiMap
extends AbstractDualBidiMap
implements SortedBidiMap,
Serializable {
    private static final long serialVersionUID = 721969328361809L;
    protected final Comparator comparator;

    public DualTreeBidiMap() {
        super(new TreeMap(), new TreeMap());
        this.comparator = null;
    }

    public DualTreeBidiMap(Map map) {
        super(new TreeMap(), new TreeMap());
        this.putAll(map);
        this.comparator = null;
    }

    public DualTreeBidiMap(Comparator comparator) {
        super(new TreeMap(comparator), new TreeMap(comparator));
        this.comparator = comparator;
    }

    protected DualTreeBidiMap(Map normalMap, Map reverseMap, BidiMap inverseBidiMap) {
        super(normalMap, reverseMap, inverseBidiMap);
        this.comparator = ((SortedMap)normalMap).comparator();
    }

    protected BidiMap createBidiMap(Map normalMap, Map reverseMap, BidiMap inverseMap) {
        return new DualTreeBidiMap(normalMap, reverseMap, inverseMap);
    }

    public Comparator comparator() {
        return ((SortedMap)this.maps[0]).comparator();
    }

    public Object firstKey() {
        return ((SortedMap)this.maps[0]).firstKey();
    }

    public Object lastKey() {
        return ((SortedMap)this.maps[0]).lastKey();
    }

    public Object nextKey(Object key) {
        if (this.isEmpty()) {
            return null;
        }
        if (this.maps[0] instanceof OrderedMap) {
            return ((OrderedMap)this.maps[0]).nextKey(key);
        }
        SortedMap sm = (SortedMap)this.maps[0];
        Iterator<Object> it = sm.tailMap(key).keySet().iterator();
        it.next();
        if (it.hasNext()) {
            return it.next();
        }
        return null;
    }

    public Object previousKey(Object key) {
        if (this.isEmpty()) {
            return null;
        }
        if (this.maps[0] instanceof OrderedMap) {
            return ((OrderedMap)this.maps[0]).previousKey(key);
        }
        SortedMap sm = (SortedMap)this.maps[0];
        SortedMap hm = sm.headMap(key);
        if (hm.isEmpty()) {
            return null;
        }
        return hm.lastKey();
    }

    public OrderedMapIterator orderedMapIterator() {
        return new BidiOrderedMapIterator(this);
    }

    public SortedBidiMap inverseSortedBidiMap() {
        return (SortedBidiMap)this.inverseBidiMap();
    }

    public OrderedBidiMap inverseOrderedBidiMap() {
        return (OrderedBidiMap)this.inverseBidiMap();
    }

    public SortedMap headMap(Object toKey) {
        SortedMap sub = ((SortedMap)this.maps[0]).headMap(toKey);
        return new ViewMap(this, sub);
    }

    public SortedMap tailMap(Object fromKey) {
        SortedMap sub = ((SortedMap)this.maps[0]).tailMap(fromKey);
        return new ViewMap(this, sub);
    }

    public SortedMap subMap(Object fromKey, Object toKey) {
        SortedMap sub = ((SortedMap)this.maps[0]).subMap(fromKey, toKey);
        return new ViewMap(this, sub);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.maps[0]);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.maps[0] = new TreeMap(this.comparator);
        this.maps[1] = new TreeMap(this.comparator);
        Map map = (Map)in.readObject();
        this.putAll(map);
    }

    protected static class BidiOrderedMapIterator
    implements OrderedMapIterator,
    ResettableIterator {
        protected final AbstractDualBidiMap parent;
        protected ListIterator iterator;
        private Map.Entry last = null;

        protected BidiOrderedMapIterator(AbstractDualBidiMap parent) {
            this.parent = parent;
            this.iterator = new ArrayList(parent.entrySet()).listIterator();
        }

        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        public Object next() {
            this.last = (Map.Entry)this.iterator.next();
            return this.last.getKey();
        }

        public boolean hasPrevious() {
            return this.iterator.hasPrevious();
        }

        public Object previous() {
            this.last = (Map.Entry)this.iterator.previous();
            return this.last.getKey();
        }

        public void remove() {
            this.iterator.remove();
            this.parent.remove(this.last.getKey());
            this.last = null;
        }

        public Object getKey() {
            if (this.last == null) {
                throw new IllegalStateException("Iterator getKey() can only be called after next() and before remove()");
            }
            return this.last.getKey();
        }

        public Object getValue() {
            if (this.last == null) {
                throw new IllegalStateException("Iterator getValue() can only be called after next() and before remove()");
            }
            return this.last.getValue();
        }

        public Object setValue(Object value) {
            if (this.last == null) {
                throw new IllegalStateException("Iterator setValue() can only be called after next() and before remove()");
            }
            if (this.parent.maps[1].containsKey(value) && this.parent.maps[1].get(value) != this.last.getKey()) {
                throw new IllegalArgumentException("Cannot use setValue() when the object being set is already in the map");
            }
            return this.parent.put(this.last.getKey(), value);
        }

        public void reset() {
            this.iterator = new ArrayList(this.parent.entrySet()).listIterator();
            this.last = null;
        }

        public String toString() {
            if (this.last != null) {
                return "MapIterator[" + this.getKey() + "=" + this.getValue() + "]";
            }
            return "MapIterator[]";
        }
    }

    protected static class ViewMap
    extends AbstractSortedMapDecorator {
        final DualTreeBidiMap bidi;

        protected ViewMap(DualTreeBidiMap bidi, SortedMap sm) {
            super((SortedMap)((Object)bidi.createBidiMap(sm, bidi.maps[1], bidi.inverseBidiMap)));
            this.bidi = (DualTreeBidiMap)this.map;
        }

        public boolean containsValue(Object value) {
            return this.bidi.maps[0].containsValue(value);
        }

        public void clear() {
            Iterator it = this.keySet().iterator();
            while (it.hasNext()) {
                it.next();
                it.remove();
            }
        }

        public SortedMap headMap(Object toKey) {
            return new ViewMap(this.bidi, super.headMap(toKey));
        }

        public SortedMap tailMap(Object fromKey) {
            return new ViewMap(this.bidi, super.tailMap(fromKey));
        }

        public SortedMap subMap(Object fromKey, Object toKey) {
            return new ViewMap(this.bidi, super.subMap(fromKey, toKey));
        }
    }
}

