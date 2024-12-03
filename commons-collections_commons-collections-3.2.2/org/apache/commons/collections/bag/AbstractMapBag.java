/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.bag;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections.Bag;
import org.apache.commons.collections.bag.HashBag;
import org.apache.commons.collections.set.UnmodifiableSet;

public abstract class AbstractMapBag
implements Bag {
    private transient Map map;
    private int size;
    private transient int modCount;
    private transient Set uniqueSet;

    protected AbstractMapBag() {
    }

    protected AbstractMapBag(Map map) {
        this.map = map;
    }

    protected Map getMap() {
        return this.map;
    }

    public int size() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public int getCount(Object object) {
        MutableInteger count = (MutableInteger)this.map.get(object);
        if (count != null) {
            return count.value;
        }
        return 0;
    }

    public boolean contains(Object object) {
        return this.map.containsKey(object);
    }

    public boolean containsAll(Collection coll) {
        if (coll instanceof Bag) {
            return this.containsAll((Bag)coll);
        }
        return this.containsAll(new HashBag(coll));
    }

    boolean containsAll(Bag other) {
        boolean result = true;
        Iterator it = other.uniqueSet().iterator();
        while (it.hasNext()) {
            Object current = it.next();
            boolean contains = this.getCount(current) >= other.getCount(current);
            result = result && contains;
        }
        return result;
    }

    public Iterator iterator() {
        return new BagIterator(this);
    }

    public boolean add(Object object) {
        return this.add(object, 1);
    }

    public boolean add(Object object, int nCopies) {
        ++this.modCount;
        if (nCopies > 0) {
            MutableInteger mut = (MutableInteger)this.map.get(object);
            this.size += nCopies;
            if (mut == null) {
                this.map.put(object, new MutableInteger(nCopies));
                return true;
            }
            mut.value += nCopies;
            return false;
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
        ++this.modCount;
        this.map.clear();
        this.size = 0;
    }

    public boolean remove(Object object) {
        MutableInteger mut = (MutableInteger)this.map.get(object);
        if (mut == null) {
            return false;
        }
        ++this.modCount;
        this.map.remove(object);
        this.size -= mut.value;
        return true;
    }

    public boolean remove(Object object, int nCopies) {
        MutableInteger mut = (MutableInteger)this.map.get(object);
        if (mut == null) {
            return false;
        }
        if (nCopies <= 0) {
            return false;
        }
        ++this.modCount;
        if (nCopies < mut.value) {
            mut.value -= nCopies;
            this.size -= nCopies;
        } else {
            this.map.remove(object);
            this.size -= mut.value;
        }
        return true;
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
        if (coll instanceof Bag) {
            return this.retainAll((Bag)coll);
        }
        return this.retainAll(new HashBag(coll));
    }

    boolean retainAll(Bag other) {
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
        Object[] result = new Object[this.size()];
        int i = 0;
        Iterator it = this.map.keySet().iterator();
        while (it.hasNext()) {
            Object current = it.next();
            for (int index = this.getCount(current); index > 0; --index) {
                result[i++] = current;
            }
        }
        return result;
    }

    public Object[] toArray(Object[] array) {
        int size = this.size();
        if (array.length < size) {
            array = (Object[])Array.newInstance(array.getClass().getComponentType(), size);
        }
        int i = 0;
        Iterator it = this.map.keySet().iterator();
        while (it.hasNext()) {
            Object current = it.next();
            for (int index = this.getCount(current); index > 0; --index) {
                array[i++] = current;
            }
        }
        if (array.length > size) {
            array[size] = null;
        }
        return array;
    }

    public Set uniqueSet() {
        if (this.uniqueSet == null) {
            this.uniqueSet = UnmodifiableSet.decorate(this.map.keySet());
        }
        return this.uniqueSet;
    }

    protected void doWriteObject(ObjectOutputStream out) throws IOException {
        out.writeInt(this.map.size());
        Iterator it = this.map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = it.next();
            out.writeObject(entry.getKey());
            out.writeInt(((MutableInteger)entry.getValue()).value);
        }
    }

    protected void doReadObject(Map map, ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.map = map;
        int entrySize = in.readInt();
        for (int i = 0; i < entrySize; ++i) {
            Object obj = in.readObject();
            int count = in.readInt();
            map.put(obj, new MutableInteger(count));
            this.size += count;
        }
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
        Iterator it = this.map.keySet().iterator();
        while (it.hasNext()) {
            Object element = it.next();
            if (other.getCount(element) == this.getCount(element)) continue;
            return false;
        }
        return true;
    }

    public int hashCode() {
        int total = 0;
        Iterator it = this.map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = it.next();
            Object element = entry.getKey();
            MutableInteger count = (MutableInteger)entry.getValue();
            total += (element == null ? 0 : element.hashCode()) ^ count.value;
        }
        return total;
    }

    public String toString() {
        if (this.size() == 0) {
            return "[]";
        }
        StringBuffer buf = new StringBuffer();
        buf.append('[');
        Iterator it = this.uniqueSet().iterator();
        while (it.hasNext()) {
            Object current = it.next();
            int count = this.getCount(current);
            buf.append(count);
            buf.append(':');
            buf.append(current);
            if (!it.hasNext()) continue;
            buf.append(',');
        }
        buf.append(']');
        return buf.toString();
    }

    protected static class MutableInteger {
        protected int value;

        MutableInteger(int value) {
            this.value = value;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof MutableInteger)) {
                return false;
            }
            return ((MutableInteger)obj).value == this.value;
        }

        public int hashCode() {
            return this.value;
        }
    }

    static class BagIterator
    implements Iterator {
        private AbstractMapBag parent;
        private Iterator entryIterator;
        private Map.Entry current;
        private int itemCount;
        private final int mods;
        private boolean canRemove;

        public BagIterator(AbstractMapBag parent) {
            this.parent = parent;
            this.entryIterator = parent.map.entrySet().iterator();
            this.current = null;
            this.mods = parent.modCount;
            this.canRemove = false;
        }

        public boolean hasNext() {
            return this.itemCount > 0 || this.entryIterator.hasNext();
        }

        public Object next() {
            if (this.parent.modCount != this.mods) {
                throw new ConcurrentModificationException();
            }
            if (this.itemCount == 0) {
                this.current = (Map.Entry)this.entryIterator.next();
                this.itemCount = ((MutableInteger)this.current.getValue()).value;
            }
            this.canRemove = true;
            --this.itemCount;
            return this.current.getKey();
        }

        public void remove() {
            if (this.parent.modCount != this.mods) {
                throw new ConcurrentModificationException();
            }
            if (!this.canRemove) {
                throw new IllegalStateException();
            }
            MutableInteger mut = (MutableInteger)this.current.getValue();
            if (mut.value > 1) {
                --mut.value;
            } else {
                this.entryIterator.remove();
            }
            this.parent.size--;
            this.canRemove = false;
        }
    }
}

