/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.concurrency;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import nonapi.io.github.classgraph.utils.LogNode;

public abstract class SingletonMap<K, V, E extends Exception> {
    private final ConcurrentMap<K, SingletonHolder<V>> map = new ConcurrentHashMap<K, SingletonHolder<V>>();

    public abstract V newInstance(K var1, LogNode var2) throws E, InterruptedException;

    public V get(K key, LogNode log, NewInstanceFactory<V, E> newInstanceFactory) throws E, InterruptedException, NullSingletonException, NewInstanceException {
        SingletonHolder singletonHolder = (SingletonHolder)this.map.get(key);
        Object instance = null;
        if (singletonHolder != null) {
            instance = singletonHolder.get();
        } else {
            SingletonHolder<Object> newSingletonHolder = new SingletonHolder<Object>();
            SingletonHolder oldSingletonHolder = this.map.putIfAbsent(key, newSingletonHolder);
            if (oldSingletonHolder != null) {
                instance = oldSingletonHolder.get();
            } else {
                try {
                    instance = newInstanceFactory != null ? (Object)newInstanceFactory.newInstance() : (Object)this.newInstance(key, log);
                }
                catch (Throwable t) {
                    newSingletonHolder.set(instance);
                    throw new NewInstanceException(key, t);
                }
                newSingletonHolder.set(instance);
            }
        }
        if (instance == null) {
            throw new NullSingletonException(key);
        }
        return instance;
    }

    public V get(K key, LogNode log) throws E, InterruptedException, NullSingletonException, NewInstanceException {
        return this.get(key, log, null);
    }

    public List<V> values() throws InterruptedException {
        ArrayList entries = new ArrayList(this.map.size());
        for (Map.Entry ent : this.map.entrySet()) {
            Object entryValue = ((SingletonHolder)ent.getValue()).get();
            if (entryValue == null) continue;
            entries.add(entryValue);
        }
        return entries;
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public List<Map.Entry<K, V>> entries() throws InterruptedException {
        ArrayList<Map.Entry<K, V>> entries = new ArrayList<Map.Entry<K, V>>(this.map.size());
        for (Map.Entry ent : this.map.entrySet()) {
            entries.add(new AbstractMap.SimpleEntry(ent.getKey(), ((SingletonHolder)ent.getValue()).get()));
        }
        return entries;
    }

    public V remove(K key) throws InterruptedException {
        SingletonHolder val = (SingletonHolder)this.map.remove(key);
        return val == null ? null : (V)val.get();
    }

    public void clear() {
        this.map.clear();
    }

    @FunctionalInterface
    public static interface NewInstanceFactory<V, E extends Exception> {
        public V newInstance() throws E, InterruptedException;
    }

    private static class SingletonHolder<V> {
        private volatile V singleton;
        private final CountDownLatch initialized = new CountDownLatch(1);

        private SingletonHolder() {
        }

        void set(V singleton) throws IllegalArgumentException {
            if (this.initialized.getCount() < 1L) {
                throw new IllegalArgumentException("Singleton already initialized");
            }
            this.singleton = singleton;
            this.initialized.countDown();
            if (this.initialized.getCount() != 0L) {
                throw new IllegalArgumentException("Singleton initialized more than once");
            }
        }

        V get() throws InterruptedException {
            this.initialized.await();
            return this.singleton;
        }
    }

    public static class NewInstanceException
    extends Exception {
        static final long serialVersionUID = 1L;

        public <K> NewInstanceException(K key, Throwable t) {
            super("newInstance threw an exception for key " + key + " : " + t, t);
        }
    }

    public static class NullSingletonException
    extends Exception {
        static final long serialVersionUID = 1L;

        public <K> NullSingletonException(K key) {
            super("newInstance returned null for key " + key);
        }
    }
}

