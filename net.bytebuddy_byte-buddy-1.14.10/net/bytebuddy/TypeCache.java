/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.bytebuddy.build.CachedReturnPlugin;
import net.bytebuddy.utility.CompoundList;
import net.bytebuddy.utility.nullability.AlwaysNull;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TypeCache<T>
extends ReferenceQueue<ClassLoader> {
    @AlwaysNull
    private static final Class<?> NOT_FOUND = null;
    protected final Sort sort;
    protected final ConcurrentMap<StorageKey, ConcurrentMap<T, Object>> cache;

    public TypeCache() {
        this(Sort.STRONG);
    }

    public TypeCache(Sort sort) {
        this.sort = sort;
        this.cache = new ConcurrentHashMap<StorageKey, ConcurrentMap<T, Object>>();
    }

    @MaybeNull
    @SuppressFBWarnings(value={"GC_UNRELATED_TYPES"}, justification="Cross-comparison is intended.")
    public Class<?> find(@MaybeNull ClassLoader classLoader, T key) {
        ConcurrentMap storage = (ConcurrentMap)this.cache.get(new LookupKey(classLoader));
        if (storage == null) {
            return NOT_FOUND;
        }
        Object value = storage.get(key);
        if (value == null) {
            return NOT_FOUND;
        }
        if (value instanceof Reference) {
            return (Class)((Reference)value).get();
        }
        return (Class)value;
    }

    @SuppressFBWarnings(value={"GC_UNRELATED_TYPES"}, justification="Cross-comparison is intended.")
    public Class<?> insert(@MaybeNull ClassLoader classLoader, T key, Class<?> type) {
        ConcurrentMap previous;
        ConcurrentMap<T, Object> storage = (ConcurrentHashMap<T, Object>)this.cache.get(new LookupKey(classLoader));
        if (storage == null && (previous = (ConcurrentMap)this.cache.putIfAbsent(new StorageKey(classLoader, this), storage = new ConcurrentHashMap<T, Object>())) != null) {
            storage = previous;
        }
        Object value = this.sort.wrap(type);
        Object previous2 = storage.putIfAbsent(key, value);
        while (previous2 != null) {
            Class previousType = (Class)(previous2 instanceof Reference ? ((Reference)previous2).get() : previous2);
            if (previousType != null) {
                return previousType;
            }
            if (storage.remove(key, previous2)) {
                previous2 = storage.putIfAbsent(key, value);
                continue;
            }
            previous2 = storage.get(key);
            if (previous2 != null) continue;
            previous2 = storage.putIfAbsent(key, value);
        }
        return type;
    }

    public Class<?> findOrInsert(@MaybeNull ClassLoader classLoader, T key, Callable<Class<?>> lazy) {
        Class<?> type = this.find(classLoader, key);
        if (type != null) {
            return type;
        }
        try {
            return this.insert(classLoader, key, lazy.call());
        }
        catch (Throwable throwable) {
            throw new IllegalArgumentException("Could not create type", throwable);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Class<?> findOrInsert(@MaybeNull ClassLoader classLoader, T key, Callable<Class<?>> lazy, Object monitor) {
        Class<?> type = this.find(classLoader, key);
        if (type != null) {
            return type;
        }
        Object object = monitor;
        synchronized (object) {
            return this.findOrInsert(classLoader, key, lazy);
        }
    }

    public void expungeStaleEntries() {
        Reference reference;
        while ((reference = this.poll()) != null) {
            this.cache.remove(reference);
        }
    }

    public void clear() {
        this.cache.clear();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class SimpleKey {
        private final Set<String> types = new HashSet<String>();
        private transient /* synthetic */ int hashCode;

        public SimpleKey(Class<?> type, Class<?> ... additionalType) {
            this(type, Arrays.asList(additionalType));
        }

        public SimpleKey(Class<?> type, Collection<? extends Class<?>> additionalTypes) {
            this(CompoundList.of(type, new ArrayList(additionalTypes)));
        }

        public SimpleKey(Collection<? extends Class<?>> types) {
            for (Class<?> type : types) {
                this.types.add(type.getName());
            }
        }

        @CachedReturnPlugin.Enhance(value="hashCode")
        public int hashCode() {
            int n;
            int n2;
            int n3 = this.hashCode;
            if (n3 != 0) {
                n2 = 0;
            } else {
                SimpleKey simpleKey = this;
                n2 = n = simpleKey.types.hashCode();
            }
            if (n == 0) {
                n = this.hashCode;
            } else {
                this.hashCode = n;
            }
            return n;
        }

        public boolean equals(@MaybeNull Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || this.getClass() != other.getClass()) {
                return false;
            }
            SimpleKey simpleKey = (SimpleKey)other;
            return this.types.equals(simpleKey.types);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class WithInlineExpunction<S>
    extends TypeCache<S> {
        public WithInlineExpunction() {
            this(Sort.STRONG);
        }

        public WithInlineExpunction(Sort sort) {
            super(sort);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Class<?> find(@MaybeNull ClassLoader classLoader, S key) {
            Class<?> clazz;
            try {
                clazz = super.find(classLoader, key);
                Object var5_4 = null;
            }
            catch (Throwable throwable) {
                Object var5_5 = null;
                this.expungeStaleEntries();
                throw throwable;
            }
            this.expungeStaleEntries();
            return clazz;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Class<?> insert(@MaybeNull ClassLoader classLoader, S key, Class<?> type) {
            Class<?> clazz;
            try {
                clazz = super.insert(classLoader, key, type);
                Object var6_5 = null;
            }
            catch (Throwable throwable) {
                Object var6_6 = null;
                this.expungeStaleEntries();
                throw throwable;
            }
            this.expungeStaleEntries();
            return clazz;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Class<?> findOrInsert(@MaybeNull ClassLoader classLoader, S key, Callable<Class<?>> builder) {
            Class<?> clazz;
            try {
                clazz = super.findOrInsert(classLoader, key, builder);
                Object var6_5 = null;
            }
            catch (Throwable throwable) {
                Object var6_6 = null;
                this.expungeStaleEntries();
                throw throwable;
            }
            this.expungeStaleEntries();
            return clazz;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Class<?> findOrInsert(@MaybeNull ClassLoader classLoader, S key, Callable<Class<?>> builder, Object monitor) {
            Class<?> clazz;
            try {
                clazz = super.findOrInsert(classLoader, key, builder, monitor);
                Object var7_6 = null;
            }
            catch (Throwable throwable) {
                Object var7_7 = null;
                this.expungeStaleEntries();
                throw throwable;
            }
            this.expungeStaleEntries();
            return clazz;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static class StorageKey
    extends WeakReference<ClassLoader> {
        private final int hashCode;

        protected StorageKey(@MaybeNull ClassLoader classLoader, ReferenceQueue<? super ClassLoader> referenceQueue) {
            super(classLoader, referenceQueue);
            this.hashCode = System.identityHashCode(classLoader);
        }

        public int hashCode() {
            return this.hashCode;
        }

        @SuppressFBWarnings(value={"EQ_CHECK_FOR_OPERAND_NOT_COMPATIBLE_WITH_THIS"}, justification="Cross-comparison is intended.")
        public boolean equals(@MaybeNull Object other) {
            if (this == other) {
                return true;
            }
            if (other instanceof LookupKey) {
                LookupKey lookupKey = (LookupKey)other;
                return this.hashCode == lookupKey.hashCode && this.get() == lookupKey.classLoader;
            }
            if (other instanceof StorageKey) {
                StorageKey storageKey = (StorageKey)other;
                return this.hashCode == storageKey.hashCode && this.get() == storageKey.get();
            }
            return false;
        }
    }

    protected static class LookupKey {
        @MaybeNull
        private final ClassLoader classLoader;
        private final int hashCode;

        protected LookupKey(@MaybeNull ClassLoader classLoader) {
            this.classLoader = classLoader;
            this.hashCode = System.identityHashCode(classLoader);
        }

        public int hashCode() {
            return this.hashCode;
        }

        @SuppressFBWarnings(value={"EQ_CHECK_FOR_OPERAND_NOT_COMPATIBLE_WITH_THIS"}, justification="Cross-comparison is intended.")
        public boolean equals(@MaybeNull Object other) {
            if (this == other) {
                return true;
            }
            if (other instanceof LookupKey) {
                return this.classLoader == ((LookupKey)other).classLoader;
            }
            if (other instanceof StorageKey) {
                StorageKey storageKey = (StorageKey)other;
                return this.hashCode == storageKey.hashCode && this.classLoader == storageKey.get();
            }
            return false;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Sort {
        WEAK{

            @Override
            protected Reference<Class<?>> wrap(Class<?> type) {
                return new WeakReference(type);
            }
        }
        ,
        SOFT{

            @Override
            protected Reference<Class<?>> wrap(Class<?> type) {
                return new SoftReference(type);
            }
        }
        ,
        STRONG{

            @Override
            protected Class<?> wrap(Class<?> type) {
                return type;
            }
        };


        protected abstract Object wrap(Class<?> var1);
    }
}

