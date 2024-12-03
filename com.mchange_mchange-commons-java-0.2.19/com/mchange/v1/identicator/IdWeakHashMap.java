/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.identicator;

import com.mchange.v1.identicator.IdHashKey;
import com.mchange.v1.identicator.IdMap;
import com.mchange.v1.identicator.Identicator;
import com.mchange.v1.identicator.WeakIdHashKey;
import com.mchange.v1.util.WrapperIterator;
import java.lang.ref.ReferenceQueue;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class IdWeakHashMap
extends IdMap
implements Map {
    ReferenceQueue rq = new ReferenceQueue();

    public IdWeakHashMap(Identicator identicator) {
        super(new HashMap(), identicator);
    }

    @Override
    public int size() {
        this.cleanCleared();
        return super.size();
    }

    @Override
    public boolean isEmpty() {
        try {
            boolean bl = super.isEmpty();
            return bl;
        }
        finally {
            this.cleanCleared();
        }
    }

    @Override
    public boolean containsKey(Object object) {
        try {
            boolean bl = super.containsKey(object);
            return bl;
        }
        finally {
            this.cleanCleared();
        }
    }

    @Override
    public boolean containsValue(Object object) {
        try {
            boolean bl = super.containsValue(object);
            return bl;
        }
        finally {
            this.cleanCleared();
        }
    }

    @Override
    public Object get(Object object) {
        try {
            Object object2 = super.get(object);
            return object2;
        }
        finally {
            this.cleanCleared();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object put(Object object, Object object2) {
        try {
            Object object3 = super.put(object, object2);
            return object3;
        }
        finally {
            this.cleanCleared();
        }
    }

    @Override
    public Object remove(Object object) {
        try {
            Object object2 = super.remove(object);
            return object2;
        }
        finally {
            this.cleanCleared();
        }
    }

    public void putAll(Map map) {
        try {
            super.putAll(map);
        }
        finally {
            this.cleanCleared();
        }
    }

    @Override
    public void clear() {
        try {
            super.clear();
        }
        finally {
            this.cleanCleared();
        }
    }

    public Set keySet() {
        try {
            Set set = super.keySet();
            return set;
        }
        finally {
            this.cleanCleared();
        }
    }

    public Collection values() {
        try {
            Collection collection = super.values();
            return collection;
        }
        finally {
            this.cleanCleared();
        }
    }

    @Override
    public Set entrySet() {
        try {
            WeakUserEntrySet weakUserEntrySet = new WeakUserEntrySet();
            return weakUserEntrySet;
        }
        finally {
            this.cleanCleared();
        }
    }

    @Override
    public boolean equals(Object object) {
        try {
            boolean bl = super.equals(object);
            return bl;
        }
        finally {
            this.cleanCleared();
        }
    }

    @Override
    public int hashCode() {
        try {
            int n = super.hashCode();
            return n;
        }
        finally {
            this.cleanCleared();
        }
    }

    @Override
    protected IdHashKey createIdKey(Object object) {
        return new WeakIdHashKey(object, this.id, this.rq);
    }

    private void cleanCleared() {
        WeakIdHashKey.Ref ref;
        while ((ref = (WeakIdHashKey.Ref)this.rq.poll()) != null) {
            this.removeIdHashKey(ref.getKey());
        }
    }

    private final class WeakUserEntrySet
    extends AbstractSet {
        Set innerEntries;

        private WeakUserEntrySet() {
            this.innerEntries = IdWeakHashMap.this.internalEntrySet();
        }

        @Override
        public Iterator iterator() {
            try {
                WrapperIterator wrapperIterator = new WrapperIterator(this.innerEntries.iterator(), true){

                    @Override
                    protected Object transformObject(Object object) {
                        Map.Entry entry = (Map.Entry)object;
                        final Object object2 = ((IdHashKey)entry.getKey()).getKeyObj();
                        if (object2 == null) {
                            return WrapperIterator.SKIP_TOKEN;
                        }
                        return new IdMap.UserEntry(entry){
                            Object preventRefClear;
                            {
                                super(entry);
                                this.preventRefClear = object2;
                            }
                        };
                    }
                };
                return wrapperIterator;
            }
            finally {
                IdWeakHashMap.this.cleanCleared();
            }
        }

        @Override
        public int size() {
            IdWeakHashMap.this.cleanCleared();
            return this.innerEntries.size();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean contains(Object object) {
            try {
                if (object instanceof Map.Entry) {
                    Map.Entry entry = (Map.Entry)object;
                    boolean bl = this.innerEntries.contains(IdWeakHashMap.this.createIdEntry(entry));
                    return bl;
                }
                boolean bl = false;
                return bl;
            }
            finally {
                IdWeakHashMap.this.cleanCleared();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(Object object) {
            try {
                if (object instanceof Map.Entry) {
                    Map.Entry entry = (Map.Entry)object;
                    boolean bl = this.innerEntries.remove(IdWeakHashMap.this.createIdEntry(entry));
                    return bl;
                }
                boolean bl = false;
                return bl;
            }
            finally {
                IdWeakHashMap.this.cleanCleared();
            }
        }

        @Override
        public void clear() {
            try {
                IdWeakHashMap.this.inner.clear();
            }
            finally {
                IdWeakHashMap.this.cleanCleared();
            }
        }
    }
}

