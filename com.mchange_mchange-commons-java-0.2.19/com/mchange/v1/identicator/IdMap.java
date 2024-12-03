/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.identicator;

import com.mchange.v1.identicator.IdHashKey;
import com.mchange.v1.identicator.Identicator;
import com.mchange.v1.util.AbstractMapEntry;
import com.mchange.v1.util.SimpleMapEntry;
import com.mchange.v1.util.WrapperIterator;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

abstract class IdMap
extends AbstractMap
implements Map {
    Map inner;
    Identicator id;

    protected IdMap(Map map, Identicator identicator) {
        this.inner = map;
        this.id = identicator;
    }

    public Object put(Object object, Object object2) {
        return this.inner.put(this.createIdKey(object), object2);
    }

    @Override
    public boolean containsKey(Object object) {
        return this.inner.containsKey(this.createIdKey(object));
    }

    public Object get(Object object) {
        return this.inner.get(this.createIdKey(object));
    }

    public Object remove(Object object) {
        return this.inner.remove(this.createIdKey(object));
    }

    protected Object removeIdHashKey(IdHashKey idHashKey) {
        return this.inner.remove(idHashKey);
    }

    public Set entrySet() {
        return new UserEntrySet();
    }

    protected final Set internalEntrySet() {
        return this.inner.entrySet();
    }

    protected abstract IdHashKey createIdKey(Object var1);

    protected final Map.Entry createIdEntry(Object object, Object object2) {
        return new SimpleMapEntry(this.createIdKey(object), object2);
    }

    protected final Map.Entry createIdEntry(Map.Entry entry) {
        return this.createIdEntry(entry.getKey(), entry.getValue());
    }

    protected static class UserEntry
    extends AbstractMapEntry {
        private Map.Entry innerEntry;

        UserEntry(Map.Entry entry) {
            this.innerEntry = entry;
        }

        @Override
        public final Object getKey() {
            return ((IdHashKey)this.innerEntry.getKey()).getKeyObj();
        }

        @Override
        public final Object getValue() {
            return this.innerEntry.getValue();
        }

        @Override
        public final Object setValue(Object object) {
            return this.innerEntry.setValue(object);
        }
    }

    private final class UserEntrySet
    extends AbstractSet {
        Set innerEntries;

        private UserEntrySet() {
            this.innerEntries = IdMap.this.inner.entrySet();
        }

        @Override
        public Iterator iterator() {
            return new WrapperIterator(this.innerEntries.iterator(), true){

                @Override
                protected Object transformObject(Object object) {
                    return new UserEntry((Map.Entry)object);
                }
            };
        }

        @Override
        public int size() {
            return this.innerEntries.size();
        }

        @Override
        public boolean contains(Object object) {
            if (object instanceof Map.Entry) {
                Map.Entry entry = (Map.Entry)object;
                return this.innerEntries.contains(IdMap.this.createIdEntry(entry));
            }
            return false;
        }

        @Override
        public boolean remove(Object object) {
            if (object instanceof Map.Entry) {
                Map.Entry entry = (Map.Entry)object;
                return this.innerEntries.remove(IdMap.this.createIdEntry(entry));
            }
            return false;
        }

        @Override
        public void clear() {
            IdMap.this.inner.clear();
        }
    }
}

