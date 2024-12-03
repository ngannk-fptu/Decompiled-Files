/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.util;

import com.sun.media.jai.util.JaiI18N;
import java.io.Serializable;
import java.util.Hashtable;
import javax.media.jai.util.CaselessStringKey;

public class CaselessStringArrayTable
implements Serializable {
    private CaselessStringKey[] keys;
    private Hashtable indices;

    public CaselessStringArrayTable() {
        this((CaselessStringKey[])null);
    }

    public CaselessStringArrayTable(CaselessStringKey[] keys) {
        this.keys = keys;
        this.indices = new Hashtable();
        if (keys != null) {
            for (int i = 0; i < keys.length; ++i) {
                this.indices.put(keys[i], new Integer(i));
            }
        }
    }

    public CaselessStringArrayTable(String[] keys) {
        this(CaselessStringArrayTable.toCaselessStringKey(keys));
    }

    private static CaselessStringKey[] toCaselessStringKey(String[] strings) {
        if (strings == null) {
            return null;
        }
        CaselessStringKey[] keys = new CaselessStringKey[strings.length];
        for (int i = 0; i < strings.length; ++i) {
            keys[i] = new CaselessStringKey(strings[i]);
        }
        return keys;
    }

    public int indexOf(CaselessStringKey key) {
        if (key == null) {
            throw new IllegalArgumentException(JaiI18N.getString("CaselessStringArrayTable0"));
        }
        Integer i = (Integer)this.indices.get(key);
        if (i == null) {
            throw new IllegalArgumentException(key.getName() + " - " + JaiI18N.getString("CaselessStringArrayTable1"));
        }
        return i;
    }

    public int indexOf(String key) {
        return this.indexOf(new CaselessStringKey(key));
    }

    public String getName(int i) {
        if (this.keys == null) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return this.keys[i].getName();
    }

    public CaselessStringKey get(int i) {
        if (this.keys == null) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return this.keys[i];
    }

    public boolean contains(CaselessStringKey key) {
        if (key == null) {
            throw new IllegalArgumentException(JaiI18N.getString("CaselessStringArrayTable0"));
        }
        return this.indices.get(key) != null;
    }

    public boolean contains(String key) {
        return this.contains(new CaselessStringKey(key));
    }
}

