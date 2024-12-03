/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.mozilla.javascript.ConsString;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Undefined;

public class Hashtable
implements Serializable,
Iterable<Entry> {
    private static final long serialVersionUID = -7151554912419543747L;
    private final HashMap<Object, Entry> map = new HashMap();
    private Entry first = null;
    private Entry last = null;

    private static Entry makeDummy() {
        Entry d = new Entry();
        d.clear();
        return d;
    }

    public int size() {
        return this.map.size();
    }

    public void put(Object key, Object value) {
        Entry nv = new Entry(key, value);
        Entry ev = this.map.putIfAbsent(nv, nv);
        if (ev == null) {
            if (this.first == null) {
                this.first = this.last = nv;
            } else {
                this.last.next = nv;
                nv.prev = this.last;
                this.last = nv;
            }
        } else {
            ev.value = value;
        }
    }

    public Object get(Object key) {
        Entry e = new Entry(key, null);
        Entry v = this.map.get(e);
        if (v == null) {
            return null;
        }
        return v.value;
    }

    public Entry getEntry(Object key) {
        Entry e = new Entry(key, null);
        return this.map.get(e);
    }

    public boolean has(Object key) {
        Entry e = new Entry(key, null);
        return this.map.containsKey(e);
    }

    public Object delete(Object key) {
        Entry e = new Entry(key, null);
        Entry v = this.map.remove(e);
        if (v == null) {
            return null;
        }
        if (v == this.first) {
            if (v == this.last) {
                v.clear();
                v.prev = null;
            } else {
                this.first = v.next;
                this.first.prev = null;
                if (this.first.next != null) {
                    this.first.next.prev = this.first;
                }
            }
        } else {
            Entry prev = v.prev;
            prev.next = v.next;
            v.prev = null;
            if (v.next != null) {
                v.next.prev = prev;
            } else {
                assert (v == this.last);
                this.last = prev;
            }
        }
        Object ret = v.value;
        v.clear();
        return ret;
    }

    public boolean deleteEntry(Object key) {
        Entry e = new Entry(key, null);
        Entry v = this.map.remove(e);
        if (v == null) {
            return false;
        }
        if (v == this.first) {
            if (v == this.last) {
                v.clear();
                v.prev = null;
            } else {
                this.first = v.next;
                this.first.prev = null;
                if (this.first.next != null) {
                    this.first.next.prev = this.first;
                }
            }
        } else {
            Entry prev = v.prev;
            prev.next = v.next;
            v.prev = null;
            if (v.next != null) {
                v.next.prev = prev;
            } else {
                assert (v == this.last);
                this.last = prev;
            }
        }
        v.clear();
        return true;
    }

    public void clear() {
        Iterator<Entry> it = this.iterator();
        it.forEachRemaining(Entry::clear);
        if (this.first != null) {
            Entry dummy;
            this.last.next = dummy = Hashtable.makeDummy();
            this.first = this.last = dummy;
        }
        this.map.clear();
    }

    @Override
    public Iterator<Entry> iterator() {
        return new Iter(this.first);
    }

    private static final class Iter
    implements Iterator<Entry> {
        private Entry pos;

        Iter(Entry start) {
            Entry dummy = Hashtable.makeDummy();
            dummy.next = start;
            this.pos = dummy;
        }

        private void skipDeleted() {
            while (this.pos.next != null && this.pos.next.deleted) {
                this.pos = this.pos.next;
            }
        }

        @Override
        public boolean hasNext() {
            this.skipDeleted();
            return this.pos != null && this.pos.next != null;
        }

        @Override
        public Entry next() {
            this.skipDeleted();
            if (this.pos == null || this.pos.next == null) {
                throw new NoSuchElementException();
            }
            Entry e = this.pos.next;
            this.pos = this.pos.next;
            return e;
        }
    }

    public static final class Entry
    implements Serializable {
        private static final long serialVersionUID = 4086572107122965503L;
        protected Object key;
        protected Object value;
        protected boolean deleted;
        protected Entry next;
        protected Entry prev;
        private final int hashCode;

        Entry() {
            this.hashCode = 0;
        }

        Entry(Object k, Object value) {
            this.key = k instanceof Number ? (k instanceof Double || k instanceof BigInteger ? k : Double.valueOf(((Number)k).doubleValue())) : (k instanceof ConsString ? k.toString() : k);
            this.hashCode = this.key == null ? 0 : (k.equals(ScriptRuntime.negativeZeroObj) ? 0 : this.key.hashCode());
            this.value = value;
        }

        public Object key() {
            return this.key;
        }

        public Object value() {
            return this.value;
        }

        void clear() {
            this.key = Undefined.instance;
            this.value = Undefined.instance;
            this.deleted = true;
        }

        public int hashCode() {
            return this.hashCode;
        }

        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            try {
                return ScriptRuntime.sameZero(this.key, ((Entry)o).key);
            }
            catch (ClassCastException cce) {
                return false;
            }
        }
    }
}

