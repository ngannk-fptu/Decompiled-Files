/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.util.CleanerThread$WeakReferenceCleared
 */
package org.apache.batik.dom.util;

import org.apache.batik.util.CleanerThread;
import org.w3c.dom.Element;

public class DocumentDescriptor {
    protected static final int INITIAL_CAPACITY = 101;
    protected Entry[] table = new Entry[101];
    protected int count;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getNumberOfElements() {
        DocumentDescriptor documentDescriptor = this;
        synchronized (documentDescriptor) {
            return this.count;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getLocationLine(Element elt) {
        DocumentDescriptor documentDescriptor = this;
        synchronized (documentDescriptor) {
            int hash = elt.hashCode() & Integer.MAX_VALUE;
            int index = hash % this.table.length;
            Entry e = this.table[index];
            while (e != null) {
                Object o;
                if (e.hash == hash && (o = e.get()) == elt) {
                    return e.locationLine;
                }
                e = e.next;
            }
        }
        return 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getLocationColumn(Element elt) {
        DocumentDescriptor documentDescriptor = this;
        synchronized (documentDescriptor) {
            int hash = elt.hashCode() & Integer.MAX_VALUE;
            int index = hash % this.table.length;
            Entry e = this.table[index];
            while (e != null) {
                Object o;
                if (e.hash == hash && (o = e.get()) == elt) {
                    return e.locationColumn;
                }
                e = e.next;
            }
        }
        return 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setLocation(Element elt, int line, int col) {
        DocumentDescriptor documentDescriptor = this;
        synchronized (documentDescriptor) {
            Entry e;
            int hash = elt.hashCode() & Integer.MAX_VALUE;
            int index = hash % this.table.length;
            Entry e2 = this.table[index];
            while (e2 != null) {
                Object o;
                if (e2.hash == hash && (o = e2.get()) == elt) {
                    e2.locationLine = line;
                }
                e2 = e2.next;
            }
            int len = this.table.length;
            if (this.count++ >= len - (len >> 2)) {
                this.rehash();
                index = hash % this.table.length;
            }
            this.table[index] = e = new Entry(hash, elt, line, col, this.table[index]);
        }
    }

    protected void rehash() {
        Entry[] oldTable = this.table;
        this.table = new Entry[oldTable.length * 2 + 1];
        for (int i = oldTable.length - 1; i >= 0; --i) {
            Entry old = oldTable[i];
            while (old != null) {
                Entry e = old;
                old = old.next;
                int index = e.hash % this.table.length;
                e.next = this.table[index];
                this.table[index] = e;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void removeEntry(Entry e) {
        DocumentDescriptor documentDescriptor = this;
        synchronized (documentDescriptor) {
            int hash = e.hash;
            int index = hash % this.table.length;
            Entry curr = this.table[index];
            Entry prev = null;
            while (curr != e) {
                prev = curr;
                curr = curr.next;
            }
            if (curr == null) {
                return;
            }
            if (prev == null) {
                this.table[index] = curr.next;
            } else {
                prev.next = curr.next;
            }
            --this.count;
        }
    }

    protected class Entry
    extends CleanerThread.WeakReferenceCleared {
        public int hash;
        public int locationLine;
        public int locationColumn;
        public Entry next;

        public Entry(int hash, Element element, int locationLine, int locationColumn, Entry next) {
            super((Object)element);
            this.hash = hash;
            this.locationLine = locationLine;
            this.locationColumn = locationColumn;
            this.next = next;
        }

        public void cleared() {
            DocumentDescriptor.this.removeEntry(this);
        }
    }
}

