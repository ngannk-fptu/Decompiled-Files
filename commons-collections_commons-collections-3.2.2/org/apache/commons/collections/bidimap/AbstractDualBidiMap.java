/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.bidimap;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.ResettableIterator;
import org.apache.commons.collections.collection.AbstractCollectionDecorator;
import org.apache.commons.collections.iterators.AbstractIteratorDecorator;
import org.apache.commons.collections.keyvalue.AbstractMapEntryDecorator;

public abstract class AbstractDualBidiMap
implements BidiMap {
    protected final transient Map[] maps = new Map[2];
    protected transient BidiMap inverseBidiMap = null;
    protected transient Set keySet = null;
    protected transient Collection values = null;
    protected transient Set entrySet = null;

    protected AbstractDualBidiMap() {
        this.maps[0] = this.createMap();
        this.maps[1] = this.createMap();
    }

    protected AbstractDualBidiMap(Map normalMap, Map reverseMap) {
        this.maps[0] = normalMap;
        this.maps[1] = reverseMap;
    }

    protected AbstractDualBidiMap(Map normalMap, Map reverseMap, BidiMap inverseBidiMap) {
        this.maps[0] = normalMap;
        this.maps[1] = reverseMap;
        this.inverseBidiMap = inverseBidiMap;
    }

    protected Map createMap() {
        return null;
    }

    protected abstract BidiMap createBidiMap(Map var1, Map var2, BidiMap var3);

    public Object get(Object key) {
        return this.maps[0].get(key);
    }

    public int size() {
        return this.maps[0].size();
    }

    public boolean isEmpty() {
        return this.maps[0].isEmpty();
    }

    public boolean containsKey(Object key) {
        return this.maps[0].containsKey(key);
    }

    public boolean equals(Object obj) {
        return this.maps[0].equals(obj);
    }

    public int hashCode() {
        return this.maps[0].hashCode();
    }

    public String toString() {
        return this.maps[0].toString();
    }

    public Object put(Object key, Object value) {
        if (this.maps[0].containsKey(key)) {
            this.maps[1].remove(this.maps[0].get(key));
        }
        if (this.maps[1].containsKey(value)) {
            this.maps[0].remove(this.maps[1].get(value));
        }
        Object obj = this.maps[0].put(key, value);
        this.maps[1].put(value, key);
        return obj;
    }

    public void putAll(Map map) {
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = it.next();
            this.put(entry.getKey(), entry.getValue());
        }
    }

    public Object remove(Object key) {
        Object value = null;
        if (this.maps[0].containsKey(key)) {
            value = this.maps[0].remove(key);
            this.maps[1].remove(value);
        }
        return value;
    }

    public void clear() {
        this.maps[0].clear();
        this.maps[1].clear();
    }

    public boolean containsValue(Object value) {
        return this.maps[1].containsKey(value);
    }

    public MapIterator mapIterator() {
        return new BidiMapIterator(this);
    }

    public Object getKey(Object value) {
        return this.maps[1].get(value);
    }

    public Object removeValue(Object value) {
        Object key = null;
        if (this.maps[1].containsKey(value)) {
            key = this.maps[1].remove(value);
            this.maps[0].remove(key);
        }
        return key;
    }

    public BidiMap inverseBidiMap() {
        if (this.inverseBidiMap == null) {
            this.inverseBidiMap = this.createBidiMap(this.maps[1], this.maps[0], this);
        }
        return this.inverseBidiMap;
    }

    public Set keySet() {
        if (this.keySet == null) {
            this.keySet = new KeySet(this);
        }
        return this.keySet;
    }

    protected Iterator createKeySetIterator(Iterator iterator) {
        return new KeySetIterator(iterator, this);
    }

    public Collection values() {
        if (this.values == null) {
            this.values = new Values(this);
        }
        return this.values;
    }

    protected Iterator createValuesIterator(Iterator iterator) {
        return new ValuesIterator(iterator, this);
    }

    public Set entrySet() {
        if (this.entrySet == null) {
            this.entrySet = new EntrySet(this);
        }
        return this.entrySet;
    }

    protected Iterator createEntrySetIterator(Iterator iterator) {
        return new EntrySetIterator(iterator, this);
    }

    protected static class BidiMapIterator
    implements MapIterator,
    ResettableIterator {
        protected final AbstractDualBidiMap parent;
        protected Iterator iterator;
        protected Map.Entry last = null;
        protected boolean canRemove = false;

        protected BidiMapIterator(AbstractDualBidiMap parent) {
            this.parent = parent;
            this.iterator = parent.maps[0].entrySet().iterator();
        }

        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        public Object next() {
            this.last = (Map.Entry)this.iterator.next();
            this.canRemove = true;
            return this.last.getKey();
        }

        public void remove() {
            if (!this.canRemove) {
                throw new IllegalStateException("Iterator remove() can only be called once after next()");
            }
            Object value = this.last.getValue();
            this.iterator.remove();
            this.parent.maps[1].remove(value);
            this.last = null;
            this.canRemove = false;
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
            this.iterator = this.parent.maps[0].entrySet().iterator();
            this.last = null;
            this.canRemove = false;
        }

        public String toString() {
            if (this.last != null) {
                return "MapIterator[" + this.getKey() + "=" + this.getValue() + "]";
            }
            return "MapIterator[]";
        }
    }

    protected static class MapEntry
    extends AbstractMapEntryDecorator {
        protected final AbstractDualBidiMap parent;

        protected MapEntry(Map.Entry entry, AbstractDualBidiMap parent) {
            super(entry);
            this.parent = parent;
        }

        public Object setValue(Object value) {
            Object key = this.getKey();
            if (this.parent.maps[1].containsKey(value) && this.parent.maps[1].get(value) != key) {
                throw new IllegalArgumentException("Cannot use setValue() when the object being set is already in the map");
            }
            this.parent.put(key, value);
            Object oldValue = super.setValue(value);
            return oldValue;
        }
    }

    protected static class EntrySetIterator
    extends AbstractIteratorDecorator {
        protected final AbstractDualBidiMap parent;
        protected Map.Entry last = null;
        protected boolean canRemove = false;

        protected EntrySetIterator(Iterator iterator, AbstractDualBidiMap parent) {
            super(iterator);
            this.parent = parent;
        }

        public Object next() {
            this.last = new MapEntry((Map.Entry)super.next(), this.parent);
            this.canRemove = true;
            return this.last;
        }

        public void remove() {
            if (!this.canRemove) {
                throw new IllegalStateException("Iterator remove() can only be called once after next()");
            }
            Object value = this.last.getValue();
            super.remove();
            this.parent.maps[1].remove(value);
            this.last = null;
            this.canRemove = false;
        }
    }

    protected static class EntrySet
    extends View
    implements Set {
        protected EntrySet(AbstractDualBidiMap parent) {
            super(parent.maps[0].entrySet(), parent);
        }

        public Iterator iterator() {
            return this.parent.createEntrySetIterator(super.iterator());
        }

        public boolean remove(Object obj) {
            Object value;
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            Map.Entry entry = (Map.Entry)obj;
            Object key = entry.getKey();
            if (this.parent.containsKey(key) && ((value = this.parent.maps[0].get(key)) == null ? entry.getValue() == null : value.equals(entry.getValue()))) {
                this.parent.maps[0].remove(key);
                this.parent.maps[1].remove(value);
                return true;
            }
            return false;
        }
    }

    protected static class ValuesIterator
    extends AbstractIteratorDecorator {
        protected final AbstractDualBidiMap parent;
        protected Object lastValue = null;
        protected boolean canRemove = false;

        protected ValuesIterator(Iterator iterator, AbstractDualBidiMap parent) {
            super(iterator);
            this.parent = parent;
        }

        public Object next() {
            this.lastValue = super.next();
            this.canRemove = true;
            return this.lastValue;
        }

        public void remove() {
            if (!this.canRemove) {
                throw new IllegalStateException("Iterator remove() can only be called once after next()");
            }
            super.remove();
            this.parent.maps[1].remove(this.lastValue);
            this.lastValue = null;
            this.canRemove = false;
        }
    }

    protected static class Values
    extends View
    implements Set {
        protected Values(AbstractDualBidiMap parent) {
            super(parent.maps[0].values(), parent);
        }

        public Iterator iterator() {
            return this.parent.createValuesIterator(super.iterator());
        }

        public boolean contains(Object value) {
            return this.parent.maps[1].containsKey(value);
        }

        public boolean remove(Object value) {
            if (this.parent.maps[1].containsKey(value)) {
                Object key = this.parent.maps[1].remove(value);
                this.parent.maps[0].remove(key);
                return true;
            }
            return false;
        }
    }

    protected static class KeySetIterator
    extends AbstractIteratorDecorator {
        protected final AbstractDualBidiMap parent;
        protected Object lastKey = null;
        protected boolean canRemove = false;

        protected KeySetIterator(Iterator iterator, AbstractDualBidiMap parent) {
            super(iterator);
            this.parent = parent;
        }

        public Object next() {
            this.lastKey = super.next();
            this.canRemove = true;
            return this.lastKey;
        }

        public void remove() {
            if (!this.canRemove) {
                throw new IllegalStateException("Iterator remove() can only be called once after next()");
            }
            Object value = this.parent.maps[0].get(this.lastKey);
            super.remove();
            this.parent.maps[1].remove(value);
            this.lastKey = null;
            this.canRemove = false;
        }
    }

    protected static class KeySet
    extends View
    implements Set {
        protected KeySet(AbstractDualBidiMap parent) {
            super(parent.maps[0].keySet(), parent);
        }

        public Iterator iterator() {
            return this.parent.createKeySetIterator(super.iterator());
        }

        public boolean contains(Object key) {
            return this.parent.maps[0].containsKey(key);
        }

        public boolean remove(Object key) {
            if (this.parent.maps[0].containsKey(key)) {
                Object value = this.parent.maps[0].remove(key);
                this.parent.maps[1].remove(value);
                return true;
            }
            return false;
        }
    }

    protected static abstract class View
    extends AbstractCollectionDecorator {
        protected final AbstractDualBidiMap parent;

        protected View(Collection coll, AbstractDualBidiMap parent) {
            super(coll);
            this.parent = parent;
        }

        public boolean removeAll(Collection coll) {
            if (this.parent.isEmpty() || coll.isEmpty()) {
                return false;
            }
            boolean modified = false;
            Iterator it = this.iterator();
            while (it.hasNext()) {
                if (!coll.contains(it.next())) continue;
                it.remove();
                modified = true;
            }
            return modified;
        }

        public boolean retainAll(Collection coll) {
            if (this.parent.isEmpty()) {
                return false;
            }
            if (coll.isEmpty()) {
                this.parent.clear();
                return true;
            }
            boolean modified = false;
            Iterator it = this.iterator();
            while (it.hasNext()) {
                if (coll.contains(it.next())) continue;
                it.remove();
                modified = true;
            }
            return modified;
        }

        public void clear() {
            this.parent.clear();
        }
    }
}

