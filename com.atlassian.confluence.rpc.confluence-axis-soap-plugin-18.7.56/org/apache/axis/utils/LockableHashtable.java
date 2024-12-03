/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.utils;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class LockableHashtable
extends Hashtable {
    Vector lockedEntries;
    private Hashtable parent = null;

    public LockableHashtable() {
    }

    public LockableHashtable(int p1, float p2) {
        super(p1, p2);
    }

    public LockableHashtable(Map p1) {
        super(p1);
    }

    public LockableHashtable(int p1) {
        super(p1);
    }

    public synchronized void setParent(Hashtable parent) {
        this.parent = parent;
    }

    public synchronized Hashtable getParent() {
        return this.parent;
    }

    public Set getAllKeys() {
        HashSet set = new HashSet();
        set.addAll(super.keySet());
        Hashtable p = this.parent;
        while (p != null) {
            set.addAll(p.keySet());
            if (p instanceof LockableHashtable) {
                p = ((LockableHashtable)p).getParent();
                continue;
            }
            p = null;
        }
        return set;
    }

    public synchronized Object get(Object key) {
        Object ret = super.get(key);
        if (ret == null && this.parent != null) {
            ret = this.parent.get(key);
        }
        return ret;
    }

    public synchronized Object put(Object p1, Object p2, boolean locked) {
        if (this.lockedEntries != null && this.containsKey(p1) && this.lockedEntries.contains(p1)) {
            return null;
        }
        if (locked) {
            if (this.lockedEntries == null) {
                this.lockedEntries = new Vector();
            }
            this.lockedEntries.add(p1);
        }
        return super.put(p1, p2);
    }

    public synchronized Object put(Object p1, Object p2) {
        return this.put(p1, p2, false);
    }

    public synchronized Object remove(Object p1) {
        if (this.lockedEntries != null && this.lockedEntries.contains(p1)) {
            return null;
        }
        return super.remove(p1);
    }

    public boolean isKeyLocked(Object key) {
        return this.lockedEntries != null && this.lockedEntries.contains(key);
    }
}

