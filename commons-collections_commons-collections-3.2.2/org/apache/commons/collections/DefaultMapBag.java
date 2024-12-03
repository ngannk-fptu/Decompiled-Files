/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections.Bag;
import org.apache.commons.collections.HashBag;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.set.UnmodifiableSet;

public abstract class DefaultMapBag
implements Bag {
    private Map _map = null;
    private int _total = 0;
    private int _mods = 0;

    public DefaultMapBag() {
    }

    protected DefaultMapBag(Map map) {
        this.setMap(map);
    }

    public boolean add(Object object) {
        return this.add(object, 1);
    }

    public boolean add(Object object, int nCopies) {
        ++this._mods;
        if (nCopies > 0) {
            int count = nCopies + this.getCount(object);
            this._map.put(object, new Integer(count));
            this._total += nCopies;
            return count == nCopies;
        }
        return false;
    }

    public boolean addAll(Collection coll) {
        boolean changed = false;
        Iterator i = coll.iterator();
        while (i.hasNext()) {
            boolean added = this.add(i.next());
            changed = changed || added;
        }
        return changed;
    }

    public void clear() {
        ++this._mods;
        this._map.clear();
        this._total = 0;
    }

    public boolean contains(Object object) {
        return this._map.containsKey(object);
    }

    public boolean containsAll(Collection coll) {
        return this.containsAll(new HashBag(coll));
    }

    public boolean containsAll(Bag other) {
        boolean result = true;
        Iterator i = other.uniqueSet().iterator();
        while (i.hasNext()) {
            Object current = i.next();
            boolean contains = this.getCount(current) >= other.getCount(current);
            result = result && contains;
        }
        return result;
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof Bag)) {
            return false;
        }
        Bag other = (Bag)object;
        if (other.size() != this.size()) {
            return false;
        }
        Iterator it = this._map.keySet().iterator();
        while (it.hasNext()) {
            Object element = it.next();
            if (other.getCount(element) == this.getCount(element)) continue;
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this._map.hashCode();
    }

    public boolean isEmpty() {
        return this._map.isEmpty();
    }

    public Iterator iterator() {
        return new BagIterator(this, this.extractList().iterator());
    }

    public boolean remove(Object object) {
        return this.remove(object, this.getCount(object));
    }

    public boolean remove(Object object, int nCopies) {
        ++this._mods;
        boolean result = false;
        int count = this.getCount(object);
        if (nCopies <= 0) {
            result = false;
        } else if (count > nCopies) {
            this._map.put(object, new Integer(count - nCopies));
            result = true;
            this._total -= nCopies;
        } else {
            result = this._map.remove(object) != null;
            this._total -= count;
        }
        return result;
    }

    public boolean removeAll(Collection coll) {
        boolean result = false;
        if (coll != null) {
            Iterator i = coll.iterator();
            while (i.hasNext()) {
                boolean changed = this.remove(i.next(), 1);
                result = result || changed;
            }
        }
        return result;
    }

    public boolean retainAll(Collection coll) {
        return this.retainAll(new HashBag(coll));
    }

    public boolean retainAll(Bag other) {
        boolean result = false;
        HashBag excess = new HashBag();
        Iterator i = this.uniqueSet().iterator();
        while (i.hasNext()) {
            Object current = i.next();
            int myCount = this.getCount(current);
            int otherCount = other.getCount(current);
            if (1 <= otherCount && otherCount <= myCount) {
                excess.add(current, myCount - otherCount);
                continue;
            }
            excess.add(current, myCount);
        }
        if (!excess.isEmpty()) {
            result = this.removeAll((Collection)excess);
        }
        return result;
    }

    public Object[] toArray() {
        return this.extractList().toArray();
    }

    public Object[] toArray(Object[] array) {
        return this.extractList().toArray(array);
    }

    public int getCount(Object object) {
        int result = 0;
        Integer count = MapUtils.getInteger(this._map, object);
        if (count != null) {
            result = count;
        }
        return result;
    }

    public Set uniqueSet() {
        return UnmodifiableSet.decorate(this._map.keySet());
    }

    public int size() {
        return this._total;
    }

    protected int calcTotalSize() {
        this._total = this.extractList().size();
        return this._total;
    }

    protected void setMap(Map map) {
        if (map == null || !map.isEmpty()) {
            throw new IllegalArgumentException("The map must be non-null and empty");
        }
        this._map = map;
    }

    protected Map getMap() {
        return this._map;
    }

    private List extractList() {
        ArrayList result = new ArrayList();
        Iterator i = this.uniqueSet().iterator();
        while (i.hasNext()) {
            Object current = i.next();
            for (int index = this.getCount(current); index > 0; --index) {
                result.add(current);
            }
        }
        return result;
    }

    private int modCount() {
        return this._mods;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("[");
        Iterator i = this.uniqueSet().iterator();
        while (i.hasNext()) {
            Object current = i.next();
            int count = this.getCount(current);
            buf.append(count);
            buf.append(":");
            buf.append(current);
            if (!i.hasNext()) continue;
            buf.append(",");
        }
        buf.append("]");
        return buf.toString();
    }

    static class BagIterator
    implements Iterator {
        private DefaultMapBag _parent = null;
        private Iterator _support = null;
        private Object _current = null;
        private int _mods = 0;

        public BagIterator(DefaultMapBag parent, Iterator support) {
            this._parent = parent;
            this._support = support;
            this._current = null;
            this._mods = parent.modCount();
        }

        public boolean hasNext() {
            return this._support.hasNext();
        }

        public Object next() {
            if (this._parent.modCount() != this._mods) {
                throw new ConcurrentModificationException();
            }
            this._current = this._support.next();
            return this._current;
        }

        public void remove() {
            if (this._parent.modCount() != this._mods) {
                throw new ConcurrentModificationException();
            }
            this._support.remove();
            this._parent.remove(this._current, 1);
            ++this._mods;
        }
    }
}

