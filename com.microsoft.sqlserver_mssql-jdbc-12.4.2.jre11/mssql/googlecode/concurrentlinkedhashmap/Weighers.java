/*
 * Decompiled with CFR 0.152.
 */
package mssql.googlecode.concurrentlinkedhashmap;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mssql.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import mssql.googlecode.concurrentlinkedhashmap.EntryWeigher;
import mssql.googlecode.concurrentlinkedhashmap.Weigher;

public final class Weighers {
    private Weighers() {
        throw new AssertionError();
    }

    public static <K, V> EntryWeigher<K, V> asEntryWeigher(Weigher<? super V> weigher) {
        return weigher == Weighers.singleton() ? Weighers.entrySingleton() : new EntryWeigherView(weigher);
    }

    public static <K, V> EntryWeigher<K, V> entrySingleton() {
        return SingletonEntryWeigher.INSTANCE;
    }

    public static <V> Weigher<V> singleton() {
        return SingletonWeigher.INSTANCE;
    }

    public static Weigher<byte[]> byteArray() {
        return ByteArrayWeigher.INSTANCE;
    }

    public static <E> Weigher<? super Iterable<E>> iterable() {
        return IterableWeigher.INSTANCE;
    }

    public static <E> Weigher<? super Collection<E>> collection() {
        return CollectionWeigher.INSTANCE;
    }

    public static <E> Weigher<? super List<E>> list() {
        return ListWeigher.INSTANCE;
    }

    public static <E> Weigher<? super Set<E>> set() {
        return SetWeigher.INSTANCE;
    }

    public static <A, B> Weigher<? super Map<A, B>> map() {
        return MapWeigher.INSTANCE;
    }

    static enum MapWeigher implements Weigher<Map<?, ?>>
    {
        INSTANCE;


        @Override
        public int weightOf(Map<?, ?> values) {
            return values.size();
        }
    }

    static enum SetWeigher implements Weigher<Set<?>>
    {
        INSTANCE;


        @Override
        public int weightOf(Set<?> values) {
            return values.size();
        }
    }

    static enum ListWeigher implements Weigher<List<?>>
    {
        INSTANCE;


        @Override
        public int weightOf(List<?> values) {
            return values.size();
        }
    }

    static enum CollectionWeigher implements Weigher<Collection<?>>
    {
        INSTANCE;


        @Override
        public int weightOf(Collection<?> values) {
            return values.size();
        }
    }

    static enum IterableWeigher implements Weigher<Iterable<?>>
    {
        INSTANCE;


        @Override
        public int weightOf(Iterable<?> values) {
            if (values instanceof Collection) {
                return ((Collection)values).size();
            }
            int size = 0;
            for (Object value : values) {
                ++size;
            }
            return size;
        }
    }

    static enum ByteArrayWeigher implements Weigher<byte[]>
    {
        INSTANCE;


        @Override
        public int weightOf(byte[] value) {
            return value.length;
        }
    }

    static enum SingletonWeigher implements Weigher<Object>
    {
        INSTANCE;


        @Override
        public int weightOf(Object value) {
            return 1;
        }
    }

    static enum SingletonEntryWeigher implements EntryWeigher<Object, Object>
    {
        INSTANCE;


        @Override
        public int weightOf(Object key, Object value) {
            return 1;
        }
    }

    static final class EntryWeigherView<K, V>
    implements EntryWeigher<K, V>,
    Serializable {
        static final long serialVersionUID = 1L;
        final Weigher<? super V> weigher;

        EntryWeigherView(Weigher<? super V> weigher) {
            ConcurrentLinkedHashMap.checkNotNull(weigher);
            this.weigher = weigher;
        }

        @Override
        public int weightOf(K key, V value) {
            return this.weigher.weightOf(value);
        }
    }
}

