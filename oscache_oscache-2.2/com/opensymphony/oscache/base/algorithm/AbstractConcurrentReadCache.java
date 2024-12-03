/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.oscache.base.algorithm;

import com.opensymphony.oscache.base.CacheEntry;
import com.opensymphony.oscache.base.persistence.CachePersistenceException;
import com.opensymphony.oscache.base.persistence.PersistenceListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractConcurrentReadCache
extends AbstractMap
implements Map,
Cloneable,
Serializable {
    public static int DEFAULT_INITIAL_CAPACITY = 32;
    private static final int MINIMUM_CAPACITY = 4;
    private static final int MAXIMUM_CAPACITY = 0x40000000;
    public static final float DEFAULT_LOAD_FACTOR = 0.75f;
    protected static final String NULL = "_nul!~";
    protected static Log log = LogFactory.getLog((Class)(class$com$opensymphony$oscache$base$algorithm$AbstractConcurrentReadCache == null ? (class$com$opensymphony$oscache$base$algorithm$AbstractConcurrentReadCache = AbstractConcurrentReadCache.class$("com.opensymphony.oscache.base.algorithm.AbstractConcurrentReadCache")) : class$com$opensymphony$oscache$base$algorithm$AbstractConcurrentReadCache));
    protected final Boolean barrierLock = new Boolean(true);
    protected transient Object lastWrite;
    protected transient Entry[] table;
    protected transient int count;
    protected PersistenceListener persistenceListener = null;
    protected boolean memoryCaching = true;
    protected boolean unlimitedDiskCache = false;
    protected float loadFactor;
    protected final int DEFAULT_MAX_ENTRIES = 100;
    protected final int UNLIMITED = 0x7FFFFFFE;
    protected transient Collection values = null;
    protected HashMap groups = new HashMap();
    protected transient Set entrySet = null;
    protected transient Set keySet = null;
    protected int maxEntries = 100;
    protected int threshold;
    private boolean overflowPersistence = false;
    static /* synthetic */ Class class$com$opensymphony$oscache$base$algorithm$AbstractConcurrentReadCache;

    public AbstractConcurrentReadCache(int initialCapacity, float loadFactor) {
        if (loadFactor <= 0.0f) {
            throw new IllegalArgumentException("Illegal Load factor: " + loadFactor);
        }
        this.loadFactor = loadFactor;
        int cap = this.p2capacity(initialCapacity);
        this.table = new Entry[cap];
        this.threshold = (int)((float)cap * loadFactor);
    }

    public AbstractConcurrentReadCache(int initialCapacity) {
        this(initialCapacity, 0.75f);
    }

    public AbstractConcurrentReadCache() {
        this(DEFAULT_INITIAL_CAPACITY, 0.75f);
    }

    public AbstractConcurrentReadCache(Map t) {
        this(Math.max(2 * t.size(), 11), 0.75f);
        this.putAll(t);
    }

    public synchronized boolean isEmpty() {
        return this.count == 0;
    }

    public Set getGroup(String groupName) {
        if (log.isDebugEnabled()) {
            log.debug((Object)("getGroup called (group=" + groupName + ")"));
        }
        Set groupEntries = null;
        if (this.memoryCaching && this.groups != null) {
            groupEntries = this.getGroupForReading(groupName);
        }
        if (groupEntries == null) {
            groupEntries = this.persistRetrieveGroup(groupName);
        }
        return groupEntries;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setMaxEntries(int newLimit) {
        if (newLimit > 0) {
            this.maxEntries = newLimit;
            AbstractConcurrentReadCache abstractConcurrentReadCache = this;
            synchronized (abstractConcurrentReadCache) {
                while (this.size() > this.maxEntries) {
                    this.remove(this.removeItem(), false);
                }
            }
        } else {
            throw new IllegalArgumentException("Cache maximum number of entries must be at least 1");
        }
    }

    public int getMaxEntries() {
        return this.maxEntries;
    }

    public void setMemoryCaching(boolean memoryCaching) {
        this.memoryCaching = memoryCaching;
    }

    public boolean isMemoryCaching() {
        return this.memoryCaching;
    }

    public void setPersistenceListener(PersistenceListener listener) {
        this.persistenceListener = listener;
    }

    public PersistenceListener getPersistenceListener() {
        return this.persistenceListener;
    }

    public void setUnlimitedDiskCache(boolean unlimitedDiskCache) {
        this.unlimitedDiskCache = unlimitedDiskCache;
    }

    public boolean isUnlimitedDiskCache() {
        return this.unlimitedDiskCache;
    }

    public boolean isOverflowPersistence() {
        return this.overflowPersistence;
    }

    public void setOverflowPersistence(boolean overflowPersistence) {
        this.overflowPersistence = overflowPersistence;
    }

    public synchronized int capacity() {
        return this.table.length;
    }

    public synchronized void clear() {
        Entry[] tab = this.table;
        for (int i = 0; i < tab.length; ++i) {
            Entry e = tab[i];
            while (e != null) {
                e.value = null;
                this.itemRemoved(e.key);
                e = e.next;
            }
            tab[i] = null;
        }
        this.persistClear();
        this.count = 0;
        this.recordModification(tab);
    }

    public synchronized Object clone() {
        try {
            AbstractConcurrentReadCache t = (AbstractConcurrentReadCache)super.clone();
            t.keySet = null;
            t.entrySet = null;
            t.values = null;
            Entry[] tab = this.table;
            Entry[] ttab = t.table = new Entry[tab.length];
            for (int i = 0; i < tab.length; ++i) {
                Entry first = tab[i];
                if (first == null) continue;
                ttab[i] = (Entry)first.clone();
            }
            return t;
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    public boolean contains(Object value) {
        return this.containsValue(value);
    }

    public boolean containsKey(Object key) {
        return this.get(key) != null;
    }

    public boolean containsValue(Object value) {
        if (value == null) {
            throw new NullPointerException();
        }
        Entry[] tab = this.getTableForReading();
        for (int i = 0; i < tab.length; ++i) {
            Entry e = tab[i];
            while (e != null) {
                Object v = e.value;
                if (v != null && value.equals(v)) {
                    return true;
                }
                e = e.next;
            }
        }
        return false;
    }

    public Enumeration elements() {
        return new ValueIterator();
    }

    public Set entrySet() {
        Set es = this.entrySet;
        if (es != null) {
            return es;
        }
        this.entrySet = new AbstractSet(){

            public Iterator iterator() {
                return new HashIterator();
            }

            public boolean contains(Object o) {
                if (!(o instanceof Map.Entry)) {
                    return false;
                }
                Map.Entry entry = (Map.Entry)o;
                Object key = entry.getKey();
                Object v = AbstractConcurrentReadCache.this.get(key);
                return v != null && v.equals(entry.getValue());
            }

            public boolean remove(Object o) {
                if (!(o instanceof Map.Entry)) {
                    return false;
                }
                return AbstractConcurrentReadCache.this.findAndRemoveEntry((Map.Entry)o);
            }

            public int size() {
                return AbstractConcurrentReadCache.this.size();
            }

            public void clear() {
                AbstractConcurrentReadCache.this.clear();
            }
        };
        return this.entrySet;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object get(Object key) {
        Entry first;
        if (log.isDebugEnabled()) {
            log.debug((Object)("get called (key=" + key + ")"));
        }
        int hash = AbstractConcurrentReadCache.hash(key);
        Entry[] tab = this.table;
        int index = hash & tab.length - 1;
        Entry e = first = tab[index];
        while (true) {
            Object value;
            if (e == null) {
                tab = this.getTableForReading();
                if (first == tab[index]) {
                    value = this.persistRetrieve(key);
                    if (value != null) {
                        this.put(key, value, false);
                    }
                    return value;
                }
                index = hash & tab.length - 1;
                e = first = tab[index];
                continue;
            }
            if (key == e.key || e.hash == hash && key.equals(e.key)) {
                value = e.value;
                if (value != null) {
                    if (NULL.equals(value)) {
                        value = this.persistRetrieve(e.key);
                        if (value != null) {
                            this.itemRetrieved(key);
                        }
                        return value;
                    }
                    this.itemRetrieved(key);
                    return value;
                }
                AbstractConcurrentReadCache abstractConcurrentReadCache = this;
                synchronized (abstractConcurrentReadCache) {
                    tab = this.table;
                }
                index = hash & tab.length - 1;
                e = first = tab[index];
                continue;
            }
            e = e.next;
        }
    }

    public Set keySet() {
        Set ks = this.keySet;
        if (ks != null) {
            return ks;
        }
        this.keySet = new AbstractSet(){

            public Iterator iterator() {
                return new KeyIterator();
            }

            public int size() {
                return AbstractConcurrentReadCache.this.size();
            }

            public boolean contains(Object o) {
                return AbstractConcurrentReadCache.this.containsKey(o);
            }

            public boolean remove(Object o) {
                return AbstractConcurrentReadCache.this.remove(o) != null;
            }

            public void clear() {
                AbstractConcurrentReadCache.this.clear();
            }
        };
        return this.keySet;
    }

    public Enumeration keys() {
        return new KeyIterator();
    }

    public float loadFactor() {
        return this.loadFactor;
    }

    public Object put(Object key, Object value) {
        return this.put(key, value, true);
    }

    public synchronized void putAll(Map t) {
        Iterator it = t.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = it.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            this.put(key, value);
        }
    }

    public Object remove(Object key) {
        return this.remove(key, true);
    }

    public synchronized int size() {
        return this.count;
    }

    public Collection values() {
        Collection vs = this.values;
        if (vs != null) {
            return vs;
        }
        this.values = new AbstractCollection(){

            public Iterator iterator() {
                return new ValueIterator();
            }

            public int size() {
                return AbstractConcurrentReadCache.this.size();
            }

            public boolean contains(Object o) {
                return AbstractConcurrentReadCache.this.containsValue(o);
            }

            public void clear() {
                AbstractConcurrentReadCache.this.clear();
            }
        };
        return this.values;
    }

    protected final synchronized Set getGroupForReading(String groupName) {
        Set group = (Set)this.getGroupsForReading().get(groupName);
        if (group == null) {
            return null;
        }
        return new HashSet(group);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final Map getGroupsForReading() {
        Boolean bl = this.barrierLock;
        synchronized (bl) {
            return this.groups;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final Entry[] getTableForReading() {
        Boolean bl = this.barrierLock;
        synchronized (bl) {
            return this.table;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final void recordModification(Object x) {
        Boolean bl = this.barrierLock;
        synchronized (bl) {
            this.lastWrite = x;
        }
    }

    protected synchronized boolean findAndRemoveEntry(Map.Entry entry) {
        Object key = entry.getKey();
        Object v = this.get(key);
        if (v != null && v.equals(entry.getValue())) {
            this.remove(key);
            return true;
        }
        return false;
    }

    protected void persistRemove(Object key) {
        if (log.isDebugEnabled()) {
            log.debug((Object)("PersistRemove called (key=" + key + ")"));
        }
        if (this.persistenceListener != null) {
            try {
                this.persistenceListener.remove((String)key);
            }
            catch (CachePersistenceException e) {
                log.error((Object)("[oscache] Exception removing cache entry with key '" + key + "' from persistence"), (Throwable)e);
            }
        }
    }

    protected void persistRemoveGroup(String groupName) {
        if (log.isDebugEnabled()) {
            log.debug((Object)("persistRemoveGroup called (groupName=" + groupName + ")"));
        }
        if (this.persistenceListener != null) {
            try {
                this.persistenceListener.removeGroup(groupName);
            }
            catch (CachePersistenceException e) {
                log.error((Object)("[oscache] Exception removing group " + groupName), (Throwable)e);
            }
        }
    }

    protected Object persistRetrieve(Object key) {
        if (log.isDebugEnabled()) {
            log.debug((Object)("persistRetrieve called (key=" + key + ")"));
        }
        Object entry = null;
        if (this.persistenceListener != null) {
            try {
                entry = this.persistenceListener.retrieve((String)key);
            }
            catch (CachePersistenceException cachePersistenceException) {
                // empty catch block
            }
        }
        return entry;
    }

    protected Set persistRetrieveGroup(String groupName) {
        if (log.isDebugEnabled()) {
            log.debug((Object)("persistRetrieveGroup called (groupName=" + groupName + ")"));
        }
        if (this.persistenceListener != null) {
            try {
                return this.persistenceListener.retrieveGroup(groupName);
            }
            catch (CachePersistenceException e) {
                log.error((Object)("[oscache] Exception retrieving group " + groupName), (Throwable)e);
            }
        }
        return null;
    }

    protected void persistStore(Object key, Object obj) {
        if (log.isDebugEnabled()) {
            log.debug((Object)("persistStore called (key=" + key + ")"));
        }
        if (this.persistenceListener != null) {
            try {
                this.persistenceListener.store((String)key, obj);
            }
            catch (CachePersistenceException e) {
                log.error((Object)("[oscache] Exception persisting " + key), (Throwable)e);
            }
        }
    }

    protected void persistStoreGroup(String groupName, Set group) {
        if (log.isDebugEnabled()) {
            log.debug((Object)("persistStoreGroup called (groupName=" + groupName + ")"));
        }
        if (this.persistenceListener != null) {
            try {
                if (group == null || group.isEmpty()) {
                    this.persistenceListener.removeGroup(groupName);
                } else {
                    this.persistenceListener.storeGroup(groupName, group);
                }
            }
            catch (CachePersistenceException e) {
                log.error((Object)("[oscache] Exception persisting group " + groupName), (Throwable)e);
            }
        }
    }

    protected void persistClear() {
        if (log.isDebugEnabled()) {
            log.debug((Object)"persistClear called");
        }
        if (this.persistenceListener != null) {
            try {
                this.persistenceListener.clear();
            }
            catch (CachePersistenceException e) {
                log.error((Object)"[oscache] Exception clearing persistent cache", (Throwable)e);
            }
        }
    }

    protected abstract void itemPut(Object var1);

    protected abstract void itemRetrieved(Object var1);

    protected abstract void itemRemoved(Object var1);

    protected abstract Object removeItem();

    private synchronized void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        int numBuckets = s.readInt();
        this.table = new Entry[numBuckets];
        int size = s.readInt();
        for (int i = 0; i < size; ++i) {
            Object key = s.readObject();
            Object value = s.readObject();
            this.put(key, value);
        }
    }

    protected void rehash() {
        Entry[] oldMap = this.table;
        int oldCapacity = oldMap.length;
        if (oldCapacity >= 0x40000000) {
            return;
        }
        int newCapacity = oldCapacity << 1;
        Entry[] newMap = new Entry[newCapacity];
        this.threshold = (int)((float)newCapacity * this.loadFactor);
        for (int i = 0; i < oldCapacity; ++i) {
            Entry l = null;
            Entry h = null;
            Entry e = oldMap[i];
            while (e != null) {
                int hash = e.hash;
                Entry next = e.next;
                if ((hash & oldCapacity) == 0) {
                    if (l == null && (next == null || next.next == null && (next.hash & oldCapacity) == 0)) {
                        l = e;
                        break;
                    }
                    l = new Entry(hash, e.key, e.value, l);
                } else {
                    if (h == null && (next == null || next.next == null && (next.hash & oldCapacity) != 0)) {
                        h = e;
                        break;
                    }
                    h = new Entry(hash, e.key, e.value, h);
                }
                e = next;
            }
            newMap[i] = l;
            newMap[oldCapacity + i] = h;
        }
        this.table = newMap;
        this.recordModification(newMap);
    }

    protected Object sput(Object key, Object value, int hash, boolean persist) {
        Entry first;
        Entry[] tab = this.table;
        int index = hash & tab.length - 1;
        Entry e = first = tab[index];
        while (true) {
            if (e == null) {
                Entry newEntry = this.memoryCaching ? new Entry(hash, key, value, first) : new Entry(hash, key, NULL, first);
                this.itemPut(key);
                if (persist && !this.overflowPersistence) {
                    this.persistStore(key, value);
                }
                if (value instanceof CacheEntry) {
                    this.updateGroups(null, (CacheEntry)value, persist);
                }
                tab[index] = newEntry;
                if (++this.count >= this.threshold) {
                    this.rehash();
                } else {
                    this.recordModification(newEntry);
                }
                return null;
            }
            if (key == e.key || e.hash == hash && key.equals(e.key)) {
                Object oldValue = e.value;
                if (this.memoryCaching) {
                    e.value = value;
                }
                if (persist && this.overflowPersistence) {
                    this.persistRemove(key);
                } else if (persist) {
                    this.persistStore(key, value);
                }
                this.updateGroups(oldValue, value, persist);
                this.itemPut(key);
                return oldValue;
            }
            e = e.next;
        }
    }

    protected Object sremove(Object key, int hash, boolean invokeAlgorithm) {
        Entry first;
        Entry[] tab = this.table;
        int index = hash & tab.length - 1;
        Entry e = first = tab[index];
        while (e != null) {
            if (key == e.key || e.hash == hash && key.equals(e.key)) {
                Object oldValue = e.value;
                e.value = null;
                --this.count;
                if (!this.unlimitedDiskCache && !this.overflowPersistence) {
                    this.persistRemove(e.key);
                }
                if (this.overflowPersistence && this.size() + 1 >= this.maxEntries) {
                    this.persistStore(key, oldValue);
                }
                if (invokeAlgorithm) {
                    this.itemRemoved(key);
                }
                Entry head = e.next;
                Entry p = first;
                while (p != e) {
                    head = new Entry(p.hash, p.key, p.value, head);
                    p = p.next;
                }
                tab[index] = head;
                this.recordModification(head);
                return oldValue;
            }
            e = e.next;
        }
        return null;
    }

    private synchronized void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeInt(this.table.length);
        s.writeInt(this.count);
        for (int index = this.table.length - 1; index >= 0; --index) {
            Entry entry = this.table[index];
            while (entry != null) {
                s.writeObject(entry.key);
                s.writeObject(entry.value);
                entry = entry.next;
            }
        }
    }

    private static int hash(Object x) {
        int h = x.hashCode();
        return (h << 7) - h + (h >>> 9) + (h >>> 17);
    }

    private void addGroupMappings(String key, Set newGroups, boolean persist) {
        Iterator it = newGroups.iterator();
        while (it.hasNext()) {
            String groupName = (String)it.next();
            if (this.memoryCaching) {
                HashSet<String> memoryGroup;
                if (this.groups == null) {
                    this.groups = new HashMap();
                }
                if ((memoryGroup = (HashSet<String>)this.groups.get(groupName)) == null) {
                    memoryGroup = new HashSet<String>();
                    this.groups.put(groupName, memoryGroup);
                }
                memoryGroup.add(key);
            }
            if (!persist) continue;
            HashSet<String> persistentGroup = this.persistRetrieveGroup(groupName);
            if (persistentGroup == null) {
                persistentGroup = new HashSet<String>();
            }
            persistentGroup.add(key);
            this.persistStoreGroup(groupName, persistentGroup);
        }
    }

    private int p2capacity(int initialCapacity) {
        int result;
        int cap = initialCapacity;
        if (cap > 0x40000000 || cap < 0) {
            result = 0x40000000;
        } else {
            for (result = 4; result < cap; result <<= 1) {
            }
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Object put(Object key, Object value, boolean persist) {
        Entry first;
        if (value == null) {
            throw new NullPointerException();
        }
        int hash = AbstractConcurrentReadCache.hash(key);
        Entry[] tab = this.table;
        int index = hash & tab.length - 1;
        Entry e = first = tab[index];
        while (true) {
            if (e == null) {
                AbstractConcurrentReadCache abstractConcurrentReadCache = this;
                synchronized (abstractConcurrentReadCache) {
                    tab = this.table;
                    if (this.size() >= this.maxEntries) {
                        this.remove(this.removeItem(), false);
                    }
                    if (first == tab[index]) {
                        Entry newEntry = null;
                        newEntry = this.memoryCaching ? new Entry(hash, key, value, first) : new Entry(hash, key, NULL, first);
                        tab[index] = newEntry;
                        this.itemPut(key);
                        if (persist && !this.overflowPersistence) {
                            this.persistStore(key, value);
                        }
                        if (value instanceof CacheEntry) {
                            this.updateGroups(null, (CacheEntry)value, persist);
                        }
                        if (++this.count >= this.threshold) {
                            this.rehash();
                        } else {
                            this.recordModification(newEntry);
                        }
                        return newEntry;
                    }
                    return this.sput(key, value, hash, persist);
                }
            }
            if (key == e.key || e.hash == hash && key.equals(e.key)) {
                AbstractConcurrentReadCache abstractConcurrentReadCache = this;
                synchronized (abstractConcurrentReadCache) {
                    tab = this.table;
                    Object oldValue = e.value;
                    if (persist && oldValue == NULL) {
                        oldValue = this.persistRetrieve(key);
                    }
                    if (first == tab[index] && oldValue != null) {
                        if (this.memoryCaching) {
                            e.value = value;
                        }
                        if (persist && this.overflowPersistence) {
                            this.persistRemove(key);
                        } else if (persist) {
                            this.persistStore(key, value);
                        }
                        this.updateGroups(oldValue, value, persist);
                        this.itemPut(key);
                        return oldValue;
                    }
                    return this.sput(key, value, hash, persist);
                }
            }
            e = e.next;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private synchronized Object remove(Object key, boolean invokeAlgorithm) {
        Entry first;
        if (key == null) {
            return null;
        }
        int hash = AbstractConcurrentReadCache.hash(key);
        Entry[] tab = this.table;
        int index = hash & tab.length - 1;
        Entry e = first = tab[index];
        while (true) {
            if (e == null) {
                tab = this.getTableForReading();
                if (first == tab[index]) {
                    return null;
                }
                return this.sremove(key, hash, invokeAlgorithm);
            }
            if (key == e.key || e.hash == hash && key.equals(e.key)) {
                AbstractConcurrentReadCache abstractConcurrentReadCache = this;
                synchronized (abstractConcurrentReadCache) {
                    tab = this.table;
                    Object oldValue = e.value;
                    if (first != tab[index] || oldValue == null) {
                        return this.sremove(key, hash, invokeAlgorithm);
                    }
                    e.value = null;
                    --this.count;
                    if (!this.unlimitedDiskCache && !this.overflowPersistence) {
                        this.persistRemove(e.key);
                    }
                    if (this.overflowPersistence && this.size() + 1 >= this.maxEntries) {
                        this.persistStore(key, oldValue);
                    }
                    if (invokeAlgorithm) {
                        this.itemRemoved(key);
                    }
                    Entry head = e.next;
                    Entry p = first;
                    while (p != e) {
                        head = new Entry(p.hash, p.key, p.value, head);
                        p = p.next;
                    }
                    tab[index] = head;
                    this.recordModification(head);
                    return oldValue;
                }
            }
            e = e.next;
        }
    }

    private void removeGroupMappings(String key, Set oldGroups, boolean persist) {
        Iterator it = oldGroups.iterator();
        while (it.hasNext()) {
            Set persistentGroup;
            Set memoryGroup;
            String groupName = (String)it.next();
            if (this.memoryCaching && this.groups != null && (memoryGroup = (Set)this.groups.get(groupName)) != null) {
                memoryGroup.remove(key);
                if (memoryGroup.isEmpty()) {
                    this.groups.remove(groupName);
                }
            }
            if (!persist || (persistentGroup = this.persistRetrieveGroup(groupName)) == null) continue;
            persistentGroup.remove(key);
            if (persistentGroup.isEmpty()) {
                this.persistRemoveGroup(groupName);
                continue;
            }
            this.persistStoreGroup(groupName, persistentGroup);
        }
    }

    private void updateGroups(Object oldValue, Object newValue, boolean persist) {
        boolean oldIsCE = oldValue instanceof CacheEntry;
        boolean newIsCE = newValue instanceof CacheEntry;
        if (newIsCE && oldIsCE) {
            this.updateGroups((CacheEntry)oldValue, (CacheEntry)newValue, persist);
        } else if (newIsCE) {
            this.updateGroups(null, (CacheEntry)newValue, persist);
        } else if (oldIsCE) {
            this.updateGroups((CacheEntry)oldValue, null, persist);
        }
    }

    private void updateGroups(CacheEntry oldValue, CacheEntry newValue, boolean persist) {
        String groupName;
        Iterator it;
        Set oldGroups = null;
        Set newGroups = null;
        if (oldValue != null) {
            oldGroups = oldValue.getGroups();
        }
        if (newValue != null) {
            newGroups = newValue.getGroups();
        }
        if (oldGroups != null) {
            HashSet<String> removeFromGroups = new HashSet<String>();
            it = oldGroups.iterator();
            while (it.hasNext()) {
                groupName = (String)it.next();
                if (newGroups != null && newGroups.contains(groupName)) continue;
                removeFromGroups.add(groupName);
            }
            this.removeGroupMappings(oldValue.getKey(), removeFromGroups, persist);
        }
        if (newGroups != null) {
            HashSet<String> addToGroups = new HashSet<String>();
            it = newGroups.iterator();
            while (it.hasNext()) {
                groupName = (String)it.next();
                if (oldGroups != null && oldGroups.contains(groupName)) continue;
                addToGroups.add(groupName);
            }
            this.addGroupMappings(newValue.getKey(), addToGroups, persist);
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    protected class ValueIterator
    extends HashIterator {
        protected ValueIterator() {
        }

        protected Object returnValueOfNext() {
            return this.currentValue;
        }
    }

    protected class KeyIterator
    extends HashIterator {
        protected KeyIterator() {
        }

        protected Object returnValueOfNext() {
            return this.currentKey;
        }
    }

    protected class HashIterator
    implements Iterator,
    Enumeration {
        protected final Entry[] tab;
        protected Entry entry = null;
        protected Entry lastReturned = null;
        protected Object currentKey;
        protected Object currentValue;
        protected int index;

        protected HashIterator() {
            this.tab = AbstractConcurrentReadCache.this.getTableForReading();
            this.index = this.tab.length - 1;
        }

        public boolean hasMoreElements() {
            return this.hasNext();
        }

        public boolean hasNext() {
            do {
                if (this.entry != null) {
                    Object v = this.entry.value;
                    if (v != null) {
                        this.currentKey = this.entry.key;
                        this.currentValue = v;
                        return true;
                    }
                    this.entry = this.entry.next;
                }
                while (this.entry == null && this.index >= 0) {
                    this.entry = this.tab[this.index--];
                }
            } while (this.entry != null);
            this.currentValue = null;
            this.currentKey = null;
            return false;
        }

        public Object next() {
            if (this.currentKey == null && !this.hasNext()) {
                throw new NoSuchElementException();
            }
            Object result = this.returnValueOfNext();
            this.lastReturned = this.entry;
            this.currentValue = null;
            this.currentKey = null;
            this.entry = this.entry.next;
            return result;
        }

        public Object nextElement() {
            return this.next();
        }

        public void remove() {
            if (this.lastReturned == null) {
                throw new IllegalStateException();
            }
            AbstractConcurrentReadCache.this.remove(this.lastReturned.key);
        }

        protected Object returnValueOfNext() {
            return this.entry;
        }
    }

    protected static class Entry
    implements Map.Entry {
        protected final Entry next;
        protected final Object key;
        protected final int hash;
        protected volatile Object value;

        Entry(int hash, Object key, Object value, Entry next) {
            this.hash = hash;
            this.key = key;
            this.next = next;
            this.value = value;
        }

        public Object getKey() {
            return this.key;
        }

        public Object setValue(Object value) {
            if (value == null) {
                throw new NullPointerException();
            }
            Object oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        public Object getValue() {
            return this.value;
        }

        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (!this.key.equals(e.getKey())) {
                return false;
            }
            Object v = this.value;
            return v == null ? e.getValue() == null : v.equals(e.getValue());
        }

        public int hashCode() {
            Object v = this.value;
            return this.hash ^ (v == null ? 0 : v.hashCode());
        }

        public String toString() {
            return this.key + "=" + this.value;
        }

        protected Object clone() {
            return new Entry(this.hash, this.key, this.value, this.next == null ? null : (Entry)this.next.clone());
        }
    }
}

