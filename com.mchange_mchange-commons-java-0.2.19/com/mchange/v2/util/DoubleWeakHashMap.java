/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.util;

import com.mchange.v1.util.AbstractMapEntry;
import com.mchange.v1.util.WrapperIterator;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class DoubleWeakHashMap
implements Map {
    HashMap inner;
    ReferenceQueue keyQ = new ReferenceQueue();
    ReferenceQueue valQ = new ReferenceQueue();
    CheckKeyHolder holder = new CheckKeyHolder();
    Set userKeySet = null;
    Collection valuesCollection = null;

    public DoubleWeakHashMap() {
        this.inner = new HashMap();
    }

    public DoubleWeakHashMap(int n) {
        this.inner = new HashMap(n);
    }

    public DoubleWeakHashMap(int n, float f) {
        this.inner = new HashMap(n, f);
    }

    public DoubleWeakHashMap(Map map) {
        this();
        this.putAll(map);
    }

    public void cleanCleared() {
        WVal wVal;
        WKey wKey;
        while ((wKey = (WKey)this.keyQ.poll()) != null) {
            this.inner.remove(wKey);
        }
        while ((wVal = (WVal)this.valQ.poll()) != null) {
            this.inner.remove(wVal.getWKey());
        }
    }

    @Override
    public void clear() {
        this.cleanCleared();
        this.inner.clear();
    }

    @Override
    public boolean containsKey(Object object) {
        this.cleanCleared();
        try {
            boolean bl = this.inner.containsKey(this.holder.set(object));
            return bl;
        }
        finally {
            this.holder.clear();
        }
    }

    @Override
    public boolean containsValue(Object object) {
        for (WVal wVal : this.inner.values()) {
            if (!object.equals(wVal.get())) continue;
            return true;
        }
        return false;
    }

    public Set entrySet() {
        this.cleanCleared();
        return new UserEntrySet();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object get(Object object) {
        try {
            this.cleanCleared();
            WVal wVal = (WVal)this.inner.get(this.holder.set(object));
            Object var3_3 = wVal == null ? null : wVal.get();
            return var3_3;
        }
        finally {
            this.holder.clear();
        }
    }

    @Override
    public boolean isEmpty() {
        this.cleanCleared();
        return this.inner.isEmpty();
    }

    public Set keySet() {
        this.cleanCleared();
        if (this.userKeySet == null) {
            this.userKeySet = new UserKeySet();
        }
        return this.userKeySet;
    }

    public Object put(Object object, Object object2) {
        this.cleanCleared();
        WVal wVal = this.doPut(object, object2);
        if (wVal != null) {
            return wVal.get();
        }
        return null;
    }

    private WVal doPut(Object object, Object object2) {
        WKey wKey = new WKey(object, this.keyQ);
        WVal wVal = new WVal(wKey, object2, this.valQ);
        return this.inner.put(wKey, wVal);
    }

    public void putAll(Map map) {
        this.cleanCleared();
        for (Map.Entry entry : map.entrySet()) {
            this.doPut(entry.getKey(), entry.getValue());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object remove(Object object) {
        try {
            this.cleanCleared();
            WVal wVal = (WVal)this.inner.remove(this.holder.set(object));
            Object var3_3 = wVal == null ? null : wVal.get();
            return var3_3;
        }
        finally {
            this.holder.clear();
        }
    }

    @Override
    public int size() {
        this.cleanCleared();
        return this.inner.size();
    }

    public Collection values() {
        if (this.valuesCollection == null) {
            this.valuesCollection = new ValuesCollection();
        }
        return this.valuesCollection;
    }

    class ValuesCollection
    implements Collection {
        ValuesCollection() {
        }

        public boolean add(Object object) {
            DoubleWeakHashMap.this.cleanCleared();
            throw new UnsupportedOperationException("DoubleWeakHashMap does not support adding to its values Collection.");
        }

        public boolean addAll(Collection collection) {
            DoubleWeakHashMap.this.cleanCleared();
            throw new UnsupportedOperationException("DoubleWeakHashMap does not support adding to its values Collection.");
        }

        @Override
        public void clear() {
            DoubleWeakHashMap.this.clear();
        }

        @Override
        public boolean contains(Object object) {
            return DoubleWeakHashMap.this.containsValue(object);
        }

        public boolean containsAll(Collection collection) {
            Iterator iterator = collection.iterator();
            while (iterator.hasNext()) {
                if (this.contains(iterator.next())) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean isEmpty() {
            return DoubleWeakHashMap.this.isEmpty();
        }

        @Override
        public Iterator iterator() {
            return new WrapperIterator(DoubleWeakHashMap.this.inner.values().iterator(), true){

                @Override
                protected Object transformObject(Object object) {
                    Object t = ((WVal)object).get();
                    if (t == null) {
                        return WrapperIterator.SKIP_TOKEN;
                    }
                    return t;
                }
            };
        }

        @Override
        public boolean remove(Object object) {
            DoubleWeakHashMap.this.cleanCleared();
            return this.removeValue(object);
        }

        public boolean removeAll(Collection collection) {
            DoubleWeakHashMap.this.cleanCleared();
            boolean bl = false;
            Iterator iterator = collection.iterator();
            while (iterator.hasNext()) {
                bl |= this.removeValue(iterator.next());
            }
            return bl;
        }

        public boolean retainAll(Collection collection) {
            DoubleWeakHashMap.this.cleanCleared();
            return this.retainValues(collection);
        }

        @Override
        public int size() {
            return DoubleWeakHashMap.this.size();
        }

        @Override
        public Object[] toArray() {
            DoubleWeakHashMap.this.cleanCleared();
            return new ArrayList(this).toArray();
        }

        public Object[] toArray(Object[] objectArray) {
            DoubleWeakHashMap.this.cleanCleared();
            return new ArrayList(this).toArray(objectArray);
        }

        private boolean removeValue(Object object) {
            boolean bl = false;
            Iterator iterator = DoubleWeakHashMap.this.inner.values().iterator();
            while (iterator.hasNext()) {
                WVal wVal = (WVal)iterator.next();
                if (!object.equals(wVal.get())) continue;
                iterator.remove();
                bl = true;
            }
            return bl;
        }

        private boolean retainValues(Collection collection) {
            boolean bl = false;
            Iterator iterator = DoubleWeakHashMap.this.inner.values().iterator();
            while (iterator.hasNext()) {
                WVal wVal = (WVal)iterator.next();
                if (collection.contains(wVal.get())) continue;
                iterator.remove();
                bl = true;
            }
            return bl;
        }
    }

    class UserKeySet
    implements Set {
        UserKeySet() {
        }

        @Override
        public boolean add(Object object) {
            DoubleWeakHashMap.this.cleanCleared();
            throw new UnsupportedOperationException("You cannot add to a Map's key set.");
        }

        @Override
        public boolean addAll(Collection collection) {
            DoubleWeakHashMap.this.cleanCleared();
            throw new UnsupportedOperationException("You cannot add to a Map's key set.");
        }

        @Override
        public void clear() {
            DoubleWeakHashMap.this.clear();
        }

        @Override
        public boolean contains(Object object) {
            return DoubleWeakHashMap.this.containsKey(object);
        }

        @Override
        public boolean containsAll(Collection collection) {
            Iterator iterator = collection.iterator();
            while (iterator.hasNext()) {
                if (this.contains(iterator.next())) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean isEmpty() {
            return DoubleWeakHashMap.this.isEmpty();
        }

        @Override
        public Iterator iterator() {
            DoubleWeakHashMap.this.cleanCleared();
            return new WrapperIterator(DoubleWeakHashMap.this.inner.keySet().iterator(), true){

                @Override
                protected Object transformObject(Object object) {
                    Object t = ((WKey)object).get();
                    if (t == null) {
                        return WrapperIterator.SKIP_TOKEN;
                    }
                    return t;
                }
            };
        }

        @Override
        public boolean remove(Object object) {
            return DoubleWeakHashMap.this.remove(object) != null;
        }

        @Override
        public boolean removeAll(Collection collection) {
            boolean bl = false;
            Iterator iterator = collection.iterator();
            while (iterator.hasNext()) {
                bl |= this.remove(iterator.next());
            }
            return bl;
        }

        @Override
        public boolean retainAll(Collection collection) {
            boolean bl = false;
            Iterator iterator = this.iterator();
            while (iterator.hasNext()) {
                if (collection.contains(iterator.next())) continue;
                iterator.remove();
                bl = true;
            }
            return bl;
        }

        @Override
        public int size() {
            return DoubleWeakHashMap.this.size();
        }

        @Override
        public Object[] toArray() {
            DoubleWeakHashMap.this.cleanCleared();
            return new HashSet(this).toArray();
        }

        @Override
        public Object[] toArray(Object[] objectArray) {
            DoubleWeakHashMap.this.cleanCleared();
            return new HashSet(this).toArray(objectArray);
        }
    }

    class UserEntry
    extends AbstractMapEntry {
        Map.Entry innerEntry;
        Object key;
        Object val;

        UserEntry(Map.Entry entry, Object object, Object object2) {
            this.innerEntry = entry;
            this.key = object;
            this.val = object2;
        }

        @Override
        public final Object getKey() {
            return this.key;
        }

        @Override
        public final Object getValue() {
            return this.val;
        }

        @Override
        public final Object setValue(Object object) {
            return this.innerEntry.setValue(new WVal((WKey)this.innerEntry.getKey(), object, DoubleWeakHashMap.this.valQ));
        }
    }

    private final class UserEntrySet
    extends AbstractSet {
        private UserEntrySet() {
        }

        private Set innerEntrySet() {
            DoubleWeakHashMap.this.cleanCleared();
            return DoubleWeakHashMap.this.inner.entrySet();
        }

        @Override
        public Iterator iterator() {
            return new WrapperIterator(this.innerEntrySet().iterator(), true){

                @Override
                protected Object transformObject(Object object) {
                    Map.Entry entry = (Map.Entry)object;
                    Object t = ((WKey)entry.getKey()).get();
                    Object t2 = ((WVal)entry.getValue()).get();
                    if (t == null || t2 == null) {
                        return WrapperIterator.SKIP_TOKEN;
                    }
                    return new UserEntry(entry, t, t2);
                }
            };
        }

        @Override
        public int size() {
            return this.innerEntrySet().size();
        }
    }

    static final class WVal
    extends WeakReference {
        WKey key;

        WVal(WKey wKey, Object object, ReferenceQueue referenceQueue) {
            super(object, referenceQueue);
            this.key = wKey;
        }

        public WKey getWKey() {
            return this.key;
        }
    }

    static final class WKey
    extends WeakReference {
        int cachedHash;

        WKey(Object object, ReferenceQueue referenceQueue) {
            super(object, referenceQueue);
            this.cachedHash = object.hashCode();
        }

        public int hashCode() {
            return this.cachedHash;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object instanceof WKey) {
                WKey wKey = (WKey)object;
                Object t = this.get();
                Object t2 = wKey.get();
                if (t == null || t2 == null) {
                    return false;
                }
                return t.equals(t2);
            }
            if (object instanceof CheckKeyHolder) {
                CheckKeyHolder checkKeyHolder = (CheckKeyHolder)object;
                Object t = this.get();
                Object object2 = checkKeyHolder.get();
                if (t == null || object2 == null) {
                    return false;
                }
                return t.equals(object2);
            }
            return false;
        }
    }

    static final class CheckKeyHolder {
        Object checkKey;

        CheckKeyHolder() {
        }

        public Object get() {
            return this.checkKey;
        }

        public CheckKeyHolder set(Object object) {
            assert (this.checkKey == null) : "Illegal concurrenct use of DoubleWeakHashMap!";
            this.checkKey = object;
            return this;
        }

        public void clear() {
            this.checkKey = null;
        }

        public int hashCode() {
            return this.checkKey.hashCode();
        }

        public boolean equals(Object object) {
            assert (this.get() != null) : "CheckedKeyHolder should never do an equality check while its value is null.";
            if (this == object) {
                return true;
            }
            if (object instanceof CheckKeyHolder) {
                return this.get().equals(((CheckKeyHolder)object).get());
            }
            if (object instanceof WKey) {
                return this.get().equals(((WKey)object).get());
            }
            return false;
        }
    }
}

