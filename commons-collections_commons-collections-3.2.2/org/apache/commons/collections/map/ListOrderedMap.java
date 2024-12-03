/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.OrderedMap;
import org.apache.commons.collections.OrderedMapIterator;
import org.apache.commons.collections.ResettableIterator;
import org.apache.commons.collections.iterators.AbstractIteratorDecorator;
import org.apache.commons.collections.keyvalue.AbstractMapEntry;
import org.apache.commons.collections.list.UnmodifiableList;
import org.apache.commons.collections.map.AbstractMapDecorator;

public class ListOrderedMap
extends AbstractMapDecorator
implements OrderedMap,
Serializable {
    private static final long serialVersionUID = 2728177751851003750L;
    protected final List insertOrder = new ArrayList();

    public static OrderedMap decorate(Map map) {
        return new ListOrderedMap(map);
    }

    public ListOrderedMap() {
        this(new HashMap());
    }

    protected ListOrderedMap(Map map) {
        super(map);
        this.insertOrder.addAll(this.getMap().keySet());
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.map);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.map = (Map)in.readObject();
    }

    public MapIterator mapIterator() {
        return this.orderedMapIterator();
    }

    public OrderedMapIterator orderedMapIterator() {
        return new ListOrderedMapIterator(this);
    }

    public Object firstKey() {
        if (this.size() == 0) {
            throw new NoSuchElementException("Map is empty");
        }
        return this.insertOrder.get(0);
    }

    public Object lastKey() {
        if (this.size() == 0) {
            throw new NoSuchElementException("Map is empty");
        }
        return this.insertOrder.get(this.size() - 1);
    }

    public Object nextKey(Object key) {
        int index = this.insertOrder.indexOf(key);
        if (index >= 0 && index < this.size() - 1) {
            return this.insertOrder.get(index + 1);
        }
        return null;
    }

    public Object previousKey(Object key) {
        int index = this.insertOrder.indexOf(key);
        if (index > 0) {
            return this.insertOrder.get(index - 1);
        }
        return null;
    }

    public Object put(Object key, Object value) {
        if (this.getMap().containsKey(key)) {
            return this.getMap().put(key, value);
        }
        Object result = this.getMap().put(key, value);
        this.insertOrder.add(key);
        return result;
    }

    public void putAll(Map map) {
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = it.next();
            this.put(entry.getKey(), entry.getValue());
        }
    }

    public Object remove(Object key) {
        Object result = this.getMap().remove(key);
        this.insertOrder.remove(key);
        return result;
    }

    public void clear() {
        this.getMap().clear();
        this.insertOrder.clear();
    }

    public Set keySet() {
        return new KeySetView(this);
    }

    public List keyList() {
        return UnmodifiableList.decorate(this.insertOrder);
    }

    public Collection values() {
        return new ValuesView(this);
    }

    public List valueList() {
        return new ValuesView(this);
    }

    public Set entrySet() {
        return new EntrySetView(this, this.insertOrder);
    }

    public String toString() {
        if (this.isEmpty()) {
            return "{}";
        }
        StringBuffer buf = new StringBuffer();
        buf.append('{');
        boolean first = true;
        Iterator it = this.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (first) {
                first = false;
            } else {
                buf.append(", ");
            }
            buf.append((Object)(key == this ? "(this Map)" : key));
            buf.append('=');
            buf.append((Object)(value == this ? "(this Map)" : value));
        }
        buf.append('}');
        return buf.toString();
    }

    public Object get(int index) {
        return this.insertOrder.get(index);
    }

    public Object getValue(int index) {
        return this.get(this.insertOrder.get(index));
    }

    public int indexOf(Object key) {
        return this.insertOrder.indexOf(key);
    }

    public Object setValue(int index, Object value) {
        Object key = this.insertOrder.get(index);
        return this.put(key, value);
    }

    public Object put(int index, Object key, Object value) {
        Map m = this.getMap();
        if (m.containsKey(key)) {
            Object result = m.remove(key);
            int pos = this.insertOrder.indexOf(key);
            this.insertOrder.remove(pos);
            if (pos < index) {
                --index;
            }
            this.insertOrder.add(index, key);
            m.put(key, value);
            return result;
        }
        this.insertOrder.add(index, key);
        m.put(key, value);
        return null;
    }

    public Object remove(int index) {
        return this.remove(this.get(index));
    }

    public List asList() {
        return this.keyList();
    }

    static class ListOrderedMapIterator
    implements OrderedMapIterator,
    ResettableIterator {
        private final ListOrderedMap parent;
        private ListIterator iterator;
        private Object last = null;
        private boolean readable = false;

        ListOrderedMapIterator(ListOrderedMap parent) {
            this.parent = parent;
            this.iterator = parent.insertOrder.listIterator();
        }

        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        public Object next() {
            this.last = this.iterator.next();
            this.readable = true;
            return this.last;
        }

        public boolean hasPrevious() {
            return this.iterator.hasPrevious();
        }

        public Object previous() {
            this.last = this.iterator.previous();
            this.readable = true;
            return this.last;
        }

        public void remove() {
            if (!this.readable) {
                throw new IllegalStateException("remove() can only be called once after next()");
            }
            this.iterator.remove();
            this.parent.map.remove(this.last);
            this.readable = false;
        }

        public Object getKey() {
            if (!this.readable) {
                throw new IllegalStateException("getKey() can only be called after next() and before remove()");
            }
            return this.last;
        }

        public Object getValue() {
            if (!this.readable) {
                throw new IllegalStateException("getValue() can only be called after next() and before remove()");
            }
            return this.parent.get(this.last);
        }

        public Object setValue(Object value) {
            if (!this.readable) {
                throw new IllegalStateException("setValue() can only be called after next() and before remove()");
            }
            return this.parent.map.put(this.last, value);
        }

        public void reset() {
            this.iterator = this.parent.insertOrder.listIterator();
            this.last = null;
            this.readable = false;
        }

        public String toString() {
            if (this.readable) {
                return "Iterator[" + this.getKey() + "=" + this.getValue() + "]";
            }
            return "Iterator[]";
        }
    }

    static class ListOrderedMapEntry
    extends AbstractMapEntry {
        private final ListOrderedMap parent;

        ListOrderedMapEntry(ListOrderedMap parent, Object key) {
            super(key, null);
            this.parent = parent;
        }

        public Object getValue() {
            return this.parent.get(this.key);
        }

        public Object setValue(Object value) {
            return this.parent.getMap().put(this.key, value);
        }
    }

    static class ListOrderedIterator
    extends AbstractIteratorDecorator {
        private final ListOrderedMap parent;
        private Object last = null;

        ListOrderedIterator(ListOrderedMap parent, List insertOrder) {
            super(insertOrder.iterator());
            this.parent = parent;
        }

        public Object next() {
            this.last = super.next();
            return new ListOrderedMapEntry(this.parent, this.last);
        }

        public void remove() {
            super.remove();
            this.parent.getMap().remove(this.last);
        }
    }

    static class EntrySetView
    extends AbstractSet {
        private final ListOrderedMap parent;
        private final List insertOrder;
        private Set entrySet;

        public EntrySetView(ListOrderedMap parent, List insertOrder) {
            this.parent = parent;
            this.insertOrder = insertOrder;
        }

        private Set getEntrySet() {
            if (this.entrySet == null) {
                this.entrySet = this.parent.getMap().entrySet();
            }
            return this.entrySet;
        }

        public int size() {
            return this.parent.size();
        }

        public boolean isEmpty() {
            return this.parent.isEmpty();
        }

        public boolean contains(Object obj) {
            return this.getEntrySet().contains(obj);
        }

        public boolean containsAll(Collection coll) {
            return this.getEntrySet().containsAll(coll);
        }

        public boolean remove(Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            if (this.getEntrySet().contains(obj)) {
                Object key = ((Map.Entry)obj).getKey();
                this.parent.remove(key);
                return true;
            }
            return false;
        }

        public void clear() {
            this.parent.clear();
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            return this.getEntrySet().equals(obj);
        }

        public int hashCode() {
            return this.getEntrySet().hashCode();
        }

        public String toString() {
            return this.getEntrySet().toString();
        }

        public Iterator iterator() {
            return new ListOrderedIterator(this.parent, this.insertOrder);
        }
    }

    static class KeySetView
    extends AbstractSet {
        private final ListOrderedMap parent;

        KeySetView(ListOrderedMap parent) {
            this.parent = parent;
        }

        public int size() {
            return this.parent.size();
        }

        public boolean contains(Object value) {
            return this.parent.containsKey(value);
        }

        public void clear() {
            this.parent.clear();
        }

        public Iterator iterator() {
            return new AbstractIteratorDecorator(this.parent.entrySet().iterator()){

                public Object next() {
                    return ((Map.Entry)super.next()).getKey();
                }
            };
        }
    }

    static class ValuesView
    extends AbstractList {
        private final ListOrderedMap parent;

        ValuesView(ListOrderedMap parent) {
            this.parent = parent;
        }

        public int size() {
            return this.parent.size();
        }

        public boolean contains(Object value) {
            return this.parent.containsValue(value);
        }

        public void clear() {
            this.parent.clear();
        }

        public Iterator iterator() {
            return new AbstractIteratorDecorator(this.parent.entrySet().iterator()){

                public Object next() {
                    return ((Map.Entry)this.iterator.next()).getValue();
                }
            };
        }

        public Object get(int index) {
            return this.parent.getValue(index);
        }

        public Object set(int index, Object value) {
            return this.parent.setValue(index, value);
        }

        public Object remove(int index) {
            return this.parent.remove(index);
        }
    }
}

