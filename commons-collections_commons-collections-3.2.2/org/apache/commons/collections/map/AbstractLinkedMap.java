/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.map;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.OrderedIterator;
import org.apache.commons.collections.OrderedMap;
import org.apache.commons.collections.OrderedMapIterator;
import org.apache.commons.collections.ResettableIterator;
import org.apache.commons.collections.iterators.EmptyOrderedIterator;
import org.apache.commons.collections.iterators.EmptyOrderedMapIterator;
import org.apache.commons.collections.map.AbstractHashedMap;

public class AbstractLinkedMap
extends AbstractHashedMap
implements OrderedMap {
    protected transient LinkEntry header;

    protected AbstractLinkedMap() {
    }

    protected AbstractLinkedMap(int initialCapacity, float loadFactor, int threshold) {
        super(initialCapacity, loadFactor, threshold);
    }

    protected AbstractLinkedMap(int initialCapacity) {
        super(initialCapacity);
    }

    protected AbstractLinkedMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    protected AbstractLinkedMap(Map map) {
        super(map);
    }

    protected void init() {
        this.header.before = this.header.after = (this.header = (LinkEntry)this.createEntry(null, -1, null, null));
    }

    public boolean containsValue(Object value) {
        if (value == null) {
            LinkEntry entry = this.header.after;
            while (entry != this.header) {
                if (entry.getValue() == null) {
                    return true;
                }
                entry = entry.after;
            }
        } else {
            LinkEntry entry = this.header.after;
            while (entry != this.header) {
                if (this.isEqualValue(value, entry.getValue())) {
                    return true;
                }
                entry = entry.after;
            }
        }
        return false;
    }

    public void clear() {
        super.clear();
        this.header.before = this.header.after = this.header;
    }

    public Object firstKey() {
        if (this.size == 0) {
            throw new NoSuchElementException("Map is empty");
        }
        return this.header.after.getKey();
    }

    public Object lastKey() {
        if (this.size == 0) {
            throw new NoSuchElementException("Map is empty");
        }
        return this.header.before.getKey();
    }

    public Object nextKey(Object key) {
        LinkEntry entry = (LinkEntry)this.getEntry(key);
        return entry == null || entry.after == this.header ? null : entry.after.getKey();
    }

    public Object previousKey(Object key) {
        LinkEntry entry = (LinkEntry)this.getEntry(key);
        return entry == null || entry.before == this.header ? null : entry.before.getKey();
    }

    protected LinkEntry getEntry(int index) {
        LinkEntry entry;
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index " + index + " is less than zero");
        }
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index " + index + " is invalid for size " + this.size);
        }
        if (index < this.size / 2) {
            entry = this.header.after;
            for (int currentIndex = 0; currentIndex < index; ++currentIndex) {
                entry = entry.after;
            }
        } else {
            entry = this.header;
            for (int currentIndex = this.size; currentIndex > index; --currentIndex) {
                entry = entry.before;
            }
        }
        return entry;
    }

    protected void addEntry(AbstractHashedMap.HashEntry entry, int hashIndex) {
        LinkEntry link = (LinkEntry)entry;
        link.after = this.header;
        link.before = this.header.before;
        this.header.before.after = link;
        this.header.before = link;
        this.data[hashIndex] = entry;
    }

    protected AbstractHashedMap.HashEntry createEntry(AbstractHashedMap.HashEntry next, int hashCode, Object key, Object value) {
        return new LinkEntry(next, hashCode, key, value);
    }

    protected void removeEntry(AbstractHashedMap.HashEntry entry, int hashIndex, AbstractHashedMap.HashEntry previous) {
        LinkEntry link = (LinkEntry)entry;
        link.before.after = link.after;
        link.after.before = link.before;
        link.after = null;
        link.before = null;
        super.removeEntry(entry, hashIndex, previous);
    }

    protected LinkEntry entryBefore(LinkEntry entry) {
        return entry.before;
    }

    protected LinkEntry entryAfter(LinkEntry entry) {
        return entry.after;
    }

    public MapIterator mapIterator() {
        if (this.size == 0) {
            return EmptyOrderedMapIterator.INSTANCE;
        }
        return new LinkMapIterator(this);
    }

    public OrderedMapIterator orderedMapIterator() {
        if (this.size == 0) {
            return EmptyOrderedMapIterator.INSTANCE;
        }
        return new LinkMapIterator(this);
    }

    protected Iterator createEntrySetIterator() {
        if (this.size() == 0) {
            return EmptyOrderedIterator.INSTANCE;
        }
        return new EntrySetIterator(this);
    }

    protected Iterator createKeySetIterator() {
        if (this.size() == 0) {
            return EmptyOrderedIterator.INSTANCE;
        }
        return new KeySetIterator(this);
    }

    protected Iterator createValuesIterator() {
        if (this.size() == 0) {
            return EmptyOrderedIterator.INSTANCE;
        }
        return new ValuesIterator(this);
    }

    protected static abstract class LinkIterator
    implements OrderedIterator,
    ResettableIterator {
        protected final AbstractLinkedMap parent;
        protected LinkEntry last;
        protected LinkEntry next;
        protected int expectedModCount;

        protected LinkIterator(AbstractLinkedMap parent) {
            this.parent = parent;
            this.next = parent.header.after;
            this.expectedModCount = parent.modCount;
        }

        public boolean hasNext() {
            return this.next != this.parent.header;
        }

        public boolean hasPrevious() {
            return this.next.before != this.parent.header;
        }

        protected LinkEntry nextEntry() {
            if (this.parent.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            if (this.next == this.parent.header) {
                throw new NoSuchElementException("No next() entry in the iteration");
            }
            this.last = this.next;
            this.next = this.next.after;
            return this.last;
        }

        protected LinkEntry previousEntry() {
            if (this.parent.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            LinkEntry previous = this.next.before;
            if (previous == this.parent.header) {
                throw new NoSuchElementException("No previous() entry in the iteration");
            }
            this.next = previous;
            this.last = previous;
            return this.last;
        }

        protected LinkEntry currentEntry() {
            return this.last;
        }

        public void remove() {
            if (this.last == null) {
                throw new IllegalStateException("remove() can only be called once after next()");
            }
            if (this.parent.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            this.parent.remove(this.last.getKey());
            this.last = null;
            this.expectedModCount = this.parent.modCount;
        }

        public void reset() {
            this.last = null;
            this.next = this.parent.header.after;
        }

        public String toString() {
            if (this.last != null) {
                return "Iterator[" + this.last.getKey() + "=" + this.last.getValue() + "]";
            }
            return "Iterator[]";
        }
    }

    protected static class LinkEntry
    extends AbstractHashedMap.HashEntry {
        protected LinkEntry before;
        protected LinkEntry after;

        protected LinkEntry(AbstractHashedMap.HashEntry next, int hashCode, Object key, Object value) {
            super(next, hashCode, key, value);
        }
    }

    protected static class ValuesIterator
    extends LinkIterator {
        protected ValuesIterator(AbstractLinkedMap parent) {
            super(parent);
        }

        public Object next() {
            return super.nextEntry().getValue();
        }

        public Object previous() {
            return super.previousEntry().getValue();
        }
    }

    protected static class KeySetIterator
    extends EntrySetIterator {
        protected KeySetIterator(AbstractLinkedMap parent) {
            super(parent);
        }

        public Object next() {
            return super.nextEntry().getKey();
        }

        public Object previous() {
            return super.previousEntry().getKey();
        }
    }

    protected static class EntrySetIterator
    extends LinkIterator {
        protected EntrySetIterator(AbstractLinkedMap parent) {
            super(parent);
        }

        public Object next() {
            return super.nextEntry();
        }

        public Object previous() {
            return super.previousEntry();
        }
    }

    protected static class LinkMapIterator
    extends LinkIterator
    implements OrderedMapIterator {
        protected LinkMapIterator(AbstractLinkedMap parent) {
            super(parent);
        }

        public Object next() {
            return super.nextEntry().getKey();
        }

        public Object previous() {
            return super.previousEntry().getKey();
        }

        public Object getKey() {
            LinkEntry current = this.currentEntry();
            if (current == null) {
                throw new IllegalStateException("getKey() can only be called after next() and before remove()");
            }
            return current.getKey();
        }

        public Object getValue() {
            LinkEntry current = this.currentEntry();
            if (current == null) {
                throw new IllegalStateException("getValue() can only be called after next() and before remove()");
            }
            return current.getValue();
        }

        public Object setValue(Object value) {
            LinkEntry current = this.currentEntry();
            if (current == null) {
                throw new IllegalStateException("setValue() can only be called after next() and before remove()");
            }
            return current.setValue(value);
        }
    }
}

