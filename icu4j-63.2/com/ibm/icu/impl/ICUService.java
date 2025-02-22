/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.impl;

import com.ibm.icu.impl.ICUDebug;
import com.ibm.icu.impl.ICUNotifier;
import com.ibm.icu.impl.ICURWLock;
import com.ibm.icu.util.ULocale;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class ICUService
extends ICUNotifier {
    protected final String name;
    private static final boolean DEBUG = ICUDebug.enabled("service");
    private final ICURWLock factoryLock = new ICURWLock();
    private final List<Factory> factories = new ArrayList<Factory>();
    private int defaultSize = 0;
    private Map<String, CacheEntry> cache;
    private Map<String, Factory> idcache;
    private LocaleRef dnref;

    public ICUService() {
        this.name = "";
    }

    public ICUService(String name) {
        this.name = name;
    }

    public Object get(String descriptor) {
        return this.getKey(this.createKey(descriptor), null);
    }

    public Object get(String descriptor, String[] actualReturn) {
        if (descriptor == null) {
            throw new NullPointerException("descriptor must not be null");
        }
        return this.getKey(this.createKey(descriptor), actualReturn);
    }

    public Object getKey(Key key) {
        return this.getKey(key, null);
    }

    public Object getKey(Key key, String[] actualReturn) {
        return this.getKey(key, actualReturn, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object getKey(Key key, String[] actualReturn, Factory factory) {
        if (this.factories.size() == 0) {
            return this.handleDefault(key, actualReturn);
        }
        if (DEBUG) {
            System.out.println("Service: " + this.name + " key: " + key.canonicalID());
        }
        CacheEntry result = null;
        if (key != null) {
            try {
                this.factoryLock.acquireRead();
                Map<String, CacheEntry> cache = this.cache;
                if (cache == null) {
                    if (DEBUG) {
                        System.out.println("Service " + this.name + " cache was empty");
                    }
                    cache = new ConcurrentHashMap<String, CacheEntry>();
                }
                String currentDescriptor = null;
                ArrayList<String> cacheDescriptorList = null;
                boolean putInCache = false;
                int NDebug = 0;
                int startIndex = 0;
                int limit = this.factories.size();
                boolean cacheResult = true;
                if (factory != null) {
                    for (int i = 0; i < limit; ++i) {
                        if (factory != this.factories.get(i)) continue;
                        startIndex = i + 1;
                        break;
                    }
                    if (startIndex == 0) {
                        throw new IllegalStateException("Factory " + factory + "not registered with service: " + this);
                    }
                    cacheResult = false;
                }
                block4: do {
                    currentDescriptor = key.currentDescriptor();
                    if (DEBUG) {
                        System.out.println(this.name + "[" + NDebug++ + "] looking for: " + currentDescriptor);
                    }
                    if ((result = cache.get(currentDescriptor)) != null) {
                        if (!DEBUG) break;
                        System.out.println(this.name + " found with descriptor: " + currentDescriptor);
                        break;
                    }
                    if (DEBUG) {
                        System.out.println("did not find: " + currentDescriptor + " in cache");
                    }
                    putInCache = cacheResult;
                    int index = startIndex;
                    while (index < limit) {
                        Object service;
                        Factory f = this.factories.get(index++);
                        if (DEBUG) {
                            System.out.println("trying factory[" + (index - 1) + "] " + f.toString());
                        }
                        if ((service = f.create(key, this)) != null) {
                            result = new CacheEntry(currentDescriptor, service);
                            if (!DEBUG) break block4;
                            System.out.println(this.name + " factory supported: " + currentDescriptor + ", caching");
                            break block4;
                        }
                        if (!DEBUG) continue;
                        System.out.println("factory did not support: " + currentDescriptor);
                    }
                    if (cacheDescriptorList == null) {
                        cacheDescriptorList = new ArrayList<String>(5);
                    }
                    cacheDescriptorList.add(currentDescriptor);
                } while (key.fallback());
                if (result != null) {
                    if (putInCache) {
                        if (DEBUG) {
                            System.out.println("caching '" + result.actualDescriptor + "'");
                        }
                        cache.put(result.actualDescriptor, result);
                        if (cacheDescriptorList != null) {
                            for (String desc : cacheDescriptorList) {
                                if (DEBUG) {
                                    System.out.println(this.name + " adding descriptor: '" + desc + "' for actual: '" + result.actualDescriptor + "'");
                                }
                                cache.put(desc, result);
                            }
                        }
                        this.cache = cache;
                    }
                    if (actualReturn != null) {
                        actualReturn[0] = result.actualDescriptor.indexOf("/") == 0 ? result.actualDescriptor.substring(1) : result.actualDescriptor;
                    }
                    if (DEBUG) {
                        System.out.println("found in service: " + this.name);
                    }
                    Object object = result.service;
                    return object;
                }
            }
            finally {
                this.factoryLock.releaseRead();
            }
        }
        if (DEBUG) {
            System.out.println("not found in service: " + this.name);
        }
        return this.handleDefault(key, actualReturn);
    }

    protected Object handleDefault(Key key, String[] actualIDReturn) {
        return null;
    }

    public Set<String> getVisibleIDs() {
        return this.getVisibleIDs(null);
    }

    public Set<String> getVisibleIDs(String matchID) {
        Set<String> result = this.getVisibleIDMap().keySet();
        Key fallbackKey = this.createKey(matchID);
        if (fallbackKey != null) {
            HashSet<String> temp = new HashSet<String>(result.size());
            for (String id : result) {
                if (!fallbackKey.isFallbackOf(id)) continue;
                temp.add(id);
            }
            result = temp;
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Map<String, Factory> getVisibleIDMap() {
        ICUService iCUService = this;
        synchronized (iCUService) {
            if (this.idcache == null) {
                try {
                    this.factoryLock.acquireRead();
                    HashMap<String, Factory> mutableMap = new HashMap<String, Factory>();
                    ListIterator<Factory> lIter = this.factories.listIterator(this.factories.size());
                    while (lIter.hasPrevious()) {
                        Factory f = lIter.previous();
                        f.updateVisibleIDs(mutableMap);
                    }
                    this.idcache = Collections.unmodifiableMap(mutableMap);
                }
                finally {
                    this.factoryLock.releaseRead();
                }
            }
        }
        return this.idcache;
    }

    public String getDisplayName(String id) {
        return this.getDisplayName(id, ULocale.getDefault(ULocale.Category.DISPLAY));
    }

    public String getDisplayName(String id, ULocale locale) {
        Map<String, Factory> m = this.getVisibleIDMap();
        Factory f = m.get(id);
        if (f != null) {
            return f.getDisplayName(id, locale);
        }
        Key key = this.createKey(id);
        while (key.fallback()) {
            f = m.get(key.currentID());
            if (f == null) continue;
            return f.getDisplayName(id, locale);
        }
        return null;
    }

    public SortedMap<String, String> getDisplayNames() {
        ULocale locale = ULocale.getDefault(ULocale.Category.DISPLAY);
        return this.getDisplayNames(locale, null, null);
    }

    public SortedMap<String, String> getDisplayNames(ULocale locale) {
        return this.getDisplayNames(locale, null, null);
    }

    public SortedMap<String, String> getDisplayNames(ULocale locale, Comparator<Object> com) {
        return this.getDisplayNames(locale, com, null);
    }

    public SortedMap<String, String> getDisplayNames(ULocale locale, String matchID) {
        return this.getDisplayNames(locale, null, matchID);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public SortedMap<String, String> getDisplayNames(ULocale locale, Comparator<Object> com, String matchID) {
        SortedMap<String, String> dncache = null;
        LocaleRef ref = this.dnref;
        if (ref != null) {
            dncache = ref.get(locale, com);
        }
        while (dncache == null) {
            ICUService iCUService = this;
            synchronized (iCUService) {
                if (ref == this.dnref || this.dnref == null) {
                    dncache = new TreeMap<Object, String>(com);
                    Map<String, Factory> m = this.getVisibleIDMap();
                    for (Map.Entry<String, Factory> entry : m.entrySet()) {
                        String id = entry.getKey();
                        Factory f = entry.getValue();
                        dncache.put(f.getDisplayName(id, locale), id);
                    }
                    dncache = Collections.unmodifiableSortedMap(dncache);
                    this.dnref = new LocaleRef(dncache, locale, com);
                } else {
                    ref = this.dnref;
                    dncache = ref.get(locale, com);
                }
            }
        }
        Key matchKey = this.createKey(matchID);
        if (matchKey == null) {
            return dncache;
        }
        TreeMap<String, String> result = new TreeMap<String, String>(dncache);
        Iterator iter = result.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = iter.next();
            if (matchKey.isFallbackOf((String)entry.getValue())) continue;
            iter.remove();
        }
        return result;
    }

    public final List<Factory> factories() {
        try {
            this.factoryLock.acquireRead();
            ArrayList<Factory> arrayList = new ArrayList<Factory>(this.factories);
            return arrayList;
        }
        finally {
            this.factoryLock.releaseRead();
        }
    }

    public Factory registerObject(Object obj, String id) {
        return this.registerObject(obj, id, true);
    }

    public Factory registerObject(Object obj, String id, boolean visible) {
        String canonicalID = this.createKey(id).canonicalID();
        return this.registerFactory(new SimpleFactory(obj, canonicalID, visible));
    }

    public final Factory registerFactory(Factory factory) {
        if (factory == null) {
            throw new NullPointerException();
        }
        try {
            this.factoryLock.acquireWrite();
            this.factories.add(0, factory);
            this.clearCaches();
        }
        finally {
            this.factoryLock.releaseWrite();
        }
        this.notifyChanged();
        return factory;
    }

    public final boolean unregisterFactory(Factory factory) {
        if (factory == null) {
            throw new NullPointerException();
        }
        boolean result = false;
        try {
            this.factoryLock.acquireWrite();
            if (this.factories.remove(factory)) {
                result = true;
                this.clearCaches();
            }
        }
        finally {
            this.factoryLock.releaseWrite();
        }
        if (result) {
            this.notifyChanged();
        }
        return result;
    }

    public final void reset() {
        try {
            this.factoryLock.acquireWrite();
            this.reInitializeFactories();
            this.clearCaches();
        }
        finally {
            this.factoryLock.releaseWrite();
        }
        this.notifyChanged();
    }

    protected void reInitializeFactories() {
        this.factories.clear();
    }

    public boolean isDefault() {
        return this.factories.size() == this.defaultSize;
    }

    protected void markDefault() {
        this.defaultSize = this.factories.size();
    }

    public Key createKey(String id) {
        return id == null ? null : new Key(id);
    }

    protected void clearCaches() {
        this.cache = null;
        this.idcache = null;
        this.dnref = null;
    }

    protected void clearServiceCache() {
        this.cache = null;
    }

    @Override
    protected boolean acceptsListener(EventListener l) {
        return l instanceof ServiceListener;
    }

    @Override
    protected void notifyListener(EventListener l) {
        ((ServiceListener)l).serviceChanged(this);
    }

    public String stats() {
        ICURWLock.Stats stats = this.factoryLock.resetStats();
        if (stats != null) {
            return stats.toString();
        }
        return "no stats";
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        return super.toString() + "{" + this.name + "}";
    }

    public static interface ServiceListener
    extends EventListener {
        public void serviceChanged(ICUService var1);
    }

    private static class LocaleRef {
        private final ULocale locale;
        private SortedMap<String, String> dnCache;
        private Comparator<Object> com;

        LocaleRef(SortedMap<String, String> dnCache, ULocale locale, Comparator<Object> com) {
            this.locale = locale;
            this.com = com;
            this.dnCache = dnCache;
        }

        SortedMap<String, String> get(ULocale loc, Comparator<Object> comp) {
            SortedMap<String, String> m = this.dnCache;
            if (m != null && this.locale.equals(loc) && (this.com == comp || this.com != null && this.com.equals(comp))) {
                return m;
            }
            return null;
        }
    }

    private static final class CacheEntry {
        final String actualDescriptor;
        final Object service;

        CacheEntry(String actualDescriptor, Object service) {
            this.actualDescriptor = actualDescriptor;
            this.service = service;
        }
    }

    public static class SimpleFactory
    implements Factory {
        protected Object instance;
        protected String id;
        protected boolean visible;

        public SimpleFactory(Object instance, String id) {
            this(instance, id, true);
        }

        public SimpleFactory(Object instance, String id, boolean visible) {
            if (instance == null || id == null) {
                throw new IllegalArgumentException("Instance or id is null");
            }
            this.instance = instance;
            this.id = id;
            this.visible = visible;
        }

        @Override
        public Object create(Key key, ICUService service) {
            if (this.id.equals(key.currentID())) {
                return this.instance;
            }
            return null;
        }

        @Override
        public void updateVisibleIDs(Map<String, Factory> result) {
            if (this.visible) {
                result.put(this.id, this);
            } else {
                result.remove(this.id);
            }
        }

        @Override
        public String getDisplayName(String identifier, ULocale locale) {
            return this.visible && this.id.equals(identifier) ? identifier : null;
        }

        public String toString() {
            StringBuilder buf = new StringBuilder(super.toString());
            buf.append(", id: ");
            buf.append(this.id);
            buf.append(", visible: ");
            buf.append(this.visible);
            return buf.toString();
        }
    }

    public static interface Factory {
        public Object create(Key var1, ICUService var2);

        public void updateVisibleIDs(Map<String, Factory> var1);

        public String getDisplayName(String var1, ULocale var2);
    }

    public static class Key {
        private final String id;

        public Key(String id) {
            this.id = id;
        }

        public final String id() {
            return this.id;
        }

        public String canonicalID() {
            return this.id;
        }

        public String currentID() {
            return this.canonicalID();
        }

        public String currentDescriptor() {
            return "/" + this.currentID();
        }

        public boolean fallback() {
            return false;
        }

        public boolean isFallbackOf(String idToCheck) {
            return this.canonicalID().equals(idToCheck);
        }
    }
}

