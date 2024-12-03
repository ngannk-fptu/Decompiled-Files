/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.collections;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import org.apache.axis.i18n.Messages;

public class SequencedHashMap
implements Map,
Cloneable,
Externalizable {
    private Entry sentinel = SequencedHashMap.createSentinel();
    private HashMap entries;
    private transient long modCount = 0L;
    private static final int KEY = 0;
    private static final int VALUE = 1;
    private static final int ENTRY = 2;
    private static final int REMOVED_MASK = Integer.MIN_VALUE;
    private static final long serialVersionUID = 3380552487888102930L;

    private static final Entry createSentinel() {
        Entry s;
        s.prev = s = new Entry(null, null);
        s.next = s;
        return s;
    }

    public SequencedHashMap() {
        this.entries = new HashMap();
    }

    public SequencedHashMap(int initialSize) {
        this.entries = new HashMap(initialSize);
    }

    public SequencedHashMap(int initialSize, float loadFactor) {
        this.entries = new HashMap(initialSize, loadFactor);
    }

    public SequencedHashMap(Map m) {
        this();
        this.putAll(m);
    }

    private void removeEntry(Entry entry) {
        entry.next.prev = entry.prev;
        entry.prev.next = entry.next;
    }

    private void insertEntry(Entry entry) {
        entry.next = this.sentinel;
        entry.prev = this.sentinel.prev;
        this.sentinel.prev.next = entry;
        this.sentinel.prev = entry;
    }

    public int size() {
        return this.entries.size();
    }

    public boolean isEmpty() {
        return this.sentinel.next == this.sentinel;
    }

    public boolean containsKey(Object key) {
        return this.entries.containsKey(key);
    }

    public boolean containsValue(Object value) {
        if (value == null) {
            Entry pos = this.sentinel.next;
            while (pos != this.sentinel) {
                if (pos.getValue() == null) {
                    return true;
                }
                pos = pos.next;
            }
        } else {
            Entry pos = this.sentinel.next;
            while (pos != this.sentinel) {
                if (value.equals(pos.getValue())) {
                    return true;
                }
                pos = pos.next;
            }
        }
        return false;
    }

    public Object get(Object o) {
        Entry entry = (Entry)this.entries.get(o);
        if (entry == null) {
            return null;
        }
        return entry.getValue();
    }

    public Map.Entry getFirst() {
        return this.isEmpty() ? null : this.sentinel.next;
    }

    public Object getFirstKey() {
        return this.sentinel.next.getKey();
    }

    public Object getFirstValue() {
        return this.sentinel.next.getValue();
    }

    public Map.Entry getLast() {
        return this.isEmpty() ? null : this.sentinel.prev;
    }

    public Object getLastKey() {
        return this.sentinel.prev.getKey();
    }

    public Object getLastValue() {
        return this.sentinel.prev.getValue();
    }

    public Object put(Object key, Object value) {
        ++this.modCount;
        Object oldValue = null;
        Entry e = (Entry)this.entries.get(key);
        if (e != null) {
            this.removeEntry(e);
            oldValue = e.setValue(value);
        } else {
            e = new Entry(key, value);
            this.entries.put(key, e);
        }
        this.insertEntry(e);
        return oldValue;
    }

    public Object remove(Object key) {
        Entry e = this.removeImpl(key);
        return e == null ? null : e.getValue();
    }

    private Entry removeImpl(Object key) {
        Entry e = (Entry)this.entries.remove(key);
        if (e == null) {
            return null;
        }
        ++this.modCount;
        this.removeEntry(e);
        return e;
    }

    public void putAll(Map t) {
        Iterator iter = t.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = iter.next();
            this.put(entry.getKey(), entry.getValue());
        }
    }

    public void clear() {
        ++this.modCount;
        this.entries.clear();
        this.sentinel.next = this.sentinel;
        this.sentinel.prev = this.sentinel;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Map)) {
            return false;
        }
        return ((Object)this.entrySet()).equals(((Map)obj).entrySet());
    }

    public int hashCode() {
        return ((Object)this.entrySet()).hashCode();
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append('[');
        Entry pos = this.sentinel.next;
        while (pos != this.sentinel) {
            buf.append(pos.getKey());
            buf.append('=');
            buf.append(pos.getValue());
            if (pos.next != this.sentinel) {
                buf.append(',');
            }
            pos = pos.next;
        }
        buf.append(']');
        return buf.toString();
    }

    public Set keySet() {
        return new AbstractSet(){

            public Iterator iterator() {
                return new OrderedIterator(0);
            }

            public boolean remove(Object o) {
                Entry e = SequencedHashMap.this.removeImpl(o);
                return e != null;
            }

            public void clear() {
                SequencedHashMap.this.clear();
            }

            public int size() {
                return SequencedHashMap.this.size();
            }

            public boolean isEmpty() {
                return SequencedHashMap.this.isEmpty();
            }

            public boolean contains(Object o) {
                return SequencedHashMap.this.containsKey(o);
            }
        };
    }

    public Collection values() {
        return new AbstractCollection(){

            public Iterator iterator() {
                return new OrderedIterator(1);
            }

            public boolean remove(Object value) {
                if (value == null) {
                    Entry pos = ((SequencedHashMap)SequencedHashMap.this).sentinel.next;
                    while (pos != SequencedHashMap.this.sentinel) {
                        if (pos.getValue() == null) {
                            SequencedHashMap.this.removeImpl(pos.getKey());
                            return true;
                        }
                        pos = pos.next;
                    }
                } else {
                    Entry pos = ((SequencedHashMap)SequencedHashMap.this).sentinel.next;
                    while (pos != SequencedHashMap.this.sentinel) {
                        if (value.equals(pos.getValue())) {
                            SequencedHashMap.this.removeImpl(pos.getKey());
                            return true;
                        }
                        pos = pos.next;
                    }
                }
                return false;
            }

            public void clear() {
                SequencedHashMap.this.clear();
            }

            public int size() {
                return SequencedHashMap.this.size();
            }

            public boolean isEmpty() {
                return SequencedHashMap.this.isEmpty();
            }

            public boolean contains(Object o) {
                return SequencedHashMap.this.containsValue(o);
            }
        };
    }

    public Set entrySet() {
        return new AbstractSet(){

            private Entry findEntry(Object o) {
                if (o == null) {
                    return null;
                }
                if (!(o instanceof Map.Entry)) {
                    return null;
                }
                Map.Entry e = (Map.Entry)o;
                Entry entry = (Entry)SequencedHashMap.this.entries.get(e.getKey());
                if (entry != null && entry.equals(e)) {
                    return entry;
                }
                return null;
            }

            public Iterator iterator() {
                return new OrderedIterator(2);
            }

            public boolean remove(Object o) {
                Entry e = this.findEntry(o);
                if (e == null) {
                    return false;
                }
                return SequencedHashMap.this.removeImpl(e.getKey()) != null;
            }

            public void clear() {
                SequencedHashMap.this.clear();
            }

            public int size() {
                return SequencedHashMap.this.size();
            }

            public boolean isEmpty() {
                return SequencedHashMap.this.isEmpty();
            }

            public boolean contains(Object o) {
                return this.findEntry(o) != null;
            }
        };
    }

    public Object clone() throws CloneNotSupportedException {
        SequencedHashMap map = (SequencedHashMap)super.clone();
        map.sentinel = SequencedHashMap.createSentinel();
        map.entries = new HashMap();
        map.putAll((Map)this);
        return map;
    }

    private Map.Entry getEntry(int index) {
        int i;
        Entry pos = this.sentinel;
        if (index < 0) {
            throw new ArrayIndexOutOfBoundsException(Messages.getMessage("seqHashMapArrayIndexOutOfBoundsException01", new Integer(index).toString()));
        }
        for (i = -1; i < index - 1 && pos.next != this.sentinel; ++i) {
            pos = pos.next;
        }
        if (pos.next == this.sentinel) {
            throw new ArrayIndexOutOfBoundsException(Messages.getMessage("seqHashMapArrayIndexOutOfBoundsException02", new Integer(index).toString(), new Integer(i + 1).toString()));
        }
        return pos.next;
    }

    public Object get(int index) {
        return this.getEntry(index).getKey();
    }

    public Object getValue(int index) {
        return this.getEntry(index).getValue();
    }

    public int indexOf(Object key) {
        Entry e = (Entry)this.entries.get(key);
        if (e == null) {
            return -1;
        }
        int pos = 0;
        while (e.prev != this.sentinel) {
            ++pos;
            e = e.prev;
        }
        return pos;
    }

    public Iterator iterator() {
        return this.keySet().iterator();
    }

    public int lastIndexOf(Object key) {
        return this.indexOf(key);
    }

    public List sequence() {
        ArrayList l = new ArrayList(this.size());
        Iterator iter = this.keySet().iterator();
        while (iter.hasNext()) {
            l.add(iter.next());
        }
        return Collections.unmodifiableList(l);
    }

    public Object remove(int index) {
        return this.remove(this.get(index));
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int size = in.readInt();
        for (int i = 0; i < size; ++i) {
            Object key = in.readObject();
            Object value = in.readObject();
            this.put(key, value);
        }
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(this.size());
        Entry pos = this.sentinel.next;
        while (pos != this.sentinel) {
            out.writeObject(pos.getKey());
            out.writeObject(pos.getValue());
            pos = pos.next;
        }
    }

    private class OrderedIterator
    implements Iterator {
        private int returnType;
        private Entry pos;
        private transient long expectedModCount;

        public OrderedIterator(int returnType) {
            this.pos = SequencedHashMap.this.sentinel;
            this.expectedModCount = SequencedHashMap.this.modCount;
            this.returnType = returnType | Integer.MIN_VALUE;
        }

        public boolean hasNext() {
            return this.pos.next != SequencedHashMap.this.sentinel;
        }

        public Object next() {
            if (SequencedHashMap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException(Messages.getMessage("seqHashMapConcurrentModificationException00"));
            }
            if (this.pos.next == SequencedHashMap.this.sentinel) {
                throw new NoSuchElementException(Messages.getMessage("seqHashMapNoSuchElementException00"));
            }
            this.returnType &= Integer.MAX_VALUE;
            this.pos = this.pos.next;
            switch (this.returnType) {
                case 0: {
                    return this.pos.getKey();
                }
                case 1: {
                    return this.pos.getValue();
                }
                case 2: {
                    return this.pos;
                }
            }
            throw new Error(Messages.getMessage("seqHashMapBadIteratorType01", new Integer(this.returnType).toString()));
        }

        public void remove() {
            if ((this.returnType & Integer.MIN_VALUE) != 0) {
                throw new IllegalStateException(Messages.getMessage("seqHashMapIllegalStateException00"));
            }
            if (SequencedHashMap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException(Messages.getMessage("seqHashMapConcurrentModificationException00"));
            }
            SequencedHashMap.this.removeImpl(this.pos.getKey());
            ++this.expectedModCount;
            this.returnType |= Integer.MIN_VALUE;
        }
    }

    private static class Entry
    implements Map.Entry {
        private final Object key;
        private Object value;
        Entry next = null;
        Entry prev = null;

        public Entry(Object key, Object value) {
            this.key = key;
            this.value = value;
        }

        public Object getKey() {
            return this.key;
        }

        public Object getValue() {
            return this.value;
        }

        public Object setValue(Object value) {
            Object oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        public int hashCode() {
            return (this.getKey() == null ? 0 : this.getKey().hashCode()) ^ (this.getValue() == null ? 0 : this.getValue().hashCode());
        }

        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            Map.Entry other = (Map.Entry)obj;
            return (this.getKey() == null ? other.getKey() == null : this.getKey().equals(other.getKey())) && (this.getValue() == null ? other.getValue() == null : this.getValue().equals(other.getValue()));
        }

        public String toString() {
            return "[" + this.getKey() + "=" + this.getValue() + "]";
        }
    }
}

