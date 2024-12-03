/*
 * Decompiled with CFR 0.152.
 */
package freemarker.cache;

import freemarker.cache.CacheStorageWithGetSize;
import freemarker.cache.ConcurrentCacheStorage;
import freemarker.template.utility.UndeclaredThrowableException;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SoftCacheStorage
implements ConcurrentCacheStorage,
CacheStorageWithGetSize {
    private static final Method atomicRemove = SoftCacheStorage.getAtomicRemoveMethod();
    private final ReferenceQueue queue = new ReferenceQueue();
    private final Map map;
    private final boolean concurrent;

    public SoftCacheStorage() {
        this(new ConcurrentHashMap());
    }

    @Override
    public boolean isConcurrent() {
        return this.concurrent;
    }

    public SoftCacheStorage(Map backingMap) {
        this.map = backingMap;
        this.concurrent = this.map instanceof ConcurrentMap;
    }

    @Override
    public Object get(Object key) {
        this.processQueue();
        Reference ref = (Reference)this.map.get(key);
        return ref == null ? null : ref.get();
    }

    @Override
    public void put(Object key, Object value) {
        this.processQueue();
        this.map.put(key, new SoftValueReference(key, value, this.queue));
    }

    @Override
    public void remove(Object key) {
        this.processQueue();
        this.map.remove(key);
    }

    @Override
    public void clear() {
        this.map.clear();
        this.processQueue();
    }

    @Override
    public int getSize() {
        this.processQueue();
        return this.map.size();
    }

    private void processQueue() {
        SoftValueReference ref;
        while ((ref = (SoftValueReference)this.queue.poll()) != null) {
            Object key = ref.getKey();
            if (this.concurrent) {
                try {
                    atomicRemove.invoke((Object)this.map, key, ref);
                }
                catch (IllegalAccessException | InvocationTargetException e) {
                    throw new UndeclaredThrowableException(e);
                }
            }
            if (this.map.get(key) != ref) continue;
            this.map.remove(key);
        }
        return;
    }

    private static Method getAtomicRemoveMethod() {
        try {
            return Class.forName("java.util.concurrent.ConcurrentMap").getMethod("remove", Object.class, Object.class);
        }
        catch (ClassNotFoundException e) {
            return null;
        }
        catch (NoSuchMethodException e) {
            throw new UndeclaredThrowableException(e);
        }
    }

    private static final class SoftValueReference
    extends SoftReference {
        private final Object key;

        SoftValueReference(Object key, Object value, ReferenceQueue queue) {
            super(value, queue);
            this.key = key;
        }

        Object getKey() {
            return this.key;
        }
    }
}

