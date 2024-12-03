/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.util;

import com.sun.xml.fastinfoset.CommonResourceBundle;
import org.jvnet.fastinfoset.FastInfosetException;

public class DuplicateAttributeVerifier {
    public static final int MAP_SIZE = 256;
    public int _currentIteration;
    private Entry[] _map;
    public final Entry _poolHead;
    public Entry _poolCurrent;
    private Entry _poolTail;

    public DuplicateAttributeVerifier() {
        this._poolTail = this._poolHead = new Entry();
    }

    public final void clear() {
        this._currentIteration = 0;
        Entry e = this._poolHead;
        while (e != null) {
            e.iteration = 0;
            e = e.poolNext;
        }
        this.reset();
    }

    public final void reset() {
        this._poolCurrent = this._poolHead;
        if (this._map == null) {
            this._map = new Entry[256];
        }
    }

    private final void increasePool(int capacity) {
        if (this._map == null) {
            this._map = new Entry[256];
            this._poolCurrent = this._poolHead;
        } else {
            Entry tail = this._poolTail;
            for (int i = 0; i < capacity; ++i) {
                Entry e = new Entry();
                this._poolTail.poolNext = e;
                this._poolTail = e;
            }
            this._poolCurrent = tail.poolNext;
        }
    }

    public final void checkForDuplicateAttribute(int hash, int value) throws FastInfosetException {
        if (this._poolCurrent == null) {
            this.increasePool(16);
        }
        Entry newEntry = this._poolCurrent;
        this._poolCurrent = this._poolCurrent.poolNext;
        Entry head = this._map[hash];
        if (head == null || head.iteration < this._currentIteration) {
            newEntry.hashNext = null;
            this._map[hash] = newEntry;
            newEntry.iteration = this._currentIteration;
            newEntry.value = value;
        } else {
            Entry e = head;
            do {
                if (e.value != value) continue;
                this.reset();
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.duplicateAttribute"));
            } while ((e = e.hashNext) != null);
            newEntry.hashNext = head;
            this._map[hash] = newEntry;
            newEntry.iteration = this._currentIteration;
            newEntry.value = value;
        }
    }

    public static class Entry {
        private int iteration;
        private int value;
        private Entry hashNext;
        private Entry poolNext;
    }
}

